package com.artemis.model;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;

public class PuntoTrayectoria {

    private final AbsoluteDate fecha;
    private final Vector3D posicion;

    public PuntoTrayectoria(AbsoluteDate fecha, Vector3D posicion) {
        this.fecha = fecha;
        this.posicion = posicion;
    }

    public AbsoluteDate getFecha() {
        return fecha;
    }

    public Vector3D getPosicion() {
        return posicion;
    }

}