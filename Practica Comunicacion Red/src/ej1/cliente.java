package ej1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;


public class cliente {

    public static final int PORT = 4444;
    public static final String SERVER = "127.0.0.1";

    public static void main(String[] args) throws IOException {
        Socket socketCliente = null;
        BufferedReader recibe = null;
        PrintWriter envia = null;

        // Creamos un socket en el lado cliente, enlazado con un
        // servidor que está en la misma máquina que el cliente
        // y que escucha en el puerto 4444
        try {
            socketCliente = new Socket(SERVER, PORT);
            // Obtenemos el canal de entrada
            recibe = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            // Obtenemos el canal de salida
            envia = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socketCliente.getOutputStream())), true);
        } catch (IOException e) {
            System.err.println("No puede establer canales de E/S para la conexión");
            System.exit(-1);
        }
        BufferedReader stdIn;
        stdIn = new BufferedReader(new InputStreamReader(System.in));

        String linea;

        // El programa cliente no analiza los mensajes enviados por el
        // usario, simplemente los reenvía al servidor hasta que este
        // se despide con "Adios"
        try {
            while (true) {
                // Leo la entrada del usuario
                System.out.println("Escriba algo ...");
                linea = stdIn.readLine();
                // La envia al servidor
                envia.println(linea);
                // Envía a la salida estándar la respuesta del servidor
                linea = recibe.readLine();
                System.out.println("Respuesta servidor: " + linea);
                // Si es "Adios" es que finaliza la comunicación
                if (linea.equals("Adios")) {
                    break;
                }
            }
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }

        // Libera recursos
        envia.close();
        recibe.close();
        stdIn.close();
        // Cierro socket
        socketCliente.close();
    }

}
