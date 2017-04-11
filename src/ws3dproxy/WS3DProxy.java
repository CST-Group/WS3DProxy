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

import java.util.StringTokenizer;
import ws3dproxy.model.Creature;
import ws3dproxy.model.World;
import ws3dproxy.util.Logger;

/**
 * Class responsible to connect a client to the WorldServer3D server.
 *
 * @author ecalhau
 */
public class WS3DProxy {

    /**
     * IP address or name of remote host (or localhost)
     */
    private String host;
    /**
     * Port number to connect to server
     */
    private int port;
    private Creature creature = null;
    private String robotIndexID, robotNameID;
    private World world = null; //also referenced by "Environment" in comments
    
    private static final long xDefault = 400;
    private static final long yDefault = 300;
    private static final long pitchDefault = 0;

    //public static final Logger logger = Logger.getLogger(WS3DProxy.class.getName());

    /**
     * A network socket is created and client server communication is
     * established.
     *
     * @param host IP address or localhost for socket connection
     * @param port port to listen to
     */
    public WS3DProxy(String host, int port) {
        this.host = host;
        this.port = port;
        connect();
        this.world = World.getInstance();
    }

    /**
     * A network socket is created and client server communication is
     * established.
     *
     * Default host: localhost Default port: 4011
     */
    public WS3DProxy() {
        this("localhost", 4011);
        //connect();
        this.world = World.getInstance();
    }

    /**
     * A network socket is created and client server communication is
     * established.
     */
    private void connect() {
        /* Server socket to receive connections */
        SocketUtility.createSocket(host, port);
    }
    
    /**
     * Creature is created in mind. Note: The creature is created at (400, 300)
     * with pitch = 0
     *
     * @return the creature
     * @throws CommandExecException
     */
    public synchronized Creature createCreature() throws CommandExecException {
        return createCreature(xDefault, yDefault, pitchDefault);
    }
    
    /**
     * A creature at position (X,Y) is returned. 
     * A new one is created if necessary. 
     * Note that if a creature already exists at (X,Y) with a different pitch
     * the pitch is changed.
     *
     * @param X
     * @param Y
     * @param pitch in degrees
     * @return the creature created
     * @throws CommandExecException
     */
    public synchronized Creature createCreature(double x, double y, double pitch) throws CommandExecException {
        return(createCreature(x,y,pitch,0));
    }

    /**
     * A creature at position (X,Y) is returned. 
     * A new one is created if necessary. 
     * Note that if a creature already exists at (X,Y) with a different pitch
     * the pitch is changed.
     *
     * @param X
     * @param Y
     * @param pitch in degrees
     * @param color : 0 (yellow) or 1 (red)
     * @return the creature created
     * @throws CommandExecException
     */
    public synchronized Creature createCreature(double x, double y, double pitch, int color) throws CommandExecException {
        String cIndex = CommandUtility.checkCreature(x, y, pitch);
        if (cIndex.equals("")) {
            StringTokenizer st = CommandUtility.sendNewCreature(x, y, pitch, color);
            if (st.hasMoreTokens()) {
                this.robotIndexID = st.nextToken();
            }
            if (st.hasMoreTokens()) {
                this.robotNameID = st.nextToken();
            }
        }else{
            String[] split = cIndex.split(" ");

            this.robotIndexID = split[0];
            this.robotNameID =  split[1];
        }

        creature = CommandUtility.initializeCreature(robotIndexID, robotNameID);
        try {
            //This delay is a precaution, since the creature takes a few milliseconds to be set in the JME scene graph
            //Otherwise, the return of the updateStatus might be a void response.
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.logException(WS3DProxy.class.getName(), ex);
        }
        if (creature == null) System.out.println("Problem in the creation of the creature ..."+this.robotIndexID+" "+this.robotNameID);
        else System.out.println("Creature "+creature.getName()+" created ...!");
        creature.updateState();
        creature.genLeaflet();
        creature.startCamera(robotIndexID);
        return creature;
    }

    /**
     * Return a certain Creature if it exists.
     * @param robotIndex
     * @return Creature if it exists; null otherwise
     * @throws CommandExecException 
     */
    public synchronized Creature getCreature(String robotIndex) throws CommandExecException {

        if (CommandUtility.ifCreatureExists(robotIndex)) {
            this.robotIndexID = robotIndex;
            creature = CommandUtility.initializeCreature(robotIndexID);
            if (creature != null) {
                creature = creature.updateState();
                creature.genLeaflet();
                creature.startCamera(robotIndexID);
                return creature;
            }
           
        } return null;

    }

    public synchronized void setWorld(World w) {
        this.world = w;
    }

    
    /**
     * Returns the World (also referenced as Environment) known by the creature.
     * The location of the Delivery Spot is also returned.
     */
    public synchronized World getWorld() {
        double xDS = 0.0, yDS = 0.0; //delivery spot
        try {
            StringTokenizer st;

            st = CommandUtility.sendGetSimulationParameters();


            if (!st.hasMoreTokens()) {
                Logger.logErr( "Width missed!");
            }
            String command = st.nextToken();
            //System.out.println("Command: "+command);
            world.setEnvironmentWidth(Integer.parseInt(command));
            if (!st.hasMoreTokens()) {
                Logger.logErr( "Height missed!");
            }
            command = st.nextToken();
            world.setEnvironmentHeight(Integer.parseInt(command));
            /*
             * Set current Delivery Spot:
             */
            if (!st.hasMoreTokens()) {
                Logger.logErr("Error - missing delivery spot coord X!");
            } else {
                command = st.nextToken();
                xDS = Double.parseDouble(command);
            }
            Logger.logErr("xDeliverySpot= " + xDS);
            if (!st.hasMoreTokens()) {
                Logger.logErr( "Error - missing delivery spot coord Y!");
            } else {
                command = st.nextToken();
                yDS = Double.parseDouble(command);
            }
            Logger.logErr("yDeliverySpot= " + yDS);
            world.setDeliverySpot(xDS, yDS);
        } catch (CommandExecException ex) {
            Logger.logException(CommandUtility.class.getName(), ex);
        }
        return world;
    }
    
}
