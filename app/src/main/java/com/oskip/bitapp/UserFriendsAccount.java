package com.oskip.bitapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserFriendsAccount extends AppCompatActivity {
    private static final String TAG = "USER";
    DatabaseReference mUserProfile;
    ImageView userprofileimage;
    TextView userprofilename;
    TextView userprofilecountry;
    Button sendRequest;
    Button declinebutton;
    Button profilefollow;
    DatabaseReference profilefollowers;
    //----------------------------------------------------
    private String mcurrent_state;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference friendrequest;
    DatabaseReference followrequest;
    private DatabaseReference mRootref;
    private FirebaseUser mCurrent_user;
    public DatabaseReference mDatabase;
    public FirebaseAuth mAuth;
    private Query mQuery;
    private TextView friendcountprofile;
    private TextView fanscountprofile;
    RecyclerView newfriendspost;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference mfollowerscountprofile;
    DatabaseReference mfriendscountprofile;
    DatabaseReference eCoin;
    String user_id;
    Button alreadyfollowing;
    DatabaseReference notificationrefremove;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);
        user_id = getIntent().getStringExtra("from_user_id");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        mRootref = FirebaseDatabase.getInstance().getReference();
        mcurrent_state = "not_friends";
        notificationrefremove = FirebaseDatabase.getInstance().getReference().child("notification");
        //---------------------------------------------------------
        linearLayoutManager = new LinearLayoutManager(UserFriendsAccount.this);
        newfriendspost = findViewById(R.id.newfriendspost);
        newfriendspost.setLayoutManager(linearLayoutManager);
        //---------------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        mQuery  =  mDatabase.orderByChild("id").equalTo(user_id);
        //------------------------------------------------------------------------
        profilefollowers = FirebaseDatabase.getInstance().getReference().child("followers").child(user_id);
        //-----------------------------------------------------------------------------------
        mUserProfile = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);
        friendrequest = FirebaseDatabase.getInstance().getReference().child("friend_request");
        eCoin = FirebaseDatabase.getInstance().getReference().child("coin").child(user_id).child("coin push").push();
        followrequest = FirebaseDatabase.getInstance().getReference().child("followers").child(user_id).child("userfollowers");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        userprofileimage = findViewById(R.id.userprofileimage);
        userprofilename = findViewById(R.id.userprofilename);
        userprofilecountry = findViewById(R.id.userprifilecountry);
        fanscountprofile = findViewById(R.id.textView26);
        alreadyfollowing = findViewById(R.id.following);
        alreadyfollowing.setVisibility(View.INVISIBLE);

        //------------------------------------------------------------------------------
        mfollowerscountprofile = FirebaseDatabase.getInstance().getReference().child("followers").child(user_id);
        mfollowerscountprofile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("followers_count")) {
                    long friends = Long.parseLong(dataSnapshot.child("followers_count").getValue().toString());
                    fanscountprofile.setText((String.valueOf(friends)));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //-----------------------------------------------------------------------------------------------------------
        friendcountprofile = findViewById(R.id.textView17);
        mfriendscountprofile = FirebaseDatabase.getInstance().getReference().child("Friends").child(user_id);
        mfriendscountprofile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("friends_count")) {
                    long friends = Long.parseLong(dataSnapshot.child("friends_count").getValue().toString());
                    friendcountprofile.setText((String.valueOf(friends)));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //---------------------------------------****************************-----------------------------------------------------------------------------------
        mUserProfile.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                if (dataSnapshot.hasChild("thumbimage")){
                    String image = dataSnapshot.child("thumbimage").getValue().toString();
                    Picasso.with(UserFriendsAccount.this).load(image).placeholder(R.mipmap.circle_spin).into(userprofileimage);
                }else{
                    userprofileimage.setBackgroundResource(R.mipmap.circle_spin);
                }
                if (dataSnapshot.hasChild("country")) {
                    String country = dataSnapshot.child("country").getValue().toString();
                    userprofilecountry.setText(country);
                }else{
                    userprofilecountry.setVisibility(View.INVISIBLE);
                }
                userprofilename.setText(name);
                //-------------------------------------------Friends List-----------------------------------------------------
                friendrequest.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_id)) {
                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {
                                mcurrent_state = "req_received";
                                sendRequest.setText("Accept Friend Request");
                                //
                                declinebutton.setVisibility(View.VISIBLE);
                                declinebutton.setEnabled(true);
                                //
                            } else if (req_type.equals("sent")) {
                                mcurrent_state = "req_sent";
                                sendRequest.setText("Cancel Friend Request");
                                declinebutton.setVisibility(View.INVISIBLE);
                                declinebutton.setEnabled(false);
                            }
                        } else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {
                                        mcurrent_state = "friendschatpage";
                                        sendRequest.setText("Unfriend this person");
                                        declinebutton.setVisibility(View.INVISIBLE);
                                        declinebutton.setEnabled(false);
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        declinebutton = findViewById(R.id.declineFriendRequest);
        declinebutton.setVisibility(View.INVISIBLE);
        declinebutton.setEnabled(false);
        declinebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ----REMOVE FRIEND  STATE---
                if (mcurrent_state .equals("req_received")){
                    Map decline = new HashMap();
                    decline.put("friend_request/" + mCurrent_user.getUid() + "/" + user_id , null);
                    decline.put("friend_request/" +  user_id + "/" + mCurrent_user.getUid(),  null );
                    mRootref.updateChildren(decline, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(final DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError  == null ) {
                                notificationrefremove.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(user_id)){
                                            notificationrefremove.child(user_id).push().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                mcurrent_state = "not_friends";
                                sendRequest.setText("send friend Request");
                                declinebutton.setVisibility(View.INVISIBLE);
                                declinebutton.setEnabled(false);
                            }else {
                                String error = databaseError.getMessage();
                                Toast.makeText( UserFriendsAccount.this , error , Toast.LENGTH_SHORT).show();
                            }
                            sendRequest.setEnabled(true);
                        }
                    });
                }
            }
        });
        profilefollow = findViewById(R.id.button13followlo);
        profilefollow.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                     follow();
                                                     profilefollow.setEnabled(false);
                                                     profilefollow.setText("FOLLOWING");
                                                     profilefollow.setHint("FOLLOWING");
                                                     alreadyfollowing.setVisibility(View.VISIBLE);
                                                     profilefollow.setVisibility(View.INVISIBLE);
                                             }
                                         });

        //-------------------------------------------------------------------------------
        sendRequest = findViewById(R.id.sendFriendRequest);
        sendRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequest.setEnabled(false);
                // --------------------NOT FRIENDS STATE --------|||
                if (mcurrent_state.equals("not_friends")) {
                    DatabaseReference notificationref = mRootref.child("notification").child(user_id).push();
                    String notificationid = notificationref.getKey();
                    HashMap<String,String> notification = new HashMap<>();
                    notification.put("from",mCurrent_user.getUid());
                    notification.put("type","request");
                    Map requestMap = new HashMap();
                    requestMap.put("friend_request/" + mCurrent_user.getUid() + "/" + user_id + "/request_type" , "sent");
                    requestMap.put("friend_request/" + user_id + "/"+ mCurrent_user.getUid() + "/request_type" , "received");
                    requestMap.put("notification/" + user_id + "/" + notificationid,notification);
                    mRootref.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null){
                                Toast.makeText(UserFriendsAccount.this, "there was an error.Try again please", Toast.LENGTH_SHORT).show();
                            }
                            sendRequest.setEnabled(true);
                            mcurrent_state ="req_sent";
                            sendRequest.setText("Cancel Friend Request Sent");
                        }
                    });
                }
                // ----- CANCEL FRIEND REQUEST  -------
                if (mcurrent_state.equals("req_sent")) {
                    friendrequest.child(mCurrent_user.getUid()).child(user_id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                          friendrequest.child(user_id).child(mCurrent_user.getUid()).removeValue()
                           .addOnSuccessListener(new OnSuccessListener<Void>() {
                               @Override
                               public void onSuccess(Void aVoid) {
                                   notificationrefremove.addValueEventListener(new ValueEventListener() {
                                       @Override
                                       public void onDataChange(DataSnapshot dataSnapshot) {
                                           if (dataSnapshot.hasChild(user_id)){
                                               notificationrefremove.child(user_id).push().removeValue();
                                           }
                                       }
                                       @Override
                                       public void onCancelled(DatabaseError databaseError) {
                                       }
                                   });
                                   sendRequest.setEnabled(true);
                                   mcurrent_state = "not_friends";
                                   sendRequest.setText("Send Friend Request");
                                   declinebutton.setVisibility(View.INVISIBLE);
                                   declinebutton.setEnabled(false);
                               }
                           });
                        }
                    });
                }
                //--------------REQUEST RECEIVED STATE------------------
                if (mcurrent_state.equals("req_received")) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss ");
                    Date date = new Date();
                    final String strdate = dateFormat.format(date).toString();
                    Map friendsMap  = new HashMap();
                    friendsMap.put("Friends/" + mCurrent_user.getUid() +"/userfriends"+ "/"+ user_id+"/date",strdate);
                    friendsMap.put("Friends/" + user_id +"/userfriends"+ "/" + mCurrent_user.getUid()+"/date",strdate);
                    friendsMap.put( "friend_request/" + mCurrent_user.getUid()  + "/" +user_id , null);
                    friendsMap.put("friend_request/" +  user_id +"/" + mCurrent_user.getUid(), null);
                    mRootref.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError  == null ) {
                                notificationrefremove.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.hasChild(user_id)){
                                            notificationrefremove.child(user_id).push().removeValue();
                                        }
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                                sendRequest.setEnabled(true);
                                mcurrent_state ="friendschatpage";
                                sendRequest.setText("unfriend");
                                declinebutton.setVisibility(View.INVISIBLE);
                                declinebutton.setEnabled(false);
                            }else {
                                String error = databaseError.getMessage();
                                Toast.makeText( UserFriendsAccount.this , error , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //-------------------------UNFRIEND----------------------------------------------
                if (mcurrent_state .equals("friendschatpage")){
                    Map unfriendmap = new HashMap();
                    unfriendmap.put("Friends/"+ mCurrent_user.getUid() + "userfriends/" + user_id , null);
                    unfriendmap.put("Friends/"+  user_id + "userfriends/" + mCurrent_user.getUid(),  null );
                    mRootref.updateChildren(unfriendmap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError  == null ) {
                                mcurrent_state = "not_friends";
                                sendRequest.setText("send friend Request");
                                declinebutton.setVisibility(View.INVISIBLE);
                                declinebutton.setEnabled(false);
                            }else {
                                String error = databaseError.getMessage();
                                Toast.makeText( UserFriendsAccount.this , error , Toast.LENGTH_SHORT).show();
                            }
                            sendRequest.setEnabled(true);
                        }
                    });
                }
            }
        });
    }

    private void follow() {
        profilefollow.setEnabled(false);
        profilefollow.setText("FOLLOWING");
        HashMap<String, String> notification = new HashMap<String, String>();
        notification.put("from",mCurrent_user.getUid());
        notification.put("type", "following");
        followrequest.push().setValue(notification);
        // --------------------NOT FRIENDS STATE --------
        /*
        if (mcurrent_state.equals("not_friends")) {
            DatabaseReference notificationref = mRootref.child("notification").child(user_id).push();
            String notificationid = notificationref.getKey();
            HashMap<String,String> notification = new HashMap<>();
            notification.put("from",mCurrent_user.getUid());
            notification.put("type","request");
            Map requestMap = new HashMap();
            requestMap.put("follow_request/" + mCurrent_user.getUid() + "/" + user_id + "/request_type" , "sent");
            requestMap.put("follow_request/" + user_id + "/"+ mCurrent_user.getUid() + "/request_type" , "received");
            requestMap.put("notification/" + user_id + "/" + notificationid,notification);
            mRootref.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null){
                        Toast.makeText(UserFriendsAccount.this, "there was an error.Try again please", Toast.LENGTH_SHORT).show();
                    }
                    profilefollow.setEnabled(true);
                    mcurrent_state ="req_sent";
                    profilefollow.setText("Cancel Friend Request Sent");
                }
            });
        }
        // ----- CANCEL FRIEND REQUEST  -------
        if (mcurrent_state.equals("req_sent")) {
            followrequest.child(mCurrent_user.getUid()).child(user_id).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            followrequest.child(user_id).child(mCurrent_user.getUid()).removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            profilefollow.setEnabled(true);
                                            mcurrent_state = "not_friends";
                                            profilefollow.setText("Send Friend Request");
                                            declinebutton.setVisibility(View.INVISIBLE);
                                            declinebutton.setEnabled(false);
                                        }
                                    });
                        }
                    });
        }
        //--------------REQUEST RECEIVED STATE------------------
        if (mcurrent_state.equals("req_received")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss ");
            Date date = new Date();
            final String strdate = dateFormat.format(date).toString();
            Map friendsMap  = new HashMap();
            friendsMap.put("followers/" + mCurrent_user.getUid() +"/"+ user_id+"/date",strdate);
            friendsMap.put("followers" + user_id +"/"+mCurrent_user.getUid()+"/date",strdate);
            friendsMap.put( "follow_request/" + mCurrent_user.getUid()  + "/" +user_id , null);
            friendsMap.put("follow_request/" +  user_id +"/" + mCurrent_user.getUid(), null);
            mRootref.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError  == null ) {
                        profilefollow.setEnabled(true);
                        mcurrent_state ="friendschatpage";
                        profilefollow.setText("unfriend");
                        declinebutton.setVisibility(View.INVISIBLE);
                        declinebutton.setEnabled(false);
                    }else {
                        String error = databaseError.getMessage();
                        Toast.makeText( UserFriendsAccount.this , error , Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        //-------------------------UNFRIEND----------------------------------------------
        if (mcurrent_state .equals("friendschatpage")){
            Map unfriendmap = new HashMap();
            unfriendmap.put("followers/"+ mCurrent_user.getUid() + "/" + user_id , null);
            unfriendmap.put("followers/"+  user_id + "/" + mCurrent_user.getUid(),  null );
            mRootref.updateChildren(unfriendmap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError  == null ) {
                        mcurrent_state = "not_friends";
                        profilefollow.setText("send friend Request");
                        declinebutton.setVisibility(View.INVISIBLE);
                        declinebutton.setEnabled(false);
                    }else {
                        String error = databaseError.getMessage();
                        Toast.makeText( UserFriendsAccount.this , error , Toast.LENGTH_SHORT).show();
                    }
                    profilefollow.setEnabled(true);
                }
            });
        }*/
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Upload, OldUserPost.OldUserPostHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Upload, OldUserPost.OldUserPostHolder>(
                Upload.class,
                R.layout.friendstalk,
                OldUserPost.OldUserPostHolder.class,
                mQuery
        ) {
            @Override
            protected void populateViewHolder(final OldUserPost.OldUserPostHolder viewHolder, Upload model, int position) {
                final String old = getRef(position).getKey();
                viewHolder.setcaption(model.getCaption());
                //------------------------------------------------------
                viewHolder.setuserupload(model.getThumb(), getApplicationContext());
                //-------------------------------------------------------
                viewHolder.setoldlike(model.getLikes_count());
                viewHolder.setoldcomments(model.getComments_count());
                viewHolder.setoldhearts(model.getHearts_count());
            }
        };
        newfriendspost.setAdapter(firebaseRecyclerAdapter);
    }
}