package com.artemis.analisis;

public class AnalizadorTrayectoria {

    private double menorDistanciaLuna;
    private String instante;

    public AnalizadorTrayectoria() {
        menorDistanciaLuna = Double.MAX_VALUE;
    }

    public void registrar(double distancia, String fecha) {

        if (distancia < menorDistanciaLuna) {
            menorDistanciaLuna = distancia;
            instante = fecha;
        }

    }

    public void imprimirResultado() {

        IO.println();
        IO.println("===== ANÁLISIS DEL SOBREVUELO =====");

        System.out.printf(
                "Menor distancia a la Luna: %.2f km%n",
                menorDistanciaLuna / 1000.0);

        IO.println("Instante del mayor acercamiento:");

        IO.println(instante);

        IO.println("===================================");

    }

}
