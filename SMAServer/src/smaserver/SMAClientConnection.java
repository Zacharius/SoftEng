package smaserver;


import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by elijah on 11/14/2016.
 */
public class SMAClientConnection implements Runnable {
    private Socket clientSocket;
    public SMAClientConnection(Socket client){
        this.clientSocket = client;
    }

    @Override
    public void run(){
        try{
            // all reading from and writing to the open socket use these two objects
            // and require using println() and readLine() respectively
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            // stores each line of input off the socket connection
            String inputLine;

            // Our Gson parser and SMAProtocolHandler objects, the Gson parser is required here for
            // reactive purposes and by the SMAProtocolHandler object
            Gson gson = new Gson();
            SMAProtocolHandler handler = new SMAProtocolHandler(gson);

            // first we need to get the authentication message and check if this was successful
            boolean authenticated = false;
            while(!authenticated && ((inputLine = in.readLine()) != null)){
                System.out.println("SERVER RECEIVED: " + inputLine);
                SMANetworkResponse response = handler.authenticateUser(inputLine);
                out.println(gson.toJson(response));
                authenticated = response.getStatus();
                if(!authenticated){
                    System.out.println("SERVER DID NOT AUTHENTICATE USER");
                }
            }
            System.out.println("SERVER AUTHENTICATED USER");

            // the primary thread loop waits for input and should pass them to the SMAProtocolHandler
            // to get an appropriate response
            while((inputLine = in.readLine()) != null) {
                SMAGenericNetworkMessage request = gson.fromJson(inputLine, SMAGenericNetworkMessage.class);
                request.print();
                // TODO: ProtocolHandler should return a valid response based on message and take action
                // based on each request
                out.println(gson.toJson(request));
            }
        }catch (IOException e){
            //
        }
        System.out.println("THREAD TERMINATED");
    }
}
