package com.example.zacharius.sma;

import java.security.PrivateKey;

/**
 * Created by zacharius on 11/21/16.
 */
public class DatabaseContract
{
    private DatabaseContract() {}
    public static final String Create_ContactTable =
            "CREATE TABLE " + ContactTable.TABLE_NAME + " ( "
            + ContactTable.COLUMN_USERID + " VARCHAR(10), "
            + ContactTable.COLUMN_NICKNAME + " VARCHAR(20), "
            + ContactTable.COLUMN_STATUS +  " INT, "
            + ContactTable.COLUMN_KEY + " TEXT)";

    public static final String Create_MessageTable =
            "CREATE TABLE " + MessageTable.TABLE_NAME + " ( "
                    + MessageTable.COLUMN_SENDERID + " VARCHAR(10), "
                    + MessageTable.COLUMN_CONTENT + " TEXT, "
                    + MessageTable.COLUMN_TIMEREC + " INT, "
                    + MessageTable.COLUMN_TIMEOUT + " INT, "
                    + MessageTable.COLUMN_TIMEREAD + " INT)";

    public static final String Delete_Tables =
            "DROP TABLE IF EXISTS " + ContactTable.TABLE_NAME + " , " + MessageTable.TABLE_NAME;


    public static class ContactTable{
        public static final String TABLE_NAME = "Contacts";
        public static final String COLUMN_USERID = "UserID";
        public static final String COLUMN_NICKNAME = "Nickname";
        public static final String COLUMN_KEY = "Key";
        /*Status Values:
            0 : Accepted Contact
            1: contact you are awating response from
            2: contact waiting for response from you
         */
        public static final String COLUMN_STATUS = "STATUS";
    }

    public static class MessageTable{
        public static final String TABLE_NAME = "Message";
        public static final String COLUMN_SENDERID = "SenderID";
        public static final String COLUMN_CONTENT = "Content";
        public static final String COLUMN_TIMEREC = "TimeRec";
        public static final String COLUMN_TIMEREAD = "TimeRead";
        public static final String COLUMN_TIMEOUT = "TimeOut";//time message is allowed to exist after it has been read
    }



}
