package com.example.zacharius.sma;

import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.SocketHandler;

/**
 * Created by zacharius on 10/23/16.
 */
public class ServerComm
{

    private static Socket server;

    public static boolean checkCredentials(String id, String password)
    {
        return true;
    }

    public static class StartServer extends AsyncTask<Void, Void, Void>
    {
        String address;
        int port;
        String serverMsg;

        public StartServer(String address, int port)
        {
            this.address = address;
            this.port = port;
        }


        public void writeServer(String msg)
        {
            JSONObject object = new JSONObject();
            try
            {
                object.put("messageType", "1");
                object.put("messageID", "2");
                object.put("senderID", "3");
                object.put("content", msg);
                if(server != null){
                    PrintWriter write = new PrintWriter(server.getOutputStream(), true);
                    System.out.println(object.toString());
                    write.println(object.toString());

                    Toast.makeText(LoginActivity.context,
                            "write to Server: " + msg,
                            Toast.LENGTH_SHORT)
                            .show();
                }
                else{
                    System.out.print("Server object is null");
                }



            } catch (Exception e)
            {
                e.printStackTrace();
            }

        }


        @Override
        protected Void doInBackground(Void... voids)
        {

            try
            {
                server = new Socket(address, port);
                serverMsg = "Connected to Server";
                publishProgress();
                BufferedReader read = new BufferedReader((new InputStreamReader(server.getInputStream())));
                PrintWriter write = new PrintWriter(server.getOutputStream());

                String readLine;

                write.println("Hi!");


                while ((serverMsg = "server says: " + read.readLine()) != null)
                {
                    publishProgress();
                }


            } catch (IOException e)
            {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values)
        {
            super.onProgressUpdate(values);

            Toast.makeText(LoginActivity.context,
                    serverMsg,
                    Toast.LENGTH_SHORT)
                    .show();
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