package smaserver;

import java.io.*;
import java.net.*;
import java.util.Arrays;
import java.util.List;

/**
 * Created by elijah on 11/13/2016.
 */
public class SMAServer {
    private static int portNumber = 4269;
    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    /**
     * Open the Socket and create the Threads for the administrative console and listening for incoming
     * accesses.
     */
    public static void main(String args[])throws IOException{
        // Set the port.
        setPort(args);

        // Attempt to open a Socket and exit if it fails.
        if(!openSocket()) {
            System.exit(1);
        }

        // Open the admin console thread.
        createConsoleThread();

        // Call the function to listen on the open Socket.
        listen();
    }

    /**
     * Creates the admin console Thread.
     */
    private static void createConsoleThread(){
        Thread adminThread = new Thread(new SMAAdminConsole());
        adminThread.start();
    }

    /**
     *Pass the arguments array of Strings and set the port number if the -p flag is set.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    private static void setPort(String[] args){
        List<String> argumentsList = Arrays.asList(args);
        int index;
        int port = 4269;
        boolean portFound = false;
        if(argumentsList.contains("-p")) {
            index = argumentsList.indexOf("-p") + 1;
            if (index < argumentsList.size()) {
                try {
                    port = Integer.parseInt(argumentsList.get(index));
                    portFound = true;
                } catch (NumberFormatException e) {
                    //
                }
            }
            if(portFound){
                portNumber = port;
                System.out.println("The port flag has been set. Server is using requested port number: " + port);
            }else{
                System.out.println("The port flag (-p) was set but the port number argument was malformed or couldn't be found.");
                System.exit(1);
            }
        }else{
            System.out.println("The port flag was not set. Server is using default port number: " + portNumber);
        }
    }

    /**
     * Attempt to open a Socket and return if it failed or succeeded.
     * @return boolean value indicating if a Socket could be opened or not.
     */
    private static boolean openSocket(){
        boolean success = false;
        try{
            serverSocket = new ServerSocket(portNumber);
            success = true;
        }catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
        return success;
    }

    /**
     * Listen indefinitely for incoming connections, accept them, and pass them to their own Threads.
     * @throws IOException if serverSocket.accept() fails.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    private static void listen()throws IOException{
        // listen on open socket in a loop, accept connections, and spin off threads
        // when one is open
        while(true){
            clientSocket = serverSocket.accept();
            Thread myThread = new Thread(new SMAClientConnection(clientSocket));
            myThread.start();
        }
    }
}
