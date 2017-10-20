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
import java.awt.Polygon;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.CommandExecException;
import ws3dproxy.CommandUtility;
import ws3dproxy.WS3DProxy;
import ws3dproxy.util.Constants;
import ws3dproxy.util.Logger;
import ws3dproxy.viewer.MindWindow;

/**
 *
 * @author eccastro
 */
public class Creature {

    public Actuator a;
    public MySensors s;
    private WorldMap worldMap;

    private static CreatureState state;

    private static Creature instance = null;
    private ConcurrentHashMap<Long, Leaflet> myLeaflets = new ConcurrentHashMap<Long, Leaflet>();
    private List<Thing> thingsInVision = Collections.synchronizedList(new ArrayList<Thing>());
    private List<Thing> thingsInFrustrum = Collections.synchronizedList(new ArrayList<Thing>());

    private Bag bag = null; //must be initialized by sending a specific command ("getsack")

    private final SelfAttributes attributes;

    private MindWindow mindDisplay;

    private JSONObject infoThingActedUpon = null;

    private Creature(CreatureState cs) {

        a = new Actuator();
        s = new MySensors();
        if (cs.getSpeed() == 0) {
            this.a.speed = Constants.DEFAULT_CREATURE_SPEED;//5 default (old: 1)
        }

        this.a.wheel = cs.getWheel();
        this.s.pitch = cs.getPitch();
        this.s.fuel = cs.getFuel();
        this.s.serotonin = cs.getSerotonin();
        this.s.endorphine = cs.getEndorphine();
        this.s.score = cs.getScore();
        this.s.comX = cs.getPosition().getX();
        this.s.comY = cs.getPosition().getY();
        this.s.x1 = cs.getX1();
        this.s.y1 = cs.getY1();
        this.s.x2 = cs.getX2();
        this.s.y2 = cs.getY2();

        this.s.directRay = new Line2D.Double(this.s.comX, this.s.comY, this.s.comX + Constants.RAY_RANGE * Math.cos(this.s.pitch), this.s.comY + Constants.RAY_RANGE * Math.sin(this.s.pitch));

        this.attributes = new SelfAttributes(cs.getIndex(), cs.getNameID(), cs.getColorName(), a, s, cs.hasLeaflet(), cs.hasCollided(), cs.getLeaflets());

        state = cs;
        mindDisplay = null;
    }
    
    public void addMindWindow() {
        mindDisplay = new MindWindow(this);
        observeWindow(mindDisplay);
        //state.addObserver(mindDisplay);
    }
    
    public void observeWindow(Observer o) {
        state.addObserver(o);        
    }

    public static Creature getInstance(CreatureState cs) {
        if (instance == null) {
            instance = new Creature(cs);
        }

        return instance;
    }

    public synchronized SensoryBuffer getSensoryBuffer() {
        return state.getBuffer();
    }

    public synchronized void setThingsInVision(List<Thing> list) {
        this.thingsInVision.clear();
        //hard copy
        for (Thing t : list) {
            thingsInVision.add(t);
        }
    }

    public synchronized List<Thing> getThingsInVision() {
        return this.thingsInVision;
    }

    public synchronized void setThingsInCameraFrustrum(List<Thing> list) {
        this.thingsInFrustrum.clear();
        //hard copy
        for (Thing t : list) {
            thingsInFrustrum.add(t);
        }
    }

    public synchronized List<Thing> getThingsInCameraFrustrum() {
        return this.thingsInFrustrum;
    }

    public SelfAttributes getAttributes() {
        return this.attributes;
    }

    public synchronized String getThingsNames() {
        String s = "";

        for (Thing t : Collections.synchronizedList(thingsInVision)) {
            s = s + " " + t.getAttributes().getName();
        }
        return s;
    }

    public synchronized String getIndex() {
        return this.attributes.getIndex();
    }

    public synchronized double calculateDistanceTo(Thing th) {

        double ret = 0;
        List<Double> dist = new ArrayList<Double>();
        //System.out.println("________________________Thing name: " + th.getName());
        List<WorldPoint> list = getHitPoints(th, this.s.directRay);
        if (!list.isEmpty()) {
            for (WorldPoint v : list) {
                dist.add(this.getPosition().distanceTo(v));
                //System.out.println("________________________Creature at " + this.getPosition() + "  and v= " + v.toString() + " and dist: " + this.getPosition().distanceTo(v));
            }
            Collections.sort(dist);
            //System.out.println("dist_0: " + dist.get(0));
            ret = dist.get(0); //the closest
        } else {
            ret = getShortestDistanceToVertices(th);
        }

        return ret;
    }

