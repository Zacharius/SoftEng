package smaprotocol;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by elijah on 11/21/2016.
 */
public class SMAGenericNetworkMessage {
    private int messageType;
    private int  messageID;
    private long timestamp;
    private String senderID;

    public SMAGenericNetworkMessage(int messageType, int messageID, long timestamp, String senderID){
        this.messageType = messageType;
        this.messageID = messageID;
        this.timestamp = timestamp;
        this.senderID = senderID;
    }
    public int getMessageType(){
        return this.messageType;
    }
    public int getMessageID(){
        return this.messageID;
    }
    public long getTimestamp() { return this.timestamp; }
    public String getSenderID(){
        return this.senderID;
    }
    public void print(){
        System.out.println("TYPE: " + this.messageType);
        System.out.println("ID: " + this.messageID);
        System.out.println("TIMESTAMP: " + this.timestamp);
        System.out.println("SENDER ID: " + this.senderID);
    }
}
