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
        byte[] informacion = leerNBytes((int) longitud);

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
     * Lee una cantidad de bytes de reader
     */
    public static byte[] leerNBytes(int len) throws IOException {
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
            while ((n = reader.read(buf, nread,
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
