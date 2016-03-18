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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The Sensory Buffer. Where the raw percepts are stored within the creature's
 * mind. (Old WMVisualBuffer)
 *
 * The raw percepts are the structures received from the environment by the
 * sensors.
 *
 * @author eccastro
 */
public final class SensoryBuffer extends Observable {

    final public SensoryBuffer semaphore = this;

    private static SensoryBuffer instance = null;
    
    private int retentionTime = -1;//default: does not expire; in secs
    private Timer timer;
    private boolean stillValid = true;

    private static final List<Thing> thingsInVision = Collections.synchronizedList(new ArrayList<Thing>());
    private static final List<Thing> thingsInFrustrum = Collections.synchronizedList(new ArrayList<Thing>());
    private final Map<String, Thing> thingsInVisionMap = Collections.synchronizedMap(new HashMap<String, Thing>());
    private final Map<String, Thing> thingsInFrustrumMap = Collections.synchronizedMap(new HashMap<String, Thing>());

    private SensoryBuffer() {

        retentionTimeCycle();
    }

    public static SensoryBuffer getInstance() {
        if (instance == null) {
            instance = new SensoryBuffer();
        }
        return instance;
    }

    void retentionTimeCycle() {
        if (retentionTime != -1) {
            timer = new Timer();
            timer.schedule(new RetentionMemoryTask(), 0, retentionTime * 1000);
        }
    }

    public synchronized void capturedInVision(Thing th) {
        thingsInVision.add(th);
        thingsInVisionMap.put(th.getName(), th);
        changed();
    }

    public synchronized void capturedInFrustrum(Thing th) {
        thingsInFrustrum.add(th);
        thingsInFrustrumMap.put(th.getName(), th);
    }

    public synchronized List<Thing> getThingsInVision() {
        return thingsInVision;
    }

    public synchronized List<Thing> getThingsInFrustrum() {
        return thingsInFrustrum;
    }

    public synchronized Map<String, Thing> getThingsInVisionMap() {
        return thingsInVisionMap;
    }

    public synchronized Map<String, Thing> getThingsInFrustrumMap() {
        return thingsInFrustrumMap;
    }

    public synchronized void resetVision() {

        this.thingsInVision.clear();
        this.thingsInVisionMap.clear();
    }
    
    public synchronized void resetFrustrum() {

        this.thingsInFrustrum.clear();
        this.thingsInFrustrumMap.clear();
    }

    public void setTime(int time) {
        retentionTime = time;
    }

    public boolean isValid() {
        return this.stillValid;
    }

    public int getTime() {
        return retentionTime;
    }

    public void addAnObserver(Observer ob) {
        this.addObserver(ob);
    }

    public void changed() {
        setChanged();
        notifyObservers();
    }

    class RetentionMemoryTask extends TimerTask {

        RetentionMemoryTask() {
            stillValid = true;
        }

        public void run() {
            stillValid = false;
            resetVision();
            resetFrustrum();
            
        }
    }
}
