package com.artemis.model;

import java.util.List;

import org.hipparchus.geometry.euclidean.threed.Vector3D;

public class ResultadoSimulacion {

    private final List<PuntoTrayectoria> trayectoria;
    private final Vector3D ejeProyeccionX;
    private final Vector3D ejeProyeccionY;
    private final double metrosPorPixel;

    public ResultadoSimulacion(List<PuntoTrayectoria> trayectoria,
                                Vector3D ejeProyeccionX,
                                Vector3D ejeProyeccionY,
                                double metrosPorPixel) {
        this.trayectoria = trayectoria;
        this.ejeProyeccionX = ejeProyeccionX;
        this.ejeProyeccionY = ejeProyeccionY;
        this.metrosPorPixel = metrosPorPixel;
    }

    public List<PuntoTrayectoria> getTrayectoria() { return trayectoria; }
    public Vector3D getEjeProyeccionX() { return ejeProyeccionX; }
    public Vector3D getEjeProyeccionY() { return ejeProyeccionY; }
    public double getMetrosPorPixel() { return metrosPorPixel; }
}