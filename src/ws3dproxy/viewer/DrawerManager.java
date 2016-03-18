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

package ws3dproxy.viewer;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.TexturePaint;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import ws3dproxy.model.Thing;
import ws3dproxy.util.Constants;

/**
 *
 * @author ecalhau
 */
public class DrawerManager {

    private Thing aThing;
    private Graphics2D g2D;

    public DrawerManager(Thing th) {
        aThing = th;
    }

    public synchronized void draw(Graphics g, Color backgroundColor) {
        if (aThing.getIfFromMemory()) {
            drawMe(g, backgroundColor);
        } else {
            drawMe(g);
        }

    }

    private void drawMe(Graphics g) {
        g.setColor(aThing.getMaterial().getColor());
        g2D = (Graphics2D) g;
        switch (aThing.getCategory()) {

            case Constants.categoryCREATURE:
                g2D.fill(new RoundRectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13, 5, 5));
//                g.drawLine((int) aThing.getX1(), (int) aThing.getY1(),
//                        (int) (aThing.getX1() + 15 * Math.cos(aThing.getAttributes().getPitch() - Constants.M_PI_2)),
//                        (int) (aThing.getY1() + 15 * Math.sin(aThing..getAttributes().getPitch() - Constants.M_PI_2)));
                break;
            case Constants.categoryBRICK:
                g2D.fill(new Rectangle2D.Double((int) aThing.getX1(), (int) aThing.getY1(), (int) aThing.getX2()
                        - (int) aThing.getX1(), (int) aThing.getY2() - (int) aThing.getY1()));
                break;
            case Constants.categoryPFOOD:
                g2D.fill(new Ellipse2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13));
                break;
            case Constants.categoryNPFOOD:
                g2D.fill(new Ellipse2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13));
                break;
            case Constants.categoryJEWEL:
                g2D.fill(new Rectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13));
                break;
            case Constants.categoryDeliverySPOT:
                g2D.fill(new Rectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13));
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
        Rectangle2D rect = new Rectangle2D.Double(0, 0, 5, 5);

        g2D = (Graphics2D) g;
        g2D.setPaint(new TexturePaint(bufferedImage, rect));
        switch (aThing.getCategory()) {

            case Constants.categoryCREATURE:
                g2D.fill(new RoundRectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13, 5, 5));
//                g.drawLine((int) aThing.getX1(), (int) aThing.getY1(),
//                        (int) (aThing.getX1() + 15 * Math.cos(aThing.getAttributes().getPitch() - Constants.M_PI_2)),
//                        (int) (aThing.getY1() + 15 * Math.sin(aThing.getAttributes().getPitch()- Constants.M_PI_2)));
                break;
            case Constants.categoryBRICK:
                g2D.fill(new Rectangle2D.Double((int) aThing.getX1(), (int) aThing.getY1(), (int) aThing.getX2()
                        - (int) aThing.getX1(), (int) aThing.getY2() - (int) aThing.getY1()));
                break;
            case Constants.categoryPFOOD:
                g2D.fill(new Ellipse2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13));
                break;
            case Constants.categoryNPFOOD:
                g2D.fill(new Ellipse2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13));
                break;
            case Constants.categoryJEWEL:
                g2D.fill(new Rectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13));
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
    public synchronized boolean ifIntersectsFOV(Polygon fov) {

        boolean ret = false;
        if (aThing.getAttributes().getIFOccluded() == 1) {
            return false; //if occluded, skeep it.
        }
        aThing.initMinimumVSArea();
        switch (aThing.getCategory()) {

            case Constants.categoryCREATURE:
                ret = fov.intersects((new RoundRectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13, 5, 5)).getBounds2D());
                break;
            case Constants.categoryBRICK:
//                ret =  fov.intersects((new Rectangle2D.Double((int) aThing.getX1(), (int) aThing.getY1(), (int) aThing.getX2()
//                        - (int) aThing.getX1(), (int) aThing.getY2() - (int) aThing.getY1())).getBounds2D());
                ret = fov.intersects(aThing.minVSArea.getBounds2D());
                break;
            case Constants.categoryPFOOD:
                ret = fov.intersects((new Ellipse2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13)).getBounds2D());
                break;
            case Constants.categoryNPFOOD:
                ret = fov.intersects((new Ellipse2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13)).getBounds2D());
                break;
            case Constants.categoryJEWEL:
                ret = fov.intersects((new Rectangle2D.Double((int) aThing.getX1() - 6, (int) aThing.getY1() - 6, 13, 13)).getBounds2D());
                break;

        }

        return ret;
    }

}
