package org.example;
import org.hipparchus.geometry.euclidean.threed.Vector3D;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.Frame;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.time.TimeScale;

public class PruebaOrekit {
    public static void main(String[] args) {
        TimeScale utc = TimeScalesFactory.getUTC();
        AbsoluteDate FechaInicial = new AbsoluteDate(2026, 04, 01, 22, 44, 12.000, utc);

        Frame inertialFrame = FramesFactory.getEME2000();

        double RadioTierra = 6_371_000;
        double AltitudOrbita = 170_000;
        double AltitudCentro = RadioTierra + AltitudOrbita;
        double G = 6.674e-11;
        double MasaTierra = 5.972e24;
        double VelocidadOrbita = Math.sqrt((G*MasaTierra)/AltitudCentro);

        Vector3D posicion = new Vector3D(AltitudCentro, 0, 0);
        Vector3D velocidad = new Vector3D(0, VelocidadOrbita,0);
    }
}