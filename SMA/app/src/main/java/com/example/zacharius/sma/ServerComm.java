package com.example.zacharius.sma;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.AsyncTask;
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
public class ServerComm
{

    private static Socket server;
    private static String address;
    private static int port;

    public ServerComm(String address, int port)
    {
        this.address = address;
        this.port = port;

    }


    public void writeServer(JSONObject object)
    {

        try
        {

            if(server != null){
                PrintWriter write = new PrintWriter(server.getOutputStream(), true);
                System.out.println(object.toString());
                write.println(object.toString());

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
            object.put("messageID", 1);
            object.put("senderID", id);
            object.put("password", password);

            writeServer(object);

        }catch(JSONException e)
        {
            e.printStackTrace();
        }


    }

    public static class ServerListener extends IntentService
    {
        String serverMsg;

        public ServerListener()
        {
            super("Server Listener");
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


                        JSONObject object = new JSONObject(serverMsg);
                        int messageType = object.getInt("messageType");
                        String messageID = object.getString("messageID");

                        switch (messageType)
                        {
                            case 2:
                                boolean status = object.getBoolean("status");
                                if (status)
                                {
                                    LoginActivity.credentials = 1;
                                }
                                if (!status)
                                {
                                    LoginActivity.credentials = -1;
                                    LoginActivity.errMsg = object.getString("reason");
                                }
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