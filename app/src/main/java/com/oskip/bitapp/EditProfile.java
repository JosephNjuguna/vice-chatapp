package com.oskip.bitapp;

import android.app.ProgressDialog;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import id.zelory.compressor.Compressor;

import static com.facebook.FacebookSdk.getApplicationContext;

public class EditProfile extends AppCompatActivity {
    private static final String TAG = EditProfile.class.getSimpleName();
    private static final int PICK_PROFILE_IMAGE = 890;
    EditText editProfileName;
    EditText editProfileCountry;
    EditText editProfilePhoneNumber;
    TextView savename;
    TextView savecountry;
    Button savecontinue;
    Button saveEditButton;
    ImageView imageViewSetImageProfile;
    ImageView cambutton;
    Uri profile;
    String name;
    String country;
    String image;
    Bitmap thumb_bitmap = null;
    Uri resultUri;
    String uid;
    //Uri downloadUriedit;
    //---------------------------------------------------------
    DatabaseReference mDatabaseProfilePic;
    StorageReference profilepicdata;
    protected DatabaseReference mDatabaseUser;
    FirebaseUser userinfo;
    protected DatabaseReference userinfoData;
    private FirebaseAuth.AuthStateListener authStateListener;
    String downurie;
    Task<Uri> urlTaske;
    Map<String ,Object>updatehashmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        //-------------------edit ui components------------------
        cambutton = findViewById(R.id.imageButtoncam);
        cambutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageselection();
            }
        });
        saveEditButton = findViewById(R.id.save_edit_button);
        saveEditButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savedppic();
            }
        });
        savecontinue = findViewById(R.id.button7);
        savecontinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent prointent = new Intent(EditProfile.this, ProfileActivity.class);
                startActivity(prointent);
                finish();
            }
        });
        editProfileName = findViewById(R.id.profile_name);
        editProfileCountry = findViewById(R.id.profile_country);
        editProfilePhoneNumber = findViewById(R.id.profile_phone);
        editProfilePhoneNumber.setEnabled(false);
        userinfo = FirebaseAuth.getInstance().getCurrentUser();
        uid = userinfo.getUid();
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mDatabaseUser.keepSynced(true);
        mDatabaseProfilePic = FirebaseDatabase.getInstance().getReference("users").child(uid);
        profilepicdata = FirebaseStorage.getInstance().getReference().child("profile pics").child(uid);
        imageViewSetImageProfile = findViewById(R.id.imageViewSetProfile);
        imageViewSetImageProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageselection();
                }
        });
        //------------------------------UPLOAD NAMES AND ETC---------------------------------------------------------------
        mDatabaseUser = FirebaseDatabase.getInstance().getReference("users").child(uid);
        mDatabaseUser.keepSynced(true);
        mDatabaseUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.child("name").getValue().toString();
                if(dataSnapshot.hasChild("country")){
                    country = dataSnapshot.child("country").getValue().toString();
                    editProfileCountry.setText(country);
                }else{
                    editProfileCountry.setHint("country");
                }
                if (dataSnapshot.hasChild("phone number")) {
                    String phone = dataSnapshot.child("phone number").getValue().toString();
                    editProfilePhoneNumber.setText(phone);
                }else{
                    String email = dataSnapshot.child("email").getValue().toString();
                    editProfilePhoneNumber.setText(email);
                }
                if (dataSnapshot.hasChild("thumbimage")) {
                    String image = dataSnapshot.child("thumbimage").getValue().toString();
                    Picasso.with(EditProfile.this).load(image).placeholder(R.mipmap.circle_spin).into(imageViewSetImageProfile);
                }
                editProfileName.setHint(name);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditProfile.this, "error", Toast.LENGTH_SHORT).show();
            }
        });
        userinfoData = FirebaseDatabase.getInstance().getReference("users").child(uid);
        savename = findViewById(R.id.textView70);
        savename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileName = editProfileName.getText().toString();
                if (TextUtils.isEmpty(profileName)) {
                    Toast.makeText(EditProfile.this, "please enter name", Toast.LENGTH_SHORT).show();
                } else {
                    userinfoData.child("name").setValue(profileName);
                    Toast.makeText(EditProfile.this, "successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
        savecountry = findViewById(R.id.textView73);
        savecountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String profileCountry = editProfileCountry.getText().toString();
                if (TextUtils.isEmpty(profileCountry)) {
                    Toast.makeText(EditProfile.this, "please enter country", Toast.LENGTH_SHORT).show();
                } else {
                    userinfoData.child("country").setValue(profileCountry);
                    Toast.makeText(EditProfile.this, "successful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void imageselection() {
        Intent intentPro = new Intent();
        intentPro.setType("image/*");
        intentPro.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intentPro, "Select Picture"), PICK_PROFILE_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PROFILE_IMAGE && resultCode == RESULT_OK) {
            profile = data.getData();
            CropImage.activity(profile)
                    .setAspectRatio(2, 2)
                    .setMinCropWindowSize(300, 300)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                File thumb_filepath = new File(resultUri.getPath());
                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxWidth(500).setMaxHeight(500).setQuality(75)
                            .compressToBitmap(thumb_filepath);
                    thumb_bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().
                            getContentResolver(), resultUri);
                    imageViewSetImageProfile.setImageBitmap(thumb_bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void savedppic() {
        if (resultUri != null) {
            final ProgressDialog progressDailogProfile = new ProgressDialog(EditProfile.this);
            progressDailogProfile.setMessage(" Updating Profile Photo...");
            progressDailogProfile.show();
            progressDailogProfile.setCancelable(false);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            final byte[] thumb_byte = byteArrayOutputStream.toByteArray();
            final StorageReference ref = profilepicdata;
            final StorageReference thumb_file_path = profilepicdata;
            ref.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String id = userinfo.getUid();
                    final String profileEmail = userinfo.getEmail();
                    UploadTask uploadTask20 = thumb_file_path.putBytes(thumb_byte);
                     urlTaske = uploadTask20.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return thumb_file_path.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUriedit = task.getResult();
                                downurie = downloadUriedit.toString();
                                updatehashmap = new HashMap<>();
                                updatehashmap.put("thumbimage",downurie);
                                updatehashmap.put("id",id);
                                updatehashmap.put("email",profileEmail);
                                mDatabaseProfilePic.updateChildren(updatehashmap);
                            }
                        }
                    });
                    uploadTask20.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(EditProfile.this, "Updated successfully.", Toast.LENGTH_SHORT).show();
                            progressDailogProfile.dismiss();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            progressDailogProfile.dismiss();
                            Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            //displaying the upload progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDailogProfile.setMessage("Uploading progress.. " + ((int) progress) + " % ...");
                        }
                    });
                }
            });
        }

    }
}