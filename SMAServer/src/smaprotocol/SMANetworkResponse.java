package smaprotocol;


import java.util.Date;

/**
 * Created by elijah on 11/23/2016.
 */
public class SMANetworkResponse {
    private int messageType;
    private int messageID;
    private Date timestamp;
    private boolean status;
    private String reason;

    SMANetworkResponse(
                int messageType,
                int messageID,
                boolean status,
                String reason
            ){
        this.messageType = messageType;
        this.messageID = messageID;
        this.timestamp = new Date();
        this.status = status;
        this.reason = reason;
    }

    public boolean getStatus(){
        return this.status;
    }
}
