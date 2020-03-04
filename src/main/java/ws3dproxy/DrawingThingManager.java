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
package ws3dproxy;

import ws3dproxy.model.Thing;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import ws3dproxy.util.Constants;


/**
 *
 * @author eccastro
 */
public class DrawingThingManager {

    private Thing aThing;
    private Graphics2D g2D;

    public DrawingThingManager(Thing th) {
        aThing = th;
    }

    public synchronized void draw(Graphics g, Graphics2D g2D, Color backgroundColor) {
        this.g2D = g2D;
        if (aThing.getIfFromMemory()) {
            drawMe(g, backgroundColor);
        } else {
            drawMe(this.g2D);
        }

    }

    private void drawMe(Graphics2D g2D) {
        g2D.setColor(aThing.getAttributes().getMaterial3D().getColor());
        switch (aThing.getAttributes().getCategory()) {

            case Constants.categoryCREATURE:
                g2D.fill(new RoundRectangle2D.Double((int) aThing.getAttributes().getCOM().getX() - Constants.CREATURE_SIZE/2, (int) aThing.getAttributes().getCOM().getY() - Constants.CREATURE_SIZE/2, Constants.CREATURE_SIZE, Constants.CREATURE_SIZE, Constants.CREATURE_SIZE/4, Constants.CREATURE_SIZE/4));

                //g2D.draw(aThing.getAttributes().getShape());
                break;
            case Constants.categoryBRICK:
                g2D.fill(new Rectangle2D.Double((int) aThing.getAttributes().getX1(), (int) aThing.getAttributes().getY1(), (int) aThing.getAttributes().getX2()
                        - (int) aThing.getX1(), (int) aThing.getY2() - (int) aThing.getY1()));
                break;
            case Constants.categoryPFOOD:
                g2D.fill(new Ellipse2D.Double((int) aThing.getAttributes().getX1(), (int) aThing.getAttributes().getY1(), 1+Constants.FOOD_SIZE, 1+Constants.FOOD_SIZE));
                break;
            case Constants.categoryNPFOOD:
                g2D.fill(new Ellipse2D.Double((int) aThing.getAttributes().getX1(), (int) aThing.getAttributes().getY1(), 1+Constants.FOOD_SIZE, 1+Constants.FOOD_SIZE));
                break;
            case Constants.categoryJEWEL:
                g2D.fill(new Rectangle2D.Double((int) aThing.getAttributes().getX1() , (int) aThing.getAttributes().getY1() , 1+Constants.CRYSTAL_SIZE, 1+Constants.CRYSTAL_SIZE));
                break;
            case Constants.categoryDeliverySPOT:
                g2D.fill(new Rectangle2D.Double((int) aThing.getAttributes().getX1() , (int) aThing.getAttributes().getY1() , 1+Constants.FOOD_SIZE, 1+Constants.FOOD_SIZE));
                break;

        }

    }

    private void drawMe(Graphics g, Color backgroundColor) {
        g.setColor(aThing.getMaterial().getColor());

        //create a texture
        BufferedImage bufferedImage = new BufferedImage(20, 20, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gTexture = bufferedImage.createGraphics();
        gTexture.setColor(aThing.getMaterial().getColor()); //same as Thing color
        gTexture.fillRect(0, 0, 15, 15);
//        gTexture.setColor(backgroundColor);
//        gTexture.drawLine(0, 0, 20, 20); // \
//        gTexture.drawLine(0, 20, 20, 0); // /
        Rectangle2D rect = new Rectangle2D.Double(0, 0, 5, 5);


        g2D = (Graphics2D) g;
        g2D.setPaint(new TexturePaint(bufferedImage, rect));
        switch (aThing.getCategory()) {

            case Constants.categoryCREATURE:
                g2D.fill(new RoundRectangle2D.Double((int) aThing.getAttributes().getCOM().getX() - Constants.CREATURE_SIZE/2, (int) aThing.getAttributes().getCOM().getY() - Constants.CREATURE_SIZE/2, Constants.CREATURE_SIZE, Constants.CREATURE_SIZE, Constants.CREATURE_SIZE/4, Constants.CREATURE_SIZE/4));

                break;
            case Constants.categoryBRICK:
                g2D.fill(new Rectangle2D.Double((int) aThing.getX1(), (int) aThing.getY1(), (int) aThing.getX2()
                        - (int) aThing.getX1(), (int) aThing.getY2() - (int) aThing.getY1()));
                break;
            case Constants.categoryPFOOD:
                g2D.fill(new Ellipse2D.Double((int) aThing.getAttributes().getX1(), (int) aThing.getAttributes().getY1(), 1+Constants.FOOD_SIZE, 1+Constants.FOOD_SIZE));
                break;
            case Constants.categoryNPFOOD:
                g2D.fill(new Ellipse2D.Double((int) aThing.getAttributes().getX1(), (int) aThing.getAttributes().getY1(), 1+Constants.FOOD_SIZE, 1+Constants.FOOD_SIZE));
                break;
            case Constants.categoryJEWEL:
                g2D.fill(new Rectangle2D.Double((int) aThing.getAttributes().getX1() , (int) aThing.getAttributes().getY1() , 1+Constants.CRYSTAL_SIZE, 1+Constants.CRYSTAL_SIZE));
                break;

        }

    }

    /**
     * Checks if a Thing is in the field-of-view of the creature. Occluded
     * Things are not considered.
     *
     * @param fov
     * @return
     */
    public synchronized boolean ifIntersectsFOV(Polygon fov){

        boolean ret = false;
        if (aThing.getAttributes().getIFOccluded() == 1) return false; //if occluded, skeep it.

        aThing.initMinimumVSArea();
        switch (aThing.getCategory()) {

            case Constants.categoryCREATURE:
               ret =  fov.intersects((new RoundRectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13, 5, 5)).getBounds2D());
                break;
            case Constants.categoryBRICK:
//                ret =  fov.intersects((new Rectangle2D.Double((int) aThing.getX1(), (int) aThing.getY1(), (int) aThing.getX2()
//                        - (int) aThing.getX1(), (int) aThing.getY2() - (int) aThing.getY1())).getBounds2D());
                ret = fov.intersects(aThing.minVSArea.getBounds2D());
                break;
            case Constants.categoryPFOOD:
                ret =  fov.intersects((new Ellipse2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13)).getBounds2D());
                break;
            case Constants.categoryNPFOOD:
                ret =  fov.intersects((new Ellipse2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13)).getBounds2D());
                break;
            case Constants.categoryJEWEL:
                ret =  fov.intersects((new Rectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13)).getBounds2D());
                break;

        }

        return ret;
    }


}
