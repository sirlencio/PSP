package ej5_2;

public class Excavadora extends Thread {
    public Excavadora() {
    }

    public void carga(Camion camion) {
        try {
            System.out.println("C_" + camion.getNombre() + " (5) – Excavadora - Cargando");
            Thread.sleep(1500);
            System.out.println("C_" + camion.getNombre() + " (6) – Excavadora - Fin carga");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
