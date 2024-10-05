package org.example;

import com.formdev.flatlaf.FlatDarkLaf;
import javax.swing.*;

public class p1 {

    private static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel( new FlatDarkLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }
        MainFrame mainFrame = new MainFrame();
        mainFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(p1::createAndShowGUI);
    }
}