package ej3;

import java.io.*;
import java.net.*;

public class servidor {

	// Puerto al que conectarse
	static final int PUERTO = 8000;

	public static void main(String[] args) {
		// Socket del servidor
		try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {

			for (;;) {
				try {
					// Nueva conexion
					Socket socketCliente = socketServidor.accept();

					// Crear Thread
					conexion conexion = new conexion(socketCliente);

					// Lanzar Thread
					conexion.start();
				} finally {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
