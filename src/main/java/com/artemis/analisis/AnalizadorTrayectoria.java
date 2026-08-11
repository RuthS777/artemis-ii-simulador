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

        System.out.println();
        System.out.println("===== ANÁLISIS DEL SOBREVUELO =====");

        System.out.printf(
                "Menor distancia a la Luna: %.2f km%n",
                menorDistanciaLuna / 1000.0);

        System.out.println("Instante del mayor acercamiento:");

        System.out.println(instante);

        System.out.println("===================================");

    }

}
