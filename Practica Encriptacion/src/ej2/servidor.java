package ej2;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class servidor {

    private static final byte[] FICH_RENOMBRADO = new String("Fichero renombrado con exito.").getBytes();
    private static final byte[] FICH_BORRADO = new String("Fichero borrado con exito.").getBytes();
    private static final byte[] ERR_1 = new String("Error. El fichero no existe.").getBytes();
    private static final byte[] ERR_2 = new String("El nuevo nombre no es correcto.").getBytes();
    private static final byte[] ERR_3 = new String("Error desconocido.").getBytes();
    static final int PUERTO = 8000;
    static DataInputStream reader;
    static DataOutputStream writer;
    static byte resultado;
    static long longitud;
    static byte[] informacion;

    public static void main(String[] args) {
        for (; ; ) {
            try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
                Socket socketCliente = socketServidor.accept();
                reader = new DataInputStream(new BufferedInputStream(socketCliente.getInputStream()));
                writer = new DataOutputStream(new BufferedOutputStream(socketCliente.getOutputStream()));

                char operador;
                do {
                    operador = operar();
                } while (operador != 'F');

                reader.close();
                writer.close();
                socketCliente.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /*
     * Recibe el operador, los numeros y devuelve al cliente
     * el resultado de esa operacion
     */
    private static char operar() throws IOException {
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
     * Recibe un nombre de archivo, comprueba que existe y opera con el.
     */
    private static void comprobarYOperarConFichero(char operacion) throws IOException {
        String nombre = reader.readUTF();
        System.out.println("Cliente: " + nombre);

        Path ubicacion = Paths.get(nombre);
        File fichero = ubicacion.toFile();

        try {
            if (!fichero.exists()) { // Si el fichero no existe, envia el error 1
                colocarDatos((byte) 1, ERR_1);
            } else {
                operarConFichero(fichero, operacion);
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
    private static void listarFicheros() {
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
    private static void colocarDatos(byte rdo, byte[] flujo) {
        resultado = rdo;
        longitud = flujo.length;
        informacion = flujo;
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
     * Selecciona la accion a realizar en funcion de operacion.
     */
    private static void operarConFichero(File fichero, char operador) throws Exception {
        switch (operador) {
            case 'G' -> enviarFichero(fichero);
            case 'R' -> renombrarFichero(fichero);
            case 'D' -> borrarFichero(fichero);
        }
    }

    /*
     * Coloca los datos para enviar un fichero.
     */
    private static void enviarFichero(File fichero) throws Exception {
        try {
            writer.writeUTF(getHash(fichero));
            System.out.println(getHash(fichero));
            InputStream lector = new FileInputStream(fichero);
            byte[] contenido = lector.readNBytes(Integer.MAX_VALUE);

            colocarDatos((byte) 0, contenido);
        } catch (IOException e) {

            throw new Exception();
        }
    }

    private static void renombrarFichero(File fichero) throws IOException {
        String nombre = reader.readUTF();
        System.out.println("Cliente: " + nombre);

        File nuevo = new File(nombre);
        boolean renombrado = fichero.renameTo(nuevo);

        if (renombrado) {
            colocarDatos((byte) 0, FICH_RENOMBRADO);
        } else {
            colocarDatos((byte) 2, ERR_2);
        }
    }

    private static void borrarFichero(File fichero) {
        if (fichero.delete()) {
            colocarDatos((byte) 0, FICH_BORRADO);
        } else {
            colocarDatos((byte) 3, ERR_3);
        }
    }

    public static String getHash(File f) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(f);
        byte[] dataBytes = new byte[1024];
        int bytesRead = 0;

        while ((bytesRead = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, bytesRead);
        }

        byte[] hashBytes = md.digest();
        StringBuilder sb = new StringBuilder();

        for (byte hashByte : hashBytes) {
            sb.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
        }
        fis.close();
        return sb.toString();
    }
}