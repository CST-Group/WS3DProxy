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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.logging.Level;
import ws3dproxy.CommandExecException;
import ws3dproxy.CommandUtility;
import ws3dproxy.resourcesgenerator.ResourcesGenerator;
import ws3dproxy.util.Constants;
import ws3dproxy.util.Logger;

/**
 * The World or "environment" known by the Creature
 *
 * @author eccastro
 */
public class World {

    private static int environmentWidth;
    private static int environmentHeight;
    private static World instance = null;
    private static WorldPoint deliverySpot;

    //All Things except myself
    private HashMap<String, Thing> allThings = new HashMap();

    private World() {
        //System.out.println("Creating new World");
        try {
            getDimensionAndDeliverySpot();
        } catch (Exception e) {

        }
    }

    public static World getInstance() {
        if (instance == null) {
            instance = new World();
        }

        return instance;
    }

    public synchronized int getEnvironmentWidth() {
        return environmentWidth;
    }

    public synchronized void setEnvironmentWidth(int environmentWidth) {
        this.environmentWidth = environmentWidth;
    }

    public synchronized int getEnvironmentHeight() {
        return environmentHeight;
    }

    public synchronized void setEnvironmentHeight(int environmentHeight) {
        this.environmentHeight = environmentHeight;
    }

    public String toString() {
        StringBuffer s = new StringBuffer();
        s.append("Simulation Parameters\n");
        s.append("Environment width: " + environmentWidth + " and height: "
                + environmentHeight + "\n");
        return s.toString();
    }

    public synchronized HashMap<String, Thing> getThingsMap() {

        return this.allThings;
    }

    public synchronized void updateThing(Thing th) {

        this.allThings.put(th.getName(), th);
    }

    public synchronized void removeThingFromMap(String name) {

        this.allThings.remove(name);
    }

    public synchronized List<Thing> getEveryThing() {

        return new ArrayList(this.allThings.values());
    }

