package Ejercicio5_2_EmpresaMineria;

public class Excavadora extends Thread{
    public Excavadora() {
    }
    public void opCarga(Volquete vol)  {//Sección Crítica
        try {
            System.out.println("V_"+vol.getNombre()+" (5) – Excavadora - Cargando");
            Thread.sleep(1500);
            System.out.println("V_"+vol.getNombre()+" (6) – Excavadora - Fin carga");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
