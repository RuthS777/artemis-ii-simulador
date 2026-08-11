package com.artemis.core;

import java.util.ArrayList;
import java.util.List;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.hipparchus.ode.events.Action;
import org.hipparchus.ode.nonstiff.DormandPrince853Integrator;
import org.orekit.attitudes.LofOffset;
import org.orekit.bodies.BodyShape;
import org.orekit.bodies.CelestialBodyFactory;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.forces.gravity.HolmesFeatherstoneAttractionModel;
import org.orekit.forces.gravity.ThirdBodyAttraction;
import org.orekit.forces.gravity.potential.GravityFieldFactory;
import org.orekit.forces.gravity.potential.NormalizedSphericalHarmonicsProvider;
import org.orekit.forces.maneuvers.ImpulseManeuver;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.LOFType;
import org.orekit.orbits.CartesianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.OrbitType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.events.AltitudeDetector;
import org.orekit.propagation.events.DateDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.numerical.NumericalPropagator;
import org.orekit.propagation.sampling.OrekitFixedStepHandler;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;
import org.orekit.utils.PVCoordinates;

import com.artemis.analisis.AnalizadorTrayectoria;
import com.artemis.model.PuntoTrayectoria;
import com.artemis.model.ResultadoSimulacion;



public class SpikeTLI {

