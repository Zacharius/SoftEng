package smaprotocol;

/**
 * Created by elijah on 11/30/2016.
 */
 public class SMAChangePublicKeyRequest {
    private int messageType;
    private int messageID;
    private String publicKey;

    public SMAChangePublicKeyRequest(int messageType, int messageID, String newPublicKey){
        this.messageType = messageType;
        this.messageID = messageID;
        this.publicKey = newPublicKey;
    }

    public int getMessageType() {
        return messageType;
    }

    public int getMessageID() {
        return messageID;
    }

    public String getPublicKey() {
        return publicKey;
    }
}
