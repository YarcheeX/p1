package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Collections;
import java.util.Random;

//класс главной панели главного окна.
//на ней отрисовывается шестиугольник, движущиеся объекты
class MainPanel extends JPanel {

    //конфигурация
    Configuration configuration;

    //задержка в таймере
    private final int MAIN_DELAY = 10;
    private final int CHECK_WIN_DELAY = 500;
    //максимальное кол-во объектов
    public static final int MAX_UNITS = 120;

    //таймеры
    Timer mainTimer;
    Timer checkWinTimer;

    //панель контроля
    ControlPanel controlPanel;

    //списки объектов
    java.util.List<Unit> units;         //все объекты
    java.util.List<Rock> rocks;         //камни
    java.util.List<Paper> papers;       //бумаги
    java.util.List <Scissors> scissors; //ножницы

    //шестиугольник
    Hexagon hexagon;

    int currentIteration = 1;           //счетчик текущей итерации

    int rockWinsCount = 0;              //счетчик побед камней
    int paperWinsCount = 0;             //счетчик побед бумаг
    int scissorsWinsCount = 0;          //счетчик побед ножниц

    int[][] permutationsCollisions = {  //массив перестановок порядка обработки столкновений
            {1,2,3},
            {3,1,2},
            {2,3,1},
            {1,3,2},
            {3,2,1},
            {2,1,3}
    };
    int[] currentPermutation = {1,2,3}; //текущая перестановка
    int currentPermutationIndex = 0;    //индекс текущей перестановки



