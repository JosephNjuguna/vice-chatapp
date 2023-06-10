package com.oskip.bitapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.facebook.FacebookSdk.getApplicationContext;

public class ProfileActivity extends AppCompatActivity  implements GoogleApiClient.OnConnectionFailedListener  {
    private static final String TAG = "TAG";
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    //----------------------------------
    Toolbar toolbar;
    FrameLayout mainframe;
    private WallFragMent wallFragMent;
    private NeonWatch neonWatch;
    private ProfileFragment profileFragment;
    GalleryFrag galleryFrag;
    String time;
    //----------------------------------
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth authuser;
    protected FirebaseUser mCurrentUser;
    GoogleApiClient mGoogleApiClient;
    protected DatabaseReference mDatabaseUser;
    private static final int REQUEST_INVITE = 1999;
    DatabaseReference mallUsers;
    DatabaseReference notification;
    String user;
    DatabaseReference friendrequest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_profile_page);
    authuser = FirebaseAuth.getInstance();
    mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
    user = mCurrentUser.getUid();
    mFirebaseAuth = FirebaseAuth.getInstance();
    mDatabaseUser = FirebaseDatabase.getInstance().getReference().child("users");
    friendrequest = FirebaseDatabase.getInstance().getReference().child("friend_request").child(user);
    mallUsers = FirebaseDatabase.getInstance().getReference().child("users");
    FirebaseUserActions.getInstance().start(getIndexApiAction());
    notification = FirebaseDatabase.getInstance().getReference().child("notification");
    notification.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.hasChild(user)) {
                friendrequestalert();
            }
        }
        @Override
        public void onCancelled(DatabaseError databaseError) {
        }
    });
        mGoogleApiClient = new GoogleApiClient.Builder(ProfileActivity.this)
            .enableAutoManage(ProfileActivity.this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API)
            .build();
    wallFragMent = new WallFragMent();
    neonWatch = new NeonWatch();
    profileFragment = new ProfileFragment();
    galleryFrag = new GalleryFrag();
    setFragment();
    BottomNavigationView navigationView = findViewById(R.id.navigation233);
    navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener2);
    mainframe = findViewById(R.id.content233);
    toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    }
    private void friendrequestalert() {
        LayoutInflater inflaterr= getLayoutInflater();
        final View view = inflaterr.inflate(R.layout.friendsnotification,null);
        final AlertDialog alertdialog = new AlertDialog.Builder(ProfileActivity.this).create();
        alertdialog.setCancelable(true);
        RecyclerView recyclerView = view.findViewById(R.id.friendspop);
        FirebaseRecyclerAdapter<Upload5,AllTestHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Upload5,AllTestHolder>(
                Upload5.class,
                R.layout.friend_request,
                AllTestHolder.class,
                friendrequest
        ) {
            @Override
            protected void populateViewHolder(final AllTestHolder viewHolder, Upload5 model, int position) {
                final String user_id2 = getRef(position).getKey();
                mallUsers.child(user_id2).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("thumb_image")){
                            String image = dataSnapshot.child("thumb_image").getValue().toString();
                            viewHolder.setuserpic(image,ProfileActivity.this);
                        }
                        String name = dataSnapshot.child("name").getValue().toString();
                        viewHolder.setname(name);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
                viewHolder.useraccept.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toprofile = new Intent( ProfileActivity.this,UserFriendsAccount.class);
                        toprofile.putExtra("from_user_id",user_id2);
                        startActivity(toprofile);
                    }
                });
                viewHolder.userdecline.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toprofile = new Intent( ProfileActivity.this,UserFriendsAccount.class);
                        toprofile.putExtra("from_user_id",user_id2);
                        startActivity(toprofile);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
        alertdialog.setView(view);
        alertdialog.show();
    }
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener2= new BottomNavigationView.OnNavigationItemSelectedListener() {
      @Override
      public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment currentfragment = getSupportFragmentManager().findFragmentById(R.id.content233);
          switch (item.getItemId()) {
            case R.id.navigation_wall:
                replaceFragment(wallFragMent,currentfragment);
                return true;
            case R.id.navigation_post:
                replaceFragment(neonWatch,currentfragment);
                return true;
            case R.id.navigation_profile:
                replaceFragment(profileFragment,currentfragment);
                return true;
            case R.id.navigation_add_pics:
                 replaceFragment(galleryFrag,currentfragment);
                 return true;
            default:
                return false;
        }
      }
    };
    private void replaceFragment(Fragment fragment, Fragment currentfragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (fragment == wallFragMent){
            fragmentTransaction.hide(neonWatch);
            fragmentTransaction.hide(galleryFrag);
            fragmentTransaction.hide(profileFragment);
        }
        if (fragment == neonWatch){
            fragmentTransaction.hide(wallFragMent);
            fragmentTransaction.hide(galleryFrag);
            fragmentTransaction.hide(profileFragment);
        }
        if (fragment == galleryFrag){
            fragmentTransaction.hide(wallFragMent);
            fragmentTransaction.hide(neonWatch);
            fragmentTransaction.hide(profileFragment);
        }
        if (fragment == profileFragment){
            fragmentTransaction.hide(wallFragMent);
            fragmentTransaction.hide(neonWatch);
            fragmentTransaction.hide(galleryFrag);
        }
        fragmentTransaction.show(fragment);
        fragmentTransaction.commit();
    }
    private void setFragment() {
        final FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.content233,wallFragMent);
        fragmentTransaction.add(R.id.content233,neonWatch);
        fragmentTransaction.add(R.id.content233,profileFragment);
        fragmentTransaction.add(R.id.content233,galleryFrag);
        fragmentTransaction.hide(neonWatch);
        fragmentTransaction.hide(profileFragment);
        fragmentTransaction.hide(galleryFrag);
        fragmentTransaction.commit();
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    protected void onStart() {
        super.onStart();
        if (isOnline()) {
                 mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
            final String time = mCurrentUser.getUid();
            FirebaseUser current_user = authuser.getCurrentUser();
            if (current_user == null) {
                Map updatehashmap = new HashMap();
                updatehashmap.put("online","true");
                mDatabaseUser.child(time).updateChildren(updatehashmap);
            }
        }else{
            Toast.makeText(ProfileActivity.this, "Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
    public static class AllTestHolder extends RecyclerView.ViewHolder {
        View mitemView;
        TextView useraccept;
        TextView userdecline;
        public AllTestHolder(final View itemView) {
            super(itemView);
            mitemView = itemView;
        }
        public void setname(String name){
            TextView userNAME = mitemView.findViewById(R.id.textViewUsee);
            userNAME.setText(name);
        }
        public void setaccept(String accept){
            useraccept = mitemView.findViewById(R.id.textView10accept);
            useraccept.setText(accept);
        }
        public void setdecline(String country){
            userdecline = mitemView.findViewById(R.id.textView14decline);
            userdecline.setText(country);
        }
        public void setuserpic(String thumb_image ,Context ctx){
            CircleImageView userpic = mitemView.findViewById(R.id.imageViewpop);
            Picasso.with(ctx).load(thumb_image).placeholder(R.mipmap.circle_spin).into(userpic);
        }
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
    protected void onStop() {
        super.onStop();
        FirebaseUserActions.getInstance().end(getIndexApiAction());
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mCurrentUser != null) {
            time = mCurrentUser.getUid();
        }
        FirebaseUser current_user = authuser.getCurrentUser();
        if (current_user != null) {
            Map updatehashmap = new HashMap();
            updatehashmap.put("online", ServerValue.TIMESTAMP);
            mDatabaseUser.child(time).updateChildren(updatehashmap);

        }else{
            Toast.makeText(ProfileActivity.this, "...Please Try Again...", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.notify) {
            Intent notifyIntent = new Intent(ProfileActivity.this, Notifications.class);
            startActivity(notifyIntent);
            return true;
        }
        if (id == R.id.nav_edit) {
            Intent editProfileIntent = new Intent(ProfileActivity.this, EditProfile.class);
            startActivity(editProfileIntent);
            return true;
        }
        if (id == R.id.search) {
            Intent shareIntent = new Intent(ProfileActivity.this, Search.class);
            startActivity(shareIntent);
            return true;
        }
        if (id == R.id.neonpage) {
            Intent editProfileIntent = new Intent(ProfileActivity.this,NeonPost.class);
            startActivity(editProfileIntent);
            return true;
        }
        if (id == R.id.invite) {
           onInviteClicked();
            return true;
        }
        if (id == R.id.action_settings) {
            Intent editProfileIntent = new Intent(ProfileActivity.this,PrivateProfileSettings.class);
            startActivity(editProfileIntent);
            return true;
        }
        if (id == R.id.chat) {
            Intent editProfileIntent = new Intent(ProfileActivity.this,Wallet.class);
            startActivity(editProfileIntent);
            return true;
        }
        if (id == R.id.log_out){
            final AlertDialog alertdialog = new AlertDialog.Builder(ProfileActivity.this).create();
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
            //alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, "PROCEED", new DialogInterface.OnClickListener() {
                //@Override
                //public void onClick(DialogInterface dialog, int which) {
                    //FirebaseAuth.getInstance().signOut();
                    //Intent logoutintent = new Intent(ProfileActivity.this,phonelogin.class);
                    //startActivity(logoutintent);
                    //finish();
            //    }
            //});
            alertdialog.show();
        }
        if (id == R.id.terms) {
            Intent editProfileIntent = new Intent(ProfileActivity.this,TermsPage.class);
            startActivity(editProfileIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public Action getIndexApiAction() {
        return Actions.newView("Profile", "http://[ENTER-YOUR-URL-HERE]");
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
}