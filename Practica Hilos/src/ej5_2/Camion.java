package ej5_2;

import java.util.concurrent.Semaphore;

public class Camion extends Thread {

    private String nombre;
    private Puente bridge;
    private Excavadora excavadora;

    private final int max = 3;

    public Camion(String nombre, Puente bridge, Excavadora excavadora) {
        super();
        this.nombre = nombre;
        this.bridge = bridge;
        this.excavadora = excavadora;
    }

    public String getNombre() {
        return nombre;
    }

    public void run(){
        System.out.println("C_"+nombre+" (1) - Esperando derecha para cruzar puente");
        synchronized (bridge){ //Seccion critica
            try {
                if(bridge.nLadoIzq() >= max){
                    bridge.wait();
                }
                bridge.cruzandoIzq(this);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("C_"+nombre+" (4) – Esperando izquierda para cargar");
        synchronized (excavadora){ //Seccion critica
            excavadora.carga(this);
        }
        System.out.println("C_"+nombre+" (7) – Esperando izquierda para cruzar puente");
        synchronized (bridge){ //Seccion critica
            bridge.cruzandoDer(this);
        }
    }
}
