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

    /**
     * Return a network response object specific to the authentication request handled.
     * @param message
     * @return
     */
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
        if(request.getPassword().
		equals(DBAccess.getPassword(request.getSenderID())) ) {
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

    /**
     * This takes a String message sent to the server and formats an appropriate response.
     * @param input
     * @param clientID
     * @return
     */
    public String getResponse(String input, String clientID){
        SMAGenericNetworkMessage request = gson.fromJson(input, SMAGenericNetworkMessage.class);
        boolean status = false;
        String reason = "";
        switch(request.getMessageType()){

            // Message type 3 indicates a change of password request.
            case 3:
                SMAPasswordChangeRequest newPassword = gson.fromJson(input, SMAPasswordChangeRequest.class);

                // Make a call to the DBAccess class to update password using the clientID parameter
                // and newPassword.getNewPassword() and check if it succeeded.
                if(true){
                    status = true;
                } else {
                    reason = "The reason the password couldn't be updated goes here.";
                }
                break;

            // The default action right now is to simply return the request.
            default:
                return input;
        }

        // Most responses will involve a generic pass/fail.
        SMANetworkResponse response = new SMANetworkResponse(
                2,
                request.getMessageID(),
                status,
                reason);
        return gson.toJson(response);
    }
}
