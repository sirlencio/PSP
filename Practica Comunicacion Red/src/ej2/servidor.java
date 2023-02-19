package ej2;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.*;

public class servidor {

    // Mensajes
    private static final byte[] FICH_RENOMBRADO = new String("Fichero renombrado con exito.").getBytes();
    private static final byte[] FICH_BORRADO = new String("Fichero borrado con exito.").getBytes();
    private static final byte[] ERR_1 = new String("El fichero no existe.").getBytes();
    private static final byte[] ERR_2 = new String("El nuevo nombre no es correcto.").getBytes();
    private static final byte[] ERR_3 = new String("Error desconocido.").getBytes();

    // Puerto al que conectarse
    static final int PUERTO = 8000;

    // Reader y writer
    static DataInputStream reader;
    static DataOutputStream writer;

    // Datos a enviar
    static byte resultado;
    static long longitud;
    static byte[] informacion;

    public static void main(String[] args) {
        try {
            for (; ; ) {
                conectarCliente();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Realiza una conexion con un cliente, asigna el lector y escritor
     * a ese cliente y procede a hacer las operaciones
     */
    private static void conectarCliente() throws IOException {
        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
            Socket socketCliente = socketServidor.accept();
            obtenerStreams(socketCliente);

            // Operar con el cliente
            char operador = ' ';
            do {
                operador = operar();
            } while (operador != 'F');

            // Liberar recursos
            reader.close();
            writer.close();
            socketCliente.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Recibe el operador, los numeros y devuelve al cliente
     * el resultado de esa operacion
     */
    private static char operar() throws IOException {
        // Obtener operacion
        char operador = reader.readChar();
        System.out.println("Cliente: " + operador);

        // Realizar operacion
        if (operador == 'G' || operador == 'D' || operador == 'R') {
            comprobarYOperarConFichero(operador);
        } else if (operador == 'L') {
            listarFicheros();
        }

        // Enviar resultados
        if (operador != 'F') {
            enviarResultados();
        }

        return operador;
    }

    /*
     * Envia los resultados grabados actualmente.
     */
    private static void enviarResultados() throws IOException {
        writer.writeByte(resultado);
        writer.writeLong(longitud);
        writer.write(informacion);
        writer.flush();

        System.out.println("Enviado: " + resultado);
    }

    /*
     * Recibe un nombre de archivo, comprueba que existe y opera con el.
     */
    private static void comprobarYOperarConFichero(char operacion) throws IOException {
        // Leer nombre del archivo
        String nombre = reader.readUTF();
        System.out.println("Cliente: " + nombre);

        // Fichero
        Path ubicacion = Paths.get(nombre);
        File fichero = ubicacion.toFile();

        try {
            // Comprobar que el fichero existe
            if (!fichero.exists()) {
                colocarDatos((byte) 1, ERR_1);
            } else {
                // Realizar la operacion
                operarConFichero(fichero, operacion);
            }
        } catch (IOException e) {
            // Relanzar
            throw e;
        } catch (Exception e) {
            // Si se produce alguna excepcion
            colocarDatos((byte) 3, ERR_3);
        }
    }

    /*
     * Coloca en los datos a enviar los nombres de todos los ficheros.
     */
    private static void listarFicheros() {
        // Obtener ficheros y carpetas
        File carpeta = new File(".");
        File[] todo = carpeta.listFiles();

        // Filtrar ficheros y obtener nombres
        String ficheros = "";
        for (File fichero : todo) {
            if (fichero.isFile() || fichero.isDirectory()) {
                ficheros += fichero.getName() + "\n";
            }
        }

        // Colocar datos del resultado
        colocarDatos((byte) 0, ficheros.getBytes());
    }

    /*
     * Coloca los datos a devolver.
     */
    private static void colocarDatos(byte res, byte[] flujo) {
        resultado = res;
        longitud = flujo.length;
        informacion = flujo;
    }

    /*
     * Selecciona la accion a realizar en funcion de operacion.
     */
    private static void operarConFichero(File fichero, char operador) throws Exception {
        switch (operador) {
            case 'G':
                enviarFichero(fichero);
                break;
            case 'R':
                renombrarFichero(fichero);
                break;
            case 'D':
                borrarFichero(fichero);
                break;
        }
    }

    /*
     * Coloca los datos para enviar un fichero.
     */
    private static void enviarFichero(File fichero) throws Exception {
        // Leer fichero
        try (InputStream lector = new FileInputStream(fichero)) {
            byte[] contenido = leerNBytes(lector, Integer.MAX_VALUE);

            // Colocar datos del resultado
            colocarDatos((byte) 0, contenido);
        } catch (IOException e) {
            // Lanzar excepcion que no detenga el flujo del programa
            throw new Exception();
        }
    }

    /*
     * Lee una cantidad de bytes de reader
     */
    public static byte[] leerNBytes(InputStream lector, int len) throws IOException {
        if (len < 0) {
            throw new IllegalArgumentException("len < 0");
        }
        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, 8192)];
            int nread = 0;
            while ((n = lector.read(buf, nread,
                    Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if (nread > 0) {
                if (Integer.MAX_VALUE - 8 - total < nread) {
                    throw new OutOfMemoryError("Required array size too large");
                }
                total += nread;
                if (result == null) {
                    result = buf;
                } else {
                    if (bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
        } while (n >= 0 && remaining > 0);

        if (bufs == null) {
            if (result == null) {
                return new byte[0];
            }
            return result.length == total ?
                    result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for (byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    /*
     * Renombra un fichero.
     */
    private static void renombrarFichero(File fichero) throws IOException {
        // Leer nuevo nombre del archivo
        String nombre = reader.readUTF();
        System.out.println("Cliente: " + nombre);

        // Renombrar
        File nuevo = new File(nombre);
        Boolean renombrado = fichero.renameTo(nuevo);

        // Colocar datos del resultado
        if (renombrado) {
            colocarDatos((byte) 0, FICH_RENOMBRADO);
        } else {
            colocarDatos((byte) 2, ERR_2);
        }
    }

    /*
     * Borra un fichero.
     */
    private static void borrarFichero(File fichero) {
        // Borrar
        fichero.delete();

        // Colocar datos del resultado
        colocarDatos((byte) 0, FICH_BORRADO);
    }

    /*
     * Obtener reader y writer.
     */
    private static void obtenerStreams(Socket socket) throws IOException {
        reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

}