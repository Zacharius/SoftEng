package smaserver;

import com.google.gson.Gson;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
                if(DBAccess.changePassword(clientID, newPassword.getNewPassword())){
                    status = true;
                } else {
                    reason = "failed to update user password.";
                }
                break;
            case 4:
                printClientLogMessage(clientID, "handling contact request");
                return sendContactRequest(input, clientID);
            case 8:
                SMAChangePublicKeyRequest newPublicKey = gson.fromJson(input, SMAChangePublicKeyRequest.class);
                if(DBAccess.changePubKey(clientID, newPublicKey.getPublicKey())){
                    status = true;
                } else {
                    reason = "failed to update public key";
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

    /**
     * Given the input line and clientID, return a server response message to a contact request.
     * @param input represents a line of input. Type should be checked prior to calling this.
     * @param clientID is the clientID sending the request.
     * @return is a String to be sent to the client in response to this request.
     */
    private String sendContactRequest(String input, String clientID){
        // Create an SMAContactRequestMessage object.
        SMAContactRequestMessage contactRequest = gson.fromJson(input, SMAContactRequestMessage.class);

        printClientLogMessage(clientID, "contact request recipient is " + contactRequest.getRecipient());

        // See if the contact request can be sent by checking if the user exists or not.
        if(!DBAccess.userExists(contactRequest.getRecipient())){

            printClientLogMessage(clientID, "contact request failed, " + contactRequest.getRecipient() + " does not exist");
            return gson.toJson(new SMANetworkResponse(
                    9,
                    contactRequest.getMessageID(),
                    false,
                    "INVALID USER: user does not exist"
            ));
        }else{
            printClientLogMessage(clientID, contactRequest.getRecipient() + " exists, routing request");
            // Insert this message into the database to be sent later.
            if(!DBAccess.addMessage(clientID, contactRequest.getRecipient(), null, new Timestamp(Calendar.getInstance().get(Calendar.MILLISECOND)), 5, contactRequest.getMessageID())){
                printClientLogMessage(clientID, "request could not be sent, database access failed");
                return gson.toJson(new SMANetworkResponse(
                        9,
                        contactRequest.getMessageID(),
                        false,
                        "DB ACCESS FAILURE: could not route contact request"
                ));
            }else{
                printClientLogMessage(clientID, "request sent");
                return gson.toJson(new SMANetworkResponse(
                        9,
                        contactRequest.getMessageID(),
                        true,
                        null
                ));
            }
        }
    }

    /**
     * Print a log messages with the [CLIENT] tag to console
     * TODO: possibly create a separate logging Thread to ensure console is clean while
     * administrators are interacting with it
     * @param clientID is the client we're associating this logging message with.
     * @param msg is the message we want to print to console.
     */
    private void printClientLogMessage(String clientID, String msg){
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println("[" + sdf.format(date) + "][CLIENT: " + clientID + "]: " + msg);
    }
}
