package ej5_1;

public class Camion extends Thread {
    private String nombre;
    private Puente bridge;
    private Excavadora exc;

    public Camion(String nombre, Puente bridge, Excavadora exc) {
        super();
        this.nombre = nombre;
        this.bridge = bridge;
        this.exc = exc;
    }

    public String getNombre() {
        return nombre;
    }

    public void run() {
        System.out.println("C_" + nombre + " (1) - Esperando derecha para cruzar puente");
        synchronized (bridge) { //Seccion critica
            bridge.cruzandoIzq(this);
            System.out.println("C_" + nombre + " (4) – Esperando izquierda para cargar");
            exc.carga(this);
            System.out.println("C_" + nombre + " (7) – Esperando izquierda para cruzar puente");
            bridge.cruzandoDer(this);
        }
    }
}