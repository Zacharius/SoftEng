package com.example.zacharius.sma;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jakew on 12/7/2016.
 */

public class messageAdapter extends BaseAdapter {
    ArrayList<Message> messList;
    Context context;
    LayoutInflater inflaters;

    public messageAdapter(Context context, ArrayList<Message> messList){
        super();
        this.messList = messList;
        this.context = context;
        this.inflaters = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

    }

    @Override
    public int getCount(){
        return messList.size();
    }

    @Override
    public Message getItem(int position){
        return messList.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount(){
        return 2;
    }

    @Override
    public int getItemViewType(int position){
        Message message = messList.get(position);
        /*// 0 = sent
        if(message.getMsgType() == 1){
            return 0;
        // 1 = received
        }else if(message.getMsgType() == 0){
            return 1;
        }*/

        if(message.getMsgType() == 1 || message.getMsgType() == 0)
        {
            return message.getMsgType();
        }
        else
            return -1;
    }

    @Override
    public View getView(int position, View counterView, ViewGroup parent){
        ViewHolder holder = new ViewHolder();
        View row = inflaters.inflate(R.layout.customlistview, parent, false);
        holder.sent = (TextView) row.findViewById(R.id.sent);
        holder.received = (TextView) row.findViewById(R.id.received);

        if(getItemViewType(position)==0){
            holder.sent.setText(messList.get(position).getContent());
            holder.sent.setBackgroundColor(Color.parseColor("#3366ff"));
            holder.sent.setGravity(Gravity.LEFT);
        }

        else if(getItemViewType(position)==1){
            holder.received.setText(messList.get(position).getContent());
            holder.received.setBackgroundColor(Color.parseColor("#00ff00"));
            holder.received.setGravity(Gravity.RIGHT);
        }
        return row;

    }
    class ViewHolder{
        TextView sent, received;
        public ViewHolder(){

        }
    }
}


