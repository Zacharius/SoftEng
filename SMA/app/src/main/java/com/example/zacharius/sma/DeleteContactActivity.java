package com.example.zacharius.sma;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Brad on 11/27/2016.
 */

public class DeleteContactActivity extends AppCompatActivity {

    ListView contactsView;
    DatabaseHelper mDbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_contact);

        //get context of the database and create/open for reading.
        mDbHelper = new DatabaseHelper(getApplicationContext());
        db = mDbHelper.getReadableDatabase();
    /*    db = mDbHelper.getWritableDatabase();
        // setup database
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.ContactTable.COLUMN_USERID, "zack");
        values.put(DatabaseContract.ContactTable.COLUMN_NICKNAME, "zack");
       // values.put(DatabaseContract.ContactTable.COLUMN_KEY, "fyc");

        //insert the new Row, returning the primary key value of the new row
        long newRowId = db.insert(DatabaseContract.ContactTable.TABLE_NAME, null, values);
        db = mDbHelper.getReadableDatabase();*/
        // define a projection that specifies which columns you
        // actually use after this query
        String[] projection = {

                DatabaseContract.ContactTable.COLUMN_USERID,
                DatabaseContract.ContactTable.COLUMN_NICKNAME,

        };

        // query the data base for contacts
        Cursor c = db.query(
                DatabaseContract.ContactTable.TABLE_NAME,           // the table to query
                projection,                                         // the columns to return
                null,                                               // the columns for the WHERE clause
                null,                                               // the values for the WHERE clause
                null,                                               // grouping of the rows
                null,                                               // filter by row
                null                                                // sort order
        );


        //grab Contacts ListView
        contactsView = (ListView) findViewById(R.id.contactDelete);

        // add contacts to an array list
        int numberOfContacts = c.getCount();
        final ArrayList<String> contactList = new ArrayList<String>();
        c.moveToFirst();
        for (int i = 0; i < numberOfContacts; i++) {
            String nextContact = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactTable.COLUMN_NICKNAME));
            c.moveToNext();
            contactList.add(nextContact);
        }
        c.close();

       ArrayAdapter<String> list = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, contactList);
        contactsView.setAdapter(list);
    }
}
