package com.ibrickedlabs.mpokket.Activities;

import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.ibrickedlabs.mpokket.R;

public class EmailLogin extends AppCompatActivity {
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private  FirebaseAuth.AuthStateListener mAuthStateListener;
    private static final String TAG = "EmailLogin";


    //Fields
    private AppCompatEditText username;
    private  AppCompatEditText password;

    //Layouts
    private TextInputLayout usernameLayout;
    private TextInputLayout passwordLayout;

    //Button
    private ImageButton goButton;
    private Button signupButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_login);

        username=(AppCompatEditText)findViewById(R.id.usernameField);
        password=(AppCompatEditText)findViewById(R.id.passwordField);

        usernameLayout=(TextInputLayout)findViewById(R.id.usernameTextLayout);
        passwordLayout=(TextInputLayout)findViewById(R.id.passwordTextLayout);

        goButton=(ImageButton)findViewById(R.id.goButton);
        signupButton=(Button) findViewById(R.id.signupButton);

        mFirebaseAuth=FirebaseAuth.getInstance();

        mAuthStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                mFirebaseUser=firebaseAuth.getCurrentUser();
                if(mFirebaseUser!=null){
                    //User signed in mode
                }
                else{
                    //signed out state

                }

            }
        };

        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userNameExtracted=username.getText().toString();
                String passwordExtracted=password.getText().toString();

                if (!TextUtils.isEmpty(userNameExtracted) && !TextUtils.isEmpty(passwordExtracted)) {
                    loginWithCredentials(userNameExtracted,passwordExtracted);

                }
                else{
                    //If the inputs are empty
                }
            }
        });

        //signup button
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    private void loginWithCredentials(String userNameExtracted, String passwordExtracted) {
        mFirebaseAuth.signInWithEmailAndPassword(userNameExtracted,passwordExtracted).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //logged in with the credentials
                }
                else{
                    //login with credentials failed account doesn't exists

                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mAuthStateListener!=null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}
