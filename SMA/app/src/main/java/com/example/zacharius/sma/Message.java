package com.example.zacharius.sma;

import org.json.*;

import java.util.ArrayList;

/**
 * Created by jakew on 11/21/2016.
 */

public class Message {


    private String content;
    private long time_rec;
    private long time_opened;
    private String contact;
    private long msgID;
    private int msgType;
    private long timeout;

    public Message(String content, String contact, long timeRec, long time_opened, long timeout, long msgID, int msgType ){
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

    public long getTime_rec(){
        return time_rec;
    }

    public long getTime_opened(){
        return time_opened;
    }

    public long getTimeout(){ return timeout; }

    public long getMsgID() {return msgID; }

    public int getMsgType() { return msgType; }
}