package com.oskip.bitapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;
import id.zelory.compressor.Compressor;

public class Chat extends AppCompatActivity {
    private static final int SEND_IMAGE_REQUEST = 1998;
    private static final String TAG = "tag" ;
    private String mChatUser;
    Toolbar mChatToolbar;
    private DatabaseReference mrootref;
    private DatabaseReference mNotification;
    private TextView chatusername;
    private TextView userisonline;
    private CircleImageView userdp;
    //-------------------------FIREBASE-------------------------------------
    FirebaseAuth mAuth;
    String mcurrentuserid;
    StorageReference sendImage;
    private FirebaseUser mCurrent_user;
    //----------------------------MESSAGE TO FIREBASE--------------------------------------------
    private ListView mMessagesList;
    LinearLayoutManager mlinearLayoutManager;
    private final List<Messages> messageslist = new ArrayList<>();
    MessageAdpater mAdapter;
    EmojiconEditText emojiconEditText;
    ImageView emojiImageView;
    ImageView submitButton;
    View rootView;
    EmojIconActions emojIcon;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);
        //----------------------------------------------------------
        rootView =findViewById(R.id.bottomlayout);
        emojiImageView = findViewById(R.id.emoji_btn);
        submitButton = findViewById(R.id.submit_btn);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendmessage();
            }
        });
        emojiconEditText = findViewById(R.id.emojicon_edit_text);
        emojIcon = new EmojIconActions(this, rootView, emojiconEditText, emojiImageView);
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
        //--------------------------------------------------
        mChatUser = getIntent().getStringExtra("from_user_id");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        //-----------------------------------------USER INTERFACE----------------------------------
        mChatToolbar = findViewById(R.id.app_chat_bar);
        setSupportActionBar(mChatToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        //---------------------------------------------------------------------------------
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View chatroombar = inflater.inflate(R.layout.chat_bar, null);
        actionBar.setCustomView(chatroombar);
        //--------------------------ACTION BAR VIEW -------------------------------------------
        chatusername = findViewById(R.id.textView7);
        userisonline = findViewById(R.id.textView16);
        userdp = findViewById(R.id.circledp);
        //---------------------------RECYCLER VIEW -----------------------
        mlinearLayoutManager = new LinearLayoutManager(this);
        mMessagesList = findViewById(R.id.recyclerViewmessages);
        //----------------------------DATABASE REFERENCE-------------------------------------
        mAuth = FirebaseAuth.getInstance();
        mcurrentuserid = mAuth.getCurrentUser().getUid();
        sendImage = FirebaseStorage.getInstance().getReference().child("message images");
        //--------------------------------------------------------------------------------------------
        mNotification = FirebaseDatabase.getInstance().getReference().child("message notification");
        //--------------------------------------ONLINE STATUS-----------------------------------------
        mrootref = FirebaseDatabase.getInstance().getReference();
        mrootref.keepSynced(true);
        mrootref.child("users").child(mChatUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String online = dataSnapshot.child("online").getValue().toString();
                if (online.equals("true")) {
                    userisonline.setText("online");
                }else{
                    GetTimeAgo getTimeAgo = new GetTimeAgo();
                    long lastTime = Long.parseLong(online);
                    String lastseenTime = GetTimeAgo.getTimeAgo(lastTime, getApplicationContext());
                    userisonline.setText((String.valueOf(lastseenTime)));
                }
                chatusername.setText(name);
                if (dataSnapshot.hasChild("thumb_image")) {
                    String image = dataSnapshot.child("thumb_image").getValue().toString();
                    Picasso.with(Chat.this).load(image).placeholder(R.mipmap.circle_spin).into(userdp);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //--------------------------SENDING MESSAGE-------------------------
        mrootref.child("Chat").child(mcurrentuserid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("ss:mm:HH dd/MM/yyyy");
                Date date = new Date();
                final String strdate = dateFormat.format(date).toString();
                if (!dataSnapshot.hasChild(mChatUser)) {
                    Map chataddmap = new HashMap();
                    chataddmap.put("seen", false);
                    chataddmap.put("timestamp", strdate);
                    Map chatusermap = new HashMap();
                    chatusermap.put("Chat/" + mcurrentuserid + "/" + mChatUser, chataddmap);
                    chatusermap.put("Chat/" + mChatUser + "/" + mcurrentuserid, chataddmap);
                    mrootref.updateChildren(chatusermap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError != null) {
                                Log.d("CHAT LOG", databaseError.getMessage().toString());
                            }
                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        loadMessages();
    }
    private void sendmessage() {
        String message = emojiconEditText.getText().toString();
        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "enter message please", Toast.LENGTH_SHORT).show();
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("(dd/MM/yyyy) HH:mm:ss");
            Date date = new Date();
            final String strdate = dateFormat.format(date).toString();
            String current_user_ref = "messages/" + mcurrentuserid + "/" + mChatUser;
            String chat_user_ref = "messages/" + mChatUser + "/" + mcurrentuserid;
            DatabaseReference user_message_push = mrootref.child("messages").child(mcurrentuserid).child(mChatUser).push();
            String push_id = user_message_push.getKey();
            Map messagemap = new HashMap();
            messagemap.put("messages", message);
            messagemap.put("seen", false);
            messagemap.put("time", strdate);
            messagemap.put("from", mcurrentuserid);
            Map messageusermap = new HashMap<>();
            messageusermap.put(current_user_ref + "/" + push_id, messagemap);
            messageusermap.put(chat_user_ref + "/" + push_id, messagemap);
            //-------------------------------------------------------------------------
            emojiconEditText.setText("");
            //--------------------------------------------------------------------
            HashMap<String, String> notification = new HashMap<String, String>();
            notification.put("from", mCurrent_user.getUid());
            notification.put("type", "message");
            mNotification.child(mChatUser).push().setValue(notification);
            //--------------------------------------
            mrootref.updateChildren(messageusermap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d("CHAT LOG", databaseError.getMessage().toString());
                    }
                }
            });
        }
    }
    private void loadMessages() {
        final DatabaseReference messageRef = mrootref.child("messages").child(mcurrentuserid).child(mChatUser);
        messageRef.keepSynced(true);
        mAdapter = new MessageAdpater(Chat.this,Messages.class,R.layout.message_layout_receive,messageRef);
        messageRef.keepSynced(true);
        mAdapter.notifyDataSetChanged();
        mMessagesList.setAdapter(mAdapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backintent = new Intent(Chat.this,ProfileActivity.class);
        startActivity(backintent);
    }
}