        public ResultadoSimulacion ejecutar(double deltaVKms, double horaEncendidoHoras, double alturaOrbitaKm) {

                AbsoluteDate initialDate = new AbsoluteDate(
                                2026, 6, 12,
                                12, 0, 0.0,
                                TimeScalesFactory.getUTC());


                double radioTierra = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
                double semiEjeMayor = radioTierra + alturaOrbitaKm * 1000.0;

                Frame gcrf = FramesFactory.getGCRF();

                AbsoluteDate fechaTLI = initialDate.shiftedBy(horaEncendidoHoras * 3600.0);
                double distanciaLunaEstimadaM = 384_400_000.0;
                Double tiempoLlegadaSeg = null;

                for (int iter = 0; iter < 4; iter++) {
                    Double t = calcularTiempoHastaRadio(deltaVKms, semiEjeMayor, distanciaLunaEstimadaM);
                    if (t == null) {
                    System.out.println("ADVERTENCIA: con este delta-v, la órbita de transferencia "
                                + "no alcanza la distancia lunar estimada (apogeo insuficiente, "
                                + "o hiperbólica).");
                    break;
                     }
                    tiempoLlegadaSeg = t;
                    AbsoluteDate fechaPrueba = fechaTLI.shiftedBy(tiempoLlegadaSeg);
                    distanciaLunaEstimadaM = CelestialBodyFactory.getMoon()
                        .getPVCoordinates(fechaPrueba, gcrf)
                        .getPosition()
                        .getNorm();
                }
                if (tiempoLlegadaSeg == null) {
                tiempoLlegadaSeg = 3.5 * 24 * 3600.0;
}
                AbsoluteDate fechaLlegadaEstimada = fechaTLI.shiftedBy(tiempoLlegadaSeg);

                System.out.printf(
                "Tiempo estimado hasta cruzar la distancia lunar: %.2f días%n",
                tiempoLlegadaSeg / 86400.0);

                Vector3D posicionLunaEstimada = CelestialBodyFactory.getMoon()
                .getPVCoordinates(fechaLlegadaEstimada, gcrf)
                .getPosition();
                Vector3D uLuna = posicionLunaEstimada.normalize();
                              
                Vector3D ejeZ = Vector3D.PLUS_K;
                Vector3D normalOrbital = ejeZ.subtract(uLuna.scalarMultiply(ejeZ.dotProduct(uLuna))).normalize();

                double radioObjetivoFinal = posicionLunaEstimada.getNorm();
                Double nu = calcularAnomaliaVerdadera(deltaVKms, semiEjeMayor, radioObjetivoFinal);

                Vector3D ejeX;
                if (nu != null) {
                   // Rotamos uLuna por -nu alrededor de normalOrbital (Rodrigues) para hallar
                   // dónde debe estar el periapsis: así, al recorrer el ángulo nu desde el
                   // periapsis, la nave queda apuntando exactamente hacia uLuna.
                   Vector3D componenteRotada = Vector3D.crossProduct(normalOrbital, uLuna)
                        .scalarMultiply(-Math.sin(nu));
                   ejeX = uLuna.scalarMultiply(Math.cos(nu)).add(componenteRotada).normalize();
                } else {
                   // Fallback si no se pudo resolver (hiperbólica o apogeo insuficiente):
                   // referencia arbitraria, como antes.
                   Vector3D referenciaAuxiliar = Math.abs(normalOrbital.getX()) < 0.9 ? Vector3D.PLUS_I : Vector3D.PLUS_J;
                  ejeX = referenciaAuxiliar
                      .subtract(normalOrbital.scalarMultiply(referenciaAuxiliar.dotProduct(normalOrbital)))
                      .normalize();
                }
                Vector3D ejeY = Vector3D.crossProduct(normalOrbital, ejeX).normalize();

                double velocidadCircular = Math.sqrt(Constants.WGS84_EARTH_MU / semiEjeMayor);
                Vector3D posicionInicial = ejeX.scalarMultiply(semiEjeMayor);
                Vector3D velocidadInicial = ejeY.scalarMultiply(velocidadCircular);

                Orbit orbit = new CartesianOrbit(
                new PVCoordinates(posicionInicial, velocidadInicial),
                gcrf, initialDate, Constants.WGS84_EARTH_MU);

                SpacecraftState estadoInicial = new SpacecraftState(orbit, 26000.0);

                Frame itrf = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
                // Forma de la Tierra para medir altitud geodésica real (no solo distancia al centro).
                BodyShape tierra = new OneAxisEllipsoid(
                Constants.WGS84_EARTH_EQUATORIAL_RADIUS,
                Constants.WGS84_EARTH_FLATTENING,
                itrf);             

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



                // Evento que dispara la maniobra
                DateDetector detectorTLI = new DateDetector(fechaTLI);

                // Delta-V aproximado de una inyección translunar (m/s)
                Vector3D deltaV = new Vector3D(deltaVKms * 1000.0, 0.0, 0.0);

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

                System.out.println("Maniobra TLI agregada.");
                AbsoluteDate[] fechaReentrada = new AbsoluteDate[1]; // solo registra el PRIMER cruce descendente
                double[] altitudReentrada = new double[]{Double.NaN};

                AltitudeDetector detectorReentrada = new AltitudeDetector(120_000.0, tierra)
                            .withHandler(new EventHandler() {
                                @Override
                                public Action eventOccurred(SpacecraftState s, EventDetector detector, boolean increasing) {
                                         if (!increasing && fechaReentrada[0] == null) {
                                         // increasing=false significa que está BAJANDO a través de 120 km
                                         // (cruce descendente = llegada real a la interfaz de reentrada).
                                         fechaReentrada[0] = s.getDate();
                                         altitudReentrada[0] = 120_000.0;
                                         System.out.println();
                                         System.out.println("===== OAM-7: INTERFAZ DE REENTRADA ALCANZADA =====");
                                         System.out.println("Fecha: " + s.getDate());
                                         System.out.println("===================================================");

                                         // Detenemos la propagación aquí: por debajo de 120 km el modelo (sin
                                         // arrastre atmosférico) deja de ser físicamente válido. El alcance del
                                         // entregable es simular hasta la interfaz de reentrada, no más allá.
                                         return Action.STOP;
                                         }
                                         return Action.CONTINUE;
                         }
                 });

        propagador.addEventDetector(detectorReentrada);

                List<PuntoTrayectoria> trayectoria = new ArrayList<>();

                AnalizadorTrayectoria analizador = new AnalizadorTrayectoria();

                OrekitFixedStepHandler stepHandler = currentState -> {

                        Vector3D posicionNave = currentState.getPVCoordinates().getPosition();

                        double velocidad = currentState.getPVCoordinates()
                                        .getVelocity()
                                        .getNorm();

                        double distanciaCentroTierra = posicionNave.getNorm();

                        double altitud = distanciaCentroTierra
                                        - Constants.WGS84_EARTH_EQUATORIAL_RADIUS;

                        Vector3D posicionLuna = CelestialBodyFactory.getMoon()
                                        .getPVCoordinates(currentState.getDate(),
                                                        FramesFactory.getGCRF())
                                        .getPosition();

                        double distanciaLuna = Vector3D.distance(posicionNave, posicionLuna);

                        analizador.registrar(
                                        distanciaLuna,
                                        currentState.getDate().toString());

                        trayectoria.add(
                                        new PuntoTrayectoria(
                                                        currentState.getDate(),
                                                        posicionNave,
                                                        velocidad,
                                                        altitud,
                                                        distanciaLuna,
                                                        posicionLuna));

                        if (trayectoria.size() % 720 == 0) {

                                System.out.printf(
                                                "%s -> Altitud: %.2f km%n",
                                                currentState.getDate(),
                                                altitud / 1000.0);

                        }

                };
                propagador.setStepHandler(60.0, stepHandler);

                propagador.addForceModel(
                                new ThirdBodyAttraction(
                                                CelestialBodyFactory.getSun()));

                AbsoluteDate fechaFinal = initialDate.shiftedBy(10 * 24 * 3600.0);

                SpacecraftState estadoFinal = propagador.propagate(fechaFinal);

                System.out.println();
                System.out.println("===== RESULTADOS =====");
                System.out.println("Simulación completada.");
                System.out.println("Duración: 10 días.");
                System.out.println("Puntos registrados: " + trayectoria.size());

                analizador.imprimirResultado();

                System.out.println("Posición final:");
                System.out.println(estadoFinal.getPVCoordinates().getPosition());

                System.out.println("Velocidad final:");
                System.out.println(estadoFinal.getPVCoordinates().getVelocity());
                System.out.println();

                System.out.println();
                System.out.println("===== SPIKE TLI =====");
                System.out.println("Órbita creada correctamente.");
                System.out.println("NumericalPropagator configurado.");
                System.out.println("Gravedad terrestre, Luna y Sol agregados.");
                System.out.println("Maniobra TLI configurada.");
                System.out.println("Altura inicial: " + alturaOrbitaKm + " km");
                System.out.println("Masa: " + estadoInicial.getMass() + " kg");
                System.out.println("Posición final:");
                System.out.println(estadoFinal.getPVCoordinates().getPosition());
                System.out.println("=====================");

                double extensionMaximaM = 0.0;
                for (PuntoTrayectoria punto : trayectoria) {
                   double xNave = punto.getPosicion().dotProduct(ejeX);
                   double yNave = punto.getPosicion().dotProduct(ejeY);
                   extensionMaximaM = Math.max(extensionMaximaM, Math.hypot(xNave, yNave));

                   double xLuna = punto.getPosicionLuna().dotProduct(ejeX);
                   double yLuna = punto.getPosicionLuna().dotProduct(ejeY);
                   extensionMaximaM = Math.max(extensionMaximaM, Math.hypot(xLuna, yLuna));
                }

                double metrosPorPixel = extensionMaximaM / 220.0; // 220 = radio en px reservado en el canvas
                return new ResultadoSimulacion(trayectoria, ejeX, ejeY, metrosPorPixel);

        }

