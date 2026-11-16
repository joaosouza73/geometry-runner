package com.example.geometrydash;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/** Ponto de entrada da aplicação. */
public final class Main {
    private Main() { }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame window = new JFrame("Geometry Runner — Java e POO");
            window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            window.setResizable(false);
            window.setContentPane(new GamePanel());
            window.pack();
            window.setLocationRelativeTo(null);
            window.setVisible(true);
        });
    }
}
