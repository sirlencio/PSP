package ej3;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import javax.swing.*;

public class Ejercicio3_1 {

    final int SIZE_X = 1200; // Ancho ventana
    final int SIZE_Y = 700; // Alto ventana
    final int W_LABEL = 25 * 3; // Ancho etiqueta
    final int H_LABEL = 25; // Alto etiqueta

    final int SALTO = 15;   // Nº de pixels máximo que avanza en cada movimiento
    final int SEP_Y = 4;    // separación entre etiquetas
    final int SIZE_CARRIL = (H_LABEL + SEP_Y);

    final int MAX_SEPARACION = 150;

    final int DEMORA_BASE = 100; // Milisegundos que esperamos para realizar el siguiente movimiento
    final int VELOCIDAD = 15;

    final boolean ESPERA_ACTIVA = false; // Indica si la demora se hace con espera activa o con sleep

    final int N_HILOS = (int) SIZE_Y / SIZE_CARRIL;  // Nº de hilos y etiquetas que se mostrarán, tantas como caben en vertical

    final Random rnd = new Random();    // Generador de nºs aleatorios. 
    // Cuando trabajamos en concurrencia debemos tratar
    // de garantizar que este objeto es accedido en modo exclusivo
    // en otro caso no hay garantía de que los números sean aleatorios

    JFrame frame;   // Ventana principal (marco)
    JPanel panel;   // La ventana principal tiene un marco

    final Ejercicio3_1.Hilo hilos[] = new Ejercicio3_1.Hilo[N_HILOS];

    //
    // METODOS =========================================================
    // 
    // default constructor 
    public Ejercicio3_1() {
        initComponents();
        ejecutaHilos();
    }

    // main class 
    public static void main(String[] args) {
        Ejercicio3_1 programa = new Ejercicio3_1();
    }

