package com.ibrickedlabs.mpokket.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import static maes.tech.intentanim.CustomIntent.customType;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.ibrickedlabs.mpokket.Data.Account;
import com.ibrickedlabs.mpokket.Data.Mpokket;
import com.ibrickedlabs.mpokket.R;
import com.wang.avi.AVLoadingIndicatorView;

public class RequestMoney extends AppCompatActivity {
    //Pbar
    AVLoadingIndicatorView mAvLoadingIndicatorView;
    LinearLayout pbarLayout;

    //Bottom navigation view
    private BottomNavigationView mBottomNavigationView;

    //Top Layout
    RelativeLayout relativeLayout;

    //Button
    private Button requestButton;

    //Textviews
    private TextView amountBorrowingLimitView;
    private TextView amountBorrowedView;

    //Spinner
    private Spinner requestingAmountSpinner;
    private Spinner repayDurationSpinner;

    //Images
    private ImageView forSpinnerOne;
    private ImageView forSpinnerTwo;

    //Extra added

    private String studentName = "";
    private String studentProfileUrl = "";
    private String studentRegNumber = "";

    //Input Values;

    private String amount;
    private String duration;
    private String amountBorrowed;

    //Firebase
    private FirebaseAuth mFirebaseAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseStorage mFirebaseStorage;


    private int summationAmount = 0;
    private boolean firstTime;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_money);

