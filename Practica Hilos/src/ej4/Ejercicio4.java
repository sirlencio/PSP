package ej4;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Ejercicio4 {
    public static void main(String[] args) {
        new Ejercicio4().runProgram();
    }

    public void runProgram() {
        SistemaDeRed sistema = new SistemaDeRed();
        Semaphore s = new Semaphore(4);
        char[] letras = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        for (char letra : letras) {
            Hilo thread = new Hilo(letra, sistema, s);
            thread.start();

        }
    }
}

class Hilo extends Thread {
    private char inicial;
    private SistemaDeRed sistema;
    private Semaphore s;

    public Hilo(char ch, SistemaDeRed sistema, Semaphore s) {
        inicial = ch;
        this.sistema = sistema;
        this.s = s;
    }

    public void run() {
        try {
            s.acquire();
            if (!sistema.claveAcertada) {
                char[] letras = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
                for (char c1 : letras) {
                    for (char c2 : letras) {
                        for (char c3 : letras) {
                            String prueba = String.valueOf(inicial) + c1 + c2 + c3;

                            if (sistema.checkClave(prueba)) {
                                sistema.claveAcertada = true;
                                s.release();
                                return;
                            } else if (sistema.claveAcertada) {
                                break;
                            }
                        }
                    }
                }
            }
            s.release();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}

class SistemaDeRed {

    private String pwd;
    public int nIntentos = 0;
    public boolean claveAcertada;

    public SistemaDeRed() {
        Random aleatorio = new Random();
        String pwd = "";
        char[] letras = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        for (int i = 0; i < 4; i++) {
            char c = letras[aleatorio.nextInt(letras.length)];
            pwd += c;
        }
        this.pwd = pwd;
    }

    public synchronized boolean checkClave(String prueba) {
        nIntentos++;
        if (prueba.equals(pwd)) {
            System.out.println(nIntentos + " - H-" + prueba.charAt(0) + ", prueba " + prueba + " - Acierto");
            return true;
        } else if (!claveAcertada) {
            System.out.println(nIntentos + " - H-" + prueba.charAt(0) + ", prueba " + prueba + " - Fallo");
        }
        return false;
    }
}
