package ej1_3;

import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {

    // Servidor y puerto
    static final String IP = "127.0.0.1";
    static final int PUERTO = 8000;
    static Scanner scan = new Scanner(System.in);

    // Reader y writer
    static DataInputStream reader;
    static DataOutputStream writer;

    public static void main(String[] args) {
        // Crear conexion
        try (Socket socket = new Socket(IP, PUERTO)) {
            System.out.println("- Cliente de operaciones matematicas -");

            // Obtener reader y writer
            obtenerStreams(socket);

            // Realizar operaciones
            boolean operar = true;
            while (operar) {
                operar = obtenerOperador(socket);
            }

            // Liberar recursos
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
        // Pedir operador
        System.out.print("Seleccione operador o comando (+, -, *, /, F, A): ");
        char operador = scan.nextLine().toUpperCase().charAt(0);
        System.out.println();

        if (compruebaOperador(operador)) {
            // Enviar operador
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
                // Cerrar socket
                socket.close();

                // Detener ejecucion
                return false;
            }
            case 'F' -> {    // Detiene solo el cliente, el servidor seguira escuchando
                // Cerrar socket
                socket.close();

                // Detener ejecucion
                return false;
            }
            default -> {    // Operar
                enviarNumeros();

                resultados();

                // Realizar otra operacion
                return true;
            }
        }
    }

    /*
     * Comprueba que el operador sea un signo valido
     */
    private static boolean compruebaOperador(char operador) {
        return operador == 'A' || operador == 'F' || operador == '+' || operador == '-' || operador == '*' || operador == '/';
    }

    /*
     * Obtiene los resultados que envía el servidor y los imprime por pantalla
     */
    private static void resultados() throws IOException {
        // Número de operación
        String noperacion = "Nº Operacion: ";
        noperacion += reader.readInt();

        // Resultado
        String total = "Resultado: ";
        total += reader.readLong();

        // Operación completa
        String rdo = "Operacion realizada: ";
        rdo += reader.readUTF();

        // Mostrar resultados
        System.out.println("===Respuesta del servidor===");
        System.out.println(noperacion);
        System.out.println(total);
        System.out.println(rdo + "\n");
    }

    /*
     * Pedir numeros por teclado y enviarlos al servidor.
     */
    private static void enviarNumeros() throws IOException {
        // Pedir numeros
        long n1 = 0, n2 = 0;
        boolean seguir = true;

        do {
            try {
                System.out.print("Numero 1: ");
                n1 = scan.nextLong();
                seguir = false;
            } catch (InputMismatchException e) {
                System.out.println("El numero introducido no tiene un formato correcto.");
            }
            scan.nextLine();
        } while (seguir);

        seguir = true;
        do {
            try {
                System.out.print("Numero 2: ");
                n2 = scan.nextLong();
                seguir = false;
            } catch (InputMismatchException e) {
                System.out.println("El numero introducido no tiene un formato correcto.");
            }
            scan.nextLine();
        } while (seguir);

        System.out.println();

        // Enviar numeros
        writer.writeLong(n1);
        writer.writeLong(n2);
        writer.flush();
    }

    /*
     * Obtener reader y writer.
     */
    private static void obtenerStreams(Socket socket) throws IOException {
        reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

}
