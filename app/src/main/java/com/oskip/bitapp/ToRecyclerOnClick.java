package com.oskip.bitapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

public class ToRecyclerOnClick extends AppCompatActivity {
    private static final String TAG ="tag" ;
    String fromwallfragment;
    ImageView  onclickfullimage;
    TextView onclickcaption;
    ImageView onclicklike;
    TextView onclicklikecount;
    ImageView onclickheart;
    TextView onclickheartcount;
    ImageView onclickcomment;
    TextView onclickcommentcount;
    RecyclerView onclickwall;
    CircleImageView imageeight;
    TextView afterwallname;
    TextView commentclick;
    //----------------FIREBASE-----------------
    private DatabaseReference comments;
    private DatabaseReference mOnclickdata;
    LinearLayoutManager mlinearLayoutManager;
    //-----------------------comment-------------------------------
    public FirebaseAuth mAuthenticatecomments;
    FirebaseUser mGetCurrentUsercomment;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    DatabaseReference databaseReference;
    private DatabaseReference commentsref;
    DatabaseReference mFirebaseDatabaseReference;
    RelativeLayout commentlayout;
    //---------------------------------------
    EmojiconEditText emojiconEditText;
    ImageView emojiImageView;
    ImageView submitButton;
    View rootView22;
    EmojIconActions emojIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_to_recycler_on_click);
        rootView22 = findViewById(R.id.commentstypo);
        emojiconEditText = findViewById(R.id.emojicon_edit_text);
        emojiconEditText.setVisibility(View.INVISIBLE);
        emojiImageView = findViewById(R.id.emoji_btn);
        emojiImageView.setVisibility(View.INVISIBLE);
        submitButton = findViewById(R.id.submit_btn);
        submitButton.setVisibility(View.INVISIBLE);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comment();
                emojiconEditText.setText("");
            }
        });
        emojIcon = new EmojIconActions(this, rootView22, emojiconEditText, emojiImageView);
        emojIcon.ShowEmojIcon();
        emojIcon.setIconsIds(R.mipmap.keyboard, R.mipmap.emo);
        emojIcon.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }
            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });
        //---------------------------UI SETUP---------------------------
        commentclick = findViewById(R.id.textView6);
        commentclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             emojiconEditText.setVisibility(View.VISIBLE);
             submitButton.setVisibility(View.VISIBLE);
             emojiImageView.setVisibility(View.VISIBLE);
            }
        });
        afterwallname = findViewById(R.id.textView30);
        imageeight = findViewById(R.id.imageView8);
        onclickfullimage = findViewById(R.id.imageView20);
        onclickcaption = findViewById(R.id.textView44);
        onclicklike = findViewById(R.id.imageView22);
        onclicklikecount = findViewById(R.id.textView42);
        onclickheart = findViewById(R.id.imageView21);
        onclickheartcount = findViewById(R.id.textView43);
        onclickcomment = findViewById(R.id.imageView23);
        onclickcommentcount = findViewById(R.id.textView45);
        mlinearLayoutManager = new LinearLayoutManager(ToRecyclerOnClick.this);
        mlinearLayoutManager.setReverseLayout(true);
        onclickwall= findViewById(R.id.recycler_comments);
        onclickwall.setLayoutManager(mlinearLayoutManager);
        //-------------------FIREBASE SETUP-----------------------------
        fromwallfragment = getIntent().getStringExtra("post_id");
        mOnclickdata = FirebaseDatabase.getInstance().getReference().child("wall uploads").child(fromwallfragment);
        mOnclickdata.keepSynced(true);
        mOnclickdata.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                afterwallname.setText(name);
                if (dataSnapshot.hasChild("thumbimage")) {
                    String thumb_image = dataSnapshot.child("thumbimage").getValue().toString();
                    Picasso.with(ToRecyclerOnClick.this).load(thumb_image).placeholder(R.mipmap.circle_spin).into(imageeight);
                }else{
                    String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();
                    Picasso.with(ToRecyclerOnClick.this).load(thumb_image).placeholder(R.mipmap.circle_spin).into(imageeight);
                }
                if (dataSnapshot.hasChild("caption")) {
                    String caption = dataSnapshot.child("caption").getValue().toString();
                    onclickcaption.setText(caption);
                }
                if (dataSnapshot.hasChild("likes_count")) {
                    long likes_count = Long.parseLong(dataSnapshot.child("likes_count").getValue().toString());
                    onclicklikecount.setText((String.valueOf(likes_count)));
                }
                if (dataSnapshot.hasChild("hearts_count")) {
                    long hearts_count = Long.parseLong(dataSnapshot.child("hearts_count").getValue().toString());
                    onclickheartcount.setText((String.valueOf(hearts_count)));
                }
                if (dataSnapshot.hasChild("comments_count")){
                    long comments_count = Long.parseLong(dataSnapshot.child("comments_count").getValue().toString());
                    onclickcommentcount.setText((String.valueOf(comments_count)));
                }
                if (dataSnapshot.hasChild("thumb")){
                    String url = dataSnapshot.child("thumb").getValue().toString();
                    Picasso.with(ToRecyclerOnClick.this).load(url).into(onclickfullimage);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //-------------------------------------------
        mAuthenticatecomments = FirebaseAuth.getInstance();
        mGetCurrentUsercomment = mAuthenticatecomments.getCurrentUser();
        //---------------------------------------------------------------------------------------------------        databaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE_PATH_UPLOADS).child(fromwallfragment).child("comments");
        commentsref = FirebaseDatabase.getInstance().getReference().child("users").child(mGetCurrentUsercomment.getUid());
        //-------------------------------------------------------------------------------------------------------
        databaseReference = FirebaseDatabase.getInstance().getReference().child("wall uploads").child(fromwallfragment).child("comments");
        comments = FirebaseDatabase.getInstance().getReference().child("wall uploads").child(fromwallfragment).child("comments");
    }
    private void comment() {
        final String friendcomment = emojiconEditText.getText().toString();
        if (TextUtils.isEmpty(friendcomment)) {
            Toast.makeText(ToRecyclerOnClick.this, "enter comment", Toast.LENGTH_LONG).show();
            return;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        Date date = new Date();
        final String strdate = dateFormat.format(date).toString();
        final DatabaseReference friendscomment = databaseReference.push();
        commentsref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                friendscomment.child("comment").setValue(friendcomment);
                friendscomment.child("id").setValue(mGetCurrentUsercomment.getUid());
                friendscomment.child("timestamp").setValue(strdate);
                friendscomment.child("username").setValue(dataSnapshot.child("name").getValue());
                friendscomment.child("userdp").setValue(dataSnapshot.child("image").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull com.google.android.gms.tasks.Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "comment posted ", Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }
                });
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    @Override
        public void onStart() {
            super.onStart();
            FirebaseRecyclerAdapter<CommentsObject,WallonClickHolder> firebaseRecyclerAdapterComments = new FirebaseRecyclerAdapter<CommentsObject,WallonClickHolder>(
                    CommentsObject.class,
                    R.layout.commentinsert,
                    WallonClickHolder.class,
                    comments
            ) {
                @Override
                protected void populateViewHolder(final WallonClickHolder viewHolder, CommentsObject model, int position) {
                    viewHolder.setcomment(model.getComment());
                    viewHolder.settime(model.getTimestamp());
                    viewHolder.setusercomment(model.getUsercommentdp(),getApplicationContext());
                    viewHolder.setcommentname(model.getCommentusername());
                }
            };
            onclickwall.setAdapter(firebaseRecyclerAdapterComments);
        }
    public static class WallonClickHolder extends RecyclerView.ViewHolder {
        View mitemview;
        public WallonClickHolder(final View itemView) {
            super(itemView);
            mitemview = itemView;
        }
        public void setusercomment(String image ,Context ctx){
            ImageView  usercommentpic = mitemview.findViewById(R.id.imageViewProfilecomment);
            Picasso.with(ctx).load(image).placeholder(R.mipmap.circle_spin).into(usercommentpic);
        }
        public void setcomment(String comment){
            TextView userwallcaption = mitemview.findViewById(R.id.comment);
            userwallcaption.setText(comment);
        }
        public void setcommentname(String username){
            TextView user = mitemview.findViewById(R.id.comment_username);
            user.setText(username);
        }
        public void settime(String timestamp){
            TextView  time  = mitemview.findViewById(R.id.textView83);
            time.setText(timestamp);
        }
    }
}