package com.ibrickedlabs.mpokket.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.ibrickedlabs.mpokket.Data.Account;
import com.ibrickedlabs.mpokket.R;
import com.wang.avi.AVLoadingIndicatorView;

import de.hdodenhof.circleimageview.CircleImageView;

import static maes.tech.intentanim.CustomIntent.customType;

public class MyAccount extends AppCompatActivity {
    //Bottom navigation view
    private BottomNavigationView mBottomNavigationView;

    //Pbar
    AVLoadingIndicatorView mAvLoadingIndicatorView;
    private LinearLayout pbarLayout;

    //Edittext Views
    private AppCompatEditText firstNameEditText;
    private AppCompatEditText lastNameEditText;
    private AppCompatEditText emailEditText;
    private AppCompatEditText phoneNumberEditText;

    //Eligibility
    boolean eligible;
    String totalRequested;

    //Button
    private Button signoutButton;

    //ImageView
    private CircleImageView profileImage;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mStorageReference;
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        //Bottom Navigation View Intz
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.botomNaviagtion);

        //Pbar stuff
        mAvLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avlbar_MA);
        pbarLayout = (LinearLayout) findViewById(R.id.pbarlayout_MA);

        //Button Intz
        signoutButton = (Button) findViewById(R.id.signout_btn);

        attachPbar();//Attach Progress bar

        //EditText Intz
        firstNameEditText = (AppCompatEditText) findViewById(R.id.firstnameField_MA);
        lastNameEditText = (AppCompatEditText) findViewById(R.id.lastnameField_MA);
        emailEditText = (AppCompatEditText) findViewById(R.id.emailField_MA);
        phoneNumberEditText = (AppCompatEditText) findViewById(R.id.phonenumField_MA);




        //ImagevIew Intz
        profileImage = (CircleImageView) findViewById(R.id.profileImage_MA);

        //Firebase Intz
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mDatabaseReference = mDatabase.getReference().child("mPokket").child("UserDetails").child(mFirebaseAuth.getCurrentUser().getUid());

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Account account = dataSnapshot.getValue(Account.class);
                firstNameEditText.setText(account.getFirstName());
                lastNameEditText.setText(account.getLastName());
                emailEditText.setText(account.getEmail());
                String phnNumber=getPhonenNumber();
                phoneNumberEditText.setText(phnNumber);
                Glide.with(MyAccount.this).load(account.getProfileImageUrl()).into(profileImage);
                deattachPbar();//Remove Progress bar
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //BottomNavigation View Listener
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem item = menu.getItem(2);
        item.setChecked(true);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.request_money:
                        startActivity(new Intent(MyAccount.this, RequestMoney.class));
                        customType(MyAccount.this,"right-to-left");
                        finish();

                        return true;
                    case R.id.recents:
                        startActivity(new Intent(MyAccount.this, HistoryActivity.class));
                        customType(MyAccount.this,"right-to-left");
                        finish();

                        return true;
                    case R.id.account:
                        startActivity(new Intent(MyAccount.this,MyAccount.class));
                        customType(MyAccount.this,"fadein-to-fadeout");
                        finish();

                        return true;
                }
                return false;
            }
        });

        //SignoutButton
        signoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFirebaseAuth.signOut();
                startActivity(new Intent(MyAccount.this,MainActivity.class));
                finish();
            }
        });



    }

    private String getPhonenNumber() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String num = sharedPreferences.getString("PHONE_NUMBER", "");
        return num;
    }


    private void deattachPbar() {
        pbarLayout.setVisibility(View.INVISIBLE);
        mAvLoadingIndicatorView.hide();
        signoutButton.setVisibility(View.VISIBLE);
    }

    private void attachPbar() {
        pbarLayout.setVisibility(View.VISIBLE);
        mAvLoadingIndicatorView.show();
        signoutButton.setVisibility(View.INVISIBLE);
    }
}
