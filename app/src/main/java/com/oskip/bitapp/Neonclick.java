package com.oskip.bitapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/*public class Neonclick extends AppCompatActivity {
    private String fromneon;
    private DatabaseReference neon;
    private ImageView neonpic;
    private TextView neontext;
    private Button about;
    private CircleImageView bloggerpic;
    private RecyclerView blogallcomments;
    private ImageView bloglike;
    private ImageView blogcomment;
    private ImageView blogShare;
    private boolean mProcessBloglike = false;
    //------------------------firebase-----------------------------------------------------------------------
    public FirebaseAuth mAuthblog;
    private DatabaseReference blogcomments;
    private DatabaseReference mDatabasebloglike;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_neonclick);
        fromneon = getIntent().getStringExtra("post_id");
        blogallcomments = findViewById(R.id.comments);
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        blogallcomments.setLayoutManager(gridLayoutManager);
        bloglike = findViewById(R.id.imageView27);
        bloglike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                likedatabase();
            }
        });
        blogcomment = findViewById(R.id.imageView33);
        blogcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentcomment = new Intent(Neonclick.this, BlogActivity.class);
                intentcomment.putExtra("post_id",fromneon);
                startActivity(intentcomment);
            }
        });
        blogShare = findViewById(R.id.imageView32);
        blogShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(v, "To be implemented in next update", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Invite More Friends", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        about = findViewById(R.id.button9);
        neontext = findViewById(R.id.neontext);
        neonpic = findViewById(R.id.neonimageclick);
        bloggerpic = findViewById(R.id.imageView24);
        //---------------------------------------------------
        mAuthblog = FirebaseAuth.getInstance();
        mDatabasebloglike = FirebaseDatabase.getInstance().getReference().child("neon upload pics");
        blogcomments = FirebaseDatabase.getInstance().getReference().child("neon upload pics").child(fromneon).child("comments");
        neon = FirebaseDatabase.getInstance().getReference().child("neon upload pics").child(fromneon);
        neon.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild("url")) {
                    String image = dataSnapshot.child("url").getValue().toString();
                    Picasso.with(getApplicationContext()).load(image).placeholder(R.mipmap.circle_spin).into(neonpic);
                }
                if (dataSnapshot.hasChild("pagepic")) {
                    String bloggerimage = dataSnapshot.child("pagepic").getValue().toString();
                    Picasso.with(getApplicationContext()).load(bloggerimage).placeholder(R.mipmap.circle_spin).into(bloggerpic);
                }
                if (dataSnapshot.hasChild("caption")) {
                    String caption = dataSnapshot.child("caption").getValue().toString();
                    neontext.setText(caption);
                }
                if (dataSnapshot.hasChild("pagename")) {
                    String blogname = dataSnapshot.child("pagename").getValue().toString();
                    about.setText(blogname);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
    private void likedatabase() {
        mProcessBloglike = true;
        mDatabasebloglike.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mProcessBloglike) {
                    if (dataSnapshot.child(fromneon).child("like").hasChild(mAuthblog.getCurrentUser().getUid())) {
                        mDatabasebloglike.child(fromneon).child("like").child(mAuthblog.getCurrentUser().getUid()).removeValue();
                        bloglike.setImageResource(R.mipmap.like_grey);
                        mProcessBloglike = false;
                    } else {
                        mDatabasebloglike.child(fromneon).child("like").child(mAuthblog.getCurrentUser().getUid()).setValue("name");
                        bloglike.setImageResource(R.mipmap.like);
                        mProcessBloglike = false;
                    }
                }
            }
            @Override
            public void onCancelled (DatabaseError databaseError){
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<CommentsObject,BlogonClickHolder> firebaseRecyclerAdapterComments = new FirebaseRecyclerAdapter<CommentsObject,BlogonClickHolder>(
                CommentsObject.class,
                R.layout.commentinsert,
                BlogonClickHolder.class,
                blogcomments
        ) {
            @Override
            protected void populateViewHolder(final BlogonClickHolder viewHolder, CommentsObject model, int position) {
                viewHolder.setcomment(model.getComment());
                viewHolder.setusercomment(model.getUsercommentdp(),getApplicationContext());
                viewHolder.setcommentname(model.getCommentusername());
            }
        };
        blogallcomments.setAdapter(firebaseRecyclerAdapterComments);
    }
    public static class BlogonClickHolder extends RecyclerView.ViewHolder {
        View mitemview;
        public BlogonClickHolder(final View itemView) {
            super(itemView);
            mitemview = itemView;
        }
        public void setusercomment(String image ,Context ctx){
            ImageView  usercommentpic = mitemview.findViewById(R.id.imageViewProfilecomment);
            Picasso.with(ctx).load(image).into(usercommentpic);
        }
        public void setcomment(String comment){
            TextView userwallcaption = mitemview.findViewById(R.id.comment);
            userwallcaption.setText(comment);
        }
        public void setcommentname(String username){
            TextView user = mitemview.findViewById(R.id.comment_username);
            user.setText(username);
        }
    }
}*/
