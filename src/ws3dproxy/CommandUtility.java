/** ***************************************************************************
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
 **************************************************************************** */
package ws3dproxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Level;
import org.json.JSONException;
import org.json.JSONObject;
import ws3dproxy.model.Creature;
import ws3dproxy.model.CreatureState;
import ws3dproxy.model.Leaflet;
import ws3dproxy.model.Thing;
import ws3dproxy.model.WorldPoint;
import ws3dproxy.util.Constants;
import ws3dproxy.util.Logger;

/**
 * Utility class to send commands to the server. The client sends commands to
 * the server in order to control the creature. Each command corresponds to an
 * action that the creature must perform or a request to change a specific
 * attribute of the creature.
 *
 * @author ecalhau
 */
public class CommandUtility {

    public static final String ERROR_CODE = "@@@";
    private static double selfX = 0.0, selfY = 0.0, wheel = 0.0, speed = 0.0, mySelfPitch = 0.0, fuel = 0.0, serotonin = 0.0, endorphine = 0.0, score = 0.0, size = 0.0;
    private static double selfX1 = 0.0, selfY1 = 0.0, selfX2 = 0.0, selfY2 = 0.0;
    private static String myselfColor = "No color yet", index = "none";
    private static String myselfName = "none";
    private static int motorSys = 2;
    private static int hasLeaflet = 0;
    private static int hasCollided = 0;
    //private static List<Leaflet> leafletList = new ArrayList<Leaflet>();
    private static List<Leaflet> leafletList;
    private static CreatureState cs;
    private static Creature creature;