    //конструктор гланвой панели
    MainPanel(MainFrame mainFrame) {

        controlPanel = new ControlPanel(mainFrame,this);

        //инициализация и настройка событий таймеров
        mainTimer = new Timer(MAIN_DELAY, e -> {
            for(Unit unit : units){
                unit.move();                                //перемещение
                unit.calculateHexagonCollision(hexagon);    //проверить столкновения с шестиугольником
            }
            collideUnits(configuration);                    //проверить столкновения между объектами
            updateUnitsCounters();                          //обновить счетчики
            repaint();                                      //перерисовка
        });

        checkWinTimer = new Timer(CHECK_WIN_DELAY, e -> {
            checkMakeWin();
        });

        //шестиугольник
        hexagon = new Hexagon(400.0, 340.0, 375.0);

        //списки объектов
        units = Unit.getAllUnits();
        rocks = Rock.getAllRocks();
        papers = Paper.getAllPapers();
        scissors = Scissors.getAllScissors();

        //установить фон
        setBackground(Color.GRAY);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установка конфигурации
    public void setConfiguration(Configuration configuration){
        this.configuration = configuration;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить установленную конфигурацию конфигурацию
    public Configuration getConfiguration(){
        return configuration;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить ссылку на панель контроля
    public ControlPanel getControlPanel(){
        return controlPanel;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить главный таймер
    public Timer getMainTimer(){
        return mainTimer;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализирование объектов и старт
    void start(){
        createUnits(configuration);
        setSkins(configuration);
        for(Unit unit : units)
            fillUnit(unit, configuration);
        mainTimer.start();
        checkWinTimer.start();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //стоп
    void stop(){
        mainTimer.stop();
        checkWinTimer.stop();
        controlPanel.stop();
        clearAllUnits();
        updateUnitsCounters();
        repaint();

        currentIteration = 1;
        rockWinsCount = 0;
        paperWinsCount = 0;
        scissorsWinsCount = 0;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //перегрузка метода отрисовки компонентов
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        hexagon.draw(g2d);
        for(Unit unit : units)
            unit.draw(g2d);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //обновить счетчики
    public void updateUnitsCounters(){
        controlPanel.updateUnitsCounters();
    }

    public void updateIterWinsCounters(){
        controlPanel.updateIterWinsCounters();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //обработать столкновения
    private void collideUnits(Configuration configuration){

        for(int i = 0; i < 3; i++){
            switch(currentPermutation[i]){
                case 1:
                    Paper.collidePapers(configuration);
                    break;
                case 2:
                    Scissors.collideScissors(configuration);
                    break;
                case 3:
                    Rock.collideRocks(configuration);
                    break;
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //создать объекты и добавить их в списки
    private void createUnits(Configuration configuration){

        int rockCount = configuration.rockCount;
        int paperCount = configuration.paperCount;
        int scissorsCount = configuration.scissorsCount;

        for(int i = 0; i < scissorsCount; i++)
            new Scissors();

        for(int i = 0; i < paperCount; i++)
            new Paper();

        for (int i = 0; i < rockCount; i++)
            new Rock();

        Collections.shuffle(units);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить скины
    private void setSkins(Configuration configuration){
        Rock.setSkin(configuration);
        Paper.setSkin(configuration);
        Scissors.setSkin(configuration);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить скорость объекта по конфигурации
    public void setUnitConfigVelocity(Unit unit, Configuration configuration){

        Random random = new Random();

        double VelocityModule = 0;

        Point2D.Double V = null;

        switch (configuration.velocityConfig) {

            case RandomAll:
                //получить случайный модуль и по нему получить вектор
                VelocityModule = random.nextDouble() * Unit.maxVelocityModule;
                V = Unit.randomDxDy(VelocityModule);
                break;

            case EqualAll:
                V = Unit.randomDxDy(Unit.maxVelocityModule);
                break;

            case EqualType:
                if(unit instanceof Rock)
                    VelocityModule = Configuration.getEqualTypePresetVelocity(configuration.rockVelocityValue);
                else if(unit instanceof Paper)
                    VelocityModule = Configuration.getEqualTypePresetVelocity(configuration.paperVelocityValue);
                else if(unit instanceof Scissors)
                    VelocityModule = Configuration.getEqualTypePresetVelocity(configuration.scissorsVelocityValue);

                V = Unit.randomDxDy(VelocityModule);
                break;

            default:
                break;
        }

        unit.setVelocity(V.x, V.y);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить радиус объекта по конфигурации
    public void setUnitConfigRadius(Unit unit, Configuration configuration) {

        switch (configuration.radiusConfig) {
            case EqualType:
                if (unit instanceof Rock)
                    unit.setRadius(Configuration.getEqualTypePresetRadius(configuration.rockRadiusValue));
                else if (unit instanceof Paper)
                    unit.setRadius(Configuration.getEqualTypePresetRadius(configuration.paperRadiusValue));
                else if (unit instanceof Scissors)
                    unit.setRadius(Configuration.getEqualTypePresetRadius(configuration.scissorsRadiusValue));
                break;
            case EqualAll:
                unit.setRadius(Unit.defaultRadius);
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить точку появления объекта по конфигурации
    public void setUnitConfigSpawn(Unit unit, Configuration.SpawnConfig spawnConfig){

        Random random = new Random();

        switch(spawnConfig){

            case SpawnPoints:
                Point2D.Double[] spawns = hexagon.getTrianglesCentralPoints();
                if(unit instanceof Rock)
                    unit.setLocation(spawns[configuration.rockSpawnPoint]);

                if(unit instanceof Paper)
                    unit.setLocation(spawns[configuration.paperSpawnPoint]);

                if(unit instanceof Scissors)
                    unit.setLocation(spawns[configuration.scissorsSpawnPoint]);
                break;

            case Random:
                //случайная точка во вписанной окружности щестиугольника
                double Radius = hexagon.getSize()*Math.sqrt(3)/2 - 13;
                Point2D.Double center = hexagon.getCenter();

                double r = Radius * Math.sqrt(random.nextDouble());
                double theta  = random.nextDouble()*2*Math.PI;
                double x = center.x + r * Math.cos(theta);
                double y = center.y + r * Math.sin(theta);

                unit.setLocation(x,y);
                break;

            default:
                break;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить значения объекта
    public void fillUnit(Unit unit, Configuration configuration){
        setUnitConfigVelocity(unit, configuration);
        setUnitConfigRadius(unit, configuration);
        setUnitConfigSpawn(unit, configuration.spawnConfig);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //проверить и выполнить победу
    private void checkMakeWin(){

        //если один из списков пуст, то прибавить победу
        if(rocks.isEmpty())
            scissorsWinsCount++;
        else if (papers.isEmpty())
            rockWinsCount++;
        else if (scissors.isEmpty()){
            paperWinsCount++;
        }
        else
            return;

        //и начать следующую итерацию
        nextIteration();
    }

    //очистить все списки юнитов
    private void clearAllUnits(){
        units.clear();
        rocks.clear();
        papers.clear();
        scissors.clear();
    }

    //переход на следующую итерацию
    private void nextIteration() {

        mainTimer.stop();
        clearAllUnits();

        if (currentIteration < configuration.maxIterationsCount) {

            currentIteration++;
            updateIterWinsCounters();

            currentPermutationIndex = (currentPermutationIndex + 1) % 6;
            currentPermutation = permutationsCollisions[currentPermutationIndex];

            start();
        }else {
            updateIterWinsCounters();
            stop();
        }
    }

}