//        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.putInt("VALUE", 0);
//        editor.apply();


        final String amountArray[] = getResources().getStringArray(R.array.borrow_amount);
        final String durationArrayFromResource[] = getResources().getStringArray(R.array.repay);
        final String[] durationArray = new String[durationArrayFromResource.length];
        for (int i = 0; i < durationArrayFromResource.length; i++) {
            durationArray[i] = String.valueOf(durationArrayFromResource[i].charAt(0));
        }

        //Top leavel layout
        relativeLayout = (RelativeLayout) findViewById(R.id.topSnackbarLayout);
        //Button Intz
        requestButton = (Button) findViewById(R.id.requestButton);


        //Pbar strucutre
        mAvLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avlbar_RM);
        pbarLayout = (LinearLayout) findViewById(R.id.pbarlayout_RM);

        //get the requireddata
        fetchMyData();


        //Firebase Intz
        mDatabase = FirebaseDatabase.getInstance();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mDatabaseReference = mDatabase.getReference().child("mPokket").child("Requests").child(mFirebaseAuth.getCurrentUser().getUid());
        //Bottom Navigation View Intz
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.botomNaviagtion);


        //ImageView Intz
        forSpinnerOne = (ImageView) findViewById(R.id.moneyDropDown);
        forSpinnerTwo = (ImageView) findViewById(R.id.durationDropDown);

        //Textviews Intz
        amountBorrowingLimitView = (TextView) findViewById(R.id.totalBorrowingLimit);
        amountBorrowedView = (TextView) findViewById(R.id.amountBorrowed);

        //Spinner intz
        requestingAmountSpinner = (Spinner) findViewById(R.id.requestingAmount);
        repayDurationSpinner = (Spinner) findViewById(R.id.repayDuration);

        //Adapter1
        ArrayAdapter amountAdapter = ArrayAdapter.createFromResource(this, R.array.borrow_amount, R.layout.spinner_text);
        amountAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        requestingAmountSpinner.setAdapter(amountAdapter);
        //Adapter2
        ArrayAdapter repayAdapter = ArrayAdapter.createFromResource(this, R.array.repay, R.layout.spinner_duration);
        repayAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown);
        repayDurationSpinner.setAdapter(repayAdapter);

        //Onclick dropdown images for spinner 1
        forSpinnerOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestingAmountSpinner.performClick();

            }
        });
        //Onclick dropdown images for spinner 1
        forSpinnerTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repayDurationSpinner.performClick();
            }
        });

        //Onclick listeners
        requestingAmountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                amount = amountArray[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        repayDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                duration = durationArray[position];

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        requestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deployTheRequest();
            }
        });


        //Bottom Navigation
        Menu menu = mBottomNavigationView.getMenu();
        MenuItem item = menu.getItem(0);
        item.setChecked(true);

        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.request_money:
                        startActivity(new Intent(RequestMoney.this, RequestMoney.class));
                        customType(RequestMoney.this, "fadein-to-fadeout");

                        finish();

                        return true;
                    case R.id.recents:
                        startActivity(new Intent(RequestMoney.this, HistoryActivity.class));
                        customType(RequestMoney.this, "right-to-left");

                        finish();

                        return true;
                    case R.id.account:
                        startActivity(new Intent(RequestMoney.this, MyAccount.class));
                        customType(RequestMoney.this, "left-to-right");

                        finish();


                        return true;
                }
                return false;
            }
        });


    }

    private void fetchMyData() {
        attachProgressBar();
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
        DatabaseReference mDatabaseReference = mDatabase.getReference().child("mPokket").child("UserDetails").child(mFirebaseAuth.getCurrentUser().getUid());

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Account account = dataSnapshot.getValue(Account.class);
                studentName = account.getFirstName() + " " + account.getLastName();
                studentProfileUrl = account.getProfileImageUrl();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference mdbRef = FirebaseDatabase.getInstance().getReference().child("mPokket").child("UserDocuments").child(mFirebaseAuth.getCurrentUser().getUid()).child("studentRegistrationNumber");

        mdbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                studentRegNumber = dataSnapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        dettachProgessBar();

    }

    private void attachProgressBar() {
        pbarLayout.setVisibility(View.VISIBLE);
        mAvLoadingIndicatorView.show();
        requestButton.setVisibility(View.INVISIBLE);
    }

    private void dettachProgessBar() {
        pbarLayout.setVisibility(View.INVISIBLE);
        mAvLoadingIndicatorView.hide();
        requestButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    private void deployTheRequest() {
        attachProgressBar();
        amountBorrowed = amountBorrowedView.getText().toString().trim();
        int amntbrwd = Integer.valueOf(amountBorrowed);
        int amnt = Integer.valueOf(amount);
        amountBorrowed = String.valueOf(amntbrwd + amnt);


        //Perform only if the amount requested is lessthan 1000
        if (isItFirstTime()) {
//            Snackbar.make(relativeLayout, "First Time" + summationAmount, Snackbar.LENGTH_SHORT).show();
            assignEligibiltyForFirstTime(amnt);
            //Inserting in AMOUNT BORROWED node
            DatabaseReference amountBorrowedNode = FirebaseDatabase.getInstance().getReference().child("mPokket").child("TotalAmountBorrowed").child(mFirebaseAuth.getCurrentUser().getUid());
            amountBorrowedNode.push().setValue(amount);

            //This is asusual
            Mpokket mpokket = new Mpokket(studentName, studentRegNumber, studentProfileUrl, amountBorrowed, amount, duration, String.valueOf(System.currentTimeMillis()), false, mFirebaseAuth.getCurrentUser().getUid());


            //        Mpokket mpokket = new Mpokket(amountBorrowed, amount, duration, String.valueOf(System.currentTimeMillis()));
            DatabaseReference newRequest = mDatabaseReference.push();
            newRequest.setValue(mpokket).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
//                    Toast.makeText(RequestMoney.this, "Pushed", Toast.LENGTH_SHORT).show();
                    updateBorrowedView();//Upadte only if the admin admin accepts
                    repayDurationSpinner.setSelection(0);
                    requestingAmountSpinner.setSelection(0);
                    attachInterestDialog(amount, duration);
                    dettachProgessBar();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            // ...
                        }
                    });

        } else {//Not the first request

            if (isItEligible(amnt) <= 1000) {//So it is lessthan 1000

//                Snackbar.make(relativeLayout, "Second Time", Snackbar.LENGTH_SHORT).show();
                assignEligibiltyForSecondTime(amnt);
                //Inserting in AMOUNT BORROWED node
                DatabaseReference amountBorrowedNode = FirebaseDatabase.getInstance().getReference().child("mPokket").child("TotalAmountBorrowed").child(mFirebaseAuth.getCurrentUser().getUid());
                amountBorrowedNode.push().setValue(amount);

                //This is asusual
                Mpokket mpokket = new Mpokket(studentName, studentRegNumber, studentProfileUrl, amountBorrowed, amount, duration, String.valueOf(System.currentTimeMillis()), false, mFirebaseAuth.getCurrentUser().getUid());


                //        Mpokket mpokket = new Mpokket(amountBorrowed, amount, duration, String.valueOf(System.currentTimeMillis()));
                DatabaseReference newRequest = mDatabaseReference.push();
                newRequest.setValue(mpokket).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
//                        Toast.makeText(RequestMoney.this, "Pushed", Toast.LENGTH_SHORT).show();
                        updateBorrowedView();//Upadte only if the admin admin accepts
                        repayDurationSpinner.setSelection(0);
                        requestingAmountSpinner.setSelection(0);
                        attachInterestDialog(amount, duration);
                        dettachProgessBar();
                    }
                })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Write failed
                                // ...
                            }
                        });

            } else {//Value exceeeded
                dettachProgessBar();
                Snackbar.make(relativeLayout, "Request can't be taken" + summationAmount, Snackbar.LENGTH_SHORT).show();
            }


        }//End of else

    }

    private void attachInterestDialog(String amount, String duration) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View v = layoutInflater.inflate(R.layout.interest_layout, null);
        ImageView moneyTreeView = (ImageView) v.findViewById(R.id.moneyTreegif);
        TextView interestTextView = (TextView) v.findViewById(R.id.interestTV);
        /**
         * calculation part
         * PTR/100
         */
        Glide.with(this).load(R.drawable.moneytree).into(moneyTreeView);
        int p = Integer.valueOf(amount);
        int t = Integer.valueOf(duration);
        int interest = (p * t * 10) / 100;
        interestTextView.setText(" Your interest rate is Rs " + interest);


        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setView(v);
        mBuilder.setNegativeButton("I Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = mBuilder.show();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(this.getResources().getColor(R.color.bltxt_clr));
    }

    private void assignEligibiltyForSecondTime(int amnt) {
        attachProgressBar();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int a = sharedPreferences.getInt("VALUE", 0);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("mPokket").child("CumulativeAmount").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.setValue(a + amnt);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("VALUE", amnt + a);
        editor.apply();
//        Snackbar.make(relativeLayout, "assignEligibiltyForSecondTime" + (amnt + a), Snackbar.LENGTH_SHORT).show();

        dettachProgessBar();
    }

    private int isItEligible(int amnt) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int a = sharedPreferences.getInt("VALUE", 0);
//        Snackbar.make(relativeLayout, "eligible ? " + (amnt + a), Snackbar.LENGTH_SHORT).show();
        return (a + amnt);
    }

    private void assignEligibiltyForFirstTime(int val) {
        attachProgressBar();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("mPokket").child("CumulativeAmount").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.setValue(val);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("VALUE", val);
        editor.apply();
        dettachProgessBar();
    }

    private boolean isItFirstTime() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int val = sharedPreferences.getInt("VALUE", 0);
        return val == 0;
    }

    private void updateBorrowedView() {
        attachProgressBar();

        amountBorrowedView.setText("0");
        mDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Mpokket mpokket = dataSnapshot.getValue(Mpokket.class);
                if (mpokket == null) {
                    dettachProgessBar();
                }
                if (mpokket.isAccepted()) {
                    int prevValue = Integer.valueOf(amountBorrowedView.getText().toString().trim());
                    int CurrentValue = Integer.valueOf(mpokket.getAmountRequested());
                    int sum = prevValue + CurrentValue;

                    amountBorrowedView.setText("" + sum);
                    dettachProgessBar();

                } else {
                    dettachProgessBar();
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

        dettachProgessBar();
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        dettachProgessBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        dettachProgessBar();
        updateBorrowedView();


    }
}
