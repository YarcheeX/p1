package org.example;

class Configuration {

    enum Mode {
        Default,        //обычный режим, в котором сильные уничтожают слабых при столкновении
        Duplication,    //режим дуплицирования, в котором слабый превращается в сильного при столкновении
        Safety          //режим в котором объекты имеют прочность (уничтожение слабого расходует прочность сильного,
    }                   //и при израсходовании прочности сильный уничтожается), также соотношение размеров приближено к реальным


    enum SpawnConfig {
        SpawnPoints,    //режим, в котором точки появления фиксированы для каждого вида объектов
                        //(центры равносторонних треугольников, на которые поделен шестиугольник)
        Random         //случайные точки появления каждого объекта
    }

    enum VelocityConfig {
        RandomAll,      //случайные скорости всех объектов
        EqualType,      //одинаковые скорости среди вида
        EqualAll        //одинаковые скорости всех объектов
    }

    enum RadiusConfig{
        EqualType,      //одинаковые радиусы среди видов
        EqualAll        //одинаковые радиусы всех объектов
    }

    enum SkinConfig{
        Default,        //обычные изображения камней ножниц и бумаг
        Hand            //изображения жестов руки
    }

    enum VelocityValue{
        //предустановленные значения скоростей
        High, Mid, Low
    }

    enum RadiusValue{
        //предустановленные значения радиусов
        Large, Mid, Small
    }

    public static final double smallUnitRadius = Unit.defaultRadius;
    public static final double midUnitRadius = smallUnitRadius * 1.25;
    public static final double largeUnitRadius = smallUnitRadius * 1.5;

    public static final double highUnitVelocity = Unit.maxVelocityModule;
    public static final double midUnitVelocity = highUnitVelocity * 0.67;
    public static final double lowUnitVelocity = highUnitVelocity * 0.45;

    public Mode mode = null;
    public SpawnConfig spawnConfig = null;
    public VelocityConfig velocityConfig = null;
    public RadiusConfig radiusConfig = null;
    public SkinConfig skinConfig = null;

    public VelocityValue rockVelocityValue = VelocityValue.High;
    public VelocityValue paperVelocityValue = VelocityValue.High;
    public VelocityValue scissorsVelocityValue = VelocityValue.High;

    public RadiusValue rockRadiusValue = RadiusValue.Small;
    public RadiusValue paperRadiusValue = RadiusValue.Small;
    public RadiusValue scissorsRadiusValue = RadiusValue.Small;

    public int rockSpawnPoint = 0;
    public int paperSpawnPoint = 0;
    public int scissorsSpawnPoint = 0;

    public int rockCount = 0;
    public int paperCount = 0;
    public int scissorsCount = 0;

    public int maxIterationsCount = 1;


    Configuration(){}

    Configuration(Configuration configuration){

        mode = configuration.mode;
        spawnConfig = configuration.spawnConfig;
        velocityConfig = configuration.velocityConfig;
        skinConfig = configuration.skinConfig;
        radiusConfig = configuration.radiusConfig;

        rockSpawnPoint = configuration.rockSpawnPoint;
        paperSpawnPoint = configuration.paperSpawnPoint;
        scissorsSpawnPoint = configuration.scissorsSpawnPoint;

        rockVelocityValue = configuration.rockVelocityValue;
        paperVelocityValue = configuration.paperVelocityValue;
        scissorsVelocityValue = configuration.scissorsVelocityValue;

        rockRadiusValue = configuration.rockRadiusValue;
        paperRadiusValue = configuration.paperRadiusValue;
        scissorsRadiusValue = configuration.scissorsRadiusValue;

        maxIterationsCount = configuration.maxIterationsCount;

        rockCount = configuration.rockCount;
        paperCount = configuration.paperCount;
        scissorsCount = configuration.scissorsCount;
    }

    //получить предустановленное значение скорости режима EqualType
    public static double getEqualTypePresetVelocity(Configuration.VelocityValue velocityValue){
        return switch(velocityValue){
            case Low -> lowUnitVelocity;
            case Mid -> midUnitVelocity;
            case High -> highUnitVelocity;
        };
    }

    //получить предустановленное значение радиуса режима EqualType
    public static double getEqualTypePresetRadius(Configuration.RadiusValue radiusValue){
        return switch(radiusValue){
            case Small -> smallUnitRadius;
            case Mid -> midUnitRadius;
            case Large -> largeUnitRadius;
        };
    }
}