package com.oskip.bitapp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by root on 5/11/18.
 */

public class NeonQueryAdapter extends FirebaseListAdapter<NeonObject>{
    List<NeonObject> mMessagelist = new ArrayList<NeonObject>();
    private NeonPost neonPost;
    Context context;
    public NeonQueryAdapter(NeonPost neonPost, Class<NeonObject> modelClass, int modelLayout, Query ref) {
        super(neonPost, modelClass, modelLayout, ref);
        this.neonPost=neonPost;
    }
    @Override
    protected void populateView(View v, NeonObject model, int position) {

        TextView timeneon = v.findViewById(R.id.time);
        TextView username = v.findViewById(R.id.textView46);
        TextView pagetype = v.findViewById(R.id.type2);
        ImageView bloggingpost = v.findViewById(R.id.iv_movie_poster);
        CircleImageView bloggingdp = v.findViewById(R.id.imageView24blogger);
        TextView neontext = v.findViewById(R.id.tv_name);
        Picasso.with(context).load(model.getBlogger()).placeholder(R.mipmap.circle_spin).into(bloggingdp);
        Picasso.with(context).load(model.getUrlcaption()).placeholder(R.mipmap.circle_spin).into(bloggingpost);
        neontext.setText(model.getNeoncaption());
        username.setText(model.getUsername());
        pagetype.setText(model.getPgtype());
        timeneon.setText(model.getTimestamp());
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NeonObject messagechat09 = getItem(position);
        String type = messagechat09.getType();
            if (type.equals("image")) {
                    convertView = neonPost.getLayoutInflater().inflate(R.layout.neontextpost, parent, false);
            } else{
                    convertView = neonPost.getLayoutInflater().inflate(R.layout.neonpicturepost, parent, false);
            }
        populateView(convertView, messagechat09, position);
        return convertView;
    }
}