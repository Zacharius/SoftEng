package smaprotocol;

import com.sun.org.apache.xpath.internal.operations.Bool;
import smaserver.Message;
import smaserver.DBAccess;
import com.google.gson.Gson;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.function.BooleanSupplier;

/**
 * Created by elijah on 11/21/2016.
 *
 * The SMAProtocolHandler contains all code for determining appropriate communication between server and client.
 */
public class SMAProtocolHandler {
    private Gson gson;

    public SMAProtocolHandler(Gson gson){
        this.gson = gson;
    }

    /**
     * Return a network response object specific to the authentication request handled. This is handled in its own
     * method due to the nature of authentication and the Thread needing to know the authentication state resulting
     * from an auth request.
     *
     * @param message is the String input passed to the server.
     * @return SMANetworkResponse object seed to indicate if authentication was successful.
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
        String ouptut;
        switch(request.getMessageType()){

            // 3 indicates a change of password request.
            case 3:
                System.out.println("[PROTOCOL LOG]: handling password change request");
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
                System.out.println("[PROTOCOL LOG}: handling contact request");
                printClientLogMessage(clientID, "handling contact request");
                return sendContactRequest(input, clientID);
            case 8:
                System.out.println("[PROTOCOL LOG}: handling contact response");
                SMAChangePublicKeyRequest newPublicKey = gson.fromJson(input, SMAChangePublicKeyRequest.class);
                if(DBAccess.changePubKey(clientID, newPublicKey.getPublicKey())){
                    status = true;
                } else {
                    reason = "failed to update public key";
                }
                break;
            // 11 indicates the user is sending their response to a contact request.
            case 11:
                printClientLogMessage(clientID, "handling contact response");

                return formatContactResponseServerReply(input, clientID);
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

    public String formatContactResponseServerReply(String input, String clientID){
        boolean status = false;
        String reason = null;
        String key = null;

        // Get the contact request response object.
        SMAContactResponseMessage contactResponse = gson.fromJson(input, SMAContactResponseMessage.class);

        // Attempt to add a message to the database containing the response.
        if(DBAccess.addMessage(
                clientID,
                contactResponse.getRecipientID(),
                String.valueOf(contactResponse.isStatus()),
                new Timestamp(Calendar.getInstance().get(Calendar.MILLISECOND)),
                11,
                contactResponse.getMessageID())
                ) {
            status = true;

            // The contact response was successfully added to the server. Now the actual response must be determined.
            // Did the user accept this request?
            if(contactResponse.isStatus()) {
                // Get the key for the user who sent the request. (the recipient of the response)
                key = DBAccess.getPublicKey(contactResponse.getRecipientID());
            }
        }else{
            reason = "DB ACCESS FAILURE: could not forward contact response";
        }

        return gson.toJson(new SMAContactResponseServerReply(
                12,
                contactResponse.getMessageID(),
                status,
                reason,
                key
        ));
    }
    /**
     * Takes an internal Message object and formats an appropriate response based on its contents. Types may include
     * but are not limited to:
     *      -contact requests
     *      -contact responses
     *      -message
     */
    public String getOutgoingMessage(Message message){
        String output = null;
        System.out.println("{PROTOCOL LOG]: message is of type " + message.getMessageType());
        switch (message.getMessageType()){
            // This is a contact request from a user.
            case 5:
                System.out.println("[PROTOCOL LOG]: handling outgoing contact request");
                output = getForwardContactRequestMessage(message);
                break;

            // This is a contact response.
            case 11:
                System.out.println("[PROTOCOL LOG]: handling forward contact response");
                output = getForwardContactResponseMessage(message);
                break;

            default:
        }
        return output;
    }


    public String getForwardContactResponseMessage(Message message){
        //  Set the key to the sender's if this request was accepted else null.;
        String publicKey = Boolean.parseBoolean(message.getContent()) ? DBAccess.getPublicKey(message.getSenderID()) : null;

        return gson.toJson(new SMAForwardContactResponseMessage(
                5,
                message.getMessageID(),
                message.getSenderID(),
                Boolean.parseBoolean(message.getContent()),
                publicKey
        ));
    }

    /**
     *
     * @param message is the Message object containing the database Message which needs to be converted to output.
     * @return a String representation of a contact request to be forwarded to a user.
     */
    public String getForwardContactRequestMessage(Message message){
        System.out.println("[SERVER]: forwarding contact request");
        return gson.toJson(
                new SMAForwardContactMessage(
                    10,
                    message.getMessageID(),
                    message.getSenderID()
                ));
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