    /**
     * Get the World's width and height and also the position (xDS, yDS) of the
     * DeliverySpot.
     *
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized void getDimensionAndDeliverySpot() throws CommandExecException {
        StringTokenizer st = CommandUtility.sendGetSimulationParameters();
        double xds, yds;

        if (st.hasMoreTokens()) {
            World.environmentWidth = Integer.parseInt(st.nextToken());
        } else {
            Logger.logErr("Error: missing World width");
            return;
        }
        if (st.hasMoreTokens()) {
            World.environmentHeight = Integer.parseInt(st.nextToken());
        } else {
            Logger.logErr("Error: missing World height");
            return;
        }
        if (st.hasMoreTokens()) {
            xds = Double.parseDouble(st.nextToken());
        } else {
            Logger.logErr("Error: missing x of DeliverySpot");
            return;
        }
        if (st.hasMoreTokens()) {
            yds = Double.parseDouble(st.nextToken());
            World.deliverySpot = new WorldPoint(xds, yds);
        } else {
            Logger.logErr("Error: missing y of DeliverySpot");
            return;
        }

    }

    /**
     * Command to create a visual reference (e.g. arrow) at a specific point
     * within the server environment and expressed in Cartesian coordinate
     * system (x,y). Each point within the server environment is called a
     * "waypoint"
     *
     * @param x abscissa of the point
     * @param y ordinate of the point
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void createWaypoint(double x, double y) throws CommandExecException {
        CommandUtility.sendNewWaypoint(x, y);

    }
    
    
    public static synchronized void createDeliverySpot(double x, double y) throws CommandExecException{
        CommandUtility.sendNewDeliverySpot(4, x, y);
        setDeliverySpot(x,y);
    }

    /**
     * Command to set the simulation game environment dimension: width, height
     * and the texture for the simulation floor.
     *
     * @param width width of the simulation environment
     * @param height height of the simulation environment
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void setEnvironmentDimension(int width, int height) throws CommandExecException {
        CommandUtility.sendEnvironmentDimension(width, height);
    }

    /**
     * Command to delete the visual reference (arrow) which indicates a location
     * (waypoint) within the server environment.
     *
     * @param x x abscissa of the waypoint
     * @param y y ordinate of the waypoint
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public synchronized void deleteWaypoint(double x, double y) throws CommandExecException {
        CommandUtility.sendDelWaypoint(x, y);
    }

    /**
     * Check if the simulation on the server has started.
     *
     * @return "true": has started; "false": not started yet.
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public boolean requestGameStatus() throws CommandExecException {
        return CommandUtility.requestGameStatus();
    }

    public static void setDeliverySpot(double x, double y) {
        deliverySpot = new WorldPoint(x, y);
    }

    public static WorldPoint getDeliverySpot() {
        return deliverySpot;
    }

    /**
     * Returns the number of creatures and other Things currently at the
     * simulation environment.
     *
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized void getNumberOfEntities() throws CommandExecException {
        CommandUtility.sendGetNumberOfEntities();
    }

    /**
     * Returns the creatures and other Things currently at the simulation
     * environment.
     *
     * @return list of all entities (creatures, food, jewel and bricks) that
     * exist in the world
     *
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized List<Thing> getWorldEntities() throws CommandExecException {

        List<Thing> all = new ArrayList<Thing>();

        StringTokenizer st;
        String ret = "";
        String command = "";

        double x1 = 0.0, x2 = 0.0, y1 = 0.0, y2 = 0.0, thingPitch = 0.0, hardness = 0.0, energy = 0.0, shininess = 0.0, x = 0.0, y = 0.0;
        String thingName = "none";
        int category = 0;
        int isOccluded = 0;
        int number = 0; //number of Thing
        String thingColor = "No color yet";

        st = CommandUtility.sendGetWorldEntities();

        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - missing number of things!");
        } else {
            command = st.nextToken();
            try {
               number = Integer.parseInt(command);
            } catch (NumberFormatException e) {
                System.out.println("Error in World:getWorldEntities> "+command);
            }   
        }

        for (int i = 0; i <= number - 1; i++) { //loop to create Things
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing name!");
            } else {
                command = st.nextToken();
                if (command.equals("||")) {
                    command = st.nextToken();
                }
                thingName = command;

            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing category!");
            } else {
                command = st.nextToken();
                category = Integer.parseInt(command);

            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing if occluded!");
            } else {
                command = st.nextToken();
                isOccluded = Integer.parseInt(command);

            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing X1!");
            } else {
                command = st.nextToken();
                x1 = Double.parseDouble(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing X2!");
            } else {
                command = st.nextToken();
                x2 = Double.parseDouble(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing Y1!");
            } else {
                command = st.nextToken();
                y1 = Double.parseDouble(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing Y2!");
            } else {
                command = st.nextToken();
                y2 = Double.parseDouble(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing pitch !");
            } else {
                command = st.nextToken();
                thingPitch = Double.parseDouble(command);
                thingPitch = Math.toRadians(thingPitch);//Math.toRadians(thingPitch) + Constants.M_PI_2;

            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing hardness!");
            } else {
                command = st.nextToken();
                hardness = Double.parseDouble(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing energy!");
            } else {
                command = st.nextToken();
                energy = Double.parseDouble(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing shininess!");
            } else {
                command = st.nextToken();
                shininess = Double.parseDouble(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing color of thing!");
            } else {
                command = st.nextToken();
                thingColor = command;
            }

            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing center of mass X!");
            } else {
                command = st.nextToken();
                x = Double.parseDouble(command);
            }
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing center of mass Y!");
            } else {
                command = st.nextToken();
                y = Double.parseDouble(command);
            }

            all.add(createThing(thingName, category, isOccluded, thingColor, x1, y1, x2, y2, x, y, thingPitch, hardness, energy, shininess));
        }
        return all;
    }

    private static Thing createThing(String name, int category, int ifIsOccluded, String thingColor, double x1, double y1, double x2, double y2, double comX, double comY, double thingPitch, double hardness, double energy, double shininess) {

        return new Thing(name, category, x1, y1, x2, y2, comX, comY, thingColor, ifIsOccluded, thingPitch, hardness, energy, shininess) {
        };
    }

    /**
     * Creates a new brick entity.
     *
     * @param type 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White
     * @param x abscissa of the location of the brick
     * @param y ordinate of the location of the brick
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized void createBrick(int type, double X1, double Y1, double X2, double Y2) throws CommandExecException {
        CommandUtility.sendNewBrick(type, X1, Y1, X2, Y2);
    }

    /**
     * Creates a new cage.
     *
     * @param x abscissa of the location of the cage
     * @param y ordinate of the location of the cage
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized void createCage(double X, double Y) throws CommandExecException {
        CommandUtility.sendNewCage(X, Y);
    }

    /**
     * Delete all entities in the world (Creatures and objects).
     *
     * @throws CommandExecException
     */
    public synchronized void reset() throws CommandExecException {
        CommandUtility.sendResetWorld();
    }

