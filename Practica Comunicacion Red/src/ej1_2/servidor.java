package ej1_2;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class servidor {

    static final String IP = "127.0.0.1";
    static final int PUERTO_SERVIDOR = 8000;
    static final int PUERTO_CLIENTE = 8001;
    static int noperacion = 1;

    public static void main(String[] args) {
        char operador = ' ';
        while (operador != 'A') {
            try (DatagramSocket socket = new DatagramSocket(PUERTO_SERVIDOR)) {
                InetAddress ip = InetAddress.getByName(IP);
                socket.connect(ip, PUERTO_CLIENTE);

                do {
                    operador = operar(socket);
                } while (operador != 'F' && operador != 'A');

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * Recibe el operador, los numeros y devuelve al cliente
     * el resultado de esa operacion
     */
    private static char operar(DatagramSocket socket) throws IOException {
        char operador = recibirChar(socket);
        System.out.println("Cliente: " + operador);

        if (operador == '+' || operador == '-' || operador == '*' || operador == '/') {
            long n1 = recibirLong(socket);
            long n2 = recibirLong(socket);

            System.out.println("Cliente: " + n1);
            System.out.println("Cliente: " + n2);

            long resultado = calcular(operador, n1, n2);

            String cadena = n1 + " " + operador + " " + n2 + " = " + resultado;

            enviarInt(socket, noperacion);
            enviarLong(socket, resultado);
            enviarString(socket, cadena);

            System.out.println("Enviado: " + cadena);

            noperacion++;
        }
        return operador;
    }

    /*
     * Realiza una operacion con los numeros dependiendo del operador.
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
     * Recibe un char por UDP.
     */
    private static char recibirChar(DatagramSocket socket) throws IOException {
        DatagramPacket paquete = new DatagramPacket(new byte[Character.BYTES], Character.BYTES);
        socket.receive(paquete);
        return ByteBuffer.wrap(paquete.getData()).getChar();
    }

    /*
     * Recibe un long por UDP.
     */
    private static long recibirLong(DatagramSocket socket) throws IOException {
        DatagramPacket paquete = new DatagramPacket(new byte[Long.BYTES], Long.BYTES);
        socket.receive(paquete);
        return ByteBuffer.wrap(paquete.getData()).getLong();
    }

    /*
     * Envia un long por UDP.
     */
    private static void enviarLong(DatagramSocket socket, long numero) throws IOException {
        byte[] numeroBytes = ByteBuffer.allocate(Long.BYTES).putLong(numero).array();
        DatagramPacket paquete = new DatagramPacket(numeroBytes, numeroBytes.length);
        socket.send(paquete);
    }

    /*
     * Envia un int por UDP.
     */
    private static void enviarInt(DatagramSocket socket, int numero) throws IOException {
        byte[] numeroBytes = ByteBuffer.allocate(Integer.BYTES).putInt(numero).array();
        DatagramPacket paquete = new DatagramPacket(numeroBytes, numeroBytes.length);
        socket.send(paquete);
    }

    /*
     * Envia un String por UDP.
     */
    private static void enviarString(DatagramSocket socket, String cadena) throws IOException {
        byte[] cadenaBytes = cadena.getBytes();
        DatagramPacket paquete = new DatagramPacket(cadenaBytes, cadenaBytes.length);
        socket.send(paquete);
    }

}