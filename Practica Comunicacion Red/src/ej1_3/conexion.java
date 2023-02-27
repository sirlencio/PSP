package ej1_3;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class conexion extends Thread {

    DataInputStream reader;
    DataOutputStream writer;
    private Socket socket;
    private static Semaphore semaforo = new Semaphore(1);

    public conexion(Socket socketCliente) {
        this.socket = socketCliente;
    }

    @Override
    public void run() {
        char operador = ' ';
        try {
            reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            do {
                operador = operar();
            } while (operador != 'F' && operador != 'A');

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (operador == 'A') {
            System.exit(0);
        }
    }

    /*
     * Recibe el operador, los numeros y devuelve al cliente
     * el resultado de esa operacion
     */
    private char operar() throws IOException {
        char operador = reader.readChar();
        System.out.println("Cliente: " + operador);

        if (operador == '+' || operador == '-' || operador == '*' || operador == '/') {
            long n1 = reader.readLong();
            long n2 = reader.readLong();

            System.out.println("Cliente: " + n1);
            System.out.println("Cliente: " + n2);

            long resultado = calcularResultado(operador, n1, n2);

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

}
