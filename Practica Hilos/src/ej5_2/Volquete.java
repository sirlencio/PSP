package Ejercicio5_2_EmpresaMineria;

public class Volquete extends Thread{

    private String nombre;
    private Puente puente;
    private Excavadora excavadora;
    private final int MAX_VOLQUETES = 3;


    public Volquete(String nombre, Puente puente, Excavadora excavadora) {
        super();
        this.nombre = nombre;
        this.puente = puente;
        this.excavadora = excavadora;
    }
    public String getNombre(){
        return nombre;
    }
    public void run(){

        System.out.println("V_"+nombre+" (1) - Esperando derecha para cruzar puente");

        synchronized (puente){//////////////////Sección Crítica/////////////////////////////
            try {
            if(puente.cantidadLadoIzq() >= MAX_VOLQUETES){
                    puente.wait();
            }
            puente.cruzarIzq(this);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("V_"+nombre+" (4) – Esperando izquierda para cargar");
        synchronized (excavadora){ //////////////////Sección Crítica/////////////////////////////
            excavadora.opCarga(this);
        }
        System.out.println("V_"+nombre+" (7) – Esperando izquierda para cruzar puente");
        synchronized (puente){ //////////////////Sección Crítica/////////////////////////////
            puente.cruzarDer(this);
       }
    }
}
