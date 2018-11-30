package com.ibrickedlabs.mpokket.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ibrickedlabs.mpokket.R;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.TimeUnit;

public class PhoneAuthActivity extends AppCompatActivity {
    public static final String TAG = PhoneAuthActivity.class.getSimpleName();
    private AppCompatButton sumbitButton;
    private AppCompatEditText phoneNumber;
    private AppCompatEditText verificationCode;
    private TextInputLayout phoneLayout;
    private TextInputLayout vcodeLayout;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken token;
    private FirebaseAuth mFirebaseAuth;
    Snackbar snackbar;
    LinearLayout pbarLayout;
    AVLoadingIndicatorView mAvLoadingIndicatorView;
    LinearLayout linearLayout;
    int buttonType = 0;//for swapping between Verify & proceed
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mcallbacks;

    private RelativeLayout topRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_auth);
        //Top Layout
        topRelativeLayout = (RelativeLayout) findViewById(R.id.relativeAuthLayout);
        sumbitButton = (AppCompatButton) findViewById(R.id.submitButton);
        phoneNumber = (AppCompatEditText) findViewById(R.id.phoneNumberInput);
        verificationCode = (AppCompatEditText) findViewById(R.id.verificationCodeInput);
        phoneLayout = (TextInputLayout) findViewById(R.id.phoneNumberTextLayout);
        vcodeLayout = (TextInputLayout) findViewById(R.id.verificationCodeTextLayout);
        linearLayout = (LinearLayout) findViewById(R.id.phoneNumerEntryLayout);
        linearLayout.setOnClickListener(null);
        pbarLayout = (LinearLayout) findViewById(R.id.pbarlayout);
        mAvLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avlbar);
        pbarLayout.setVisibility(View.GONE);
        mFirebaseAuth = FirebaseAuth.getInstance();


        phoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (phoneNumber.getText().toString().isEmpty()) {
                    phoneLayout.setEnabled(true);
                    phoneLayout.setError("Enter your Mobile number");
                } else {
                    phoneLayout.setErrorEnabled(false);
                }
            }
        });
        phoneNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //becz it will called soon we enter something & we will remove the error
                if (phoneNumber.getText().toString().isEmpty() || phoneNumber.getText().toString().length() < 10) {
                    phoneLayout.setEnabled(true);
                    phoneLayout.setError("Enter 10 digit Mobile number");
                } else {
                    phoneLayout.setErrorEnabled(false);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        sumbitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonType == 0) {
//                    Toast.makeText(PhoneAuthActivity.this, "Clicked", Toast.LENGTH_SHORT).show();
                    if (!TextUtils.isEmpty(phoneNumber.getText().toString().trim()) && phoneNumber.getText().toString().trim().length() == 10) {
                        attachAvlPbar();
                        sumbitButton.setEnabled(false);
                        String phoneNo = "+91" + phoneNumber.getText().toString().trim();
                        saveInSharedPreferences(phoneNumber.getText().toString().trim());//For checking if he is an admin
                        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                                phoneNo,
                                60,
                                TimeUnit.SECONDS,
                                PhoneAuthActivity.this,
                                mcallbacks
                        );
                    } else {

                        Snackbar snackbar = Snackbar
                                .make(topRelativeLayout, "Please Enter Valid Phone number", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                } else {
                    attachAvlPbar();
                    sumbitButton.setEnabled(false);
                    String vcode = verificationCode.getText().toString().trim();
                    Log.d(TAG,"HERE IS WHAT #"+vcode);
                    if (!TextUtils.isEmpty(vcode)) {
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, vcode);
                        signInWithPhoneAuthCredential(credential);
                    } else {
                        dettachAvlPbar();
                        sumbitButton.setEnabled(true);
                        Snackbar snackbar = Snackbar
                                .make(topRelativeLayout, "Please Enter Verification Code", Snackbar.LENGTH_SHORT);
                        snackbar.show();
                    }

                }

            }
        });
        mcallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.

               signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);
                snackbar.make(linearLayout, "Oops that may be a invalid phone number", Snackbar.LENGTH_SHORT).show();


            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId = s;
                token = forceResendingToken;
                buttonType = 1;
                verificationCode.setFocusableInTouchMode(true);
                // verificationCode.setEnabled(true);
                sumbitButton.setEnabled(true);
                sumbitButton.setText("Verify");
                dettachAvlPbar();


            }
        };


    }//onCreate


    //Saving the mobile number in shared Preferences
    private void saveInSharedPreferences(String phoneNumber) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("PHONE_NUMBER", phoneNumber);
        editor.apply();
    }

    private void attachAvlPbar() {
        verificationCode.setVisibility(View.INVISIBLE);//check this out
        vcodeLayout.setVisibility(View.INVISIBLE);
        pbarLayout.setVisibility(View.VISIBLE);
        mAvLoadingIndicatorView.show();
        sumbitButton.setVisibility(View.INVISIBLE);
    }

    private void dettachAvlPbar() {
        verificationCode.setVisibility(View.VISIBLE);
        vcodeLayout.setVisibility(View.VISIBLE);
        sumbitButton.setVisibility(View.VISIBLE);
        pbarLayout.setVisibility(View.GONE);
        mAvLoadingIndicatorView.hide();
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            if (isNewSignUp()) {

                                assignEligibiltyForNew();

                                Intent intent = new Intent(PhoneAuthActivity.this, AccountDetails.class);
                                startActivity(intent);
                                finish();

                            } else {

                                if (isAdmin()) {
                                    Intent intent = new Intent(PhoneAuthActivity.this, AdminActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {

                                    assignEligibiltyForReturingUsers();
                                    Intent intent = new Intent(PhoneAuthActivity.this, RequestMoney.class);
                                    startActivity(intent);
                                    finish();

                                }

                            }

                        } else {
                            dettachAvlPbar();
                            snackbar.make(linearLayout,"Error in credentials",Snackbar.LENGTH_SHORT).show();
                            finish();
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void assignEligibiltyForReturingUsers() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("mPokket").child("CumulativeAmount").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cumvalue = dataSnapshot.getValue(Integer.class);
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(PhoneAuthActivity.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putInt("VALUE", cumvalue);
                editor.apply();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean isAdmin() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String num = sharedPreferences.getString("PHONE_NUMBER", "");
        return num.equals("8008853854");
    }

    public boolean isNewSignUp() {
        FirebaseUserMetadata metadata = mFirebaseAuth.getCurrentUser().getMetadata();
        return metadata.getCreationTimestamp() == metadata.getLastSignInTimestamp();
    }

    private void assignEligibiltyForNew() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("mPokket").child("CumulativeAmount").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.setValue(0);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("VALUE", 0);
        editor.apply();
    }


}
