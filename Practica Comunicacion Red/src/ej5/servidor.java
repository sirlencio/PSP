package ej5;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class servidor {

    static final int PUERTO = 8000;
    static ArrayList<conexion> lista = new ArrayList<>();

    public static void main(String[] args) {
        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
            for (; ; ) {
                Socket socketCliente = socketServidor.accept();

                conexion conexion = new conexion(socketCliente);
                lista.add(conexion);
                conexion.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
