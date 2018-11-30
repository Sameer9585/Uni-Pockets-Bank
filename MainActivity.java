package com.ibrickedlabs.mpokket.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrickedlabs.mpokket.Data.Account;
import com.ibrickedlabs.mpokket.R;

public class MainActivity extends AppCompatActivity {
    //Firebase Intz
    private FirebaseDatabase mDatabase;
    private DatabaseReference mVerified;
    private DatabaseReference mDetailsCreated;
    private FirebaseAuth mFirebaseAuth;

    private DatabaseReference mAdminDetailsCreated;


    //TextView
    private TextView mpktText;

    //For checking
    Account DetailsCreated;
    Boolean verified = false;
    Account AdminDetails;
    //For gif image
    ImageView gifImage;

    //RelativeTop
    RelativeLayout mTopLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTopLayout=(RelativeLayout)findViewById(R.id.TopLayoutMA);

        //Mpkt Text
        Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/beyno.ttf");
        mpktText = (TextView) findViewById(R.id.mpktTv);
        mpktText.setTypeface(typeface);


        //Gify
        gifImage = (ImageView) findViewById(R.id.gifIamge);
        Glide.with(this).load(R.drawable.entrygif).into(gifImage);


        //Intializing firebase
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();


        //System.out.println(mFirebaseAuth.getCurrentUser());

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser != null) {
            //Already signed in state


            if (getPhonenNumber().equals("8008853854")){
                Log.d("MainActivity","yes entered");
                getAdminPathRetrieve();
                mAdminDetailsCreated.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        AdminDetails=dataSnapshot.getValue(Account.class);
                        System.out.println(AdminDetails);
                        if(AdminDetails!=null){//if he filled the account form
                            startActivity(new Intent(MainActivity.this,AdminActivity.class));
                            finish();

                        }
                        else{
                            startActivity(new Intent(MainActivity.this,AccountDetails.class));
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                }

            else{//Not the admin


                //retrieving the path of the firebase
                getThePathsToRetrieve();
                //Check wether he has inputed the details
                mDetailsCreated.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        //getting the object from there
                        DetailsCreated = dataSnapshot.getValue(Account.class);
                        System.out.println(DetailsCreated);

                        //Check wether he is verified or not
                        mVerified.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                //getting the boolean value from there
                                verified = (Boolean) dataSnapshot.getValue();
                                System.out.println(verified);
                                if (DetailsCreated != null && verified != null) {
                                    startActivity(new Intent(MainActivity.this, RequestMoney.class));
                                    finish();
                                } else if (DetailsCreated == null) {
                                    startActivity(new Intent(MainActivity.this, AccountDetails.class));
                                    finish();
                                } else {
                                    startActivity(new Intent(MainActivity.this, UploadActivity.class));
                                    finish();
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }





        }//Closing of signed state if
        else {
            //Not signed in
            Snackbar.make(mTopLayout,"Signed out",Snackbar.LENGTH_SHORT).show();
            startActivity(new Intent(MainActivity.this, PhoneAuthActivity.class));
            finish();
        }
    }

    private void assignEligibilty() {
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
       SharedPreferences.Editor editor= sharedPreferences.edit();
       editor.putInt("VALUE",0);
    }

    private String getPhonenNumber() {
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
        return  sharedPreferences.getString("PHONE_NUMBER","");

    }

    private void getAdminPathRetrieve() {
        mAdminDetailsCreated = mDatabase.getReference().child("mPokket").child("AdminDetails").child(mFirebaseAuth.getCurrentUser().getUid());
    }


    private void getThePathsToRetrieve() {
        mDetailsCreated = mDatabase.getReference().child("mPokket").child("UserDetails").child(mFirebaseAuth.getCurrentUser().getUid());
        mVerified = mDatabase.getReference().child("mPokket").child("Verification").child(mFirebaseAuth.getCurrentUser().getUid()).child("verified");
    }


}
