package com.ibrickedlabs.mpokket.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.ibrickedlabs.mpokket.Data.Account;
import com.ibrickedlabs.mpokket.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.wang.avi.AVLoadingIndicatorView;

public class UploadActivity extends AppCompatActivity {
    private static final int GALLERY_CODE = 123;
    //TextViews
    private TextView studentName;
    private DatabaseReference showNamePath = FirebaseDatabase.getInstance().getReference().child("mPokket").child("UserDetails").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

    //Buttons
    private Button continueButton;

    //Pbar
    AVLoadingIndicatorView mAvLoadingIndicatorView;
    LinearLayout pbarlayout;

    AVLoadingIndicatorView mFirstLoadedData;
    LinearLayout pbarlayout2;


    //Inputs
    private String aadharNumberExtracted = "";
    private String addressInputExtracted = "";
    private String regNumberExtracted = "";
    private String bankaccExtracted = "";
    private String resultantUri = "";
    private Uri studentIdUri = null;

    //LinearLayouts
    private LinearLayout aadharLinearLayout;
    private LinearLayout addressLinearLayout;
    private LinearLayout studdentInfoLinearLayout;
    private LinearLayout bankAccLinearLayout;
    private LinearLayout studentIdLinearLayout;

    //TopLayout
    private RelativeLayout topUploadActRelLayout;

    //Fields
    private ImageView aadhaarIView;
    private ImageView addressIView;
    private ImageView studInfoIView;
    private ImageView bankaccIView;
    private ImageView studIdIView;

    //TextInputLayout
    private TextInputLayout aadharTextInputLayout;

    //EditTexts-Aadhar

    private AppCompatEditText aadharEditText;

    //EditTexts-Adress
    private AppCompatEditText addressNameEditText;
    private AppCompatEditText cityEditText;
    private AppCompatEditText stateEditText;
    private AppCompatEditText zipEditText;

    //EditTexts-StudentInfo
    private AppCompatEditText studInfoEditText;

    //EditTexts-BankAcc
    private AppCompatEditText bankAccEditText;


    //Firebase
    private DatabaseReference mDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private StorageReference mStorageReference;


    private static final String TAG = "UploadActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        //Top Layout
        topUploadActRelLayout = (RelativeLayout) findViewById(R.id.topUploadActivityLayout);

        //Button Intz
        continueButton = (Button) findViewById(R.id.continueButton);

        //Pbar Intz
        mAvLoadingIndicatorView = (AVLoadingIndicatorView) findViewById(R.id.avlbar);
        pbarlayout = (LinearLayout) findViewById(R.id.pbarlayout);

        //pbar Intz2
        mFirstLoadedData = (AVLoadingIndicatorView) findViewById(R.id.avlbar_2);
        pbarlayout2 = (LinearLayout) findViewById(R.id.pbarlayout_2);

        showUntilNameLoaded();//show Pbar untill the name loads
        getNameOfTheStudent();//calling to load the name

        //FirebaseIntz
        mDatabaseReference = FirebaseDatabase.getInstance().getReference().child("mPokket").child("UserDocuments");
        mFirebaseAuth = FirebaseAuth.getInstance();
        mStorageReference = FirebaseStorage.getInstance().getReference();
        //FieldsIntz
        aadhaarIView = (ImageView) findViewById(R.id.aadharVerification);
        addressIView = (ImageView) findViewById(R.id.addressVerification);
        studInfoIView = (ImageView) findViewById(R.id.studentInfoVerification);
        bankaccIView = (ImageView) findViewById(R.id.bankVerification);
        studIdIView = (ImageView) findViewById(R.id.studentIdVerification);

        //LinearLayouts Intz
        aadharLinearLayout = (LinearLayout) findViewById(R.id.aadharLinearLayout);
        addressLinearLayout = (LinearLayout) findViewById(R.id.addressLinearLayout);
        studdentInfoLinearLayout = (LinearLayout) findViewById(R.id.studentInfoLinearLayout);
        bankAccLinearLayout = (LinearLayout) findViewById(R.id.bankAccLinearLayout);
        studentIdLinearLayout = (LinearLayout) findViewById(R.id.studenIdLinearLayout);


        aadhaarIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(v);
            }
        });

        addressIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(v);
            }
        });
        studInfoIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(v);
            }
        });

        bankaccIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDialog(v);
            }
        });

        studIdIView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                intent.setType("image/*");
                Intent chooser = Intent.createChooser(intent, "Complete Action Using");
                startActivityForResult(chooser, GALLERY_CODE);

            }
        });


        //when the continue button is clicked

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                commitIntoFirebase();
            }
        });


    }

    private void getNameOfTheStudent() {
        studentName = (TextView) findViewById(R.id.nameOfClient);
        showNamePath.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                Account account = snapshot.getValue(Account.class);
                studentName.setText("Hi, " + account.getFirstName());
                DetachNameLoaded();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }

        });


    }

    private void DetachNameLoaded() {
        pbarlayout2.setVisibility(View.INVISIBLE);
        mFirstLoadedData.hide();
        continueButton.setVisibility(View.VISIBLE);
    }

    private void showUntilNameLoaded() {
        pbarlayout2.setVisibility(View.VISIBLE);
        mFirstLoadedData.show();
        continueButton.setVisibility(View.INVISIBLE);


    }

    //Uploading data into firebase
    private void commitIntoFirebase() {
        attachAvlBar();//Progress bar attached

        final String aadharToBeUploaded = aadharNumberExtracted;
        final String addressToBeUploaded = addressInputExtracted;
        final String studentInfoToBeUploaded = regNumberExtracted;
        final String bankAccToBeUploaded = bankaccExtracted;
        final String studentIdToBeUploaded = resultantUri;

        Log.d("UploadActivity.this", aadharToBeUploaded);
        Log.d("UploadActivity.this", addressToBeUploaded);
        Log.d("UploadActivity.this", studentIdToBeUploaded);
        Log.d("UploadActivity.this", bankAccToBeUploaded);
        Log.d("UploadActivity.this", studentIdToBeUploaded);

        if (!TextUtils.isEmpty(aadharToBeUploaded) && !TextUtils.isEmpty(addressToBeUploaded) && !TextUtils.isEmpty(regNumberExtracted) && !TextUtils.isEmpty(bankAccToBeUploaded) && !TextUtils.isEmpty(resultantUri)) {

            //Now push into data base,Since all are fields are not empty assign a boolean value;
            final String userId = mFirebaseAuth.getCurrentUser().getUid();
            final StorageReference filePath = mStorageReference.child("StudentId").child(studentIdUri.getLastPathSegment());
            filePath.putFile(studentIdUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    //returns the downloadable url of the image
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        //If the image uploaded successfully we get the link here
                        Uri downloadUri = task.getResult();
                        DatabaseReference currentDb = mDatabaseReference.child(userId);

                        currentDb.child("aadharNumber").setValue(aadharToBeUploaded);
                        currentDb.child("address").setValue(addressToBeUploaded);
                        currentDb.child("studentRegistrationNumber").setValue(studentInfoToBeUploaded);
                        currentDb.child("bankAccountNumber").setValue(bankAccToBeUploaded);
                        currentDb.child("studenId").setValue(resultantUri);

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("mPokket").child("Verification");
                        databaseReference.child(userId).child("verified").setValue(true);


                        dettachAvlBar();//dettaching here again
                        Snackbar.make(topUploadActRelLayout, "Verification Completed Successfully", Snackbar.LENGTH_SHORT).show();

                        startActivity(new Intent(UploadActivity.this, RequestMoney.class));
                        finish();//Take this to next Activity


                    } else {
                        dettachAvlBar();//dettached the pbar
                        Toast.makeText(UploadActivity.this, "Verification Failed" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();


                    }
                }
            });


        } else {
            dettachAvlBar();
            Snackbar snackbar = Snackbar
                    .make(topUploadActRelLayout, "Please upload all the documents", Snackbar.LENGTH_SHORT);
            snackbar.show();

        }


    }

    private void dettachAvlBar() {
        pbarlayout.setVisibility(View.INVISIBLE);
        mAvLoadingIndicatorView.hide();
        continueButton.setVisibility(View.VISIBLE);
        continueButton.setEnabled(true);
    }

    private void attachAvlBar() {
        pbarlayout.setVisibility(View.VISIBLE);
        mAvLoadingIndicatorView.show();
        continueButton.setEnabled(false);
        continueButton.setVisibility(View.INVISIBLE);
    }

    private void displayDialog(View v) {

        switch (v.getId()) {
            case R.id.aadharVerification:
                displayDialogForAadhar();
                break;
            case R.id.addressVerification:
                displayDialogForAadress();
                break;
            case R.id.studentInfoVerification:
                displayDialogForStudentInfo();
                break;
            case R.id.bankVerification:
                dispalyDialogForBankAccInfo();
                break;
        }


    }

    //dispalyDialogForBankAccInfo
    private void dispalyDialogForBankAccInfo() {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UploadActivity.this);

        final View view = getLayoutInflater().inflate(R.layout.bankacc_dialog, null);
        mBuilder.setView(view);
        mBuilder.setTitle("Enter Your Bank Account Number");
        mBuilder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bankAccEditText = (AppCompatEditText) view.findViewById(R.id.bankaccField);

                bankaccExtracted = bankAccEditText.getText().toString().trim();
                if (bankaccExtracted.length() == 11) {
                    bankAccLinearLayout.setBackgroundColor(getResources().getColor(R.color.uploaded_succ_bg));
                    bankaccIView.setImageResource(R.drawable.tick);
                    dialog.dismiss();
                } else {
                    bankaccExtracted = "";
                }


            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.bltxt_clr));

    }

    //displayDialogForStudentInfo
    private void displayDialogForStudentInfo() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UploadActivity.this);

        final View view = getLayoutInflater().inflate(R.layout.stud_info_dialog, null);
        mBuilder.setView(view);
        mBuilder.setTitle("Enter Your Registration Number");
        mBuilder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                studInfoEditText = (AppCompatEditText) view.findViewById(R.id.regnumField);

                regNumberExtracted = studInfoEditText.getText().toString().trim();
                if (regNumberExtracted.length() == 8) {
                    studdentInfoLinearLayout.setBackgroundColor(getResources().getColor(R.color.uploaded_succ_bg));
                    studInfoIView.setImageResource(R.drawable.tick);
                    dialog.dismiss();
                } else {
                    regNumberExtracted = "";
                }

            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.bltxt_clr));

    }

    //displayDialogForAadress
    private void displayDialogForAadress() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UploadActivity.this);

        final View view = getLayoutInflater().inflate(R.layout.address_dialog, null);
        mBuilder.setView(view);
        mBuilder.setTitle("Enter Your address");
        mBuilder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addressNameEditText = (AppCompatEditText) view.findViewById(R.id.addressField);
                cityEditText = (AppCompatEditText) view.findViewById(R.id.cityField);
                stateEditText = (AppCompatEditText) view.findViewById(R.id.stateField);
                zipEditText = (AppCompatEditText) view.findViewById(R.id.zipField);

                addressInputExtracted = addressNameEditText.getText().toString().trim() +
                        cityEditText.getText().toString().trim() +
                        stateEditText.getText().toString().trim() +
                        zipEditText.getText().toString().trim();
                //Checking here
                if (addressInputExtracted.length() > 0 && zipEditText.getText().toString().trim().length() == 6) {
                    addressLinearLayout.setBackgroundColor(getResources().getColor(R.color.uploaded_succ_bg));
                    addressIView.setImageResource(R.drawable.tick);
                    dialog.dismiss();
                } else {
                    aadharNumberExtracted = "";
                }


            }
        });

        AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.bltxt_clr));


    }

    //displayDialogForAadhar
    private void displayDialogForAadhar() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(UploadActivity.this);

        final View view = getLayoutInflater().inflate(R.layout.aadhar_layout, null);
        mBuilder.setView(view);
        mBuilder.setTitle("Enter Your 12-digit Aadhar Number");
        mBuilder.setPositiveButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mBuilder.setNegativeButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                aadharEditText = (AppCompatEditText) view.findViewById(R.id.enteraadharNumber);
                aadharTextInputLayout = (TextInputLayout) view.findViewById(R.id.addharTextInputLayout);
                aadharNumberExtracted = aadharEditText.getText().toString().trim();

                if (aadharNumberExtracted.length() == 12) {
                    aadharLinearLayout.setBackgroundColor(getResources().getColor(R.color.uploaded_succ_bg));
                    aadhaarIView.setImageResource(R.drawable.tick);
                    dialog.dismiss();
                } else {
                    aadharNumberExtracted = "";

                }

            }
        });


        AlertDialog dialog = mBuilder.create();

        dialog.show();
        dialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.bltxt_clr));

    }


    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {
            Uri profileURL = data.getData();
            CropImage.activity(profileURL).setAspectRatio(1, 1).setGuidelines(CropImageView.Guidelines.ON)
                    .start(this);


        }

        //cropped image
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                studentIdUri = resultUri;
                resultantUri = String.valueOf(resultUri);
                studentIdLinearLayout.setBackgroundColor(getResources().getColor(R.color.uploaded_succ_bg));
                studIdIView.setImageResource(R.drawable.tick);

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
            //TODO need implement if the pro is null keep default one
        }
    }//end of activityresult

    @Override
    protected void onResume() {
        super.onResume();


    }
}
