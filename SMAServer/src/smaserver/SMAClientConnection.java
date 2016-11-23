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
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine;
            Gson gson = new Gson();
            SMAProtocolHandler handler = new SMAProtocolHandler(gson);

            // first we need to get the authentication message and check if this was successful
            boolean authenticated = false;
            while(!authenticated && ((inputLine = in.readLine()) != null)){
                SMANetworkResponse response = handler.authenticateUser(inputLine);
                out.println(gson.toJson(response));
                authenticated = response.getStatus();
                System.out.println("SERVER RECEIVED: " + inputLine);
            }
            System.out.println("SERVER AUTHENTICATED USER");
            while((inputLine = in.readLine()) != null) {
                SMAGenericNetworkMessage request = gson.fromJson(inputLine, SMAGenericNetworkMessage.class);
                request.print();
                // TODO: ProtocolHandler should return a valid response based on message
                // content
                out.println(gson.toJson(request));
            }
        }catch (IOException e){
            //
        }
        System.out.println("THREAD TERMINATED");
    }
}
