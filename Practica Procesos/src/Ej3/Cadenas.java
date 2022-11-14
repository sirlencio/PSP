package Ej3;

public class Cadenas {
    public static void main(String[] args) {
        long id = ProcessHandle.current().pid();
        int ncad = Integer.parseInt(args[0]);
        for (int j = 0; j < ncad; j++) {
            int rango = (int) (Math.random() * 20) + 1;
            System.out.println(id + ": " + genera(rango));
        }
    }

    public static StringBuffer genera(int rango) {
        String base = "qwertyuiopasdfghjklñzxcvbnm";
        StringBuffer palabra = new StringBuffer();

        for (int i = 0; i < rango; i++) {
            int selector = (int) (Math.random() * 27);
            palabra.append(base.charAt(selector));
        }
        return palabra;
    }
}
