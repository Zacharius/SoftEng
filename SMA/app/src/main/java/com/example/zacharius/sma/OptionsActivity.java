package com.example.zacharius.sma;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by jakew on 10/29/2016.
 */

public class OptionsActivity extends Activity
{

    Spinner delete_rec;
    Spinner delete_send;
    ArrayAdapter<CharSequence> delete_adapter;
    DatabaseHelper mDbHelper;
    SQLiteDatabase db;
    ServerComm server;
    ServiceConnection connector = new ServiceConnection()
    {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder)
        {
            ServerComm.ServerBinder binder = (ServerComm.ServerBinder) iBinder;
            server = binder.getServer();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName)
        {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        delete_rec = (Spinner) findViewById(R.id.spinner);
        delete_send = (Spinner) findViewById(R.id.spinner);
        delete_adapter = ArrayAdapter.createFromResource(this, R.array.deleteOptions, android.R.layout.simple_spinner_item);
        delete_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        delete_send.setAdapter(delete_adapter);
        delete_rec.setAdapter(delete_adapter);



        Intent intent = new Intent(this, ServerComm.class);
        bindService(intent, connector, Context.BIND_AUTO_CREATE);

        Button addContact = (Button) findViewById(R.id.addContactButton);
        //TextView enterUserID = new (TextView) findViewById(R.id.contactAdd);

        addContact.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                View view = (LayoutInflater.from(OptionsActivity.this)).inflate(R.layout.add_contact, null);

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(OptionsActivity.this);
                alertBuilder.setView(view);
                final EditText addContact = (EditText) view.findViewById(R.id.enterUserID);
                alertBuilder.setCancelable(true).setPositiveButton("Add", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String contact = addContact.getText().toString();
                        Log.d("OptionsActivity", "Requesting to add contact " + contact );

                    }
                });
                Dialog dialog = alertBuilder.create();
                dialog.show();

            }


        });

    }

    public void onClickDelete(View v) {
        Intent intent = new Intent(getApplicationContext(), DeleteContactActivity.class);
        startActivity(intent);
    }

    public void onOptionResetPW(View v)
    {
        mDbHelper = new DatabaseHelper(getApplicationContext());
        db = mDbHelper.getWritableDatabase();
        db.delete(DatabaseContract.ContactTable.TABLE_NAME, DatabaseContract.ContactTable.COLUMN_USERID + "  LIKE ? ", new String[]{"USER_PUB"});
        db.delete(DatabaseContract.ContactTable.TABLE_NAME, DatabaseContract.ContactTable.COLUMN_USERID + "  LIKE ? ", new String[]{"USER_PRI"});

        Log.d("Login", "generating intial keys");

        KeyPair keyPair = Crypto.keygen();

        PublicKey pub = keyPair.getPublic();
        PrivateKey pri = keyPair.getPrivate();

        String pubString = Crypto.publicKeyToString(pub);
        String priString = Crypto.privateKeyToString(pri);

        //write keys to local database as strings
        db.close();
        db = mDbHelper.getWritableDatabase();

        ContentValues value = new ContentValues();
        value.put(DatabaseContract.ContactTable.COLUMN_USERID, "USER_PUB");
        value.put(DatabaseContract.ContactTable.COLUMN_KEY, pubString);
        if(db.insert(DatabaseContract.ContactTable.TABLE_NAME, null, value) == -1)
        {
            Log.d("Login", "Trouble inserting into database");
        }


        value = new ContentValues();
        value.put(DatabaseContract.ContactTable.COLUMN_USERID, "USER_PRI");
        value.put(DatabaseContract.ContactTable.COLUMN_KEY, priString);
        if(db.insert(DatabaseContract.ContactTable.TABLE_NAME, null, value) == -1)
        {
            Log.d("Login", "Trouble inserting into database");
        }

        db.close();



        //give public key to server
        server.pushPublicKey(pubString);

        Intent i = new Intent(getApplicationContext(), ResetPasswordActivity.class);
        startActivity(i);



    }

    protected  void onDestroy()
    {
        super.onDestroy();
        unbindService(connector);
    }


    /*

    New code...
     */



}
