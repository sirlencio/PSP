package ej2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;


public class Ejercicio2 {
    final int DERECHA = 0, IZQUIERDA = 1, ARRIBA = 2, ABAJO = 3;
    final int SIZE_X = 1000; // Ancho ventana
    final int SIZE_Y = 700; // Alto ventana
    final int W_LABEL = 30; // Ancho etiqueta
    final int H_LABEL = 30; // Alto etiqueta
    final int OFFSET = 10;   // Nº de pixels que avanza en cada movimiento
    final int PROB_CAMBIADIRECCION = 5; // Porcentaje por el que se cambia de dirección
    final static int N_HILOS = 50;  // Nº de hilos, y etiquetas que se mostrarán
    final Random rnd = new Random();    // Generador de nºs aleatorios.
    // Cuando trabajamos en concurrencia debemos tratar
    // de garantizar que este objeto es accedido en modo exclusivo
    // en otro caso no hay garantía de que los números sean aleatorios
    JFrame frame;   // Ventana principal (marco)
    JPanel panel;   // La ventana principal tiene un marco
    final Hilo[] hilos = new Hilo[N_HILOS];

    //
    // METODOS =========================================================
    //
    // default constructor
    Ejercicio2() {
        initComponents();
    }

    // main class
    public static void main(String[] args) {
        Ejercicio2 programa = new Ejercicio2();
        programa.ejecutaHilos();
    }

    /**
     * Creamos la ventana principal
     */
    public void initComponents() {
        // create a new frame to store text field and button
        frame = new JFrame("Etiquetas");
        panel = new JPanel(); // create a panel
        panel.setLayout(null);
        // add panel to frame
        frame.add(panel);
        frame.setSize(SIZE_X, SIZE_Y); // set the size of frame
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        /*
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
        frame.show();   // Obsoleto, aunque a nosotros no nos preocupa
    }

    /**
     * Crea una etiqueta JLabel y la situa en la posición indicada
     */
    JLabel creaEtiqueta(int x, int y, String id) {
        // create a label to display text
        JLabel label = new JLabel();
        // add text to label
        label.setText(id);
        label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBounds(x, y, W_LABEL, H_LABEL);
        panel.add(label);
        return label;
    }

    public Point getPosLibre() {
        synchronized (hilos) {
            Rectangle rect = new Rectangle(0, 0, H_LABEL, W_LABEL);
            Rectangle rPanel = panel.getBounds();
            int x, y;
            do {
                x = rnd.nextInt((int) rPanel.getWidth() - W_LABEL);
                y = rnd.nextInt((int) rPanel.getHeight() - H_LABEL);
                rect.setLocation(x, y);
            } while (colisionaConOtras(null, rect));
            return new Point(x, y);
        }
    }

    public boolean colisionaConOtras(JLabel label, Rectangle newPos) {
        synchronized (hilos) {
            for (Hilo hilo : hilos) {
                if (hilo != null) {
                    JLabel aux = hilo.getLabel();
                    if (aux != label) {
                        if (newPos.intersects(aux.getBounds())) {
                            return true;
                        }
                    }
                }
            }
            return false;
        }
    }
    /**
     * Lanza los hilos
     */
    public void ejecutaHilos() {
        for (int i = 0; i < hilos.length; i++) {
            Point pos = getPosLibre();
            JLabel l = creaEtiqueta(pos.x, pos.y, Integer.toString(i));
            hilos[i] = new Hilo(l);
            hilos[i].start();
        }
    }

    /**
     * Comunica a todos los hilos que deben finalizar
     */
    public void finalizaHilos() {
        for (Hilo hilo : hilos) {
            hilo.finaliza();
        }
    }

    /**
     * *********************************************************************
     * CLASE ANIDADA
     * **********************************************************************
     */
    class Hilo extends Thread {

        JLabel label;
        boolean continuarHilo = true;
        int direccion = ABAJO;
        boolean obligaCambioDireccion = false;

        /**
         * Constructor
         */
        public Hilo(JLabel label) {
            this.label = label;
        }

        /**
         * Se ejecuta indefinidamente hasta que se marca el hilo para finalizar
         */
        @Override
        public void run() {
            while (continuarHilo) {
                //synchronized (hilos) {
                    calculaDireccion();
                    desplazaEtiqueta();
                //}
                try {
                    Thread.sleep(30);
                } catch (Exception ignored) {
                }
            }
            System.out.println("\nFinalizado " + label.getText());
        }

        /**
         * Marca el hilo para que se finalice
         */
        public void finaliza() {
            continuarHilo = false;
        }

        /**
         * Calcula la dirección en la que se debe mover la etiqueta
         */
        private void calculaDireccion() {
            // Calculamos dirección de movimiento
            if (rnd.nextInt(100) < PROB_CAMBIADIRECCION || obligaCambioDireccion) {
                direccion = rnd.nextInt(4);
                obligaCambioDireccion = false;
            }
        }

        /**
         * Mueve la etiqueta en la dirección calculada y comprueba que no se
         * salga de los límites de la ventana
         */
        private void desplazaEtiqueta() {
            Rectangle rLabel = label.getBounds();
            Rectangle rPanel = panel.getBounds();
            int newX = (int) rLabel.getX();
            int newY = (int) rLabel.getY();
            System.out.print("\nDirección " + label.getText() + " = " + direccion + "(" + newX + "," + newY + ")");
            switch (direccion) {
                case DERECHA:
                    if (newX + OFFSET < rPanel.getWidth() - rLabel.getWidth()) {
                        newX += OFFSET;
                    } else {
                        obligaCambioDireccion = true;
                    }
                    break;
                case IZQUIERDA:
                    if (newX - OFFSET > 0) {
                        newX -= OFFSET;
                    } else {
                        obligaCambioDireccion = true;
                    }
                    break;
                case ARRIBA:
                    if (newY - OFFSET > 0) {
                        newY -= OFFSET;
                    } else {
                        obligaCambioDireccion = true;
                    }
                    break;
                case ABAJO:
                    if (newY + OFFSET < rPanel.getHeight() - rLabel.getHeight()) {
                        newY += OFFSET;
                    } else {
                        obligaCambioDireccion = true;
                    }
                    break;
            }
            rLabel.setLocation(newX, newY);
            if (!Ejercicio2.this.colisionaConOtras(label, rLabel)) {
                label.setBounds(rLabel);
            }else{
                obligaCambioDireccion = true;
            }
        }

        public JLabel getLabel() {
            return label;
        }
    }
}
