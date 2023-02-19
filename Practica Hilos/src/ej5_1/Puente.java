package ej5_1;

public class Puente {

    public void cruzandoIzq(Camion camion) {
        try {
            System.out.println("C_" + camion.getNombre() + " (2) - PUENTE - Cruzando");
            Thread.sleep(500);
            System.out.println("C_" + camion.getNombre() + " (3) - PUENTE - Fin cruzado");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void cruzandoDer(Camion camion) {
        try {
            System.out.println("C_" + camion.getNombre() + " (8) - PUENTE - Cruzando");
            Thread.sleep(500);
            System.out.println("C_" + camion.getNombre() + " (9) - PUENTE - Fin cruzado");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
