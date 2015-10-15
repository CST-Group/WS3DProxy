/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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