    /**
     * Command to create a visual reference (e.g. arrow) at a specific point
     * within the server environment and expressed in Cartesian coordinate
     * system (x,y). Each point within the server environment is called a
     * "waypoint"
     *
     * @param x abscissa of the point
     * @param y ordinate of the point
     * @return server response: Point (x, y) where the waypoint was created.
     * Format: "x y"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewWaypoint(double x, double y) throws CommandExecException {

        SocketUtility.show("Sending waypoint: x= " + x + " y= : " + y);

        String controlMessage = "newwp " + x + " " + y;//newwp <X> <Y>
        return sendCmdAndGetResponse(controlMessage);

    }

    /**
     * Command to set the simulation game environment dimension: width, height
     * and the texture for the simulation floor.
     *
     * @param width width of the simulation environment
     * @param height height of the simulation environment
     * @return report from server in format: "width height pathToFloorTexture"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendEnvironmentDimension(int width, int height) throws CommandExecException {

        SocketUtility.show("Sending dimension: width= " + width + " height= : " + height);

        String controlMessage = "setenv " + width + " " + height;//setenv <width> <height>
        return sendCmdAndGetResponse(controlMessage);

    }

    /**
     * Command to delete the visual reference (arrow) which indicates a location
     * (waypoint) within the server environment.
     *
     * @param x x abscissa of the waypoint
     * @param y y ordinate of the waypoint
     * @return report from server: position of the deleted waypoint. Format "x
     * y"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendDelWaypoint(double x, double y) throws CommandExecException {
        SocketUtility.show("Delete waypoint: x= " + x + " y= : " + y);

        String controlMessage = "delwp " + x + " " + y;//delwp <X> <Y>
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Command to start the motor system of the creature.
     *
     * @return report from server. Format: "Run creature run..."
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendStartCreature(String robotID) throws CommandExecException {
        String controlMessage = "start " + robotID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Command to stop the motor system of the creature.
     *
     * @return report from server. Format: "Creature has stopped!!!"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendStopCreature(String robotID) throws CommandExecException {
        String controlMessage = "stop " + robotID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Send the creature to a specific waypoint (location).
     *
     * The angle of the turn is evaluated according to the direction established
     * between the points: robot location and (X,Y) position (waypoint). The
     * creature speed (i.e. linear velocity) is the arithmetic mean of Vr and
     * Vl.
     *
     * @param vr linear velocity of the right wheel
     * @param vl linear velocity of the left wheel
     * @param x abscissa of the waypoint
     * @param y ordinate of the waypoint
     * @return report from server. Format: "speed pitch"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGoTo(String robotID, double vr, double vl, double x, double y) throws CommandExecException {
        SocketUtility.show("Sending speeds: Vr= " + vr + " Vl= " + vl);
        String controlMessage = "setGoTo " + robotID + " " + vr + " " + vl + " " + x + " " + y;
        return sendCmdAndGetResponse(controlMessage);
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
     * @return report from server. Format: "speed pitch"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendSetAngle(String robotID, double vr, double vl, double w) throws CommandExecException {
        SocketUtility.show("Sending speeds: Vr= " + vr + " Vl= " + vl + " and w= " + w);

        String controlMessage = "setAngle " + robotID + " " + vr + " " + vl + " " + w;//set <ID> <Vv> <Vl> <w>
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * This command makes the creature turn a specific angle. The angle of the
     * turn is derived from the linear velocity of the wheels (Vr-right
     * Vl-left). The velocity of the creature's overall movement corresponds to
     * the speed parameter.
     *
     * Note: The final pitch of the robot depends on its original orientation
     * (pitch). HINTs: +~30deg: Vl= 11 and Vr= 0.6; +~45deg: Vl= 16 and Vr= 0.2;
     * +~60deg: Vl= 22 and Vr= 1;+~90deg: Vl= 32 and Vr= 0.6; +~180deg: Vl= 63
     * and Vr= 0.2; +~270deg: Vl= 95 and Vr= 0.8.
     *
     *
     * @param robotID sequence number assigned to each object once it is
     * created.
     * @param speed velocity of the creature's overall motion
     * @param vr right wheel velocity
     * @param vl left wheel velocity
     * @return report from server. Format: "speed pitch[rad] pitch[deg]"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendSetTurn(String robotID, double speed, double vr, double vl) throws CommandExecException {
        SocketUtility.show("Sending speeds: Vr= " + vr + " Vl= " + vl + " and speed= " + speed);

        String controlMessage = "setTurn " + robotID + " " + speed + " " + vr + " " + vl;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Randomly generates a new leaflet. A leaflet is a list of crystals to be
     * collected and delivered at the DeliverySpot.
     *
     * @return the leaflets (3 in total). Format: "Leaflet1 Leaflet2 Leaflet3"
     * Leaflet format: "[Color TotalNumber NumberCollected] Payment Example:
     * White 2 0 Yellow 1 0 6 White 1 0 Magenta 2 0 5 Blue 1 0 Red 1 0 Green 1 0
     * 24 Leaflet1: 2 white jewels and 1 yellow jewel. None already collected.
     * This leaflet increases the score in 6 points Leaflet2: 1 white jewel and
     * 2 magenta jewels. None already collected. This leaflet increases the
     * score in 5 points Leaflet3: 1 Blue, 1 Red and 1 green jewel. None already
     * collected. This leaflet increases the score in 24 points
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGenLeaflet() throws CommandExecException {
        //randomly generates a new leaflet:
        String controlMessage = new String("leaflet");
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * The leaflet is delivered at the DeliverySpot. All the crystals specified
     * in the leaflet were collected and the creature is at the DeliverySpot.
     *
     * @param robotID
     * @param leafletID
     *
     * @return report from the server. Format: "Leaflet delivered!"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendDeliverLeaflet(String robotID, String leafletID) throws CommandExecException {
        //randomly generates a new leaflet:
        String controlMessage = "deliver " + robotID + " " + leafletID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * The creature grasp and put an object in its sack. The object may be: food
     * or crystal.
     *
     * @param thingName the unique name of the Thing. Example:
     * Jewel_1363904023537
     * @return report from server with the name of the grasped Thing. Format:
     * "ThingName".
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendPutInSack(String robotID, String thingName) throws CommandExecException {
        SocketUtility.show("Sending thing name: " + thingName);

        String controlMessage = new String("sackit " + robotID + " " + thingName);//old graspit
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * The creature eats a food.
     *
     * @param thingName the unique name of the Thing. Example:
     * Jewel_1363904023537
     * @return report from server with the name of the Thing which has been
     * eaten. Format: "ThingName".
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendEatIt(String robotID, String thingName) throws CommandExecException {
        SocketUtility.show("Sending food name: " + thingName);

        String controlMessage = "eatit " + robotID + " " + thingName;//eatit <ID> <thing name>
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Command to drop a Thing which is in creature's sack.
     *
     * @param robotID sequence number assigned to each object once it is
     * @param type 3-Jewel; 21-Perishable food; 22- Non-Perishable food
     * @param color 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White
     * @return report from server with the point (x,y) where the Thing has been
     * dropped. Format: "x y".
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendDropIt(String robotID, int type, int color) throws CommandExecException {
        SocketUtility.show("Sending drop: type= " + type + " color= " + color);
        //drop <CreaturePoolIndex> <Type> <Thing color (for jewels) or any number for food> - Type: 3-Jewel;
        String controlMessage = new String("drop " + robotID + " " + type + " " + color);
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * The creature grasp and hide an object under the ground.
     *
     * @param thingName the unique name of the Thing. Example:
     * Jewel_1363904023537
     * @return report from server with the name of the Thing which has been
     * hidden. Format: "ThingName".
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendHideIt(String robotID, String thingName) throws CommandExecException {
        SocketUtility.show("Sending thing name: " + thingName);

        String controlMessage = "hideit " + robotID + " " + thingName;//hideit <ID> <thing name>
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * The creature digs an object up. The object must have been hided before.
     *
     * @see #sendHideIt
     *
     * @param thingName the unique name of the Thing. Example:
     * Jewel_1363904023537
     * @return report from server with the name of the Thing which has been
     * "unearthed". Format: "ThingName".
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendUnhideIt(String robotID, String thingName) throws CommandExecException {
        SocketUtility.show("Sending thing name: " + thingName);

        String controlMessage = new String("unhideit " + robotID + " " + thingName);//unhideit <ID> <thing name>
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Check if the simulation on the server has started.
     *
     * @return "true": has started; "false": not started yet.
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static boolean requestGameStatus() throws CommandExecException {
        boolean ret = false;
        SocketUtility.show("Sending request if game has started.");

        String controlMessage = "game";
        SocketUtility.sendMessage(controlMessage);
        String returnMessage = SocketUtility.receiveMessage();
        checkIfErrorMessage(returnMessage);
        SocketUtility.show("----->>>>Server response: " + returnMessage);

        if (returnMessage.equals("yes")) {
            ret = true;
        }
        SocketUtility.show("----->>>> " + ret);
        return ret;
    }

    /**
     * Check if the specified creature has been already created on the server.
     *
     * @param robotID an integer that is a sequence number assigned to each
     * object once it is created. It specifies a creature.
     * @return true if creature exists; false otherwise.
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static boolean ifCreatureExists(String robotID) throws CommandExecException {
        boolean ret = false;
        String firstParam = "";
        //command "check" returns two parameters. First: true if creature exists; false otherwise. 
        // Second: creatureNameID
        String controlMessage = "check " + robotID;
        StringTokenizer st = sendCmdAndGetResponse(controlMessage);
        if (st.hasMoreTokens()) {
            firstParam = st.nextToken();
            if (firstParam.equals("yes")) {
                ret = true;
            }
        }
        if (st.hasMoreTokens()) {
            myselfName = st.nextToken();

        }

        return ret;
    }

    /**
     * Check if the specified creature has been already created on the server at
     * a certain position. If it already exists but with different pitch, the
     * pitch is changed.
     *
     * @param X x of position of creature
     * @param Y y of position of creature
     * @param pitch pitch of position of creature
     * @return "": does not exist; otherwise: creatureIndex creatureNameID
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static String checkCreature(double X, double Y, double pitch) throws CommandExecException {
        String controlMessage = "checkXY " + X + " " + Y + " " + pitch;
        SocketUtility.sendMessage(controlMessage);
        String msg = SocketUtility.receiveMessage();
        if (!msg.equals("")) {
            checkIfErrorMessage(msg);
        }
        return msg;
    }

    /**
     * Delete all entities in the world (creatures and objects).
     *
     * @return "done" if successful
     * @throws CommandExecException
     */
    public static String sendResetWorld() throws CommandExecException {
        String controlMessage = "worldReset ";
        SocketUtility.sendMessage(controlMessage);
        String msg = SocketUtility.receiveMessage();
        if (!msg.equals("")) {
            checkIfErrorMessage(msg);
        }
        return "done";
    }

