package com.artemis.ui;

import java.util.List;

import com.artemis.model.PuntoTrayectoria;
import com.artemis.model.ResultadoSimulacion;
import com.artemis.task.SimulacionTask;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class InterfazFX extends Application {
    private List<PuntoTrayectoria> trayectoria; // lista de todos lo puntos de trayectoria
    private ResultadoSimulacion resultado;
    private int indiceActual = 0; // punto actual que se esta animando
    private boolean reproduciendo = false; // si la simulación se esta reproduciendo o no
    private double escalaTiempo = 1.0; // velocidad de la reproducción
    private Button botonEjecutar;
    private Canvas canvas; // Este es el lienzo
    private Label lblTiempo, lblAltitud, lblVelocidad, lblDistanciaLuna; // Los datos de telemetría
    private AnimationTimer timer; // motor de animación
    private double[] estrellasX, estrellasY, estrellasBrillo; // fondo estrellado
    private final java.util.List<double[]> rastroNave = new java.util.ArrayList<>();
    private static final int RASTRO_MAX = 400;

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane(); // Esta parte se encarga de
        // dividir y repartir las secciones de la interfaz

        root.setLeft(crearFormularioParametros());
        root.setRight(crearPanelTelemetria());
        root.setCenter(crearCanvas());

        Scene scene = new Scene(root, 1200, 800);
        stage.setScene(scene);
        stage.setTitle("Artemis II - Simulador de trayectoria Lunar");
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }

    private VBox crearFormularioParametros() { // Organiza los elementos verticalmente
        VBox box = new VBox(8);
        box.setPadding(new Insets(10));
        box.setPrefWidth(220);
        box.setStyle("-fx-background-color: #1a1a2e; -fx-text-fill: white;");

        TextField deltaV = new TextField("3.1430");
        TextField horaEncendido = new TextField("3");
        TextField alturaOrbita = new TextField("185 ");

        botonEjecutar = new Button("Ejecutar Simulación"); // Estos son los botones donde
        Button botonReiniciar = new Button("Reiniciar"); // se podrá hacer clic

        botonEjecutar.setOnAction(e -> {
            try {
                double dV = Double.parseDouble(deltaV.getText().trim());
                double hEnc = Double.parseDouble(horaEncendido.getText().trim());
                double alt = Double.parseDouble(alturaOrbita.getText().trim());

                botonEjecutar.setDisable(true);

                ejecutarSimulacion(dV, hEnc, alt);
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(AlertType.ERROR,
                        "Por favor ingrese valores numéricos válidos en el formulario.");
                alert.showAndWait();
            }
        });

        botonReiniciar.setOnAction(e -> reiniciar());

        box.getChildren().addAll(
                new Label("Delta-v TLI (km/s)"), deltaV,
                new Label("Hora de encendido TLI (h)"), horaEncendido,
                new Label("Altura órbita inicial (km)"), alturaOrbita,
                botonEjecutar, botonReiniciar,
                crearControlesReproduccion());
        box.setStyle("-fx-background-color: #1a1a2e;");

        return box;

    }

    private VBox crearPanelTelemetria() {
        VBox box = new VBox(6);
        box.setPadding(new Insets(10));
        box.setPrefWidth(220);
        box.setStyle("-fx-background-color: #1a1a2e;");
        lblTiempo = new Label("Tiempo: --");
        lblAltitud = new Label("Altitud: --");
        lblVelocidad = new Label("Velocidad: --");
        lblDistanciaLuna = new Label("Distancia a la Luna: --");
        lblTiempo.setStyle("-fx-text-fill: white;");
        lblAltitud.setStyle("-fx-text-fill: white;");
        lblVelocidad.setStyle("-fx-text-fill: white;");
        lblDistanciaLuna.setStyle("-fx-text-fill: white;");
        box.getChildren().addAll(lblTiempo, lblAltitud, lblVelocidad, lblDistanciaLuna);
        return box;

    }

    private StackPane crearCanvas() {
        canvas = new Canvas(600, 500);

        StackPane contenedor = new StackPane(canvas);
        contenedor.setStyle("-fx-background-color: black;");

        canvas.widthProperty().bind(contenedor.widthProperty());
        canvas.heightProperty().bind(contenedor.heightProperty());

        canvas.widthProperty().addListener((obs, viejo, nuevo) -> redibujarActual());
        canvas.heightProperty().addListener((obs, viejo, nuevo) -> redibujarActual());

        generarEstrellas();
        dibujarEscenaBase();
        return contenedor;
    }

    private void redibujarActual() {
        if (canvas.getWidth() <= 0 || canvas.getHeight() <= 0)
            return;
        generarEstrellas();
        if (trayectoria != null && indiceActual < trayectoria.size()) {
            actualizarFrame(trayectoria.get(indiceActual));
        } else {
            dibujarEscenaBase();
        }
    }

    private void generarEstrellas() {
        java.util.Random rnd = new java.util.Random(42); // Las estrellas son fijas, siempre las mismas
        int n = 150;
        estrellasX = new double[n];
        estrellasY = new double[n];
        estrellasBrillo = new double[n];
        for (int i = 0; i < n; i++) {
            estrellasX[i] = rnd.nextDouble() * canvas.getWidth();
            estrellasY[i] = rnd.nextDouble() * canvas.getHeight();
            estrellasBrillo[i] = 0.3 + rnd.nextDouble() * 0.7;
        }
    }

    private void dibujarEscenaBase() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.rgb(3, 3, 12));// Fondo negro un poco azulado
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Estrellas de fondo, posiciones fijas
        for (int i = 0; i < estrellasX.length; i++) {
            gc.setFill(Color.rgb(255, 255, 255, estrellasBrillo[i]));
            gc.fillOval(estrellasX[i], estrellasY[i], 1.5, 1.5);
        }

        double cx = canvas.getWidth() / 2.0;
        double cy = canvas.getHeight() / 2.0;

        dibujarTierra(gc, cx, cy);
    }

    private void dibujarTierra(GraphicsContext gc, double cx, double cy) {
        double radio = 10;

        // Halo atmosférico
        javafx.scene.paint.RadialGradient halo = new javafx.scene.paint.RadialGradient(
                0, 0, cx, cy, radio * 2.2, false,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0, Color.rgb(100, 170, 255, 0.35)),
                new javafx.scene.paint.Stop(1, Color.rgb(100, 170, 255, 0)));
        gc.setFill(halo);
        gc.fillOval(cx - radio * 2.2, cy - radio * 2.2, radio * 4.4, radio * 4.4);

        // Esfera con luz simulada
        javafx.scene.paint.RadialGradient esferaTierra = new javafx.scene.paint.RadialGradient(
                0, 0, cx - radio * 0.4, cy - radio * 0.4, radio * 1.6, false,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0.0, Color.rgb(120, 190, 255)),
                new javafx.scene.paint.Stop(0.5, Color.rgb(40, 110, 210)),
                new javafx.scene.paint.Stop(1.0, Color.rgb(10, 40, 90)));
        gc.setFill(esferaTierra);
        gc.fillOval(cx - radio, cy - radio, radio * 2, radio * 2);

        // Continentes esquemáticos (No son realistar por ahora)
        gc.setFill(Color.rgb(60, 150, 70, 0.85));
        gc.fillOval(cx - radio * 0.5, cy - radio * 0.3, radio * 0.6, radio * 0.4);
        gc.fillOval(cx + radio * 0.1, cy + radio * 0.1, radio * 0.5, radio * 0.35);

        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.fillText("Tierra", cx - 14, cy + radio + 14);
    }

    private void dibujarLuna(GraphicsContext gc, double x, double y) {
        double radio = 4;

        javafx.scene.paint.RadialGradient esferaLuna = new javafx.scene.paint.RadialGradient(
                0, 0, x - radio * 0.4, y - radio * 0.4, radio * 1.6, false,
                javafx.scene.paint.CycleMethod.NO_CYCLE,
                new javafx.scene.paint.Stop(0.0, Color.rgb(230, 230, 225)),
                new javafx.scene.paint.Stop(0.6, Color.rgb(170, 170, 165)),
                new javafx.scene.paint.Stop(1.0, Color.rgb(90, 90, 90)));
        gc.setFill(esferaLuna);
        gc.fillOval(x - radio, y - radio, radio * 2, radio * 2);

        // Cráteres esquemáticos, posiciones fijas
        gc.setFill(Color.rgb(120, 120, 115, 0.6));
        gc.fillOval(x - radio * 0.4, y - radio * 0.3, radio * 0.35, radio * 0.35);
        gc.fillOval(x + radio * 0.1, y + radio * 0.2, radio * 0.25, radio * 0.25);

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(javafx.scene.text.Font.font(9));
        gc.fillText("Luna", x - 10, y - radio - 4);
    }

    private VBox crearControlesReproduccion() {

        VBox box = new VBox(6);
        box.setPadding(new Insets(10, 0, 0, 0));

        Label titulo = new Label("Controles de reproducción");

        Button botonPlay = new Button("Reproducir");
        Button botonPausa = new Button("Pausar");

        botonPlay.setPrefWidth(180);
        botonPausa.setPrefWidth(180);

        Label lblVelocidad = new Label("Escala de tiempo: 1x");

        Slider sliderVelocidad = new Slider(1, 1000, 1);
        sliderVelocidad.setPrefWidth(180);
        sliderVelocidad.setMaxWidth(180);

        sliderVelocidad.valueProperty().addListener((obs, old, val) -> {
            escalaTiempo = val.doubleValue();

            lblVelocidad.setText(
                    String.format("Escala de tiempo: %.0fx", escalaTiempo));
        });

        botonPlay.setOnAction(e -> {
            reproduciendo = true;
        });

        botonPausa.setOnAction(e -> {
            reproduciendo = false;
        });

        box.getChildren().addAll(
                titulo,
                botonPlay,
                botonPausa,
                lblVelocidad,
                sliderVelocidad);

        return box;
    }

    private void ejecutarSimulacion(double deltaV, double horaEncendido, double alturaOrbita) {
        SimulacionTask task = new SimulacionTask(deltaV, horaEncendido, alturaOrbita);

        task.setOnSucceeded(e -> {
            botonEjecutar.setDisable(false);
            resultado = task.getValue();
            trayectoria = resultado.getTrayectoria();
            indiceActual = 0; // reinicia el índice
            iniciarAnimacion();
        });

        task.setOnFailed(e -> {
            botonEjecutar.setDisable(false);
            Throwable ex = task.getException();
            ex.printStackTrace();
            new Alert(AlertType.ERROR, "Falló la simulación:\n" + ex).showAndWait();
        });

        Thread hilo = new Thread(task); // Ejecuta en un hilo separado
        hilo.setDaemon(true);
        hilo.start();
    }

    private void reiniciar() {
        reproduciendo = false;
        indiceActual = 0;
        rastroNave.clear();
        if (timer != null)
            timer.stop();
        dibujarEscenaBase();
        lblTiempo.setText("Tiempo: --");
        lblAltitud.setText("Altitud: --");
        lblVelocidad.setText("Velocidad: --");
        lblDistanciaLuna.setText("Distancia a la Luna: --");

    }

    private void iniciarAnimacion() {

        if (timer != null)
            timer.stop();

        timer = new AnimationTimer() {
            private long ultimoFrame = -1;

            @Override
            public void handle(long now) { // Es llamado 60 veces por segundo
                if (!reproduciendo || trayectoria == null || indiceActual >= trayectoria.size()) {
                    ultimoFrame = -1;
                    return;
                }
                if (ultimoFrame < 0) {
                    ultimoFrame = now;
                    return;
                }

                double segundosTranscurridos = (now - ultimoFrame) / (1_000_000_000.0); // Calcula el tiempo entre
                                                                                        // frames
                int puntosAAvanzar = (int) Math.max(1, segundosTranscurridos * escalaTiempo * 10);

                indiceActual = Math.min(indiceActual + puntosAAvanzar, trayectoria.size() - 1);

                ultimoFrame = now;

                actualizarFrame(trayectoria.get(indiceActual));
            }
        };

        timer.start();
        reproduciendo = true;

    }

    private void actualizarFrame(PuntoTrayectoria punto) {
        lblTiempo.setText("Tiempo: " + punto.getFecha());
        lblAltitud.setText(String.format("Altitud: %.1f km", punto.getAltitud() / 1000.0));
        lblVelocidad.setText(String.format("Velocidad: %.2f m/s", punto.getVelocidad()));
        lblDistanciaLuna.setText(String.format("Distancia a la Luna: %.0f km", punto.getDistanciaLuna() / 1000.0));
        // Actualiza la Telemetría

        // Dibuja la escena base con el fondp, la Tierra y la Luna, limpia y redibuja
        dibujarEscenaBase();

        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Define la posición del centro de la Tierra
        double centroTierraX = canvas.getWidth() / 2.0;
        double centroTierraY = canvas.getHeight() / 2.0;
        double metrosPorPixel = resultado.getMetrosPorPixel();

        double xLuna = punto.getPosicionLuna().dotProduct(resultado.getEjeProyeccionX());
        double yLuna = punto.getPosicionLuna().dotProduct(resultado.getEjeProyeccionY());
        double lunaX = centroTierraX + xLuna / metrosPorPixel;
        double lunaY = centroTierraY - yLuna / metrosPorPixel;
        dibujarLuna(gc, lunaX, lunaY);

        double xNave = punto.getPosicion().dotProduct(resultado.getEjeProyeccionX());
        double yNave = punto.getPosicion().dotProduct(resultado.getEjeProyeccionY());
        double naveX = centroTierraX + xNave / metrosPorPixel;
        double naveY = centroTierraY - yNave / metrosPorPixel;

        // Registra el punto actual en el rastro
        rastroNave.add(new double[] { naveX, naveY });
        if (rastroNave.size() > RASTRO_MAX) {
            rastroNave.remove(0);

        }

        // Dibuja el rastro con opacidad creciente hacia el punto más reciente
        gc.setLineWidth(1.5);
        for (int i = 1; i < rastroNave.size(); i++) {
            double opacidad = (double) i / rastroNave.size();
            gc.setStroke(Color.rgb(255, 165, 0, opacidad * 0.7));
            double[] p0 = rastroNave.get(i - 1);
            double[] p1 = rastroNave.get(i);
            gc.strokeLine(p0[0], p0[1], p1[0], p1[1]);
        }

        // Marcador de la nave con pequeño resplandor
        gc.setFill(Color.rgb(255, 200, 100, 0.4));
        gc.fillOval(naveX - 6, naveY - 6, 12, 12);
        gc.setFill(Color.ORANGE);
        gc.fillOval(naveX - 3, naveY - 3, 6, 6);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
