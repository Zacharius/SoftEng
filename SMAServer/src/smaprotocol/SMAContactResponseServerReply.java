package smaprotocol;

/**
 * Created by elijah on 12/7/2016.
 */
public class SMAContactResponseServerReply {
    private int messageType;
    private int messageID;
    private boolean status;
    private String reason;
    private String publicKey;

    public SMAContactResponseServerReply(
            int messageType,
            int messageID,
            boolean status,
            String reason,
            String publicKey)
    {
        this.messageType = messageType;
        this.messageID = messageID;
        this.status = status;
        this.reason = reason;
        this.publicKey = publicKey;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getMessageID() {
        return messageID;
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
