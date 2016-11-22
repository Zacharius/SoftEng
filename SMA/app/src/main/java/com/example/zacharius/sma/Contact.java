package com.example.zacharius.sma;

import java.util.ArrayList;

/**
 * Created by jakew on 11/21/2016.
 */

public class Contact {

    private ArrayList message = new ArrayList();
    private String contactID;
    private String nickName;

    public Contact(String id, String name){
        this.contactID = id;
        this.nickName = name;
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
        message.add("now");
        message.add("this moment is set");
        message.add("sleep insane");
        message.add("dream on the inside, dream on my own");
        message.add("once escaped star filled road");
        message.add("my head will not rest on this pillow");
        }

    }
