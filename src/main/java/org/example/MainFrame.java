package org.example;

import javax.swing.*;
import java.awt.*;

//класс главного окна
class MainFrame extends JFrame {

    private final int width = 1075;
    private final int height = 720;

    MainPanel mainPanel;        //ссылка на главную панель
    ControlPanel controlPanel;  //ссылка на панель контроля
    JLayeredPane layeredPane;   //панель для расположения по слоям

    public MainFrame(){

        // установить заголовок окна
        setTitle("RockPaperScissors");
        //установить иконку
        setIconImage(new ImageIcon(MainFrame.class.getResource("/imgs/ProgrammIcon.png")).getImage());
        //установить операцию закрытия по умолчанию
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        //установить размеры окна
        setSize(width, height);
        //начальное положение окна по середине экрана
        setLocationRelativeTo(null);
        //запретить зименение размеров
        setResizable(false);

        layeredPane = new JLayeredPane();                           //создать панель со слоями
        layeredPane.setPreferredSize(new Dimension(width, height)); //установить предпочтительный размер

        mainPanel = new MainPanel(this);                  //создать главную панель
        controlPanel = mainPanel.getControlPanel();                 //получить ссылку на панель контроля из главной панели

        mainPanel.setBounds(0, 0, width, height);             //установить границы главной панели
        controlPanel.setBounds(800, 0, 260, 375);//установить границы панели контроля

        layeredPane.add(mainPanel, JLayeredPane.DEFAULT_LAYER);     //разместить панели
        layeredPane.add(controlPanel, JLayeredPane.PALETTE_LAYER);  //на разных слоях

        add(layeredPane);                                           //добавить к окну панель со слоями
    }
}