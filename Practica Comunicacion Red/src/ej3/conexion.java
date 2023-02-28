package ej3;

import java.io.*;
import java.net.Socket;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Semaphore;

public class conexion extends Thread {

    private static final byte[] FICH_RENOMBRADO = new String("Fichero renombrado con exito.").getBytes();
    private static final byte[] FICH_BORRADO = new String("Fichero borrado con exito.").getBytes();
    private static final byte[] ERR_1 = new String("Error. El fichero no existe.").getBytes();
    private static final byte[] ERR_2 = new String("El nuevo nombre no es correcto.").getBytes();
    private static final byte[] ERR_3 = new String("Error desconocido.").getBytes();
    DataInputStream reader;
    DataOutputStream writer;
    byte resultado;
    long longitud;
    byte[] informacion;
    private Socket socket;

    // Semaforo para controlar el acceso a los ficheros
    private static Semaphore semaforo = new Semaphore(1);

    public conexion(Socket socketCliente) {
        socket = socketCliente;
    }

    public void run() {
        char operador;
        try {
            reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            do {
                operador = operar();
            } while (operador != 'F');

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
        char operador = reader.readChar();
        System.out.println("Cliente: " + operador);

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
        String nombre = reader.readUTF();
        System.out.println("Cliente: " + nombre);

        Path ubicacion = Paths.get(nombre);
        File fichero = ubicacion.toFile();

        try {
            if (!fichero.exists()) {
                colocarDatos((byte) 1, ERR_1);
            } else {
                // Realizar la operacion
                operarConFichero(fichero, operador);
            }
        } catch (IOException e) {
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
        File carpeta = new File(".");
        File[] todo = carpeta.listFiles();

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
            case 'G' -> enviarFichero(fichero);
            case 'R' -> renombrarFichero(fichero);
            case 'D' -> borrarfichero(fichero);
        }
    }

    /*
     * Coloca los datos para enviar un fichero.
     */
    private void enviarFichero(File fichero) throws Exception {
        try (InputStream lector = new FileInputStream(fichero)) {
            semaforo.acquire();
            byte[] contenido = lector.readNBytes(Integer.MAX_VALUE);
            semaforo.release();

            // Colocar datos del resultado
            colocarDatos((byte) 0, contenido);
        } catch (IOException e) {
            // Lanzar excepcion que no detenga el flujo del programa
            throw new Exception();
        }
    }

    /*
     * Renombre un fichero.
     */
    private void renombrarFichero(File fichero) throws IOException, InterruptedException {
        String nombre = reader.readUTF();
        System.out.println("Cliente: " + nombre);

        File nuevo = new File(nombre);
        semaforo.acquire();
        boolean renombrado = fichero.renameTo(nuevo);
        semaforo.release();

        if (renombrado) {
            colocarDatos((byte) 0, FICH_RENOMBRADO);
        } else {
            colocarDatos((byte) 2, ERR_2);
        }
    }

    private void borrarfichero(File fichero) throws InterruptedException {
        semaforo.acquire();
        boolean borrado = fichero.delete();
        semaforo.release();

        if (borrado) {
            colocarDatos((byte) 0, FICH_BORRADO);
        } else {
            colocarDatos((byte) 1, ERR_1);
        }
    }

}
