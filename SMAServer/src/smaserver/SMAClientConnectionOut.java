package smaserver;

import java.lang.Runnable;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Thread.interrupted;

/**
 * Created by elijah on 12/5/2016.
 */
class SMAClientConnectionOut implements Runnable {
    private String clientID;
    private PrintWriter out;

    SMAClientConnectionOut(String clientID, PrintWriter out) {
        this.clientID = clientID;
        this.out = out;
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run(){
        printClientLogMessage("output thread started");
        // The actions must be performed in a loop.
        while(interrupted()){
            // Messages for this client need to be fetched from the server.

            // In a loop, each of these needs to be written to the client.
            try {
                // Loop through the messages we have writing one at a time to the client.
            }catch(Exception e) {
                // Put all the leftover messages back in the database.
            }
        }
        printClientLogMessage("output thread stopping");
    }

    private void printServerLogMessage(String msg){
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("[" + sdf.format(date) + "][SERVER]: " + msg);
    }

    private void printClientLogMessage(String msg){
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("[" + sdf.format(date) + "][CLIENT: " + clientID + "]: " + msg);
    }
}
