package com.oskip.bitapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.GoogleApiClient;
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
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;
import retrofit2.http.Url;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;
/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFrag extends Fragment {
    RecyclerView recyclerViewOld;
    private static final int PICK_IMAGE_REQUEST0 = 234;
    ImageView imageView;
    Uri filePath;
    Uri resultsUri2;
    Bitmap thumb_bitmap2 = null;
    android.support.design.widget.FloatingActionButton floatingActionButtonpics;
    //--------------------------------------------------------------------------
    FirebaseAuth mAuth;
    private StorageReference storageReference;
    DatabaseReference musers;
    String currentuser;
    DatabaseReference postnotification;
    DatabaseReference eCoin;
    DatabaseReference mDatabase;
    DatabaseReference mDatabasecoin;
    GoogleApiClient mGoogleApiClient;
    //-----------------------------------------------------
    EmojiconEditText emojiconEditText;
    ImageView emojiImageView;
    ImageView submitButton;
    View rootView;
    EmojIconActions emojIcon;
    TextView ninecancel;
    StaggeredGridLayoutManager gridLayoutManager;
    private Query mQuerypics;
    Task<Uri> urlTask;
    public GalleryFrag() {
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerViewOld = view.findViewById(R.id.profilealloldpics);
        recyclerViewOld.setLayoutManager(gridLayoutManager);
        //----------------------------------------------------------
        ninecancel = view.findViewById(R.id.textView9cancel);
        ninecancel.setVisibility(View.INVISIBLE);
        ninecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiconEditText.setVisibility(View.INVISIBLE);
                submitButton.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                rootView.setVisibility(View.INVISIBLE);
                ninecancel.setVisibility(View.INVISIBLE);
                submitButton.setEnabled(true);
                emojiconEditText.setText("");
            }
        });
        floatingActionButtonpics = view.findViewById(R.id.addpicsmore);
        floatingActionButtonpics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST0);
            }
        });
        imageView = view.findViewById(R.id.imageView105);
        imageView.setVisibility(View.INVISIBLE);
        rootView = view.findViewById(R.id.relative_wall);
        rootView.setVisibility(View.INVISIBLE);
        emojiImageView = view.findViewById(R.id.emoji_btn);
        emojiImageView.setVisibility(View.INVISIBLE);
        submitButton = view.findViewById(R.id.submit_btn);
        submitButton.setVisibility(View.INVISIBLE);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eCoin.setValue("coin");
                emojiImageView.setVisibility(View.INVISIBLE);
                rootView.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                emojiconEditText.setVisibility(View.INVISIBLE);
                post();
                submitButton.setEnabled(true);
            }
        });
        emojiconEditText = view.findViewById(R.id.emojicon_edit_text);
        emojiconEditText.setVisibility(View.INVISIBLE);
        emojIcon = new EmojIconActions(getContext().getApplicationContext(), rootView, emojiconEditText, emojiImageView);
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
        //--------------------------------------------------------------------------
        mGoogleApiClient = new GoogleApiClient.Builder(getContext().getApplicationContext()).addApi(Auth.GOOGLE_SIGN_IN_API).build();
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser().getUid();
        postnotification = FirebaseDatabase.getInstance().getReference().child("notification");
        eCoin = FirebaseDatabase.getInstance().getReference().child("coin").child(currentuser).child("coin push").push();
        storageReference = FirebaseStorage.getInstance().getReference().child("wall uploads/");
        eCoin.keepSynced(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        mDatabase.keepSynced(true);
        mQuerypics = mDatabase.orderByChild("id").equalTo(currentuser);
        musers = FirebaseDatabase.getInstance().getReference().child("users").child(currentuser);
        //---------------------------------------------------------------------------
        imageView = view.findViewById(R.id.imageView105);
        imageView.setVisibility(View.INVISIBLE);
        mDatabasecoin = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        return view;
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Upload, OldUserAllPostHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Upload, OldUserAllPostHolder>(
                Upload.class,
                R.layout.allpics,
                OldUserAllPostHolder.class,
                mQuerypics) {
            @Override
            protected void populateViewHolder(final OldUserAllPostHolder viewHolder, Upload model, int position) {
                final String old = getRef(position).getKey();
                viewHolder.setuserupload(model.getThumb(), getApplicationContext());
            }
        };
        recyclerViewOld.setAdapter(firebaseRecyclerAdapter);
    }
    public static class OldUserAllPostHolder extends RecyclerView.ViewHolder {
        View mitemView;

        public OldUserAllPostHolder(final View itemView) {
            super(itemView);
            mitemView = itemView;
        }

        public void setuserupload(String thumb, Context ctx) {
            ImageView useroldpost = mitemView.findViewById(R.id.imageViewpicsAll);
            Picasso.with(ctx).load(thumb).placeholder(R.mipmap.circle_spin).into(useroldpost);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            CropImage.activity(filePath)
                    .setAspectRatio(4, 2)
                    .setMinCropWindowSize(2000, 450)
                    .start(getContext(), this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result2 = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultsUri2 = result2.getUri();
                File thumb_filepath2 = new File(resultsUri2.getPath());
                try {
                    thumb_bitmap2 = new Compressor(getApplicationContext().
                            getApplicationContext()).setMaxWidth(2000).setMaxHeight(450).setQuality(75)
                            .compressToBitmap(thumb_filepath2);
                    thumb_bitmap2 = MediaStore.Images.Media.getBitmap(getApplicationContext().
                            getContentResolver(), resultsUri2);
                    imageView.setImageBitmap(thumb_bitmap2);
                    imageView.setVisibility(View.VISIBLE);
                    emojiconEditText.setVisibility(View.VISIBLE);
                    submitButton.setVisibility(View.VISIBLE);
                    emojiImageView.setVisibility(View.VISIBLE);
                    rootView.setVisibility(View.VISIBLE);
                    ninecancel.setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void post() {
        if (resultsUri2 != null) {
            HashMap<String, String> notification = new HashMap<String, String>();
            notification.put("from", currentuser);
            notification.put("type", "a pic was posted.check it out first.");
            postnotification.push().setValue(notification);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            thumb_bitmap2.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            final byte[] thumb_byte2 = byteArrayOutputStream.toByteArray();
            final String imagecaption = emojiconEditText.getText().toString().trim();
            //------------------------------------------
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading");
            progressDialog.setCancelable(false);
            progressDialog.show();
            final StorageReference thumb_file = storageReference.child("thumb").child(".jpg" + UUID.randomUUID().toString());
            final StorageReference sRef = storageReference.child("image").child(".jpg" + UUID.randomUUID().toString());
            sRef.putFile(resultsUri2);
            final UploadTask uploadTask2 = thumb_file.putBytes(thumb_byte2);
            Task<Uri> urlTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return thumb_file.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        final String downloadUrl= downloadUri.toString();
                        //------------------------------------------------------------------
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss ");
                        Date date = new Date();
                        final String strdate = dateFormat.format(date).toString();
                        //----------------------------------------------------------------------------
                        final DatabaseReference imagepost = mDatabase.push();
                        uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                musers.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        imagepost.child("caption").setValue(imagecaption);
                                        imagepost.child("thumb").setValue(downloadUrl);
                                        imagepost.child("timestamp").setValue(strdate);
                                        imagepost.child("id").setValue(currentuser);
                                        imagepost.child("name").setValue(dataSnapshot.child("name").getValue());
                                        imagepost.child("thumb_image").setValue(dataSnapshot.child("thumb_image").getValue())
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        emojiconEditText.setVisibility(View.INVISIBLE);
                                                        submitButton.setVisibility(View.INVISIBLE);
                                                        imageView.setVisibility(View.INVISIBLE);
                                                        progressDialog.dismiss();
                                                        rootView.setVisibility(View.INVISIBLE);
                                                        submitButton.setEnabled(true);
                                                        emojiconEditText.setText("");
                                                    }
                                                });
                                    }
                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
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
                                        progressDialog.setMessage("Uploading progress " + ((int) progress) + " % ...");
                                    }
                                });
                    } else {

                    }
                }
            });

                   /* .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    final String downloadUrl= sRef.getDownloadUrl().toString();
                    final String thumb_downloadurlwall = thumb_file.getDownloadUrl().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss ");
                    Date date = new Date();
                    final String strdate = dateFormat.format(date).toString();
                    final DatabaseReference imagepost = mDatabase.push();
                    UploadTask uploadTask2 = thumb_file.putBytes(thumb_byte2);
                    uploadTask2.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            musers.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(final DataSnapshot dataSnapshot) {
                                    imagepost.child("caption").setValue(imagecaption);
                                    imagepost.child("thumb").setValue(thumb_downloadurlwall);
                                    imagepost.child("timestamp").setValue(strdate);
                                    imagepost.child("id").setValue(currentuser);
                                    imagepost.child("name").setValue(dataSnapshot.child("name").getValue());
                                    imagepost.child("thumb_image").setValue(dataSnapshot.child("thumb_image").getValue())
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                                emojiconEditText.setVisibility(View.INVISIBLE);
                                                submitButton.setVisibility(View.INVISIBLE);
                                                imageView.setVisibility(View.INVISIBLE);
                                                progressDialog.dismiss();
                                                rootView.setVisibility(View.INVISIBLE);
                                                submitButton.setEnabled(true);
                                                emojiconEditText.setText("");
                                        }
                                    });
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
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
                            progressDialog.setMessage("Uploading progress " + ((int) progress) + " % ...");
                        }
                    });
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "Please Select a Picture", Toast.LENGTH_LONG).show();
        }*/
        }
    }
}