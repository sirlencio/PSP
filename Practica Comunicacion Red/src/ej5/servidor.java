package ej5;

import java.io.*;
import java.net.*;
import java.util.Random;

public class servidor {

    static final int PUERTO = 8000;
    static DataInputStream reader;
    static DataOutputStream writer;
    static int intentos;
    static int adivinar;

    public static void main(String[] args) {
        try {
            int dificultad = 1;
            while (dificultad != 0) {
                try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
                    Socket socketCliente = socketServidor.accept();
                    System.out.println("-Cliente conectado-");
                    reader = new DataInputStream(new BufferedInputStream(socketCliente.getInputStream()));
                    writer = new DataOutputStream(new BufferedOutputStream(socketCliente.getOutputStream()));

                    dificultad = reader.readInt();
                    System.out.println("Elegida dificultad " + dificultad);
                    crearPartida(dificultad);
                    do {
                        int nrecibido = reader.readInt();
                        String pista;

                        if (nrecibido == adivinar) {
                            writer.writeUTF("Te han sobrado " + intentos + " intentos");
                            writer.writeUTF("Has ganado!");
                            writer.flush();
                            System.out.println("El cliente ha ganado la partida, desconectandolo...");
                            break;
                        } else if (nrecibido < adivinar) {
                            pista = "El numero a adivinar es mas grande";
                            intentos--;
                        } else {
                            pista = "El numero a adivinar es mas pequeño";
                            intentos--;
                        }

                        if (intentos == 0) {
                            writer.writeUTF("Te quedan " + intentos + " intentos");
                            writer.writeUTF("Has perdido");
                            writer.flush();
                            System.out.println("El cliente ha perdido la partida, desconectandolo...");
                            break;
                        } else {
                            writer.writeUTF("Te quedan " + intentos + " intentos");
                            writer.writeUTF(pista);
                            writer.flush();
                        }

                    } while (true);
                    intentos = 5;
                    socketCliente.close();
                    System.out.println("-Cliente desconectado-");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            reader.close();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void crearPartida(int dificultad) {
        Random rand = new Random();
        if (dificultad == 1) {
            adivinar = rand.nextInt(10) + 1;
            intentos = 4;
        } else if (dificultad == 2) {
            adivinar = rand.nextInt(50) + 1;
            intentos = 6;
        } else if (dificultad == 3) {
            adivinar = rand.nextInt(100) + 1;
            intentos = 7;
        }
        System.out.println("Elegido numero " + adivinar);
    }


}
