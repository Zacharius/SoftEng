package com.example.zacharius.sma;

import java.util.ArrayList;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


/**
 * Created by jakew on 11/21/2016.
 */

public class Contact {

    private ArrayList message = new ArrayList();
    private String contactID;
    private String nickName;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase db;

    public Contact(String id, String name, DatabaseHelper dbhelp, SQLiteDatabase dataB){
        this.contactID = id;
        this.nickName = name;
        this.mDbHelper = dbhelp;
        this.db = dataB;
        setMessage(); // only here for a test, should be removed or changed.
    }

    public String getName(){
        return nickName;
    }

    public String getID(){
        return contactID;
    }

    public ArrayList getMessage(){
        return message;
    }

    public void setMessage(){
        String[] projection = {
                DatabaseContract.MessageTable.COLUMN_SENDERID,
                DatabaseContract.MessageTable.COLUMN_CONTENT
        };

        String selection = DatabaseContract.MessageTable.COLUMN_SENDERID + "= ?";
        String[] selectionArgs = { contactID };

        Cursor c = db.query(
                DatabaseContract.MessageTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        c.moveToFirst();
        int num_message = c.getCount();
        for(int i = 0; i< num_message; i++){
            String mess = c.getString(c.getColumnIndexOrThrow(DatabaseContract.MessageTable.COLUMN_CONTENT));
            message.add(mess);
        }
    }

}
