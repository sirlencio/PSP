package Ej1;

import java.io.*;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPFile;

public class ClienteFTP {
    public static FTPFile[] files;
    public static FTPClient ftp = new FTPClient();
    public static String basePath = "/";
    public static StringBuilder folderPath = new StringBuilder(basePath);
    public static Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        String server = "ftp.rediris.es";

        try {
            ftp.connect(server);

            System.out.println("Connected to " + server + ".");
            System.out.print(ftp.getReplyString());

            int reply = ftp.getReplyCode();

            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                System.err.println("Servidor FTP rechazo la conexión.");
                System.exit(1);
            }

            // Logueado un usuario (true = pudo conectarse, false = no pudo
            // conectarse)
            boolean login = ftp.login("anonymous", "anonymous");
            if (login) {
                ftp.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
                ftp.setFileTransferMode(FTP.BINARY_FILE_TYPE);
                ftp.enterLocalPassiveMode();

                listarArchivos(".");

                int opc;
                do {
                    menu();
                    opc = input.nextInt();
                    switch (opc) {
                        case 1 -> {
                            entrarCarpeta();
                        }
                        case 2 -> {
                            descargarArchivo();
                        }
                        case 0 -> {
                            // Cerrando sesión
                            ftp.logout();

                            // Desconectandose con el servidor
                            ftp.disconnect();
                            System.out.println("Saliendo...");
                        }
                        default -> System.out.println("Opcion invalida");
                    }
                } while (opc != 0);
            } else {
                System.out.println("El usuario no se pudo loguear");
            }
        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }
    }

    //Metodo por el que indicamos la carpeta donde se va a descargar el archivo y el archivo a descargar del servidor
    private static void descargarArchivo() throws IOException {
        System.out.print("Nombre de archivo: ");
        input.nextLine();
        String fileName = input.nextLine();

        System.out.print("Carpeta de destino: ");
        String destino = input.nextLine();

        File archivoDescargado = new File(destino + "/" + fileName);

        OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(archivoDescargado));

        boolean success = ftp.retrieveFile(folderPath.toString() + "/" + fileName, outputStream);

        outputStream.close();

        if (success) {
            System.out.println("** Descargado el fichero correctamente en " + archivoDescargado.getAbsolutePath() + " **");
        } else {
            System.out.println("No se pudo descargar el fichero");
        }
    }

    //Metodo para mostrar la carpeta indicada
    private static void entrarCarpeta() throws IOException {
        //Recogemos el nombre de la carpeta a la que queremos acceder
        System.out.print("Nombre carpeta: ");
        input.nextLine();
        String carpeta = input.nextLine();

        if (carpeta.equals("..")) { //Si el nombre es .., quiere decir que queremos volver hacia atras, asique cambiamos el string
            int index = folderPath.substring(0, folderPath.length() - 1).lastIndexOf("/");
            if (index >= 0) {
                folderPath.setLength(index + 1);
            } else {
                System.out.println("Ya se encuentra en la carpeta raíz.");
                return;
            }

        } else { // Si no, accedemos a la carpeta dicha anteriormente
            folderPath.append(carpeta).append("/");
        }

        System.out.println("Lista de archivos y carpetas de " + folderPath.toString());

        listarArchivos(folderPath.toString());
    }

    // Recuperamos lista de archivos de una ruta
    public static void listarArchivos(String ruta) throws IOException {
        files = ftp.listFiles(ruta);
        for (FTPFile fileServ : files) {
            if (fileServ.isFile()) {
                System.out.println(fileServ.getName() + "\t");
            } else {
                System.out.println(fileServ.getName() + "\t" + "[carpeta]");
            }
        }
    }

    public static void menu() {
        System.out.println("Operar: ");
        System.out.println("1 – Entrar en carpeta");
        System.out.println("2 – Descargar archivo");
        System.out.println("0 - Salir");
        System.out.print("Seleccione opción: ");

    }
}
