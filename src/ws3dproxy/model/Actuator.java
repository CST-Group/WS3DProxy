/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ws3dproxy.model;

/**
 *
 * @author eccastro
 */
public class Actuator {
    /**
     * Control parameters:
     */
    
    //Creature attributes under mind control
        //car-like:
	public double wheel; //to be deleted
	public double speed;

    //two-wheeled differential steering robot :
        public double vr;
        public double vl;
        public double w;
}

