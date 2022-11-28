package Ejercicio5_2_EmpresaMineria;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class Puente {
    ArrayList<Thread> ExplanadaIzquierda = new ArrayList<>();
    ArrayList<Thread> ExplanadaDerecha = new ArrayList<>();




    public void añadirDerecha(Volquete vol){
        ExplanadaDerecha.add(vol);
    }

    public void  cruzarDer(Volquete vol){ //Sección Crítica
        try {

            ExplanadaIzquierda.remove(vol);
            System.out.println("V_"+vol.getNombre()+" (8) - PUENTE - Comienzo cruzar");
            Thread.sleep(500);
            ExplanadaDerecha.add(vol);

            notify();
            System.out.println("V_"+vol.getNombre()+" (9) - PUENTE - Fin cruzar");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void  cruzarIzq(Volquete vol){
        try {

            ExplanadaDerecha.remove(vol);
            System.out.println("V_"+vol.getNombre()+" (2) - PUENTE - Comienzo cruzar");

            Thread.sleep(500);
            ExplanadaIzquierda.add(vol);

            System.out.println("V_"+vol.getNombre()+" (3) - PUENTE - Fin cruzar");

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int cantidadLadoIzq(){
        return ExplanadaIzquierda.size();
    }
}
