package ej1_3;

import java.io.*;
import java.net.*;

public class servidor {

	// Puerto al que conectarse
	static final int PUERTO = 8000;

	// Contador de operaciones
	static int contador = 1;

	public static void main(String[] args) {
		// Socket del servidor
		try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {

			for (;;) {
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
