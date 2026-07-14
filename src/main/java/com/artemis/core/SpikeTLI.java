package com.artemis.core;

import java.util.ArrayList;
import java.util.List;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.attitudes.LofOffset;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.ThirdBodyAttraction;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.forces.maneuvers.ImpulseManeuver;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.LOFType;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.OrbitType;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.DateDetector;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import com.artemis.analisis.AnalizadorTrayectoria;
import com.artemis.model.PuntoTrayectoria;


public class SpikeTLI {

        public void ejecutar() {

                AbsoluteDate initialDate = new AbsoluteDate();

                double alturaOrbitaKm = 185.0;
                double radioTierra = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
                double semiEjeMayor = radioTierra + alturaOrbitaKm * 1000.0;

                KeplerianOrbit orbit = new KeplerianOrbit(
                                semiEjeMayor,
                                0.0001,
                                Math.toRadians(28.5),
                                0.0,
                                0.0,
                                0.0,
                                PositionAngleType.TRUE,
                                FramesFactory.getGCRF(),
                                initialDate,
                                Constants.WGS84_EARTH_MU);

                SpacecraftState estadoInicial = new SpacecraftState(orbit, 26000.0);

                Frame itrf = FramesFactory.getITRF(IERSConventions.IERS_2010, true);

                OrbitType tipoOrbita = OrbitType.CARTESIAN;

                double[][] tolerancias = NumericalPropagator.tolerances(10.0, orbit, tipoOrbita);

                DormandPrince853Integrator integrador = new DormandPrince853Integrator(
                                0.001,
                                300.0,
                                tolerancias[0],
                                tolerancias[1]);

                NumericalPropagator propagador = new NumericalPropagator(integrador);

                propagador.setOrbitType(tipoOrbita);
                propagador.setInitialState(estadoInicial);

                NormalizedSphericalHarmonicsProvider gravedad = GravityFieldFactory.getNormalizedProvider(8, 8);

                propagador.addForceModel(
                                new HolmesFeatherstoneAttractionModel(
                                                itrf,
                                                gravedad));

                propagador.addForceModel(
                                new ThirdBodyAttraction(
                                                CelestialBodyFactory.getMoon()));

                // Configuración de la mainobra TLI

                // La nave realiza la inyección translunar después de 3 horas
                AbsoluteDate fechaTLI = initialDate.shiftedBy(3 * 3600.0);

                // Evento que dispara la maniobra
                DateDetector detectorTLI = new DateDetector(fechaTLI);

                // Delta-V aproximado de una inyección translunar (m/s)
                Vector3D deltaV = new Vector3D(3150.0, 0.0, 0.0);

                // Maniobra impulsiva en marco tangencial orbital
                ImpulseManeuver tli = new ImpulseManeuver(
                                detectorTLI,
                                new LofOffset(
                                                orbit.getFrame(),
                                                LOFType.TNW),
                                deltaV,
                                300.0);

                // Registra maniobra en el propagador
                propagador.addEventDetector(tli);

                IO.println("Maniobra TLI agregada.");

                List<PuntoTrayectoria> trayectoria = new ArrayList<>();

                AnalizadorTrayectoria analizador = new AnalizadorTrayectoria();

                OrekitFixedStepHandler stepHandler = currentState -> {

                        Vector3D posicionNave = currentState.getPVCoordinates().getPosition();

                        double distanciaTierra = posicionNave.getNorm() / 1000.0;

                        Vector3D posicionLuna = CelestialBodyFactory.getMoon()
                                        .getPVCoordinates(currentState.getDate(),
                                                        FramesFactory.getGCRF())
                                        .getPosition();

                        double distancia = Vector3D.distance(posicionNave, posicionLuna);

                        analizador.registrar(
                                        distancia,
                                        currentState.getDate().toString());

                        trayectoria.add(
                                        new PuntoTrayectoria(
                                                        currentState.getDate(),
                                                        posicionNave));

                        if (trayectoria.size() % 720 == 0) {

                                System.out.printf(
                                                "%s -> Distancia a la Tierra: %.2f km%n",
                                                currentState.getDate(),
                                                distanciaTierra);

                        }

                };
                propagador.setStepHandler(60.0, stepHandler);

                propagador.addForceModel(
                                new ThirdBodyAttraction(
                                                CelestialBodyFactory.getSun()));

                AbsoluteDate fechaFinal = initialDate.shiftedBy(10 * 24 * 3600.0);

                SpacecraftState estadoFinal = propagador.propagate(fechaFinal);

                IO.println();
                IO.println("===== RESULTADOS =====");
                IO.println("Simulación completada.");
                IO.println("Duración: 10 días.");
                IO.println("Puntos registrados: " + trayectoria.size());

                analizador.imprimirResultado();

                IO.println("Posición final:");
                IO.println(estadoFinal.getPVCoordinates().getPosition());

                IO.println("Velocidad final:");
                IO.println(estadoFinal.getPVCoordinates().getVelocity());
                IO.println();

                IO.println();
                IO.println("===== SPIKE TLI =====");
                IO.println("Órbita creada correctamente.");
                IO.println("NumericalPropagator configurado.");
                IO.println("Gravedad terrestre, Luna y Sol agregados.");
                IO.println("Maniobra TLI configurada.");
                IO.println("Altura inicial: " + alturaOrbitaKm + " km");
                IO.println("Masa: " + estadoInicial.getMass() + " kg");
                IO.println("Posición final:");
                IO.println(estadoFinal.getPVCoordinates().getPosition());
                IO.println("=====================");
        }

}
