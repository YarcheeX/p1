package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

class ControlPanel extends JPanel {

    //ссылка на главное окно и главную панель
    MainFrame mainFrame;
    MainPanel mainPanel;

    //кнопки управления
    JButton configButton;
    JButton startButton;
    JButton stopButton;
    JButton playButton;
    JButton pauseButton;

    //кнопки добваления
    JButton rockButton;
    JButton paperButton;
    JButton scissorsButton;
    JButton rpsButton;

    //лэйблы счетчики
    JLabel rockCounter;
    JLabel paperCounter;
    JLabel scissorsCounter;
    JLabel totalCounter;

    //счетчик итераций
    JLabel iterationsCounter;

    //счетчики побед
    JLabel rockWinsCounter;
    JLabel paperWinsCounter;
    JLabel scissorsWinsCounter;

    //лэйблы конфигурации
    JLabel modeLabel;
    JLabel velocityCfgLabel;
    JLabel radiusCfgLabel;
    JLabel spawnCfgLabel;
    JLabel skinCfgLabel;
    JLabel maxIterationCountLabel;

    //слайдеры коэффициента скорости объектов
    JSlider velocitySlider;
    JSlider rockVelocitySlider;
    JSlider paperVelocitySlider;
    JSlider scissorsVelocitySlider;

