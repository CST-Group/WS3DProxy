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

import java.awt.Polygon;
import java.awt.geom.Line2D;

/**
 *
 * @author eccastro
 */
public class MySensors {

    //creature position - center of mass (COM)

    public double comX;
    public double comY;
    public double comZ;

    //creature vertices
    public double x1, y1, x2, y2;

    //Creature direction:
    public double pitch;
    /**
     * Ray in the direction of the pitch.
     */
    public Line2D.Double directRay;

    //energy of creature (self)
    public double fuel;
    public double serotonin;
    public double endorphine;
    public double score;

    //visual system: current "field-of-view"
    Polygon FOV;
}
