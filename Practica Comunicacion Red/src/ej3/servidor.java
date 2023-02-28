package ej3;

import java.io.*;
import java.net.*;

public class servidor {

    static final int PUERTO = 8000;

    public static void main(String[] args) {
        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {

            for (; ; ) {
                // Nueva conexion
                Socket socketCliente = socketServidor.accept();

                // Crear Thread
                conexion conexion = new conexion(socketCliente);

                // Lanzar Thread
                conexion.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
