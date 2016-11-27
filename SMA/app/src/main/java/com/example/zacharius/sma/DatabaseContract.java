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
            + ContactTable.COLUMN_KEY + " TEXT)";

    public static final String Create_MessageTable =
            "CREATE TABLE " + MessageTable.TABLE_NAME + " ( "
                    + MessageTable.COLUMN_COMPOSERID + " VARCHAR(10), "
                    + MessageTable.COLUMN_CONTENT + " TEXT, "
                    + MessageTable.COLUMN_TIMEREC + " INT, "
                    + MessageTable.COLUMN_TIMEREAD + " INT)";

    public static final String Delete_Tables =
            "DROP TABLE IF EXISTS " + ContactTable.TABLE_NAME + " , " + MessageTable.TABLE_NAME;


    public static class ContactTable{
        public static final String TABLE_NAME = "Contacts";
        public static final String COLUMN_USERID = "UserID";
        public static final String COLUMN_NICKNAME = "Nickname";
        public static final String COLUMN_KEY = "Key";
    }

    public static class MessageTable{
        public static final String TABLE_NAME = "Message";
        public static final String COLUMN_COMPOSERID = "ComposerID";
        public static final String COLUMN_CONTENT = "Content";
        public static final String COLUMN_TIMEREC = "TimeRec";
        public static final String COLUMN_TIMEREAD = "TimeRead";
    }



}
