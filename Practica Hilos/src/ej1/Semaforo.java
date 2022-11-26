package ej1;

import java.util.concurrent.Semaphore;

public class Semaforo {

    final int noperaciones = 30000;

    final int vinicial = 99;

    /**
     * Semaforo binario que nos garantizará la exclusión mutua
     */
    protected final Semaphore mutex = new Semaphore(1);

    int contador = vinicial;

    public static void main(String[] args) {
        new Semaforo().runProgram();
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
            for (int n = 0; n < noperaciones; n++) {
                try {
                    mutex.acquire(1);   // Entramos en sección critica
                    contador++;         // Sección critica
                    mutex.release(1);   // Salimos de sección critica
                } catch (Exception ex) {
                    // Desbloqueamos si salimos
                    mutex.release(1);
                }
            }
        }
    }

    // HILO QUE DECREMENTA
    class Decrementa extends Thread {

        public void run() {
            for (int n = 0; n < noperaciones; n++) {
                try {
                    mutex.acquire(1);   // Entramos en sección critica
                    contador--;         // Sección critica
                    mutex.release(1);   // Salimos de sección critica
                } catch (Exception ex) {
                    // Desbloqueamos si salimos
                    mutex.release(1);
                }
            }
        }
    }

}
