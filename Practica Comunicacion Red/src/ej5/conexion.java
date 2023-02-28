package ej5;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.Semaphore;

public class conexion extends Thread {

    DataInputStream reader;
    DataOutputStream writer;
    private Socket socket;

    public conexion(Socket socketCliente) {
        this.socket = socketCliente;
    }

    @Override
    public void run() {
        String usuario;
        try {
            reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            writer = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            do {
                usuario = reader.readUTF();
                System.out.println("Cliente: " + usuario);

                if (!usuario.equals("adios")) {
                    String msg = reader.readUTF();
                    System.out.println("Cliente: " + msg);

                    String todo = usuario + ": " + msg;
                    mensajeaTodos(todo);

                    System.out.println("Enviado: " + todo);

                }
            } while (!usuario.equals("adios"));

            reader.close();
            writer.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void mensajeaTodos(String cadena) throws IOException {
        for (conexion c : servidor.lista) {
            c.writer.writeUTF(cadena);
            c.writer.flush();
        }
    }

}
