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
            // first we need to get the authentication message and check if this was successful
            if(authenticateUser()){
                printServerLogMessage("user authenticated successfully");

                // the primary thread loop waits for input and should pass them to the SMAProtocolHandler
                // to get an appropriate response
                String inputLine;
                while((inputLine = in.readLine()) != null) {
                    SMAGenericNetworkMessage request = gson.fromJson(inputLine, SMAGenericNetworkMessage.class);
                    request.print();
                    // TODO: ProtocolHandler should return a valid response based on message and take action
                    // based on each request
                    out.println(gson.toJson(request));
                }
            }
        }catch(IOException e){
            // System.out.print(e.getStackTrace());
        }
        printServerLogMessage("connection terminated");
    }

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
            }
        }
        return authenticated;
    }

    private void printServerLogMessage(String msg){
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("[" + sdf.format(date) + "][SERVER]: " + msg);
    }
}