    /**
     * Creamos la ventana principal
     */
    public void initComponents() {
        // create a new frame to stor text field and button 
        frame = new JFrame("Etiquetas");
        panel = new JPanel(); // create a panel 
        panel.setLayout(null);

        // add panel to frame 
        //frame.add(panel);
        frame.getContentPane().add(panel);
        frame.setSize(SIZE_X, SIZE_Y); // set the size of frame 
        frame.setDefaultCloseOperation(javax.swing.JFrame.DISPOSE_ON_CLOSE);

        /**
         * Evento que captura cuando se cierra la ventana. Comunica a los hilos
         * que finalicen
         */
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                finalizaHilos();
            }
        });

        //5. Show it.
        frame.setVisible(true);

        //frame.show();   // Obsoleto, aunque a nosotros no nos preocupa
    }

    /**
     * Crea una etiqueta JLabel y la situa en la posición indicada
     *
     * @param x
     * @param y
     * @return
     */
    JLabel creaEtiqueta(int x, int y, String id) {
        // create a label to display text 
        JLabel label = new JLabel();

        // add text to label 
        label.setText(id);
        label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBounds(x, y, W_LABEL, H_LABEL);
        label.setOpaque(true);
        panel.add(label);
        return label;
    }

    /**
     * Lanza los hilos
     */
    public void ejecutaHilos() {
        for (int i = 0; i < hilos.length; i++) {
            JLabel l = creaEtiqueta(0, i * SIZE_CARRIL + SEP_Y / 2, Integer.toString(i + 1));
            hilos[i] = new Hilo(l);
        }
        for (int i = 0; i < hilos.length; i++) {
            hilos[i].start();
        }
    }

    public int posPrimero() {
        int x = 0;
        for (int i = 0; i < hilos.length; i++) {
            Rectangle rectLabel = hilos[i].getLabel().getBounds();
            if (hilos[i].continuarHilo && (int) rectLabel.getX() >= x) {
                x = (int) rectLabel.getX();
            }
        }
        return x;
    }

    public Hilo ultimo() {
        int idx = 0;
        boolean encontrado = false;
        while (!encontrado && idx < 24) {
            if (!hilos[idx].continuarHilo) {
                idx++;
            } else {
                encontrado = true;
            }
        }
        for (int i = 0; i < hilos.length; i++) {
            if (hilos[i].continuarHilo && hilos[i].getX() <= hilos[idx].getX()) {
                idx = i;
            }
        }
//        System.out.print("\n posUltimo () - FIN =" + x);
        return hilos[idx];

    }

    /**
     * Comunica a todos los hilos que deben finalizar
     */
    public void finalizaHilos() {
        for (int i = 0; i < hilos.length; i++) {
            hilos[i].finaliza();

        }
    }


    /**
     * *********************************************************************
     * CLASE ANIDADA
     * **********************************************************************
     */
    class Hilo extends Thread {

        private JLabel label;
        private String textLabel;
        boolean continuarHilo = true;
        Hilo bloqueadoPor = null;

        /**
         * Constructor
         *
         * @param label
         */
        public Hilo(JLabel label) {
            this.label = label;
            this.textLabel = label.getText();
        }

        /**
         * Se ejecuta indefinidamente hasta que se marca el hilo para finalizar
         */
        @Override
        public void run() {
            try {
                while (continuarHilo) {
                    //System.out.print("\n [ " + getLabel().getText() + " ]");
                    desplazaEtiqueta();
                    int espera = rnd.nextInt(DEMORA_BASE) * rnd.nextInt(VELOCIDAD);
                    if (ESPERA_ACTIVA) {
                        double inicio = System.currentTimeMillis();
                        while (System.currentTimeMillis() < inicio + espera) ;
                    } else {
                        try {
                            Thread.sleep(espera);
                        } catch (Exception Ex) {
                        }
                    }
                    System.out.println("\nFinalizado " + getLabel().getText());

                }
            } catch (Exception e) {
                System.out.println("\nFinalizado " + getLabel().getText() + " ANOMALO");
                e.printStackTrace();
            }
        }

        public int getX() {
            return (int) label.getBounds().getX();
        }

        /**
         * Marca el hilo para que se finalice
         */
        public void finaliza() {
            continuarHilo = false;
        }

        /**
         * Mueve la etiqueta en la dirección calculada y comprueba que no se
         * salga de los límites de la ventana
         */
        private void desplazaEtiqueta() throws Exception {
            Rectangle rectLabel = label.getBounds();
            Rectangle rectPanel = panel.getBounds();

            int newX = ((int) rectLabel.getX());
            int y = ((int) rectLabel.getY());
            int salto = SALTO;
            if (newX + salto < rectPanel.getWidth() - rectLabel.getWidth()) {
                // No llega al final
                newX += salto;
                //System.out.print("\n" + getLabel().getText() + " Avanza  [" + salto + "] - X=" + newX);
            } else {
                // LLega al final, no sobrepasa
                newX = (int) (rectPanel.getWidth() - rectLabel.getWidth());
                continuarHilo = false;
                System.out.print("\n** Termina " + getLabel().getText());
            }
            rectLabel.setLocation(newX, y);
            label.setBounds(rectLabel);
            Hilo ultimo = ultimo();
            int primeroX = posPrimero();
            int ultimoX = ultimo.getX();
            int separacion = newX - ultimoX;

            if (!ultimo.continuarHilo) {
                throw new Exception("El último no puede estar muerto");
            }

            if (newX == primeroX) { //Va primero
                label.setForeground(Color.YELLOW);
                label.setBackground(Color.BLUE);
            } else if (newX == ultimoX) { //Va ultimo
                label.setForeground(Color.ORANGE);
                label.setBackground(Color.CYAN);
            } else { //Va enmedio
                label.setForeground(Color.BLACK);
                label.setBackground(Color.WHITE);
            }
            label.setText(textLabel + "(" + separacion + ")");
            if (separacion > MAX_SEPARACION) {
                // Penalizado
                label.setForeground(Color.RED);
                label.setBackground(Color.GRAY);
                System.out.print("\n" + getLabel().getText() + " Descalificado");
                this.continuarHilo = false;
                this.label.setText(textLabel + "<" + ultimo.textLabel + ">");
            }
        }

        /**
         * @return the label
         */
        public JLabel getLabel() {
            return label;
        }
    }

}