    /**
     * A name that identifies the client. Examples: RobotMind_0, RobotMind_1
     * etc.
     *
     * @param robotID sequence number assigned to each object once it is
     * created.
     * @return report from server with the "mind name". Format: "MindName"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer setMindName(String robotID) throws CommandExecException {
        String controlMessage = "mindName " + robotID;
        return sendCmdAndGetResponse(controlMessage);
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
     * @return report from server with the "creature index" indicating the
     * current owner of a camera. Even indexes are attached to right camera and
     * odd indexes to the left one. Example: camera 0 0
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendStartCamera(String robotID) throws CommandExecException {
        String controlMessage = "camera " + robotID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Get the values of the simulation parameters: width, height, xDS, yDS
     * width and height of the environment of the game (simulation) (xDS, yDS)
     * location of the DeliverySpot.
     *
     * @return a string in the format "width height xDS yDS"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetSimulationParameters() throws CommandExecException {
        String controlMessage = "getsimulpars";
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the amount of free memory in the Java Virtual Machine.
     *
     * @return a string indicating an approximation to the total amount of
     * memory currently available for future allocated objects, measured in
     * bytes.
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetFreeMemory() throws CommandExecException {
        String controlMessage = "memory";
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the current time in milliseconds.
     *
     * @return a string indicating the difference, measured in milliseconds,
     * between the current time and midnight, January 1, 1970 UTC.
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetClock() throws CommandExecException {
        String controlMessage = "getclock";
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Command to get the creature position.
     *
     * @param robotID sequence number assigned to each object once it is
     * created.
     * @return the creature position in the format: "X Y Pitch" Example: 287.0
     * 391.0 0.0
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetCreaturePosition(String robotID) throws CommandExecException {
        String controlMessage = "getcreatcoords " + robotID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the creature's speed and fuel current values.
     *
     * @param robotID sequence number assigned to each object once it is
     * created.
     * @return creature's speed and fuel values in the format: "speed fuel"
     * Example: 1.0 1000
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetCreatureInfo(String robotID) throws CommandExecException {
        String controlMessage = "getcreatinfo " + robotID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the content of the creature's bag.
     *
     * @param robotID sequence number assigned to each object once it is
     * created.
     * @return Return the creature's bag content: Total number of Food; Total
     * number of Crystals; Number of PerishableFood; Number of
     * Non-PerishableFood; Number of crystals each color in the sequence: RED,
     * GREEN, BLUE, YELLOW, MAGENTA, WHITE.
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetCreatureBagContent(String robotID) throws CommandExecException {
        String controlMessage = "getsack " + robotID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the affordances of a Thing. The server respond with the list of
     * affordances code. Codes: Affordance__VIEWABLE = 30; Affordance__HIDEABLE
     * = 31; Affordance__UNHIDEABLE = 32; Affordance__GRASPABLE = 33;
     * Affordance__EATABLE = 34; Affordance__PUTINBAGABLE = 35;//sth that can be
     * put in a bag; Affordance__OPENABLE = 36; //sth that can be opened (eg. a
     * cage); Affordance__CLOSEABLE = 37;//sth than be closed (eg. a cage);
     * Affordance__INSERTABLE = 38;//sth than can contain another thing (which
     * had been inserted into this sth)[eg. a container];
     * Affordance__REMOVEFROMABLE = 39;//sth from whose inside another thing is
     * removed (eg. a container)
     *
     * @param thingID refers to a ThingID. Example: Brick_1364939987440
     * @return list of affordance codes. Example: 30 31 32 Meaning:
     * Affordance__VIEWABLE Affordance__HIDEABLE Affordance__UNHIDEABLE
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetAffordances(String thingID) throws CommandExecException {
        String controlMessage = "getcreatcoords " + thingID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the simulation environment dimension.
     *
     * @return environment dimension in the format: "width height". Example: 800
     * 600
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetEnvironmentDimension() throws CommandExecException {
        String controlMessage = "getenvironmen ";
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the number of creatures and other Things currently at the
     * simulation environment.
     *
     * @return server response with the number of entities. Format:
     * "NumCreatures NumOtherThings"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetNumberOfEntities() throws CommandExecException {
        String controlMessage = "getNumEntities ";
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the creatures and other Things currently at the simulation
     * environment.
     *
     * @return server response with the entities. Format: "numberOfEntities
     * ThingName1 category isOccluded X1 Y1 X2 Y2 pitch colorIndex
     * ThingName2..." Example: 3 Creature_1364966079239 0 0 275.0 275.0 373.0
     * 373.0 0 Jewel_1364966084869 3 0 552.0 552.0 355.0 355.0 1
     * NPFood_1364966088030 22 0 469.0 469.0 311.0 311.0 1
     *
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetWorldEntities() throws CommandExecException {
        String controlMessage = "getall ";
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Returns the Thing which is closest to a creature.
     *
     * @param robotID sequence number assigned to each object once it is
     * @return server response with the number of entities. Format: "ThingName1
     * category isOccluded X1 X2 Y1 Y2 pitch hardness energy shininess
     * colorName"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetClosest(String robotID) throws CommandExecException {
        String controlMessage = "closest";
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Create a new creature.
     *
     * @param x abscissa of the location of the creature
     * @param y ordinate of the location of the creature
     * @param pitch direction to which the creature is headed
     * @return server successful response with creature position. Format:
     * "CreatureIndex CreatureName X Y Pitch"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewCreature(double X, double Y, double pitch) throws CommandExecException {
        String controlMessage = "new " + X + " " + Y + " " + pitch;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Create a new creature (coloured version).
     *
     * @param x abscissa of the location of the creature
     * @param y ordinate of the location of the creature
     * @param pitch direction to which the creature is headed
     * @return server successful response with creature position. Format:
     * "CreatureIndex CreatureName X Y Pitch"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewCreature(double X, double Y, double pitch, int color) throws CommandExecException {
        String controlMessage = "new " + X + " " + Y + " " + pitch + " " + color;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Creates a new cage.
     *
     * @param x abscissa of the location of the cage
     * @param y ordinate of the location of the cage
     * @return server successful response with cage position. Format: "CageName
     * X Y"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewCage(double X, double Y) throws CommandExecException {
        String controlMessage = "cage " + X + " " + Y;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Creates a new food entity.
     *
     * @param type 0-perishable 1-non-perishable
     * @param x abscissa of the location of the food
     * @param y ordinate of the location of the food
     * @return server successful response with food position. Format: "FoodName
     * X Y"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewFood(int type, double X, double Y) throws CommandExecException {
        String controlMessage = "food " + type + " " + X + " " + Y;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Creates several food at once.
     *
     * @param type 0-perishable 1-non-perishable
     * @param number number of food entities to create
     * @param X_Y text with the list of location where a new food must be
     * created
     * @return server successful response with food names and positions. Format:
     * "FoodName0 X Y FoodName1 X Y ..."
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewFoodInBatch(int type, int number, String X_Y) throws CommandExecException {
        String controlMessage = "batchoffood " + type + " " + number + " " + X_Y;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Creates several jewels at once.
     *
     * @param type 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White
     * @param number number of jewel entities to create
     * @param X_Y text with the list of location where a new jewel must be
     * created
     * @return server successful response with jewel names and positions.
     * Format: "JewelName0 X Y JewelName1 X Y ..."
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewJewelsInBatch(int type, int number, String X_Y) throws CommandExecException {
        String controlMessage = "batchofjewels " + type + " " + number + " " + X_Y;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Creates a new jewel entity.
     *
     * @param type 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White
     * @param x abscissa of the location of the jewel
     * @param y ordinate of the location of the jewel
     * @return server successful response with jewel position. Format:
     * "JewelName X Y"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewJewel(int type, double X, double Y) throws CommandExecException {
        String controlMessage = "jewel " + type + " " + X + " " + Y;
        return sendCmdAndGetResponse(controlMessage);
    }

    public static synchronized StringTokenizer sendNewDeliverySpot(int type, double X, double Y) throws CommandExecException {
        String controlMessage = "newDeliverySpot " + type + " " + X + " " + Y;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Creates a new brick entity.
     *
     * @param type 0-Red 1-Green 2-Blue 3-Yellow 4-Magenta 5-White
     * @param x abscissa of the location of the brick
     * @param y ordinate of the location of the brick
     * @return server successful response with brick position. Format:
     * "BrickName X Y"
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendNewBrick(int type, double X1, double Y1, double X2, double Y2) throws CommandExecException {
        String controlMessage = "brick " + type + " " + X1 + " " + Y1 + " " + X2 + " " + Y2;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * The creature state refers to its location and camera content (list of
     * objects that have been captured).
     *
     * @param robotNameID name that uniquely identifies the creature in the
     * system
     *
     * @return list of creature's attributes and the objects that have been
     * captured by the camera (creature's visual system).
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendGetCreatureState(String robotNameID) throws CommandExecException {
        String controlMessage = "getcreaturestate " + robotNameID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Creature's energy is restored.
     *
     * @param robotID sequence number assigned to each object once it is
     * created.
     * @return creature's fuel value
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendRefuel(String robotID) throws CommandExecException {
        String controlMessage = "refuel " + robotID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Delete an entity: creature or other Thing.
     *
     * @param type 0: food, jewel or blick; 1: creature.
     * @param thingID sequence number assigned to each creature and to other
     * entities (different pools)
     * @return the name of the deleted Thing
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized StringTokenizer sendDeleteThing(int type, String thingID) throws CommandExecException {
        String controlMessage = "deleteth " + type + " " + thingID;
        return sendCmdAndGetResponse(controlMessage);
    }

    /**
     * Creates a creature with its initial attribute values: either default
     * values or those got from a creature already present in the server.
     *
     * @param indexID
     * @param nameID
     * @return
     */
    public static synchronized Creature initializeCreature(String indexID, String nameID) {
        myselfName = nameID;
        creature = initializeCreature(indexID);
        return creature;
    }

