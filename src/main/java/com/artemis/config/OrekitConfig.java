package com.artemis.config;

import java.io.File;

import org.orekit.data.DataContext;
import org.orekit.data.DirectoryCrawler;

public class OrekitConfig {

    public static void inicializar() {

        File orekitData = new File(resolveOrekitDataPath());

        if (!orekitData.exists()) {
            throw new IllegalStateException(
                    "No se encontró la carpeta orekit-data.\n" +
                            "Ruta esperada: " + orekitData.getAbsolutePath());
        }

        DataContext.getDefault()
                .getDataProvidersManager()
                .addProvider(new DirectoryCrawler(orekitData));

        System.out.println("Datos de Orekit cargados correctamente.");
    }

    /**
     * Busca automáticamente la carpeta orekit-data dentro del proyecto.
     */
    private static String resolveOrekitDataPath() {

        String[] rutas = {
                "orekit-data",
                "orekit-data-main",
                "data/orekit-data",
                "data/orekit-data-main"
        };

        for (String ruta : rutas) {
            if (new File(ruta).exists()) {
                return ruta;
            }
        }

        return "orekit-data";
    }

}