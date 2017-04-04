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
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import ws3dproxy.model.Creature;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;
import ws3dproxy.model.WorldPoint;
import ws3dproxy.util.Constants;

/**
 *
 * @author ecalhau
 */
public class EnvironmentPanel extends JPanel {

    private final Color mindBoardColor = Color.DARK_GRAY;
    private final Color targetColor = Color.CYAN;
    private Creature creature = null;
    private Rectangle2D.Double creatureRec;
    Graphics2D g2;

    public EnvironmentPanel() {
    }
    /**
     *
     */
    private static final long serialVersionUID = -1234952763951826229L;

    public void paint(Graphics g) {

        super.paint(g);
        g2 = (Graphics2D) g;
        World parameters = World.getInstance();

        g.setColor(mindBoardColor);
        g.fillRect(0, 0, parameters.getEnvironmentWidth(), parameters.getEnvironmentHeight());

        if (creature != null) {
            paintThingsInVision(g);
            paintMyself(creature, g);
            paintArea(g);
            //paintAreaSides(g2);
            paintRay(g2);
            ///showIntersections(g);
            paintSecurityArea(g);
        }
    }

    public void setCreature(Creature c) {
        creature = c;
    }

    private void paintMyself(Creature creature, Graphics g) {
        WorldPoint p = creature.getPosition();
        double xCreaturePosition = p.getX();
        double yCreaturePosition = p.getY();
        double pitch = creature.getAttributes().getPitch();
        g.setColor(creature.getAttributes().getMaterial3D().getColor());

        g2.fill(new RoundRectangle2D.Double((int) creature.getAttributes().getCOM().getX() - Constants.CREATURE_SIZE/2, (int) creature.getAttributes().getCOM().getY() - Constants.CREATURE_SIZE/2, Constants.CREATURE_SIZE, Constants.CREATURE_SIZE, Constants.CREATURE_SIZE/4, Constants.CREATURE_SIZE/4));

        
        //draw a line to indicate the creature current pitch:
        g.drawLine((int) xCreaturePosition, (int) yCreaturePosition,
                (int) (xCreaturePosition + 40 * Math.cos(pitch)),
                (int) (yCreaturePosition + 40 * Math.sin(pitch)));

        showFOV(creature, g);
    }

    private void showFOV(Creature creature, Graphics g) {
        g.setColor(Color.YELLOW);
        g.drawPolygon(creature.getFOV());
    }

    private void paintThingsInVision(Graphics g) {

        CopyOnWriteArrayList<Thing> thingsList = new CopyOnWriteArrayList(creature.getThingsInVision());
        if (thingsList.size() > 0) {
            //List<Thing> thingsListCopy = Collections.synchronizedList(new ArrayList<Thing>());
            for (Thing o : thingsList) {
            //for (int i = 0; i < thingsList.size(); i++) {
                //Thing o = thingsList.get(i);
                //thingsListCopy.add(o);
                if (o.getDrawer() != null) {
                    o.getDrawer().draw(g, g2, mindBoardColor);
                }
            }
        }

    }

    private void paintArea(Graphics g) {

        List<Thing> thingsList = Collections.synchronizedList(creature.getThingsInVision());
        CopyOnWriteArrayList<Thing> tL = new CopyOnWriteArrayList(thingsList);
        if (tL.size() > 0) {
            for (Thing th : tL) {
                Rectangle2D.Double area = th.getAttributes().getShape();
                g2.setColor(Color.CYAN);
                g2.draw(area);

            }
        }
    }
    
        private synchronized void paintSecurityArea(Graphics g) {

        List<Thing> thingsList = Collections.synchronizedList(creature.getThingsInVision());
        CopyOnWriteArrayList<Thing> tL = new CopyOnWriteArrayList(thingsList);
        if (tL.size() > 0) {
            for (Thing th : tL) {
                Rectangle2D.Double area = th.getSecArea();
                g2.setColor(Color.RED);
                g2.draw(area);

            }
        }
    }

    private void paintAreaSides(Graphics2D g2) {

        List<Thing> thingsList = Collections.synchronizedList(creature.getThingsInVision());
        if (thingsList.size() > 0) {
            for (Thing th : thingsList) {
                List<Line2D.Double> sides = th.getAreaSides();
                g2.setColor(Color.CYAN);
                for (Line2D.Double l : sides) {
                    g2.draw(l);
                }

            }
        }
    }

    private void paintRay(Graphics2D g2) {

        Line2D.Double ray = creature.s.directRay;
        g2.setColor(Color.MAGENTA);
        g2.draw(ray);

    }

    private void showIntersections(Graphics g) {
        g.setColor(Color.PINK);
        List<Thing> inVision = Collections.synchronizedList(creature.getThingsInVision());
        
        if(!inVision.isEmpty())
        for (Thing th : inVision) {
            List<WorldPoint> hits = creature.getHitPoints(th, creature.s.directRay);
            if(!hits.isEmpty())
            for (WorldPoint pt : hits) {
                g.fillOval((int) pt.getX() - 2, (int) pt.getY() - 2, 5, 5);
            }
        }
    }
    
    public void attachToJScrollPane(JScrollPane father) {
        javax.swing.GroupLayout epLayout = new javax.swing.GroupLayout(this);
        this.setLayout(epLayout);
        Dimension d = this.getSize();
        epLayout.setHorizontalGroup(
            epLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, d.width, Short.MAX_VALUE)
        );
        epLayout.setVerticalGroup(
            epLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, d.height, Short.MAX_VALUE)
        );
        father.setViewportView(this);
    }

}
