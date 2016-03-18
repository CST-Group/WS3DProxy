/*****************************************************************************
 * Copyright 2007-2015 DCA-FEEC-UNICAMP
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *    Patricia Rocha de Toro, Elisa Calhau de Castro, Ricardo Ribeiro Gudwin
 *****************************************************************************/
package ws3dproxy.model;

import ws3dproxy.util.Constants;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import ws3dproxy.DrawingThingManager;

/**
 *
 * @author eccastro
 */
public class Thing {

    private final ThingAttributes attributes;

    private boolean fromMemory = false;

    public boolean hidden = false;
    public int ifIsADeliverySot = 0;
//    protected WorldPoint center;
    public WorldPoint[] vertex = new WorldPoint[4]; //formed by waypoints
    public WorldPoint[] vertex_ = new WorldPoint[4]; //no gaps. Probably substitute the previous. 
    //Currently, it happens to be the same as vertex (previous array). It may be different if "GAP" is different from "SEC".
    public WorldPoint[] secAreaVertex = new WorldPoint[4]; //this is the security area around any thing
    private Rectangle2D.Double recSecArea;
    public Rectangle2D.Double recGapArea;
    public Rectangle2D.Double minVSArea;

    private DrawingThingManager drawer;

    /**
     * Used only to "recreate" a "remembered Thing".
     *
     * @param name
     * @param category
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param comX
     * @param comY
     * @param colorName
     */
    public Thing(String name, int category, double x1, double y1, double x2, double y2, double comX, double comY, String colorName, double pitch, double shininess) {

        this(name, category, x1, y1, x2, y2, comX, comY, colorName, 0, pitch, 0, 0, shininess);
        this.fromMemory = true;

    }

    public Thing(String name, int category, double x1, double y1, double x2, double y2, double comX, double comY, String colorName, int isOccluded, double pitch, double hardness, double energy, double shininess) {

        this.attributes = new ThingAttributes(name, category, colorName, x1, y1, x2, y2, comX, comY, pitch, shininess, energy, isOccluded);

        this.drawer = new DrawingThingManager(this);
        this.fromMemory = false;
    }

    //
    public Thing update(double x1, double y1, double x2, double y2, double comX, double comY, String colorName, int isOccluded, double energy, double pitch, double shininess) {
        this.attributes.update(colorName, x1, y1, x2, y2, comX, comY, pitch, energy, isOccluded, shininess);
        this.fromMemory = false;

        return this;
    }

    public ThingAttributes getAttributes() {
        return this.attributes;
    }

    public double getX1() {
        return this.attributes.getX1();
    }

    public double getY1() {
        return this.attributes.getY1();
    }

    public double getX2() {
        return this.attributes.getX2();
    }

    public double getY2() {
        return this.attributes.getY2();
    }

    public double getHeight() {
        return (this.attributes.getY2() - this.attributes.getY1());
    }

    public double getWidth() {
        return (this.attributes.getX2() - this.attributes.getX1());
    }

    public void setX1(double x1) {
        this.attributes.setX1(x1);
    }

    public void setY1(double y1) {
        this.attributes.setY1(y1);
    }

    public void setX2(double x2) {
        this.attributes.setX2(x2);
    }

    public void setY2(double y2) {
        this.attributes.setY2(y2);
    }

    public int getCategory() {
        return this.attributes.getCategory();
    }

    public boolean getIfFromMemory() {
        return this.fromMemory;
    }

    public DrawingThingManager getDrawer() {
        return this.drawer;
    }

    public Material3D getMaterial() {
        return this.attributes.getMaterial3D();
    }

    //public abstract void setID(Long id);
    public void setName(String id) {
        this.attributes.setName(id);
    }

    public String getName() {
        return this.attributes.getName();
    }

    //currently not in use!
    public boolean containsPoint(WorldPoint p) {
        double xx = p.getX();
        double yy = p.getY();
        return this.containsPoint(xx, yy);
        //return this.withinBoundingArea(xx, yy);
    }

    public boolean containsPoint(double x, double y) {
        return (this.getX1() <= x) && (this.getX2() >= x) && (this.getY1() <= y) && (this.getY2() >= y);

    }

    /**
     * Checks if point is inside the security area around (and including) the
     * thing.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean innerWayPointAreaContainsPoint(double x, double y) {

        WorldPoint[] points = this.initAndGetWayPoints();
        this.getSecArea();

        for (int i = 0; i < points.length; i++) {
            if (points[i].getX() == x && points[i].getY() == y) {
                return false;
            }
        }

        //System.out.println("------- contains= " + this.recGapArea.contains(x, y));
        //return this.recGapArea.contains(x, y);
        //System.out.println("------- contains= " + this.recSecArea.contains(x, y));
        return this.getSecArea().contains(x, y);
    }

    /**
     * Checks if point is inside the gap area around (and including) the thing.
     * Its vertex (or waypoints) ARE considered.
     *
     * @param x
     * @param y
     * @return
     */
    public boolean isInsideGapArea(double x, double y) {

        // initAndGetWayPoints();
        boolean checkBoundary = false;
        WorldPoint[] points = this.initAndGetWayPoints();
        for (int i = 0; i < points.length; i++) {
            if (points[i].getX() == x && points[i].getY() == y) {
                checkBoundary = true;
            }
        }
        if (checkBoundary) {
            return true;
        } else {
            return this.recGapArea.contains(x, y);
        }
//        this.initAndGetWayPoints();
//
        //System.out.println("---isMyWaypoint---- contains= " + this.recGapArea.contains(x, y)+ " x= "+x+" y= "+y);

        //return (points[0].getX() <= x) && (points[3].getX() >= x) && ( points[0].getY()<= y) && (points[3].getY() >= y);
    }

