package com.artemis;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import org.junit.jupiter.api.Test;

import com.artemis.config.OrekitConfig;

class OrekitConfigTest {

    @Test
    void inicializaOrekitSinExcepciones() {
        assertDoesNotThrow(OrekitConfig::inicializar);
    }
}