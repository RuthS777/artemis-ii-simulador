package com.artemis;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.artemis.core.SpikeTLI;
import com.artemis.model.ResultadoSimulacion;

class SpikeTLITest {

    @Test
    void simulacionGeneraTrayectoria() {

        SpikeTLI spike = new SpikeTLI();

        ResultadoSimulacion resultado =
                spike.ejecutar(3.15, 0.0, 185.0);

        assertNotNull(resultado);
        assertNotNull(resultado.getTrayectoria());
        assertFalse(resultado.getTrayectoria().isEmpty());

    }

}
