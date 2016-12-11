package smaprotocol;

/**
 * Created by elijah on 12/7/2016.
 */
public class SMAForwardContactResponseMessage {
    private int messageType;
    private int messageID;
    private String contactID;
    private boolean status;
    private String publicKey;

    public SMAForwardContactResponseMessage(
            int messageType,
            int messageID,
            String contactID,
            boolean status,
            String publicKey)
    {
        this.messageType = messageType;
        this.messageID = messageID;
        this.contactID = contactID;
        this.status = status;
        this.publicKey = publicKey;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getContactID() {
        return contactID;
    }

    public boolean isStatus() {
        return status;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
