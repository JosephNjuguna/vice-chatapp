package com.oskip.bitapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class OldUserPost extends AppCompatActivity {
    private static final String TAG = "Tag!!!!" ;
    public DatabaseReference mDatabase;
    public FirebaseAuth mAuth;
    private Query mQuery;
    LinearLayoutManager linearLayoutManager;
    public String id;
    String current_user;
    protected RecyclerView recyclerViewold;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.old_user_post);
        //------------------------------------UI---------------------------------
        recyclerViewold = findViewById(R.id.retrievedolduserpost);
        linearLayoutManager = new LinearLayoutManager(OldUserPost.this);
        linearLayoutManager.setReverseLayout(true);
        recyclerViewold.setLayoutManager(linearLayoutManager);
        //---------------------------------FIREBASE--------------------------------
        mAuth = FirebaseAuth.getInstance();
        current_user = mAuth.getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("wall uploads");
        mQuery  =  mDatabase.orderByChild("id").equalTo(current_user);
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Upload, OldUserPostHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Upload, OldUserPostHolder>(
                Upload.class,
                R.layout.old_post_retreived_layout,
                OldUserPostHolder.class,
                mQuery){
            @Override
            protected void populateViewHolder(final OldUserPostHolder viewHolder, Upload model, int position) {
                final String old = getRef(position).getKey();
                //--------------------------------------------
                viewHolder.setcaption(model.getCaption());
                viewHolder.setoldlike(model.getLikes_count());
                viewHolder.setoldcomments(model.getComments_count());
                viewHolder.setoldhearts(model.getHearts_count());
                //--------------------------------------------
                viewHolder.setuserupload(model.getThumb(), getApplicationContext());
                viewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDatabase.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.child(old).hasChild(current_user)) {
                                    mDatabase.child(old).child(current_user).removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                        });
                    }
                });
            }
        };
        recyclerViewold.setAdapter(firebaseRecyclerAdapter);
    }
    public static class OldUserPostHolder extends RecyclerView.ViewHolder {
        View mitemView;
        TextView oldlike;
        TextView  oldhearts;
        TextView oldcomments;
        ImageView delete;
        public OldUserPostHolder(final View itemView) {
            super(itemView);
            mitemView = itemView;
            delete = mitemView.findViewById(R.id.buttonDelete);
        }
        public void setcaption(String caption) {
            TextView useroldcaption = mitemView.findViewById(R.id.textViewoldpost);
            useroldcaption.setText(caption);
        }
        public void setoldlike(long likes_count) {
            oldlike = mitemView.findViewById(R.id.LIKECOUNT);
            oldlike.setText((String.valueOf(likes_count)));
        }
        public void setoldhearts(long hearts_count) {
            oldhearts= mitemView.findViewById(R.id.HEARTCOUNT);
            oldhearts.setText((String.valueOf(hearts_count)));
        }
        public void setoldcomments(long hearts_count) {
            oldcomments = mitemView.findViewById(R.id.COMMENTCOUNT);
            oldcomments.setText((String.valueOf(hearts_count)));
        }
        public void setuserupload(String image, Context ctx) {
            ImageView useroldpost = mitemView.findViewById(R.id.imageButtonold);
            Picasso.with(ctx).load(image).placeholder(R.mipmap.circle_spin).into(useroldpost);
        }
    }
}