package smaserver;

import java.security.Timestamp;
import java.util.Date;

/**
 * Created by elijah on 11/21/2016.
 */
public class SMAAuthenticationNetworkMessage extends SMAGenericNetworkMessage {
    private String password;
    public SMAAuthenticationNetworkMessage
            (
                    int messageType,
                    int messageID,
                    String senderID,
                    Date timestamp,
                    String password
            ){
        super(messageType, messageID, timestamp, senderID);
        this.password = password;
    }
}
