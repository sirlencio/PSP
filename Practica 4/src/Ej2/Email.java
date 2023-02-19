package Ej2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;

public class Email {

    public static void main(String[] args) {
        final String fromEmail = args[0];
        final String password = "aula10dam2";
        String archivo = "Clientes.txt";

        try (BufferedReader lector = new BufferedReader(new FileReader(archivo))) {
            String toEmail;
            do {
                toEmail = lector.readLine();
                if (toEmail != null) {
                    System.out.println("Empezando envio al correo: " + toEmail);
                    Properties props = new Properties();

                    props.put("mail.smtp.host", "ssl0.ovh.net"); //SMTP Host
                    props.put("mail.smtp.port", "587"); //TLS Port
                    props.put("mail.smtp.auth", "true"); //enable authentication
                    props.put("mail.smtp.starttls.enable", "true"); //enable STARTTLS


                    //create Authenticator object to pass in Session.getInstance argument
                    Authenticator auth = new Authenticator() {
                        //override the getPasswordAuthentication method
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(fromEmail, password);
                        }
                    };
                    Session session = Session.getInstance(props, auth);

                    EmailUtil.sendEmail(session, toEmail, "Mensaje de Spam&Arrea", leeCuerpo());
                }
            } while (toEmail != null);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    //Metodo que lee el cuerpo de un txt y lo guarda en un string
    private static String leeCuerpo() {
        File archivo = new File("Mensaje.txt");
        StringBuilder contenido = new StringBuilder();

        try (FileReader fileReader = new FileReader(archivo)) {
            int caracter;
            while ((caracter = fileReader.read()) != -1) {
                contenido.append((char) caracter);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return contenido.toString();
    }

}
