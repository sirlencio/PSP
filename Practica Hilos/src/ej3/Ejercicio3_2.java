package ej3;

import java.awt.Color;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Ejercicio3_2 {
    final int SIZE_X = 1200;

    final int SIZE_Y = 700;

    final int W_LABEL = 75;

    final int H_LABEL = 25;

    final int SALTO = 15;

    final int SEP_Y = 4;

    final int SIZE_CARRIL = 29;

    final int MAX_SEPARACION = 150;

    final int DEMORA_BASE = 100;

    final int VELOCIDAD = 15;


    final int N_HILOS = 24;

    final Random rnd = new Random();

    JFrame frame;

    JPanel panel;

    final Hilo[] hilos = new Hilo[N_HILOS];

    public Ejercicio3_2() {
        initComponents();
        ejecutaHilos();
    }

    public static void main(String[] args) {
        Ejercicio3_2 programa = new Ejercicio3_2();
    }

    public void initComponents() {
        this.frame = new JFrame("Etiquetas");
        this.panel = new JPanel();
        this.panel.setLayout(null);
        this.frame.getContentPane().add(this.panel);
        this.frame.setSize(1200, 700);
        this.frame.setDefaultCloseOperation(2);
        this.frame.addWindowListener(new WindowAdapter() {
            public void windowClosed(WindowEvent e) {
                super.windowClosed(e);
                Ejercicio3_2.this.finalizaHilos();
            }
        });
        this.frame.setVisible(true);
    }

    JLabel creaEtiqueta(int x, int y, String id) {
        JLabel label = new JLabel();
        label.setText(id);
        label.setBorder(BorderFactory.createLineBorder(Color.BLUE, 1));
        label.setHorizontalAlignment(0);
        label.setBounds(x, y, 75, 25);
        label.setOpaque(true);
        this.panel.add(label);
        return label;
    }

    public void ejecutaHilos() {
        int i;
        for (i = 0; i < this.hilos.length; i++) {
            JLabel l = creaEtiqueta(0, i * 29 + 2, Integer.toString(i));
            this.hilos[i] = new Hilo(l);
        }
        for (i = 0; i < this.hilos.length; i++)
            this.hilos[i].start();
    }

    public synchronized int posPrimero() {
        int x = 0;
        for (int i = 0; i < this.hilos.length; i++) {
            if (this.hilos[i] != null) {
                Rectangle rectLabel = this.hilos[i].getLabel().getBounds();
                if ((int)rectLabel.getX() > x)
                    x = (int)rectLabel.getX();
            }
        }
        return x;
    }

    public synchronized Hilo ultimo() {
        int idx = 0;
        for (int i = 0; i < this.hilos.length; i++) {
            if (this.hilos[i].getX() < this.hilos[idx].getX())
                idx = i;
        }
        return this.hilos[idx];
    }

    public void finalizaHilos() {
        for (int i = 0; i < this.hilos.length; i++)
            this.hilos[i].finaliza();
    }

    public synchronized void despiertaBloqueadosPor(Hilo hiloBloqueador) {
        for (int i = 0; i < this.hilos.length; i++) {
            if ((this.hilos[i]).bloqueadoPor == hiloBloqueador && this.hilos[i]
                    .getX() <= hiloBloqueador.getX())
                synchronized (this.hilos[i]) {
                    this.hilos[i].notify();
                    (this.hilos[i]).bloqueadoPor = null;
                }
        }
    }

    class Hilo extends Thread {
        private JLabel label;

        private String textLabel;

        boolean continuarHilo = true;

        Hilo bloqueadoPor = null;

        public Hilo(JLabel label) {
            this.label = label;
            this.textLabel = label.getText();
        }

        public void run() {
            while (this.continuarHilo) {
                desplazaEtiqueta();
                int espera = Ejercicio3_2.this.rnd.nextInt(100) * Ejercicio3_2.this.rnd.nextInt(15);
                try {
                    Thread.sleep(espera);
                } catch (Exception exception) {}
            }
            System.out.println("\nFinalizado " + getLabel().getText());
        }

        public void bloqueadoPor(Hilo por) {
            this.bloqueadoPor = por;
            this.label.setForeground(Color.ORANGE);
            System.out.print("\n" + getLabel().getText() + " Bloqueado");
            this.label.setBorder(BorderFactory.createLineBorder(Color.ORANGE, 4));
            try {
                synchronized (this) {
                    wait();
                }
                System.out.print("\n" + getLabel().getText() + " Despierta");
                this.label.setBorder(BorderFactory.createLineBorder(Color.MAGENTA, 2));
            } catch (InterruptedException interruptedException) {}
        }

        public int getX() {
            return (int)this.label.getBounds().getX();
        }

        public void finaliza() {
            this.continuarHilo = false;
        }

        private void desplazaEtiqueta() {
            Rectangle rectLabel = this.label.getBounds();
            Rectangle rectPanel = Ejercicio3_2.this.panel.getBounds();
            int newX = (int)rectLabel.getX();
            int y = (int)rectLabel.getY();
            int salto = SALTO;
            if ((newX + salto) < rectPanel.getWidth() - rectLabel.getWidth()) {
                newX += salto;
            } else {
                newX = (int)(rectPanel.getWidth() - rectLabel.getWidth());
                this.continuarHilo = false;
                System.out.print("\n** Termina " + getLabel().getText());
            }
            rectLabel.setLocation(newX, y);
            this.label.setBounds(rectLabel);
            Hilo ultimo = ultimo();
            int primeroX = posPrimero();
            int ultimoX = ultimo.getX();
            int separacion = newX - ultimoX;

            if (newX == primeroX) {
                this.label.setForeground(Color.YELLOW);
                this.label.setBackground(Color.BLUE);
            } else if (newX == ultimoX) {
                this.label.setForeground(Color.RED);
                this.label.setBackground(Color.CYAN);
            } else {
                this.label.setForeground(Color.BLACK);
                this.label.setBackground(Color.WHITE);
            }
            this.label.setText(this.textLabel + "(" + separacion + ")");
            if (separacion > MAX_SEPARACION) {
                System.out.print("\n Penalizado [ **" + getLabel().getText() + "** ]");
                this.label.setForeground(Color.ORANGE);
                System.out.print("\n" + getLabel().getText() + " Duerme");
                bloqueadoPor(ultimo);
            }
            Ejercicio3_2.this.despiertaBloqueadosPor(this);
        }

        public JLabel getLabel() {
            return this.label;
        }
    }
}
