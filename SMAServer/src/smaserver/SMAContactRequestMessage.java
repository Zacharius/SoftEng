package smaserver;

/**
 * Created by elijah on 12/5/2016.
 */
class SMAContactRequestMessage {
    private int messageType;
    private int messageID;
    private String recipient;
    public SMAContactRequestMessage(int messageType, int messageID, String recipient){
        this.messageType = messageType;
        this.messageID = messageID;
        this.recipient = recipient;
    }
    public String getRecipient(){
        return recipient;
    }
    public int getMessageID(){
        return messageID;
    }
    public int getMessageType(){
        return messageType;
    }
}
