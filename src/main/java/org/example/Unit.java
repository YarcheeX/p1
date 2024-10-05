package org.example;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

abstract class Unit implements Cloneable {

    private static List<Unit> allUnits = new ArrayList<>(); //список всех объектов

    protected Point2D.Double center;                        //точка центра объекта
    protected double radius;                                //радиус объекта
    protected double dx;                                    //напрявляющая изменения скорости по оси X
    protected double dy;                                    //направляющая изменения скорости по оси Y
    protected Point2D.Double velocity;                      //вектор скорости
    protected int safety;                                   //прочность объекта
    public static final double defaultRadius = 12.0;        //радиус объекта по умолчанию
    public final static double maxVelocityModule = 1.0;     //максимальный модуль скорости
    private static double velocityRatio = 1.0;              //коэффициент скорости всех объектов

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    protected Unit (){
        radius = dx = dy = 0;
        center = new Point2D.Double(0,0);
        velocity = new Point2D.Double(0, 0);
        allUnits.add(this);
    }

    protected Unit(double x, double y, double radius, double dx, double dy) {
        center = new Point2D.Double(x, y);
        velocity = new Point2D.Double();
        this.radius = radius;
        this.dx = dx;
        this.dy = dy;
        setVelocity(dx, dy);
        allUnits.add(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить список всех юнитов
    public static List<Unit> getAllUnits(){
        return allUnits;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //метод клонирования
    @Override
    public Object clone() throws CloneNotSupportedException{

        Unit copy = (Unit) super.clone();
        copy.center = (Point2D.Double)center.clone();
        return copy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //абстрактный метод для реализации отрисовки
    public abstract void draw(Graphics2D g2d);

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //движение объекта
    public void move() {
        center.x += velocity.x;
        center.y += velocity.y;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //обработать столкновение с шестиугольником
    public void calculateHexagonCollision(Hexagon hexagon) {

        Line2D.Double[] sides = hexagon.getSides();
        for (int i = 0; i < 6; i++) {
            if ( sides[i].ptSegDist(center) <= radius) {
                double nx = (sides[i].getY2() - sides[i].getY1());
                double ny = (sides[i].getX1() - sides[i].getX2());
                double nLength1 = Math.sqrt(nx * nx + ny * ny);

                //нормализация вектора нормали
                nx /= nLength1;
                ny /= nLength1;

                //скалярное произведение
                double dotProduct = dx * nx + dy * ny;

                //отражение Vотр = V - 2(V*n)*n
                double newdx = dx - 2.0 * dotProduct * nx;
                double newdy = dy - 2.0 * dotProduct * ny;

                if(sides[(i+1)%6].ptSegDist(center) <= radius) {

                    nx = (sides[(i+1)%6].getY2() - sides[(i+1)%6].getY1());
                    ny = (sides[(i+1)%6].getX1() - sides[(i+1)%6].getX2());

                    double[] resDxDy = calculateAngleCollision(nx, ny, newdx, newdy);
                    newdx = resDxDy[0];
                    newdy = resDxDy[1];
                } else if (i == 0 && sides[5].ptSegDist(center) <= radius) {

                    nx = (sides[5].getY2() - sides[5].getY1());
                    ny = (sides[5].getX1() - sides[5].getX2());

                    double[] resDxDy = calculateAngleCollision(nx, ny, newdx, newdy);
                    newdx = resDxDy[0];
                    newdy = resDxDy[1];
                }

                setVelocity(newdx, newdy);
                break;

            }

        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //обработать попадание объекта в угол
    private double[] calculateAngleCollision(double nx, double ny, double newdx1, double newdy1){

        double length2 = Math.sqrt(nx * nx + ny * ny);

        nx /= length2;
        ny /= length2;

        double dotProduct = dx * nx + dy * ny;

        double newdx2 = dx - 2.0 * dotProduct * nx;
        double newdy2 = dy - 2.0 * dotProduct * ny;

        double dlenght = Math.sqrt(dx * dx + dy * dy);
        newdx1 /= dlenght;
        newdy1 /= dlenght;
        newdx2 /= dlenght;
        newdy2 /= dlenght;

        double resdx = newdx1 + newdx2;
        double resdy = newdy1 + newdy2;

        double reslenght = Math.sqrt(resdx*resdx + resdy*resdy);
        resdx = resdx / reslenght * dlenght;
        resdy = resdy / reslenght * dlenght;

        return new double[]{resdx,resdy};
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //проверить столкновение с другими объектами
    public boolean collidesWith(Unit unit){

        double distance = center.distance(unit.getCenter());
        return distance <= (this.radius + unit.radius);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить X координату
    public double getX() {
        return center.x;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить Y координату
    public double getY() {
        return center.y;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить точку центра
    public Point2D.Double getCenter(){
        return (Point2D.Double)center.clone();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить местоположение юнита
    public void setLocation(double x, double y){
        center.setLocation(x,y);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить локацию объекта
    public void setLocation(Point2D.Double point){
        center.setLocation(point);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить радиус юнита
    public double getRadius() {
        return radius;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить радиус
    public void setRadius(double radius){
        this.radius = radius;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить dx
    public double getDx() {
        return dx;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить dy
    public double getDy() {
        return dy;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить dx
    public void setDx(double dx) {
        this.dx = dx;
        setVelocity(dx, dy);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить dy
    public void setDy(double dy) {
        this.dy = dy;
        setVelocity(dx, dy);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //случайный вектор скорости по заданному модулю
    public static Point2D.Double randomDxDy(double modulus){

        Random random = new Random();

        double dx, dy;

        dx = random.nextDouble() * modulus;
        dy = Math.sqrt(modulus*modulus - dx*dx);
        dx *= random.nextBoolean() ? 1.0 : -1.0;
        dy *= random.nextBoolean() ? 1.0 : -1.0;

        return new Point2D.Double(dx,dy);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить вектор скорости
    public Point2D.Double getVelocity(){
        return velocity;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить скорость
    public void setVelocity(double dx, double dy){
        setDx(dx);
        setDy(dy);
        velocity.setLocation(dx * velocityRatio, dy * velocityRatio);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //установить общий коэффициент скорости и изменить скорость
    public static void setVelocityRatio(int ratio){
        velocityRatio = ratio;
        for(Unit unit : allUnits)
            unit.setVelocity(unit.dx, unit.dy);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить прочность юнита
    public int getSafety(){
        return safety;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить юнитом урон при столкновении
    public void takeDamage(){
        safety--;
    }
}