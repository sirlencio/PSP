package ej1_1;

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
            System.out.println("- Cliente de operaciones matemáticas -");

            //Inicia reader y writer
            reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            boolean operar = true;
            while (operar) {
                operar = obtenerOperador(socket);
            }

            // Liberar recursos
            reader.close();
            writer.close();
            scan.close();
        } catch (IOException e) {
            System.out.println("No se ha podido establecer la conexión.");
        }
    }

    /*
     * Comprueba que el operador obtenido por teclado sea permitido y realiza su respectiva operacion
     */
    private static boolean obtenerOperador(Socket socket) throws IOException {
        System.out.print("Seleccione operador o comando (+,-,*,/, F, A): ");
        char operador = scan.nextLine().toUpperCase().charAt(0);
        System.out.println();

        if (operador == 'A' || operador == 'F' || operador == '+' || operador == '-' || operador == '*' || operador == '/') {
            writer.writeChar(operador);
            writer.flush();
            return realizarOperacion(socket, operador);
        } else {
            System.out.println("El operador no es correcto. \n");
            return true;
        }
    }

    /*
     * Dependiendo del operador ingresado por teclado, el programa continuará enviando los numeros,
     * deteniendo la ejecucion del servidor, o deteniendo la ejecucion del cliente
     */
    private static boolean realizarOperacion(Socket socket, char operador) throws IOException {
        switch (operador) {
            case 'A' -> {   //Detiene el servidor
                System.out.println("Servidor detenido\n");

                socket.close();

                return false;
            }
            case 'F' -> {   // Detiene solo el cliente, el servidor seguira escuchando
                socket.close();

                System.out.println("Finalizada la conexión\n");

                return false;
            }
            default -> {    //  Operar
                enviarNumeros();

                System.out.println("===Respuesta del servidor===");
                System.out.println("Nº Operacion: " + reader.readInt());
                System.out.println("Resultado: " + reader.readLong());
                System.out.println("Operacion realizada: " + reader.readUTF() + "\n");

                return true;
            }
        }
    }

    /*
     * Pedir números por teclado y enviarlos al servidor.
     */
    private static void enviarNumeros() throws IOException {
        System.out.print("Número 1: ");
        long n1 = scan.nextLong();
        scan.nextLine();

        System.out.print("Número 2: ");
        long n2 = scan.nextLong();
        scan.nextLine();

        System.out.println();

        // Enviar números
        writer.writeLong(n1);
        writer.writeLong(n2);
        writer.flush();
    }

}
