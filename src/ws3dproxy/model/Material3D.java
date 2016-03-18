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

/**
 *
 * @author eccastro
 */
import ws3dproxy.util.Constants;
import java.awt.Color;

public class Material3D {

    private double hardness = 1.0;
    private double energy = 0.0;
    private double shininess = 0; //not currently in use

    private Color color;

    public Material3D(Color color) {
        this.color = color;
    }

    public Material3D(double hardness, double energy, Color color) {
        this.hardness = hardness;
        this.energy = energy;
        this.color = color;
    }
    
    public Material3D(double hardness, double energy, Color color, double shininess) {
        this.hardness = hardness;
        this.energy = energy;
        this.color = color;
        this.shininess = shininess;
    }

    public void setShininess(double t) {
        this.shininess = t;
    }

    public void makeItNotHard() {
        this.hardness = 0.0;
    }

    public double getEnergy() {
        return energy;
    }

    public double getHardness() {
        return hardness;
    }

    public Color getColor() {
        return this.color;
    }

    public String getColorName() {
        return Constants.getColorName(color);
    }

    public double getShininess() {
        return shininess;
    }

    public void setColor(Color c) {
        this.color = c;
    }

    public void setEnergy(double e) {
        this.energy = e;
    }

    public void setHardness(double hardness) {
        this.hardness = hardness;
    }

    public void setColor(String colorName) {

        this.color = Constants.translateIntoColor(colorName);

    }
    
    public String toString(){
        return ""+this.getColorName()+" "+this.getEnergy()+" "+this.getHardness()+" "+this.getShininess();
    }
}