    /**
     * Creates a creature with its initial attribute values: either default
     * values or those got from a creature already present in the server.
     *
     * @param indexID
     * @return
     */
    public static synchronized Creature initializeCreature(String indexID) {
        creature = null;
        try {
            //myselfName was already initialized in ifCreatureExists
            //return initializeCreature(indexID, myselfName);
            cs = CommandUtility.getCreatureState(myselfName);
            myselfColor = cs.getColorName();
            speed = cs.getSpeed();
            wheel = cs.getWheel();
            size = cs.getCreatureSize();
            mySelfPitch = cs.getPitch();
            motorSys = cs.getMotorSys();
            fuel = cs.getFuel();
            serotonin = cs.getSerotonin();
            endorphine = cs.getEndorphine();
            creature = Creature.getInstance(cs);

        } catch (CommandExecException ex) {
            Logger.logException(CommandUtility.class.getName(), ex);
        }
        return creature;
    }

    /**
     * Get information regarding the creature state and the content of its
     * visual system (camera) according to a protocol established with the
     * server. The information is used to create the list of objects (Thing)
     * that are manipulated in the "mind".
     *
     * @param unique ID of the Creature (e.g. Creature_1364966079239)
     * @return the creature state that encapsulates its attributes such as
     * position, color, pitch, energy level etc
     * @throws CommandExecException An exception is thrown in case of missing or
     * invalid parameter
     */
    public static synchronized CreatureState getCreatureState(String nameID) throws CommandExecException {

        HashMap<String, Integer[]> leafletItemsMap = new HashMap<String, Integer[]>();

        String command = "";

        double x1 = 0.0, x2 = 0.0, y1 = 0.0, y2 = 0.0, thingPitch = 0.0, hardness = 0.0, energy = 0.0, shininess = 0.0;
        double comX = 0.0, comY = 0.0;
        float distance = 0;
        String thingName = "none";
        int category = 0;
        int isOccluded = 0;
        int number = 0; //number of Thing in visual sensor
        int spuriousNum = 0;
        int numberOfLeaflets = 0;
        int numberOfLeafletItems = 0;
        long leafletID = 0;
        int payment = 0;
        int situationOfLeaflet = 0;
        String itemKey = "";
        Integer totalNumber = 0;
        Integer collected = 0;
        String thingColor = "No color yet";
        List<Thing> thingsInVision = new ArrayList<Thing>();
        List<Thing> thingsInFrustrum = new ArrayList<Thing>();
        String actionData = " ";

        StringTokenizer st = CommandUtility.sendGetCreatureState(nameID);

        ///////////////////Creature data:
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - myName missing!");
        } else {
            command = st.nextToken();
            myselfName = command; //string
        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - index is missing!");
        } else {
            index = st.nextToken();
        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - Center of mass X is missing!");
        } else {
            command = st.nextToken();
            selfX = Double.parseDouble(command);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - Center of mass Y is missing!");
        } else {
            command = st.nextToken();
            selfY = Double.parseDouble(command);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - creature size missing!");
        } else {
            command = st.nextToken();
            size = Double.parseDouble(command);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - pitch missing!");
        } else {
            command = st.nextToken();
            mySelfPitch = Double.parseDouble(command);
//            mySelfPitch = Math.toRadians(mySelfPitch) + Constants.M_PI_2;
            mySelfPitch = Math.toRadians(mySelfPitch);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - motor system missing!");

        } else {
            command = st.nextToken();
            motorSys = Integer.parseInt(command);
        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - wheel missing!");
        } else {
            command = st.nextToken();
            wheel = Double.parseDouble(command);
        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - speed missing!");
        } else {
            command = st.nextToken();
            speed = Double.parseDouble(command);
        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - fuel missing !");
        } else {
            command = st.nextToken();
            fuel = Double.parseDouble(command);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - serotonin missing !");
        } else {
            command = st.nextToken();
            serotonin = Double.parseDouble(command);
        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - endorphine missing !");
        } else {
            command = st.nextToken();
            endorphine = Double.parseDouble(command);
        }

        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - score is missing !");
        } else {
            command = st.nextToken();
            score = Double.parseDouble(command);
        }

        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - X1 is missing!");
        } else {
            command = st.nextToken();
            selfX1 = Double.parseDouble(command);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - Y1 is missing!");
        } else {
            command = st.nextToken();
            selfY1 = Double.parseDouble(command);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - X2 is missing!");
        } else {
            command = st.nextToken();
            selfX2 = Double.parseDouble(command);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - Y2 is missing!");
        } else {
            command = st.nextToken();
            selfY2 = Double.parseDouble(command);

        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - creature's color missing !");
        } else {
            command = st.nextToken();
            myselfColor = command; //string
        }
        /**
         * Thing upon which an action was performed (except SEE):
         */
        if (!st.hasMoreTokens()) {
            System.out.println("No contact action performed. Thing upon which an action was performed is empty!");
            Logger.logErr("No contact action performed. Thing upon which an action was performed is empty!");
        } else {
            actionData = st.nextToken();
            //System.out.println("--------------------actionData: " + actionData);
        }

        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - hasCollided missing !");
        } else {
            command = st.nextToken();
            hasCollided = Integer.parseInt(command);
        }
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - hasLeaflet missing !");
        } else {
            command = st.nextToken();
            hasLeaflet = Integer.parseInt(command);
        }

        if (hasLeaflet == 1) { //true
            /*
             * Read leaflets:
             */
            leafletList = new ArrayList<Leaflet>();
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing number of leaflets!");
            } else {
                command = st.nextToken();
                numberOfLeaflets = Integer.parseInt(command);
            }
            for (int i = 0; i <= numberOfLeaflets - 1; i++) { //loop to create leaflets

                if (!st.hasMoreTokens()) {
                    Logger.logErr("Error - missing leaflet ID!");
                } else {
                    command = st.nextToken();
                    leafletID = Long.parseLong(command);
                }

                if (!st.hasMoreTokens()) {
                    Logger.logErr("Error - missing number of types in leaflet!");
                } else {
                    command = st.nextToken();
                    numberOfLeafletItems = Integer.parseInt(command);
                }
                for (int j = 0; j <= numberOfLeafletItems - 1; j++) { //loop to create a leaflet
                    if (!st.hasMoreTokens()) {
                        Logger.logErr("Error - type is missing!");
                    } else {
                        command = st.nextToken();
                        itemKey = command;
                    }
                    if (!st.hasMoreTokens()) {
                        Logger.logErr("Error - total number of a specific type is missing!");
                    } else {
                        command = st.nextToken();
                        totalNumber = Integer.parseInt(command);
                    }
                    if (!st.hasMoreTokens()) {
                        Logger.logErr("Error - number of collected items of a type is missing!");
                    } else {
                        command = st.nextToken();
                        collected = Integer.parseInt(command);
                    }
                    Integer[] values = {0, 0};
                    values[0] = totalNumber; //total number of a type
                    values[1] = collected; //how many have been collected
                    leafletItemsMap.put(itemKey, values);
                }
                if (!st.hasMoreTokens()) {
                    Logger.logErr("Error - missing payment of leaflet!");
                } else {
                    command = st.nextToken();
                    payment = Integer.parseInt(command);
                }
                if (!st.hasMoreTokens()) {
                    Logger.logErr("Error - missing situation of leaflet!");
                } else {
                    command = st.nextToken();
                    if (command.equals("true")) {
                        situationOfLeaflet = 1;
                    } else {
                        situationOfLeaflet = 0;

                    }

                }
                leafletList.add(new Leaflet(leafletID, leafletItemsMap, payment, situationOfLeaflet));
                leafletItemsMap.clear();
            }

        } else {

            leafletList = new ArrayList<Leaflet>();

            //consume spurious " 0" of the empty leaflet list:
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - expecting 0 of the leaflet empty list!");
            } else {
                command = st.nextToken();
                spuriousNum = Integer.parseInt(command);
            }
        }

        //update creature state in Status:
        cs = CreatureState.getInstance(index, myselfName, myselfColor, speed, wheel, mySelfPitch, motorSys, fuel, serotonin, endorphine, score, new WorldPoint(selfX, selfY), selfX1, selfY1, selfX2, selfY2, hasCollided, hasLeaflet, leafletList);
        /**
         * Read Contact and Visual sensors!!!!!!
         */
        if (!st.hasMoreTokens()) {
            Logger.logErr("Error - missing number of things!");
        } else {
            command = st.nextToken();
            number = Integer.parseInt(command);
        }

        if (number > 0) {
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
                    comX = Double.parseDouble(command);
                }
                if (!st.hasMoreTokens()) {
                    Logger.logErr("Error - missing center of mass Y!");
                } else {
                    command = st.nextToken();
                    comY = Double.parseDouble(command);
                }
                //
                switch (isOccluded) {
                    case 0: //actually visible

                        if (cs.getThingsInVisionMap().containsKey(thingName)) {
                            thingsInVision.add(cs.getThingsInVisionMap().get(thingName).update(x1, y1, x2, y2, comX, comY, thingColor, isOccluded, energy, thingPitch, shininess));
                        } else {
                            thingsInVision.add(createThing(thingName, category, isOccluded, thingColor, x1, y1, x2, y2, comX, comY, thingPitch, hardness, energy, shininess));
                        }
                    default: //add both cases (occluded or not) in the next list:
                        if (cs.getThingsInCameraFrustrumMap().containsKey(thingName)) {
                            thingsInFrustrum.add(cs.getThingsInCameraFrustrumMap().get(thingName).update(x1, y1, x2, y2, comX, comY, thingColor, isOccluded, energy, thingPitch, shininess));
                        } else {
                            thingsInFrustrum.add(createThing(thingName, category, isOccluded, thingColor, x1, y1, x2, y2, comX, comY, thingPitch, hardness, energy, shininess));
                        }
                }

            }
        }

        //may be "NONE":
        cs.setInfoThingActedUpon(actionData);

        cs.setThingsInVision(thingsInVision);
        cs.setThingsInCameraFrustrum(thingsInFrustrum);

        cs.notifyMyObservers();
        return cs;
    }

    private static void checkIfErrorMessage(String resp) throws CommandExecException {
        StringTokenizer st = new StringTokenizer(resp);
        if (st.hasMoreTokens()) {
            String startResp = st.nextToken();
            if (startResp.equals(ERROR_CODE)) {
                throw new CommandExecException(resp.toString());
            }
        }
    }

    private static StringTokenizer sendCmdAndGetResponse(String formattedCmd) throws CommandExecException {
        SocketUtility.sendMessage(formattedCmd);
        String returnMessage = SocketUtility.receiveMessage();
        checkIfErrorMessage(returnMessage);
        StringTokenizer st = new StringTokenizer(returnMessage);
        SocketUtility.show("----->>>>Server response: " + returnMessage);
        return st;
    }

    //TODO update an already created THING instead of keep creating objects
    private static Thing createThing(String name, int category, int ifIsOccluded, String thingColor, double x1, double y1, double x2, double y2, double comX, double comY, double thingPitch, double hardness, double energy, double shininess) {

        return new Thing(name, category, x1, y1, x2, y2, comX, comY, thingColor, ifIsOccluded, thingPitch, hardness, energy, shininess) {
        };
    }

    private static Leaflet createLeaflet(Long ID, HashMap items, int payment, int situation) {
        return new Leaflet(ID, items, payment, situation);
    }
}