  /**
 * Calcula el tiempo de vuelo (segundos) desde el periapsis (donde ocurre el TLI)
 * hasta que la nave cruza un radio objetivo dado, usando la ecuación de Kepler
 * (2 cuerpos, rama de ida, antes del apogeo).
 *
 * A diferencia de calcular el tiempo hasta el APOGEO, esto calcula el tiempo hasta
 * que la nave está a la distancia real de la Luna — que normalmente ocurre bastante
 * antes del apogeo cuando el delta-v es alto, porque la órbita de transferencia
 * "se pasa" de la distancia lunar antes de llegar a su punto más lejano.
 *
 * @return segundos hasta cruzar radioObjetivoM, o null si la órbita es hiperbólica
 *         o si su apogeo no alcanza esa distancia.
 */


 private Double calcularTiempoHastaRadio(double deltaVKms, double radioPeriapsisM, double radioObjetivoM) {
     double mu = Constants.WGS84_EARTH_MU;

     double vCircular = Math.sqrt(mu / radioPeriapsisM);
     double vPeriapsis = vCircular + deltaVKms * 1000.0;

     double energiaEspecifica = (vPeriapsis * vPeriapsis) / 2.0 - mu / radioPeriapsisM;

     if (energiaEspecifica >= 0.0) {
                return null; // hiperbólica: no hay apogeo finito
     }

     double semiEjeMayor = -mu / (2.0 * energiaEspecifica);
     double excentricidad = 1.0 - radioPeriapsisM / semiEjeMayor;
     double radioApoapsis = semiEjeMayor * (1.0 + excentricidad);

     if (radioObjetivoM > radioApoapsis) {
                return null; // la transferencia no llega tan lejos
        }
     double cosE = (1.0 - radioObjetivoM / semiEjeMayor) / excentricidad;
     cosE = Math.max(-1.0, Math.min(1.0, cosE)); // protección numérica

     double anomaliaExcentrica = Math.acos(cosE); // rama de ida (0..pi), antes del apogeo
     double anomaliaMedia = anomaliaExcentrica - excentricidad * Math.sin(anomaliaExcentrica);
     double movimientoMedio = Math.sqrt(mu / Math.pow(semiEjeMayor, 3));

     return anomaliaMedia / movimientoMedio;
     }  

 private Double calcularAnomaliaVerdadera(double deltaVKms, double radioPeriapsisM, double radioObjetivoM) {

        double mu = Constants.WGS84_EARTH_MU;

        double vCircular = Math.sqrt(mu / radioPeriapsisM);
        double vPeriapsis = vCircular + deltaVKms * 1000.0;

        double energiaEspecifica = (vPeriapsis * vPeriapsis) / 2.0 - mu / radioPeriapsisM;
        if (energiaEspecifica >= 0.0) {
                return null;
        }

        double semiEjeMayor = -mu / (2.0 * energiaEspecifica);
        double excentricidad = 1.0 - radioPeriapsisM / semiEjeMayor;
        double radioApoapsis = semiEjeMayor * (1.0 + excentricidad);

        if (radioObjetivoM > radioApoapsis) {
                return null;
        }

        double cosE = (1.0 - radioObjetivoM / semiEjeMayor) / excentricidad;
        cosE = Math.max(-1.0, Math.min(1.0, cosE));
        double anomaliaExcentrica = Math.acos(cosE);

        double tanMitadNu = Math.sqrt((1.0 + excentricidad) / (1.0 - excentricidad))
                        * Math.tan(anomaliaExcentrica / 2.0);

        return 2.0 * Math.atan(tanMitadNu);
}
}
