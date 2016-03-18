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
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import ws3dproxy.util.Logger;

/**
 * Visual working memory space.
 * Where the creature "stores" the perceived entities of the environment.
 *
 * @author eccastro
 */
public class WMVisualBuffer{

    final public WMVisualBuffer semaphore = this;

    private int retentionTime = -1;//default: does not expire; in secs
    private Timer timer;
    private boolean stillValid = true;

    private List<Thing>newThing = Collections.synchronizedList(new ArrayList<Thing>());
    private List<Thing> modifiedThing = Collections.synchronizedList(new ArrayList<Thing>());
    private List<Thing> thingsCurrentlyInVisionList = Collections.synchronizedList(new ArrayList<Thing>());
    private List<Thing> goneThingsList = Collections.synchronizedList(new ArrayList<Thing>());
    private List<Thing> rememberedThingsInWMBuffer = Collections.synchronizedList(new ArrayList<Thing>());
    private HashMap<String, Boolean> validRememberedThingsInWMBuffer = new HashMap<String, Boolean>();

    public WMVisualBuffer() {

        retentionTimeCycle();
    }

    void retentionTimeCycle() {
        if (retentionTime != -1) {
            timer = new Timer();
            timer.schedule(new RetentionMemoryTask(), 0, retentionTime * 1000);
        }
    }

    public synchronized List<Thing> getModifiedThing(){
        return modifiedThing;
    }

    public synchronized void addModifiedThing(Thing o){
        modifiedThing.add(o);
    }

    public synchronized void clearModifiedThing() {
        modifiedThing.clear();
    }

    public synchronized List<Thing> getNewThing() {
        return this.newThing;
    }

    public synchronized void addNewThing(Thing o) {
        this.newThing.add(o);
    }

    public synchronized void resetNewThing() {
        this.newThing.clear();
    }


    public HashMap<String, Thing> getThingListMap() {
        return World.getInstance().getThingsMap();
    }

    public synchronized void reset() {

        this.thingsCurrentlyInVisionList.clear();
        this.rememberedThingsInWMBuffer.clear();
    }

    public synchronized void setThingsInVision(List<Thing> inVision) {

            thingsCurrentlyInVisionList.clear();
            for (Thing th : inVision) {
                thingsCurrentlyInVisionList.add(th);
            }
    }
    /**
     * All Things that are considered "gone" are stored again.
     * @param gone 
     */
    public synchronized void setGoneThings(List<Thing> gone) {

        goneThingsList.clear();
        goneThingsList.addAll(gone);
    }
    /**
     * Here the remembered Things are persisted for a while (until an external timer expires and
     * the corresponding entry is deleted.
     * Note: it does not add new entries until a new plan (and then a new cue is posted) is generated.
     * Do not "remember" things in vision and not already in WM.
     * @param remembered
     */
    public synchronized void setRememberedThingsNow(List<Thing> remembered) {

        synchronized (rememberedThingsInWMBuffer) {
            for (Thing th : remembered) {
                validRememberedThingsInWMBuffer.put(th.getName(), true);//valid
                rememberedThingsInWMBuffer.add(th);
            }

        }
    }

    public synchronized void updateMissingThings(List<Thing> forgotten) {

        System.out.println(".........forgotten: "+forgotten);
        synchronized (rememberedThingsInWMBuffer) {
            for (Iterator iter = rememberedThingsInWMBuffer.iterator(); iter.hasNext();) {

                Thing rth = (Thing) iter.next();
                for (Thing fth : forgotten) {
                    if (rth.getName().equals(fth.getName())) {
                        iter.remove();
                    }
                }
            }
        }

    }

    

//    public synchronized void updateThingsOld(List<Thing> inVision) {
//
//        logger.info("******************************* updateThings");
//
//
//        setThingsInVision(inVision);
//      //  this.newThing.clear();
//        this.modifiedThing.clear();
//        boolean foundNew = false; //if at least one Thing is new, set the flag to
//        //create an episode with all the objects in vision
//        for (Thing o : inVision) {
//            if (!getThingListMap().containsKey(o.getName())) {
//                logger.info("--------- First time I see this Thing: " + o.getName());
//                getThingListAll().add(o);
//                getThingListMap().put(o.getName(), o);
//                foundNew = true;
//
//            } else { //update the object if position changed (or is unhide)
//
//                Thing old = getThingListMap().get(o.getName());
//                logger.info("old.getCenterPosition()= " + old.getCenterPosition() + " o.getCenterPosition()= " + o.getCenterPosition() + " old.hidden= " + old.hidden + " o.hidden= " + o.hidden);
//                if (!old.getCenterPosition().equals(o.getCenterPosition())) {
//                    getThingListAll().remove(getThingListMap().get(o.getName()));
//                    getThingListAll().add(o);
//                    getThingListMap().remove(o.getName());
//
//                    /**Replace the thing since it has changed.
//                    This will update its position and other attributes
//                     */
//                    getThingListMap().put(o.getName(), o);
//                    logger.info("--------- Thing must be updated in VisualWS: " + o.getName());
//                    modifiedThing.add(o);
//                } else {
//                    logger.info("--------- Thing does not need to be updated in VisualWS: " + o.getName());
//                }
//
//            }
//
//        }
//        //if at least one Thing is new, create only one episode of the scene
//        if (foundNew)
//        for (Thing o : inVision) {
//            this.newThing.add(o);
//        }
//
//    }



