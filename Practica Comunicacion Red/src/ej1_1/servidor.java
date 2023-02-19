package ej1_1;

import java.io.*;
import java.net.*;

public class servidor {

    // Puerto al que conectarse
    static final int PUERTO = 8000;

    // Contador de operaciones
    static int noperaciones = 1;

    // Reader y writer en datos primitivos
    static DataInputStream reader;
    static DataOutputStream writer;

    public static void main(String[] args) {
        try {
            char operador = ' ';
            while (operador != 'A') { // Siempre que el cliente no devuelva la letra 'A', el servidor seguirá en ejecución
                operador = conectarCliente(operador);
            }
            // Liberar recursos
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Realiza una conexion con un cliente, asigna el lector y escritor
     * a ese cliente y procede a hacer las operaciones
     */
    private static char conectarCliente(char operador) {
        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) { //El servidor atiende al puerto asignado
            Socket socketCliente = socketServidor.accept(); //El servidor acepta la conexion con el cliente
            obtenerStreams(socketCliente);

            // Operar con el cliente
            do {
                operador = operar();
            } while (operador != 'F' && operador != 'A');

            // Liberar recursos
            socketCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return operador;
    }

    /*
     * Recibe el operador, los numeros y devuelve al cliente
     * el resultado de esa operacion
     */
    private static char operar() throws IOException {
        // Obtener operacion
        char operador = reader.readChar(); //Lee el signo que envia el cliente
        System.out.println("Cliente: " + operador);

        if (comprobarSigno(operador)) {
            // Leer numeros
            long n1 = reader.readLong();
            long n2 = reader.readLong();

            System.out.println("Cliente: " + n1);
            System.out.println("Cliente: " + n2);

            // Calcular operacion
            long rdo = calcular(operador, n1, n2);

            // Crear cadena de la operacion
            String cadena = n1 + "" + operador + "" + n2 + " = " + rdo;

            // Enviar resultados al cliente
            writer.writeInt(noperaciones);
            writer.writeLong(rdo);
            writer.writeUTF(cadena);
            writer.flush();

            System.out.println("Enviado: " + cadena);

            // Aumentar contador
            noperaciones++;
        }
        return operador;
    }

    /*
     * Realiza una operacion con un signo
     * y dos numeros que hemos ingresado por parametro
     */
    private static long calcular(char operador, long n1, long n2) {
        return switch (operador) {
            case '+' -> n1 + n2;
            case '-' -> n1 - n2;
            case '*' -> n1 * n2;
            case '/' -> n1 / n2;
            default -> 0;
        };
    }

    /*
     * Comprobar que el operador sea un signo válido
     */
    private static boolean comprobarSigno(char operador) {
        return operador == '+' || operador == '-' || operador == '*' || operador == '/';
    }

    /*
     * Asignar reader y writer
     */
    private static void obtenerStreams(Socket socket) throws IOException {
        reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

}
