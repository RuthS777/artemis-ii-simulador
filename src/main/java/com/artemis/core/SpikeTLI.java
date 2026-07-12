package com.artemis.core;

import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.utils.Constants;

public class SpikeTLI {

    public void ejecutar() {

        AbsoluteDate initialDate = new AbsoluteDate();

        double a = 6771000;
        double e = 0.001;
        double i = Math.toRadians(51.6);
        double omega = 0;
        double raan = 0;
        double lv = 0;

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

        KeplerianPropagator propagator = new KeplerianPropagator(orbit);

        System.out.println("Spike TLI iniciado.");
        System.out.println("Órbita inicial creada correctamente.");
    }
}
