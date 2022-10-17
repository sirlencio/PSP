package Ej3;

import java.io.*;

public class Lanzador {
    public static void main(String[] args) throws IOException {
        String JAVA_FILE_LOCATION = "C:\\Users\\manana\\PSP\\src\\Ej3\\Cadenas.java";
        String[] command = {"javac", JAVA_FILE_LOCATION};

        ProcessBuilder processBuilder = new ProcessBuilder(command);

        Process process = processBuilder.start();
        /*
         * Check if any errors or compilation errors encounter then print on Console.
         */

        if (process.getErrorStream().read() != -1) {
            print("Compilation Errors", process.getErrorStream());
        }
    /*
    Check if javac process execute successfully or Not
     0 - successful
    */
        if (process.exitValue() == 0) {
            process = new ProcessBuilder(new String[]{"java", "-cp", "C:\\Users\\manana\\PSP\\", "Cadenas"}).start();
    /* Check if RuntimeException or Errors encounter during execution then print errors on console
    Otherwise print Output
    */
            if (process.getErrorStream().read() != -1) {
                print("Errors ", process.getErrorStream());
            } else {
                print("Output ", process.getInputStream());
            }

        }
    }

    private static void print(String status, InputStream input) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        System.out.println("************* " + status + "***********************");
        String line = null;
        while ((line = in.readLine()) != null) {
            System.out.println(line);
        }
        in.close();
    }

}
