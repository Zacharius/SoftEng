package smaserver;

/**
 * Created by elijah on 12/5/2016.
 */
class SMAContactRequestMessage {
    private int messageType;
    private int messageID;
    private String recipientID;
    public SMAContactRequestMessage(int messageType, int messageID, String recipient){
        this.messageType = messageType;
        this.messageID = messageID;
        this.recipientID = recipient;
    }
    public String getRecipient(){
        return recipientID;
    }
    public int getMessageID(){
        return messageID;
    }
    public int getMessageType(){
        return messageType;
    }
}
