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
package ws3dproxy.resourcesgenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import ws3dproxy.CommandExecException;
import ws3dproxy.WS3DProxy;
import ws3dproxy.model.Thing;
import ws3dproxy.model.World;
import ws3dproxy.model.WorldPoint;
import ws3dproxy.util.Constants;
import ws3dproxy.util.Logger;

/**
 *
 * @author eccastro
 */
public class ResourcesGenerator extends Thread {

    private int timeInMinutes;
    private List<Thing> allThings = new ArrayList<Thing>();
    private double width;
    private double height;
    private WorldPoint dsLocation;

    public ResourcesGenerator(int timeframe, double envWidth, double envHeight, double xDS, double yDS) {
        super("ResourcesGenerator");
        if (timeframe == 0) timeInMinutes = Constants.TIMEFRAME;
        else timeInMinutes = timeframe;
        width = envWidth;
        height = envHeight;
        dsLocation = new WorldPoint(xDS,yDS); //delivery spot
    }

    public void run() {
        while (true) {
            try {
                //System.out.println(".......ResourcesGenerator cycle running.........");

                //generate food
                //perishable
                generateFood(0);
                //non-perishable
                generateFood(1);
                ///generate jewels
                for (int jewelType = 0; jewelType < 6; jewelType++) {
                    generateJewel(jewelType);
                }

                //System.out.println("..............ResourcesGenerator SLEEPING........");
                Thread.sleep(timeInMinutes * 60000);

            } catch (Exception ex) {
                ex.printStackTrace();
                System.exit(-1);
            }

        }

    }

    /**
     * Knuth's algorithm to generate random Poisson distributed numbers
     * @param lambda average rate of success in a Poisson distribution
     * @return random number
     */
    public static int getPoissonRandomNumber(double lambda) {
        int k = 1;
        double p = 1.0;
        Random rd = new Random();

        do {
            k += 1;
            p *= rd.nextDouble();
        } while (p > Math.exp((double) -lambda));
        return k - 1;
    }

    private boolean checkAvailability(double cX, double cY) {
        //discard the location of the DeliverySpot:
        if((dsLocation.getX() == cX) && (dsLocation.getY() == cY)){
            return false;
        }

        for (Thing each : allThings) {
            if (each.secAreaContain(cX, cY)) {
                return false;
            }

        }
        return true;
    }

    private void generateFood(int type) {
        try {
            allThings = World.getWorldEntities();
            int number = 1;
            Random rdX = new Random();
            Random rdY = new Random();
            double cX, cY;
            String pointListStr = "" ;

            switch (type) {
                //perishable
                case 0:
                    number = getPoissonRandomNumber(Constants.pFoodLAMBDA);
                    break;
                case 1:
                    //non-perishable
                    number = getPoissonRandomNumber(Constants.npFoodLAMBDA);
                    break;
            }
            for (int i = 0; i < number; i++) {
                do {
                    cX = rdX.nextDouble() * width;
                    cY = rdY.nextDouble() * height;

                } while (!checkAvailability(cX, cY));

                pointListStr = pointListStr+" "+cX+" "+cY;
            }
            World.createFoodInBatch(type, number, pointListStr);
        } catch (CommandExecException ex) {
            Logger.logException(ResourcesGenerator.class.getName(), ex);
        }

    }

    private void generateJewel(int type) {
        try {
            allThings = World.getWorldEntities();
            int number = 1;
            Random rdX = new Random();
            Random rdY = new Random();
            double cX, cY;
            String pointListStr = "" ;

            switch (type) {
                case 0:
                    number = getPoissonRandomNumber(Constants.redLAMBDA);
                    break;
                case 1:
                    number = getPoissonRandomNumber(Constants.greenLAMBDA);
                    break;
                case 2:
                    number = getPoissonRandomNumber(Constants.blueLAMBDA);
                    break;
                case 3:
                    number = getPoissonRandomNumber(Constants.yellowLAMBDA);
                    break;
                case 4:
                    number = getPoissonRandomNumber(Constants.magentaLAMBDA);
                    break;
                case 5:
                    number = getPoissonRandomNumber(Constants.whiteLAMBDA);
                    break;
            }
            for (int i = 0; i < number; i++) {
                do {
                    cX = rdX.nextDouble() * width;
                    cY = rdY.nextDouble() * height;

                } while (!checkAvailability(cX, cY));

                    pointListStr = pointListStr+" "+cX+" "+cY;
                }
                World.createJewelsInBatch(type, number, pointListStr);
        } catch (CommandExecException ex) {
            Logger.logException(ResourcesGenerator.class.getName(), ex);
        }

    }
}


