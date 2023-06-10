package com.oskip.bitapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileFragment extends Fragment {
    public RecyclerView recyclerView90;
    ImageView mprofilepic;
    TextView mname;
    TextView mcountry;
    TextView friendcount;
    TextView fanscount;
    TextView vicebuckscount;
    ImageView imagetoviceaccount;
    TextView texttovoiceaccount;
    //---------------------------------------------------
    DatabaseReference mfriendscount;
    DatabaseReference mfollowerscount;
    public DatabaseReference mDatabaseoldpost;
    public DatabaseReference mDatabasefriends2;
    protected FirebaseUser mCurrentUser;
    protected DatabaseReference mDatabaseUser;
    private static final int REQUEST_READ_PERMISSION = 120;
    //------------------------------------------------------
    FloatingActionMenu menuprofile;
    FloatingActionButton floatingaddfriendsbutton;
    FloatingActionButton floatingtimelinebutton;
    FloatingActionButton floatingsettingsbutton;
    FloatingActionButton floatingwalletbutton;
    LinearLayoutManager linearLayoutManager;
    //_________________________________________________
    String coinedd;
    String uid;
    DatabaseReference mCoin;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        menuprofile = view.findViewById(R.id.profilemenu);
         floatingaddfriendsbutton = view.findViewById(R.id.floatingaddfriends);
         floatingaddfriendsbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent searchfriends = new Intent(getContext().getApplicationContext(),Search.class);
                 startActivity(searchfriends);
             }
         });
         floatingtimelinebutton = view.findViewById(R.id.floatingtimeline);
         floatingtimelinebutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent oldpost = new Intent(getContext().getApplicationContext(),OldUserPost.class);
                 startActivity(oldpost);
             }
         });
         floatingsettingsbutton = view.findViewById(R.id.floatingprivacysettings);
         floatingsettingsbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent privacy = new Intent(getContext().getApplicationContext(),PrivateProfileSettings.class);
                 startActivity(privacy);
             }
         });
         floatingwalletbutton = view.findViewById(R.id.floatingwallet);
         floatingwalletbutton.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Intent ewallet = new Intent(getContext().getApplicationContext(),Wallet.class);
                 startActivity(ewallet);
             }
         });
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mCurrentUser != null;
        uid = mCurrentUser.getUid();
        recyclerView90 = view.findViewById(R.id.friendsimageprofile);
        linearLayoutManager = new LinearLayoutManager(getContext().getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView90.setLayoutManager(linearLayoutManager);
        recyclerView90.setHasFixedSize(true);
        //-----------------------------------------------------------------------------
        vicebuckscount = view.findViewById(R.id.vicebucks);
        mCoin = FirebaseDatabase.getInstance().getReference().child("coin").child(uid);
        mCoin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("coins_count")){
                    coinedd = dataSnapshot.child("coins_count").getValue().toString();
                    vicebuckscount.setText(coinedd);
                }else {
                    vicebuckscount.setText("nil bal");
                    Toast.makeText(getApplicationContext(), "Post a picture to start earning", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        imagetoviceaccount = view.findViewById(R.id.toaccount);
        imagetoviceaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viceaccount = new Intent(getApplicationContext(),Wallet.class);
                startActivity(viceaccount);
            }
        });
        texttovoiceaccount = view.findViewById(R.id.vicetext);
        texttovoiceaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viceaccount = new Intent(getApplicationContext(),Wallet.class);
                startActivity(viceaccount);
            }
        });
        mprofilepic = view.findViewById(R.id.circleView);
        mname = view.findViewById(R.id.name);
        mcountry = view.findViewById(R.id.country);
        friendcount = view.findViewById(R.id.friendsNumber);
        fanscount = view.findViewById(R.id.fansNumber);
        mfollowerscount = FirebaseDatabase.getInstance().getReference().child("followers").child(uid);
        mfollowerscount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("followers_count")) {
                    long friends = Long.parseLong(dataSnapshot.child("followers_count").getValue().toString());
                    fanscount.setText((String.valueOf(friends)));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mfriendscount = FirebaseDatabase.getInstance().getReference().child("Friends").child(uid);
        mfriendscount.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("friends_count")) {
                    long friends = Long.parseLong(dataSnapshot.child("friends_count").getValue().toString());
                    friendcount.setText((String.valueOf(friends)));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mDatabaseUser.keepSynced(true);
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                mname.setText(name);
                if (dataSnapshot.hasChild("thumbimage")) {
                    String image = dataSnapshot.child("thumbimage").getValue().toString();
                    Picasso.with(getContext()).load(image).placeholder(R.mipmap.circle_spin).into(mprofilepic);
                }else{
                    mprofilepic.setBackgroundResource(R.mipmap.circle_spin);
                }
                if(dataSnapshot.hasChild("country")){
                    String country = dataSnapshot.child("country").getValue().toString();
                    mcountry.setText(country);
                }else{
                    mcountry.setText("");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), "Something went wrong!!", Toast.LENGTH_SHORT).show();
            }
        });
        mDatabasefriends2 = FirebaseDatabase.getInstance().getReference().child("Friends").child(uid).child("userfriends");
        mDatabasefriends2.keepSynced(true);
        mDatabaseoldpost = FirebaseDatabase.getInstance().getReference().child("users");
        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(getActivity(), "Sorry!!!, you can't use this app without granting this permission", Toast.LENGTH_LONG).show();
            }
        }
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (isOnline()) {
            Toast.makeText(getApplicationContext(), "Good Connection", Toast.LENGTH_LONG).show();
            FirebaseRecyclerAdapter<Upload5, OldUserPostHolder> firebaseRecyclerAdaptermyfriends = new FirebaseRecyclerAdapter<Upload5, OldUserPostHolder>(
                    Upload5.class,
                    R.layout.friendsimage,
                    OldUserPostHolder.class,
                    mDatabasefriends2) {
                @Override
                protected void populateViewHolder(final OldUserPostHolder viewHolder2, Upload5 model, int position) {
                    final String user_id2 = getRef(position).getKey();
                    mDatabaseoldpost.child(user_id2).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.hasChild("thumbimage")){
                                String image = dataSnapshot.child("thumbimage").getValue().toString();
                                viewHolder2.setfrienddp(image, getContext().getApplicationContext());
                            }else{
                                Toast.makeText(getApplicationContext(),"GET SOME FRIENDS",Toast.LENGTH_SHORT).show();
                            }
                            String name = dataSnapshot.child("name").getValue().toString();
                            viewHolder2.setName(name);
                            String online = dataSnapshot.child("online").getValue().toString();
                            if (online.equals("true")) {
                                viewHolder2.useronline.setVisibility(View.VISIBLE);
                            }else{
                                viewHolder2.useronline.setBackgroundResource(R.mipmap.offline);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                    viewHolder2.useroldpost.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                          Intent messagetofriend = new Intent(getContext(), Chat.class);
                          messagetofriend.putExtra("from_user_id", user_id2);
                          startActivity(messagetofriend);
                        }
                    });
                }
            };
            recyclerView90.setAdapter(firebaseRecyclerAdaptermyfriends);
        }else {
            Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
    public static class OldUserPostHolder extends RecyclerView.ViewHolder {
        View mitemView;
        ImageView useroldpost;
        ImageButton useronline;
        public OldUserPostHolder(final View itemView) {
            super(itemView);
            mitemView = itemView;
            useroldpost = mitemView.findViewById(R.id.my_friends_dp);
            useronline = mitemView.findViewById(R.id.imageButtonONLINE);
        }
        public  void  setName(String name){
            TextView friendsname = mitemView.findViewById(R.id.myfriendsname);
            friendsname.setText(name);
        }
        public void setfrienddp(String thumbimage, Context ctx) {
            Picasso.with(ctx).load(thumbimage).placeholder(R.mipmap.circle_spin).into(useroldpost);
        }
    }
}