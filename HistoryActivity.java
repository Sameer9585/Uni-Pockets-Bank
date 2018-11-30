package com.ibrickedlabs.mpokket.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.util.Half;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.ibrickedlabs.mpokket.Adapters.HistoryAdapter;
import com.ibrickedlabs.mpokket.Adapters.SliderAdapter;
import com.ibrickedlabs.mpokket.Data.Mpokket;
import com.ibrickedlabs.mpokket.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import me.relex.circleindicator.CircleIndicator;

import static maes.tech.intentanim.CustomIntent.customType;

public class HistoryActivity extends AppCompatActivity {

    //Bottom navigation view
    private BottomNavigationView mBottomNavigationView;

    //For Viewpager
    private ViewPager mViewPager;
    private Handler handler;
    private Timer timer;
    private Runnable runnable;
    CircleIndicator circleIndicator;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFirebaseStorage;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mDatabase;

    //Recycler Adapter
    HistoryAdapter mHistoryAdapter;
    List<Mpokket> list;
    RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        final SliderAdapter sliderAdapter = new SliderAdapter(this);
        mViewPager.setAdapter(sliderAdapter);
        circleIndicator = (CircleIndicator) findViewById(R.id.circleIndicator);
        circleIndicator.setViewPager(mViewPager);
        handler = new Handler();
        //Bottom Navigation View Intz
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.botomNaviagtion);


        //Recyclerview stuff
        list = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mHistoryAdapter = new HistoryAdapter(HistoryActivity.this, list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mHistoryAdapter);


        //FIREBASE INTZ
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("mPokket").child("Requests").child(mFirebaseAuth.getCurrentUser().getUid());


        //Viewpager stuff
        runnable = new Runnable() {
            @Override
            public void run() {
                int i = mViewPager.getCurrentItem();
                if (i == sliderAdapter.imagesArray.length - 1) {
                    i = 0;
                    mViewPager.setCurrentItem(i, true);
                } else {
                    i++;
                    mViewPager.setCurrentItem(i, true);
                }

            }
        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(runnable);
            }
        }, 4000, 4000);

        //Bottom Navigation
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem item = menu.getItem(1);
        item.setChecked(true);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.request_money:
                        startActivity(new Intent(HistoryActivity.this, RequestMoney.class));
                        customType(HistoryActivity.this, "right-to-left");

                        finish();


                        return true;
                    case R.id.recents:
                        startActivity(new Intent(HistoryActivity.this, HistoryActivity.class));
                        customType(HistoryActivity.this, "fadein-to-fadeout");

                        finish();


                        return true;
                    case R.id.account:
                        startActivity(new Intent(HistoryActivity.this, MyAccount.class));
                        customType(HistoryActivity.this, "left-to-right");
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
                Mpokket mpokket = dataSnapshot.getValue(Mpokket.class);
                list.add(mpokket);
                Collections.reverse(list);
                mHistoryAdapter.notifyDataSetChanged();

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
    }

    private void updateNavigationBarState(int actionId){
        Menu menu = mBottomNavigationView.getMenu();

        for (int i = 0, size = menu.size(); i < size; i++) {
            MenuItem item = menu.getItem(i);
            item.setChecked(item.getItemId() == actionId);
        }
    }


}
