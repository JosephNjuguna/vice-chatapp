package com.oskip.bitapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
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
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;
import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;

import static android.content.ContentValues.TAG;

public class NeonPost extends AppCompatActivity {
    Bitmap thumb_bitmap2neon = null;
    Uri resultsUrineon;
    private static final int PICK_IMAGE_REQUEST0 = 911;
    //-------------------------DATABASE-----------------------------
    private FirebaseAuth mAuth;
    DatabaseReference postnotification;
    DatabaseReference rootref;
    StorageReference storageref;
    DatabaseReference neonposting;
    DatabaseReference pageinfo;
    DatabaseReference neonnotification;
    private DatabaseReference retrieveforneonpost;
    //--------------------------------UI-----------------------------
    EmojiconEditText emojiconEditText;
    View rootView;
    ImageView emojiImageView;
    EmojIconActions emojIcon;
    //---------------------------------------------------------------
    View rootView2;
    ImageView emojiImageView2;
    EmojIconActions emojIcon2;
    EmojiconEditText emojiconEditText2;
    ImageView buttonposttoneon2;
    //----------------------------------------------------------------
    TextView canceltext;
    Button neonpic;
    ImageView buttonposttoneon;
    ImageView viewimage;
    Uri neonpost;
    //---------------------------------------------
    TextView pagename;
    TextView pageabout;
    TextView pagestarttime;
    TextView pageedit;
    ImageView coverpic;
    CircleImageView pagepic;
    protected ListView oldpostneon;
    NeonQueryAdapter queryadapter;
    private final List<NeonObject> neonlist = new ArrayList<>();
    LinearLayoutManager linearLayoutManager;
    String getid;
    Query mQuery;
    //--------------------------------------------

