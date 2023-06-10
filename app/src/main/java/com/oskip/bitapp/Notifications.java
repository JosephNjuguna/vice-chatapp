package com.oskip.bitapp;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Notifications extends AppCompatActivity {
    RecyclerView notifications;
    FirebaseAuth mAuth;
    FirebaseUser user;
    TextView netconnectiontext;
    private LinearLayoutManager linearLayoutManager;
    private DatabaseReference messagenotification;
    String uid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        netconnectiontext = findViewById(R.id.inet);
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
             uid = user.getUid();
        }
        messagenotification = FirebaseDatabase.getInstance().getReference().child("notification");
        notifications = findViewById(R.id.notificationwall);
        linearLayoutManager = new LinearLayoutManager(Notifications.this);
        linearLayoutManager.setReverseLayout(true);
        notifications.setLayoutManager(linearLayoutManager);
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (isOnline()) {//
            netconnectiontext.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Good Connection", Toast.LENGTH_LONG).show();
            FirebaseRecyclerAdapter<Notificatio,NotificationHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Notificatio,NotificationHolder>(
                    Notificatio.class,
                    R.layout.notification_layout,
                    NotificationHolder.class,
                    messagenotification
            ) {
                @Override
                protected void populateViewHolder(final NotificationHolder viewHolder, Notificatio model, int position) {
                    final String post_key = getRef(position).getKey();
                    viewHolder.settype(model.getType());
                }
            };
            notifications.scrollToPosition(firebaseRecyclerAdapter.getItemCount() -1);
            notifications.setAdapter(firebaseRecyclerAdapter);
        }else {
            netconnectiontext.setVisibility(View.VISIBLE);
            notifications.setBackgroundColor(Color.BLUE);
            Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
    public static class NotificationHolder extends RecyclerView.ViewHolder {
        View mitemView;
        public NotificationHolder(final View itemView) {
            super(itemView);
            mitemView = itemView;
        }
        public void settype(String type){
            TextView notitype = mitemView.findViewById(R.id.textView35);
            notitype.setText(type);
        }
    }
}