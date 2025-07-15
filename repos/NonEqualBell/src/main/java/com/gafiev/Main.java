package com.gafiev;

import javax.swing.*;

public class Main {
    public static void main(String[] args){
        SwingUtilities.invokeLater(() -> {
            BellInequalityChart example = new BellInequalityChart("Неравенство Белла");
            example.setSize(800, 600);
            example.setLocationRelativeTo(null);
            example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            example.setVisible(true);
        });
    }
    }