    /**
     *
     * @return thingList Things currently in vision and recalled from ME
     * (not ALL Things known)
     */
    public synchronized Vector<Thing> getThingList() {
        Vector<Thing> thingList = new Vector<Thing>();
        HashMap<String, Thing> thingMap = new HashMap<String, Thing>();

        for(Thing vth: this.thingsCurrentlyInVisionList){
            thingList.add(vth);
            thingMap.put(vth.getName(), vth);
        }
        
        for(Thing rth: this.rememberedThingsInWMBuffer){
            if(!thingMap.containsKey(rth.getName())){
                thingList.add(rth);
            }
        }

        return thingList;

    }

    /**
     * This method is used by the Planner to evaluate if a path crosses a Thing.
     * @return The Things that cross the path
     */
    public synchronized Vector<Thing> getValidThingList() {
        Vector<Thing> thingList = new Vector<Thing>();
        HashMap<String, Thing> thingMap = new HashMap<String, Thing>();

        for (Thing vth : this.thingsCurrentlyInVisionList) {
            thingList.add(vth);
            thingMap.put(vth.getName(), vth);
        }

        for (Thing rth : this.rememberedThingsInWMBuffer) {
            if (!thingMap.containsKey(rth.getName())) {
                if (this.validRememberedThingsInWMBuffer.get(rth.getName())) {
                    thingList.add(rth);
                }
            }
        }

        return thingList;

    }
    /**
     *
     * @return thingsList All known Things (vision + in ME).
     */
//    public synchronized Vector<Thing> getThingListAll() {// old getThingList
//        return thingsList;
//
//    }
    public List<Thing> getThingListAll() {
        return World.getInstance().getEveryThing();

    }

    public synchronized void expiredInRememberedThings(String nameID) {
        validRememberedThingsInWMBuffer.put(nameID, false);
    }

    public synchronized void removeFromRememberedThings(String nameID) {
        synchronized (rememberedThingsInWMBuffer) {
            for (Iterator<Thing> iter = rememberedThingsInWMBuffer.iterator(); iter.hasNext();) {
                Thing toDelete = iter.next();
                if (toDelete.getName().equals(nameID)) {
                    System.out.println("Thing to remove from rememberedThings: "+ toDelete.getName());
                    iter.remove();

                    break;
                }
            }
        }
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
//    /**
//     * Only for new episodes of SEE action.
//     * @param th
//     */
//    private void callEncoderToAddEpisode(List<Thing> lo) {
//
//        logger.info("------------------- callEncoderToAddEpisode: a new Thing has been seen.");
//
//        int emotion = -1;//TODO emotion????
//
//        wem.addEpisode(encoder.addEpisode(wem.getName(), Constants.action_SEE, lo, emotion));
//    }
//    private void callEncoderToAddEpisodeOfModifiedThing(Thing th) {
//
//        logger.info("------------------- callEncoderToAddEpisodeOfModifiedThing: Thing has changed " + th.getName());
//        List<Thing> objs = new ArrayList<Thing>();
//        objs.add(th);
//        int emotion = -1;//TODO emotion????
//
//        wem.addEpisode(encoder.addEpisode(wem.getName(),Constants.action_CHANGED, objs, emotion));
//    }


//    public void flushLists() {
//        List<List<Thing>> lists = new ArrayList();
//        lists.add(newThing);
//        lists.add(modifiedThing);
//        setChanged();
//        notifyObservers(lists);
//    }

    class RetentionMemoryTask extends TimerTask {

        RetentionMemoryTask() {
            stillValid = true;
        }

        public void run() {
            stillValid = false;
            reset();
        }
    }
}
