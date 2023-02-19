package ej1;

public class Monitor {

    final int noperaciones = 3000;

    final int vinicial = 99;

    /**
     * Variable compartida por los hilos sin control de exclusión
     * <p>
     * Se crea clase pues synchronized pide un objeto para realizar monitor
     */
    class Contador {
        public int value = vinicial;
    }

    final Contador contador = new Contador();

    public static void main(String[] args) {
        new Monitor().runProgram();
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
            for (int n = 0; n < noperaciones; n++) {
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
            for (int n = 0; n < noperaciones; n++) {
                synchronized (contador) {
                    contador.value--;
                }
            }
        }
    }

}
