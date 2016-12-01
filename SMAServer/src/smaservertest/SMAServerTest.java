package smaservertest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Created by elijah on 11/13/2016.
 */
public class SMAServerTest {
    public static void main(String args[])throws IOException{
        String hostName = "127.0.0.1";
        int portNumber = 4269;
        try(
            Socket clientSocket = new Socket(hostName, portNumber);
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        ){
            String userInput;
            while((userInput = stdIn.readLine()) != null){
                out.println(userInput);
                System.out.println("SERVER: " + in.readLine());
            }
        }
    }
}
