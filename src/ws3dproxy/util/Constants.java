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
package ws3dproxy.util;

import java.awt.Color;

/**
 *
 * @author eccastro
 */
public class Constants {

    public static double GAP = 40; //area including waypoints
    public static double SEC = 30;//area narrower than GAP. The segments of the path
    // must not cross it. There must be no waypoints in this area.
    

    //offset from path. Things this far from path are retrieved
    public static double OFFSET = 50;

    
    public static double MINDISTANCE; //depend on size of the creature

    //this value indicates that the edge must not be consider as a "good" path
    // in Dijkstra algorithm. It is interpreted as very long or "heavy" edge.
    public static double MAXEDGELENGTH = Double.MAX_VALUE;

    public static final double CREATURE_SIZE = 40.0;
    public static final double FOOD_SIZE = 12.0;
    public static final double CRYSTAL_SIZE = 12.0;

    //Spurious value for angular velocity:
    public static final double WNULL= 1001;
    
    public static final String HOST = "localhost";
    public static final int PORT = 4011;

    public static final int startGamePollingTIMEFRAME = 3; //default in seconds

    public static final double ODOMETRY_ERROR = 5;

    public static final double surroundingAreaRadius = 100;

    //Constants of PI
    public static final double M_PI = Math.PI;
    public static final double M_PI_2 = M_PI / 2.0;
    public static final double M_PI_4 = M_PI / 4.0;
    
    public static final double PITCH_INEXISTENT = -11111;
            
    //Creature's "directRay" range
    public static final double RAY_RANGE = 1000;
    
    //creature index not set yet
    public static final String INDEX_NULL = "-1";
    
    //Maximum fuel:
    public static final double CREATURE_MAX_FUEL = 1000.0;

    public static final double DEFAULT_CREATURE_SPEED = 4; //SPEED
    public static final double REVERSE_SPEED = -100;//keep it negative only to indicate it is a reverse motion
    
    //Unconstious/conscious matters:
    public static final int NOT_YET_CONSCIOUS = 0;
    public static final int CAN_LEAVE_PLAYINGFIELD = 1;
    public static final int MUST_STAY_ON_PLAYINGFIELD = 2;

    /**
     * Tokens to parser Attribute
     */
        
    public static final String TOKEN_NAME_ID = "NAMEID=";
    public static final String TOKEN_INDEX = "INDEX=";
    public static final String TOKEN_COLOR = "COLOR=";
    public static final String TOKEN_SPEED = "SPEED=";
    public static final String TOKEN_WHEEL = "WHEEL=";//to be deleted!!!
    public static final String TOKEN_WHEEL_R = "WHEEL_R=";
    public static final String TOKEN_WHEEL_L = "WHEEL_L=";
    public static final String TOKEN_CREATURE_PITCH = "CREATURE_PITCH=";
    public static final String TOKEN_THING_PITCH = "THING_PITCH=";
    public static final String TOKEN_CREATURE_ENERGY = "CREATURE_ENERGY=";
    public static final String TOKEN_SEROTONIN = "SEROTONIN=";
    public static final String TOKEN_ENDORPHINE = "ENDORPHINE=";
    public static final String TOKEN_ACTION = "ACTION";
    public static final String TOKEN_SCORE = "SCORE=";
    public static final String TOKEN_X1 = "X1=";
    public static final String TOKEN_X2 = "X2=";
    public static final String TOKEN_Y1 = "Y1=";
    public static final String TOKEN_Y2 = "Y2=";
    public static final String TOKEN_CENTER_OF_MASS_X = "COM_X=";
    public static final String TOKEN_CENTER_OF_MASS_Y = "COM_Y=";
    public static final String TOKEN_HAS_LEAFLET = "HASLEAFLET=";
    public static final String TOKEN_HAS_COLLIDED = "HASCOLLIDED=";
    public static final String TOKEN_MY_LEAFLETS = "MYLEFLETS=";
    public static final String TOKEN_CATEGORY = "CATEGORY=";
    
    
    public static final String TOKEN_SITUATION = "situation=";
    public static final String TOKEN_PAYMENT = "payment=";
    public static final String TOKEN_LEAFLET_ID = "LeafletID:";
     public static final String TOKEN_ITENS = "Itens:";
    public static final String TOKEN_LEAFLET_ID_INVALID = "-1";
    
