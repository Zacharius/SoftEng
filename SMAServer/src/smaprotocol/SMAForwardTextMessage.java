package smaprotocol;

import java.sql.Timestamp;

/**
 * Created by elijah on 12/10/2016.
 */
public class SMAForwardTextMessage {
    int messageType;
    int messageID;
    String senderID;
    Timestamp timestamp;
    int timeout;
    String content;
    public SMAForwardTextMessage(int messageType, int messageID, String senderID, Timestamp timestamp,int timeout, String content){
        this.messageType = messageType;
        this.messageID = messageID;
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.timeout = timeout;
        this.content = content;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getSenderID() {
        return senderID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getContent() {
        return content;
    }
}
