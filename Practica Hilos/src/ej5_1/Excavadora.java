package ej5_1;

public class Excavadora extends Thread{
    public Excavadora() {
    }
    public void opCarga(Volquete vol)  {
        try {
            System.out.println("V_"+vol.getNombre()+" (5) – Excavadora - Cargando vehículo");
            Thread.sleep(1500);
            System.out.println("V_"+vol.getNombre()+" (6) – Excavadora - Fin carga");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
