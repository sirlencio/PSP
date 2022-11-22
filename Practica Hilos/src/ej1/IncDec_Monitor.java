/*
    Programa que intenta demostrar los problemas de la condición de carrera
    si no utilizamos mecanismos de sincronización.
 */
package ej1;

/**
 * Ejemplo de aplicación que demuestra el problema de la condición de carrera
 *
 * Lo solucionamos utilizando un Monitor (synchronized)
 *
 * El programa  da resultados acorde a lo esperado. Solventa los problemas
 * de sincronización
 *
 *
 */
public class IncDec_Monitor {

    final int N_OPERACIONES = 3000;

    final int VALOR_INICIAL = 99;

    /**
     * Variable compartida por los hilos sin control de exclusión
     * 
     * Se crea clase pues synchronized pide un objeto para realizar monitor
     */
    class Contador {
        public int value=VALOR_INICIAL;
    }
    
    final Contador contador = new Contador();

    public static void main(String[] args) {
        new IncDec_Monitor().runProgram();
    }

    public void runProgram() {
        System.out.println("\nValor incial: " + contador.value);
        Thread hiloInc = new Incrementa();
        Thread hiloDec = new Decrementa();
        hiloInc.start();
        hiloDec.start();
        try {
            hiloInc.join();
            hiloDec.join();
        } catch (InterruptedException ex) {
        }
        System.out.println("\nValor FINAL: " + contador.value);
    }

    // HILO QUE INCREMENTA
    class Incrementa extends Thread {

        @Override
        public void run() {
            for (int n = 0; n < N_OPERACIONES; n++) {
                synchronized (contador) {
                    contador.value++;
                }
            }
        }
    }

    // HILO QUE DECREMENTA
    class Decrementa extends Thread {

        @Override
        public void run() {
            for (int n = 0; n < N_OPERACIONES; n++) {
                synchronized (contador) {
                    contador.value--;
                }
            }
        }
    }

}
