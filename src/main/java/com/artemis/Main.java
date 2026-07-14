package com.artemis;

import com.artemis.config.OrekitConfig;
import com.artemis.core.SpikeTLI;

// import org.orekit.frames.FramesFactory;
// import org.orekit.orbits.KeplerianOrbit;
// import org.orekit.orbits.PositionAngleType;
// import org.orekit.propagation.analytical.KeplerianPropagator;
// import org.orekit.time.AbsoluteDate;
// import org.orekit.utils.Constants;

public class Main {

    public static void main(String[] args) {

        IO.println("Simulación Artemis II - Propagación LEO iniciada");

        OrekitConfig.inicializar();

    SpikeTLI spike = new SpikeTLI();
spike.ejecutar();

        IO.println("Simulación finalizada");
    }
}

