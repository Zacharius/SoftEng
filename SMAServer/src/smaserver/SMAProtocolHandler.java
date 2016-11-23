package smaserver;

import com.google.gson.Gson;

import java.util.Date;

/**
 * Created by elijah on 11/21/2016.
 */
public class SMAProtocolHandler {
    private Gson gson;

    public SMAProtocolHandler(Gson gson){
        this.gson = gson;
    }

    // returns an SMANetworkResponse appropriate to message content
    public SMANetworkResponse authenticateUser(String message){
        SMAGenericNetworkMessage incoming = gson.fromJson(message, SMAGenericNetworkMessage.class);

        // is this an authentication request
        if(incoming.getMessageType() != 1){
            return new SMANetworkResponse(
                    2,
                    incoming.getMessageID(),
                    false,
                    "BAD REQUEST: server expects message type 1 and received type " + incoming.getMessageType()
            );
        }

        // TODO: the following needs to be replaced with calls to the server database and should check:
        //     -does a user exist
        //     -does the username and password match
        SMAAuthenticationNetworkMessage request = gson.fromJson(message, SMAAuthenticationNetworkMessage.class);
        if(request.getPassword().equals("goodpassword")) {
            return new SMANetworkResponse(
                    2,
                    incoming.getMessageID(),
                    true,
                    null
            );
        } else {
            return new SMANetworkResponse(
                    2,
                    incoming.getMessageID(),
                    false,
                    "USERNAME PASSWORD MISMATCH: username and password do not match"
            );
        }
    }
}
