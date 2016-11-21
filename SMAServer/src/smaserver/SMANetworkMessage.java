package smaserver;

/**
 * Created by elijah on 11/21/2016.
 */
public class SMANetworkMessage {
    private int messageType;
    private int messageID;
    private int senderID;
    private int recipientID;
    private String content;

    /*
     * The constructor requires that all fields be filled before a message object is created.
     */
    public SMANetworkMessage(int messageType, int messageID, int senderID, int recipientID, String content){
        this.messageType = messageType;
        this.messageID = messageID;
        this.senderID = senderID;
        this.recipientID = recipientID;
        this.content = content;
    }
    public int getMessageType(){
        return messageType;
    }
    public int getMessageID(){
        return this.messageID;
    }
    public int getSenderID(){
        return this.senderID;
    }
    public int getRecipientID(){
        return this.recipientID;
    }
    public String getContent(){
        return this.content;
    }
    public void print(){
        System.out.println("TYPE: " + this.messageType);
        System.out.println("ID: " + this.messageID);
        System.out.println("SENDER ID: " + this.senderID);
        System.out.println("RECIPIENT ID: " + this.recipientID);
        System.out.println("CONTENT: " + this.content);
    }
}