    private double getShortestDistanceToVertices(Thing th) {
        WorldPoint[] thingVertex = th.getVertex();

        List<Double> dist = new ArrayList<Double>();

        //each Thing has 4 vertex
        for (WorldPoint v : th.getVertex()) {
            dist.add(this.getPosition().distanceTo(v));
        }
        Collections.sort(dist);
        return dist.get(0); //the closest
    }

    public synchronized double calculateAngleTowards(Thing th) {

        double x = th.getCenterPosition().getX() - this.attributes.centerOfMass_X;
        double y = th.getCenterPosition().getY() - this.attributes.centerOfMass_Y;
        return Math.atan2(y, x);
    }

    /**
     * Update creature state (i.e. list of attributes).
     *
     * @param index
     * @param colorName
     * @param speed
     * @param wheel
     * @param pitch
     * @param fuel
     * @param serotonin
     * @param endorphine
     * @param score
     * @param position
     * @param leafletList
     */
    public void update(String index, String colorName, double speed, double wheel, double pitch, double fuel, double stamina, double endorphine, double score, WorldPoint position, double x1, double y1, double x2, double y2, List<Leaflet> leafletList, int hasCollided) {

        if (speed == 0) {
            speed = Constants.DEFAULT_CREATURE_SPEED;
        }

        this.a.speed = speed;
        this.a.wheel = wheel;
        this.s.pitch = pitch; //in RAD
        this.s.fuel = fuel;
        this.s.serotonin = stamina;
        this.s.endorphine = endorphine;
        this.s.score = score;
        this.s.comX = position.getX();
        this.s.comY = position.getY();
        this.s.x1 = x1;
        this.s.y1 = y1;
        this.s.x2 = x2;
        this.s.y2 = y2;
        int hasAnyLeaflet = (leafletList.size() > 0) ? 1 : 0;

        this.attributes.update(index, a, s, hasAnyLeaflet, hasCollided,leafletList);

        this.s.directRay = new Line2D.Double(this.s.comX, this.s.comY, this.s.comX + Constants.RAY_RANGE * Math.cos(this.s.pitch), this.s.comY + Constants.RAY_RANGE * Math.sin(this.s.pitch));
        
        for (Leaflet l : leafletList) {

            if (!ifHasLeaflet(l.getID())) {
                this.addLeaflet(l);
            } else {
                this.updateLeaflet(l.getID(), l.getItems(), l.getSituation());
            }
        }
    }

    /**
     * Creature is updated according to the latest state received from server.
     *
     * @return Creature object
     */
    public synchronized Creature updateState() {

        try {
            String nameId = this.attributes.name;
            CreatureState cs = CommandUtility.getCreatureState(nameId);
            update(cs.getIndex(), cs.getColorName(), cs.getSpeed(), cs.getWheel(), cs.getPitch(), cs.getFuel(), cs.getSerotonin(), cs.getEndorphine(), cs.getScore(), cs.getPosition(), cs.getX1(), cs.getY1(), cs.getX2(), cs.getY2(), cs.getLeaflets(), cs.hasCollided());
            this.setThingsInVision(cs.getThingsInVision());
            this.setThingsInCameraFrustrum(cs.getThingsInCameraFrustrum());
            this.setInfoThingActedUpon(cs.getInfoThingActedUpon());

            
            
        } catch (CommandExecException ex) {
            Logger.logException(WS3DProxy.class.getName(), ex);
        }

        return this;
    }

