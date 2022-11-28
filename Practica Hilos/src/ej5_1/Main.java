package ej5_1;

public class Main {
    public static void main(String[] args) {
        new Main().ejecutaPrograma();
    }
    public void ejecutaPrograma() {
        Puente puente = new Puente();
        Excavadora exc = new Excavadora();
        for (int i = 0; i < 6; i++) {
            Volquete volquete = new Volquete("Volquete"+(i+1), puente, exc);
            puente.añadirDerecha(volquete);
            volquete.start();
        }
    }
}
