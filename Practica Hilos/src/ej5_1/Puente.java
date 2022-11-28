package ej5_1;

import java.util.ArrayList;

public class Puente {

    public void añadirDerecha(Volquete vol) {

    }

    public void cruzarDer(Volquete vol) {
        try {

            System.out.println("V_" + vol.getNombre() + " (8) - PUENTE - Comienzo cruzar");
            Thread.sleep(500);
            System.out.println("V_" + vol.getNombre() + " (9) - PUENTE - Fin cruzar");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void cruzarIzq(Volquete vol) {
        try {

            System.out.println("V_" + vol.getNombre() + " (2) - PUENTE - Comienzo cruzar");
            Thread.sleep(500);
            System.out.println("V_" + vol.getNombre() + " (3) - PUENTE - Fin cruzar");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
