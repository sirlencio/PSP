package Ej4;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

public class HTTPPost {

    static final String URLREGISTRO = "https://ieslamarisma.net/prof/santi/psput4_login/register.php";
    static final String URLLISTADO = "https://ieslamarisma.net/prof/santi/psput4_login/index.php";
    static final String URLLOGIN = "https://ieslamarisma.net/prof/santi/psput4_login/login.php";

    public static void main(String[] args) throws Exception {
        Scanner input = new Scanner(System.in);
        int opc;
        do {
            menu();
            opc = input.nextInt();
            switch (opc) {
                case 1 -> {
                    mostrarLista();
                }
                case 2 -> {
                    System.out.print("Introduzca el nombre registrar: ");
                    String nombre = input.next();
                    System.out.print("Introduzca el apellido registrar: ");
                    String ape = input.next();
                    registrarUsuario(nombre, ape);
                }
                case 3 -> {
                    System.out.print("Introduzca el email para loguear: ");
                    String email = input.next();
                    System.out.print("Introduzca la password para loguear: ");
                    String pass = input.next();
                    loginUsuario(email, pass);
                }
                case 0 -> {
                    System.out.println("Saliendo...");
                }
                default -> System.out.println("Opcion invalida");
            }
        } while (opc != 0);
    }

    private static void loginUsuario(String email, String pass) throws IOException {
        String data = "";
        data += URLEncoder.encode("email", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8");
        data += "&" + URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pass, "UTF-8");
        data += "&" + URLEncoder.encode("login", "UTF-8") + "=" + URLEncoder.encode("Iniciar Sesion", "UTF-8");

        URL url = new URL(URLLOGIN);

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
        String contenidoArchivo = stringBuilder.toString();
        wr.close();
        rd.close();
        if (!contenidoArchivo.contains("Revisa los datos!!!")) {
            System.out.println("Usuario logueado con exito");
        } else {
            System.out.println("Revisa los datos!!!");
        }
    }

    private static void registrarUsuario(String nombre, String ape) throws IOException {
        int n = 0;
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
            String contenidoArchivo = stringBuilder.toString();
            wr.close();
            rd.close();
            if (!contenidoArchivo.contains("Error de registro")) {
                n++;
            }

        } while (n != 20);
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

    public static void mostrarLista() throws IOException {
        Document doc = Jsoup.connect(URLLISTADO).get();

        System.out.println("Titulo de la página: " + doc.title());

        Element tbody = doc.select("tbody").first(); // selecciona el primer tbody
        Elements rows = tbody.select("tr"); // selecciona todas las filas en la tabla

        System.out.println();
        // Itera a través de cada fila y muestra el contenido de las celdas
        for (Element row : rows) {
            Elements th = row.select("th");
            System.out.println("ID: " + th.get(0).text());

            Elements cells = row.select("td");
            for (Element cell : cells) {
                System.out.println(cell.text());
            }
            System.out.println();
        }
    }

    public static void menu() {
        System.out.println("Operar: ");
        System.out.println("1 – Ver lista de usuarios registrados");
        System.out.println("2 – Registrar 20 usuarios");
        System.out.println("3 - Loguearse");
        System.out.println("0 - Salir");
        System.out.print("Seleccione opción: ");

    }
}
