package ej1_3;

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
            System.out.println("- Cliente de operaciones matematicas -");

            reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            
            boolean operar = true;
            while (operar) {
                operar = obtenerOperador(socket);
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
     * Comprueba que el operador obtenido por teclado sea permitido
     */
    private static boolean obtenerOperador(Socket socket) throws IOException {
        System.out.print("Seleccione operador o comando (+, -, *, /, F, A): ");
        char operador = scan.nextLine().toUpperCase().charAt(0);
        System.out.println();

        if (operador == 'A' || operador == 'F' || operador == '+' || operador == '-' || operador == '*' || operador == '/') {
            writer.writeChar(operador);
            writer.flush();
            return realizarOperacion(socket, operador);
        } else {
            System.out.println("El operador no es correcto.");
            System.out.println();
            return true;
        }
    }

    /*
     * Dependiendo del operador ingresado por teclado, el programa continuará enviando los numeros,
     * deteniendo la ejecucion del servidor, o deteniendo la ejecucion del cliente
     */
    private static boolean realizarOperacion(Socket socket, char operador) throws IOException {
        switch (operador) {
            case 'A' -> {    // Detiene el servidor
                System.out.println("** Servidor detenido **\n");
                socket.close();

                return false;
            }
            case 'F' -> {    // Detiene solo el cliente, el servidor seguira escuchando
                socket.close();

                return false;
            }
            default -> {    // Operar
                enviarNumeros();

                resultados();

                return true;
            }
        }
    }

    /*
     * Obtiene los resultados que envía el servidor y los imprime por pantalla
     */
    private static void resultados() throws IOException {
        String noperacion = "Nº Operacion: " + reader.readInt();

        String total = "Resultado: " + reader.readLong();

        String rdo = "Operacion realizada: " + reader.readUTF();

        System.out.println("===Respuesta del servidor===");
        System.out.println(noperacion);
        System.out.println(total);
        System.out.println(rdo + "\n");
    }

    /*
     * Pedir numeros por teclado y enviarlos al servidor.
     */
    private static void enviarNumeros() throws IOException {
        System.out.print("Numero 1: ");
        long n1 = scan.nextLong();
        scan.nextLine();

        System.out.print("Numero 2: ");
        long n2 = scan.nextLong();
        scan.nextLine();

        System.out.println();

        writer.writeLong(n1);
        writer.writeLong(n2);
        writer.flush();
    }


}
