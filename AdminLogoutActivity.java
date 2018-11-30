package com.ibrickedlabs.mpokket.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrickedlabs.mpokket.Data.Account;
import com.ibrickedlabs.mpokket.R;
import com.wang.avi.AVLoadingIndicatorView;

import static maes.tech.intentanim.CustomIntent.customType;

public class AdminLogoutActivity extends AppCompatActivity {
    //imageView
    private ImageView profileImage;

    //Appcompat EditText
    private AppCompatEditText firstNameView;
    private AppCompatEditText lastNameView;
    private AppCompatEditText emailView;
    private AppCompatEditText phoneNumberView;

    //Firebase
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    //Button
    private Button signOutButton;

    //Firebase
    private  FirebaseAuth mFirebaseAuth;

    //Pbar
    AVLoadingIndicatorView mAvLoadingIndicatorView;
    LinearLayout pbarLayout;

    //Bottom navigation view
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_logout);
        //Button Intz
        signOutButton = (Button) findViewById(R.id.signout_btn_AL);

        //pbar Intz
        mAvLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avlbar_AL);
        pbarLayout = (LinearLayout) findViewById(R.id.pbarlayout_AL);
        attachProgressBar();

        //ImagevIew Intz
        profileImage = (ImageView) findViewById(R.id.profileImage_AL);

        //Firebase Intz
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("mPokket").child("AdminDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mFirebaseAuth=FirebaseAuth.getInstance();

        //Bottom Navigation View Intz
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.botomNaviagtion);


        //Appcompat EditText Intz
        firstNameView = (AppCompatEditText) findViewById(R.id.firstnameField_AL);
        lastNameView = (AppCompatEditText) findViewById(R.id.lastnameField_AL);
        emailView = (AppCompatEditText) findViewById(R.id.emailField_AL);
        phoneNumberView = (AppCompatEditText) findViewById(R.id.phonenumField_AL);




        //Bottom Navigation
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem item = menu.getItem(1);
        item.setChecked(true);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.admin_account:
                        startActivity(new Intent(AdminLogoutActivity.this, AdminLogoutActivity.class));
                        customType(AdminLogoutActivity.this, "fadein-to-fadeout");

                        finish();

                        return true;
                    case R.id.requests:
                        startActivity(new Intent(AdminLogoutActivity.this, AdminActivity.class));
                        customType(AdminLogoutActivity.this, "right-to-left");

                        finish();

                        return true;

                }
                return false;
            }
        });

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attachProgressBar();
                mFirebaseAuth.signOut();
                startActivity(new Intent(AdminLogoutActivity.this,MainActivity.class));
                customType(AdminLogoutActivity.this, "fadein-to-fadeout");
                finish();
                deattachProgressBar();

            }
        });


    }

    private void attachProgressBar() {
        pbarLayout.setVisibility(View.VISIBLE);
        mAvLoadingIndicatorView.show();
        signOutButton.setVisibility(View.INVISIBLE);
    }

    private void deattachProgressBar(){
        pbarLayout.setVisibility(View.INVISIBLE);
        mAvLoadingIndicatorView.hide();
        signOutButton.setVisibility(View.VISIBLE);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Account adminAccount = dataSnapshot.getValue(Account.class);
                firstNameView.setText(adminAccount.getFirstName());
                lastNameView.setText(adminAccount.getLastName());
                emailView.setText(adminAccount.getEmail());
                phoneNumberView.setText(adminAccount.getPhoneNumber());
                Glide.with(AdminLogoutActivity.this).load(adminAccount.getProfileImageUrl()).into(profileImage);
                deattachProgressBar();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
