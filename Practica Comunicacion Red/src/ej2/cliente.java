package ej2;

import java.io.*;
import java.net.Socket;

public class cliente {

    static final String IP = "127.0.0.1";
    static final int PUERTO = 8000;
    static BufferedReader teclado = new BufferedReader(new InputStreamReader(System.in));
    static DataInputStream reader;
    static DataOutputStream writer;
    private static String nuevoNombre;

    public static void main(String[] args) {
        try (Socket socket = new Socket(IP, PUERTO)) {
            System.out.println("- Cliente de descarga de ficheros -");

            reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            // Realizar operaciones
            boolean operar = true;
            while (operar) {
                operar = operacion();
            }

            writer.writeChar('F');
            writer.flush();

            reader.close();
            writer.close();
            teclado.close();
        } catch (IOException e) {
            System.out.println("No se ha podido establecer la conexion.");
        }
    }

    /*
     * Pide una operacion por teclado y muestra el resultado.
     */
    private static boolean operacion() throws IOException {
        System.out.println("Indica una operacion");
        System.out.println("G: Traer el fichero (GET)");
        System.out.println("D: Borrar el fichero (DELETE)");
        System.out.println("R: Renombrar el fichero (RENAME)");
        System.out.println("L: Obtener lista de ficheros ");
        System.out.print("(Deje en blanco para finalizar) Operacion: ");
        String entrada = teclado.readLine();
        System.out.println();

        if (entrada.isEmpty()) {
            return false;
        }

        char operador = entrada.toUpperCase().charAt(0);

        if (operador == 'G' || operador == 'R' || operador == 'D' || operador == 'L') {
            writer.writeChar(operador);
            writer.flush();

            enviarDatos(operador);

            obtieneResultados(operador);
        } else {
            System.out.println("El operador no es correcto.\n");
        }
        return true;
    }

    /*
     * Envia los datos necesarios en funcion de la operacion.
     */
    private static void enviarDatos(char operador) throws IOException {
        switch (operador) {
            case 'G':
                String nombre = pedirNombre("Ruta del fichero en el servidor: ");

                nuevoNombre = pedirNombre("Ruta del nuevo fichero en el cliente: ") + "/" + nombre;

                writer.writeUTF(nombre);
                writer.flush();

                break;
            case 'R':
                nombre = pedirNombre("Ruta del fichero en el servidor: ");

                nuevoNombre = pedirNombre("Indique la ruta y el nuevo nombre del fichero en el servidor: ");

                writer.writeUTF(nombre);
                writer.writeUTF(nuevoNombre);
                writer.flush();

                break;
            case 'D':
                nombre = pedirNombre("Ruta del fichero en el servidor: ");

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
        System.out.print(string);
        try {
            nombre = teclado.readLine();
        } catch (IOException ignored) {
        }
        System.out.println();
        return nombre;
    }

    /*
     * Obtiene y muestra los resultados del servidor.
     */
    private static void obtieneResultados(char operador) throws IOException {
        byte resultado = reader.readByte();
        long longitud = reader.readLong();
        byte[] informacion = reader.readNBytes((int) longitud);

        if (resultado != 0) {
            System.out.println("Error " + resultado + ": " + new String(informacion));
        } else if (operador == 'G') {
            copiarArchivo(informacion);
        } else {
            System.out.println(new String(informacion));
        }
        System.out.println();
    }

    /*
     * Crea un archivo con el nombre especificado y escribe el contenido
     * recibido.
     */
    private static void copiarArchivo(byte[] informacion) throws IOException {
        File fichero = new File(nuevoNombre);
        boolean creado = false;
        boolean correcto = true;
        try {
            creado = fichero.createNewFile();
        } catch (IOException e) {
            System.out.println("Error 4: El nombre del fichero de destino no es correcto.");
            correcto = false;
        }

        if (creado) {
            try (OutputStream escritor = new FileOutputStream(fichero)) {
                escritor.write(informacion);
            }
            System.out.println("Fichero copiado con exito.");
        } else if (correcto) {
            System.out.println("Error 4: El fichero de destino ya existe.");
        }
    }

}
