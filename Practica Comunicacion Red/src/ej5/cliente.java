package ej5;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class cliente {

    static final String IP = "127.0.0.1";
    static final int PUERTO = 8000;
    static Scanner scan = new Scanner(System.in);
    static DataInputStream reader;
    static DataOutputStream writer;
    static String username;

    public static void main(String[] args) {
        try (Socket socket = new Socket(IP, PUERTO)) {
            System.out.println("- Cliente de operaciones matematicas -");

            reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            System.out.print("Ingrese nombre de usuario: ");
            username = scan.nextLine();
            System.out.println();

            while (true) {
                System.out.print("Ingrese el mensaje: ");
                String msg = scan.nextLine();
                System.out.println();
                if (!msg.equals("adios")) {
                    writer.writeUTF(username);
                    writer.writeUTF(msg);
                    writer.flush();
                    recibirMensajes();
                } else {
                    writer.writeUTF("adios");
                    writer.writeUTF(msg);
                    writer.flush();
                    break;
                }
            }

            reader.close();
            writer.close();
            scan.close();

            System.out.println("** Finalizada la conexion **");
        } catch (IOException e) {
            System.out.println("No se ha podido establecer la conexion.");
        }
    }

    /*
     * Obtiene los resultados que envía el servidor y los imprime por pantalla
     */
    private static void recibirMensajes() throws IOException {

        String msg = reader.readUTF();

        System.out.println("===Respuesta del servidor===");
        System.out.println(msg + "\n");
    }


}
