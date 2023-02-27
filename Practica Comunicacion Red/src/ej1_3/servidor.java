package ej1_3;

import java.io.*;
import java.net.*;

public class servidor {

    static final int PUERTO = 8000;
    static int contador = 1;

    public static void main(String[] args) {
        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
            for (; ; ) {
                Socket socketCliente = socketServidor.accept();

                conexion conexion = new conexion(socketCliente);

                conexion.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