    Task<Uri> urlTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neon_post);
        //--------------------------------..firebase ui------------------------------------
        mAuth = FirebaseAuth.getInstance();
        getid = mAuth.getCurrentUser().getUid();
        rootref=FirebaseDatabase.getInstance().getReference();
        postnotification = FirebaseDatabase.getInstance().getReference().child("notification");
        storageref = FirebaseStorage.getInstance().getReference().child("neon page post/");
        //---------------------------------------------------------------------------------
        retrieveforneonpost = FirebaseDatabase.getInstance().getReference().child("neon page").child(getid);
        neonposting = FirebaseDatabase.getInstance().getReference().child("neon page post");
        mQuery =  neonposting.orderByChild("id").equalTo(getid);
        pageinfo = FirebaseDatabase.getInstance().getReference().child("neon page").child(getid);
        pageinfo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("pagetype")) {
                    String typepage = dataSnapshot.child("pagetype").getValue().toString();
                    pageabout.setText(typepage);
                }
                if (dataSnapshot.hasChild("pagename")) {
                    String  pagenametext = dataSnapshot.child("pagename").getValue().toString();
                    pagename.setText(pagenametext);
                }
                if (dataSnapshot.hasChild("pagepic")) {
                    String image = dataSnapshot.child("pagepic").getValue().toString();
                    Picasso.with(NeonPost.this).load(image).placeholder(R.mipmap.circle_spin).into(coverpic);
                }
                if (dataSnapshot.hasChild("pagepic")) {
                    String image = dataSnapshot.child("pagepic").getValue().toString();
                    Picasso.with(NeonPost.this).load(image).placeholder(R.mipmap.circle_spin).into(pagepic);
                }
                if (dataSnapshot.hasChild("started")){
                    String startedpage = dataSnapshot.child("started").getValue().toString();
                    pagestarttime.setText(startedpage);
                }else{
                    pagestarttime.setText("");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        //------------------------------------------------------------------------------------------
        oldpostneon = findViewById(R.id.recyclerViewpageoldpost);
        linearLayoutManager = new LinearLayoutManager(NeonPost.this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        oldpostneon.setAdapter(queryadapter);
        //-----------------------------------------------------------------------------
        coverpic = findViewById(R.id.imageView3pagecoverpic);
        pagepic = findViewById(R.id.imageView5pagepic);
        pagename = findViewById(R.id.textViewpagename);
        pageabout = findViewById(R.id.textView3about);
        pagestarttime= findViewById(R.id.textView4started);
        pageedit = findViewById(R.id.button4editpage);
        pageedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent editpageintent = new Intent(NeonPost.this,NeonPageSetUp.class);
               startActivity(editpageintent);
            }
        });
        //------------------------------------------------------------------------------------------
        rootView = findViewById(R.id.neonpicpost);
        rootView.setVisibility(View.INVISIBLE);
        rootView2 = findViewById(R.id.thoughtpost);
        //------------------------------------------------
        emojiImageView = findViewById(R.id.emoji_btnpost);
        emojiImageView.setVisibility(View.INVISIBLE);
        emojiconEditText = findViewById(R.id.emojicon_edit_neoncaptiontext);
        emojiconEditText.setVisibility(View.INVISIBLE);
        emojIcon = new EmojIconActions(NeonPost.this, rootView, emojiconEditText, emojiImageView);
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
        viewimage = findViewById(R.id.pictopost);
        viewimage.setVisibility(View.INVISIBLE);
        buttonposttoneon = findViewById(R.id.submit_btn);
        buttonposttoneon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.setVisibility(View.INVISIBLE);
                emojiImageView.setVisibility(View.INVISIBLE);
                emojiconEditText.setVisibility(View.INVISIBLE);
                buttonposttoneon.setVisibility(View.INVISIBLE);
                buttonposttoneon.setEnabled(false);
                neonpic.setEnabled(true);
                posttoneon();
            }
        });
        neonpic = findViewById(R.id.button3postpic);
        neonpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST0);
                neonpic.setEnabled(false);
                emojiconEditText.setEnabled(true);
                coverpic.setVisibility(View.INVISIBLE);
                pagepic.setVisibility(View.INVISIBLE);
                buttonposttoneon.setVisibility(View.VISIBLE);
                emojiconEditText.setVisibility(View.VISIBLE);
                emojiImageView.setVisibility(View.VISIBLE);
                rootView.setVisibility(View.VISIBLE);
                viewimage.setVisibility(View.VISIBLE);
            }
        });
        canceltext = findViewById(R.id.textView9cancel);
        canceltext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiconEditText.setText("");
                rootView.setVisibility(View.INVISIBLE);
                rootView.setEnabled(false);
                viewimage.setVisibility(View.INVISIBLE);
            }
        });
        //----------------------------------------------------------------------------------------
        emojiImageView2 = findViewById(R.id.emoji_btn2);
        emojiconEditText2 = findViewById(R.id.emojicon_edit_neoncaptiontext2);
        emojIcon2 = new EmojIconActions(NeonPost.this, rootView2, emojiconEditText2, emojiImageView2);
        emojIcon2.ShowEmojIcon();
        emojIcon2.setIconsIds(R.mipmap.keyboard, R.mipmap.emo);
        emojIcon2.setKeyboardListener(new EmojIconActions.KeyboardListener() {
            @Override
            public void onKeyboardOpen() {
                Log.e(TAG, "Keyboard opened!");
            }
            @Override
            public void onKeyboardClose() {
                Log.e(TAG, "Keyboard closed");
            }
        });
        buttonposttoneon2 = findViewById(R.id.submit_btn2);
        buttonposttoneon2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String thought = emojiconEditText2.getText().toString();
                emojiconEditText2.setText("");
                thoughtpost(thought);
            }
        });
        loadMessages();
    }
    private void thoughtpost(final String thought) {
        if (!TextUtils.isEmpty(thought)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss (dd/MM/yyyy)");
            Date date = new Date();
            final String strdate = dateFormat.format(date).toString();
            final ProgressDialog progressDialog = new ProgressDialog(NeonPost.this);
            progressDialog.setMessage("posting");
            progressDialog.show();
            progressDialog.setCancelable(false);
            final DatabaseReference thoughtpost =neonposting.push();
            retrieveforneonpost.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    thoughtpost.child("caption").setValue(thought);
                    thoughtpost.child("timestamp").setValue(strdate);
                    thoughtpost.child("type").setValue("thought");
                    thoughtpost.child("id").setValue(dataSnapshot.child("pageid").getValue());
                    thoughtpost.child("pgtype").setValue(dataSnapshot.child("pagetype").getValue());
                    thoughtpost.child("pagename").setValue(dataSnapshot.child("pagename").getValue());
                    thoughtpost.child("pagepic").setValue(dataSnapshot.child("pagepic").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                Toast.makeText(NeonPost.this, "posted", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                                startActivity(new Intent(getApplicationContext().getApplicationContext(), ProfileActivity.class));
                            }
                        }
                    });
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        } else {
            Toast.makeText(getApplicationContext().getApplicationContext(), "Enter a thought please", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            neonpost = data.getData();
            CropImage.activity(neonpost)
                    .setAspectRatio(5, 3)
                    .setMinCropWindowSize(2000, 450)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result2 = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultsUrineon = result2.getUri();
                File thumb_filepath2 = new File(resultsUrineon.getPath());
                try {
                    thumb_bitmap2neon = new Compressor(this).setMaxWidth(2000).setMaxHeight(550).setQuality(75).compressToBitmap(thumb_filepath2);
                    thumb_bitmap2neon = MediaStore.Images.Media.getBitmap(getContentResolver(), resultsUrineon);
                    viewimage.setImageBitmap(thumb_bitmap2neon);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void posttoneon() {
        if (resultsUrineon != null) {
            HashMap<String, String> notification = new HashMap<String, String>();
            notification.put("from",getid);
            notification.put("type", "a pic was posted.check it out first.");
            postnotification.push().setValue(notification);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumb_bitmap2neon.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            final byte[] thumb_byte2 = byteArrayOutputStream.toByteArray();
            final String neonpostCaption = emojiconEditText.getText().toString();
            //--------------------------------------------------------------------------

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("uploading pic");
            progressDialog.show();
            StorageReference sRefe = storageref.child("image").child(UUID.randomUUID().toString());
            final StorageReference thumb_file_neon = storageref.child( UUID.randomUUID().toString());
            sRefe.putFile(resultsUrineon).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss (dd/MM/yyyy)");
                    Date date = new Date();
                    final String strdate = dateFormat.format(date).toString();
                    final DatabaseReference imagepostneon = neonposting.push();
                    UploadTask uploadTask22 = thumb_file_neon.putBytes(thumb_byte2);
                    urlTask = uploadTask22.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return thumb_file_neon.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUrineon = task.getResult();
                                final String downurin = downloadUrineon.toString();
                                retrieveforneonpost.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        imagepostneon.child("caption").setValue(neonpostCaption);
                                        imagepostneon.child("type").setValue("image");
                                        imagepostneon.child("timestamp").setValue(strdate);
                                        imagepostneon.child("url").setValue(downurin);
                                        imagepostneon.child("id").setValue(dataSnapshot.child("pageid").getValue());
                                        imagepostneon.child("pgtype").setValue(dataSnapshot.child("pagetype").getValue());
                                        imagepostneon.child("pagename").setValue(dataSnapshot.child("pagename").getValue());
                                        imagepostneon.child("pagepic").setValue(dataSnapshot.child("pagepic").getValue());
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    });
                    uploadTask22.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            neonnotification();
                            Toast.makeText(getApplicationContext(), "pic uploaded successfully ", Toast.LENGTH_LONG).show();
                            Intent finishi=new Intent(NeonPost.this,ProfileActivity.class);
                            startActivity(finishi);
                            finish();
                        }
                    })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        //displaying the upload progress
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        progressDialog.setMessage("Uploading progress" + ((int) progress) + "%...");
                    }
                });
                }
            });

        } else {
            Toast.makeText(getApplicationContext(), "Please Select a Picture", Toast.LENGTH_LONG).show();
        }
    }
    private void neonnotification() {
        neonnotification = FirebaseDatabase.getInstance().getReference().child("notification");
        HashMap<String, String> notification = new HashMap<String, String>();
        notification.put("from", mAuth.getCurrentUser().getUid());
        notification.put("type", "Hey" + "\uD83D\uDE18" + "A Blogger posted in Neon Page..Be the first to check it..");
        neonnotification.push().setValue(notification);
    }
    @Override
    protected void onStart() {
        super.onStart();
        rootref = FirebaseDatabase.getInstance().getReference().child("users").child(getid);
        rootref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("blogger")) {
                    Toast.makeText(NeonPost.this, "post to your page", Toast.LENGTH_LONG).show();
                } else {
                    Intent neonpage = new Intent(NeonPost.this, NeonPageSetUp.class);
                    neonpage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(neonpage);
                    finish();
                    Toast.makeText(NeonPost.this, "Create a page to promote your Website,Blog,Youtube channel and Social Platforms", Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void loadMessages() {
        queryadapter = new NeonQueryAdapter(NeonPost.this, NeonObject.class, R.layout.neontextpost,mQuery);
        queryadapter.notifyDataSetChanged();
        oldpostneon.setAdapter(queryadapter);
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(NeonPost.this, ProfileActivity.class);
        startActivity(back);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent back = new Intent(NeonPost.this, ProfileActivity.class);
        startActivity(back);
    }
}