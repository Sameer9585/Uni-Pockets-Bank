package com.ibrickedlabs.mpokket.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.ibrickedlabs.mpokket.Adapters.AdminRequestsAdapter;
import com.ibrickedlabs.mpokket.Data.Mpokket;
import com.ibrickedlabs.mpokket.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static maes.tech.intentanim.CustomIntent.customType;

public class AdminActivity extends AppCompatActivity {
    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;


    //Bottom navigation view
    private BottomNavigationView mBottomNavigationView;


    //ImageGif
    private ImageView adminImageGif;

    //Recylerview
    private RecyclerView mRecyclerView;
    private AdminRequestsAdapter mAdminRequestsAdapter;

    //List for storing
    List<Mpokket> list;
    private List<String> parentList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        //List Intz
        list = new ArrayList<>();
        parentList = new ArrayList<>();

        //Bottom Navigation View Intz
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.botomNaviagtion);

        //Gif Intz
        adminImageGif = (ImageView) findViewById(R.id.adminGif);

        //Firebase Intz
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("mPokket").child("Requests");

        //RecycylerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_AA);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Adapter
        mAdminRequestsAdapter = new AdminRequestsAdapter(this, list, parentList);

        //Attachment
        mRecyclerView.setAdapter(mAdminRequestsAdapter);

        //Bottom Navigation
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.admin_account:
                        startActivity(new Intent(AdminActivity.this, AdminLogoutActivity.class));
                        customType(AdminActivity.this, "left-to-right");

                        finish();

                        return true;
                    case R.id.requests:
                        startActivity(new Intent(AdminActivity.this, AdminActivity.class));

                        customType(AdminActivity.this, "fadein-to-fadeout");

                        finish();

                        return true;

                }
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Iterable<DataSnapshot> requests = dataSnapshot.getChildren();
                for (DataSnapshot cuurentRequest : requests) {
                    Mpokket c = cuurentRequest.getValue(Mpokket.class);

                    list.add(c);
                    parentList.add(cuurentRequest.getKey());
                    Log.d("AdminActivity", cuurentRequest.getKey());
                    Collections.reverse(list);
                    Collections.reverse(parentList);
                    mAdminRequestsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        list.clear();
        parentList.clear();
    }
}
