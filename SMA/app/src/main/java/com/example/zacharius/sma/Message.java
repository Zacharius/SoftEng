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
    private String composserID;

    public Message(String cont, String compID, int timeRec ){
        this.content = cont;
        this.composserID = compID;
        this.time_rec = timeRec;

    }

    public String getContent(){
        return content;
    }

    public String getComposser(){
        return composserID;
    }

    public int getTime_rec(){
        return time_rec;
    }

    public int getTime_opened(){
        return time_opened;
    }

    public void setTime_opened(){
        this.time_opened = time_rec;
    }


}