package ej5;

import java.io.*;
import java.net.*;
import java.util.*;

public class servidor {
    public static void main(String[] args) throws IOException {

        try {
            //Creamos el socket del servidor y mandamos un mensaje al conectar
            ServerSocket serverSocket = new ServerSocket(8000);
            System.out.println("Servidor escuchando en el puerto 8000");
            while (true) {

                //Si todo ha ido bien aceptamos al cliente e indicamos que se ha conectado un cliente
                Socket clientSocket = serverSocket.accept();
                System.out.println("Cliente conectado");


                //Nuevamente el uso de estas clases para almacenar y generar una respuesta en funcion de la peticion del cliente
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));


                //Array que almacena las palabras del juego
                String[] secretWords = {"manzana", "platano", "melon", "naranja", "sandia", "uva", "pomelo", "fresa",
                        "kiwi", "limon"};


                //Mediante la clase random elegimos una palabra del array, la cual se debera adivinar
                Random random = new Random();
                int secretIndex = random.nextInt(secretWords.length);
                String secretWord = secretWords[secretIndex];


                out.println("Introduce una palabra:");
                String inputLine;

                //Mediante este array comprobamos las respuestas enviadas y generamos una respuesta en funcion de estas
                while ((inputLine = in.readLine()) != null) {
                    if (inputLine.equalsIgnoreCase(secretWord)) {
                        out.println("Enhorabuena, has acertado!");

                        clientSocket.close();
                        break;
                    } else if (inputLine.equalsIgnoreCase("salir")) {
                        out.println("Oh, espero verte pronto, mas suerte la proxima");
                        clientSocket.close();
                        System.out.println("Cliente desconectado");
                        break;
                    } else {

                        out.println("Incorrecto, prueba otra vez:");
                    }
                }
                clientSocket.close();

            }

        } catch (IOException e) {
            System.out.println("Cierre conexion del cliente");

        }
    }

}