package com.artemis.model;

import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.time.AbsoluteDate;
 

public class PuntoTrayectoria {

    private final AbsoluteDate fecha;
    private final Vector3D posicion;
    private final double velocidad;
    private final double altitud;
    private final double distanciaLuna;
    private final Vector3D posicionLuna;

    public PuntoTrayectoria(
            AbsoluteDate fecha,
            Vector3D posicion,
            double velocidad,
            double altitud,
            double distanciaLuna,
            Vector3D posicionLuna) {

        this.fecha = fecha;
        this.posicion = posicion;
        this.velocidad = velocidad;
        this.altitud = altitud;
        this.distanciaLuna = distanciaLuna;
        this.posicionLuna = posicionLuna; 
    }

    public AbsoluteDate getFecha() {
        return fecha;
    }

    public Vector3D getPosicion() {
        return posicion;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public double getAltitud() {
        return altitud;
    }

    public double getDistanciaLuna() {
        return distanciaLuna;
    }

    public Vector3D getPosicionLuna() {
    return posicionLuna;
    }

}