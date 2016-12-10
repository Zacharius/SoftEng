package smaprotocol;

/**
 * Created by elijah on 12/9/2016.
 */
public class SMAContactRequestServerResponse {
    private int messageType;
    private String contactID;
    private boolean status;
    private String reason;
    public SMAContactRequestServerResponse(int messageType, String contactID, boolean status, String reason){
        this.messageType = messageType;
        this.contactID = contactID;
        this.status = status;
        this.reason = reason;
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
}
