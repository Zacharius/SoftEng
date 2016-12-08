package smaprotocol;

/**
 * Created by elijah on 12/7/2016.
 */
public class SMAForwardContactMessage {
    private int messageType;
    private int messageID;
    private String senderID;

    public SMAForwardContactMessage(int messageType, int messageID, String senderID){
        this.messageType = messageType;
        this.messageID = messageID;
        this.senderID = senderID;
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
}
