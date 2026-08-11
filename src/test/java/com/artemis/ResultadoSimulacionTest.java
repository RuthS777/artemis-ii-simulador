package com.artemis;

import java.util.ArrayList;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;

import com.artemis.model.ResultadoSimulacion;

class ResultadoSimulacionTest {

    @Test
    void resultadoSeConstruyeCorrectamente() {

        ResultadoSimulacion resultado =
                new ResultadoSimulacion(
                        new ArrayList<>(),
                        Vector3D.PLUS_I,
                        Vector3D.PLUS_J,
                        1000.0);

        assertNotNull(resultado.getTrayectoria());
        assertEquals(Vector3D.PLUS_I, resultado.getEjeProyeccionX());
        assertEquals(Vector3D.PLUS_J, resultado.getEjeProyeccionY());
        assertEquals(1000.0, resultado.getMetrosPorPixel());

    }

}
