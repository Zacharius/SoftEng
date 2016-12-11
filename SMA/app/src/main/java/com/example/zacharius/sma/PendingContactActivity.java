package com.example.zacharius.sma;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PendingContactActivity extends Activity
{
    DatabaseHelper helper;
    SQLiteDatabase db;
    ArrayAdapter contactAdapter;

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_pending_contact);

        helper = new DatabaseHelper(this);
        db = helper.getReadableDatabase();

        Intent intent = new Intent(this, ServerComm.class);
        bindService(intent, connector, Context.BIND_AUTO_CREATE);

        //query database for all contacts that we are waiting on a response from
        String[] projection = {
                DatabaseContract.ContactTable.COLUMN_USERID
        };

        String selection = DatabaseContract.ContactTable.COLUMN_STATUS + " = ?";
        String[] selectionArgs = { "1" };

        Cursor c = db.query(
                DatabaseContract.ContactTable.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        ArrayList<String> requestedContacts = new ArrayList<>();



        //if query returns something, add the userid of all contacts to an arraylist and put it in listview
        if(c.moveToFirst())
        {
            for(int i=0; i<c.getCount(); i++)
            {
                c.move(i);
                requestedContacts.add(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactTable.COLUMN_USERID)));
            }
            ListView requestedContactsList = (ListView) findViewById(R.id.requestedContacts);
            contactAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, requestedContacts);
            requestedContactsList.setAdapter(contactAdapter);
        }
        else//else change textview to notify user we aren't waiting on anybody
        {
            TextView requestedText = (TextView) findViewById(R.id.requestedText);
            requestedText.setText("No requested contacts awaiting approval");
        }

        //query database for all contacts we need to respond to
         projection = new String[]{
                DatabaseContract.ContactTable.COLUMN_USERID
        };

        selection = DatabaseContract.ContactTable.COLUMN_STATUS + " = ?";
        selectionArgs = new String[] { "2" };

        c = db.query(
                DatabaseContract.ContactTable.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        final ArrayList<String> pendingContacts = new ArrayList<>();



        //if query returns something, add the userid of all contacts to an arraylist and put it in listview
        if(c.moveToFirst())
        {
            for(int i=0; i<c.getCount(); i++)
            {
                c.move(i);
                pendingContacts.add(c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactTable.COLUMN_USERID)));
            }
            final ListView pendingContactsList = (ListView) findViewById(R.id.pendingContacts);
            contactAdapter = new ArrayAdapter<>(this, R.layout.simple_list_item, pendingContacts);
            pendingContactsList.setAdapter(contactAdapter);


            pendingContactsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                public void onItemClick(AdapterView<?> adapterView, View view, final int position, long id) {

                    View popupView = (LayoutInflater.from(PendingContactActivity.this)).inflate(R.layout.approve_contact, null);

                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(PendingContactActivity.this);
                    //alertBuilder.setView(popupView);
                    alertBuilder.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            server.contactResponse(pendingContacts.get(position), true);
                        }
                    });
                    alertBuilder.setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            server.contactResponse(pendingContacts.get(position), false);
                        }
                    });


                    Dialog dialog = alertBuilder.create();
                    dialog.show();

                }


            });
        }
        else//else change textview to notify user we aren't waiting on anybody
        {
            TextView pendingText = (TextView) findViewById(R.id.pendingText);
            pendingText.setText("No new contacts to approve");
        }


        db.close();


    }



    /*class ContactAdapter extends ArrayAdapter
    {
        ArrayList<String> contacts;

        public ContactAdapter(ArrayList contacts)
        {
            super(PendingContactActivity.this, R.layout.approve_contact, contacts);
            this.contacts = contacts;
        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View row= super.getView(position, convertView, parent);

            TextView contactView =(TextView)row.findViewById(R.id.contactName);
            contactView.setText(contacts.get(position));
            return(row);

        }
    }*/

    protected  void onDestroy()
    {
        super.onDestroy();
        unbindService(connector);
    }




}
