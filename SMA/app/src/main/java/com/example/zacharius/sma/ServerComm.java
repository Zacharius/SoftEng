package com.example.zacharius.sma;

import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.util.logging.SocketHandler;

/**
 * Created by zacharius on 10/23/16.
 */
public class ServerComm extends Service
{

    private static Socket server;
    private static String address;
    private static int port;
    private int messageID;
    private final IBinder binder = new ServerBinder();

    public ServerComm()
    {

    }

    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Log.d("ServerComm", "Service Starting ");

        address = intent.getStringExtra("address");
        port = intent.getIntExtra("port", -1);

        Intent serverListener = new Intent(this, ServerComm.ServerListener.class);
        startService(serverListener);

        return START_NOT_STICKY;
    }

    public void onDestroy()
    {
        Log.d("ServerComm", "Service ending");
    }


    public IBinder onBind(Intent intent){
        return binder;
    }



    public class ServerBinder extends Binder
    {
        public ServerComm getServer()
        {
            return  ServerComm.this;
        }
    }


    public void writeServer(JSONObject object)
    {

        ServerWriter serverWriter = new ServerWriter(object);
        (new Thread(serverWriter)).start();

    }

    public void checkCredentials(String id, String password)
    {

        Log.d("ServerComm","Checking credentials");
        JSONObject object = new JSONObject();

        try{
            object.put("messageType", 1);
            object.put("messageID", messageID);
            object.put("senderID", id);
            object.put("password", password);

            writeServer(object);

        }catch(JSONException e)
        {
            e.printStackTrace();
        }


    }

    public void pushPublicKey(String pub)
    {
        Log.d("ServerComm", "sending public key to server");

        JSONObject json = new JSONObject();

        try{
            json.put("messageType", 8);
            json.put("messageID", messageID);
            json.put("publicKey", pub);

            writeServer(json);
        }catch (JSONException e){
            e.printStackTrace();
        }

    }

    public void changePassword(String pass)
    {
        Log.d("ServerComm", "sending new password to server");

        JSONObject json = new JSONObject();

        try{
            json.put("messageType", 3);
            json.put("messageID", messageID);
            json.put("newPassword", pass);

            writeServer(json);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void contactRequest(String contactID)
    {
        Log.d("ServerComm", "sending contact request to server");

        JSONObject json = new JSONObject();

        try{
            json.put("messageType", 4);
            json.put("messageID", messageID);
            json.put("recipientID", contactID);

            writeServer(json);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void contactResponse(String contactID, boolean response)
    {
        Log.d("ServerComm", "accepting contact request for " + contactID);

        JSONObject json = new JSONObject();

        try{
            json.put("messageType", 11);
            json.put("messageID", messageID);
            json.put("recipientID", contactID);
            json.put("status", response);

            writeServer(json);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void sendText(String contactID, int timeout, String content)
    {
        Log.d("ServerComm", "sending text for " + contactID + " to server");

        JSONObject json = new JSONObject();

        try{
            json.put("messageType", 6);
            json.put("messageID", messageID);
            json.put("recipientID", contactID);
            json.put("timestamp", System.currentTimeMillis());
            json.put("timeout", timeout);
            json.put("content", content);


            writeServer(json);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public class ServerWriter implements Runnable
    {
        JSONObject object;
        public ServerWriter(JSONObject object)
        {
            this.object = object;
        }

        @Override
        public void run()
        {
            try
            {

                if(server != null){
                    PrintWriter write = new PrintWriter(server.getOutputStream(), true);
                    Log.d("ServerWriter", object.toString());
                    write.println(object.toString());
                    messageID++;

                }
                else{
                    Log.d("ServerWriter","Server object is null");
                }



            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }



    }



    public static class ServerListener extends IntentService
    {
        String serverMsg;

        public ServerListener()
        {
            super("ServerListener");
        }

        @Override
        protected void onHandleIntent(Intent intent)
        {
            while(1==1)
            {
                try
                {
                    server = new Socket(address, port);
                    break;
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            Log.d("ServerComm", "Connected to Server");
            publishProgress("Connected to Server");
            try
            {
                    BufferedReader read = new BufferedReader((new InputStreamReader(server.getInputStream())));
                    while ((serverMsg = read.readLine()) != null)
                    {
                        publishProgress(serverMsg);
                        Log.d("ServerComm", serverMsg);

                        DatabaseHelper helper = new DatabaseHelper(this);
                        SQLiteDatabase db;// = helper.getWritableDatabase();
                        ContentValues values;


                        JSONObject object = new JSONObject(serverMsg);
                        int messageType = object.getInt("messageType");
                        boolean status;
                        String reason, key, id;

                        switch (messageType)
                        {
                            case 2://login response
                                status = object.getBoolean("status");
                                if (status)
                                {
                                    LoginActivity.credentials = 1;
                                }
                                if (!status)
                                {
                                    LoginActivity.credentials = -1;
                                    LoginActivity.errMsg = object.getString("reason");
                                }
                                break;
                            case 12://contact response server reply
                                status = object.getBoolean("status");
                                id = object.getString("contactID");
                                if(status)
                                {
                                    Log.d("ServerListener", "Server has succesfully added " + id + " to your contact list");
                                    db = helper.getWritableDatabase();

                                    values = new ContentValues();
                                    values.put(DatabaseContract.ContactTable.COLUMN_NICKNAME, id);
                                    values.put(DatabaseContract.ContactTable.COLUMN_STATUS, 0);

                                    key = object.getString("publicKey");
                                    values.put(DatabaseContract.ContactTable.COLUMN_KEY, key);

                                    String selection = DatabaseContract.ContactTable.COLUMN_USERID + " LIKE ?";
                                    String [] selectionArgs = { id };

                                    db.update(
                                            DatabaseContract.ContactTable.TABLE_NAME,
                                            values,
                                            selection,
                                            selectionArgs);
                                    db.close();

                                }
                                else
                                {
                                    Log.d("ServerListener", "Denied Contact " + id);
                                    Toast.makeText(this, "Denied Contact " + id, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 7://receive message
                                db = helper.getWritableDatabase();
                                values = new ContentValues();

                                values.put(DatabaseContract.MessageTable.COLUMN_TYPE, "1");

                                id = object.getString("senderID");
                                values.put(DatabaseContract.MessageTable.COLUMN_CONTACT, id);


                                values.put(DatabaseContract.MessageTable.COLUMN_TIMEREC, System.currentTimeMillis());

                                String content = object.getString("content");
                                values.put(DatabaseContract.MessageTable.COLUMN_CONTENT, content);

                                String timeout = object.getString("timeout");
                                values.put(DatabaseContract.MessageTable.COLUMN_TIMEOUT, timeout);

                                db.insert(DatabaseContract.MessageTable.TABLE_NAME, null, values);
                                db.close();

                                if(ContactDetailActivity.name == id)
                                    ContactDetailActivity.displayMessages();
                                break;
                            case 9://accepted contact request
                                status = object.getBoolean("status");
                                id = object.getString("contactID");
                                if(status)
                                {
                                    Log.d("ServerListener", "Server has accepted Contact Request for " + id);
                                    Toast.makeText(this, "Server has accepted Contact Request for " + id, Toast.LENGTH_SHORT).show();

                                    db = helper.getWritableDatabase();
                                    values = new ContentValues();


                                    values.put(DatabaseContract.ContactTable.COLUMN_USERID, id);
                                    values.put(DatabaseContract.ContactTable.COLUMN_STATUS, 1);

                                    db.insert(DatabaseContract.ContactTable.TABLE_NAME, null, values);
                                    db.close();
                                }
                                else
                                {
                                    reason = object.getString("reason");

                                    Log.d("ServerListener", "Server has denied Contact Request for " + id + ": " + reason);
                                    Toast.makeText(this, "Server has denied Contact Request for " + id + ": " + reason, Toast.LENGTH_SHORT).show();
                                }
                                break;
                            case 10://Forwarded Contact Request
                                id = object.getString("senderID");

                                Log.d("ServerListener", id + " has requested you as a contact");
                                Toast.makeText(this, id + " has requested you as a contact", Toast.LENGTH_SHORT).show();

                                db = helper.getWritableDatabase();
                                values = new ContentValues();


                                values.put(DatabaseContract.ContactTable.COLUMN_USERID, id);
                                values.put(DatabaseContract.ContactTable.COLUMN_STATUS, 2);

                                db.insert(DatabaseContract.ContactTable.TABLE_NAME, null, values);
                                db.close();
                                break;
                            case 13://contact response reply
                                status = object.getBoolean("status");
                                id = object.getString("contactID");

                                db = helper.getWritableDatabase();
                                values = new ContentValues();

                                if(status)
                                {

                                    Log.d("ServerListener", id + " has accepted you as a contact");
                                    Toast.makeText(this, id + " has accepted us you a contact", Toast.LENGTH_SHORT).show();


                                    key = object.getString("publicKey");

                                    values.put(DatabaseContract.ContactTable.COLUMN_NICKNAME, id);
                                    values.put(DatabaseContract.ContactTable.COLUMN_STATUS, 0);
                                    values.put(DatabaseContract.ContactTable.COLUMN_KEY, key);

                                    String selection = DatabaseContract.ContactTable.COLUMN_USERID + " LIKE ?";
                                    String [] selectionArgs = { id };

                                    db.update(
                                            DatabaseContract.ContactTable.TABLE_NAME,
                                            values,
                                            selection,
                                            selectionArgs);
                                    db.close();
                                }
                                else{
                                    reason = object.getString("reason");

                                    Log.d("ServerListener", id + " has denied Contact Request :" + reason);
                                    Toast.makeText(this, id + " has denied Contact Request for :" + reason, Toast.LENGTH_SHORT).show();

                                    String selection = DatabaseContract.ContactTable.COLUMN_USERID + " LIKE ?";
                                    String [] selectionArgs = { id };

                                    db.delete(DatabaseContract.ContactTable.TABLE_NAME, selection, selectionArgs);
                                    db.close();
                                }
                                break;
                            default://unknown message
                                Log.d("ServerListener", "Unknwon Message from Server");
                                Toast.makeText(this, "Unknown Message from server", Toast.LENGTH_SHORT).show();
                        }
                    }
            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }


        private void publishProgress(final String serverMsg)
        {
            if(LoginActivity.context != null)
            {
                Toast.makeText(LoginActivity.context,
                        serverMsg,
                        Toast.LENGTH_SHORT)
                        .show();
            }


        }


    }

    public static Socket getServer()
    {
        return server;
    }

    public static void setServer(Socket server)
    {
        ServerComm.server = server;
    }


}