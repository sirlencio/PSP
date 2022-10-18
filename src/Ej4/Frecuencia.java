package Ej4;

import java.util.Scanner;

public class Frecuencia {
    public static void main(String[] args) {
        int a = 0, e = 0, i = 0, o = 0, u = 0;
        Scanner input = new Scanner(System.in);
        StringBuilder palabra = new StringBuilder();
        while (input.hasNext()){
            palabra.append(input.nextLine());
        }
        for (int j = 0; j < palabra.length(); j++) {
            if (palabra.charAt(j) == 'a') {
                a++;
            }
            if (palabra.charAt(j) == 'e') {
                e++;
            }
            if (palabra.charAt(j) == 'i') {
                i++;
            }
            if (palabra.charAt(j) == 'o') {
                o++;
            }
            if (palabra.charAt(j) == 'u') {
                u++;
            }
        }
        System.out.println("a = " + a);
        System.out.println("e = " + e);
        System.out.println("i = " + i);
        System.out.println("o = " + o);
        System.out.println("u = " + u);
    }
}