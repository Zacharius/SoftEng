package com.example.zacharius.sma;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.app.ListActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import com.example.zacharius.sma.Contact;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ContactDetailActivity extends AppCompatActivity
{
    TextView contactName;
    DatabaseHelper mDbHelper;
    SQLiteDatabase db;
    ArrayList<Message> messages = new ArrayList<>();
    private ServerComm server;
    Contact con;
    long currentSystime = System.currentTimeMillis();

    private ServiceConnection connector = new ServiceConnection()
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

        con = new Contact(ContactID, ContactName, mDbHelper, db);
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
            long timeout = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_TIMEOUT));
            long timerec = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_TIMEREC));
            long timeread = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_TIMEREAD));
            int messageType = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_TYPE));
            long messageID = c.getInt(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_MSGID));
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

    public void onClickSend(){
        EditText sendMessage = (EditText) findViewById(R.id.editMessage);
        String sendmgs = sendMessage.getText().toString();
        server.sendText(con.getID(), 600, sendmgs);
        db = mDbHelper.getWritableDatabase();

        //create a new map of values
        ContentValues values = new ContentValues();
        values.put(DatabaseContract.MessageTable.COLUMN_CONTACT, con.getID());
        values.put(DatabaseContract.MessageTable.COLUMN_CONTENT, sendmgs);

        // msgid needs to be updated to create a unique on for each message
        values.put(DatabaseContract.MessageTable.COLUMN_MSGID, currentSystime);
        values.put(DatabaseContract.MessageTable.COLUMN_TIMEOUT, 600);
        values.put(DatabaseContract.MessageTable.COLUMN_TIMEREAD, currentSystime);
        values.put(DatabaseContract.MessageTable.COLUMN_TIMEREC, currentSystime);
        values.put(DatabaseContract.MessageTable.COLUMN_TYPE, 0); // 0 means sent

        //insert row
        long newRowId = db.insert(DatabaseContract.MessageTable.TABLE_NAME, null, values);

        //adding message to messages arraylist
        Message sentmessage = new Message(sendmgs, con.getID(), currentSystime, currentSystime, 600, currentSystime, 0 );
        messages.add(sentmessage);
        displayMessages();
        sendMessage.setText("");
        db = mDbHelper.getReadableDatabase();

    }

}
