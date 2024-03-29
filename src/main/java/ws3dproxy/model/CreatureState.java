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

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.util.Constants;

/**
 * Class that defines the list of attributes of a Creature. Corresponds to the
 * state of the creature at the instant.
 *
 * @author eccastro
 */
public class CreatureState extends Observable{

    private SensoryBuffer sensoryBuffer;
    
    private static List<CreatureState> instances = new ArrayList<>();
    private String name;
    private String index;
    private String colorName;
    private double speed;
    private double wheel;
    private double size;
    private double pitch;
    private int motorSys = 2;
    private double fuel;
    private double serotonin;
    private double endorphine;
    private double score;
    private double center_of_mass_X;
    private double center_of_mass_Y;
    private double X1;
    private double Y1;
    private double X2;
    private double Y2;
    private int hasLeaflet = 0;
    private int hasCollided = 0;
   // private static Map<Long, Leaflet> myLeaflets = Collections.synchronizedMap(new HashMap<Long, Leaflet>());
    private static Map<Long, Leaflet> myLeaflets;
    

    private JSONObject infoThingActedUpon = null;
    
    private CreatureState(String indexID, String myName, String colorName, double speed, double wheel, double pitch, int motorSys, double fuel, double serotonin, double endorphine, double score, WorldPoint position, double x1, double y1, double x2, double y2, int hasCollided, int hasLeaflet, List<Leaflet> leafletList) {
        sensoryBuffer = SensoryBuffer.getInstance();
        updateMe(indexID, myName, colorName, speed, wheel, pitch, motorSys, fuel, serotonin, endorphine, score, position, x1, y1, x2, y2, hasCollided, hasLeaflet, leafletList);
    }

    /**
     * Must call notifyMyObservers() after it.
     * 
     * @param indexID
     * @param myName
     * @param colorName
     * @param speed
     * @param wheel
     * @param pitch
     * @param motorSys
     * @param fuel
     * @param serotonin
     * @param endorphine
     * @param position
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param hasLeaflet
     * @param leafletList
     * @return 
     */
    public static CreatureState getInstance(String indexID, String myName, String colorName, double speed, double wheel, double pitch, int motorSys, double fuel, double serotonin, double endorphine, double score, WorldPoint position, double x1, double y1, double x2, double y2, int hasCollided, int hasLeaflet, List<Leaflet> leafletList) {
        for (CreatureState cs : instances) {
            if (cs.name.equals(myName)) {
                cs.updateMe(indexID, myName, colorName, speed, wheel, pitch, motorSys, fuel, serotonin, endorphine, score, position, x1, y1, x2, y2, hasCollided, hasLeaflet, leafletList);
                return(cs);
            }
        }
        CreatureState newcs = new CreatureState(indexID, myName, colorName, speed, wheel, pitch, motorSys, fuel, serotonin, endorphine, score, position, x1, y1, x2, y2, hasCollided, hasLeaflet, leafletList);
        instances.add(newcs);
        return newcs;
    }
    

    
    private void updateMe(String indexID, String myName, String colorName, double speed, double wheel, double pitch, int motorSys, double fuel, double serotonin, double endorphine, double score, WorldPoint position, double x1, double y1, double x2, double y2, int hasCollided,int hasLeaflet, List<Leaflet> leafletList) {
        index = indexID;
        this.colorName = colorName;
        this.motorSys = motorSys;
        if (speed == 0) {
            speed = Constants.DEFAULT_CREATURE_SPEED;
        }
        this.speed = speed;
        this.wheel = wheel;
        this.pitch = pitch;
        this.fuel = fuel;
        this.serotonin = serotonin;
        this.endorphine = endorphine;
        this.score = score;
        center_of_mass_X = position.getX();
        center_of_mass_Y = position.getY();
        X1 = x1;
        Y1 = y1;
        X2 = x2;
        Y2 = y2;
        size = Math.round(size);
        this.hasLeaflet = hasLeaflet;
        this.hasCollided = hasCollided;
        name = myName;
        
        if (hasLeaflet == 1) {
            myLeaflets = Collections.synchronizedMap(new HashMap<Long, Leaflet>());
            for (Leaflet l : leafletList) {
               myLeaflets.put(l.getID(), l);
            }
        } else {
            myLeaflets = Collections.synchronizedMap(new HashMap<Long, Leaflet>());
        }
    }

