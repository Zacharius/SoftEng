package com.example.zacharius.sma;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.example.zacharius.sma.Contact;

import java.util.ArrayList;


public class ContactDetailActivity extends AppCompatActivity
{
    TextView contactName;
    DatabaseHelper mDbHelper;
    SQLiteDatabase db;
    ArrayList<Message> messages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_detail);

        Intent intent = getIntent();
        String name = intent.getStringExtra("NAME");

        //Contact con = new Contact("this is the id", name);
        contactName = (TextView) findViewById(R.id.contactName);
        contactName.setText(name);

        mDbHelper = new DatabaseHelper(getApplicationContext());
        db = mDbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseContract.ContactTable.COLUMN_USERID,
                DatabaseContract.ContactTable.COLUMN_NICKNAME
        };

        String selection = DatabaseContract.ContactTable.COLUMN_NICKNAME + " = ? ";
        String[] selectionArgs = {name};// need to add user id or name

        Cursor c = db.query(
                DatabaseContract.ContactTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        c.moveToFirst();
        String ContactID = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactTable.COLUMN_USERID));
        String ContactName = c.getString(c.getColumnIndexOrThrow(DatabaseContract.ContactTable.COLUMN_NICKNAME));
        c.close();

        Contact con = new Contact(ContactID, ContactName, mDbHelper, db);
        messages = createMessageList(con);
        displayMessages();

       /* ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, con.getMessage());
        ListView LV = (ListView)findViewById(R.id.contactMessages);
        LV.setAdapter(adapter);*/


    }

    public ArrayList<Message> createMessageList(Contact con){

        ArrayList<Message> message = new ArrayList<>();
        String[] projection = {
                DatabaseContract.MessageTable.COLUMN_CONTACT,
                DatabaseContract.MessageTable.COLUMN_TIMEOUT,
                DatabaseContract.MessageTable.COLUMN_CONTENT,
                DatabaseContract.MessageTable.COLUMN_TYPE,
                DatabaseContract.MessageTable.COLUMN_TIMEREC,
                DatabaseContract.MessageTable.COLUMN_TIMEREAD,
                DatabaseContract.MessageTable.COLUMN_MSGID

        };

        String selection = DatabaseContract.MessageTable.COLUMN_CONTACT + "= ?";
        String[] selectionArgs = { con.getID() };

        String sortOrder =
                DatabaseContract.MessageTable.COLUMN_TIMEREC + " DESC";

        Cursor c = db.query(
                DatabaseContract.MessageTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        c.moveToFirst();
        int num_message = c.getCount();
        for(int i = 0; i< num_message; i++) {
            String mess = c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_CONTENT));
            String sendID = c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_CONTACT));
            int timeout = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_TIMEOUT));
            int timerec = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_TIMEREC));
            int timeread = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_TIMEREAD));
            int messageType = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_TYPE));
            int messageID = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_MSGID));
            Message info = new Message(mess, sendID, timerec, timeread, timeout,messageID, messageType );
            message.add(info);
        }

        return message;
    }

    public void displayMessages(){
        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, messages);
        messageAdapter adapter = new messageAdapter(this, messages);
        ListView LV = (ListView)findViewById(R.id.contactMessages);
        LV.setAdapter(adapter);
    }

}
