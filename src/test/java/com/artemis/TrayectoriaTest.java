package com.artemis;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.artemis.core.SpikeTLI;
import com.artemis.model.ResultadoSimulacion;

class TrayectoriaTest {

    @Test
    void trayectoriaTieneMasDe500Puntos() {

        SpikeTLI spike = new SpikeTLI();

        ResultadoSimulacion resultado =
                spike.ejecutar(3.15, 0.0, 185.0);

        assertTrue(resultado.getTrayectoria().size() >= 500);

    }

}