    /**
     * Send command to get creature's bag content and update it.
     *
     * @return the updated bag
     */
    public synchronized Bag updateBag() {

        String command = "";
        int totalFood = 0;
        int totalCrystals = 0;
        int perishableFood = 0;
        int nonPerishableFood = 0;
        List<Integer> crystals = new ArrayList<Integer>();
        try {
            StringTokenizer st = CommandUtility.sendGetCreatureBagContent(this.attributes.robotIndexID);

            ///////////////////Bag data:
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Total number of food is missing!");
            } else {
                command = st.nextToken();
                totalFood = Integer.parseInt(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Total number of crystals is missing!");
            } else {
                command = st.nextToken();
                totalCrystals = Integer.parseInt(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Number of perishable food is missing!");
            } else {
                command = st.nextToken();
                perishableFood = Integer.parseInt(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Number of non-perishable food is missing!");
            } else {
                command = st.nextToken();
                nonPerishableFood = Integer.parseInt(command);
            }
//                                RED, GREEN, BLUE, YELLOW, MAGENTA, WHITE
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Number of RED crystals is missing!");
            } else {
                command = st.nextToken();
                crystals.add(Integer.parseInt(command));
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Number of GREEN crystals is missing!");
            } else {
                command = st.nextToken();
                crystals.add(Integer.parseInt(command));
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Number of BLUE crystals is missing!");
            } else {
                command = st.nextToken();
                crystals.add(Integer.parseInt(command));
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Number of YELLOW crystals is missing!");
            } else {
                command = st.nextToken();
                crystals.add(Integer.parseInt(command));
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Number of MAGENTA crystals is missing!");
            } else {
                command = st.nextToken();
                crystals.add(Integer.parseInt(command));
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - Number of WHITE crystals is missing!");
            } else {
                command = st.nextToken();
                crystals.add(Integer.parseInt(command));
            }

            if (bag == null) {
                bag = new Bag(totalFood, totalCrystals, perishableFood, nonPerishableFood, crystals);
            } else {
                bag.update(totalFood, totalCrystals, perishableFood, nonPerishableFood, crystals);
            }
        } catch (CommandExecException ex) {
            Logger.logException(Creature.class.getName(), ex);
        }
        return bag;
    }

    /**
     * Return a bottom point of the frustrum far plane. Refer to "OpenGL frustru
     * culling" for more details.
     *
     * @param farDist
     * @param angle
     * @return
     */
    public WorldPoint getBottomPointFarPlane(double farDist, double angle) {
        double alpha, lo;
        //isosceles triangle:
        //side = 2*Height*sqrt(3)/3
        lo = (2 * farDist * Math.sqrt(3)) / 3;

        WorldPoint point = new WorldPoint();
        alpha = s.pitch + angle;
        point.setX(s.comX + lo * Math.cos(alpha));
        point.setY(s.comY + lo * Math.sin(alpha));

        return point;
    }

    /**
     * Calculate the polar coordinates relative to the creature position
     *
     * @param x - the x cartesian coordinate
     * @param y - the y cartesian coordinate
     * @return double[] {distance, angle}
     */
    public double[] xyToPolar(double x, double y) {
        //cartesian coords:
        double dx = x - s.comX;
        double dy = y - s.comY;

        //kinematics model
        double lo = Math.sqrt(dx * dx + dy * dy); //ro
        //alpha - angle between the creature and the target
//        double to = -s.pitch - Math.atan2(-dy, dx) + Constants.M_PI_2;
        double to = -s.pitch - Math.atan2(-dy, dx);

        //[-PI , PI]
        while (to > Constants.M_PI) {
            to -= 2 * Constants.M_PI;
        }

        while (to < -Constants.M_PI) {
            to += 2 * Constants.M_PI;
        }

        //in polar coords {distancia, angulo}
        return new double[]{lo, to};
    }

    public synchronized WorldMap getWorldMap() {
        return worldMap;
    }

    public synchronized void setWorldMap(WorldMap worldMap) {
        this.worldMap = worldMap;
    }

    public synchronized double getSpeed() {
        return this.a.speed;
    }

    public synchronized int getMotorSys() {
        return state.getMotorSys();
    }

    public synchronized double getWheel() {
        return this.a.wheel;
    }

    public synchronized double getPitch() {
        return this.s.pitch;
    }

    public synchronized double getFuel() {
        return this.s.fuel;
    }

    public synchronized Bag getBag() {
        return this.bag; //may be null
    }

    public synchronized WorldPoint getPosition() {
        WorldPoint p = new WorldPoint(s.comX, s.comY);
//        logger.info(" Creature " + this.attributes.robotIndexID + " at: x= " + s.x + " and y= " + s.y);
        return p;
    }

    public String getColor() {
        return this.attributes.colorName;
    }

    private Color translateIntoColor(String name) {
        return Constants.translateIntoColor(name);
    }

    public double getCreatureSize() {

        return state.getSize();
    }

    public boolean ifIsAt(WorldPoint point) {
        double pointX = point.getX();
        double pointY = point.getY();

        return (this.xyToPolar(pointX, pointY)[0] == 0);
    }

    public synchronized void addLeaflet(Leaflet l) {
        myLeaflets.put(l.getID(), l);
    }

    public synchronized List<Leaflet> getLeaflets() {
        List v = new ArrayList<Leaflet>();
        for (Iterator iter = myLeaflets.values().iterator(); iter.hasNext();) {
            Leaflet l = (Leaflet) iter.next();
            v.add(l);
        }
        return v;

    }

    public synchronized boolean ifHasLeaflet(Long ID) {
        boolean ret = false;
        if (myLeaflets.containsKey(ID)) {
            ret = true;
        }
        return ret;
    }

    public synchronized void updateLeaflet(Long ID, HashMap items, int situation) {

        myLeaflets.get(ID).setItems(items);
        myLeaflets.get(ID).setSituation(situation);
    }

    public String getName() {
        return state.getNameID();
    }

    /**
     * Refer to "OpenGL frustrum culling" for more details.
     *
     * @return
     */
    private double getHalfAngleFOVX() {
        World parameters = World.getInstance();

        //these settings must be according to those on the "server"
        double farDist = 1000;
        double ratio = parameters.getEnvironmentWidth() / parameters.getEnvironmentHeight();
        double fovY = 1.03; //~(pi/4 + err due to roundings)

        double Wfar = 2 * farDist * ratio * Math.tan(fovY / 2);
        double halfAlpha = Math.asin((Wfar / 2) / Math.sqrt((farDist * farDist) + (Wfar * Wfar) / 4));
        return halfAlpha;
    }

    /**
     * Generates a "triangule" that represents the field of view (horizontal
     * perspective) of the creature (what is seen on its camera).
     *
     * @param creature
     * @param g
     * @return
     */
    public Polygon getFOV() {
        double farDist = 1000; //settings from "server" (setFrustumPerspective)
        WorldPoint p = this.getPosition();
        double xCreaturePosition = p.getX();
        double yCreaturePosition = p.getY();
        Polygon pol = new Polygon();
        pol.addPoint((int) xCreaturePosition, (int) yCreaturePosition);

        double ang = getHalfAngleFOVX();
        p = this.getBottomPointFarPlane(farDist, ang);
        pol.addPoint((int) p.getX(), (int) p.getY());
        p = this.getBottomPointFarPlane(farDist, -ang);
        pol.addPoint((int) p.getX(), (int) p.getY());

        this.s.FOV = pol;
        return pol;
    }

    /**
     * Command to start the motor system of the creature.
     *
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void start() throws CommandExecException {
        CommandUtility.sendStartCreature(this.attributes.robotIndexID);
    }

    /**
     * Command to stop the motor system of the creature.
     *
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void stop() throws CommandExecException {
        CommandUtility.sendStopCreature(this.attributes.robotIndexID);
    }

    /**
     * Set the rotation around the creature's axis of symmetry. The creature's
     * movement is changed in order to make it faces towards a certain
     * direction. The creature speed (i.e. linear velocity) is the arithmetic
     * mean of Vr and Vl. The angle must be in rad.
     *
     * @param vr linear velocity of the right wheel
     * @param vl linear velocity of the left wheel
     * @param w the new pitch of the creature in rad
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void move(double vr, double vl, double w) throws CommandExecException {
        CommandUtility.sendSetAngle(this.attributes.robotIndexID, vr, vl, w);
    }

    public synchronized void moveto(double v, double x, double y) throws CommandExecException {
        //CommandUtility.sendSetAngle(this.attributes.robotIndexID, vr, vl, w);
        CommandUtility.sendGoTo(this.attributes.robotIndexID, v, v, x, y);
    }

    /**
     * The creature grasp and put an object in its sack. The object may be: food
     * or crystal.
     *
     * @param thingName the unique name of the Thing. Example:
     * Jewel_1363904023537
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void putInSack(String thingName) throws CommandExecException {
        CommandUtility.sendPutInSack(this.attributes.robotIndexID, thingName);
    }

    /**
     * The creature eats a food.
     *
     * @param thingName the unique name of the Thing. Example:
     * Jewel_1363904023537
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void eatIt(String thingName) throws CommandExecException {
        CommandUtility.sendEatIt(this.attributes.robotIndexID, thingName);
    }

    /**
     * The creature grasp and hide an object under the ground.
     *
     * @param thingName the unique name of the Thing. Example:
     * Jewel_1363904023537
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void hideIt(String thingName) throws CommandExecException {
        CommandUtility.sendHideIt(this.attributes.robotIndexID, thingName);
    }

    /**
     * The creature digs an object up. The object must have been hided before.
     *
     * @see #sendHideIt
     *
     * @param thingName the unique name of the Thing. Example:
     * Jewel_1363904023537
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void unhideIt(String thingName) throws CommandExecException {
        CommandUtility.sendUnhideIt(this.attributes.robotIndexID, thingName);
    }

    /**
     * Starts the camera (visual sensor) of the creature.
     *
     * Note: Currently, there are only 2 cameras available on the server. The
     * right camera is attached to even robotIDs and the left camera, to the odd
     * robotIDs.
     *
     * @param robotID sequence number assigned to each object once it is
     * created.
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void startCamera(String robotID) throws CommandExecException {
        CommandUtility.sendStartCamera(robotID);
    }

    /**
     * Randomly generates a new leaflet. A leaflet is a list of crystals to be
     * collected and delivered at the DeliverySpot.
     *
     * The leaflets (3 in total). Format: "Leaflet1 Leaflet2 Leaflet3" Leaflet
     * format: "[Color TotalNumber NumberCollected] Payment Example: White 2 0
     * Yellow 1 0 6 White 1 0 Magenta 2 0 5 Blue 1 0 Red 1 0 Green 1 0 24
     * Leaflet1: 2 white jewels and 1 yellow jewel. None already collected. This
     * leaflet increases the score in 6 points Leaflet2: 1 white jewel and 2
     * magenta jewels. None already collected. This leaflet increases the score
     * in 5 points Leaflet3: 1 Blue, 1 Red and 1 green jewel. None already
     * collected. This leaflet increases the score in 24 points
     *
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void genLeaflet() throws CommandExecException {
        CommandUtility.sendGenLeaflet();
    }

    /**
     * The leaflet is delivered at the DeliverySpot. All the crystals specified
     * in the leaflet were collected and the creature is at the DeliverySpot.
     *
     * @param leaflet ID
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void deliverLeaflet(String leafletID) throws CommandExecException {
        CommandUtility.sendDeliverLeaflet(this.attributes.robotIndexID, leafletID);
    }

    /**
     * Check if a specific leaflet has not been assigned to the creature yet.
     *
     * @param ID the leafletID
     * @return true: has not been assigned, so must do it; false otherwise
     */
    public synchronized boolean mustInitializeLeaflet(Long ID) {
        boolean ret = false;

        if (!ifHasLeaflet(ID)) {
            //the leaflet has not been created yet, create now.
            ret = true;
        }
        return ret;
    }

    /**
     * Retrieve the list of intersection points between the ray (direct laser)
     * and the obstacle. In case of a convex shape, 2 points are obtained. For
     * concave shapes, more points may be retrieved.
     *
     * @param th
     * @param ray
     * @return
     */
    public synchronized List<WorldPoint> getHitPoints(Thing th, Line2D.Double ray) {
        List<Line2D.Double> sides = th.getAreaSides();
        List<WorldPoint> hitPoints = new ArrayList();
        for (Line2D.Double s : sides) {
            WorldPoint hit = getIntersections(s.x1, s.y1, s.x2, s.y2, ray.x1, ray.y1, ray.x2, ray.y2);
            if (hit != null) {
                hitPoints.add(hit);
            }
        }
        return hitPoints;

    }

    public synchronized void rotate(double vel) throws CommandExecException {
        CommandUtility.sendSetAngle(this.attributes.robotIndexID, vel, -vel, vel);
    }
    
    private static WorldPoint getIntersections(double a_x1, double a_y1, double a_x2, double a_y2, double b_x1, double b_y1, double b_x2, double b_y2) {

        WorldPoint pt = null;

        Line2D.Double lineA = new Line2D.Double(a_x1, a_y1, a_x2, a_y2);
        Line2D.Double lineB = new Line2D.Double(b_x1, b_y1, b_x2, b_y2);

        if (lineA.intersectsLine(lineB)) {
            double det = (a_x1 - a_x2) * (b_y1 - b_y2) - (a_y1 - a_y2) * (b_x1 - b_x2);
            if (det == 0) {
                return null;
            }

            double xi = ((b_x1 - b_x2) * (a_x1 * a_y2 - a_y1 * a_x2) - (a_x1 - a_x2) * (b_x1 * b_y2 - b_y1 * b_x2)) / det;
            double yi = ((b_y1 - b_y2) * (a_x1 * a_y2 - a_y1 * a_x2) - (a_y1 - a_y2) * (b_x1 * b_y2 - b_y1 * b_x2)) / det;

            pt = new WorldPoint(xi, yi);
        }
        return pt;
    }

    public void setInfoThingActedUpon(String attData) {

        if (!attData.equals("NONE")) {
            try {
                infoThingActedUpon = new JSONObject(attData);

                //System.out.println("-----Thing acted upon: " + infoThingActedUpon.toString());

            } catch (JSONException ex) {
                java.util.logging.Logger.getLogger(CreatureState.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else infoThingActedUpon = null; //reset
    }

    public JSONObject getInfoThingActedUpon() {
        //may be NULL!!!! (If no contact action performed.)
        return infoThingActedUpon;
    }

}
