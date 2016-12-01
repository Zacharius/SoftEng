package smaserver;

import java.io.*;
import java.net.*;

/**
 * Created by elijah on 11/13/2016.
 */
public class SMAServer {
    // TODO: modify to take port number via command line
    private static int portNumber = 4269;
    private static ServerSocket serverSocket;
    private static Socket clientSocket = null;

    public static void main(String args[])throws IOException{
        // check if we opened socket and exit if it failed
        if(!openSocket()) {
            System.exit(1);
        }

        // call function to listen on open socket
        listen();
    }

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
