package ej1_3;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class conexion extends Thread {

    // Reader y writer
    DataInputStream reader;
    DataOutputStream writer;

    // Socket
    private Socket socket;

    // Semaforo para controlar el acceso al contador
    private static Semaphore semaforo = new Semaphore(1);

    public conexion(Socket socketCliente) {
        this.socket = socketCliente;
    }

    @Override
    public void run() {
        char operador = ' ';
        try {
            // Obtener streams
            obtenerStreams(socket);

            // Operar con el cliente
            do {
                operador = operar();
            } while (operador != 'F' && operador != 'A');

            // Liberar recursos
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Apagar servidor si procede
        if (operador == 'A') {
            System.exit(0);
        }
    }

    /*
     * Recibe el operador, los numeros y devuelve al cliente
     * el resultado de esa operacion
     */
    private char operar() throws IOException {
        // Obtener operador
        char operador = reader.readChar();
        System.out.println("Cliente: " + operador);

        // Procesar operador
        if (comprobarSigno(operador)) {
            // Leer numeros
            long n1 = reader.readLong();
            long n2 = reader.readLong();

            System.out.println("Cliente: " + n1);
            System.out.println("Cliente: " + n2);

            // Procesar numeros
            long resultado = calcularResultado(operador, n1, n2);

            // Crear cadena de la operacion
            String cadena = n1 + " " + operador + " " + n2 + " = " + resultado;

            // Enviar resultados
            try {
                semaforo.acquire();
                writer.writeInt(servidor.contador);
                semaforo.release();
            } catch (InterruptedException ignored) {
            }
            writer.writeLong(resultado);
            writer.writeUTF(cadena);
            writer.flush();

            System.out.println("Enviado: " + cadena);

            // Aumentar contador
            try {
                semaforo.acquire();
                servidor.contador++;
                semaforo.release();
            } catch (InterruptedException ignored) {
            }
        }
        return operador;
    }

    /*
     * Realiza una operacion con los numeros dependiendo del operador.
     */
    private static long calcularResultado(char operador, long n1, long n2) {
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
    private static boolean comprobarSigno(char operacion) {
        return operacion == '+' || operacion == '-' || operacion == '*' || operacion == '/';
    }

    /*
     * Obtener reader y writer.
     */
    private void obtenerStreams(Socket socket) throws IOException {
        reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

}
