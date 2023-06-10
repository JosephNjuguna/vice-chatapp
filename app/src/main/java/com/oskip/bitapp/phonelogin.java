package com.oskip.bitapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class phonelogin extends AppCompatActivity {
    /*EditText phonenumber;
    EditText pass;
    SignInButton googlebutton;
    Button buttoncontinue;
    TextView tosignup;
    Button button;
    FirebaseAuth mAuth;
    ProgressDialog mlog;
    DatabaseReference mdata;
    Query firebasesearch;
    ImageView viewpass;
    ImageView nonview;
    private static final String TAG = "TAG";
    GoogleSignInAccount account;
    GoogleApiClient mGoogleApiClient;
    //FirebaseUser mCurrentUser;
    private static final int RC_SIGN_IN = 100;
    String personEmail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_login);
        pass = (EditText) findViewById(R.id.editTextPassword);
        viewpass = (ImageView)findViewById(R.id.imageViewpassview);
        viewpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewpass.setVisibility(View.INVISIBLE);
                nonview.setVisibility(View.VISIBLE);
            }
        });
        nonview = (ImageView)findViewById(R.id.imageViewpasshide);
        nonview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               viewpass.setVisibility(View.VISIBLE);
               nonview.setVisibility(View.INVISIBLE);
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(phonelogin.this, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        googlebutton = (SignInButton) findViewById(R.id.imageView25);
        googlebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googlelogin();
            }
        });
        //----------------phone auth-------------------------------
        phonenumber = (EditText) findViewById(R.id.editText13);
        mlog = new ProgressDialog(this);
        mdata = FirebaseDatabase.getInstance().getReference().child("users");
        //-------------------------------------------------------------------------------------------
        buttoncontinue = (Button) findViewById(R.id.button15);
        buttoncontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String phone = phonenumber.getText().toString();
                final String password = pass.getText().toString();
                if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                    Snackbar.make(v, "Please fill all the fields above to continue", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    final AlertDialog alertdialog = new AlertDialog.Builder(phonelogin.this).create();
                    alertdialog.setMessage("the password you have used you will use it in your next log in.please don`t forget the password so as to avoid any inconveniences");
                    alertdialog.setCancelable(false);
                    alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            verifyphonenumberlogin(phone, password);
                        }
                    });
                    alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, "BACK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            alertdialog.dismiss();
                        }
                    });
                    alertdialog.show();
                }
            }
        });
        tosignup = (TextView) findViewById(R.id.textView39);
        tosignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tosignup = new Intent(phonelogin.this, startuppage.class);
                startActivity(tosignup);
            }
        });
    }
    private void googlelogin() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                if (account != null) {
                    personEmail = account.getEmail();
                }
                firebaseAuthWithGoogle(account);
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                  String deviceToken = FirebaseInstanceId.getInstance().getToken();
                  Map tokenMap = new HashMap<>();
                  tokenMap.put("deviceToken", deviceToken);
                  mdata.updateChildren(tokenMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                      @Override
                      public void onComplete(@NonNull Task<Void>task) {
                          if (task.isSuccessful()) {
                              Intent nextintent = new Intent(phonelogin.this, ProfileActivity.class);
                              nextintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              startActivity(nextintent);
                              finish();
                          } else {
                              final AlertDialog alertdialog = new AlertDialog.Builder(phonelogin.this).create();
                              alertdialog.setMessage("Authentication failed. Please try again.");
                              alertdialog.setIcon(R.mipmap.vicechat);
                              alertdialog.setCancelable(true);
                              alertdialog.show();
                          }
                      }
                  });
                 }else {
                Toast.makeText(phonelogin.this, "Sign in failed.Please Try Again", Toast.LENGTH_SHORT).show();
                updateUI(null);
                }
            }
        });
    }
    private void updateUI(Object o) {
    }
    private void verifyphonenumberlogin(String phone, final String password) {
        firebasesearch = mdata.orderByChild("phone number").startAt(phone).endAt(phone + "\uf8ff");
        firebasesearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map userMap = new HashMap<>();
                userMap.put("password",password);
                mdata.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void>task) {
                        if (task.isSuccessful()) {
                            Intent nextintent = new Intent(phonelogin.this, ProfileActivity.class);
                            nextintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(nextintent);
                            finish();
                        } else {
                            final AlertDialog alertdialog = new AlertDialog.Builder(getApplicationContext()).create();
                            alertdialog.setMessage("Please check your phone number!!! and try again.");
                            alertdialog.setIcon(R.mipmap.vicechat);
                            alertdialog.setCancelable(true);
                            alertdialog.show();
                        }
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    } */
}