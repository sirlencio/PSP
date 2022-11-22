package ej1;

import java.util.concurrent.Semaphore;

/**
 * Ejemplo de aplicación que demuestra el problema de la condición de carrera
 *
 * Creamos sección critica utilizando un semaforo binario.
 *
 * El problema de esta propuesta es que hay que prestar mucha atención al orden
 * de las operaciones para evitar interbloqueos o una sección critica no operativa.
 * 
 * Prueba a cambiar o comentar alguna de las operaciones adquire() o release()
 */
public class IncDec_Mutex {

    final int N_OPERACIONES = 30000;

    final int VALOR_INICIAL = 99;

    /**
     * Semaforo binario que nos garantizará la exclusión mutua
     */
    protected final Semaphore mutex = new Semaphore(1);

     int contador = VALOR_INICIAL;

    public static void main(String[] args) {
        new IncDec_Mutex().runProgram();
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
            for (int n = 0; n < N_OPERACIONES; n++) {
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
            for (int n = 0; n < N_OPERACIONES; n++) {
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
