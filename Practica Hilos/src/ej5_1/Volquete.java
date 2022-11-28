package ej5_1;

public class Volquete extends Thread {
    private String nombre;
    private Puente puente;
    private Excavadora excavadora;


    public Volquete(String nombre, Puente puente, Excavadora excavadora) {
        super();
        this.nombre = nombre;
        this.puente = puente;
        this.excavadora = excavadora;
    }

    public String getNombre() {
        return nombre;
    }

    public void run() {

        System.out.println("V_" + nombre + " (1) - Esperando derecha para cruzar puente");
        synchronized (puente) {/////////////Seccion Crítica////////////////////////////
            puente.cruzarIzq(this);
            System.out.println("V_" + nombre + " (4) – Esperando izquierda para cargar");
            excavadora.opCarga(this);
            System.out.println("V_" + nombre + " (7) – Esperando izquierda para cruzar puente");
            puente.cruzarDer(this);
        }
    }
}
