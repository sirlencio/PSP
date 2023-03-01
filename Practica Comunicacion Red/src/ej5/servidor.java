package ej5;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class servidor {

    static final int PUERTO = 8000;
    static int noperaciones = 1;
    static DataInputStream reader;
    static DataOutputStream writer;

    public static void main(String[] args) {
        try {
            char operador = ' ';
            while (operador != 'A') { // Siempre que el cliente no devuelva la letra 'A', el servidor seguirá en ejecución
                try (ServerSocket socketServidor = new ServerSocket(PUERTO)) { //El servidor atiende al puerto asignado
                    Socket socketCliente = socketServidor.accept(); //El servidor acepta la conexion con el cliente
                    reader = new DataInputStream(new BufferedInputStream(socketCliente.getInputStream()));
                    writer = new DataOutputStream(new BufferedOutputStream(socketCliente.getOutputStream()));

                    do {
                        operador = operar();
                    } while (operador != 'F' && operador != 'A');

                    socketCliente.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Recibe el operador, los numeros y devuelve al cliente
     * el resultado de esa operacion
     */
    private static char operar() throws IOException {
        char operador = reader.readChar();
        System.out.println("Cliente: " + operador);

        if (operador == '+' || operador == '-' || operador == '*' || operador == '/') {
            long n1 = reader.readLong();
            long n2 = reader.readLong();

            System.out.println("Cliente: " + n1);
            System.out.println("Cliente: " + n2);

            long rdo = calcular(operador, n1, n2);

            String cadena = n1 + "" + operador + "" + n2 + " = " + rdo;

            writer.writeInt(noperaciones);
            writer.writeLong(rdo);
            writer.writeUTF(cadena);
            writer.flush();

            System.out.println("Enviado: " + cadena);

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

}
