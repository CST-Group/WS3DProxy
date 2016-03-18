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

/**
 * Simple logger.
 *
 * @author eccastro
 */
public class Logger {
    
    public static int level=0;

    public static void logException(String className, Exception exception) {
        if (level >= 0) System.err.println(className + " " + exception);
    }

    public static void logErr(String info) {
        if (level >= 1 ) System.err.println(info);
    }

    public static void log(String info) {
        if (level >= 2) System.out.println(info);
    }

}
