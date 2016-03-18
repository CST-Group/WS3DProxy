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

import java.util.HashMap;
import java.util.Map;
import ws3dproxy.util.Constants;


/**
 * Thing entities (objects and creatures) that may be captured by the sensors.
 *
 * @author eccastro
 */
public class ThingAttributes extends Attributes {

    protected int isOccluded = 0; //0-false; 1-true;
    private HashMap<String, String> infoMap = new HashMap<String, String>();
    
    public ThingAttributes(String name, int category, String c, double x1, double y1, double x2, double y2, double comX, double comY, double pitch, double shininess, double energy, int isOccluded) {
        super(name, category, c, x1, y1, x2, y2, comX, comY, pitch, shininess, energy);
        this.isOccluded = isOccluded;
        this.updateMap(name, category, c, x1, y1, x2, y2, comX, comY, pitch, shininess, energy, isOccluded);
    }

    public void update(String colorName, double x1, double y1, double x2, double y2, double comX, double comY, double pitch, double energy, int isOccluded, double shininess) {
        super.update(colorName, x1, y1, x2, y2, comX, comY, pitch, energy);
        this.isOccluded = isOccluded;
        this.material.setShininess(shininess);
        //String name, int category, String c, double x1, double y1, double x2, double y2, double comX, double comY, double pitch, double shininess, double energy, int isOccluded
        this.updateMap(name, category, colorName, x1, y1, x2, y2, comX, comY, pitch, shininess, energy, isOccluded);
    }
    
    private void updateMap(String name, int category, String c, double x1, double y1, double x2, double y2, double comX, double comY) {
        infoMap.put(Constants.TOKEN_NAME_ID, name); 
        infoMap.put(Constants.TOKEN_COLOR, c);
        infoMap.put(Constants.TOKEN_CENTER_OF_MASS_X, (new Double(comX)).toString());
        infoMap.put(Constants.TOKEN_CENTER_OF_MASS_Y, (new Double(comY)).toString());
        infoMap.put(Constants.TOKEN_X1, (new Double(x1)).toString());
        infoMap.put(Constants.TOKEN_X2, (new Double(x2)).toString());
        infoMap.put(Constants.TOKEN_Y1, (new Double(y1)).toString());
        infoMap.put(Constants.TOKEN_Y2, (new Double(y2)).toString());
        infoMap.put(Constants.TOKEN_CATEGORY, (new Integer(category)).toString());
        infoMap.put(Constants.TOKEN_HARDNESS, (new Integer(Constants.HARDNESS_DEFAULT)).toString()); 
        infoMap.put(Constants.TOKEN_THING_ENERGY, (new Double(Constants.ENERGY_DEFAULT)).toString());
        infoMap.put(Constants.TOKEN_OCCLUDED, (new Double(Constants.OCCLUDED_DEFAULT)).toString());
        infoMap.put(Constants.TOKEN_SHININESS, (new Integer(Constants.SHININESS_DEFAULT)).toString()); 
        infoMap.put(Constants.TOKEN_THING_PITCH, (new Double(Constants.PITCH_INEXISTENT)).toString());//default; only actually valid for creature (as Thing, not self)
    }

    private void updateMap(String name, int category, String c, double x1, double y1, double x2, double y2, double comX, double comY, double pitch) {
        this.updateMap(name, category, c, x1, y1, x2, y2, comX, comY);
        infoMap.put(Constants.TOKEN_THING_PITCH, (new Double(pitch)).toString());
        
    }

    private void updateMap(String name, int category, String c, double x1, double y1, double x2, double y2, double comX, double comY, double pitch, double shininess, double energy, int isOccluded) {
        this.updateMap(name, category, c, x1, y1, x2, y2, comX, comY, pitch);
        infoMap.put(Constants.TOKEN_THING_ENERGY, (new Double(energy)).toString());
        infoMap.put(Constants.TOKEN_OCCLUDED, (new Double(isOccluded)).toString());
        infoMap.put(Constants.TOKEN_SHININESS, (new Double(shininess)).toString());
    }

    public void setIFOccluded(int b) {
        this.isOccluded = b;
    }

    public int getIFOccluded() {
        return this.isOccluded;
    }
    

    @Override
    public String toString() {
        return "" + this.getName() + " " + this.getX1() + " " + this.getY1() + " " + this.getX2() + " " + this.getY2() + " " + this.getCategory() + " " + this.getIFOccluded() + " " + this.getMaterial3D();
    }

    @Override
    public Map getMap() {
        return this.infoMap;
    }
   
}
