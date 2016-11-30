package smaserver;

/**
 * Created by elijah on 11/30/2016.
 */
public class SMAPasswordChangeRequest {
    private int messageType;
    private int  messageID;
    private String newPassword;

    public SMAPasswordChangeRequest(int messageType, int messageID, String newPassword){
        this.messageID = messageID;
        this.messageType = messageType;
        this.newPassword = newPassword;
    }

    public int getMessageType(){
        return messageType;
    }

    public int getMessageID(){
        return messageID;
    }

    public String getNewPassword(){
        return newPassword;
    }
}
