package ej1_2;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class cliente {

    static final String IP = "127.0.0.1";
    static final int PUERTO_SERVIDOR = 8000;
    static char operador;
    static long n1 = 0, n2 = 0;
    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        try (DatagramSocket socket = new DatagramSocket()) {
            System.out.println("- Cliente de operaciones matematicas -");

            do {
                System.out.print("Seleccione operador o comando (+, -, *, /, F, A): ");
                operador = scan.nextLine().toUpperCase().charAt(0);
                System.out.println();

                if (operador == '+' || operador == '-' || operador == '*' || operador == '/') {
                    guardarNumeros();
                    enviarPaquete(socket);
                    recibirPaquete(socket);
                } else if (operador == 'A') {
                    enviarPaquete(socket);
                    socket.close();
                    System.out.println("** Servidor detenido **\n");
                    break;
                } else {
                    enviarPaquete(socket);
                    socket.close();
                    System.out.println("** Finalizada la conexion **\n");
                    break;
                }
            } while (true);
            scan.close();
        } catch (IOException e) {
            System.out.println("No se ha podido establecer la conexion.");
        }

    }

    private static void enviarPaquete(DatagramSocket socket) throws IOException {
        InetAddress ip = InetAddress.getByName(IP);
        ByteBuffer buffer = ByteBuffer.allocate(18);
        buffer.putChar(operador);
        buffer.putLong(n1);
        buffer.putLong(n2);
        byte[] datos = buffer.array();

        //Vamos a enviar el paquete al servidor a traves de su puerto
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, ip, PUERTO_SERVIDOR);
        socket.send(paquete);
    }

    private static void recibirPaquete(DatagramSocket socket) throws IOException {
        byte[] buffer = new byte[2048];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

        socket.receive(packet);

        // Se convierten los datos recibidos a los tipos de datos correspondientes
        ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
        DataInputStream dis = new DataInputStream(bais);
        int noperacion = dis.readInt();
        long rdo = dis.readLong();
        String cadena = dis.readUTF();

        System.out.println("N.operacion: " + noperacion);
        System.out.println("Resultado: " + rdo);
        System.out.println(cadena);
    }

    private static void guardarNumeros() {
        System.out.print("Numero 1: ");
        n1 = scan.nextLong();
        scan.nextLine();

        System.out.print("Numero 2: ");
        n2 = scan.nextLong();
        scan.nextLine();

        System.out.println();
    }

}
