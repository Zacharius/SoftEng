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
            String inputLine, outputLine;
            Gson gson = new Gson();
            while((inputLine = in.readLine()) != null) {
                SMANetworkMessage request = gson.fromJson(inputLine, SMANetworkMessage.class);
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
