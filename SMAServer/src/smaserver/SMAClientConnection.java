package smaserver;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by elijah on 11/14/2016.
 */
public class SMAClientConnection implements Runnable {
    private Socket clientSocket;
    private String clientID;
    private PrintWriter out;
    private BufferedReader in;
    private Gson gson;
    private SMAProtocolHandler handler;

    public SMAClientConnection(Socket client)throws IOException{
        this.clientSocket = client;
        gson = new Gson();
        handler = new SMAProtocolHandler(gson);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    @Override
    public void run(){
        printServerLogMessage("client connected, waiting for authentication request");
        try{
            // Attempt to authenticate user and listen for incoming requests if successful.
            if(authenticateUser()){
                printServerLogMessage("user authenticated successfully");
                printServerLogMessage("session started for " + clientID);
                listenForIncomingRequests();
            }
        }catch(IOException e){
            // It tried to get away but we caught it. Hopefully this won't happen again.
        }
        printServerLogMessage("connection terminated");
    }

    public void listenForIncomingRequests()throws IOException{
        // While we're receiving input, print a log message, get a response from the SMAProtocolHandler,
        // print a log message for the response, and write the response to the Socket.
        String inputLine, outputLine;
        while((inputLine = in.readLine()) != null) {
            printClientLogMessage("says, \"" + inputLine + "\"");
            outputLine = handler.getResponse(inputLine, clientID);
            printServerLogMessage("says to client: " + clientID + ", \"" + outputLine + "\"");
            out.println(outputLine);
        }
    }

    /**
     * Return true if a user authenticates successfully and set the cientID field.
     * @return boolean
     * @throws IOException
     */
    private boolean authenticateUser()throws IOException{
        boolean authenticated = false;
        String inputLine;
        while(!authenticated && ((inputLine = in.readLine()) != null)) {
            printServerLogMessage("incoming message \"" + inputLine + "\"");
            SMANetworkResponse response = handler.authenticateUser(inputLine);
            out.println(gson.toJson(response));
            authenticated = response.getStatus();
            if (!authenticated) {
                printServerLogMessage("client failed to authenticate");
            } else if(authenticated){
                clientID =  gson.fromJson(inputLine, SMAAuthenticationNetworkMessage.class).getSenderID();
            }
        }
        return authenticated;
    }

    /**
     * print log messages with the [SERVER] tag to console
     * TODO: possibly create a separate logging Thread to ensure console is clean while
     * administrators are interacting with it
     * @param msg
     */
    private void printServerLogMessage(String msg){
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("[" + sdf.format(date) + "][SERVER]: " + msg);
    }

    public void printClientLogMessage(String msg){
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("[" + sdf.format(date) + "][CLIENT: " + clientID + "]: " + msg);
    }
}
