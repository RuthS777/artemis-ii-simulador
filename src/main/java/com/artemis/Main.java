package com.artemis;

import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;

public class Main {

    public static void main(String[] args) {

        IO.println("Simulación Artemis II - Propagación LEO iniciada");

        // Tiempo inicial correcto (ahora)
        AbsoluteDate initialDate = new AbsoluteDate();

        // Parámetros de órbita LEO
        double a = 6771000; // semi-eje mayor (m)
        double e = 0.001;   // excentricidad
        double i = Math.toRadians(51.6); // inclinación
        double omega = 0;   // argumento del perigeo
        double raan = 0;    // nodo ascendente
        double lv = 0;      // anomalía verdadera inicial

        // Creación de la órbita
        KeplerianOrbit orbit = new KeplerianOrbit(
                a,
                e,
                i,
                omega,
                raan,
                lv,
                PositionAngleType.TRUE,
                FramesFactory.getEME2000(),
                initialDate,
                Constants.EIGEN5C_EARTH_MU
        );

        // Propagador orbital
        KeplerianPropagator propagator = new KeplerianPropagator(orbit);

        IO.println("Órbita LEO creada correctamente");
        IO.println("Iniciando propagación...");

        // Simulación: 10 minutos, pasos de 60 segundos
        for (int t = 0; t <= 600; t += 60) {

            AbsoluteDate currentDate = initialDate.shiftedBy(t);

            var state = propagator.propagate(currentDate);

            IO.println("Tiempo (s): " + t);

            IO.println("Posición (m): " +
                    state.getPVCoordinates().getPosition());

            IO.println("Velocidad (m/s): " +
                    state.getPVCoordinates().getVelocity());

            IO.println("------------------------------");
        }

        IO.println("Simulación finalizada");
    }
}

