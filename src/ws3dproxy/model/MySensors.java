/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws3dproxy.model;

import java.awt.Polygon;
import java.awt.geom.Line2D;

/**
 *
 * @author eccastro
 */
public class MySensors {

    //creature position - center of mass (COM)

    public double comX;
    public double comY;
    public double comZ;

    //creature vertices
    public double x1, y1, x2, y2;

    //Creature direction:
    public double pitch;
    /**
     * Ray in the direction of the pitch.
     */
    public Line2D.Double directRay;

    //energy of creature (self)
    public double fuel;
    public double serotonin;
    public double endorphine;
    public double score;

    //visual system: current "field-of-view"
    Polygon FOV;
}
