package smaprotocol;

/**
 * Created by elijah on 12/7/2016.
 */
public class SMAContactResponseServerReply {
    private int messageType;
    private String contactID;
    private boolean status;
    private String reason;
    private String publicKey;

    public SMAContactResponseServerReply(
            int messageType,
            String contactID,
            boolean status,
            String reason,
            String publicKey)
    {
        this.messageType = messageType;
        this.contactID = contactID;
        this.status = status;
        this.reason = reason;
        this.publicKey = publicKey;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getContactID() {
        return contactID;
    }

    public boolean isStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
