package com.artemis;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.artemis.config.OrekitConfig;
import com.artemis.core.SpikeTLI;
import com.artemis.model.ResultadoSimulacion;

class MissionIntegrationTest {

    @Test
    void misionCompletaSeEjecutaCorrectamente() {

        OrekitConfig.inicializar();

        SpikeTLI simulador = new SpikeTLI();

        ResultadoSimulacion resultado = simulador.ejecutar(3.15, 0.0, 185.0);

        assertNotNull(resultado);
        assertNotNull(resultado.getTrayectoria());
        assertTrue(resultado.getTrayectoria().size() >= 500);
    }
}