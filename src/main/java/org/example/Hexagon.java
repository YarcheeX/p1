package org.example;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class Hexagon {

    private final int SIDES = 6;                //количество сторон
    private final double Size;                  //размер (длина стороны)
    private final Point2D.Double center;        //координаты центра
    private final Point2D.Double[] vertices;    //массив вершин
    private final Line2D.Double[] sides;        //массив сторон
    private final Point2D.Double[] trianglesCentralPoints; //массив точек появления (центры правильных треугольников, на которые делится шестиугольник)
    private final Path2D.Double frame;          //рамка шестиугольника для проверки нахождения точки внутри

    public Hexagon(double centerX, double centerY, double size) {

        Size = size;

        center = new Point2D.Double(centerX, centerY);

        vertices = new Point2D.Double[SIDES];

        sides = new Line2D.Double[SIDES];

        trianglesCentralPoints = new Point2D.Double[SIDES];

        frame = new Path2D.Double();

        initCalculateHexagon(centerX, centerY, size);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //инициализация и вычисление вершин, сторон и рамки
    private void initCalculateHexagon(double centerX, double centerY, double size) {

        //инициализация вершин и вычисление их координат
        for (int i = 0; i < SIDES; i++) {
            vertices[i] = new Point2D.Double();
            vertices[i].x = centerX + size *  Math.cos(i * 2 * Math.PI / SIDES);
            vertices[i].y = centerY + size *  Math.sin(i * 2 * Math.PI / SIDES);
        }
        //инициалиция сторон и их установка по координатам вершин
        for (int i = 0; i < SIDES; i++) {
            sides[i] = new Line2D.Double(vertices[i], vertices[(i + 1) % SIDES]);
        }
        //построение рамки по координатам
        frame.moveTo(vertices[0].x, vertices[0].y);
        for (int i = 1; i < SIDES; i++) {
            frame.lineTo(vertices[i].x, vertices[i].y);
        }
        frame.closePath();

        //вычисление вычислить координаты центров правильных треугольников
        for (int i = 0; i < SIDES; i++) {

            double spawnX = (vertices[i].x + vertices[(i + 1) % SIDES].x + center.x)/3.0f;
            double spawnY = (vertices[i].y + vertices[(i + 1) % SIDES].y + center.y)/3.0f;

            trianglesCentralPoints[i] = new Point2D.Double(spawnX, spawnY);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //рисование шестиугольника
    public void draw(Graphics2D g2d) {

        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(5));
        for (Line2D.Double side : sides) {
            g2d.draw(side);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public Point2D.Double getCenter(){
        return (Point2D.Double)center.clone();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public double getSize() {
        return Size;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить массив вершин
    public Point2D.Double[] getVertices(){
        Point2D.Double[] points = new Point2D.Double[SIDES];
        for (int i = 0; i < SIDES; i++) {
            points[i] = (Point2D.Double)vertices[i].clone();
        }
        return points;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить получить массив сторон
    public Line2D.Double[] getSides() {
        Line2D.Double[] lines = new Line2D.Double[SIDES];
        for (int i = 0; i < SIDES; i++) {
            lines[i] = (Line2D.Double)sides[i].clone();
        }
        return lines;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить сторону по индексу
    public Line2D.Double getSide(int i){return (Line2D.Double)sides[i%SIDES].clone(); }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить массив центральных точек правильных треугольников
    public Point2D.Double[] getTrianglesCentralPoints(){
        Point2D.Double[] points = new Point2D.Double[SIDES];
        for (int i = 0; i < SIDES; i++) {
            points[i] = (Point2D.Double)trianglesCentralPoints[i].clone();
        }
        return points;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //получить количество сторон
    public int getSidesCount() {
        return SIDES;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //проверить точку на нахождение в шестиугольнике
    public boolean contains(Point2D.Double point) {
        return frame.contains(point);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //проверить координаты на нахождение в шестиугольнике
    public boolean contains(double x, double y){
        return frame.contains(x, y);
    }
}