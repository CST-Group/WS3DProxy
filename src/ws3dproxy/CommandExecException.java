/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ws3dproxy;

/**
 * This exception is thrown whenever the server responds with an "error code"
 * message. It happens in case of a missing or invalid parameter.
 * @author eccastro
 */
public class CommandExecException extends Exception {
    public CommandExecException(String message){
     super(message);
  }
    
}
