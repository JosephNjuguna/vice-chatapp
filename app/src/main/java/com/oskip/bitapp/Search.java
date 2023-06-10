package com.oskip.bitapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Search extends AppCompatActivity {
    DatabaseReference mallUsers;
    protected RecyclerView allusers;
    Toolbar mfriendToolbar;
    ImageView buttonsearch;
    EditText searchtext;
    ImageView chatroom;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_test);
        //---------------------------tool bar------------------------------------------------------
        mfriendToolbar = findViewById(R.id.app_search_bar);
        setSupportActionBar(mfriendToolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        //--------------------------------bar inflater---------------------------------------------
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View chatroombar = inflater.inflate(R.layout.search_bar, null);
        actionBar.setCustomView(chatroombar);
        mallUsers = FirebaseDatabase.getInstance().getReference().child("users");
        chatroom = findViewById(R.id.imageButtonchatroom);
        chatroom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence options[] = new CharSequence[]{"Create chatroom"};
                AlertDialog.Builder builder = new AlertDialog.Builder(Search.this);
                builder.setTitle("Create a ChatRoom");
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Intent chatroom = new Intent(Search.this,ChatRoomCreation.class);
                            startActivity(chatroom);
                        }
                    }
                });
                builder.show();
            }
        });
        searchtext = findViewById(R.id.editText10);
        buttonsearch = findViewById(R.id.button10);
        buttonsearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchname = searchtext.getText().toString();
                firebaseUsersearch(searchname);
                searchtext.setText("");
            }
        });
        //------------------------recycler inflater------------------------------------------------
        StaggeredGridLayoutManager gridLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        allusers = findViewById(R.id.all);
        allusers.setLayoutManager(gridLayoutManager);
    }
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<SearchObject,AllUserHolder> firebaseRecyclerAdapter1 = new FirebaseRecyclerAdapter<SearchObject, AllUserHolder>(
                SearchObject.class,
                R.layout.alluserlayoutpage,
                AllUserHolder.class,
                mallUsers
        ) {
            @Override
            protected void populateViewHolder(AllUserHolder viewHolder, SearchObject model, int position) {
                viewHolder.setusername(model.getName());
                viewHolder.setsearchuserpic(model.getThumbimage(),getApplicationContext());
                final String user_id = getRef(position).getKey();
                viewHolder.mitemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toprofile = new Intent( Search.this,UserFriendsAccount.class);
                        toprofile.putExtra("from_user_id",user_id);
                        startActivity(toprofile);
                    }
                });
            }
        };
        allusers.setAdapter(firebaseRecyclerAdapter1);
    }
    private void firebaseUsersearch(String searchname) {
        Query firebasesearch = mallUsers.orderByChild("name").startAt(searchname).endAt(searchname + "\uf8ff");
        FirebaseRecyclerAdapter<SearchObject,AllUserHolder> firebaseRecyclerAdapter2 = new FirebaseRecyclerAdapter<SearchObject, AllUserHolder>(
                SearchObject.class,
                R.layout.alluserlayoutpage,
                AllUserHolder.class,
                firebasesearch
        ) {
            @Override
            protected void populateViewHolder(AllUserHolder viewHolder,SearchObject model, int position) {
                viewHolder.setusername(model.getName());
                viewHolder.setsearchuserpic(model.getThumbimage(),getApplicationContext());
                final String user_id = getRef(position).getKey();
                viewHolder.mitemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent toprofile = new Intent( Search.this,UserFriendsAccount.class);
                        toprofile.putExtra("from_user_id",user_id);
                        startActivity(toprofile);
                    }
                });
            }
        };
        allusers.setAdapter(firebaseRecyclerAdapter2);
    }
    public static class AllUserHolder extends RecyclerView.ViewHolder {
        View mitemView;
        public AllUserHolder(final View itemView) {
            super(itemView);
            mitemView = itemView;
        }
        public void setusername(String name){
           TextView usersnames= mitemView.findViewById(R.id.allusersname);
            usersnames.setText(name);
        }
        public void setsearchuserpic(String thumbimage ,Context ctx){
            ImageView userpic = mitemView.findViewById(R.id.allusersimagesearch);
            Picasso.with(ctx).load(thumbimage).placeholder(R.mipmap.circle_spin).into(userpic);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent backintent = new Intent(Search.this,ProfileActivity.class);
        startActivity(backintent);
    }
}