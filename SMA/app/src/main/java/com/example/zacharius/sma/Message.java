package com.example.zacharius.sma;

import org.json.*;

import java.util.ArrayList;

/**
 * Created by jakew on 11/21/2016.
 */

public class Message {


    private String content;
    private int time_rec;
    private int time_opened;
    private String contact;
    private int msgID;
    private int msgType;
    private int timeout;

    public Message(String content, String contact, int timeRec,int time_opened, int timeout, int msgID, int msgType ){
        this.content = content;
        this.contact = contact;
        this.time_rec = timeRec;
        this.msgID = msgID;
        this.msgType = msgType;
        this.timeout = timeout;
        this.time_opened = time_opened;
    }

    public String getContent(){
        return content;
    }

    public String getContact(){
        return contact;
    }

    public int getTime_rec(){
        return time_rec;
    }

    public int getTime_opened(){
        return time_opened;
    }

    public int getTimeout(){ return timeout; }

    public int getMsgID() {return msgID; }

    public int getMsgType() { return msgType; }
}