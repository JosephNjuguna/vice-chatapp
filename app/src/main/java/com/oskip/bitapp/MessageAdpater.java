package com.oskip.bitapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

/**
 * Created by jose on 11/9/17.
 */

public class MessageAdpater extends FirebaseListAdapter<Messages> {
    List<Messages> mMessagelist = new ArrayList<Messages>();
    FirebaseAuth mAuth;
    DatabaseReference musers ;
    String current_user_id;
    String  from_user;
    View view;
    private Chat chat;
    public MessageAdpater(Chat chat, Class<Messages> modelClass, int modelLayout, DatabaseReference ref) {
        super(chat, modelClass, modelLayout, ref);
        this.chat= chat;
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    protected void populateView(View v, Messages model, int position) {
       TextView receivetime = v.findViewById(R.id.time_message_received);
       TextView send = (EmojiconTextView)v.findViewById(R.id.real_message_received);
       send.setText(model.getMessages());
       receivetime.setText(model.getTimestamp());
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        current_user_id = mAuth.getCurrentUser().getUid();
        Messages messagechat = getItem(position);
        from_user = messagechat.getFrom();
        musers = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);
        if (from_user.equals(current_user_id)) {
            convertView = chat.getLayoutInflater().inflate(R.layout.message_layout_send, parent, false);
        } else {
            convertView = chat.getLayoutInflater().inflate(R.layout.message_layout_receive, parent, false);
        }
        populateView(convertView, messagechat, position);
        return convertView;
    }
    /*if (message_type.equals("text")) {
            if (from_user.equals(current_user_id)){
                holder.send.setText(chat.getMessages());
                holder.receivetime.setText(chat.getTimestamp());
            }else {
                holder.send.setText(chat.getMessages());
                holder.receivetime.setText(chat.getTimestamp());
            }
        }
        */

    /*public MessageAdpater(Context context){
        this.context = context;
    }
    @Override
    public int getCount() {
        return mMessagelist.size();
    }
    @Override
    public Object getItem(int position) {
        return mMessagelist.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageHolder  holder = new MessageHolder();
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        current_user_id = mAuth.getCurrentUser().getUid();
        Messages chat = mMessagelist.get(position);
        from_user = chat.getFrom();
        String message_type = chat.getType();
        musers = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);

        if (from_user.equals(current_user_id)){
            convertView= inflater.inflate(R.layout.message_layout_send,null);
            convertView.setTag(holder);
            holder.receivetime = (TextView)convertView.findViewById(R.id.time_message_received);
            holder.send = (TextView)convertView.findViewById(R.id.real_message_received);

            holder.receivetime.setTextColor(Color.BLACK);
            //holder.send.setBackgroundResource(R.drawable.chat_background_all);
           // holder.send.setTextColor(Color.WHITE);
        }else{
            convertView= inflater.inflate(R.layout.message_layout_receive,null);
            convertView.setTag(holder);
            holder.receivetime = (TextView)convertView.findViewById(R.id.time_message_received2);
            holder.send = (TextView)convertView.findViewById(R.id.real_message_received2);

            holder.receivetime.setTextColor(Color.BLACK);
            //holder.send.setBackgroundResource(R.drawable.chat_background_all2);
           // holder.send.setTextColor(Color.BLACK);
        }
        if (message_type.equals("textwhite")) {
            if (from_user.equals(current_user_id)){
                holder.send.setText(chat.getMessages());
                holder.receivetime.setText(chat.getTimestamp());
            }else {
                holder.send.setText(chat.getMessages());
                holder.receivetime.setText(chat.getTimestamp());
            }
        }
        return convertView;
    }
    class MessageHolder{
        public TextView send;
       public  TextView receivetime;
        }
    /*public MessageAdpater(List<Messages> mMessagelist,Context context) {
        this.mMessagelist = mMessagelist;
        this.context = context;
        mAuth = FirebaseAuth.getInstance();
    }
    @Override
    public MessageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        MessageHolder viewHolder = null;
        LayoutInflater messageinflater =(LayoutInflater)context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        viewHolder = new MessageHolder(messageinflater, mMessagelist);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final MessageHolder holder, int position) {
        current_user_id = mAuth.getCurrentUser().getUid();
        Messages chat = mMessagelist.get(position);
        from_user = chat.getFrom();
        String message_type = chat.getType();
        musers = FirebaseDatabase.getInstance().getReference().child("users").child(from_user);
        if (from_user.equals(current_user_id)){
            holder.receivetime.setTextColor(Color.BLACK);
            holder.send.setBackgroundResource(R.drawable.chat_background_all);
            holder.send.setTextColor(Color.WHITE);
        }else{
            holder.receivetime.setTextColor(Color.BLACK);
            holder.send.setBackgroundResource(R.drawable.chat_background_all2);
            holder.send.setTextColor(Color.BLACK);
        }
        if (message_type.equals("textwhite")) {
            if (from_user.equals(current_user_id)){
                holder.send.setText(chat.getMessages());
                holder.receivetime.setText(chat.getTimestamp());
            }else {
                holder.send.setText(chat.getMessages());
                holder.receivetime.setText(chat.getTimestamp());
            }
        }
    }
    @Override
    public int getItemCount() {
        return mMessagelist.size();
    }*/
}