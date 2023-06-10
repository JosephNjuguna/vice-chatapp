package com.oskip.bitapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class NeonPageSetUp extends AppCompatActivity {
    private static final int NEON_PROFILE_IMAGE = 456;
    CircleImageView neonimage;
    EditText pageName;
    EditText pageownerssname;
    EditText pagetype;
    Button neonuserprofile;
    private Uri neonprofile;
    StorageReference neonpicdata;
    private DatabaseReference mdataneon;
    private DatabaseReference neonref;
    FirebaseUser neonuser;
    Bitmap thumb_bitmap2 = null;
    private DatabaseReference mDatabase;
    DatabaseReference musers;
    FirebaseUser mCurrentUser;
    String uid;
    Button cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neon_page_set_up);
        cancel = findViewById(R.id.buttoncancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pageName.setText("");
                pageownerssname.setText("");
                pagetype.setText("");
                Intent toneon = new Intent(NeonPageSetUp.this, ProfileActivity.class);
                toneon.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(toneon);
                finish();
            }
        });
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mCurrentUser.getUid();
        neonimage = findViewById(R.id.imageView10);
        neonimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPro = new Intent();
                intentPro.setType("image/*");
                intentPro.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intentPro, "Select Picture"), NEON_PROFILE_IMAGE);
            }
        });
        pageName = findViewById(R.id.editText);
        pageownerssname = findViewById(R.id.editText6);
        pagetype = findViewById(R.id.editText7);
        pagetype.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Entertainment","Business","CryptoCurrency","Trending Fashion","e-Gaming","Technology","Gadgets","Education","Health","Music"};
                AlertDialog.Builder builder = new AlertDialog.Builder(NeonPageSetUp.this);
                builder.setTitle("Select Notificatio Option");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            pagetype.setText("Entertainment");
                        }
                        if (which == 1) {
                            pagetype.setText("Business");
                        }
                        if (which == 2) {
                            pagetype.setText("CryptoCurrency");
                        }
                        if (which == 3) {
                            pagetype.setText("Trending Fashion");
                        }
                        if (which == 4) {
                            pagetype.setText("e-Gaming");
                        }
                        if (which == 5) {
                            pagetype.setText("Technology");
                        }
                        if (which == 6) {
                            pagetype.setText("Gadgets");
                        }
                        if (which == 7) {
                            pagetype.setText("Health");
                        }
                        if (which == 8) {
                            pagetype.setText("Music");
                        }
                        if (which == 8) {
                            pagetype.setText("News");
                        }
                    }
                });
                builder.show();
            }
        });
        neonuserprofile = findViewById(R.id.button11);
        neonuser = FirebaseAuth.getInstance().getCurrentUser();
        final String user_id = neonuser.getUid();
        neonpicdata = FirebaseStorage.getInstance().getReference().child("page pic");
        neonref = FirebaseDatabase.getInstance().getReference().child("neon page").child(user_id);
        neonuserprofile = findViewById(R.id.button11);
        neonuserprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createpage();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEON_PROFILE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            neonprofile = data.getData();
            CropImage.activity(neonprofile)
                    .setAspectRatio(2, 2)
                    .setMinCropWindowSize(300, 300)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result2 = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                neonprofile = result2.getUri();
                File thumb_filepath2 = new File(neonprofile.getPath());
                try {
                    thumb_bitmap2 = new Compressor(this).setMaxWidth(300).setMaxHeight(300).setQuality(75).compressToBitmap(thumb_filepath2);
                    thumb_bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), neonprofile);
                    neonimage.setImageBitmap(thumb_bitmap2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void createpage() {
        if (neonprofile != null) {
            final String pagename = pageName.getText().toString();
            final String pagewriter = pageownerssname.getText().toString();
            final String neonwriterbustype = pagetype.getText().toString();
            final String id = neonuser.getUid();
            final String profileEmail = neonuser.getEmail();
            final String phonenumber = neonuser.getPhoneNumber();
            if (TextUtils.isEmpty(pagename) || TextUtils.isEmpty(pagewriter) || TextUtils.isEmpty(neonwriterbustype)) {
                Toast.makeText(NeonPageSetUp.this, "please fill the whole form", Toast.LENGTH_LONG).show();
            } else {
                //---------------------------UI-------------------------------------------
                final ProgressDialog progressDailogProfile = new ProgressDialog(NeonPageSetUp.this);
                progressDailogProfile.setMessage("Creating Your Page...");
                progressDailogProfile.show();
                //---------------------------page pic----------------------------------------------------
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap2.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
                final byte[] thumb_byte2 = byteArrayOutputStream.toByteArray();
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss (dd/MM/yyyy)");
                Date date = new Date();
                final String strdate = dateFormat.format(date).toString();
                //---------------------------------------------------------------------------------------
                String uid = neonuser.getUid();
                final StorageReference ref = neonpicdata.child("image").child(uid);
                final StorageReference ref2 = neonpicdata.child("thumb").child(uid);
                ref.putFile(neonprofile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        UploadTask uploadTask2 = ref2.putBytes(thumb_byte2);
                        Task<Uri> urlTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                          if (!task.isSuccessful()) {
                              throw task.getException();
                          }
                          return ref2.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                          if (task.isSuccessful()) {
                              Uri downloadUri = task.getResult();
                              final String downurip = downloadUri.toString();
                              Map<String, String> userMap = new HashMap<>();
                              userMap.put("pagename", pagename);
                              userMap.put("neonwriter", pagewriter);
                              userMap.put("pagetype", neonwriterbustype);
                              userMap.put("pageid", id);
                              userMap.put("pagepic", downurip);
                              userMap.put("pageemail", profileEmail);
                              userMap.put("pagenumber", phonenumber);
                              userMap.put("started", strdate);
                              neonref.setValue(userMap);
                          } else {
                              Toast.makeText(NeonPageSetUp.this, "Failed Try Again Please", Toast.LENGTH_SHORT).show();
                          }
                          }
                        });
                        uploadTask2.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                           @Override
                           public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_walltask2) {
                               //----------------------------------------------------------
                               neonuser = FirebaseAuth.getInstance().getCurrentUser();
                               final String uid = neonuser.getUid();
                               mdataneon = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                               Map updatehashmap = new HashMap();
                               updatehashmap.put("blogger", "blogger");
                               mdataneon.updateChildren(updatehashmap);
                               progressDailogProfile.dismiss();
                               Toast.makeText(NeonPageSetUp.this, "Successful", Toast.LENGTH_SHORT).show();
                               Intent prointent = new Intent(NeonPageSetUp.this, ProfileActivity.class);
                               prointent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                               startActivity(prointent);
                               finish();

                           }
                        });
                    }
                });
            }
        } else {
            Toast.makeText(NeonPageSetUp.this, "select a picture please", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    protected void onStart () {
            super.onStart();
            musers = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
            musers.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("blogger")) {
                        Intent neonpage = new Intent(NeonPageSetUp.this, NeonPost.class);
                        neonpage.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(neonpage);
                        finish();
                    } else {
                        createpage();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
}