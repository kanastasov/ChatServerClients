package chatserverclients;

/**
 * <p>MyChatServer.java is responsible for hosting the server concurently.</p>
 *
 * <p>This program is part of the solution for the second ICA for AJP in
 * Teesside University.</p>
 *
 * <p>AJP middleware 2013-SOLUTION is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.</p>
 *
 * <p>This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.</p>
 *
 * <p>You should have received a copy of the GNU General Public License along
 * with this program. If not, see http://www.gnu.org/licenses/.</p>
 *
 * <p>Copyright Kiril Anastasov L1087591@live.tees.ac.uk 10-April-2013 </p>
 */
import java.awt.BorderLayout;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import javax.swing.*;

public class MyChatServer extends JFrame {

    /**
     * @param jta is the text area for displaying the text in the GUI
     */
    private JTextArea jta;
    /**
     * @param outputStreams is a hash table which does not allow duplicate
     * elements and consists of keys and values.
     */
    private Hashtable outputStreams;
    /**
     * @param serverSocket is used to establish a server.
     */
    private ServerSocket serverSocket;

    /**
     * ServerThread is an inner class for the thread
     *
     */
    private class ServerThread extends Thread {

        /**
         * @param server is an object of type MyChatServer.
         */
        private MyChatServer server;
        /**
         * @param socket opens a socket so the client can communicate with the
         * server.
         */
        private Socket socket;
        /**
         * @param chatServer is a constant object of type MyChatServer.
         */
        private final MyChatServer chatServer;

        /**
         * ServerThread is a parameterized constructor.
         *
         * @param server initialize the server
         * @param socket initialize the socket
         */
        public ServerThread(MyChatServer server, Socket socket) {
            super();
            chatServer = MyChatServer.this;
            this.socket = socket;
            this.server = server;
//            call the run
            start();
        }

        @Override
        public void run() {
            try {
                //input stream to read from the socket
                DataInputStream din = new DataInputStream(socket.getInputStream());
                do {
                    String string = din.readUTF();
                    server.sendToAll(string);
                    jta.append((new StringBuilder(String.valueOf(string))).append('\n').toString());
                } while (true);
            } catch (IOException e) {
                System.err.println(e);
            }
        }
    }

    /**
     * main method to start the client.
     *
     * @param args
     */
    public static void main(String args[]) {
        new MyChatServer();
    }

    /**
     * MyChatServer default constructor initializing the GUI for the client.
     *
     */
    public MyChatServer() {
        jta = new JTextArea();
        outputStreams = new Hashtable();
        setLayout(new BorderLayout());
        add(new JScrollPane(jta), "Center");
        setTitle("ConcurrentServer");
        setSize(500, 300);
        setDefaultCloseOperation(3);
        setLocationRelativeTo(null);
        setVisible(true);
        jta.setEditable(false);
        listen();
    }

    /**
     * listen() server listen for connection
     *
     */
    private void listen() {
        try {
//            serverSocket listens for connection on local port 8000
            serverSocket = new ServerSocket(8000);
            jta.append((new StringBuilder("ConcurrentServer started at: ")).append(new Date()).append('\n').toString());
            do {
//                after the server socket is created, waits until client connects to the server.
                Socket socket = serverSocket.accept();
                jta.append((new StringBuilder("Connection from ")).append(socket).append(" at ").append(new Date()).append('\n').toString());
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
                outputStreams.put(socket, dout);
                new ServerThread(this, socket);
            } while (true);
        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

    /**
     * getOutputStreams fetch the elements sequentially.
     *
     * @return Returns an enumeration of the values in this hashtable
     */
    public Enumeration getOutputStreams() {
        return outputStreams.elements();
    }

    /**
     * sendToAll() goes through the hashtable and writes the message.
     *
     * @param message the message to be send
     */
    public void sendToAll(String message) {
        for (Enumeration e = getOutputStreams(); e.hasMoreElements();) {
            DataOutputStream dout = (DataOutputStream) e.nextElement();
            try {
                dout.writeUTF(message);
            } catch (IOException ex) {
                System.err.println(ex);
            }
        }

    }
}
