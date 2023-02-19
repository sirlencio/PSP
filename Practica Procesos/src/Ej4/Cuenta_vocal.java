package Ej4;

import java.io.*;
import java.lang.ProcessBuilder.Redirect;

public class Cuenta_vocal {
    static final String CLASE1_EJECUTAR = "Cadenas";
    static final String CLASE2_EJECUTAR = "Frecuencia";

    public static void main(String[] args) {
        try {
            System.out.println("RUTA: " + System.getProperty("user.dir"));
            Cuenta_vocal prog = new Cuenta_vocal();
            prog.creaCad(args[1]);
            System.out.println("Cadenas creadas");
            leeVocal(args[0].charAt(0));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void creaCad(String ncad) throws IOException, InterruptedException {
        String[] command = {
                "java",
                "-classpath",
                "C:\\temp\\PSP\\out\\production\\PSP",
                "Ej4." + CLASE1_EJECUTAR,
                ncad
        };

        File arc = new File("texto.txt");
        arc.createNewFile();

        System.out.println("Ejecutando ... \n" + String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectOutput(arc);

        System.out.println("\nDirectorio trabajo: " + "C:\\temp\\PSP\\out\\production\\PSP");
        pb.directory(new File("C:\\temp\\PSP\\out\\production\\PSP"));

        System.out.println("Comando lanzado");
        Process process = pb.start();
        System.out.println("Esperando resultado ...");
        int errCode = process.waitFor();
        System.out.println("Ejecutada aplicación. Código error (valor devuelto) = " + errCode);
    }
    public static void leeVocal(char vocal) throws IOException, InterruptedException {
        String[] command = {
                "java",
                "-classpath",
                "C:\\temp\\PSP\\out\\production\\PSP",
                "Ej4." + CLASE2_EJECUTAR
        };

        System.out.println("Ejecutando ... \n" + String.join(" ", command));
        ProcessBuilder pb = new ProcessBuilder(command);

        File arc = new File("texto.txt");
        pb.redirectInput(Redirect.from(arc));

        System.out.println("\nDirectorio trabajo: " + "C:\\temp\\PSP\\out\\production\\PSP");
        pb.directory(new File("C:\\temp\\PSP\\out\\production\\PSP"));

        System.out.println("Comando lanzado");
        Process process = pb.start();
        System.out.println("Esperando resultado ...");
        int errCode = process.waitFor();
        System.out.println("Ejecutada aplicación. Código error (valor devuelto) = " + errCode);

        String[] parts = output(process.getInputStream()).split("\n");

        switch (vocal) {
            case 'a':
                System.out.println("Frecuencia de " + parts[0]);
                break;
            case 'e':
                System.out.println("Frecuencia de " + parts[1]);
                break;
            case 'i':
                System.out.println("Frecuencia de " + parts[2]);
                break;
            case 'o':
                System.out.println("Frecuencia de " + parts[3]);
                break;
            case 'u':
                System.out.println("Frecuencia de " + parts[4]);
                break;
        }
        FileReader fr = new FileReader(arc);
        BufferedReader br = new BufferedReader(fr);
        String linea;
        while ((linea = br.readLine()) != null)
            System.out.println(linea);
    }

    private static String output(InputStream inputStream) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line + System.getProperty("line.separator"));
            }
        } finally {
            br.close();
        }
        return sb.toString();
    }
}
