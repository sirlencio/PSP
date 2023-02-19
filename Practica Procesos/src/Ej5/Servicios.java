package Ej5;

import java.io.*;
public class Servicios {
    public static void main(String[] args) {
        try {
            String[] command = {
                    "cmd",
                    "/c",
                    "tasklist",
                    "/fi",
                    "\"SESSIONNAME eq Services\""
            };

            ProcessBuilder pb = new ProcessBuilder(command);

            Process p = pb.start();

            // Recogeremos lo que devuelva el proceso, que en este caso será
            // la lista de servicios
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            copiaStream(System.out, br);

        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    private static void copiaStream(PrintStream dest, BufferedReader orig) throws IOException {
        String line;
        while ((line = orig.readLine()) != null) {
            dest.println(line);
        }
    }
}