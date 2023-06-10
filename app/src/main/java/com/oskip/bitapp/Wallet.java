package com.oskip.bitapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Wallet extends AppCompatActivity {
    Button balance;
    Button sendcrypto;
    Button paycrypto;
    Button sellcrypto;
    public DatabaseReference mDatabase;
    Query mQuery;
    DatabaseReference mCoin;
    FirebaseUser mUser;
    FirebaseAuth firebaseAuth;
    TextView privatecoin;
    TextView privatecoin2;

    String coinedd;
    String likecoinedd;
    String coinuser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wallet);
        firebaseAuth = FirebaseAuth.getInstance();
        privatecoin2 = findViewById(R.id.textView10likecoin);
        privatecoin = findViewById(R.id.textView10coin);
        coinuser = firebaseAuth.getCurrentUser().getUid();
        mCoin = FirebaseDatabase.getInstance().getReference().child("coin").child(coinuser);
        mCoin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
              if (dataSnapshot.hasChild("coins_count")){
                  coinedd = dataSnapshot.child("coins_count").getValue().toString();
                  privatecoin.setText(coinedd);
              }else {
              }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        mQuery  =  mDatabase.orderByChild("id").equalTo(coinuser);
        mQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("coin_count")){
                    likecoinedd = dataSnapshot.child("coin_count").getValue().toString();
                    privatecoin2.setText(likecoinedd);
                }else {
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss (dd/MM/yyyy)");
        Date date = new Date();
        final String strdate = dateFormat.format(date).toString();
        balance = findViewById(R.id.balancebutton);
        balance.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(Wallet.this);
                        builder.setTitle("Your current cryptocurrency balance is :");
                        builder.setMessage(coinedd+": at  "+ strdate + "Other balance include : "+ likecoinedd  +"  Continue using ViceChat and Invite more friendschatpage so you can have fun and mine more cryptocurrency");
                        builder.show();
                    }
                });
        sendcrypto = findViewById(R.id.sendcoin);
        sendcrypto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "please mine more cryptocoins", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        paycrypto = findViewById(R.id.pay);
        paycrypto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "please mine more cryptocoins", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        sellcrypto = findViewById(R.id.sellcoins);
        sellcrypto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "to be implemented in next update", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }
}