    /**
     * Creates a new food entity.
     *
     * @param type 0-perishable 1-non-perishable
     * @param x abscissa of the location of the food
     * @param y ordinate of the location of the food
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized void createFood(int type, double X, double Y) throws CommandExecException {
        CommandUtility.sendNewFood(type, X, Y);
    }

    public static synchronized void grow() {
        grow(Constants.TIMEFRAME);
    }

    public static synchronized void grow(int time) {
        try {
            if (time <= 0) {
                time = Constants.TIMEFRAME;
            }
            getDimensionAndDeliverySpot();
            ResourcesGenerator rg = new ResourcesGenerator(time, World.environmentWidth, World.environmentHeight, World.deliverySpot.getX(), World.deliverySpot.getY());
            rg.start();
        } catch (CommandExecException ex) {
            Logger.logException(World.class.getName(), ex);
        }
    }

    /**
     * Creates several food at once.
     *
     * @param type 0-perishable 1-non-perishable
     * @param number number of food entities to create
     * @param X_Y text with the list of location where a new food must be
     * created
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized void createFoodInBatch(int type, int number, String X_Y) throws CommandExecException {
        CommandUtility.sendNewFoodInBatch(type, number, X_Y);
    }

    /**
     * Creates a new jewel entity.
     *
     * @param type 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White
     * @param x abscissa of the location of the jewel
     * @param y ordinate of the location of the jewel
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized void createJewel(int type, double X, double Y) throws CommandExecException {
        CommandUtility.sendNewJewel(type, X, Y);
    }

    /**
     * Creates several jewels at once.
     *
     * @param type 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White
     * @param number number of jewel entities to create
     * @param X_Y text with the list of location where a new jewel must be
     * created
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized void createJewelsInBatch(int type, int number, String X_Y) throws CommandExecException {
        CommandUtility.sendNewJewelsInBatch(type, number, X_Y);
    }

    public static synchronized WorldPoint getRandomTarget() {
        WorldPoint wp = new WorldPoint();
        Random rdX = new Random();
        Random rdY = new Random();

        do {
            wp.setX(rdX.nextDouble() * environmentWidth);
            wp.setY(rdY.nextDouble() * environmentHeight);

        } while (!checkAvailability(wp.getX(), wp.getY()));

        return wp;
    }

    private static boolean checkAvailability(double cX, double cY) {

        List<Thing> allThings;
        boolean ret = true;
        try {
            allThings = getWorldEntities();

            for (Thing each : allThings) {
                if (each.secAreaContain(cX, cY)) {
                    return false;
                }

            }
        } catch (CommandExecException ex) {
            java.util.logging.Logger.getLogger(World.class.getName()).log(Level.SEVERE, null, ex);
        }

        return ret;
    }

}
