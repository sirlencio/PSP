package ej5_2;

import java.util.ArrayList;

public class Puente {
    ArrayList<Thread> ladoIzq = new ArrayList<>();
    ArrayList<Thread> ladoDer = new ArrayList<>();

    public void anadirDerecha(Camion camion) {
        ladoDer.add(camion);
    }

    public void cruzandoIzq(Camion camion) {
        try {
            ladoDer.remove(camion);
            System.out.println("C_" + camion.getNombre() + " (2) - PUENTE - Comienzo cruzar");
            Thread.sleep(500);
            ladoIzq.add(camion);
            System.out.println("C_" + camion.getNombre() + " (3) - PUENTE - Fin cruzar");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void cruzandoDer(Camion camion) {
        try {
            ladoIzq.remove(camion);
            System.out.println("C_" + camion.getNombre() + " (8) - PUENTE - Comienzo cruzar");
            Thread.sleep(500);
            ladoDer.add(camion);
            System.out.println("C_" + camion.getNombre() + " (9) - PUENTE - Fin cruzar");
            notify(); //Llamamos al siguiente hilo
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int nLadoIzq() {
        return ladoIzq.size();
    }

}
