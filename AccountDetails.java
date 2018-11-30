package com.ibrickedlabs.mpokket.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrickedlabs.mpokket.Data.Account;
import com.ibrickedlabs.mpokket.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;

public class AccountDetails extends AppCompatActivity {
    private static final String TAG = "AccountDetails";
    private static final int GALLERY_CODE = 123;
    //pbar
    AVLoadingIndicatorView mAVL;
    LinearLayout pbarLayout;

    //LinearLayout
    LinearLayout toplayout;
    //layouts
    private TextInputLayout firstnameLayout;
    private TextInputLayout phoneNumberLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout lastnameLayout;
    //fields
    private ImageView profileView;
    private AppCompatEditText firstName;
    private AppCompatEditText lasstName;
    private AppCompatEditText email;
    private AppCompatEditText phoneNum;
    //buttons
    private Button proceedButton;
    //firebase
    private FirebaseUser mFirebaseUser;
    private FirebaseAuth mFirebaseAuth;
    DatabaseReference mDatabaseReference;
    FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private FirebaseStorage mFirebaseStorage;
    private StorageReference mstoStorageReference;

    //Uri
    Uri resultUri;
    Uri profileURL;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_details);
        //Pbar Intz
        pbarLayout = (LinearLayout) findViewById(R.id.pbarlayout_ADetails);
        mAVL = (AVLoadingIndicatorView) findViewById(R.id.avlbar_ADetails);

        //Top Layout
        toplayout = (LinearLayout) findViewById(R.id.topLinearLayout);
        toplayout.setOnClickListener(null);
        //firebase intz
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mstoStorageReference = mFirebaseStorage.getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("mPokket").child("UserDetails");

        //layouts intz
        firstnameLayout = (TextInputLayout) findViewById(R.id.firstNameTextLayout_AD);
        lastnameLayout = (TextInputLayout) findViewById(R.id.lastNameTextLayout_AD);
        emailLayout = (TextInputLayout) findViewById(R.id.emailTextLayout_AD);
        phoneNumberLayout = (TextInputLayout) findViewById(R.id.phoneNumberTextLayout_AD);

        //fields intz
        firstName = (AppCompatEditText) findViewById(R.id.firstnameField_AD);
        lasstName = (AppCompatEditText) findViewById(R.id.lastnameField_AD);
        email = (AppCompatEditText) findViewById(R.id.emailField_AD);
        phoneNum = (AppCompatEditText) findViewById(R.id.phonenumField_AD);

        //PerformValidations
        firstNameValidation();
        lastNameValidation();
        emailValidation();
        phoneNumberValidation();

        //Button intz
        profileView = (ImageView) findViewById(R.id.profileImage_AD);
        proceedButton = (Button) findViewById(R.id.singout_btn);

        profileView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.setType("image/*");
                Intent chooser = Intent.createChooser(intent, "Complete Action Using");
                startActivityForResult(chooser, GALLERY_CODE);
            }
        });


        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(AccountDetails.this, "Clicked", Toast.LENGTH_SHORT).show();
                attachPbar();
                createAccountWithDetails();

            }
        });
    }

    private void attachPbar() {
        pbarLayout.setVisibility(View.VISIBLE);
        mAVL.show();
        proceedButton.setVisibility(View.INVISIBLE);
    }

    private void deattachPbar() {
        pbarLayout.setVisibility(View.INVISIBLE);
        mAVL.hide();
        proceedButton.setVisibility(View.VISIBLE);
    }

    //Validation for Phone Number
    private void phoneNumberValidation() {
        phoneNum.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (phoneNum.getText().toString().isEmpty()) {
                    phoneNumberLayout.setEnabled(true);
                    phoneNumberLayout.setError("Enter your phone number");
                } else {
                    phoneNumberLayout.setErrorEnabled(false);

                }
            }
        });
        phoneNum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //becz it will called soon we enter something & we will remove the error
                if (phoneNum.getText().toString().isEmpty() || phoneNum.getText().toString().length() < 10) {
                    phoneNumberLayout.setEnabled(true);
                    phoneNumberLayout.setError("Enter your 10 digit mobile number");
                } else {
                    phoneNumberLayout.setErrorEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    //Validation for email
    private void emailValidation() {
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (email.getText().toString().isEmpty()) {
                    emailLayout.setEnabled(true);
                    emailLayout.setError("Enter your email");
                } else {
                    emailLayout.setErrorEnabled(false);

                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //becz it will called soon we enter something & we will remove the error
                if (email.getText().toString().isEmpty()) {
                    emailLayout.setEnabled(true);
                    emailLayout.setError("Enter your email");
                } else {
                    emailLayout.setErrorEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Validation for LastName
    private void lastNameValidation() {
        lasstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (lasstName.getText().toString().isEmpty()) {
                    lastnameLayout.setEnabled(true);
                    lastnameLayout.setError("Enter your last name");
                } else {
                    lastnameLayout.setErrorEnabled(false);

                }
            }
        });
        lasstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //becz it will called soon we enter something & we will remove the error
                if (lasstName.getText().toString().isEmpty()) {
                    lastnameLayout.setEnabled(true);
                    lastnameLayout.setError("Enter your last name");
                } else {
                    lastnameLayout.setErrorEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //Validation for FirstName
    private void firstNameValidation() {
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (firstName.getText().toString().isEmpty()) {
                    firstnameLayout.setEnabled(true);
                    firstnameLayout.setError("Enter your first name");
                } else {
                    firstnameLayout.setErrorEnabled(false);

                }
            }
        });
        firstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //becz it will called soon we enter something & we will remove the error
                if (firstName.getText().toString().isEmpty()) {
                    firstnameLayout.setEnabled(true);
                    firstnameLayout.setError("Enter your first name");
                } else {
                    firstnameLayout.setErrorEnabled(false);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    /**
     * Extract the strings and create account
     */
    @SuppressLint("ResourceAsColor")
    private void createAccountWithDetails() {
        final String fnameExtracted = firstName.getText().toString().trim();
        final String lnameExtracted = lasstName.getText().toString().trim();
        final String emailExtracted = email.getText().toString().trim();
        final String phoneNumberExtracted = phoneNum.getText().toString().trim();
//        System.out.println(fnameExtracted);
//        System.out.println(lnameExtracted);
//        System.out.println(emailExtracted);
//        System.out.println(phoneNumberExtracted);

        //checking the requirements

        if (!TextUtils.isEmpty(fnameExtracted) && !TextUtils.isEmpty(lnameExtracted) && !TextUtils.isEmpty(emailExtracted) && !TextUtils.isEmpty(phoneNumberExtracted) && resultUri != null) {

            final StorageReference filePath = mstoStorageReference.child("UserProfilePicture").child(resultUri.getLastPathSegment());
            filePath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    //retursn the downloadable url of the image
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {

                        String phoneNumberUsed = getFromSharedPreferences();

                        if (phoneNumberUsed.equals("8008853854")) {//If he is the admin

                            setBoolForAdmin();

                            //If the image uploaded successfully we get the link here
                            Uri downloadUri = task.getResult();
                            //returns the Uid
                            String userId = mFirebaseAuth.getCurrentUser().getUid();
                            DatabaseReference adminDb = FirebaseDatabase.getInstance().getReference().child("mPokket").child("AdminDetails").child(userId);

                            Account account = new Account(fnameExtracted, lnameExtracted, emailExtracted, phoneNumberExtracted, String.valueOf(downloadUri));
                            adminDb.setValue(account);
//                            Toast.makeText(AccountDetails.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
                            deattachPbar();


                            startActivity(new Intent(AccountDetails.this, AdminActivity.class));
                            finish();

                        } else {//for Normal Users

                            //If the image uploaded successfully we get the link here
                            Uri downloadUri = task.getResult();
                            //returns the Uid
                            String userId = mFirebaseAuth.getCurrentUser().getUid();
                            DatabaseReference currentDb = mDatabaseReference.child(userId);
                            Account account = new Account(fnameExtracted, lnameExtracted, emailExtracted, phoneNumberExtracted, String.valueOf(downloadUri));
                            currentDb.setValue(account);
//                            Toast.makeText(AccountDetails.this, "Inserted Successfully", Toast.LENGTH_SHORT).show();
                            deattachPbar();


                            startActivity(new Intent(AccountDetails.this, UploadActivity.class));
                            finish();
                        }//end of normal users


                    } else {
                        deattachPbar();
                        Toast.makeText(AccountDetails.this, "Details Insertion Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                    }
                }
            });


        }//end of checking if

        else {//If you miss any of the details
            deattachPbar();
            if (resultUri == null) {
                Snackbar snackbar = Snackbar
                        .make(toplayout, "Please upload the profile image", Snackbar.LENGTH_SHORT);
                snackbar.show();
            } else if (!TextUtils.isEmpty(fnameExtracted) || !TextUtils.isEmpty(lnameExtracted) || !TextUtils.isEmpty(emailExtracted) || !TextUtils.isEmpty(phoneNumberExtracted)) {
                deattachPbar();
                Snackbar snackbar = Snackbar
                        .make(toplayout, "Please fill all the fields", Snackbar.LENGTH_SHORT);
                snackbar.show();

            }

        }


    }

    private void setBoolForAdmin() {
        SharedPreferences sharedPreferences=PreferenceManager.getDefaultSharedPreferences(this);
       SharedPreferences.Editor editor= sharedPreferences.edit();
       editor.putBoolean("isAdmin",true);
       editor.apply();
    }

    private String getFromSharedPreferences() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        return sharedPreferences.getString("PHONE_NUMBER", "");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            profileURL = data.getData();
            CropImage.activity(profileURL).setAspectRatio(1, 1).setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);


        }

        //cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                Bitmap bitmap = null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                RoundedBitmapDrawable roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                roundedBitmapDrawable.setCircular(true);
                profileView.setImageDrawable(roundedBitmapDrawable);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
            //TODO need implement if the pro is null keep default one
        }
    }//end of activityresult

}
