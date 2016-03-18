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
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;

/**
 *
 * @author eccastro
 */
public class WorldMap {

    final public WorldMap semaphore = this;
    
    public List<Creature> creatures = new ArrayList<Creature>();
    //WS populates the "worldmap" with all things that have been seen. 
    private HashMap<String, Thing> thingsListMap = new HashMap();
    private Vector<Thing> thingsList = new Vector<Thing>();

    public Creature cr;
    //Matriz of known and unknown regions of the environment:
    public boolean knownPlaces[][];

//	public List<WorldPoint> pointHistory = new ArrayList<WorldPoint>(); // pp

    //Scene dimension (world view in mind)
    public static double environmentWidth = 500;
    public static double environmentHeigth = 500;
    private NewThingsNotifier notifier = new NewThingsNotifier();

    public WorldMap() {
//		int sensorSize = SensorUnit.SIDE * VisualSystem.NSENSORS;
//
//		this.knownPlaces = new boolean[(int) (4 * WorldMap.environmentWidth
//				/ (sensorSize) + 1)][(int) (4 * WorldMap.environmentHeigth
//				/ (sensorSize) + 1)];

        this.knownPlaces = new boolean[(int) (4 * WorldMap.environmentWidth)][(int) (4 * WorldMap.environmentHeigth)];

        this.clearKnownPlaceMap();

    }

    //not in use
    public boolean addCreature(Creature creature) {
        cr = creature;
        return creatures.add(creature);
    }

    public void addThing(Thing o) {
        // System.err.println("Worldmap: Adicionou algo!");
        //this.thingsList.add(o);
        this.thingsListMap.put(o.getAttributes().getName(), o);
        notifier.changed();
        notifier.notifyObservers();
    }

    /**
     * Update the things on the worldmap.
     * Currently not removing things, only ading and updating the still existent based
     * on the robot visual system.
     * NOTE: Consider use the EM to remove the deleted (or put in knapsack) things!!!!
     *
     *
     * @param lo
     */
    public synchronized void updateThings(List<Thing> lo) {

        for (Thing o : lo) {
            if (!thingsListMap.containsKey(o.getAttributes().getName())) {
                this.thingsList.add(o);
            }
                /**replace the thing if already in map.
                   this will update its position and other attributes
                */
                this.thingsListMap.put(o.getAttributes().getName(), o);

            //TODO:  check if process is correct when a thing was removed and update which already exists!!!!

        }

    }

    @SuppressWarnings("unchecked")
    public synchronized Vector<Thing> getThingList() {
//        Vector<Thing> cloneList = (Vector<Thing>) this.thingsList.clone();
//        return cloneList;
        return thingsList;

    }

    @SuppressWarnings("unchecked")
    public double[] NearestUnknownAreaPolar(boolean constraintToVisualScan,
            double minDist, MySensors s) {

        if (knownPlaces == null) {
            return null;
        }

        double[] nearest = null;

        for (int i = 0; i < knownPlaces.length; i++) {
            double CentroX = (i + 0.5) * environmentWidth / knownPlaces.length;
            while (Math.abs(s.comX - CentroX) > Math.abs(s.comX - (CentroX - environmentWidth))) {
                CentroX -= environmentWidth;
            }
            while (Math.abs(s.comX - CentroX) > Math.abs(s.comX - (CentroX + environmentWidth))) {
                CentroX += environmentWidth;
            }

            for (int j = 0; j < knownPlaces[i].length; j++) {
                double CentroY = (j + 0.5) * environmentHeigth / knownPlaces[i].length;
                while (Math.abs(s.comY - CentroY) > Math.abs(s.comY - (CentroY - environmentHeigth))) {
                    CentroY -= environmentHeigth;
                }
                while (Math.abs(s.comY - CentroY) > Math.abs(s.comY - (CentroY + environmentHeigth))) {
                    CentroY += environmentHeigth;
                }

                double[] polarCoords = cr.xyToPolar(CentroX, CentroY);

                boolean valid = !knownPlaces[i][j] && (polarCoords[0] > minDist);
//				if (constraintToVisualScan) {
//					valid = valid
//							&& (polarCoords[0] >= Constants.MIN_VS_DISTANCE)
//							&& (polarCoords[0] <= Constants.MAX_VS_DISTANCE)
//							&& (polarCoords[1] >= Constants.MIN_VS_ANGLE)
//							&& (polarCoords[1] <= Constants.MAX_VS_ANGLE);
//				}

                if (valid) {
                    if (nearest == null ? true : polarCoords[0] < nearest[0]) {
                        nearest = polarCoords;
                    }
                }
            }
        }

        return nearest;
    }

    public WorldPoint randomUnknownArea() {
        List<WorldPoint> unknownAreas = new ArrayList<WorldPoint>();

        // Faz a lista dos pontos desconhecidos
        for (int i = 0; i < knownPlaces.length; i++) {
            double CentroX = (i + 0.5) * environmentWidth / knownPlaces.length;

            for (int j = 0; j < knownPlaces[i].length; j++) {
                double CentroY = (j + 0.5) * environmentHeigth / knownPlaces[i].length;

                if (!knownPlaces[i][j]) {
                    unknownAreas.add(new WorldPoint(CentroX, CentroY));
                }
            }
        }

        // Pega um dos pontos aleatoriamente.
        if (unknownAreas.size() == 0) {
            return null;
        } else {
            return (WorldPoint) unknownAreas.get((int) (Math.random() * unknownAreas.size()));
        }

    }

    public void clearKnownPlaceMap() {
        int maxi = knownPlaces.length;
        int maxj = knownPlaces[0].length;
        for (int i = 0; i < maxi; i++) {
            for (int j = 0; j < maxj; j++) {
                knownPlaces[i][j] = false;
            }
        }
    }

//    public void clearThings() {
//        thingsList.clear();
//    }

    public void addNotifierObserver(Observer observer) {

        notifier.addAnObserver(observer);

    }

    class NewThingsNotifier extends Observable {

        public void addAnObserver(Observer ob) {
            this.addObserver(ob);
        }

        public void changed() {
            setChanged();
        }
    }
}
