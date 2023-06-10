package com.oskip.bitapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.github.clans.fab.FloatingActionMenu;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
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

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

public class WallFragMent extends Fragment implements GoogleApiClient.OnConnectionFailedListener {
    protected RecyclerView recyclerView99;
    LinearLayoutManager linearLayoutManager;
    FloatingActionButton addpic;
    String currentuser;
    FloatingActionMenu menufab;
    FloatingActionButton postpic;
    FloatingActionButton postinvite;
    FloatingActionButton postcreatepage;
    private static final int REQUEST_INVITE = 1000;
    private static final int PICK_IMAGE_REQUEST0 = 234;
    private static final int PICK_VIDEO_REQUEST0 = 24;
    ImageView imageView;
    Uri filePath;
    Uri resultsUri2;
    Uri videoPath;
    Bitmap thumb_bitmap2 = null;
    public FirebaseAuth mAuth;
    StorageReference storageReference;
    DatabaseReference musers;
    FirebaseUser mCurrentUser;
    DatabaseReference postnotification;
    DatabaseReference eCoin;
    DatabaseReference likenotification;
    public DatabaseReference mDatabase;
    private DatabaseReference mDatabaselike;
    private DatabaseReference mDatabasecoin;
    AdView mAdView;
    private boolean mProcesslike = false;
    private boolean mProcesscoin = false;
    GoogleApiClient mGoogleApiClient;
    //---------------------------------------------
    EmojiconEditText emojiconEditText;
    ImageView emojiImageView;
    ImageView submitButton;
    View rootView;
    EmojIconActions emojIcon;
    TextView ninecancel;
    //-------------------------
    Task<Uri> urlTask;
    public WallFragMent() {
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wall, container, false);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mDatabaselike = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        mDatabasecoin = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        //--------------------------------------------------------------------------------
        recyclerView99 = view.findViewById(R.id.list_fragment);
        recyclerView99.setLayoutManager(linearLayoutManager);
        recyclerView99.setHasFixedSize(true);
        mDatabase = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        mDatabase.keepSynced(true);
        ninecancel = view.findViewById(R.id.textView9cancel);
        ninecancel.setVisibility(View.INVISIBLE);
        ninecancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiconEditText.setVisibility(View.INVISIBLE);
                submitButton.setVisibility(View.INVISIBLE);
                imageView.setVisibility(View.INVISIBLE);
                menufab.setVisibility(View.VISIBLE);
                rootView.setVisibility(View.INVISIBLE);
                ninecancel.setVisibility(View.INVISIBLE);
                submitButton.setEnabled(true);
                emojiconEditText.setText("");
            }
        });
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
        mGoogleApiClient = new GoogleApiClient.Builder(getContext().getApplicationContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();
        //-----------------------------------------------------
        mAuth = FirebaseAuth.getInstance();
        currentuser = mAuth.getCurrentUser().getUid();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        postnotification = FirebaseDatabase.getInstance().getReference().child("notification");
        eCoin = FirebaseDatabase.getInstance().getReference().child("coin").child(currentuser).child("coin push").push();
        storageReference = FirebaseStorage.getInstance().getReference().child("wall uploads");
        eCoin.keepSynced(true);
        //===================================================================================================
        imageView = view.findViewById(R.id.imageView105);
        imageView.setVisibility(View.INVISIBLE);
        musers = FirebaseDatabase.getInstance().getReference().child("users").child(currentuser);
        menufab = view.findViewById(R.id.addpost);
        postcreatepage = view.findViewById(R.id.floatingpostcreatepage);
        postcreatepage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "Create a page to promote your Website,Blog,Youtube channel and Social Platforms", Toast.LENGTH_LONG).show();
                Intent toneon = new Intent(getContext().getApplicationContext(), NeonPost.class);
                startActivity(toneon);
            }
        });
        postpic = view.findViewById(R.id.floatingpostpic);
        postpic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST0);
            }
        });
        postinvite = view.findViewById(R.id.floatingpostinvitefriends);
        postinvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendInvitation();
            }
        });
        likenotification = FirebaseDatabase.getInstance().getReference().child("notification").push();
        MobileAds.initialize(getContext(), "ca-app-pub-1211241739368711~3509108984");
        mAdView = view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        return view;
    }
    private void sendInvitation() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /*if (requestCode == PICK_VIDEO_REQUEST0 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                videoPath = data.getData();
                String path = data.getData().toString();
                videoView.setVideoPath(path);
                videoView.requestFocus();
                videoView.start();
            } catch (Exception ec) {
                ec.printStackTrace();
            }
        }*/
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
                    thumb_bitmap2 = new Compressor(getApplicationContext()
                            .getApplicationContext()).setMaxWidth(2000).setMaxHeight(450).setQuality(75)
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
                    menufab.setVisibility(View.INVISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }
    private void post() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        if (resultsUri2 != null) {
            if (isOnline()){
                HashMap<String, String> notification = new HashMap<String, String>();
                notification.put("from", currentuser);
                notification.put("type", "a pic was posted.check it out first.");
                postnotification.push().setValue(notification);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap2.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
                final byte[] thumb_byte2 = byteArrayOutputStream.toByteArray();
                final String imagecaption = emojiconEditText.getText().toString().trim();
                //------------------------------------------
                progressDialog.setTitle("Uploading");
                progressDialog.setCancelable(false);
                progressDialog.show();
                final StorageReference thumb_file = storageReference.child(UUID.randomUUID().toString());
                StorageReference sRef = storageReference.child("image").child(UUID.randomUUID().toString());
                sRef.putFile(resultsUri2).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss ");
                    Date date = new Date();
                    final String strdate = dateFormat.format(date).toString();
                    final DatabaseReference imagepost = mDatabase.push();
                    UploadTask uploadTask21 = thumb_file.putBytes(thumb_byte2);
                    urlTask = uploadTask21.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return thumb_file.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();
                                final String downurinwall = downloadUri.toString();
                                musers.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(final DataSnapshot dataSnapshot) {
                                        imagepost.child("caption").setValue(imagecaption);
                                        imagepost.child("type").setValue("image");
                                        imagepost.child("timestamp").setValue(strdate);
                                        imagepost.child("id").setValue(currentuser);
                                        imagepost.child("thumb").setValue(downurinwall);
                                        imagepost.child("name").setValue(dataSnapshot.child("name").getValue());
                                        imagepost.child("thumbimage").setValue(dataSnapshot.child("thumbimage").getValue());
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                    }
                                });
                            }
                        }
                    });
                    uploadTask21.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot2) {
                           emojiconEditText.setVisibility(View.INVISIBLE);
                           submitButton.setVisibility(View.INVISIBLE);
                           imageView.setVisibility(View.INVISIBLE);
                           progressDialog.dismiss();
                           menufab.setVisibility(View.VISIBLE);
                           rootView.setVisibility(View.INVISIBLE);
                           submitButton.setEnabled(true);
                           emojiconEditText.setText("");
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
                final ProgressDialog progressDialog2 = new ProgressDialog(getContext());
                progressDialog2.setTitle("Check Your Internet Connection");
                progressDialog2.setCancelable(true);
                progressDialog2.show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Please Select a Picture", Toast.LENGTH_LONG).show();
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (isOnline()) {
            Query wallquery = mDatabase.orderByChild("timestamp");
            Toast.makeText(getApplicationContext(), "Good Connection", Toast.LENGTH_LONG).show();
            FirebaseRecyclerAdapter<Upload, WallHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Upload, WallHolder>(
                    Upload.class,
                    R.layout.insert,
                    WallHolder.class,
                    wallquery) {
                @Override
                protected void populateViewHolder(final WallHolder viewHolder, final Upload model, int position) {
                    final String post_key = getRef(position).getKey();
                    viewHolder.setuseruploadthumb(model.getThumb(), getContext().getApplicationContext());
                    viewHolder.setcaption(model.getCaption());
                    viewHolder.setname(model.getUsername());
                    viewHolder.setuserdpthumb(model.getThumb_image(), getContext().getApplicationContext());
                    viewHolder.userdp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                        }
                    });
                    viewHolder.setCommentCount(model.getComments_count());
                    viewHolder.setheartCount(model.getHearts_count());
                    viewHolder.setlike(post_key);
                    viewHolder.setheartlike(post_key);
                    viewHolder.setTimewall(model.getTimestamp());
                    viewHolder.setlikesCount(model.getLikes_count());
                    viewHolder.privatetext.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent recyclerintent = new Intent(getApplicationContext(), ToRecyclerOnClick.class);
                            recyclerintent.putExtra("post_id", post_key);
                            startActivity(recyclerintent);
                        }
                    });
                    viewHolder.userpic.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent recyclerintent = new Intent(getApplicationContext(), ToRecyclerOnClick.class);
                            recyclerintent.putExtra("post_id", post_key);
                            startActivity(recyclerintent);
                        }
                    });
                    viewHolder.comment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent commentintent = new Intent(getApplicationContext(), ToRecyclerOnClick.class);
                            commentintent.putExtra("post_id", post_key);
                            startActivity(commentintent);
                        }
                    });
                    viewHolder.wallbuttoncomment.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent commentintent = new Intent(getApplicationContext(), ToRecyclerOnClick.class);
                            commentintent.putExtra("post_id", post_key);
                            startActivity(commentintent);
                        }
                    });
                    viewHolder.buttonlike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            like(post_key);
                            coin(post_key);
                            likenotify();
                        }
                    });
                    viewHolder.heartlike.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            heart(post_key);
                            haertnotify();
                        }
                    });
                }
            };
            recyclerView99.scrollToPosition(firebaseRecyclerAdapter.getItemCount()-1);
            recyclerView99.setAdapter(firebaseRecyclerAdapter);
            firebaseRecyclerAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(getApplicationContext(), "Check Your Internet Connection", Toast.LENGTH_LONG).show();
        }
    }
    private void haertnotify() {
        HashMap<String, String> notification = new HashMap<String, String>();
        notification.put("from", currentuser);
        notification.put("type", "a pic was loved.Show some love too.");
        postnotification.push().setValue(notification);
    }
    private void heart(final String post_key) {
        mProcesslike = true;
        mDatabaselike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcesslike) {
                    if (dataSnapshot.child(post_key).child("heart").hasChild(currentuser)) {
                        mDatabaselike.child(post_key).child("heart").child(currentuser).removeValue();
                        mProcesslike = false;
                    } else {
                        mDatabaselike.child(post_key).child("heart").child(currentuser).setValue("name");
                        mProcesslike = false;
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void likenotify() {
        HashMap<String, String> notification = new HashMap<String, String>();
        notification.put("from", currentuser);
        notification.put("type", "a pic was liked.like it too first.");
        postnotification.push().setValue(notification);
    }
    private void coin(final String post_key) {
        mProcesscoin = true;
        mDatabasecoin.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcesscoin) {
                    if (dataSnapshot.child(post_key).child("coin").hasChild(currentuser)) {
                        mDatabasecoin.child(post_key).child("coin").child(currentuser).removeValue();
                        mProcesscoin = false;
                    } else {
                        mDatabasecoin.child(post_key).child("coin").child(currentuser).setValue("coin");
                        mProcesscoin = false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void like(final String post_key) {
        mProcesslike = true;
        mDatabaselike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcesslike) {
                    if (dataSnapshot.child(post_key).child("like").hasChild(currentuser)) {
                        mDatabaselike.child(post_key).child("like").child(currentuser).removeValue();
                        mProcesslike = false;
                    } else {
                        mDatabaselike.child(post_key).child("like").child(currentuser).setValue("name");
                        mProcesslike = false;
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    public static class WallHolder extends RecyclerView.ViewHolder {
        ImageView buttonlike;
        ImageView comment;
        ImageView heartlike;
        public FirebaseAuth mAuth;
        private DatabaseReference mDatabaselike;
        View mitemView;
        ImageView userpic;
        ImageView userdp;
        ImageView dots;
        ImageView privatetext;
        Button wallbuttoncomment;
        public WallHolder(final View itemView) {
            super(itemView);
            mitemView = itemView;
            wallbuttoncomment= mitemView.findViewById(R.id.wallcomment);
            dots = itemView.findViewById(R.id.imageView25dots);
            privatetext = itemView.findViewById(R.id.imageView27text);
            comment = itemView.findViewById(R.id.comments);
            buttonlike = itemView.findViewById(R.id.Like);
            heartlike = itemView.findViewById(R.id.heartLike);
            mAuth = FirebaseAuth.getInstance();
            mDatabaselike = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        }
        public void setcaption(String caption) {
            TextView userNAME = mitemView.findViewById(R.id.textViewName);
            userNAME.setText(caption);
        }
        public void setname(String name) {
            TextView user = mitemView.findViewById(R.id.post_username);
            user.setText(name);
        }
        public void setTimewall(String timestamp) {
            TextView dateView = mitemView.findViewById(R.id.textView82);
            dateView.setText(timestamp);
        }
        public void setuserdpthumb(String thumbimage, Context ctx) {
            userdp = mitemView.findViewById(R.id.Profile);
            Picasso.with(ctx).load(thumbimage).placeholder(R.mipmap.circle_spin).into(userdp);
        }
        public void setuseruploadthumb(String thumb, Context ctx) {
            userpic = mitemView.findViewById(R.id.imageButton2);
            Picasso.with(ctx).load(thumb).placeholder(R.mipmap.circle_spin).into(userpic);
        }
        public void setlike(final String post_key) {
            mDatabaselike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).child("like").hasChild(mAuth.getCurrentUser().getUid())) {
                        buttonlike.setImageResource(R.mipmap.like);
                    } else {
                        buttonlike.setImageResource(R.mipmap.like_grey);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        public void setheartlike(final String post_key) {
            mDatabaselike.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child(post_key).child("heart").hasChild(mAuth.getCurrentUser().getUid())) {
                        heartlike.setImageResource(R.mipmap.full_blue_heart);
                    } else {
                        heartlike.setImageResource(R.mipmap.heart_grey);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }
        public void setlikesCount(long likes_count) {
            TextView likecount = mitemView.findViewById(R.id.textViewlikecount);
            likecount.setText((String.valueOf(likes_count)));
        }
        public void setCommentCount(long comments_count) {
            TextView commentcount = mitemView.findViewById(R.id.textViewCommentCount);
            commentcount.setText((String.valueOf(comments_count)));
        }
        public void setheartCount(long hearts_count) {
            TextView heartcount = mitemView.findViewById(R.id.textViewheartcount);
            heartcount.setText((String.valueOf(hearts_count)));
        }
    }
}