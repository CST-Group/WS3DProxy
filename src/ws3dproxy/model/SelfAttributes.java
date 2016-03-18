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

import java.awt.geom.Line2D;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.util.Constants;

/**
 *
 * @author eccastro
 */
public class SelfAttributes extends Attributes {

    protected String robotIndexID;
    protected double wheel = 0.111; //to be deleted
    protected double speed;
    //HomeostaticParameters:
    protected double serotonin;
    protected double fuel;//energy
    protected double endorphine;
    protected double score;
    
    protected int hasLeaflet = 0; //false
    protected int hasCollided = 0; //false
    
    private HashMap<String, String> infoMap = new HashMap<String, String>();

    public SelfAttributes(String indexID, String name, String  colorName, Actuator actuator, MySensors s, int hasLeaflet, int hasCollided, List<Leaflet> myLeaflets) {
        super(name, Constants.categoryCREATURE, colorName, s.x1, s.y1, s.x2, s.y2, s.comX, s.comY, s.pitch);
        //pitch = s.pitch;
        robotIndexID = indexID;
        this.speed = actuator.speed;
        this.wheel = actuator.wheel;
        this.serotonin = s.serotonin;
        this.score = s.score;
        this.fuel = s.fuel;
        this.endorphine = s.endorphine;
        this.hasLeaflet = hasLeaflet;
        this.hasCollided = hasCollided;
        this.updateMap(indexID, name, colorName, actuator, s, hasLeaflet, hasCollided, myLeaflets);
    }

    public void update(String index, Actuator actuator, MySensors s, int hasLeaflet, int hasCollided, List<Leaflet> myLeaflets) {
        this.robotIndexID = index;
        this.x1 = s.x1;
        this.y1 = s.y1;
        this.x2 = s.x2;
        this.y2 = s.y2;
        this.centerOfMass_X = s.comX;
        this.centerOfMass_Y = s.comY;
        this.fuel = s.fuel;
        this.pitch = s.pitch;
        this.serotonin = s.serotonin;
        this.endorphine = s.endorphine;
        this.score = s.score;
        this.hasLeaflet = hasLeaflet;
        this.hasCollided = hasCollided;
        super.update(x1, y1, x2, y2, centerOfMass_X, centerOfMass_Y);
        this.updateMap(index, name, colorName, actuator, s, hasLeaflet, hasCollided, myLeaflets);
    }
    
    private void updateMap(String indexID, String name, String  colorName, Actuator act, MySensors s, int hasLeaflet, int hasCollided, List<Leaflet> myLeaflets){
        infoMap.put(Constants.TOKEN_NAME_ID, name);
        infoMap.put(Constants.TOKEN_INDEX, indexID);
        infoMap.put(Constants.TOKEN_COLOR, colorName);
        infoMap.put(Constants.TOKEN_CREATURE_X, (new Double(s.comX)).toString());//center of mass
        infoMap.put(Constants.TOKEN_CREATURE_Y, (new Double(s.comY)).toString());// ""
        infoMap.put(Constants.TOKEN_CREATURE_X1, (new Double(s.x1)).toString());
        infoMap.put(Constants.TOKEN_CREATURE_Y1, (new Double(s.y1)).toString());
        infoMap.put(Constants.TOKEN_CREATURE_X2, (new Double(s.x2)).toString());
        infoMap.put(Constants.TOKEN_CREATURE_Y2, (new Double(s.y2)).toString());
        infoMap.put(Constants.TOKEN_SPEED, (new Double(act.speed)).toString());
        infoMap.put(Constants.TOKEN_WHEEL, (new Double(act.wheel)).toString());
        //TODO: include:
//        infoMap.put(Constants.TOKEN_WHEEL_R, (new Double(act.vr)).toString());
//        infoMap.put(Constants.TOKEN_WHEEL_L, (new Double(act.vl)).toString());
        infoMap.put(Constants.TOKEN_CREATURE_PITCH, (new Double(s.pitch)).toString());
        infoMap.put(Constants.TOKEN_CREATURE_ENERGY, (new Double(s.fuel)).toString());
        infoMap.put(Constants.TOKEN_SEROTONIN, (new Double(s.serotonin)).toString());
        infoMap.put(Constants.TOKEN_ENDORPHINE, (new Double(s.endorphine)).toString());  
        infoMap.put(Constants.TOKEN_SCORE, (new Double(s.score)).toString());
        infoMap.put(Constants.TOKEN_HAS_LEAFLET, (new Integer(hasLeaflet)).toString());
        infoMap.put(Constants.TOKEN_HAS_COLLIDED, (new Integer(hasCollided)).toString());
        infoMap.put(Constants.TOKEN_MY_LEAFLETS, manipulatedLeaflet(myLeaflets));
        
    }
    
     
    public double getPitch() {
        return pitch;
    }

    public void setPitch(double d) {
        this.pitch = d;
    }

    public String getIndex() {
        return robotIndexID;
    }

    public double getSerotonin() {
        return serotonin;
    }

    public void setSerotonin(double d) {
        this.serotonin = d;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double d) {
        this.score = d;
    }
    
    public double getEndorphine() {
        return endorphine;
    }

    public void setEndorphine(double d) {
        this.endorphine = d;
    }
    
    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double d) {
        this.speed = d;
    }

    public double getWheel() {
        return wheel;
    }

    public void setWheel(double d) {
        this.wheel = d;
    }

    public double getFuel() {
        return fuel;
    }

    public void setFuel(double d) {
        this.fuel = d;
    }

    public synchronized int getIfHasLeaflet() {
        return this.hasLeaflet;
    }

    @Override
    public String toString() {
        return "" + getName() + " " + this.getIndex() + " " + getColor() + " " + getSpeed() + " " + getWheel() + " " + getPitch() + " " + getFuel() + " " + getSerotonin() + " " +  getEndorphine() + " " + getScore() + " " + getX1() + " " + getY1()+" " + getX2() + " " + getY2()+" "+this.getIfHasLeaflet() + " "+this.hasCollided;
    }

    @Override
    public Map getMap() {
        return this.infoMap;
    }
    
    
    private String manipulatedLeaflet(List<Leaflet> myLeaflets) {

        String r = "";
        
         JSONArray jsonArray = new JSONArray ();
         
        for (Leaflet lf : myLeaflets) {

            JSONObject json = new JSONObject();
            try {
                json.put(Constants.TOKEN_LEAFLET_ID, lf.getID());
                json.put(Constants.TOKEN_PAYMENT, lf.getPayment());
                json.put(Constants.TOKEN_SITUATION, lf.getSituation());

                HashMap<String, Integer[]> itens = lf.getItems();
                JSONObject jsonItens = new JSONObject();

                for (Iterator<String> iter = itens.keySet().iterator(); iter.hasNext();) {

                    String str = iter.next(); //jewel color
                    Integer[] values = (Integer[]) itens.get(str); //(total number) (collected)

                    jsonItens.put(str, values);

                }

                json.put(Constants.TOKEN_ITENS, jsonItens);

                jsonArray.put(json);

            } catch (JSONException ex) {
                Logger.getLogger(SelfAttributes.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
         
         

        return (String)(jsonArray.toString());

    }

}