    /**
     * Currently not in use. Use: innerWayPointAreaContainsPoint instead
     *
     * @param x
     * @param y
     * @return
     */
    public boolean withinBoundingArea(double x, double y) {
        double xx1, xx2, yy1, yy2;
        xx1 = this.getX1() - Constants.GAP;
        xx2 = this.getX2() + Constants.GAP;
        yy1 = this.getY1() - Constants.GAP;
        yy2 = this.getY2() + Constants.GAP;

        return (xx1 <= x) && (xx2 >= x) && (yy1 <= y) && (yy2 >= y);

    }

    public synchronized WorldPoint getCenterPosition() {
        return attributes.getCOM();
    }

//    public synchronized void setCenterPosition(WorldPoint c) {
//        center = c;
//    }
    public synchronized WorldPoint[] initAndGetWayPoints() {

        double gap = Constants.GAP;

        vertex[0] = new WorldPoint(this.getX1() - gap, this.getY1() - gap); //upper left
        vertex[1] = new WorldPoint(this.getX1() - gap, this.getY2() + gap);
        vertex[2] = new WorldPoint(this.getX2() + gap, this.getY1() - gap);
        vertex[3] = new WorldPoint(this.getX2() + gap, this.getY2() + gap);

        // vertex[1] is the upper left of the gap area
        double height = Math.abs(vertex[3].getX() - vertex[1].getX());
        double width = Math.abs(vertex[1].getY() - vertex[0].getY());

        this.recGapArea = new Rectangle2D.Double(vertex[0].getX(), vertex[0].getY(), height, width);

        return vertex;
    }

    public synchronized WorldPoint[] getVertex() {
        vertex_[0] = new WorldPoint(this.getX1(), this.getY1()); //upper left
        vertex_[1] = new WorldPoint(this.getX1(), this.getY2());
        vertex_[2] = new WorldPoint(this.getX2(), this.getY1());
        vertex_[3] = new WorldPoint(this.getX2(), this.getY2());

        return vertex_;

    }

    public synchronized Rectangle2D.Double getSecArea() {

        WorldPoint[] insideSecArea = new WorldPoint[4];

        insideSecArea[0] = new WorldPoint(this.getX1() - Constants.SEC, this.getY1() - Constants.SEC);
        insideSecArea[1] = new WorldPoint(this.getX1() - Constants.SEC, this.getY2() + Constants.SEC);
        insideSecArea[2] = new WorldPoint(this.getX2() + Constants.SEC, this.getY1() - Constants.SEC);
        insideSecArea[3] = new WorldPoint(this.getX2() + Constants.SEC, this.getY2() + Constants.SEC);

        double height = Math.abs(insideSecArea[3].getX() - insideSecArea[1].getX());
        double width = Math.abs(insideSecArea[1].getY() - insideSecArea[0].getY());

        this.recSecArea = new Rectangle2D.Double(insideSecArea[0].getX(), insideSecArea[0].getY(), height, width);
        initMinimumVSArea();
        return this.recSecArea;
    }

    public synchronized boolean secAreaContain(double x, double y) {
        this.getSecArea();
        return this.getSecArea().contains(x, y);
    }

    public void initMinimumVSArea() {

        double factor = 5;

        WorldPoint[] minimumVSArea = new WorldPoint[4];

        minimumVSArea[0] = new WorldPoint(this.getX1() + factor, this.getY1() + factor);
        minimumVSArea[1] = new WorldPoint(this.getX1() + factor, this.getY2() - factor);
        minimumVSArea[2] = new WorldPoint(this.getX2() - factor, this.getY1() + factor);
        minimumVSArea[3] = new WorldPoint(this.getX2() - factor, this.getY2() - factor);

        double height = Math.abs(minimumVSArea[3].getX() - minimumVSArea[1].getX());
        double width = Math.abs(minimumVSArea[1].getY() - minimumVSArea[0].getY());

        this.minVSArea = new Rectangle2D.Double(minimumVSArea[0].getX(), minimumVSArea[0].getY(), height, width);

    }

    public synchronized List<Line2D.Double> getAreaSides() {
        List<Line2D.Double> sides = new ArrayList<Line2D.Double>();
        try {
            final PathIterator areaIter = this.attributes.shape.getPathIterator(null); //Getting an iterator along the polygon path
            final double[] coords = new double[6]; //Double array of length 6 needed by iterator
            final double[] firstCoords = new double[2]; //First point (needed to close the area)
            final double[] lastCoords = new double[2]; //Previously visited point
            if (!areaIter.isDone()) {
                areaIter.currentSegment(firstCoords); //Getting the first coordinate pair
            }
            lastCoords[0] = firstCoords[0]; //previous coordinate pair
            lastCoords[1] = firstCoords[1];
            areaIter.next();
            while (!areaIter.isDone()) {
                final int type = areaIter.currentSegment(coords);
                switch (type) {
                    case PathIterator.SEG_LINETO: {
                        sides.add(new Line2D.Double(lastCoords[0], lastCoords[1], coords[0], coords[1]));
                        lastCoords[0] = coords[0];
                        lastCoords[1] = coords[1];
                        break;
                    }
                    case PathIterator.SEG_CLOSE: {
                        //simply close the area
                        break;
                    }
                    default: {
                        //segment type not supported
                    }
                }
                areaIter.next();
            }
        } catch (NoSuchElementException ex) {
            System.err.println("_____NoSuchElementException");

        } finally {//no matter what even empty
            return sides;
        }

    }

    private Color translateIntoColor(String name) {
        return Constants.translateIntoColor(name);
    }

    public String toString() {
        return this.getName();
    }

}
