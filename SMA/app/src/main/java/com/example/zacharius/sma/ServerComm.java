package com.example.zacharius.sma;

import android.os.AsyncTask;
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
public class ServerComm extends AsyncTask<Void, Void, Void>
{

    private static Socket server;
    String address;
    int port;
    String serverMsg;

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

        System.out.println("Checking credentials");
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

    @Override
    protected Void doInBackground(Void... voids)
    {

        try
        {


            try{
                server = new Socket(address, port);
            }catch (ConnectException e)
            {
                e.printStackTrace();
                serverMsg = "can't connect to server";
                return null;

            }
            serverMsg = "Connected to Server";
            publishProgress();
            BufferedReader read = new BufferedReader((new InputStreamReader(server.getInputStream())));
            PrintWriter write = new PrintWriter(server.getOutputStream());

            String readLine;


            while ((serverMsg = read.readLine()) != null)
            {
                publishProgress();

                try{
                    JSONObject object = new JSONObject(serverMsg);
                    int messageType = object.getInt("messageType");
                    String messageID = object.getString("messageID");

                    switch(messageType)
                    {
                        case 2:
                            boolean status = object.getBoolean("status");
                            if(status)
                            {
                                LoginActivity.credentials = 1;
                            }
                            if(!status)
                            {
                                LoginActivity.credentials = -1;
                                LoginActivity.errMsg = object.getString("reason");
                            }
                    }
                }catch(JSONException e)
                {
                    e.printStackTrace();
                }


            }


        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... voids)
    {
    super.onProgressUpdate(voids);

    Toast.makeText(LoginActivity.context,
            "server says: " + serverMsg,
            Toast.LENGTH_SHORT)
            .show();
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