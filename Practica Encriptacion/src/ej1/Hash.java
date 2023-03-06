package ej1;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class Hash {
    static final int BUFFER_SIZE = 2048;
    static final String ARCHIVO = "original.png";

    public static void main(String[] args) {
        try {
            // Inicializa el tipo de Hash que vamos a utilizar
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            MessageDigest messageDigest1 = MessageDigest.getInstance("MD5");
            MessageDigest messageDigest2 = MessageDigest.getInstance("SHA-1");

            // leer fichero byte a byte
            try {
                InputStream archivo = new FileInputStream(ARCHIVO);
                byte[] buffer = new byte[BUFFER_SIZE];
                int n_bytes;

                while ((n_bytes = archivo.read(buffer)) > 0) {
                    // Pasa texto claro a la función resumen
                    messageDigest.update(buffer, 0, n_bytes);
                    messageDigest1.update(buffer, 0, n_bytes);
                    messageDigest2.update(buffer, 0, n_bytes);
                }

                archivo.close();
                // Genera el resumen para el algoritmo seleccionado
                byte[] resumen = messageDigest.digest();
                byte[] resumen1 = messageDigest1.digest();
                byte[] resumen2 = messageDigest2.digest();

                // Pasar los resumenes a hexadecimal para mostrarlos archivo 1
                StringBuilder hashBuffer = new StringBuilder();
                for (byte resuman : resumen) {
                    // Genera la letra o digito que corresponde a cada byte
                    // 4 bits primeros y 4 bits segundos en Hex
                    // Se tiene que hacer así para mostrar los ceros a la izquierda que de otra
                    // forma no saldrían
                    hashBuffer.append(Integer.toHexString((resuman >> 4) & 0xf));
                    hashBuffer.append(Integer.toHexString(resuman & 0xf));
                }
                System.out.println("Resumen SHA-256: \n" + hashBuffer);
                StringBuilder hashBuffer1 = new StringBuilder();
                for (byte b : resumen1) {
                    // Genera la letra o digito que corresponde a cada byte
                    // 4 bits primeros y 4 bits segundos en Hex
                    // Se tiene que hacer así para mostrar los ceros a la izquierda que de otra
                    // forma no saldrían
                    hashBuffer1.append(Integer.toHexString((b >> 4) & 0xf));
                    hashBuffer1.append(Integer.toHexString(b & 0xf));
                }
                System.out.println("Resumen MD5: \n" + hashBuffer1);

                StringBuilder hashBuffer2 = new StringBuilder();
                for (byte b : resumen2) {
                    // Genera la letra o digito que corresponde a cada byte
                    // 4 bits primeros y 4 bits segundos en Hex
                    // Se tiene que hacer así para mostrar los ceros a la izquierda que de otra
                    // forma no saldrían
                    hashBuffer2.append(Integer.toHexString((b >> 4) & 0xf));
                    hashBuffer2.append(Integer.toHexString(b & 0xf));
                }
                System.out.println("Resumen SHA-1: \n" + hashBuffer2);
            } // lectura de los datos del fichero
            catch (java.io.FileNotFoundException fnfe) {
                System.out.println("No existe el fichero " + ARCHIVO);
            } catch (java.io.IOException ignored) {
            }
        } // declarar funciones resumen
        catch (java.security.NoSuchAlgorithmException ignored) {
        }

    }
}
