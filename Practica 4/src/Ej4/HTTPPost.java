package Ej4;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class HTTPPost {

    static final String URLREGISTRO = "https://ieslamarisma.net/prof/santi/psput4_login/register.php";

    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);

        System.out.print("Introduzca el nombre registrar: ");
        String nombre = input.next();
        System.out.print("Introduzca el apellido registrar: ");
        String ape = input.next();
        registrarUsuario(nombre, ape);

    }

    private static void registrarUsuario(String nombre, String ape) throws IOException {
        int nregistros = 0;
        int secuencia = 1;
        do {
            String name = nombre + ape + obtenerLetraSecuenciada(secuencia);
            String email = nombre + obtenerLetraSecuenciada(secuencia) + "@dam2.ieslamisma.net";
            String pass = nombre + obtenerLetraSecuenciada(secuencia);
            String data = "";
            secuencia++;
            data += URLEncoder.encode("name", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
            data += "&" + URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
            data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
            data += "&" + URLEncoder.encode("cpassword", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
            data += "&" + URLEncoder.encode("terminosycond", "UTF-8") + "=" + URLEncoder.encode("on", "UTF-8");
            data += "&" + URLEncoder.encode("signup", "UTF-8") + "=" + URLEncoder.encode("Registrar", "UTF-8");

            URL url = new URL(URLREGISTRO);

            // Usamos la clase HttpURLConnection que hereda de la clase abstracta
            // URLConnection para poder usar el método "setRequestMethod". Aunque no
            // es necesario, queda más claro
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");

            // Introducimos los campos en el cuerpo de la petición
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(data);
            wr.flush();

            //Se lee lo que devuelve la pagina, ya que al no ocurrir error en el POST, la respuesta es una pagina
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String linea;

            // Leer cada línea del BufferedReader
            while ((linea = rd.readLine()) != null) {
                // Concatenar cada línea en el StringBuilder
                stringBuilder.append(linea);
                stringBuilder.append("\n"); // Agregar un salto de línea
            }

            // Convertir el StringBuilder en un String
            String contenidoPagina = stringBuilder.toString();
            wr.close();
            rd.close();

            //Buscamos en la respuesta que no exista un error de registro para pasar al siguiente registro
            if (!contenidoPagina.contains("Error de registro")) {
                nregistros++;
            }

        } while (nregistros != 20);
    }

    public static String obtenerLetraSecuenciada(int secuencia) {
        StringBuilder sb = new StringBuilder();

        while (secuencia > 0) {
            int remainder = (secuencia - 1) % 26;
            char c = (char) (remainder + 'A');
            sb.insert(0, c);
            secuencia = (secuencia - 1) / 26;
        }

        return sb.toString();
    }

}
