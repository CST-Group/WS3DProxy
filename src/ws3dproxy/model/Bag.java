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
import java.util.Map;
import ws3dproxy.util.Constants;

/**
 *
 * @author ecalhau
 */
public class Bag {

    private int totalFood = 0;
    private int totalCrystals = 0;
    private int perishableFood = 0;
    private int nonPerishableFood = 0;
    private final HashMap<String, Integer> crystalsPerType = new HashMap();
    private final List<String>supportedCrystalTypes = new ArrayList<String>();
    private HashMap<String, String> infoMap = new HashMap<String, String>();

    public Bag(int numFood, int numCrystals, int numPerishableFood, int numNonPerishableFood, List<Integer> crystals) {

        //Initialization with all possible crystal types RED, GREEN, BLUE, YELLOW, MAGENTA, WHITE:
        crystalsPerType.put(Constants.colorRED, new Integer(0));
        crystalsPerType.put(Constants.colorGREEN, new Integer(0));
        crystalsPerType.put(Constants.colorBLUE, new Integer(0));
        crystalsPerType.put(Constants.colorYELLOW, new Integer(0));
        crystalsPerType.put(Constants.colorMAGENTA, new Integer(0));
        crystalsPerType.put(Constants.colorWHITE, new Integer(0));
        
        supportedCrystalTypes.add(Constants.colorRED);
        supportedCrystalTypes.add(Constants.colorGREEN);
        supportedCrystalTypes.add(Constants.colorBLUE);
        supportedCrystalTypes.add(Constants.colorYELLOW);
        supportedCrystalTypes.add(Constants.colorMAGENTA);
        supportedCrystalTypes.add(Constants.colorWHITE);
        

        update(numFood, numCrystals, numPerishableFood, numNonPerishableFood, crystals);

    }

    public void update(int numFood, int numCrystals, int numPerishableFood, int numNonPerishableFood, List<Integer> crystals) {

        totalFood = numFood;
        totalCrystals = numCrystals;
        perishableFood = numPerishableFood;
        nonPerishableFood = numNonPerishableFood;

        //update values:
        for (int i = 0; i <= crystals.size() - 1; i++) {
            crystalsPerType.put(supportedCrystalTypes.get(i), crystals.get(i));
        }
        
        updateMap();
        System.out.println("Bag is: "+this.printBag());
    }

    public int getTotalNumberFood() {
        return totalFood;
    }

    public int getTotalNumberCrystals() {
        return totalCrystals;
    }

    public int getNumberPFood() {
        return perishableFood;
    }

    public int getNumberNPFood() {
        return nonPerishableFood;
    }

    public int getNumberCrystalPerType(String type) {
        return crystalsPerType.get(type).intValue();
    }
    
    public Map getMap() {
        return this.infoMap;
    }

    private void updateMap() {

        infoMap.put(Constants.TOKEN_BAG_TOTAL_FOOD, ""+totalFood);
        infoMap.put(Constants.TOKEN_BAG_TOTAL_PFOOD, ""+perishableFood);
        infoMap.put(Constants.TOKEN_BAG_TOTAL_NPFOOD, ""+nonPerishableFood);        
        
        infoMap.put(Constants.TOKEN_BAG_TOTAL_CRYSTALS, ""+totalCrystals);
        infoMap.put(Constants.TOKEN_BAG_CRYSTAL_RED, ""+crystalsPerType.get(Constants.colorRED));
        infoMap.put(Constants.TOKEN_BAG_CRYSTAL_GREEN, ""+crystalsPerType.get(Constants.colorGREEN));
        infoMap.put(Constants.TOKEN_BAG_CRYSTAL_BLUE, ""+crystalsPerType.get(Constants.colorBLUE));
        infoMap.put(Constants.TOKEN_BAG_CRYSTAL_YELLOW, ""+crystalsPerType.get(Constants.colorYELLOW));
        infoMap.put(Constants.TOKEN_BAG_CRYSTAL_MAGENTA, ""+crystalsPerType.get(Constants.colorMAGENTA));
        infoMap.put(Constants.TOKEN_BAG_CRYSTAL_WHITE, ""+crystalsPerType.get(Constants.colorWHITE));
        
    }
    
    public String printBag() {
        String content = "Total number of Food= " + totalFood + "\r\n" + "Total number of Crystals= " + totalCrystals + "\r\n"
                + "Number of PerishableFood= " + perishableFood + "\r\n" + "Number of NonPerishableFood= " + nonPerishableFood + "\r\n"
                + "Number of RED crystals= " + crystalsPerType.get(Constants.colorRED) + "\r\n"
                + "Number of GREEN crystals= " + crystalsPerType.get(Constants.colorGREEN) + "\r\n"
                + "Number of BLUE crystals= " + crystalsPerType.get(Constants.colorBLUE) + "\r\n"
                + "Number of YELLOW crystals= " + crystalsPerType.get(Constants.colorYELLOW) + "\r\n"
                + "Number of MAGENTA crystals= " + crystalsPerType.get(Constants.colorMAGENTA) + "\r\n"
                + "Number of WHITE crystals= " + crystalsPerType.get(Constants.colorWHITE) + "\r\n";

        return content;
    }
}
