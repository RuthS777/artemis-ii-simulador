package com.artemis;

import com.artemis.config.OrekitConfig;
import com.artemis.ui.InterfazFX;
 
import javafx.application.Application; 

public class Main {

    public static void main(String[] args) {

        System.out.println("Simulación Artemis II - Propagación LEO iniciada");

        OrekitConfig.inicializar();

        System.out.println("Inicializando interfaz Grafica");

        Application.launch(InterfazFX.class, args);

        System.out.println("Simulación finalizada");
    }
}

