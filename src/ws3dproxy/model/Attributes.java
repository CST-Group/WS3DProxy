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

import java.awt.geom.Rectangle2D;
import java.util.Map;
import ws3dproxy.util.Constants;

/**
 * All entities share certain attributes. However, the creature itself _knows_
 * certain inner attributes and all other Thing (objects and other creatures)
 * that are captured by the sensors present another set of _perceived_
 * attributes.
 *
 * @author ecalhau
 */
public abstract class Attributes {

    protected String name;
    protected int category;
    protected String colorName;
    protected double centerOfMass_X, centerOfMass_Y; //center of mass
    protected double x1, x2, y1, y2;
    protected WorldPoint centerOfMassLocation;
    protected double pitch = Constants.PITCH_INEXISTENT;

    protected Material3D material;
    
    protected Rectangle2D.Double shape;

    
    public Attributes(String name, int category, String colorName, double x1, double y1, double x2, double y2, double comX, double comY, double pitch) {

        this.name = name; //does not change 
        this.category = category; //dows not change
        this.colorName = colorName;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        centerOfMass_X = comX;
        centerOfMass_Y = comY;
        //this.centerOfMassLocation = new WorldPoint((x1 + x2) / 2, (y1 + y2) / 2);
        this.centerOfMassLocation = new WorldPoint(centerOfMass_X, centerOfMass_Y);

        this.material = new Material3D(Constants.translateIntoColor(colorName));

        this.pitch = pitch;

        this.shape = new Rectangle2D.Double((int) this.x1, (int) this.y1, (int) this.x2
                    - (int) this.x1, (int) this.y2 - (int) this.y1);
        
    }


    public Attributes(String name, int category, String colorName, double x1, double y1, double x2, double y2, double comX, double comY, double pitch, double shininess, double energy) {
        this(name, category, colorName, x1, y1, x2, y2, comX, comY, pitch);
        this.material.setShininess(shininess);
        this.material.setEnergy(energy);
    }
    
    public void update(double x1, double y1, double x2, double y2, double comX, double comY) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;

        this.centerOfMassLocation = new WorldPoint(comX, comY);

        this.shape = new Rectangle2D.Double((int) this.x1, (int) this.y1, (int) this.x2
                    - (int) this.x1, (int) this.y2 - (int) this.y1);
        

    }
    public void update(String colorName, double x1, double y1, double x2, double y2, double comX, double comY) {
        this.update(x1, y1, x2, y2, comX, comY);
        this.colorName = colorName;
    }

    public void update(String colorName, double x1, double y1, double x2, double y2, double comX, double comY, double pitch, double foodEnergy) {
        this.update(colorName, x1, y1, x2, y2, comX, comY);
        this.pitch = pitch;
        this.material.setEnergy(foodEnergy);
        

    }
    
    public  WorldPoint getCOM() {
        return centerOfMassLocation;
    }

    public  int getCategory() {
        return category;
    }

    public  String getColor() {
        return colorName;
    }

    public  double getX1() {
        return x1;
    }

    public  void setX1(double d) {
        this.x1 = d;
    }

    public  double getX2() {
        return x2;
    }

    public  void setX2(double d) {
        this.x2 = d;
    }

    public  double getY1() {
        return y1;
    }

    public  void setY1(double d) {
        this.y1 = d;
    }

    public  double getY2() {
        return y2;
    }

    public  void setY2(double d) {
        this.y2 = d;
    }

        public  double getPitch() {
        return pitch;
    }

    public  void setPitch(double d) {
        this.pitch = d;
    }
    
    public  void setCategory(int c) {
        this.category = c;
    }

    public  String getName() {
        return this.name;
    }

    public  void setName(String n) {
        this.name = n;
    }

    public  Material3D getMaterial3D() {
        return this.material;
    }

    public  Rectangle2D.Double getShape(){
        return shape;
    }
    public abstract Map getMap();

    @Override
    public abstract String toString();

}
