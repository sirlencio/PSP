package ej4;

import java.util.ArrayList;
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
                for (int i = 0; i < letras.length; i++) {
                    char c1 = letras[i];
                    for (int j = 0; j < letras.length; j++) {
                        char c2 = letras[j];
                        for (int k = 0; k < letras.length; k++) {
                            char c3 = letras[k];

                            if (sistema.claveAcertada) {
                                s.release();
                                return;
                            }
                            String prueba = "" + inicial + c1 + c2 + c3;

                            if (sistema.checkClave(prueba)) {
                                sistema.claveAcertada = true;
                                s.release();
                                return;
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
        String password = "";
        char[] letras = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

        for (int i = 0; i < 4; i++) {
            char c = letras[aleatorio.nextInt(letras.length)];
            password += c;
        }
        pwd = password;
    }

    public synchronized boolean checkClave(String prueba) {
        nIntentos++;
        if (prueba.equals(pwd)) {
            System.out.println(nIntentos + " - H-" + prueba.charAt(0) + ", prueba " + prueba + " - Acierto");
            return true;
        } else if (!claveAcertada){
            System.out.println(nIntentos + " - H-" + prueba.charAt(0) + ", prueba " + prueba + " - Fallo");
        }
        return false;
    }
}