    /**
     * Must be called after updateMe
     */
    public void notifyMyObservers() {
        setChanged();
        notifyObservers();
    }

    public synchronized SensoryBuffer getBuffer(){
        return sensoryBuffer;
    }

    public synchronized Map<String, Thing> getThingsInVisionMap() {
        return sensoryBuffer.getThingsInVisionMap();
    }

    public synchronized Map<String, Thing> getThingsInCameraFrustrumMap() {
        return sensoryBuffer.getThingsInFrustrumMap();
    }

    public String getIndex() {
        return this.index;
    }

    public String getNameID() {
        return this.name;
    }

    public int hasLeaflet() {
        return hasLeaflet;
    }
    
    public int hasCollided() {
        return hasCollided;
    }

    public void addLeaflet(Leaflet l) {
        myLeaflets.put(l.getID(), l);
    }
    
    

    public synchronized void setThingsInVision(List<Thing> list) {
        sensoryBuffer.resetVision();
        //hard copy
        for (Thing t : list) {
            sensoryBuffer.capturedInVision(t);
        }
    }

    public synchronized List<Thing> getThingsInVision() {
        return sensoryBuffer.getThingsInVision();
    }

    public synchronized void setThingsInCameraFrustrum(List<Thing> list) {
        sensoryBuffer.resetFrustrum();
        //hard copy
        for (Thing t : list) {
            sensoryBuffer.capturedInFrustrum(t);
        }
    }

    public synchronized List<Thing> getThingsInCameraFrustrum() {
        return sensoryBuffer.getThingsInFrustrum();
    }

    public double getSpeed() {
        return this.speed;
    }

    public double getSize() {
        return this.size;
    }

    public int getMotorSys() {
        return this.motorSys;
    }

    public double getWheel() {
        return this.wheel;
    }

    public double getPitch() {
        return this.pitch;
    }

    public double getFuel() {
        return this.fuel;
    }

    public double getSerotonin() {
        return this.serotonin;
    }

    public double getEndorphine() {
        return this.endorphine;
    }

    public double getScore() {
        return this.score;
    }

    public WorldPoint getPosition() {
        WorldPoint p = new WorldPoint(center_of_mass_X, center_of_mass_Y);
        return p;
    }

    public String getColorName() {
        return this.colorName;
    }

    public double getCreatureSize() {

        return this.size;
    }

    public double getX1() {
        return X1;
    }

    public double getY1() {
        return Y1;
    }

    public double getX2() {
        return X2;
    }

    public double getY2() {
        return Y2;
    }

    public List<Leaflet> getLeaflets() {
        List v = new ArrayList<Leaflet>();
        for (Iterator iter = myLeaflets.values().iterator(); iter.hasNext();) {
            Leaflet l = (Leaflet) iter.next();
            v.add(l);
        }
        return v;

    }
    
   

    public void setInfoThingActedUpon(String actiondata) {

        if (!actiondata.equals("NONE")) {
            try {
                infoThingActedUpon = new JSONObject(actiondata);
                String actionName = (String) infoThingActedUpon.get(Constants.TOKEN_ACTION);
                String attribData = (infoThingActedUpon.get(Constants.TOKEN_THING_DATA)).toString();
                JSONObject jsonAttribs = new JSONObject(attribData);

                //System.out.println("-----Thing acted upon: " + jsonAttribs.toString());
                //System.out.println("-----Action and Thing acted upon: " + infoThingActedUpon.toString());

            } catch (JSONException ex) {
                Logger.getLogger(CreatureState.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else infoThingActedUpon = null; //reset
    }
    
    /**
     * 
     * @return may be null(in case of no contact action performed)
     */
    public String getInfoThingActedUpon() {
        if (infoThingActedUpon == null) return "NONE";
        else return  infoThingActedUpon.toString();
    }
}
