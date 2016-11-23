package smaserver;

import java.security.Timestamp;
import java.util.Date;

/**
 * Created by elijah on 11/21/2016.
 */
public class SMAAuthenticationNetworkMessage {
    private int messageType;
    private int messageID;
    private String senderID;
    private Date timestamp;
    private String password;
    public SMAAuthenticationNetworkMessage
            (
                    int messageType,
                    int messageID,
                    String senderID,
                    Date timestamp,
                    String password
            ){
        this.messageType = messageType;
        this.messageID = messageID;
        this.senderID = senderID;
        this.timestamp = timestamp;
        this.password = password;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public String getPassword() {
        if(password == null){
            return "";
        } else {
            return password;
        }
    }
}
