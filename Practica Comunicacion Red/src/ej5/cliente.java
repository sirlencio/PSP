package ej5;

import java.io.*;
import java.net.*;
import java.util.*;

public class cliente {
    public static void main(String[] args) throws IOException {

        try {
            //Creamos el socket para el cliente indicando el host y el puerto al que nos conectamos
            Socket clientSocket = new Socket("localhost", 8000);

            //Mandamos un mensaje informativo al cliente que se conecte
            System.out.println("Bienvenido al juego de las adivinanzas.\nIntroduce salir para finalizar\nTematica: Frutas");

            //creamos un objeto de las sigueintes clases que nos permitiran saber que informacion nos da el cliente
            //Y mandar una respuesta en funcion de esta
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            //Mediante el uso de la clase scanner almacenamos la informacion que introduzca el cliente por teclado
            Scanner stdIn = new Scanner(System.in);
            String userInput;

            //Mostramos dicha informacion
            System.out.println(in.readLine());

            //Mediante un bucle que nos comprueba si lo que mandamos es diferente de null realice las peticiones
            while ((userInput = stdIn.nextLine()) != null) {
                out.println(userInput);
                //Mostramos la respuesta del servidor
                System.out.println("Server: " + in.readLine());
            }
        } catch (IOException e) {
            System.out.println("Cierre conexion del servidor");
        }
    }
}