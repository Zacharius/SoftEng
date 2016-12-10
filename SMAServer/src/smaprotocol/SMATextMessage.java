package smaprotocol;

/**
 * Created by elijah on 12/10/2016.
 */
public class SMATextMessage {
    int messageType;
    String recipientID;
    int timeout;
    String content;
    public SMATextMessage(int messageType, String recipientID, int timeout, String content){
        this.messageType = messageType;
        this.recipientID = recipientID;
        this.timeout = timeout;
        this.content = content;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public int getTimeout() {
        return timeout;
    }

    public String getContent() {
        return content;
    }
}
