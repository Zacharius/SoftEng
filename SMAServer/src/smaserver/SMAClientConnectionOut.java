package smaserver;

import com.google.gson.Gson;
import smaprotocol.*;
import java.lang.Runnable;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static java.lang.Thread.interrupted;

/**
 * Created by elijah on 12/5/2016.
 *
 * The SMAClientConnectionOut Thread handles outgoing messages to the client routed from the server database. It
 * handles no authentication and is written under the assumption that the creating Thread has authenticated a client.
 * Instead, aside from checking the database and sending outgoing messages, the only other task it is concerned only
 * with is whether or not it has been interrupted in which case it must gracefully terminate itself.
 */
class SMAClientConnectionOut implements Runnable {
    private String clientID;
    private PrintWriter out;
    private SMAProtocolHandler handler;


    SMAClientConnectionOut(String clientID, PrintWriter out) {
        this.clientID = clientID;
        this.out = out;
        this.handler = new SMAProtocolHandler(new Gson());
    }

    @Override
    @SuppressWarnings("InfiniteLoopStatement")
    public void run(){

        String output = null;
        printClientLogMessage("output thread started");

        // The actions must be performed in a loop.
        while(!interrupted()){
            // Messages for this client need to be fetched from the server.
            printClientLogMessage("fetching user messages from server");
            ArrayList<Message> outgoingMessages = DBAccess.getMessages(clientID);
            printClientLogMessage(outgoingMessages.size() + " messages found");

            // In a loop, each of these needs to be written to the client.
            try {

                // Loop through the messages we have writing one at a time to the client.
                for(Message message : outgoingMessages){
                    // Format an appropriate message depending on the type we're sending.
                    output = handler.getOutgoingMessage(message);
                    printServerLogMessage("says, \"" + output + "\"");
                    out.println(output);
                    DBAccess.deleteMessage(message.getID(), false);
                }
                Thread.sleep(5000);
            }catch(Exception e) {
                // Why do they keep trying to escape? Should we call a professional?
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
