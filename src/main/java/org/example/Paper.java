package org.example;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Paper extends Unit {

    final static Image imageDefault = new ImageIcon(Paper.class.getResource("/imgs/Skins/PaperSkins/PaperDefault.png")).getImage();
    final static Image imageHand = new ImageIcon(Paper.class.getResource("/imgs/Skins/PaperSkins/PaperHand.png")).getImage();

    static Image currentSkin = imageDefault;
    static int centerOffset;
    static double paperVelocityRatio = 1.0;

    static List<Paper> allPapers = new ArrayList<>();

    Paper(double x, double y, double radius, double dx, double dy){
        super(x, y, radius, dx, dy);
        safety = 3;
        allPapers.add(this);
    }

    Paper(){
        super();
        safety = 3;
        allPapers.add(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<Paper> getAllPapers(){
        return allPapers;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void setSkin(Configuration configuration){

        int size = (int)Configuration.getEqualTypePresetRadius(configuration.paperRadiusValue) * 2;
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

    public static void setPaperVelocityRatio(int ratio){
        paperVelocityRatio = ratio;
        for(Paper paper : allPapers)
            paper.setVelocity(paper.dx,paper.dy);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void setVelocity(double dx, double dy){
        super.setVelocity(dx, dy);
        velocity.setLocation(velocity.x * paperVelocityRatio, velocity.y * paperVelocityRatio);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void draw(Graphics2D g2d){
        g2d.drawImage(currentSkin, (int)(center.x - centerOffset), (int)(center.y - centerOffset) ,null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Object clone() throws CloneNotSupportedException{

        Paper copy = (Paper) super.clone();
        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static void collidePapers(Configuration configuration){

        List<Scissors> scissors = Scissors.getAllScissors();
        List<Unit> units = Unit.getAllUnits();

        Iterator<Paper> paperIterator = allPapers.iterator();
        while (paperIterator.hasNext()) {
            Paper paper = paperIterator.next();

            Iterator<Scissors> scissorsIterator = scissors.iterator();
            while(scissorsIterator.hasNext()) {
                Scissors sci = scissorsIterator.next();

                if (paper.collidesWith(sci)) {
                    switch (configuration.mode){
                        case Duplication:
                            if (configuration.velocityConfig == Configuration.VelocityConfig.EqualType) {
                                Scissors newScissors = new Scissors();
                                newScissors.setLocation(paper.getCenter());
                                newScissors.setRadius(Configuration.getEqualTypePresetRadius(configuration.scissorsRadiusValue));
                                double svm = Configuration.getEqualTypePresetVelocity(configuration.scissorsVelocityValue);
                                double pvm = Configuration.getEqualTypePresetVelocity(configuration.paperVelocityValue);
                                newScissors.setVelocity(paper.getDx()/pvm*svm, paper.getDy()/pvm*svm);
                            }else
                                new Scissors(paper.getX(), paper.getY(),
                                            Configuration.getEqualTypePresetRadius(configuration.scissorsRadiusValue),
                                            paper.getDx(), paper.getDy());
                            break;
                        case Safety:
                            sci.takeDamage();
                            if(sci.getSafety() == 0) {
                                scissorsIterator.remove();
                                units.remove(sci);
                            }
                            break;
                        default:
                            break;
                    }

                    paperIterator.remove();
                    units.remove(paper);
                    break;
                }
            }
        }
    }
}