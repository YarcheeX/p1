package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class ConfigurationFrame extends JFrame {


    MainFrame mainFrame;
    MainPanel mainPanel;
    ControlPanel controlPanel;


    private JPanel comboBoxPanel;
    private JPanel buttonPanel;
    private JPanel spinnerPanel;

    //кнопка подтверждения установки конфигурации и кнопка закрытия
    private JButton applyButton;
    private JButton cancelButton;

    private JComboBox modeComboBox;                 //комбобокс режима
    private JComboBox velocityComboBox;             //комбобокс режима установки скорости
    private JComboBox radiusComboBox;               //комбобокс редима установки радиуса
    private JComboBox spawnComboBox;                //комбобокс режима появления объектов
    private JComboBox skinComboBox;                 //комбобокс установки скинов объектов

    //комбобоксы предуставновленных значений скорости режима одинаковой скорости среди вида
    private JComboBox rockVelocityComboBox;         //скорость камней
    private JComboBox paperVelocityComboBox;        //скорость бумаг
    private JComboBox scissorsVelocityComboBox;     //скорость ножниц
    private List<JComboBox> velocityComboBoxes;     //список этих комбобоксов

    //комбобоксы точек появления режима определенных точек появления
    private JComboBox rockSpawnComboBox;            //точка появления камней
    private JComboBox paperSpawnComboBox;           //точка появления бумаг
    private JComboBox scissorsSpawnComboBox;        //точка появления ножниц
    private List<JComboBox> spawnComboBoxes;
    private Integer[] selectedSpawnItems;           //массив выбранных элементах на этих комбобоксах

    //комбобоксы значений появления режима определенных точек появления
    private JComboBox rockRadiusComboBox;           //радиус камней
    private JComboBox paperRadiusComboBox;          //радиус бумаг
    private JComboBox scissorsRadiusComboBox;       //радиус ножниц
    private List<JComboBox> radiusComboBoxes;

    //спинеры для установки начальных количеств объектов
    private JSpinner rockSpinner;
    private JSpinner paperSpinner;
    private JSpinner scissorsSpinner;
    private JSpinner iterationsSpinner;

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    ConfigurationFrame(MainFrame mainFrame, MainPanel mainPanel, ControlPanel controlPanel) {

        this.mainFrame = mainFrame;
        this.mainPanel = mainPanel;
        this.controlPanel = controlPanel;

        initButtons();
        initComboBoxes();
        initSpiners();
        initPanels();

        //обработка нажатия на крестик закрытия окна
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                mainFrame.setEnabled(true);
            }
        });

        add(comboBoxPanel, BorderLayout.NORTH);
        add(spinnerPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        ToolTipManager.sharedInstance().setDismissDelay(10000);

        setTitle("Configuration Window");
        setSize(450, 280);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Только закрытие окна конфигурации
        setLocationRelativeTo(null);
        setResizable(false);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать кнопки
    private void initButtons(){

        applyButton = new JButton("Apply");
        cancelButton = new JButton("Cancel");

        //добавить слушатель подтверждения установки конфигурации
        applyButton.addActionListener(e -> {

            //создать конфигурацию
            Configuration configuration = new Configuration();

            //сформировать конфигурацию
            configuration.mode = Configuration.Mode.valueOf((String) modeComboBox.getSelectedItem());
            configuration.velocityConfig = Configuration.VelocityConfig.valueOf((String) velocityComboBox.getSelectedItem());
            configuration.spawnConfig = Configuration.SpawnConfig.valueOf((String) spawnComboBox.getSelectedItem());
            configuration.skinConfig = Configuration.SkinConfig.valueOf((String) skinComboBox.getSelectedItem());
            configuration.radiusConfig = Configuration.RadiusConfig.valueOf((String) radiusComboBox.getSelectedItem());


            if(configuration.spawnConfig == Configuration.SpawnConfig.SpawnPoints){
                configuration.rockSpawnPoint = (int)rockSpawnComboBox.getSelectedItem();
                configuration.paperSpawnPoint = (int)paperSpawnComboBox.getSelectedItem();
                configuration.scissorsSpawnPoint = (int)scissorsSpawnComboBox.getSelectedItem();
            }

            if(configuration.velocityConfig == Configuration.VelocityConfig.EqualType) {
                configuration.rockVelocityValue = Configuration.VelocityValue.valueOf((String) rockVelocityComboBox.getSelectedItem());
                configuration.paperVelocityValue = Configuration.VelocityValue.valueOf((String) paperVelocityComboBox.getSelectedItem());
                configuration.scissorsVelocityValue = Configuration.VelocityValue.valueOf((String) scissorsVelocityComboBox.getSelectedItem());
            }

            if(configuration.radiusConfig == Configuration.RadiusConfig.EqualType){
                configuration.rockRadiusValue = Configuration.RadiusValue.valueOf((String) rockRadiusComboBox.getSelectedItem());
                configuration.paperRadiusValue = Configuration.RadiusValue.valueOf((String) paperRadiusComboBox.getSelectedItem());
                configuration.scissorsRadiusValue = Configuration.RadiusValue.valueOf((String) scissorsRadiusComboBox.getSelectedItem());
            }

            configuration.rockCount = (int) rockSpinner.getValue();
            configuration.paperCount = (int) paperSpinner.getValue();
            configuration.scissorsCount = (int) scissorsSpinner.getValue();

            configuration.maxIterationsCount = (int) iterationsSpinner.getValue();

            //установить конфигурацию
            mainPanel.setConfiguration(configuration);

            controlPanel.setAddingButtonsIcons(configuration.skinConfig);
            controlPanel.startButton.setEnabled(true);

            //отобразить конфигурацию
            controlPanel.modeLabel.setText(configuration.mode.toString());
            controlPanel.velocityCfgLabel.setText(configuration.velocityConfig.toString());
            controlPanel.radiusCfgLabel.setText(configuration.radiusConfig.toString());
            controlPanel.spawnCfgLabel.setText(configuration.spawnConfig.toString());
            controlPanel.skinCfgLabel.setText(configuration.skinConfig.toString());
            controlPanel.maxIterationCountLabel.setText(String.valueOf(configuration.maxIterationsCount));

            mainFrame.setEnabled(true);
            dispose(); // Закрываем окно конфигурации

        });

        cancelButton.addActionListener(e -> {
            mainFrame.setEnabled(true);
            dispose();
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать комбобоксы
    private void initComboBoxes(){

        initModeComboBox();
        initSpawnComboBoxes();
        initRadiusComboBoxes();
        initVelocityComboBoxes();
        initSkinComboBox();

        //установить одинаковые размеры комбобоксов для красоты
        Dimension dimension = new Dimension(spawnComboBox.getPreferredSize());
        modeComboBox.setPreferredSize(dimension);
        velocityComboBox.setPreferredSize(dimension);
        radiusComboBox.setPreferredSize(dimension);
        skinComboBox.setPreferredSize(dimension);

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать комбобоксы режимов
    private void initModeComboBox(){

        modeComboBox = new JComboBox<>(new String[]{"Default", "Duplication", "Safety"});

        //установка всплывающих подсказок
        modeComboBox.setToolTipText("Выбор режима взаимодействия юнитов.");
        String[] modeToolTips = {
                "Обычный режим, в котором сильные уничтожают слабых при столкновении.",
                "Режим дуплицирования, в котором слабый превращается в сильного при столкновении.",
                "Режим в котором юниты имеют прочность (уничтожение\n" +
                    "слабого расходует прочность сильного, и при израсходовании\n" +
                    "прочности сильный уничтожается), также соотношение\n" +
                    "размеров и скоростей приближено к реальным."
        };

        modeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0) {
                    list.setToolTipText(modeToolTips[index]);
                }

                return c;
            }
        });

        modeComboBox.addActionListener(e -> {
            //предустановить значания комбобоксов соответсвуя Safety режиму
            if (modeComboBox.getSelectedItem() == "Safety") {

                velocityComboBox.setSelectedItem("EqualType");
                rockVelocityComboBox.setSelectedItem("High");
                paperVelocityComboBox.setSelectedItem("Low");
                scissorsVelocityComboBox.setSelectedItem("Mid");
                velocityComboBox.setEnabled(false);
                for(JComboBox comboBox : velocityComboBoxes)
                    comboBox.setEnabled(false);

                radiusComboBox.setSelectedItem("EqualType");
                rockRadiusComboBox.setSelectedItem("Small");
                paperRadiusComboBox.setSelectedItem("Large");
                scissorsRadiusComboBox.setSelectedItem("Mid");
                radiusComboBox.setEnabled(false);
                for(JComboBox comboBox : radiusComboBoxes)
                    comboBox.setEnabled(false);

            }else if(modeComboBox.getSelectedItem() != "Safety"){

                velocityComboBox.setEnabled(true);
                for(JComboBox comboBox : velocityComboBoxes)
                    comboBox.setEnabled(true);

                radiusComboBox.setEnabled(true);
                for(JComboBox comboBox: radiusComboBoxes)
                    comboBox.setEnabled(true);
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать комбобоксы режима и значений скорости
    private void initVelocityComboBoxes(){

        velocityComboBox = new JComboBox<>(new String[]{"EqualType", "EqualAll", "RandomAll"});

        //установка всплывающих подсказок
        velocityComboBox.setToolTipText("Выбор режима установки скоростей.");
        String[] velocityToolTips = {
                "Одинаковая скорость внутри типа юнитов.\n" +
                        "Нужно выбрать предустановленное значение для соответствующего типа.",
                "Одинаковая скорость всех юнитов.",
                "Случайная скорость всех юнитах в пределах предустановленного модуля."
        };
        velocityComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0) {
                    list.setToolTipText(velocityToolTips[index]);
                }

                return c;
            }
        });

        velocityComboBoxes = new ArrayList<>();

        String[] HighMidLow = new String[]{"High", "Mid", "Low"};
        rockVelocityComboBox = new JComboBox<>(HighMidLow);
        paperVelocityComboBox = new JComboBox<>(HighMidLow);
        scissorsVelocityComboBox = new JComboBox<>(HighMidLow);

        velocityComboBoxes.add(rockVelocityComboBox);
        velocityComboBoxes.add(paperVelocityComboBox);
        velocityComboBoxes.add(scissorsVelocityComboBox);

        velocityComboBox.addActionListener(e->{
            if(velocityComboBox.getSelectedItem() == "EqualType")
                for(JComboBox comboBox : velocityComboBoxes)
                    comboBox.setEnabled(true);
            else if(velocityComboBox.getSelectedItem() != "EqualType")
                for(JComboBox comboBox : velocityComboBoxes) {
                    comboBox.setSelectedItem("High");
                    comboBox.setEnabled(false);
                }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать комбобоксы режима и значений радиусов
    private void initRadiusComboBoxes(){

        radiusComboBox = new JComboBox<>(new String[]{"EqualType", "EqualAll"});

        //установка всплывающих подсказок
        radiusComboBox.setToolTipText("Выбор режима установки радиусов юнитов.");
        String[] radiusToolTips = {
            "Одинаковый радиус внутри типа юнитов.\n" +
                "Нужно выбрать предустановленное значение для соответствующего типа.",
            "Одинаковый радиус всех юнитов."
        };
        radiusComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0) {
                    list.setToolTipText(radiusToolTips[index]);
                }

                return c;
            }
        });

        radiusComboBoxes = new ArrayList<>();

        String[] LargeMediumSmall = new String[]{"Small", "Mid", "Large"};
        rockRadiusComboBox = new JComboBox<>(LargeMediumSmall);
        paperRadiusComboBox = new JComboBox<>(LargeMediumSmall);
        scissorsRadiusComboBox = new JComboBox<>(LargeMediumSmall);

        radiusComboBoxes.add(rockRadiusComboBox);
        radiusComboBoxes.add(paperRadiusComboBox);
        radiusComboBoxes.add(scissorsRadiusComboBox);

        radiusComboBox.addActionListener(e -> {
            if(radiusComboBox.getSelectedItem() == "EqualType")
                for(JComboBox comboBox : radiusComboBoxes)
                    comboBox.setEnabled(true);
            else if(radiusComboBox.getSelectedItem() == "EqualAll"){
                for(JComboBox comboBox : radiusComboBoxes) {
                    comboBox.setSelectedItem("Small");
                    comboBox.setEnabled(false);
                }
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать комбобоксы режима и значений появления объектов
    private void initSpawnComboBoxes(){

        spawnComboBox = new JComboBox<>(new String[]{"SpawnPoints", "Random"});

        //установка всплывающих подсказок
        spawnComboBox.setToolTipText("Выбор режима появления юнитов.");
        String[] spawnToolTips = {
            "Точки появления фиксированы для каждого вида объектов\n" +
                "Нужно выбрать соответствующее значение для каждого типа\n" +
                "(Центры шести равносторонних треугольников, на которые поделен шестиугольник)",
            "Случайные точки появления всех юнитов внутри вписаной окружности шестиугольника"
        };
        spawnComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0) {
                    list.setToolTipText(spawnToolTips[index]);
                }

                return c;
            }
        });

        spawnComboBoxes = new ArrayList<>();
        selectedSpawnItems = new Integer[3];

        //инициализировать JComboBoxes в соответствии с начальными точками появления объектов {0,2,4}
        // и тем, что установленная точка появления в одном будет нодуступна для других
        rockSpawnComboBox = new JComboBox<>(new Integer[]{0,1,3,5});
        paperSpawnComboBox = new JComboBox<>(new Integer[]{1,2,3,5});
        scissorsSpawnComboBox = new JComboBox<>(new Integer[]{1,3,4,5});

        rockSpawnComboBox.setSelectedItem(0);
        paperSpawnComboBox.setSelectedItem(2);
        scissorsSpawnComboBox.setSelectedItem(4);

        spawnComboBoxes.add(rockSpawnComboBox);
        spawnComboBoxes.add(paperSpawnComboBox);
        spawnComboBoxes.add(scissorsSpawnComboBox);

        spawnComboBox.addActionListener(e -> {

            switch ((String)spawnComboBox.getSelectedItem()){

                case "SpawnPoints":
                    for(JComboBox comboBox : spawnComboBoxes)
                        comboBox.setEnabled(true);
                    break;

                default:
                    for(JComboBox comboBox : spawnComboBoxes)
                        comboBox.setEnabled(false);
                    break;
            }
        });

        for (int i = 0; i < 3; i++)
            selectedSpawnItems[i] = (Integer)spawnComboBoxes.get(i).getSelectedItem();

        for(JComboBox comboBox : spawnComboBoxes){
            comboBox.addActionListener(e -> {
                updateSpawnComboBoxes(comboBox);
            });
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void updateSpawnComboBoxes(JComboBox actionComboBox){

        //список для сортировки элементов JComboBox
        List<Integer> items = new ArrayList<>();

        for(JComboBox jComboBox : spawnComboBoxes){
            //если JComboBox не равен JComboBox, в котором произошло событие
            if(!jComboBox.equals(actionComboBox)){

                //достать, сохранить и убрать слушатель, чтобы не было реакции на изменения JComboBox
                ActionListener actionListener = jComboBox.getActionListeners()[0];
                jComboBox.removeActionListener(actionListener);

                //добавить бывший selectedItem и убрать новый
                jComboBox.addItem(selectedSpawnItems[spawnComboBoxes.indexOf(actionComboBox)]);
                jComboBox.removeItem(actionComboBox.getSelectedItem());

                //достать элементы в список
                for (int i = 0; i < jComboBox.getItemCount(); i++)
                    items.add((Integer) jComboBox.getItemAt(i));

                //остортировать
                Collections.sort(items);

                //убрать все элементы из JComboBox
                jComboBox.removeAllItems();

                //поместить элементы обратно в отсортированном порядке
                for (Integer item : items)
                    jComboBox.addItem(item);

                //восстановить selectedItem
                jComboBox.setSelectedItem(selectedSpawnItems[spawnComboBoxes.indexOf(jComboBox)]);

                //восстановить слушатель
                jComboBox.addActionListener(actionListener);

                //очистить список
                items.clear();
            }
        }
        //установить новый selectedItem в соответствующий массив
        selectedSpawnItems[spawnComboBoxes.indexOf(actionComboBox)] = (Integer)actionComboBox.getSelectedItem();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private void initSkinComboBox(){
        skinComboBox = new JComboBox<>(new String[]{"Default", "Hand"});
        skinComboBox.setToolTipText("Выбор предустановленных скинов.");
        String[] skinToolTips = {
                "Скины юнитов соответствующие изображениям обычных предметов.",
                "Скины юнитов соответствующие изображениям жестов руки."
        };
        skinComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (index >= 0) {
                    list.setToolTipText(skinToolTips[index]);
                }

                return c;
            }
        });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать спинеры
    private void initSpiners(){

        rockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 40, 1));
        rockSpinner.setToolTipText("Начальное количество камней. (MAX = 40).");

        paperSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 40, 1));
        paperSpinner.setToolTipText("Начальное количество бумаг. (MAX = 40).");

        scissorsSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 40, 1));
        scissorsSpinner.setToolTipText("Начальное количество ножниц. (MAX = 40).");

        iterationsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10000, 1));
        iterationsSpinner.setToolTipText("Количество итераций (прогонов) симуляции.");
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализировать панели и настроить расположение компонентов
    private void initPanels(){

        comboBoxPanel = new JPanel(new GridBagLayout());
        GridBagConstraints grid = new GridBagConstraints();
        grid.anchor = GridBagConstraints.WEST;

        grid.insets = new Insets(4,4,0,0);

        grid.weightx = 1;
        grid.gridy = 0;

        grid.gridwidth = 1;

        grid.gridx = 0;
        comboBoxPanel.add(new Label("Mode:"), grid);
        grid.gridx = 1;
        comboBoxPanel.add(modeComboBox, grid);
        grid.gridx = 2;
        comboBoxPanel.add(new Label("Rock:"), grid);
        grid.gridx = 3;
        comboBoxPanel.add(new Label("Paper:"), grid);
        grid.gridx = 4;
        comboBoxPanel.add(new Label("Scissors:"), grid);

        grid.gridy = 1;
        grid.gridx = 0;
        comboBoxPanel.add(new Label("Spawns:"), grid);
        grid.gridx = 1;
        comboBoxPanel.add(spawnComboBox, grid);
        grid.gridx = 2;
        comboBoxPanel.add(rockSpawnComboBox, grid);
        grid.gridx = 3;
        comboBoxPanel.add(paperSpawnComboBox, grid);
        grid.gridx = 4;
        comboBoxPanel.add(scissorsSpawnComboBox, grid);

        grid.gridy = 2;

        grid.gridx = 0;
        comboBoxPanel.add(new Label("Velocity:"), grid);
        grid.gridx = 1;
        comboBoxPanel.add(velocityComboBox, grid);
        grid.gridx = 2;
        comboBoxPanel.add(rockVelocityComboBox, grid);
        grid.gridx = 3;
        comboBoxPanel.add(paperVelocityComboBox, grid);
        grid.gridx = 4;
        comboBoxPanel.add(scissorsVelocityComboBox, grid);

        grid.gridy = 3;
        grid.gridx = 0;
        comboBoxPanel.add(new Label("Radius:"), grid);
        grid.gridx = 1;
        comboBoxPanel.add(radiusComboBox, grid);
        grid.gridx = 2;
        comboBoxPanel.add(rockRadiusComboBox, grid);
        grid.gridx = 3;
        comboBoxPanel.add(paperRadiusComboBox, grid);
        grid.gridx = 4;
        comboBoxPanel.add(scissorsRadiusComboBox, grid);

        grid.gridy = 4;

        grid.gridx = 0;
        comboBoxPanel.add(new Label("Skins:"), grid);
        grid.gridx = 1;
        comboBoxPanel.add(skinComboBox, grid);

        comboBoxPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

        spinnerPanel = new JPanel();
        spinnerPanel.add(new Label("Rocks:"));
        spinnerPanel.add(rockSpinner);
        spinnerPanel.add(new Label("Papers:"));
        spinnerPanel.add(paperSpinner);
        spinnerPanel.add(new Label("Scissors:"));
        spinnerPanel.add(scissorsSpinner);
        spinnerPanel.add(new Label("Iterations: "));
        spinnerPanel.add(iterationsSpinner);
        spinnerPanel.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));

        buttonPanel = new JPanel();
        buttonPanel.add(applyButton);
        buttonPanel.add(cancelButton);
    }
}