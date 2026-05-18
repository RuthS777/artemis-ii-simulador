package com.example;

import org.hipparchus.util.FastMath;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngleType;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;

import java.io.File;

public class HolamundoOrekit {

    public static void main(String[] args) {
        // Cambia esto por TU ruta real
File orekitData = new File("C:\\Users\\Usuario\\OneDrive\\Escritorio\\orekit-data-master");
        DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
        manager.addProvider(new DirectoryCrawler(orekitData));

        Frame marcoReferencia = FramesFactory.getEME2000();
        AbsoluteDate fechaInicial = new AbsoluteDate(2026, 5, 17, 12, 0, 0.0, TimeScalesFactory.getUTC());
        
        double mu = Constants.WGS84_EARTH_MU; 
        double a = 6378137.0 + 600000.0;      
        double e = 0.001;                     
        double i = FastMath.toRadians(98.0);  
        double omega = 0.0;                   
        double raan = 0.0;                    
        double lM = 0.0;                      

        Orbit orbitaInicial = new KeplerianOrbit(a, e, i, omega, raan, lM, PositionAngleType.MEAN, marcoReferencia, fechaInicial, mu);

        KeplerianPropagator propagador = new KeplerianPropagator(orbitaInicial);

        AbsoluteDate fechaFinal = fechaInicial.shiftedBy(3600.0);
        SpacecraftState estadoFinal = propagador.propagate(fechaFinal);

        System.out.println("Fecha inicial: " + fechaInicial);
        System.out.println("Posición inicial (X,Y,Z): " + orbitaInicial.getPVCoordinates().getPosition());
        System.out.println("--------------------------------------------------");
        System.out.println("Fecha tras 1 hora: " + fechaFinal);
        System.out.println("Posición predicha (X,Y,Z): " + estadoFinal.getPVCoordinates().getPosition());
    }
}
