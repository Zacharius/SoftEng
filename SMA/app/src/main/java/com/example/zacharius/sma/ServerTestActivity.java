package com.example.zacharius.sma;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class ServerTestActivity extends AppCompatActivity
{
    ServerComm.StartServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server_test);

    }

    public void onConnectServer(View v)
    {
        server = new ServerComm.StartServer("198.27.65.177", 6969);
        server.execute();
    }

    public void onSayHi(View v)
    {
        server.writeServer("Hi!");
    }

    public void onSayBye(View v)
    {
        server.writeServer("Bye!");
    }




}
