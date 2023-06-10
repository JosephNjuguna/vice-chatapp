package com.oskip.bitapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
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
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class Phonesignup extends AppCompatActivity {
    private static final String TAG = "Phone Auth";
    CircleImageView phoneimage;
    EditText phonenumber;
    EditText mCodeText;
    Button phonenumbersignup;
    TextView gotologin;
    ProgressBar mProgressbarphone;
    String mVerificatioCode;
    EditText phonename;
    EditText code;
    TextView imagetext;
    private Uri profile;
    private Uri filePath;
    Bitmap thumb_bitmap2 = null;
    DatabaseReference mdata;
    String number;
    String name;
    FirebaseUser mCurrentUser;
    final LayoutInflater inflater = null;
    PhoneAuthProvider.ForceResendingToken mResendToken;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    private static final int PICK_PROFILE_IMAGE = 12231;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    HashMap<String, String> userMap;
    String downuri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonesignup);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        mAuth = FirebaseAuth.getInstance();
        imagetext = findViewById(R.id.textView79);
        imagetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PROFILE_IMAGE);
            }
        });
        mProgressbarphone = findViewById(R.id.progressBar2);
        code = findViewById(R.id.editText12);
        phonenumber = findViewById(R.id.editText8);
        phonename = findViewById(R.id.editText17);
        phoneimage = findViewById(R.id.imageView15);
        phoneimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PROFILE_IMAGE);
            }
        });
        phonenumbersignup = findViewById(R.id.button5);
        phonenumbersignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name  = phonename.getText().toString();
                number = phonenumber.getText().toString();
                if (TextUtils.isEmpty(name)|| TextUtils.isEmpty(number)) {
                    Toast.makeText(Phonesignup.this, "please fill your name and number to continue", Toast.LENGTH_LONG).show();
                }else{
                    mProgressbarphone.setVisibility(View.VISIBLE);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            number,        // Phone number to verify
                            60,                 // Timeout duration
                            TimeUnit.SECONDS,   // Unit of timeout
                            Phonesignup.this,               // Activity (for callback binding)
                            mCallbacks // OnVerificationStateChangedCallbacks
                    );
                    phonename.setEnabled(false);
                    phoneimage.setEnabled(false);
                    phonenumber.setEnabled(false);
                    phonenumbersignup.setEnabled(false);
                }
            }
        });
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
            @Override
            public void onVerificationFailed(FirebaseException e) {
                Toast.makeText(Phonesignup.this, "Error Verifying Your Code!!  " +name+ " Please Try Again",Toast.LENGTH_LONG).show();
                mProgressbarphone.setVisibility(View.INVISIBLE);
                phonenumbersignup.setEnabled(true);
                phonename.setEnabled(true);
                phoneimage.setEnabled(true);
                phonenumber.setEnabled(true);
            }
            @Override
            public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                mVerificatioCode = verificationId;
                mResendToken = token;
                mProgressbarphone.setVisibility(View.VISIBLE);
                code.setVisibility(View.VISIBLE);
                code.setText("Verification Code Received:");
            }
        };
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == PICK_PROFILE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();
            CropImage.activity(filePath)
                    .setAspectRatio(2, 2)
                    .setMinCropWindowSize(300, 300)
                    .start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result2 = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profile = result2.getUri();
                File thumb_filepath2 = new File(profile.getPath());
                try {
                    thumb_bitmap2 = new Compressor(this).setMaxWidth(300).setMaxHeight(300).setQuality(75).compressToBitmap(thumb_filepath2);
                    thumb_bitmap2 = MediaStore.Images.Media.getBitmap(getContentResolver(), profile);
                    phoneimage.setImageBitmap(thumb_bitmap2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
        if (task.isSuccessful()) {
            if (profile != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                thumb_bitmap2.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
                final byte[] thumb_byte2 = byteArrayOutputStream.toByteArray();
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                final String uid = user.getUid();
                final ProgressDialog progressDialog = new ProgressDialog(Phonesignup.this);
                progressDialog.setMessage("Getting your account ready." + "Have Fun" + name);
                progressDialog.show();
                progressDialog.setCancelable(false);
                mdata = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                final StorageReference mstore = FirebaseStorage.getInstance().getReference().child("users").child(uid);
                final String deviceToken = FirebaseInstanceId.getInstance().getToken();
                mstore.putFile(profile).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        UploadTask uploadTask2 = mstore.putBytes(thumb_byte2);
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy, HH:mm:ss ");
                        Date date = new Date();
                        final String strdate = dateFormat.format(date).toString();
                        Task<Uri> urlTask = uploadTask2.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                         if (!task.isSuccessful()) {
                             throw task.getException();
                         }
                         // Continue with the task to get the download URL
                         return mstore.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                         @Override
                         public void onComplete(@NonNull Task<Uri> task) {
                           if (task.isSuccessful()) {
                               Uri downloadUri = task.getResult();
                               downuri = downloadUri.toString();
                               userMap = new HashMap<>();
                               userMap.put("name", name);
                               userMap.put("phone number",number);
                               userMap.put("uId",uid);
                               userMap.put("started",strdate);
                               userMap.put("thumbimage",downuri);
                               userMap.put("deviceToken",deviceToken);
                               mdata.setValue(userMap);
                           } else {
                           }
                         }
                        });
                        uploadTask2.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                          @Override
                          public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_walltask2) {
                              progressDialog.dismiss();
                              LayoutInflater inflaterr = getLayoutInflater();
                              final View view = inflaterr.inflate(R.layout.refercode, null);
                              final AlertDialog alertdialog = new AlertDialog.Builder(Phonesignup.this).create();
                              alertdialog.setMessage("Do you have a refer code??Enter it below....if you don`t have proceed since its optional");
                              alertdialog.setCancelable(false);
                              final EditText code = view.findViewById(R.id.textViewCode);
                              alertdialog.setButton(AlertDialog.BUTTON_POSITIVE, "proceed", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      String recode = code.getText().toString();
                                      code.setText("");
                                      FirebaseUser usercoderef = FirebaseAuth.getInstance().getCurrentUser();
                                      final String uid = usercoderef.getUid();
                                      DatabaseReference coderef = FirebaseDatabase.getInstance().getReference().child("users").child(uid);
                                      Map updatehashmap = new HashMap();
                                      updatehashmap.put("refercode", recode);
                                      coderef.updateChildren(updatehashmap);
                                  }
                              });
                              alertdialog.setButton(AlertDialog.BUTTON_NEGATIVE, "don`t have any,proceed anyway", new DialogInterface.OnClickListener() {
                                  @Override
                                  public void onClick(DialogInterface dialog, int which) {
                                      alertdialog.dismiss();
                                  }
                              });
                              alertdialog.setView(view);
                              alertdialog.show();
                              mProgressbarphone.setVisibility(View.INVISIBLE);
                              Intent nextintent = new Intent(Phonesignup.this, Search.class);
                              nextintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                              startActivity(nextintent);
                              finish();
                              }
                             });
                          }
                        });
                    }else {
                        Toast.makeText(getApplicationContext(), "Please Select a Display Picture", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
    private void updateUI(FirebaseUser user) {
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        mAuth.addAuthStateListener(mAuthListener);
    }
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}