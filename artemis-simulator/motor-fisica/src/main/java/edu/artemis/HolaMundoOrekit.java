package edu.artemis;

import java.io.File;

import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.ZipJarCrawler;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

public class HolaMundoOrekit {

    public static void main(String[] args) throws Exception {

        // 1. Cargar datos de Orekit (SIEMPRE debe ser lo primero)
        DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
        manager.addProvider(new ZipJarCrawler(new File("motor-fisica/orekit-data-main.zip")));
        System.out.println("Datos de Orekit cargados correctamente.");

        // 2. Fecha de inicio: lanzamiento Artemis II
        AbsoluteDate fechaInicio = new AbsoluteDate(
            2026, 4, 1, 12, 0, 0.0,
            TimeScalesFactory.getUTC()
        );

        // 3. Orbita de estacionamiento LEO (~185 km, circular)
        double radioTierra    = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
        double altitud        = 185_000.0;
        double semiEjeMayor   = radioTierra + altitud;
        double excentricidad  = 0.0;
        double inclinacion    = Math.toRadians(28.5);
        double raan           = 0.0;
        double argPerigeo     = 0.0;
        double anomaliaMedia  = 0.0;

        KeplerianOrbit orbitaLEO = new KeplerianOrbit(
            semiEjeMayor,
            excentricidad,
            inclinacion,
            raan,
            argPerigeo,
            anomaliaMedia,
            PositionAngleType.MEAN,
            FramesFactory.getEME2000(),
            fechaInicio,
            Constants.WGS84_EARTH_MU
        );

        System.out.println("Orbita LEO definida.");
        System.out.println("Semi-eje mayor: " + semiEjeMayor / 1000 + " km");
        System.out.println("Periodo orbital: " + 
            String.format("%.2f", orbitaLEO.getKeplerianPeriod() / 60) + " minutos");

        // 4. Propagar 90 minutos e imprimir posicion cada 10 minutos
        KeplerianPropagator propagador = new KeplerianPropagator(orbitaLEO);

        System.out.println("\n--- Propagacion de 90 minutos ---");
        System.out.printf("%-12s %-15s %-15s %-15s%n", "Tiempo", "X (km)", "Y (km)", "Z (km)");

        for (int min = 0; min <= 90; min += 10) {
            AbsoluteDate fecha = fechaInicio.shiftedBy(min * 60.0);
            var estado = propagador.propagate(fecha);
            var pos = estado.getPVCoordinates().getPosition();
            System.out.printf("T+%-4d min   %-15.1f %-15.1f %-15.1f%n",
                min,
                pos.getX() / 1000,
                pos.getY() / 1000,
                pos.getZ() / 1000);
        }

        System.out.println("\nPrueba exitosa. Cadena de herramientas Orekit funcionando.");
    }
}