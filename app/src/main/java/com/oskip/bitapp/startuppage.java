package com.oskip.bitapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class startuppage extends AppCompatActivity {
    private static final String TAG = "google auth";
    private DatabaseReference mdata;
    private StorageReference mstore;
    private FirebaseUser mCurrentUser;
    FirebaseAuth mAuth;
    ImageView phone;
    SignInButton googlebutton;
    GoogleApiClient mGoogleApiClient;
    private ProgressDialog googleProgressDialog;
    private static final int RC_SIGN_IN = 100;
    String personName;
    String personEmail;
    String personId;
    Uri personPhoto;
    GoogleSignInAccount account;
    TextView logintext;
    RelativeLayout startlayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.startuppage);
        mAuth = FirebaseAuth.getInstance();
        startlayout= findViewById(R.id.re);
        startlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneintent = new Intent(startuppage.this,Phonesignup.class);
                startActivity(phoneintent);
            }
        });
        /*logintext = (TextView)findViewById(R.id.textView81);
        logintext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent log = new Intent(startuppage.this,phonelogin.class);
                startActivity(log);
            }
        });*/
        googlebutton = findViewById(R.id.imageView16);
        phone = findViewById(R.id.imageView18);
        /*phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent phoneintent = new Intent(startuppage.this,Phonesignup.class);
                startActivity(phoneintent);
            }
        });*/
        googleProgressDialog = new ProgressDialog(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).
                requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(startuppage.this, "Try Again", Toast.LENGTH_SHORT).show();
                    }
                }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();
        googlebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                googlesignIn();
            }
        });
    }
    private void googlesignIn() {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                account = result.getSignInAccount();
                if (account != null) {
                    personName = account.getDisplayName();
                    personEmail = account.getEmail();
                    personId = account.getId();
                    personPhoto = account.getPhotoUrl();
                }
                firebaseAuthWithGoogle(account);
            } else {
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        googleProgressDialog.setMessage("signing in");
        googleProgressDialog.show();
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    //Sign in success, update UI with the signed-in user's information
                    Toast.makeText(startuppage.this, "welcome", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "signInWithCredential:success");
                    //--------------------------------------------
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss ");
                    Date date = new Date();
                    final String strdate = dateFormat.format(date).toString();
                    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
                    final String uid = mCurrentUser.getUid();
                    mdata = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();
                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", personName);
                    userMap.put("thumbimage", personPhoto.toString());
                    userMap.put("email", personEmail);
                    userMap.put("uId", personId);
                    userMap.put("started",strdate);
                    userMap.put("deviceToken", deviceToken);
                    mdata.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            googleProgressDialog.dismiss();
                            LayoutInflater inflater3 = getLayoutInflater();
                            final View view = inflater3.inflate(R.layout.refercode,null);
                            final AlertDialog alertdialog = new AlertDialog.Builder(startuppage.this).create();
                            alertdialog.setMessage("Do you have a refer code??Enter it below....if you don`t have proceed since its optional");
                            alertdialog.setIcon(R.mipmap.smallcon);
                            alertdialog.setCancelable(false);
                            final EditText code = view.findViewById(R.id.textViewCode);
                            alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, "proceed", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    String recode = code.getText().toString();
                                    code.setText("");
                                    FirebaseUser usercoderef = FirebaseAuth.getInstance().getCurrentUser();
                                    final String uid = usercoderef.getUid();
                                    DatabaseReference coderef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                                    Map updatehashmap = new HashMap();
                                    updatehashmap.put("refercode",recode);
                                    coderef.updateChildren(updatehashmap);
                                    Intent googleintent = new Intent(startuppage.this, Search.class);
                                    googleintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(googleintent);
                                    finish();
                                }
                            });
                            alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, "proceed without referral code", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent googleintent = new Intent(startuppage.this, Search.class);
                                    googleintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(googleintent);
                                    finish();
                                }
                            });
                            alertdialog.setView(view);
                            alertdialog.show();

                        }
                    });
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.getException());
                    Toast.makeText(startuppage.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                    googleProgressDialog.dismiss();
                    updateUI(null);
                }
            }
        });
    }
    private void updateUI(FirebaseUser user) {
    }
}