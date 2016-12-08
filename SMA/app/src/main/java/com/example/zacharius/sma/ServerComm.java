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

        try
        {

            if(server != null){
                PrintWriter write = new PrintWriter(server.getOutputStream(), true);
                System.out.println(object.toString());
                write.println(object.toString());
                messageID++;

            }
            else{
                System.out.print("Server object is null");
            }



        } catch (Exception e)
        {
            e.printStackTrace();
        }

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
                        String messageID = object.getString("messageID");
                        boolean status;
                        String id;

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
                            case 5://accepted contact
                                status = object.getBoolean("status");
                                id = object.getString("senderID");
                                if(status)
                                {
                                    Log.d("ServerListener", "Accepted Contact " + id);
                                    Toast.makeText(this, "Accepted Contact " + id, Toast.LENGTH_SHORT);

                                    db = helper.getWritableDatabase();

                                    values = new ContentValues();
                                    values.put(DatabaseContract.ContactTable.COLUMN_USERID, id);
                                    values.put(DatabaseContract.ContactTable.COLUMN_NICKNAME, id);
                                    values.put(DatabaseContract.ContactTable.COLUMN_STATUS, 0);

                                    String key = object.getString("publicKey");
                                    values.put(DatabaseContract.ContactTable.COLUMN_KEY, key);

                                    db.insert(DatabaseContract.ContactTable.TABLE_NAME, null, values);
                                    db.close();
                                }
                                else
                                {
                                    Log.d("ServerListener", "Denied Contact " + id);
                                    Toast.makeText(this, "Denied Contact " + id, Toast.LENGTH_SHORT);
                                }
                                break;
                            case 6://receive message
                                db = helper.getWritableDatabase();
                                values = new ContentValues();

                                id = object.getString("senderID");
                                values.put(DatabaseContract.MessageTable.COLUMN_CONTACT, id);


                                values.put(DatabaseContract.MessageTable.COLUMN_TIMEREC, System.currentTimeMillis());

                                String content = object.getString("content");
                                values.put(DatabaseContract.MessageTable.COLUMN_CONTENT, content);

                                String timeout = object.getString("timeout");
                                values.put(DatabaseContract.MessageTable.COLUMN_CONTENT, timeout);

                                db.insert(DatabaseContract.MessageTable.TABLE_NAME, null, values);
                                db.close();

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