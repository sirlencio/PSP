package ej3;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class conexion extends Thread {

    // Mensajes
    private static final byte[] FICH_RENOMBRADO = new String("Fichero renombrado con exito.").getBytes();
    private static final byte[] FICH_BORRADO = new String("Fichero borrado con exito.").getBytes();
    private static final byte[] ERR_1 = new String("El fichero no existe.").getBytes();
    private static final byte[] ERR_2 = new String("El nuevo nombre no es correcto.").getBytes();
    private static final byte[] ERR_3 = new String("Error desconocido.").getBytes();

    // Reader y writer
    DataInputStream reader;
    DataOutputStream writer;

    // Datos a enviar
    byte resultado;
    long longitud;
    byte[] informacion;

    // Socket del cliente
    private Socket socket;

    // Semaforo para controlar el acceso a los ficheros
    private static Semaphore semaforo = new Semaphore(1);

    public conexion(Socket socketCliente) {
        socket = socketCliente;
    }

    public void run() {
        char operador = '+';

        try {
            // Obtener streams
            obtenerStreams(socket);

            // Operar con el cliente
            do {
                operador = operar();
            } while (operador != 'F');

            // Liberar recursos
            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * Recibe el operador, los numeros y devuelve al cliente
     * el resultado de esa operacion
     */
    private char operar() throws IOException {
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
    private void enviarResultados() throws IOException {
        writer.writeByte(resultado);
        writer.writeLong(longitud);
        writer.write(informacion);
        writer.flush();

        System.out.println("Enviado: " + resultado);
    }

    /*
     * Recibe un nombre de archivo, comprueba que existe y opera con el.
     */
    private void comprobarYOperarConFichero(char operador) throws IOException {
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
                operarConFichero(fichero, operador);
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
    private void listarFicheros() {
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
    private void colocarDatos(byte res, byte[] flujo) {
        resultado = res;
        longitud = flujo.length;
        informacion = flujo;
    }

    /*
     * Selecciona la accion a realizar en funcion de operacion.
     */
    private void operarConFichero(File fichero, char operador) throws Exception {
        switch (operador) {
            case 'G':
                enviarFichero(fichero);
                break;
            case 'R':
                renombrarFichero(fichero);
                break;
            case 'D':
                borrarfichero(fichero);
                break;
        }
    }

    /*
     * Coloca los datos para enviar un fichero.
     */
    private void enviarFichero(File fichero) throws Exception {
        // Leer fichero
        try (InputStream lector = new FileInputStream(fichero)) {
            semaforo.acquire();
            byte[] contenido = leerNBytes(lector, Integer.MAX_VALUE);
            semaforo.release();

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
    public byte[] leerNBytes(InputStream lector, int len) throws IOException {
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

            // read to EOF which may read more or less than buffer size
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
            // if the last call to read returned -1 or the number of bytes
            // requested have been read then break
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
     * Renombre un fichero.
     */
    private void renombrarFichero(File fichero) throws IOException, InterruptedException {
        // Leer nuevo nombre del archivo
        String nombre = reader.readUTF();
        System.out.println("Cliente: " + nombre);

        // Renombrar
        File nuevo = new File(nombre);
        semaforo.acquire();
        Boolean renombrado = fichero.renameTo(nuevo);
        semaforo.release();

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
    private void borrarfichero(File fichero) {
        // Borrar
        fichero.delete();

        // Colocar datos del resultado
        colocarDatos((byte) 0, FICH_BORRADO);
    }

    /*
     * Obtener reader y writer.
     */
    private void obtenerStreams(Socket socket) throws IOException {
        reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

}
