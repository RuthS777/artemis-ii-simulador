package com.artemis;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import org.orekit.time.AbsoluteDate;

import com.artemis.model.PuntoTrayectoria;

class PuntoTrayectoriaTest {

    @Test
    void gettersFuncionanCorrectamente() {

        AbsoluteDate fecha = AbsoluteDate.J2000_EPOCH;
        Vector3D posicion = new Vector3D(1, 2, 3);
        Vector3D luna = new Vector3D(4, 5, 6);

        PuntoTrayectoria punto = new PuntoTrayectoria(
                fecha,
                posicion,
                7500.0,
                185000.0,
                380000000.0,
                luna);

        assertEquals(fecha, punto.getFecha());
        assertEquals(posicion, punto.getPosicion());
        assertEquals(7500.0, punto.getVelocidad());
        assertEquals(185000.0, punto.getAltitud());
        assertEquals(380000000.0, punto.getDistanciaLuna());
        assertEquals(luna, punto.getPosicionLuna());
    }

}
