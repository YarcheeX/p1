package org.example;

import javax.swing.*;
import java.awt.*;

class MainFrame extends JFrame {

    private final int width = 1075;
    private final int height = 720;

    MainPanel mainPanel;
    ControlPanel controlPanel;
    JLayeredPane layeredPane;

    public MainFrame(){

        setTitle("RockPaperScissors");
        setIconImage(new ImageIcon(MainFrame.class.getResource("/imgs/ProgrammIcon.png")).getImage());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);
        setResizable(false);

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(width, height));

        mainPanel = new MainPanel(this);
        controlPanel = mainPanel.getControlPanel();

        mainPanel.setBounds(0, 0, width, height);
        controlPanel.setBounds(800, 0, 260, 375);

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(controlPanel, JLayeredPane.PALETTE_LAYER);

        add(layeredPane);
    }
}