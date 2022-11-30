package ej5_1;

public class Main {
    public static void main(String[] args) {
        new Main().ejecutaPrograma();
    }
    public void ejecutaPrograma() {
        Puente bridge = new Puente();
        Excavadora exc = new Excavadora();
        for (int i = 0; i < 6; i++) {
            Camion camion = new Camion("Camion nº"+(i+1), bridge, exc);
            camion.start();
        }
    }
}
