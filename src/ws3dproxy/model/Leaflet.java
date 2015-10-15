/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ws3dproxy.model;

import java.util.HashMap;
import java.util.Iterator;

/**
 *
 * @author ecalhau
 */
public class Leaflet {
private Long ID;

    //private int active = 1; //true: not delivered yet
    private int payment = 0; //number of points is gained by a creature when it is delivered

    //Type (i.e. color name), (Total number, Collected number)
    private HashMap<String, Integer[]> itemsMap = new HashMap<String, Integer[]>();

    //public Leaflet(Long ID, int activity, HashMap items, int payment) {
    public Leaflet(Long ID, HashMap items, int payment) {
        this.ID = ID;
        //this.active = activity;
        setItems(items);
        this.payment = payment;
    }


    public HashMap<String, Integer[]> getItems() {

        return itemsMap;
    }

    public void setItems(HashMap<String, Integer[]> items){

        for (Iterator<String> iter = items.keySet().iterator(); iter.hasNext();) {
            String str = iter.next(); //jewel color
            Integer[]values = (Integer[]) items.get(str); //(total number) (collected)

            itemsMap.put(str, values);

        }
    }

    public int getPayment(){
        return payment;
    }
//    public synchronized int getActivity(){
//        return active;
//    }
//    public synchronized void setActivity(int ac){
//        active = ac;
//    }

    public int getTotalNumberOfType(String type){

        if(itemsMap.containsKey(type)){
            Integer[]values = null;
            values = itemsMap.get(type);
            return values[0];
        }else return -1;
    }
    public int getCollectedNumberOfType(String type){

        if(itemsMap.containsKey(type)){
            Integer[]values = null;
            values = itemsMap.get(type);
            return values[1];
        }else return -1;
    }

    public int getMissingNumberOfType(String type){

        if(itemsMap.containsKey(type)){
            Integer[]values = null;
            values = itemsMap.get(type);
            return (values[0] - values[1]);
        }else return -1;
    }

    public HashMap getWhatToCollect(){

        HashMap<String, Integer> itemsToSearch = new HashMap<String, Integer>();
        for (Iterator iter = itemsMap.keySet().iterator(); iter.hasNext();) {

            String type = (String)iter.next();
            itemsToSearch.put(type, getMissingNumberOfType(type));
        }
        return itemsToSearch;
    }

    public boolean ifInLeaflet(String type) {

        if (itemsMap.containsKey(type)) return true;
        else return false;
    }

    public Long getID() {
        return ID;
    }


    /**
     * Format: (space)Color(space)number(space)
     * Begin and end with blank space
     * @return
     */
    @Override
    public String toString(){
        String ret = "LeafletID: "+this.ID+" ";
        for (Iterator<String> iter = itemsMap.keySet().iterator(); iter.hasNext();) {
            String str = iter.next(); //jewel color
            ret = ret + "Type: "+ str + " ";
            //(total number) (collected)
            ret = ret + itemsMap.get(str)[0] + " ";
            ret = ret + itemsMap.get(str)[1] + " ";
        }
        ret = ret+" payment= "+payment+" ";
        return ret;

    }

//    public class ItemAttributes {
//
//        int totalNumber;
//        int collected;
//
//        public ItemAttributes(int tn) {
//            totalNumber = tn;
//            collected = 0;
//        }
//        public void setCollected(int c){
//            collected = c;
//        }
//        public void incCollected(){
//            collected++;
//        }
//        public void decCollected(){
//            collected--;
//        }
//
//    }
}
