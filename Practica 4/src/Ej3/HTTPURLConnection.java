package Ej3;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class HTTPURLConnection {

    // http://elpais.es         https://elpais.com          https://elpais.com/paginainexistente
    public static void main(String[] args) throws IOException {
        URL url = new URL("http://elpais.es"); // args[0]

        URLConnection urlc = url.openConnection();

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        urlc.setRequestProperty("User-Agent", "Chichinabo de tres al cuarto");

        try { //Estamos declarando el inputstream dependiendo de si la pagina devuelve un codigo u otro (como por ejemplo un codigo de notfound)
            InputStream inputStream;
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                inputStream = connection.getErrorStream();
            } else {
                inputStream = urlc.getInputStream();
            }
            FileOutputStream outputStream = new FileOutputStream("output.html"); // args[1]

            // Leer datos del InputStream y escribirlos en el archivo
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            // Cerrar los flujos de entrada y salida
            inputStream.close();
            outputStream.close();
            System.out.println("Guardado el fichero "); // args[1]
            System.out.println("Código de respuesta HTTP: " + responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}