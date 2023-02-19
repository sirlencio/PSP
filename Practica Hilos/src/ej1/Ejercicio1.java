package ej1;

public class Ejercicio1 {
    final int nOperaciones = 10000;

    final int vInicial = 99;

    /**
     * Variable compartida por los hilos sin control de exclusión
     */
    int contador = vInicial;

    public static void main(String[] args) {
        new Ejercicio1().runProgram();
    }

    public void runProgram() {
        System.out.println("\nValor incial: " + contador);
        Thread hiloInc = new Incrementa();
        Thread hiloDec = new Decrementa();
        hiloInc.start();
        hiloDec.start();
        try {
            hiloInc.join();
            hiloDec.join();
        } catch (InterruptedException ex) {
        }
        System.out.println("\nValor FINAL: " + contador);
    }

    // HILO QUE INCREMENTA
    class Incrementa extends Thread {

        public void run() {
            for (int n = 0; n < nOperaciones; n++) {
                contador++;//Seccion critica
            }
        }
    }

    // HILO QUE DECREMENTA
    class Decrementa extends Thread {

        public void run() {
            for (int n = 0; n < nOperaciones; n++) {
                contador--;//Seccion critica
            }
        }
    }
}