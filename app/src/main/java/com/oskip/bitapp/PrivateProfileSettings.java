package com.oskip.bitapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class PrivateProfileSettings extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "TAG";
    private static final int REQUEST_INVITE = 1999;
    CircleImageView privatedp;
    TextView privatename;
    ImageView privatenotification;
    TextView privatenotificationtext;
    ImageView privacykey;
    TextView privacykeytext;
    ImageView privateinvite;
    TextView privateinvitefriendstext;
    ImageView privatewalletcoin;
    TextView privatewallettext;
    ImageView privateredlogout;
    TextView privateredlogouttext;
    ImageView privatehelp;
    TextView privatehelptext;
    ImageView privatechatsetting;
    TextView privatechatsettingtext;
    //--------------------------
    protected FirebaseUser mCurrentUser;
    protected DatabaseReference mDatabaseUser;
    FirebaseAuth mAuth ;
    //-------------------------------------------
    GoogleApiClient mGoogleApiClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.private_profile_settings);
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser= FirebaseAuth.getInstance().getCurrentUser();
        final String uid = mCurrentUser.getUid();
        mDatabaseUser= FirebaseDatabase.getInstance().getReference("users").child(uid);
        mDatabaseUser.keepSynced(true);
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("name")) {
                    String phone = dataSnapshot.child("name").getValue().toString();
                    privatename.setText(phone);
                }else{
                    privatename.setText("phone number");
                }
                if (dataSnapshot.hasChild("thumb_image")) {
                    String phone = dataSnapshot.child("thumb_image").getValue().toString();
                    Picasso.with(getApplicationContext()).load(phone).placeholder(R.mipmap.circle_spin).into(privatedp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PrivateProfileSettings.this,"Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });
        mGoogleApiClient = new GoogleApiClient.Builder(PrivateProfileSettings.this)
                .enableAutoManage(PrivateProfileSettings.this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        //-----------------------------------------------------
        privatechatsetting= findViewById(R.id.chatsetting);
        privatechatsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Choose chat background image"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivateProfileSettings.this);
                builder.setTitle("Select Chat Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                        }
                    }
                });
                builder.show();
            }
        });
        privatechatsettingtext = findViewById(R.id.chatsettingtext);
        privatechatsettingtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Choose chat background image"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivateProfileSettings.this);
                builder.setTitle("Select Chat Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                        }
                    }
                });
                builder.show();
            }
        });
        privatename = findViewById(R.id.nametext);
        privatedp = findViewById(R.id.privateaccountdp);
        privatenotification = findViewById(R.id.notificationacc);
        privatenotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Receive notification fully", "Receive notification only from friendschatpage" , "Receive notification from people you like" , "Block All notifications"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivateProfileSettings.this);
                builder.setTitle("Select Notification Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                        }
                        if (which == 1) {
                        }
                    }
                });
                builder.show();
            }
        });
        privatenotificationtext = findViewById(R.id.notificationacctext);
        privatenotificationtext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          CharSequence options[] = new CharSequence[]{"Receive notification fully", "Receive notification only from friendschatpage" , "Receive notification from people you like" , "Block All notifications"};
          AlertDialog.Builder builder = new AlertDialog.Builder(PrivateProfileSettings.this);
          builder.setTitle("Select Notification Option");
          builder.setItems(options, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  if (which == 0) {
                  }
                  if (which == 1) {
                  }
              }
          });
          builder.show();
            }
        });
        privacykey = findViewById(R.id.privacy);
        privacykey.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          CharSequence options[] = new CharSequence[]{"Make Account Private","Block Comments in your Pics","Hide account","Block someone","Receive screenshot notification",};
          AlertDialog.Builder builder = new AlertDialog.Builder(PrivateProfileSettings.this);
          builder.setTitle("Select PRIVACY Option");
          builder.setItems(options, new DialogInterface.OnClickListener() {
              @Override
              public void onClick(DialogInterface dialog, int which) {
                  if (which == 0) {
                  }
                  if (which == 1) {
                  }
              }
          });
         builder.show();
            }
        });
        privacykeytext = findViewById(R.id.privacytext);
        privacykeytext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Make Account Private","Block Comments in your Pics","Hide account","Block someone","Receive screenshot notification",};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivateProfileSettings.this);
                builder.setTitle("Select PRIVACY Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                        }
                        if (which == 1) {
                        }
                    }
                });
                builder.show();
            }
        });
        privateinvite = findViewById(R.id.invitefriends);
        privateinvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInviteClicked();
            }
        });
        privateinvitefriendstext = findViewById(R.id.invitefriendstext);
        privateinvitefriendstext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onInviteClicked();
            }
        });
        privatewalletcoin = findViewById(R.id.walletcoin);
        privatewalletcoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent walletintent = new Intent(PrivateProfileSettings.this,Wallet.class);
                startActivity(walletintent);
            }
        });
        privatewallettext = findViewById(R.id.walllettext);
        privatewallettext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent walletintent = new Intent(PrivateProfileSettings.this,Wallet.class);
                startActivity(walletintent);
            }
        });
        privateredlogout = findViewById(R.id.redlogout);
        privateredlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertdialog = new AlertDialog.Builder(PrivateProfileSettings.this).create();
                alertdialog.setTitle("Are you Sure??");
                alertdialog.setMessage("please don`t log out.This will lead to loss of your account data including the CYRPTOCOIN." +
                        "this issue will be implemented on next update.thanks");
                alertdialog.setCancelable(false);
                alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertdialog.dismiss();
                    }
                });
                alertdialog.show();
            }
        });
        privateredlogouttext = findViewById(R.id.logouttext);
        privateredlogouttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog alertdialog = new AlertDialog.Builder(PrivateProfileSettings.this).create();
                alertdialog.setTitle("Are you Sure??");
                alertdialog.setMessage("please don`t log out.This will lead to loss of your account data including the CYRPTOCOIN." +
                        "this issue will be implemented on next update.thanks");
                alertdialog.setCancelable(false);
                alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertdialog.dismiss();
                    }
                });
                alertdialog.show();
            }
        });
        privatehelp = findViewById(R.id.help);
        privatehelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Contact Us","Terms & Policy","How to use the app"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivateProfileSettings.this);
                builder.setTitle("Select Help Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent touserpage = new Intent(PrivateProfileSettings.this,ConnectWithUs.class);
                            startActivity(touserpage);
                        }
                        if (which == 1) {
                            Intent messagetofriend = new Intent(PrivateProfileSettings.this,TermsPage.class);
                            startActivity(messagetofriend);
                        }
                    }
                });
                builder.show();
            }
        });
        privatehelptext = findViewById(R.id.helptext);
        privatehelptext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Contact Us","Terms & Policy","How to use the app"};
                AlertDialog.Builder builder = new AlertDialog.Builder(PrivateProfileSettings.this);
                builder.setTitle("Select Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent touserpage = new Intent(PrivateProfileSettings.this,ConnectWithUs.class);
                            startActivity(touserpage);
                        }
                        if (which == 1) {
                            Intent messagetofriend = new Intent(PrivateProfileSettings.this,TermsPage.class);
                            startActivity(messagetofriend);
                        }
                    }
                });
                builder.show();
            }
        });
        //---------------------------------------------------------------------------------------
        privatename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenteditProfile = new Intent(PrivateProfileSettings.this,EditProfile.class);
                startActivity(intenteditProfile);
            }
        });
        privatedp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intenteditProfile = new Intent(PrivateProfileSettings.this,EditProfile.class);
                startActivity(intenteditProfile);
            }
        });
        //--------------------------------------------------------------------

    }
    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Get the invitation IDs of all sent messages
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                for (String id : ids) {
                    Log.d(TAG, "onActivityResult: sent invitation " + id);
                }
            } else {
            }
        }
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent logoutintent = new Intent(PrivateProfileSettings.this ,ProfileActivity.class);
        startActivity(logoutintent);
    }
}