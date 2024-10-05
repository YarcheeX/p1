package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Scissors extends Unit {

    final static Image imageDefault = new ImageIcon(Scissors.class.getResource("/imgs/Skins/ScissorsSkins/ScissorsDefault.png")).getImage();
    final static Image imageHand = new ImageIcon(Scissors.class.getResource("/imgs/Skins/ScissorsSkins/ScissorsHand.png")).getImage();


    static Image currentSkin = imageDefault;
    static int centerOffset;
    static double scissorsVelocityRatio = 1.0;

    static List<Scissors> allScissors = new ArrayList<>();

    public Scissors(double x, double y, double radius, double dx, double dy){
        super(x, y, radius, dx, dy);
        safety = 4;
        allScissors.add(this);
    }

    public Scissors(){
        super();
        safety = 4;
        allScissors.add(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setSkin(Configuration configuration) {

        int size = (int)Configuration.getEqualTypePresetRadius(configuration.scissorsRadiusValue) * 2;
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

    public static List<Scissors> getAllScissors(){
        return allScissors;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setScissorsVelocityRatio(int ratio){
        scissorsVelocityRatio = ratio;
        for(Scissors scissors : allScissors)
            scissors.setVelocity(scissors.dx, scissors.dy);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setVelocity(double dx, double dy){
        super.setVelocity(dx, dy);
        velocity.setLocation(velocity.x * scissorsVelocityRatio, velocity.y * scissorsVelocityRatio);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void draw(Graphics2D g2d){
        g2d.drawImage(currentSkin, (int)(center.x - centerOffset), (int)(center.y - centerOffset), null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object clone() throws CloneNotSupportedException{
        Scissors copy = (Scissors) super.clone();
        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void collideScissors(Configuration configuration){

        List<Rock> rocks = Rock.getAllRocks();
        List<Unit> units = Unit.getAllUnits();

        Iterator<Scissors> scissorsIterator = allScissors.iterator();
        while (scissorsIterator.hasNext()) {
            Scissors sci = scissorsIterator.next();

            Iterator<Rock> rockIterator = rocks.iterator();
            while(rockIterator.hasNext()) {
                Rock rock = rockIterator.next();

                if (sci.collidesWith(rock)) {
                    switch(configuration.mode) {
                        case Duplication:
                            if (configuration.velocityConfig == Configuration.VelocityConfig.EqualType) {
                                Rock newRock = new Rock();
                                newRock.setLocation(sci.getCenter());
                                newRock.setRadius(Configuration.getEqualTypePresetRadius(configuration.rockRadiusValue));
                                double rvm = Configuration.getEqualTypePresetVelocity(configuration.rockVelocityValue);
                                double svm = Configuration.getEqualTypePresetVelocity(configuration.scissorsVelocityValue);
                                newRock.setVelocity(sci.getDx()/svm*rvm, sci.getDy()/svm*rvm);
                            }else
                                new Rock(sci.getX(), sci.getY(),
                                        Configuration.getEqualTypePresetRadius(configuration.rockRadiusValue),
                                        sci.getDx(), sci.getDy());
                            break;
                        case Safety :
                            rock.takeDamage();
                            if (rock.getSafety() == 0) {
                                rockIterator.remove();
                                units.remove(rock);
                            }
                            break;
                        default:
                            break;
                    }

                    scissorsIterator.remove();
                    units.remove(sci);
                    break;
                }
            }
        }
    }
}