    public static final String TOKEN_OCCLUDED = "OCCLUDED=";
    public static final String TOKEN_THING_ENERGY = "THING_ENERGY=";
    public static final String TOKEN_HARDNESS = "HARDNESS=";
    public static final String TOKEN_SHININESS = "SHININESS=";
    public static final String TOKEN_CREATURE_X = "CREATURE_X=";//center of mass
    public static final String TOKEN_CREATURE_Y = "CREATURE_Y=";// ""
    public static final String TOKEN_CREATURE_X1 = "CREATURE_X1=";
    public static final String TOKEN_CREATURE_X2 = "CREATURE_X2=";
    public static final String TOKEN_CREATURE_Y1 = "CREATURE_Y1=";
    public static final String TOKEN_CREATURE_Y2 = "CREATURE_Y2=";
    public static final String TOKEN_BAG_TOTAL_FOOD = "BAG_TOTAL_FOOD=";
    public static final String TOKEN_BAG_TOTAL_CRYSTALS = "BAG_TOTAL_CRYSTALS=";
    public static final String TOKEN_BAG_TOTAL_PFOOD = "BAG_TOTAL_PFOOD=";
    public static final String TOKEN_BAG_TOTAL_NPFOOD = "BAG_TOTAL_NPFOOD=";
    public static final String TOKEN_BAG_CRYSTAL_RED = "TOKEN_BAG_CRYSTAL_RED=";
    public static final String TOKEN_BAG_CRYSTAL_GREEN = "TOKEN_BAG_CRYSTAL_GREEN=";
    public static final String TOKEN_BAG_CRYSTAL_BLUE = "TOKEN_BAG_CRYSTAL_BLUE=";
    public static final String TOKEN_BAG_CRYSTAL_YELLOW = "TOKEN_BAG_CRYSTAL_YELLOW=";
    public static final String TOKEN_BAG_CRYSTAL_MAGENTA = "TOKEN_BAG_CRYSTAL_MAGENTA=";
    public static final String TOKEN_BAG_CRYSTAL_WHITE = "TOKEN_BAG_CRYSTAL_WHITE=";
    public static final String TOKEN_PERCEPT_ID = "PERCEPT_ID=";
    public static final String TOKEN_PERCEPT_TIMESTAMP = "PERCEPT_TIMESTAMP=";
    public static final String TOKEN_THING_DATA = "THING_DATA=";
    public static final String TOKEN_SELF_DATA = "SELF_DATA=";
    
    
    
    public static final int HARDNESS_DEFAULT = 1; //solid
    public static final int ENERGY_DEFAULT = 0; //not a food
    public static final int OCCLUDED_DEFAULT = 0; //it is visible;not occluded
     public static final int SHININESS_DEFAULT = 0; //only jewel shines
    
    //Define the location (X,Y) status after certain actions upon a Thing:
    //After a contact action is completed, the Thing location is updated:    
    public static final double AFTER_EAT_COORDS = -25;
    public static final double AFTER_PUT_IN_BAG_COORDS = -26;
    public static final double AFTER_DELIVERY_COORDS = -27;
    public static final double AFTER_HIDE_COORDS = -28;
    
    /**
     * BN propositions:
     */
    public static final String BN_STOPPED = "stopped";
//    public static final String BN_NONE_STATE = "in-none-state";
//    public static final String BN_CLOSE_TO_THING = "close-to-thing";
    public static final String BN_CLOSE_TO_THING_NOT_WALL = "close-to-thing-not-wall";
    public static final String BN_CLOSE_TO_HIDDEN_THING = "close-to-hidden-thing";
    public static final String BN_NO_PLAN = "no-plan";
    public static final String BN_HAS_PLAN = "has-plan";
    public static final String BN_FULL_ENERGY = "full-energy";
    public static final String BN_LOW_ENERGY = "low-energy";
    public static final String BN_HAS_TARGET = "has-target";
    public static final String BN_TOUCHING_JEWEL = "close-to-jewel";
    public static final String BN_TOUCHING_JEWEL_IN_LEAFLET = "close-to-jewel-in-leaflet";
    public static final String BN_TOUCHING_FOOD = "close-to-food";
    public static final String BN_NO_LEAFLET = "no-leaflet";
    public static final String BN_HAS_LEAFLET = "has-leaflet";
    public static final String BN_LEAFLET_COMPLETED = "leaflet-completed";
    public static final String BN_NEAR_DELIVERY_SPOT = "near-delivery-spot";
//    public static final String BN_GET_TARGET = "get-target";
    public static final String BN_CRYSTAL_IN_BAG = "crystal-in-bag";
    public static final String BN_FOOD_IN_BAG = "food-in-bag";
//    public static final String BN_FOOD_NOT_ROTTEN = "food-not-rotten";
//    public static final String BN_ONLY_MOVE = "only-move";



