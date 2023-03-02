package ej5;

import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {

    static final String IP = "127.0.0.1";
    static final int PUERTO = 8000;
    static Scanner scan = new Scanner(System.in);
    static DataInputStream reader;
    static DataOutputStream writer;

    public static void main(String[] args) {
        try (Socket socket = new Socket(IP, PUERTO)) {
            System.out.println("- Bienvenido a adivina el numero -");

            //Inicia reader y writer
            reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            String respuesta;
            System.out.print("1.- Facil (1 - 10)\n2.- Normal (1 - 50)\n3.- Dificil (1 - 100)\n");
            int dificultad;
            do {
                System.out.print("Seleccione la dificultad: ");
                dificultad = scan.nextInt();
            } while (dificultad != 1 && dificultad != 2 && dificultad != 3);

            writer.writeInt(dificultad);
            writer.flush();

            System.out.println("Ha escogido la dificultad " + dificultad);
            do {
                System.out.print("Introduzca un numero: ");
                int guess = scan.nextInt();

                writer.writeInt(guess);
                writer.flush();

                System.out.println(reader.readUTF());
                respuesta = reader.readUTF();
                System.out.println(respuesta);

            } while (!respuesta.equals("Has perdido") && !respuesta.equals("Has ganado!"));

            socket.close();
            // Liberar recursos
            reader.close();
            writer.close();
            scan.close();
        } catch (IOException e) {
            System.out.println("No se pudo conectar con el servidor.");
        }
    }

}
