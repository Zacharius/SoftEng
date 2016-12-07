package smaprotocol;

/**
 * Created by elijah on 12/7/2016.
 */
public class SMAContactResponseMessage {
    private int messageType;
    private int messageID;
    private String recipientID;
    private boolean status;
    public SMAContactResponseMessage(int messageType, int messageID, String recipientID, boolean status){
        this.messageType = messageType;
        this.messageID = messageID;
        this.recipientID = recipientID;
        this.status = status;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getRecipientID() {
        return recipientID;
    }

    public boolean isStatus() {
        return status;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setMessageID(int messageID) {
        this.messageID = messageID;
    }

    public void setRecipientID(String recipientID) {
        this.recipientID = recipientID;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
