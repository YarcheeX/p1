package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Rock extends Unit {

    //исходные изображения
    final static Image imageDefault = new ImageIcon(Rock.class.getResource("/imgs/Skins/RockSkins/RockDefault.png")).getImage();
    final static Image imageHand = new ImageIcon(Rock.class.getResource("/imgs/Skins/RockSkins/RockHand.png")).getImage();

    static Image currentSkin = imageDefault;        //установленный скин
    static int centerOffset;                        //смещение для отображения по центру объекта
    static double rockVelocityRatio = 1.0;          //коэффициент скорости камней

    static List<Rock> allRocks = new ArrayList<>(); //список всех камней

    Rock(double x, double y, double radius, double dx, double dy){

        super(x, y, radius, dx, dy);
        this.safety = 5;
        allRocks.add(this);
    }

    Rock(){

        super();
        this.safety = 5;
        allRocks.add(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить список всех камней
    public static List<Rock> getAllRocks(){
        return allRocks;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить скин (изображение камней)
    public static void setSkin(Configuration configuration) {

        int size = (int)Configuration.getEqualTypePresetRadius(configuration.rockRadiusValue) * 2;
        switch (configuration.skinConfig){
            case Default:
                currentSkin = imageDefault.getScaledInstance(size, size, Image.SCALE_SMOOTH);
               break;
           case Hand:
               currentSkin = imageHand.getScaledInstance(size, size, Image.SCALE_SMOOTH);
               break;
           default:
               break;
        }

        centerOffset = size/2;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //перегрузка метода установки скорости
    @Override
    public void setVelocity(double dx, double dy){
        super.setVelocity(dx, dy);
        velocity.setLocation(velocity.x * rockVelocityRatio, velocity.y * rockVelocityRatio);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить коэффициент скорости камней с изменением корости
    public static void setRockVelocityRatio(int ratio){
        rockVelocityRatio = ratio;
        for(Rock rock : allRocks)
            rock.setVelocity(rock.dx, rock.dy);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //метод отрисовки камня
    @Override
    public void draw(Graphics2D g2d){
        g2d.drawImage(currentSkin, (int)(center.x - centerOffset), (int)(center.y - centerOffset),null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //метод клонирования
    @Override
    public Object clone() throws CloneNotSupportedException{
        Rock copy = (Rock) super.clone();
        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //обработать столкновения
    public static void collideRocks(Configuration configuration){

        List<Paper> papers = Paper.getAllPapers();
        List<Unit> units = Unit.getAllUnits();

        Iterator<Rock> rockIterator = allRocks.iterator();           //итератор по камням
        while (rockIterator.hasNext()) {
            Rock rock = rockIterator.next();

            Iterator<Paper> paperIterator = papers.iterator();      //итератор по бумагам
            while (paperIterator.hasNext()) {
                Paper paper = paperIterator.next();

                if (rock.collidesWith(paper)) {                     //если камень столкнулся с бумагой
                    switch (configuration.mode){
                        case Duplication:
                            if(configuration.velocityConfig == Configuration.VelocityConfig.EqualType) {
                                //создать новую бумагу
                                Paper newPaper = new Paper();
                                //установить ее местоположение на месте камня
                                newPaper.setLocation(rock.getCenter());
                                //установить радиус в соответствии с конфигурацией
                                newPaper.setRadius(Configuration.getEqualTypePresetRadius(configuration.paperRadiusValue));
                                //получить модули бумаг и камней
                                double pvm = Configuration.getEqualTypePresetVelocity(configuration.paperVelocityValue);
                                double rvm = Configuration.getEqualTypePresetVelocity(configuration.rockVelocityValue);
                                //установить новую скорость бумаги, сохранив направление, но изменив модуль
                                newPaper.setVelocity(rock.getDx()/rvm*pvm, rock.getDy()/rvm*pvm);
                            }else
                                new Paper(rock.getX(), rock.getY(),
                                        Configuration.getEqualTypePresetRadius(configuration.paperRadiusValue),
                                        rock.getDx(), rock.getDy());
                            break;
                        case Safety:
                            paper.takeDamage();                     //получить урон
                            if (paper.getSafety() == 0) {           //удалить, если прочность == 0
                                paperIterator.remove();
                                units.remove(paper);
                            }
                            break;
                        default:
                            break;
                    }

                    rockIterator.remove();                          //удалить камень из камней
                    units.remove(rock);                             //удалить из всех объектов
                    break;
                }
            }
        }
    }

}