    public static final String colorRED = "Red";
    public static final String colorGREEN = "Green";
    public static final String colorBLUE = "Blue";
    public static final String colorYELLOW = "Yellow";
    public static final String colorMAGENTA = "Magenta";
    public static final String colorWHITE = "White";
    public static final String colorDARKGRAY_SPOILED = "DarkGray_Spoiled";
    public static final String colorORANGE = "Orange";


    public static final int categoryCREATURE = 0;
    public static final int categoryBRICK = 1;
    public static final int categoryFOOD = 2; //currently not actually in use
    public static final int categoryPFOOD = 21;  
    public static final int categoryNPFOOD = 22; 
    public static final int categoryJEWEL = 3;
    public static final int categoryDeliverySPOT = 4;

    //Actions in the Episode:
    public static final int action_SEE = 0;
    public static final int action_HIDE = 1;
    public static final int action_UNHIDE = 2;
    public static final int action_PutIntoBAG = 3;
    public static final int action_EAT = 4;
    public static final int action_MOVE = 5; //creature moves itself (currently not in use)
    //public static final int action_LIFT = 6;
    public static final int action_CARRY = 7; //on the head
    public static final int action_DROP = 8; //stop carrying: put in a place on the ground
    public static final int action_GONE = 9; //a Thing has missed
    public static final int action_CHANGED = 10; //a Thing has changed and older episodes must be updated
    public static final int action_ENLARGED = 11;
    
    public static final String ACTION_NAME_SEE = "Action_SEE";
    public static final String ACTION_NAME_HIDE = "Action_HIDE";
    public static final String ACTION_NAME_UNHIDE = "Action_UNHIDE";
    public static final String ACTION_NAME_PUTINTOBAG = "Action_PUT_IN_BAG";
    public static final String ACTION_NAME_EAT = "Action_EAT";
    public static final String ACTION_NAME_MOVE = "Action_MOVE";
    public static final String ACTION_NAME_CARRY = "Action_CARRY";
    public static final String ACTION_NAME_DROP = "Action_DROP";
    public static final String ACTION_NAME_DELIVER = "Action_DELIVER";
    //public static final int action_GONE = 9; //a Thing has missed
    //public static final int action_CHANGED = 10; //a Thing has changed and older episodes must be updated
    //public static final int action_ENLARGED = 11;


    public final static String CUE_PATH_TYPE = "cue-path-type";

    
    /**
     * Resources Generator package constants
     */
    public static final int TIMEFRAME = 3; //default in minutes
    ////////Poisson distribution
    //the average rate of generation of each kind of crystal:
    public static final double redLAMBDA = 1;
    public static final double greenLAMBDA = 0.4;
    public static final double blueLAMBDA = 0.5;
    public static final double yellowLAMBDA = 0.7;
    public static final double magentaLAMBDA = 0.3;
    public static final double whiteLAMBDA = 0.2;
    public static final double pFoodLAMBDA = 1;
    public static final double npFoodLAMBDA = 0.7;
    public static double SECURITY = 30; //empiric
    
    public static Color translateIntoColor(String colorName){

        if (colorName.equals(Constants.colorRED)) return Color.RED;
        else if (colorName.equals(Constants.colorGREEN)) return Color.GREEN;
        else if (colorName.equals(Constants.colorBLUE)) return Color.BLUE;
        else if (colorName.equals(Constants.colorYELLOW)) return Color.YELLOW;
        else if (colorName.equals(Constants.colorMAGENTA)) return Color.MAGENTA;
        else if (colorName.equals(Constants.colorWHITE)) return Color.WHITE;
        else if (colorName.equals(Constants.colorDARKGRAY_SPOILED)) return Color.DARK_GRAY;
        else if (colorName.equals(Constants.colorORANGE)) return Color.ORANGE;
        else return Color.MAGENTA; //default
    }
    
    public static String getColorName(Color color) {
        String st = new String();
        if(color.equals(Color.RED) )st = Constants.colorRED;
        else if(color.equals(Color.GREEN) )st = Constants.colorGREEN;
        else if(color.equals(Color.BLUE) )st = Constants.colorBLUE;
        else if(color.equals(Color.YELLOW) )st = Constants.colorYELLOW;
        else if(color.equals(Color.MAGENTA) )st = Constants.colorMAGENTA;
        else if(color.equals(Color.WHITE) )st = Constants.colorWHITE;
        else if(color.equals(Color.DARK_GRAY) )st = Constants.colorDARKGRAY_SPOILED;
        else if(color.equals(Color.ORANGE) )st = Constants.colorORANGE;

        return st;
    }
}

