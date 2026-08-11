package com.artemis.task;

import com.artemis.core.SpikeTLI;
import com.artemis.model.ResultadoSimulacion;

import javafx.concurrent.Task;

/**
 * Ejecuta la propagación orbital en un hilo secundario y devuelve
 * la trayectoria completamente precalculada. La interfaz JavaFX
 * consumirá esta lista para reproducir la animación mediante un
 * AnimationTimer, sin ejecutar Orekit en tiempo real.
 */
public class SimulacionTask extends Task<ResultadoSimulacion> { 

    private final double deltaV;
    private final double horaEncendido;
    private final double alturaOrbita;

    public SimulacionTask(double deltaV, double horaEncendido, double alturaOrbita) {
        this.deltaV = deltaV;
        this.horaEncendido = horaEncendido;
        this.alturaOrbita = alturaOrbita;
    }

    @Override
    protected ResultadoSimulacion call() throws Exception {

        SpikeTLI spike = new SpikeTLI();

        return spike.ejecutar(deltaV, horaEncendido, alturaOrbita);

    }

}