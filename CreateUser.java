package com.ibrickedlabs.mpokket.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrickedlabs.mpokket.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;

public class CreateUser extends AppCompatActivity {
    private static final String TAG = "CreateUser";
    private static final int GALLERY_CODE = 123;
    //layouts
    private TextInputLayout firstnameLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout lastnameLayout;
    //fields
    private ImageView profileView;
    private AppCompatEditText firstName;
    private AppCompatEditText lasstName;
    private AppCompatEditText email;
    private AppCompatEditText password;
    //buttons
    private Button signupButton;
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
        setContentView(R.layout.activity_create_user);
        //firebase intz
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseStorage = FirebaseStorage.getInstance();
        mstoStorageReference = mFirebaseStorage.getReference();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("UserDetails");

        //layouts intz
        firstnameLayout = (TextInputLayout) findViewById(R.id.firstNameTextLayout);
        lastnameLayout = (TextInputLayout) findViewById(R.id.lastNameTextLayout);
        emailLayout = (TextInputLayout) findViewById(R.id.emailTextLayout);
        passwordLayout = (TextInputLayout) findViewById(R.id.passwordTextLayout);

        //fields intz
        firstName = (AppCompatEditText) findViewById(R.id.firstnameField);
        lasstName = (AppCompatEditText) findViewById(R.id.lastnameField);
        email = (AppCompatEditText) findViewById(R.id.emailField);
        password = (AppCompatEditText) findViewById(R.id.passwordField);

        //Button intz
        profileView = (ImageView) findViewById(R.id.profileImage);
        signupButton = (Button) findViewById(R.id.proceedBtn);

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


        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateUser.this, "Clicked", Toast.LENGTH_SHORT).show();
                createAccountWithDetails();

            }
        });
    }

    /**
     * Extract the strings and create account
     */
    private void createAccountWithDetails() {
        final String fnameExtracted = firstName.getText().toString().trim();
        final String lnameExtracted = lasstName.getText().toString().trim();
        final String emailExtracted = email.getText().toString().trim();
        final String passwordExtracted = password.getText().toString().trim();
        System.out.println(fnameExtracted);
        System.out.println(lnameExtracted);
        System.out.println(emailExtracted);
        System.out.println(passwordExtracted);

        //checking the requirements

        if (!TextUtils.isEmpty(fnameExtracted) && !TextUtils.isEmpty(lnameExtracted) && !TextUtils.isEmpty(emailExtracted) && !TextUtils.isEmpty(passwordExtracted)) {


            mFirebaseAuth.createUserWithEmailAndPassword(emailExtracted, passwordExtracted).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                @Override
                public void onSuccess(AuthResult authResult) {
                    if (authResult != null) {
                        //remove
                        Toast.makeText(CreateUser.this, "created on successfull", Toast.LENGTH_SHORT).show();
                        final StorageReference filepath = mstoStorageReference.child("UserProfilePictures").child(resultUri.getLastPathSegment());

                        filepath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                return filepath.getDownloadUrl();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(CreateUser.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
                                    Uri uploadeduri = task.getResult();
                                    String userId = mFirebaseAuth.getCurrentUser().getUid();
                                    DatabaseReference currentDB = mDatabaseReference.child(userId);
                                    currentDB.child("firstName").setValue(fnameExtracted);
                                    currentDB.child("lastName").setValue(lnameExtracted);
                                    currentDB.child("profileImage").setValue(String.valueOf(uploadeduri));
                                    //TODO TAKE THIS TO NEXT ACTIVITY


                                }//end of if

                                else {
                                    Toast.makeText(CreateUser.this, "Account Creation Failed " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }//end of oncomplete
                        });


                    }
                }
            });


        }//end of checking if


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