    //лэйбл для отображения перетаскиваемой иконки
    private JLabel draggedLabel;
    private boolean dragging = false;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ControlPanel(MainFrame mainFrame, MainPanel mainPanel){

        this.mainFrame = mainFrame;
        this.mainPanel = mainPanel;

        //инициализация и настройка кнопок управления
        initControlButtons();

        //инициализация и настройка кнопок добавления
        initAddingButtons();

        //инициализация счетчиков
        initCountersLabels();

        //инициализация лэйблов конфигурации
        modeLabel = new JLabel();
        velocityCfgLabel = new JLabel();
        radiusCfgLabel = new JLabel();
        spawnCfgLabel = new JLabel();
        skinCfgLabel = new JLabel();
        maxIterationCountLabel = new JLabel("0");

        //инициализация и настройка слайдеров
        initVelocitySliders();

        //настройка расположения эелментов
        configLayout();

        setBackground(Color.DARK_GRAY);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать кнопки контроля
    private void initControlButtons(){

        configButton = new JButton("Set configuration");

        startButton = new JButton(new ImageIcon(ControlPanel.class.getResource("/imgs/Control_Icons/Start.png")));
        stopButton = new JButton(new ImageIcon(ControlPanel.class.getResource("/imgs/Control_Icons/Stop.png")));
        playButton = new JButton(new ImageIcon(ControlPanel.class.getResource("/imgs/Control_Icons/Play.png")));
        pauseButton = new JButton(new ImageIcon(ControlPanel.class.getResource("/imgs/Control_Icons/Pause.png")));

        List<JButton> controlButtons = new ArrayList<>();
        controlButtons.add(startButton);
        controlButtons.add(stopButton);
        controlButtons.add(playButton);
        controlButtons.add(pauseButton);

        for(JButton button : controlButtons){

            button.setEnabled(false);

            //установить фиксированные размеры
            button.setPreferredSize(new Dimension(63, 35));
            button.setMaximumSize(new Dimension(63, 35));
            button.setMinimumSize(new Dimension(63, 35));
        }

        //добавить обработчик нажатия на кнопку конфигурации
        configButton.addActionListener(e ->{
            ConfigurationFrame configurationFrame = new ConfigurationFrame(mainFrame, mainPanel, this);
            mainFrame.setEnabled(false);
            configurationFrame.setVisible(true);
        });

        //добавить обработчик нажатия на кнопку старта
        startButton.addActionListener(e->{

            mainPanel.start();
            for(Component component : this.getComponents())
                component.setEnabled(true);
            startButton.setEnabled(false);
            configButton.setEnabled(false);
            playButton.setEnabled(false);
            updateUnitsCounters();
            updateIterWinsCounters();
        });

        //добавить обработчик нажатия на кнопку стопа
        stopButton.addActionListener(e ->{

            mainPanel.stop();

            iterationsCounter.setText("0");
            rockWinsCounter.setText("0");
            paperWinsCounter.setText("0");
            scissorsWinsCounter.setText("0");
        });

        //добавить обработчик нажатия на кнопку плэй
        playButton.addActionListener(e->{
            mainPanel.getMainTimer().start();
            playButton.setEnabled(false);
            pauseButton.setEnabled(true);
        });

        //добавить обработчик нажатия на кнопку паузы
        pauseButton.addActionListener(e->{
            mainPanel.getMainTimer().stop();
            playButton.setEnabled(true);
            pauseButton.setEnabled(false);
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //изменить инзменить интерфейс в соответствии с остановкой симуляции
    void stop(){

        for(Component component : this.getComponents())
            component.setEnabled(false);
        configButton.setEnabled(true);
        startButton.setEnabled(true);

        velocitySlider.setValue(1);
        rockVelocitySlider.setValue(1);
        paperVelocitySlider.setValue(1);
        scissorsVelocitySlider.setValue(1);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //иницилизировать кнопки добавления
    private void initAddingButtons(){

        rockButton = new JButton();
        paperButton = new JButton();
        scissorsButton = new JButton();
        rpsButton = new JButton();

        setAddingButtonsIcons(Configuration.SkinConfig.Default);

        List<JButton> addingButtons = new ArrayList<>();
        addingButtons.add(rockButton);
        addingButtons.add(paperButton);
        addingButtons.add(scissorsButton);
        addingButtons.add(rpsButton);

        draggedLabel = new JLabel();
        mainFrame.layeredPane.add(draggedLabel, JLayeredPane.DRAG_LAYER);
        draggedLabel.setSize(30,30);
        draggedLabel.setVisible(false);


        //добавить слушатели кнопкам добавления
        for(JButton button : addingButtons){

            button.setEnabled(false);

            button.addMouseListener(new MouseAdapter() {

                //нажатие
                @Override
                public void mousePressed(MouseEvent e) {
                    if(!button.isEnabled()) return;
                    dragging = false;
                    draggedLabel.setIcon(button.getIcon());
                }

                //отпускание
                @Override
                public void mouseReleased(MouseEvent e) {

                    if(!button.isEnabled()) return;

                    draggedLabel.setVisible(false);

                    if(Unit.getAllUnits().size() == mainPanel.MAX_UNITS) return;

                    Configuration configuration = mainPanel.getConfiguration();

                    Unit unit = null;

                    //создать новый объект в соответствии с кнопкой
                    if(button == rockButton)
                        unit = new Rock();
                    else if(button == paperButton)
                        unit = new Paper();
                    else if(button == scissorsButton)
                        unit = new Scissors();
                    else if(button == rpsButton){
                        Random random = new Random();
                        int rndNum = random.nextInt(3);
                        unit = switch (rndNum) {
                            case 0:
                                yield new Rock();
                            case 1:
                                yield new Paper();
                            case 2:
                                yield new Scissors();
                            default:
                                yield  null;
                        };
                    }

                    //установить значения
                    mainPanel.setUnitConfigVelocity(unit, configuration);
                    mainPanel.setUnitConfigRadius(unit, configuration);
                    mainPanel.setUnitConfigSpawn(unit, Configuration.SpawnConfig.Random);

                    //если курсор зажат и перетаскивается
                    if(dragging) {
                        if(!button.isEnabled()) return;
                        //установить местоположение объекта по координатам курсора
                        Point point = SwingUtilities.convertPoint(button, e.getPoint(), mainPanel);
                        if(mainPanel.hexagon.contains(point.x, point.y))
                            unit.setLocation(point.x, point.y);
                        else
                            return;
                    }

                    //обновить счетчики
                    updateUnitsCounters();
                    mainPanel.repaint();
                }
            });

            //перетаскивание
            button.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (!dragging) {
                        dragging = true;
                        draggedLabel.setVisible(true);
                    }
                    Point point = SwingUtilities.convertPoint(button, e.getPoint(), mainFrame.layeredPane);
                    draggedLabel.setLocation(point.x - Rock.centerOffset, point.y - Rock.centerOffset);
                }
            });

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить иконки на кнопки добавления
    public void setAddingButtonsIcons(Configuration.SkinConfig skinConfig){

        switch (skinConfig){
            case Default:
                rockButton.setIcon(new ImageIcon(Rock.imageDefault.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                paperButton.setIcon(new ImageIcon(Paper.imageDefault.getScaledInstance(30,30,Image.SCALE_SMOOTH)));
                scissorsButton.setIcon(new ImageIcon(Scissors.imageDefault.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                rpsButton.setIcon(new ImageIcon(ControlPanel.class.getResource("/imgs/Skins/RPS/DefaultRPS.png")));
                break;
            case Hand:
                rockButton.setIcon(new ImageIcon(Rock.imageHand.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                paperButton.setIcon(new ImageIcon(Paper.imageHand.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                scissorsButton.setIcon(new ImageIcon(Scissors.imageHand.getScaledInstance(30, 30, Image.SCALE_SMOOTH)));
                rpsButton.setIcon(new ImageIcon(ControlPanel.class.getResource("/imgs/Skins/RPS/HandRPS.png")));
                break;
            default:
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать лэйблы-счетчики
    private void initCountersLabels(){

        rockCounter = new JLabel(":0");
        paperCounter = new JLabel(":0");
        scissorsCounter = new JLabel(":0");
        totalCounter = new JLabel(":0");

        iterationsCounter = new JLabel("0");

        rockWinsCounter = new JLabel("0");
        paperWinsCounter = new JLabel("0");
        scissorsWinsCounter = new JLabel("0");

        rockCounter.setHorizontalAlignment(SwingConstants.CENTER);
        paperCounter.setHorizontalAlignment(SwingConstants.CENTER);
        scissorsCounter.setHorizontalAlignment(SwingConstants.CENTER);
        totalCounter.setHorizontalAlignment(SwingConstants.CENTER);
        iterationsCounter.setHorizontalAlignment(SwingConstants.CENTER);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать слайдеры коэффициентов скорости
    private void initVelocitySliders(){

        velocitySlider = new JSlider(1,3, 1);
        rockVelocitySlider = new JSlider(1, 3, 1);
        paperVelocitySlider = new JSlider(1,3,1);
        scissorsVelocitySlider = new JSlider(1,3,1);

        List<JSlider> sliders = new ArrayList<>();
        sliders.add(velocitySlider);
        sliders.add(rockVelocitySlider);
        sliders.add(paperVelocitySlider);
        sliders.add(scissorsVelocitySlider);


        for(JSlider slider: sliders) {
            slider.setEnabled(false);
            slider.setMajorTickSpacing(1);
            slider.setPaintTicks(true);
            slider.setPaintLabels(true);
        }

        //добавить слайдерам обработчики изменения
        velocitySlider.addChangeListener(e -> {
            Unit.setVelocityRatio(velocitySlider.getValue());
        });

        rockVelocitySlider.addChangeListener(e -> {
            Rock.setRockVelocityRatio(rockVelocitySlider.getValue());
        });

        paperVelocitySlider.addChangeListener(e->{
            Paper.setPaperVelocityRatio(paperVelocitySlider.getValue());
        });

        scissorsVelocitySlider.addChangeListener(e->{
            Scissors.setScissorsVelocityRatio(scissorsVelocitySlider.getValue());
        });

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //метод настройки расположения элементов
    private void configLayout(){

        setLayout(new GridBagLayout());

        GridBagConstraints grid = new GridBagConstraints();
        grid.anchor = GridBagConstraints.NORTH;
        grid.fill = GridBagConstraints.HORIZONTAL;

        grid.gridheight = 1;
        grid.weightx = 1.0;
        grid.weighty = 0;

        //кнопки управления
        grid.gridwidth = 4;
        grid.gridy = 0;
        grid.gridx = 0;
        add(configButton, grid);

        grid.gridwidth = 1;
        grid.gridy = 1;
        add(startButton, grid);

        grid.gridx = 1;
        add(stopButton, grid);

        grid.gridx = 2;
        add(playButton, grid);
        grid.gridx = 3;
        add(pauseButton, grid);

        //надпись добавления
        grid.gridwidth = 4;
        grid.gridx = 0;
        grid.gridy = 2;;
        add(new JLabel("Adding:"), grid);

        //кнопки добавления
        grid.gridwidth = 1;
        grid.gridy = 3;
        grid.gridx = 0;
        add(rockButton, grid);

        grid.gridx = 1;
        add(paperButton, grid);

        grid.gridx = 2;
        add(scissorsButton, grid);

        grid.gridx = 3;
        add(rpsButton, grid);

        //счетчики
        grid.gridy = 4;
        grid.gridx = 0;
        grid.gridwidth = 1;
        add(rockCounter, grid);

        grid.gridx = 1;
        add(paperCounter, grid);

        grid.gridx = 2;
        add(scissorsCounter, grid);

        grid.gridx = 3;
        add(totalCounter, grid);

        //надпись коэффициента скорости
        grid.gridx = 0;
        grid.gridy = 5;
        grid.gridwidth = 5;
        add(new JLabel("Velocity Ratio:"), grid);

        //слайдеры коэффициентов скорости
        grid.gridwidth = 1;
        grid.gridy = 6;
        add(rockVelocitySlider, grid);

        grid.gridx = 1;
        add(paperVelocitySlider, grid);

        grid.gridx = 2;
        add(scissorsVelocitySlider, grid);

        grid.gridx = 3;
        add(velocitySlider, grid);

        //отступ
        grid.gridx = 0;
        grid.gridy = 7;
        grid.gridwidth = 4;
        add(new JLabel(" "), grid);

        //надписи, показывающие текущую конфигурацию
        grid.gridy = 8;
        add(new JLabel("Configuration:"), grid);

        grid.gridy = 9;
        grid.gridwidth = 2;
        add(new JLabel("Mode:"), grid);

        grid.gridx = 2;
        add(modeLabel, grid);

        grid.gridy = 10;
        grid.gridx = 0;
        add(new JLabel("Spawn:"),grid);

        grid.gridx = 2;
        add(spawnCfgLabel, grid);

        grid.gridy = 11;
        grid.gridx = 0;
        add(new JLabel("Velocity:"), grid);

        grid.gridx = 2;
        add(velocityCfgLabel, grid);

        grid.gridy = 12;
        grid.gridx = 0;
        add(new JLabel("Radius:"), grid);

        grid.gridx = 2;
        add(radiusCfgLabel, grid);

        grid.gridy = 13;
        grid.gridx = 0;
        add(new JLabel("Skins:"), grid);

        grid.gridx = 2;
        add(skinCfgLabel, grid);

        grid.gridwidth = 4;
        grid.gridy = 14;
        grid.gridx = 0;
        add(new JLabel(" "), grid);

        grid.gridwidth = 1;
        grid.gridy = 15;
        grid.gridx = 0;
        add(new JLabel("Iteration"), grid);

        grid.gridx = 1;
        add(iterationsCounter, grid);

        grid.gridx = 2;
        add(new JLabel("of"), grid);

        grid.gridx = 3;
        add(maxIterationCountLabel, grid);

        grid.gridwidth = 4;
        grid.gridy = 16;
        grid.gridx = 0;
        add(new JLabel(" "), grid);

        grid.gridwidth = 1;
        grid.gridy = 17;
        grid.gridx = 0;
        add(new JLabel(" "), grid);

        grid.gridx = 1;
        add(new JLabel("Rocks:"), grid);

        grid.gridx = 2;
        add(new JLabel("Papers:"), grid);

        grid.gridx = 3;
        add(new JLabel("Scissors:"), grid);

        grid.gridy = 18;
        grid.gridx = 0;
        add(new JLabel("Wins:"), grid);

        grid.gridx = 1;
        add(rockWinsCounter, grid);

        grid.gridx = 2;
        add(paperWinsCounter, grid);

        grid.gridx = 3;
        add(scissorsWinsCounter, grid);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //обновить счетчики
    public void updateUnitsCounters(){
        rockCounter.setText(":"+Rock.getAllRocks().size());
        paperCounter.setText(":"+Paper.getAllPapers().size());
        scissorsCounter.setText(":"+Scissors.getAllScissors().size());
        totalCounter.setText(":"+Unit.getAllUnits().size());
    }

    public void updateIterWinsCounters(){

        iterationsCounter.setText(String.valueOf(mainPanel.currentIteration));
        rockWinsCounter.setText(String.valueOf(mainPanel.rockWinsCount));
        paperWinsCounter.setText(String.valueOf(mainPanel.paperWinsCount));
        scissorsWinsCounter.setText(String.valueOf(mainPanel.scissorsWinsCount));
    }

}