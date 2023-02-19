package ej5_1;

public class Ejercicio5 {
    public static void main(String[] args) {
        new Ejercicio5().ejecutaPrograma();
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
