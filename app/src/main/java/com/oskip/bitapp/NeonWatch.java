package com.oskip.bitapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
/**
 * A simple {@link Fragment} subclass.
 */
public class NeonWatch extends Fragment {
    protected ListView recyclerView99neon;
    public DatabaseReference mDatabaseneon;
    AdView mAdView2;
    FloatingActionButton postthought;
    LinearLayoutManager linearLayoutManager;
    NeonAdapter neonadapter;
    private final List<NeonObject> neonlist = new ArrayList<>();
    //Query neonquery;
    public NeonWatch() {
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_neon_watch, container, false);
        recyclerView99neon = view.findViewById(R.id.neonwatch);
        linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView99neon.setAdapter(neonadapter);
        postthought = view.findViewById(R.id.postneon);
        postthought.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent neonpostintent = new Intent(getContext().getApplicationContext(),NeonPost.class);
                startActivity(neonpostintent);
            }
        });
        mDatabaseneon = FirebaseDatabase.getInstance().getReference().child("neon page post");
        //neonquery = mDatabaseneon.orderByChild("timestamp");
        MobileAds.initialize(getContext(), "ca-app-pub-1211241739368711~3509108984");
        mAdView2 = view.findViewById(R.id.adView2);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView2.loadAd(adRequest);
        loadMessages();
        return view;
    }
    private void loadMessages() {
        neonadapter = new NeonAdapter(getActivity(), NeonObject.class, R.layout.neontextpost, mDatabaseneon);
        mDatabaseneon.keepSynced(true);
        neonadapter.notifyDataSetChanged();
        recyclerView99neon.setAdapter(neonadapter);
    }
    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}