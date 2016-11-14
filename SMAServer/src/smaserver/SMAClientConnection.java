package smaserver;


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
            while((inputLine = in.readLine()) != null) {
                if(inputLine.equals("CLOSE")){
                    out.println("SERVER TERMINATING CONNECTION");
                    break;
                }
                System.out.println("CLIENT SAYS: " + inputLine);
                out.println("ECHO BACK: " + inputLine);
            }
        }catch (IOException e){
            //
        }
        System.out.println("THREAD TERMINATED");
    }
}
