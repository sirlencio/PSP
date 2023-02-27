package ej3;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class cliente {

    // Ip y puerto
    static final String IP = "127.0.0.1";
    static final int PUERTO = 8000;

    // Entrada por teclado
    static BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));

    // Reader y writer
    static DataInputStream reader;
    static DataOutputStream writer;

    // Nombre del archivo
    private static String nuevoNombre;

    public static void main(String[] args) {
        // Crear conexion
        try (Socket socket = new Socket(IP, PUERTO)) {
            System.out.println("- Cliente de descarga de ficheros -");

            // Obtener reader y writer
            obtenerStreams(socket);

            // Realizar operaciones
            boolean operar = true;
            while (operar) {
                operar = operacion(socket);
            }

            // Notificar fin al servidor
            writer.writeChar('F');
            writer.flush();

            // Liberar recursos
            reader.close();
            writer.close();
            teclado.close();

            System.out.println("** Finalizada la conexion **");
        } catch (IOException e) {
            System.out.println("No se ha podido establecer la conexion.");
        }
    }

    /*
     * Obtener reader y writer.
     */
    private static void obtenerStreams(Socket socket) throws IOException {
        reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
        writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
    }

    /*
     * Pide una operacion por teclado y muestra el resultado.
     */
    private static boolean operacion(Socket socket) throws IOException {
        // Pedir operador
        System.out.println("Indica una operacion");
        System.out.println("G: Traer el fichero (GET)");
        System.out.println("D: Borrar el fichero (DELETE)");
        System.out.println("R: Renombrar el fichero (RENAME)");
        System.out.println("L: Obtener lista de ficheros ");
        System.out.print("(Deje en blanco para finalizar) Operacion: ");
        String entrada = teclado.readLine();
        System.out.println();

        // Comprobar si se ha dejado en blanco
        if (entrada.isEmpty()) {
            return false;
        }

        char operador = entrada.toUpperCase().charAt(0);

        // Comprobar operador
        if (operadorCorrecto(operador)) {
            // Enviar operador
            writer.writeChar(operador);
            writer.flush();

            // Enviar nombres
            enviarDatos(operador);

            // Mostrar resultados
            resultados(operador);
        } else {
            System.out.println("El operador no es correcto.\n");
        }

        return true;
    }

    /*
     * Env�a los datos necesarios en funcion de la operacion.
     */
    private static void enviarDatos(char operador) throws IOException {
        switch (operador) {
            case 'G':
                // Obtener nombre en el servidor
                String nombre = pedirNombre("Ruta del fichero en el servidor: ");

                nuevoNombre = pedirNombre("Ruta del nuevo fichero en el cliente: ");

                // Enviar nombres
                writer.writeUTF(nombre);
                writer.flush();

                break;
            case 'R':
                // Obtener nombre en el servidor
                nombre = pedirNombre("Ruta del fichero en el servidor: ");

                // Obtener nombre en el cliente
                nuevoNombre = pedirNombre("Nuevo nombre: ");

                // Enviar nombres
                writer.writeUTF(nombre);
                writer.writeUTF(nuevoNombre);
                writer.flush();

                break;
            case 'D':
                // Obtener nombre en el servidor
                nombre = pedirNombre("Ruta del fichero en el servidor: ");

                // Enviar nombre
                writer.writeUTF(nombre);
                writer.flush();

                break;
        }
    }

    /*
     * Pide un String por teclado.
     */
    private static String pedirNombre(String string) {
        String nombre = "";

        do {
            // Pedir nombre
            System.out.print(string);

            try {
                nombre = teclado.readLine();
            } catch (IOException e) {
            }

            // Comprobar que se ha introducido algo
        } while (nombre.isEmpty());

        System.out.println();

        return nombre;
    }

    /*
     * Obtiene y muestra los resultados del servidor.
     */
    private static void resultados(char operador) throws IOException {
        // Obtener resultados
        byte resultado = reader.readByte();
        long longitud = reader.readLong();
        byte[] informacion = reader.readNBytes((int) longitud);

        if (resultado != 0) {
            // C�digo de error
            System.out.println("Error " + resultado + ": " + new String(informacion));
        } else if (operador == 'G') {
            copiarArchivo(informacion);
        } else {
            // Mostrar resultado
            System.out.println(new String(informacion));
        }

        System.out.println();

    }

    /*
     * Crea un archivo con el nombre especificado y escribe en ese archivo el contenido
     * recibido.
     */
    private static void copiarArchivo(byte[] informacion) throws IOException, FileNotFoundException {
        // Crear archivo
        File fichero = new File(nuevoNombre);
        boolean creado = false;
        boolean correcto = true;
        try {
            creado = fichero.createNewFile();
        } catch (IOException e) {
            System.out.println("Error 4: El nombre del fichero de destino no es correcto.");
            correcto = false;
        }

        // Comprobar si se ha creado
        if (creado) {
            // Copiar contenido
            try (OutputStream escritor = new FileOutputStream(fichero)) {
                escritor.write(informacion);
            }

            // Mostrar resultado
            System.out.println("Fichero copiado con exito.");
        } else if (correcto) {
            System.out.println("Error 4: El fichero de destino ya existe.");
        }
    }

    /*
     * Comprueba que el operador es uno de los cuatro permitidos
     */
    private static boolean operadorCorrecto(char operador) {
        return operador == 'G' || operador == 'R' || operador == 'D' || operador == 'L';
    }

}
