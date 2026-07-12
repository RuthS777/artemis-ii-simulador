package com.artemis.config;

import java.io.File;

import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;

public class OrekitConfig {

    /**
     * Inicializa los datos de Orekit.
     * Debe ejecutarse antes de utilizar cualquier clase de Orekit.
     */
    public static void inicializar() {

        // Cambiar esta ruta por la ubicación local de orekit-data
        File orekitData = new File("C:\\Users\\celes\\OneDrive\\Escritorio\\Orekit\\orekit-data-main");

        DataProvidersManager manager =
                DataContext.getDefault().getDataProvidersManager();

        manager.addProvider(new DirectoryCrawler(orekitData));

        System.out.println("Datos de Orekit cargados correctamente.");
    }
}