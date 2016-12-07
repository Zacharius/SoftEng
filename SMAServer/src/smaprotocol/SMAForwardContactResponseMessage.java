package smaprotocol;

/**
 * Created by elijah on 12/7/2016.
 */
public class SMAForwardContactResponseMessage {
    private int messageType;
    private int messageID;
    private String senderID;
    private boolean status;
    private String publicKey;

    public SMAForwardContactResponseMessage(
            int messageType,
            int messageID,
            String senderID,
            boolean status,
            String publicKey)
    {
        this.messageType = messageType;
        this.messageID = messageID;
        this.senderID = senderID;
        this.status = status;
        this.publicKey = publicKey;
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

    public boolean isStatus() {
        return status;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
