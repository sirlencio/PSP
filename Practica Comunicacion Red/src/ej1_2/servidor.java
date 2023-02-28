package ej1_2;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;

public class servidor {

    static final int PUERTO_SERVIDOR = 8000;
    static long n1, n2;
    static char operador;
    static int noperacion = 1;

    public static void main(String[] args) {
        while (operador != 'A') {
            try (DatagramSocket socket = new DatagramSocket(PUERTO_SERVIDOR)) {

                do {
                    DatagramPacket recibido = recibirPaquete(socket);
                    System.out.println("Cliente: " + operador);

                    if (operador == '+' || operador == '-' || operador == '*' || operador == '/') {
                        System.out.println("Cliente: " + n1);
                        System.out.println("Cliente: " + n2);

                        long resultado = calcular(operador, n1, n2);

                        String cadena = n1 + " " + operador + " " + n2 + " = " + resultado;

                        enviarPaquete(socket, noperacion, resultado, cadena, recibido);

                        System.out.println("Enviado: " + cadena);

                        noperacion++;
                    }
                } while (operador != 'F' && operador != 'A');

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void enviarPaquete(DatagramSocket socket, int noperacion, long rdo, String cadena, DatagramPacket recibido) throws IOException {
        byte[] buffer = new byte[2048];

        // Se convierten los datos recibidos a los tipos de datos correspondientes
        ByteArrayOutputStream bais = new ByteArrayOutputStream(buffer.length);
        DataOutputStream dis = new DataOutputStream(bais);
        dis.writeInt(noperacion);
        dis.writeLong(rdo);
        dis.writeUTF(cadena);

        //Enviaremos la respuesta a la direccion y el puerto del paquete recibido
        DatagramPacket paquete = new DatagramPacket(bais.toByteArray(), bais.size(), recibido.getAddress(), recibido.getPort());
        socket.send(paquete);
    }

    private static DatagramPacket recibirPaquete(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[18]; //18 porque el char ocupa 2 bytes y cada long ocupa 8 bytes
        DatagramPacket paqueteRecibido = new DatagramPacket(buffer, buffer.length);
        socket.receive(paqueteRecibido);

        ByteBuffer byteBuffer = ByteBuffer.wrap(paqueteRecibido.getData());
        operador = byteBuffer.getChar();
        n1 = byteBuffer.getLong();
        n2 = byteBuffer.getLong();
        return paqueteRecibido;
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

}