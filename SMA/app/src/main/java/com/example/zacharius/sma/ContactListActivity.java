package com.example.zacharius.sma;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class ContactListActivity extends AppCompatActivity {
    DatabaseHelper mDbHelper;
    SQLiteDatabase db;
   // SQLiteDatabase dbw;
    ListView contactsView;
    ArrayAdapter<String> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        Log.d("ContactlistActivity", "Activity starting");

        //get context of the database and create/open for reading.
        mDbHelper = new DatabaseHelper(getApplicationContext());

        db = mDbHelper.getReadableDatabase();
        // define a projection that specifies which columns you
        // actually use after this query
        String[] projection = {

                DatabaseContract.ContactTable.COLUMN_USERID,
                DatabaseContract.ContactTable.COLUMN_NICKNAME,


        };


        String selection = DatabaseContract.ContactTable.COLUMN_STATUS + "= ?";
        String[] selectionArgs = { "0" };

        // query the data base for contacts
        Cursor c = db.query(
                DatabaseContract.ContactTable.TABLE_NAME,           // the table to query
                projection,                                         // the columns to return
                selection,                                               // the columns for the WHERE clause
                selectionArgs,                                               // the values for the WHERE clause
                null,                                               // grouping of the rows
                null,                                               // filter by row
                null                                                // sort order
                );


        //grab Contacts ListView
        contactsView = (ListView) findViewById(R.id.contacts);

        // add contacts to an array list
        int numberOfContacts = c.getCount();
        final ArrayList<String> contactList =  new ArrayList<String>();
        c.moveToFirst();
        for(int i = 0; i < numberOfContacts; i++){
            String nextContact = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactTable.COLUMN_NICKNAME));
            if(nextContact == null)
            {
                Log.d("ContactListActivity", "contact " + c.getString(c.getColumnIndex(DatabaseContract.ContactTable.COLUMN_USERID))+ " has no nickname, excluding from listview");
            }
            else{
                contactList.add(nextContact);
            }
            c.moveToNext();

        }


        c.close();


        //sample contancts
        /*final ArrayList<String> contactList = new ArrayList<String>();
        contactList.add("Zach");
        contactList.add("Elijah");
        contactList.add("Brad");
        contactList.add("Jake");
        contactList.add("Nick");
        contactList.add("Carlos");
        contactList.add("Enrique");
        contactList.add("Peju");
        contactList.add("Alex");
        contactList.add("Connor"); */


        //bind contactList to list, an ArrayAdapter
        list = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_list_item, contactList);


        //bind list(arrayadapter) to contactView
        contactsView.setAdapter(list);



        //determine what will happen when a user presses an item in the list
        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), ContactDetailActivity.class);
                intent.putExtra("NAME", contactList.get(position));
                startActivity(intent);

            }
        });

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        //get context of the database and create/open for reading.
        mDbHelper = new DatabaseHelper(getApplicationContext());

        db = mDbHelper.getReadableDatabase();
        // define a projection that specifies which columns you
        // actually use after this query
        String[] projection = {

                DatabaseContract.ContactTable.COLUMN_USERID,
                DatabaseContract.ContactTable.COLUMN_NICKNAME,

        };

        String selection = DatabaseContract.ContactTable.COLUMN_STATUS + "= ?";
        String[] selectionArgs = { "0" };

        // query the data base for contacts
        Cursor c = db.query(
                DatabaseContract.ContactTable.TABLE_NAME,           // the table to query
                projection,                                         // the columns to return
                selection,                                               // the columns for the WHERE clause
                selectionArgs,                                               // the values for the WHERE clause
                null,                                               // grouping of the rows
                null,                                               // filter by row
                null                                                // sort order
        );


        //grab Contacts ListView
        contactsView = (ListView) findViewById(R.id.contacts);

        // add contacts to an array list
        int numberOfContacts = c.getCount();
        final ArrayList<String> contactList =  new ArrayList<String>();
        c.moveToFirst();
        for(int i = 0; i < numberOfContacts; i++){
            String nextContact = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactTable.COLUMN_NICKNAME));
            if(nextContact == null)
            {
                Log.d("ContactListActivity", "contact " + c.getString(c.getColumnIndex(DatabaseContract.ContactTable.COLUMN_USERID))+ " has no nickname, excluding from listview");
            }
            else{

                contactList.add(nextContact);
            }
            c.moveToNext();

        }


        c.close();


        //bind contactList to list, an ArrayAdapter
        list = new ArrayAdapter<>(getApplicationContext(), R.layout.simple_list_item, contactList);


        //bind list(arrayadapter) to contactView
        contactsView.setAdapter(list);

        contactsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id)
            {
                Intent intent = new Intent(getApplicationContext(), ContactDetailActivity.class);
                intent.putExtra("NAME", contactList.get(position));
                startActivity(intent);

            }
        });
    }

    /*
        Options button
         */
    public void onClickOptions(View v)
    {
        Intent intent = new Intent(getApplicationContext(), OptionsActivity.class);
        startActivity(intent);
    }

    public void onClickPendingContacts(View v)
    {
        Intent intent = new Intent(this, PendingContactActivity.class);
        startActivity(intent);
    }

}
