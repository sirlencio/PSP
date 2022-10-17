package Ej3;

import java.util.Scanner;

public class Cadenas {
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.println("Cuantas cadenas quieres generar: ");
        int ncad = input.nextInt();
        for (int j = 0; j < ncad; j++) {
            int rango = (int) (Math.random() * 20)+1;
            System.out.println(genera(rango));
        }
    }

    public static StringBuffer genera(int rango) {
        String base = "qwertyuiopasdfghjklñzxcvbnm";
        StringBuffer palabra = new StringBuffer();

        for (int i = 0; i < rango; i++) {
            int selector = (int) (Math.random() * 27);
            palabra.append(base.charAt(selector));
        }
        return palabra;
    }
}
