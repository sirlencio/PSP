package ej1_2;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.*;

public class cliente {

    // Servidor y puerto
    static final String IP = "127.0.0.1";
    static final int PUERTO_SERVIDOR = 8000;
    static final int PUERTO_CLIENTE = 8001;

    // Tamanio maximo de datagrama
    private static final int SIZE = 64;

    static Scanner scan = new Scanner(System.in);

    public static void main(String[] args) {
        // Crear conexion
        try (DatagramSocket socket = new DatagramSocket(PUERTO_CLIENTE)) {
            System.out.println("- Cliente de operaciones matematicas -");

            // Conectar al servidor
            InetAddress ip = InetAddress.getByName(IP);
            socket.connect(ip, PUERTO_SERVIDOR);

            // Realizar operaciones
            boolean operar = true;
            while (operar) {
                operar = obtenerOperador(socket);
            }

            // Liberar recursos
            scan.close();
        } catch (IOException e) {
            System.out.println("No se ha podido establecer la conexion.");
        }

    }

    /*
     * Comprueba que el operador obtenido por teclado sea permitido
     */
    private static boolean obtenerOperador(DatagramSocket socket) throws IOException {
        // Pedir operador
        System.out.print("Seleccione operador o comando (+, -, *, /, F, A): ");
        char operador = scan.nextLine().toUpperCase().charAt(0);
        System.out.println();

        if (compruebaOperador(operador)) {
            // Enviar operador
            enviarChar(socket, operador);
            return realizarOperacion(socket, operador);
        } else {
            System.out.println("El operador no es correcto \n");
            return true;
        }
    }

    /*
     * Dependiendo del operador ingresado por teclado, el programa continuará enviando los numeros,
     * deteniendo la ejecucion del servidor, o deteniendo la ejecucion del cliente
     */
    private static boolean realizarOperacion(DatagramSocket socket, char operador) throws IOException {
        switch (operador) {
            case 'A' -> {   //Detiene el servidor
                System.out.println("** Servidor detenido **\n");

                // Cerrar socket
                socket.close();

                // Detener ejecucion
                return false;
            }
            case 'F' -> { // Detiene solo el cliente, el servidor seguira escuchando
                // Cerrar socket
                socket.close();

                System.out.println("** Finalizada la conexion **\n");

                // Detener ejecucion
                return false;
            }
            default -> {    // Operar
                enviarNumeros(socket);

                resultados(socket);

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
    private static void resultados(DatagramSocket socket) throws IOException {
        // Número de operación
        String noperacion = "Nº Operacion: ";
        noperacion += recibirInt(socket);

        // Resultado
        String total = "Resultado: ";
        total += recibirLong(socket);

        // Operación completa
        String rdo = "Operacion realizada: ";
        rdo += recibirString(socket);

        // Mostrar resultados
        System.out.println("===Respuesta del servidor===");
        System.out.println(noperacion);
        System.out.println(total);
        System.out.println(rdo + "\n");
    }

    /*
     * Recibe una cadena de caracteres por UDP.
     */
    private static String recibirString(DatagramSocket socket) throws IOException {
        DatagramPacket paqueteOperacion = new DatagramPacket(new byte[SIZE], SIZE);
        socket.receive(paqueteOperacion);
        return new String(paqueteOperacion.getData(), 0, paqueteOperacion.getLength());
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
     * Recibe un integer por UDP.
     */
    private static int recibirInt(DatagramSocket socket) throws IOException {
        DatagramPacket paquete = new DatagramPacket(new byte[Integer.BYTES], Integer.BYTES);
        socket.receive(paquete);
        return ByteBuffer.wrap(paquete.getData()).getInt();
    }

    /*
     * Pedir numeros por teclado y enviarlos al servidor.
     */
    private static void enviarNumeros(DatagramSocket socket) throws IOException {
        // Pedir numeros
        long n1 = 0;
        long n2 = 0;

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
        enviarLong(socket, n1);
        enviarLong(socket, n2);
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
     * Envia un caracter por UDP.
     */
    private static void enviarChar(DatagramSocket socket, char caracter) throws IOException {
        byte[] caracterBytes = ByteBuffer.allocate(Character.BYTES).putChar(caracter).array();
        DatagramPacket paquete = new DatagramPacket(caracterBytes, caracterBytes.length);
        socket.send(paquete);
    }
}
