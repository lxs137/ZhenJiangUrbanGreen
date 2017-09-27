package com.nju.urbangreen.zhenjiangurbangreen.message;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.nju.urbangreen.zhenjiangurbangreen.R;

import java.util.List;

/**
 * Created by Kyle on 2017/8/17.
 */

public class MessageAdapter extends ArrayAdapter<Message> {
    private int resourceId;
    public MessageAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Message> objects) {
        super(context, resource, objects);
        resourceId=resource;
    }
    public View getView(int position, View convertView, ViewGroup parent){
        Message message=getItem(position);
        String time= message.getQM_CreateTime().substring(0,19).replace("T"," ");
        View view= LayoutInflater.from(getContext()).inflate(resourceId,parent,false);
        TextView sendort=(TextView)view.findViewById(R.id.Sender_Label);
        TextView messaget=(TextView)view.findViewById(R.id.Msg);
        TextView timet=(TextView)view.findViewById(R.id.Time);
        sendort.setText(message.getPFullName_From());
        messaget.setText(message.getQuickMessage());
        //timet.setText(message.getQM_CreateTime());
        timet.setText(time);
        return view;
    }
}
