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
package ws3dproxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import ws3dproxy.util.Logger;

/**
 * Utility class for this client server communication model through a network
 * socket.
 *
 * @author ecalhau
 */
public class SocketUtility {

    /**
     * The network socket through which the server/client interact with each
     * other: client sends commands to the server which in turn process them and
     * sends back a failure or success report.
     */
    protected static Socket sock;
    /**
     * Writer of the commands (text messages) sent to the server.
     */
    protected static PrintWriter out;
    /**
     * Reader of the text report received from the server after the command
     * execution.
     */
    protected static BufferedReader in;

    /**
     * Creation of the network socket through which the server/client interact
     * with each other: client sends commands to the server which in turn
     * process them and sends back a failure or success report.
     *
     * @param host IP address or name of remote host (or localhost)
     * @param port port number to connect to server
     */
    public static void createSocket(String host, int port) {
        /* Server socket to receive connections */
        try {
            sock = new Socket(host, port);
            createWriter();
            createReader();
            registerWithIKernel();

        } catch (Exception ex) {
            show("Server " + host + ":" + port + " is unavailable");
            show("Please check WorldServer3D.");
            System.exit(-1);
        }
    }

    /**
     * The command is a text message (sequence of strings) that are sent to the
     * server through the network socket.
     *
     * @param s command in text format
     */
    protected static void sendMessage(String s) {
        //show("Sent to server: " + s);
        out.println(s);
    }

    /**
     * After having processed the command, the server responds to the client
     * with a failure or success report in text format.
     *
     * @return failure or success report of the command execution
     */
    @SuppressWarnings("empty-statement")
    protected static String receiveMessage() {
        StringBuilder s = new StringBuilder("");
        String st;
        if (!sock.isClosed()) {
            try {
                while (!in.ready());
                do {

                    st = in.readLine();
                    if (st != null && !st.equals("")) {
                        s.append(st);
                    }

                } while (in.ready());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return s.toString();
    }

    private static void createWriter() {
        try {
            out = new PrintWriter(sock.getOutputStream(), true);
        } catch (IOException ex) {
//            show("Error during writer creation.");
            Logger.logException(SocketUtility.class.getName(), ex);
        }
    }

    private static void createReader() {
        try {
            in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()), 65535);
        } catch (IOException ex) {
//            show("Error during reader creation.");
            Logger.logException(SocketUtility.class.getName(), ex);
        }
    }

    private static void registerWithIKernel() {
        String s;
        s = receiveMessage();
        show("Connecting to Server ... " + s);
    }

    /**
     * After the establishment of the connection, the client writer is ready to
     * send commands to the server.
     *
     * @return the writer or null if the client is not connected to the server
     */
    public static PrintWriter getWriter() {
        return out;
    }

    /**
     * After the establishment of the connection, the client reader is ready to
     * receive the failure/success report from the server.
     *
     * @return the reader or null if the client is not connected to the server
     */
    public static BufferedReader getReader() {
        return in;
    }

    /**
     * Display a text on the standard output.
     *
     * @param s text to be displayed
     */
    protected static void show(String s) {
        //System.out.println(s);
        ws3dproxy.util.Logger.logErr(s);
    }
}
