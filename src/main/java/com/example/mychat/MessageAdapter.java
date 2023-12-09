package com.example.mychat;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


public class MessageAdapter extends ArrayAdapter {
    public MessageAdapter(Context context, int resource, List<MyChatMessage> messages) {
        super(context, resource, messages);
    }
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.message_item, parent, false);
        ImageView photoImageMessage = convertView.findViewById(R.id.photoImageMessage);
        TextView textTextMessage = convertView.findViewById(R.id.textTextMessage);
        TextView textTextName = convertView.findViewById(R.id.textTextName);
        MyChatMessage message = (MyChatMessage) getItem(position);
        boolean isText = message.GetImageUrl() == null;
        textTextName.setText(message.name);
        if(isText){
            textTextMessage.setVisibility(View.VISIBLE);
            photoImageMessage.setVisibility(View.GONE);
            textTextMessage.setText(message.GetText());
        }else{
            textTextMessage.setVisibility(View.GONE);
            photoImageMessage.setVisibility(View.VISIBLE);
            Glide.with(photoImageMessage.getContext()).load(message.GetImageUrl()).into(photoImageMessage);
        }
        return convertView;
    }
}
