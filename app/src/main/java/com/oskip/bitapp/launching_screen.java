package com.oskip.bitapp;

import android.*;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class launching_screen extends AppCompatActivity {
    private static final String TAG = "signed out";
    Button signup;
    Button login;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launching_screen);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        } else {
            startActivity(new Intent(this, startuppage.class));
            finish();
        }
        signup = findViewById(R.id.buttonTosignup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupintent = new Intent(launching_screen.this, startuppage.class);
                startActivity(signupintent);
            }
        });
        login = findViewById(R.id.buttontologin);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginintent = new Intent(launching_screen.this, phonelogin.class);
                startActivity(loginintent);
            }
        });
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isOnline()) {
            Toast.makeText(launching_screen.this, "Good Connection", Toast.LENGTH_LONG).show();
            Intent i2 = new Intent(launching_screen.this, ProfileActivity.class);
            i2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i2);
        } else {
            Toast.makeText(launching_screen.this, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
}