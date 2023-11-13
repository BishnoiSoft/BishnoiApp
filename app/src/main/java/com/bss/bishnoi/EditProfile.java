package com.bss.bishnoi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    TextView text_edit_save, text_create_save, text_skip;
    EditText edit_name, edit_email, edit_address, text_phone, text_dob;
    Spinner spinner_gender;
    DatePicker picker_dob;
    ImageView profile_photo, update_photo, arrow_back;// line_name, line_email, line_phone, line_gender, line_address, line_dob;
    RelativeLayout layout_create_profile;

    LinearLayout layout_edit_profile;
    Intent intent = getIntent();
    String preActivity;
    String fName, fEmail, uPhone, fPhone, fGender, fDob, fAddress, fProfileUrl;

    String full_name, email, gender, address, dob;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;

    TextInputLayout email_input_layout, name_input_layout, address_input_layout;

    Button btnSaveProfile;
    CollectionReference usersRef;
    DocumentReference userDocRef;

    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        setWindow();
        initViews();

        initFireStore();
        setOnClickListeners();
        getIntentData();
        getUserData();
    }

    private void initFireStore() {

        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();


        if (currentUser == null) {
            Intent intent_login = new Intent(EditProfile.this, Login.class);
            startActivity(intent_login);
            finish();
        } else {
            uPhone = currentUser.getPhoneNumber();
            text_phone.setText(uPhone);
        }

        usersRef = firebaseFirestore.collection("users");
        userDocRef = usersRef.document(uPhone);
    }

    private void initViews() {
        layout_create_profile = findViewById(R.id.layout_create_profile);
        layout_edit_profile = findViewById(R.id.layout_edit_profile);

        profile_photo = findViewById(R.id.profile_image);
        update_photo = findViewById(R.id.edit_profile_image);
        arrow_back = findViewById(R.id.arrow_back);

        name_input_layout = (TextInputLayout) findViewById(R.id.filledNameTextField);
        email_input_layout = (TextInputLayout) findViewById(R.id.filledEmailField);
        address_input_layout = (TextInputLayout) findViewById(R.id.filledAddressField);

        btnSaveProfile = findViewById(R.id.btnSaveProfile);

        spinner_gender = findViewById(R.id.gender_spinner);

        edit_name = (EditText) findViewById(R.id.et_full_name);
        edit_email = (EditText) findViewById(R.id.et_email);
        edit_address = (EditText) findViewById(R.id.et_address);

        text_edit_save = findViewById(R.id.save_profile);
        text_create_save = findViewById(R.id.btn_save);
        text_skip = findViewById(R.id.skip);
        text_phone = findViewById(R.id.phone);
        text_dob = findViewById(R.id.et_dob);

        text_dob.setKeyListener(null);
        text_phone.setKeyListener(null);

        String[] genders = {"Select Gender", "Male", "Female"};
        adapter = new ArrayAdapter<>(this, R.layout.gender_spinner, genders);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        spinner_gender.setAdapter(adapter);
    }

    private void setOnClickListeners() {
        update_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        text_skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_skip = new Intent(EditProfile.this, MainActivity.class);
                startActivity(intent_skip);
            }
        });

        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        MaterialDatePicker.Builder materialDateBuilder = MaterialDatePicker.Builder.datePicker();

        materialDateBuilder.setTitleText("DATE OF BIRTH");

        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();

        text_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");
            }
        });

        materialDatePicker.addOnPositiveButtonClickListener(
                new MaterialPickerOnPositiveButtonClickListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onPositiveButtonClick(Object selection) {

                        // if the user clicks on the positive
                        // button that is ok button update the
                        // selected date
                        text_dob.setText(materialDatePicker.getHeaderText());
                        // in the above statement, getHeaderText
                        // is the selected date preview from the
                        // dialog
                    }
                });

        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                put_data_on_firestore(usersRef, true);
                uploadFile();
            }
        });
    }
    private void checkFilledData() {
        Toast.makeText(EditProfile.this, "Checking Filled Data", Toast.LENGTH_SHORT).show();

        full_name = edit_name.getText().toString();
        email = edit_email.getText().toString();
        gender = spinner_gender.getItemAtPosition(spinner_gender.getSelectedItemPosition()).toString();
        address = edit_address.getText().toString();
        dob = String.valueOf(text_dob.getText());

        if (full_name.isEmpty() || email.isEmpty() || gender.isEmpty() || address.isEmpty() || dob.isEmpty()) {
            Toast.makeText(this, "Please fill all the fields..", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "checkFilledData: " + full_name + " " + email + gender + address + dob);
    }

    private void getUserData() {
        Log.d(TAG, "getUserData: Getting User Data");
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        Log.d(TAG, "Database Exists");
                        // Document with the phone number already exists.
                        fName = (String) documentSnapshot.get("fName");
                        fEmail = (String) documentSnapshot.get("fEmail");
                        fPhone = (String) documentSnapshot.get("fPhone");
                        fGender = (String) documentSnapshot.get("fGender");
                        fDob = (String) documentSnapshot.get("fDob");
                        fAddress = (String) documentSnapshot.get("fAddress");
                        fProfileUrl = (String) documentSnapshot.get("fProfileUrl");

                        Log.d("Show Profile", "Profile ImageUrl"+fProfileUrl + uPhone + fName);
                        Log.d("Show Profile", "Profile Data" + uPhone + fName + fEmail + fGender + fDob + fAddress);


//                        text_gender.setText(fGender);
                        text_phone.setText(uPhone);
                        text_dob.setText(fDob);

                        name_input_layout.setHint(fName);
                        edit_name.setText(fName);

                        email_input_layout.setHint(fEmail);
                        edit_email.setText(fEmail);
                        address_input_layout.setHint(fAddress);
                        edit_address.setText(fAddress);

                        int position = -1; // Initialize to an invalid position

                        for (int i = 0; i < adapter.getCount(); i++) {
                            if (fGender.equals(adapter.getItem(i).toString())) {
                                position = i; // Match found, set the position
                                break; // Exit the loop
                            }
                        }

                        if (position != -1) {
                            // Set the selected position for the spinner
                            spinner_gender.setSelection(position);
                        } else {
                            // Handle the case when the string is not found in the spinner items
                        }

                        if (fProfileUrl != null) {
                            Picasso.get().load(fProfileUrl).into(profile_photo);
                            profile_photo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(EditProfile.this, FullScreenImageView.class);
                                    intent.putExtra("PROFILE_URL", fProfileUrl); // Replace imageUri with the actual image URI
                                    startActivity(intent);
                                }
                            });
                        }

                        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                put_data_on_firestore(usersRef, true);
                                uploadFile();
                            }
                        });

                        text_create_save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                put_data_on_firestore(usersRef, true);
                                uploadFile();
                            }
                        });

                    } else {
                        // Document with the phone number does not exists.
                        btnSaveProfile.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                put_data_on_firestore(usersRef, false);
                            }
                        });

                        text_edit_save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                put_data_on_firestore(usersRef, false);
                            }
                        });
                    }
                }
            }
        });
    }

    private void put_data_on_firestore(CollectionReference usersRef, Boolean update) {
        checkFilledData();

        DocumentReference userDocRef = usersRef.document(uPhone);
        Map<String, Object> userProfile = new HashMap<>();
        userProfile.put("fName", full_name);
        userProfile.put("fPhone", uPhone);
        userProfile.put("fEmail", email);
        userProfile.put("fGender", gender);
        userProfile.put("fDob", dob);
        userProfile.put("fAddress", address);

        if (!update) {
            userDocRef.set(userProfile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            build_ok_alert_dialog("Successful", "Your profile has created successfully.", Boolean.TRUE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            build_ok_alert_dialog("Failed", "Your profile could not be created..", Boolean.TRUE);
                        }
                    });
        } else {
            userDocRef.update(userProfile)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            build_ok_alert_dialog("Successful","Your profile updated successfully.", Boolean.TRUE);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            build_ok_alert_dialog("Failed", "Your profile could not be updated.", Boolean.TRUE);
                        }
                    });
        }
    }
    private void build_ok_alert_dialog(String title, String message, Boolean goToMainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfile.this);
        builder.setMessage(message)
                .setTitle(title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        if (goToMainActivity) {
                            Intent intent_main_activity = new Intent(EditProfile.this, MainActivity.class);
                            startActivity(intent_main_activity);
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.white));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }

    private void getIntentData() {
        String activity = "";

        try {
            activity = getIntent().getStringExtra("ACTIVITY");
            if (activity.equals("LOGIN")) {
                layout_create_profile.setVisibility(View.VISIBLE);
                layout_edit_profile.setVisibility(View.GONE);
            } else {
                layout_create_profile.setVisibility(View.GONE);
                layout_edit_profile.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            Log.d(TAG, "getIntentData: " + e);
            layout_create_profile.setVisibility(View.GONE);
            layout_edit_profile.setVisibility(View.VISIBLE);
        }

        if (intent != null) {
            preActivity = intent.getStringExtra("activity");
            if (preActivity.equals("login")) {
                layout_create_profile.setVisibility(View.VISIBLE);
                layout_edit_profile.setVisibility(View.GONE);
            } else {
                layout_create_profile.setVisibility(View.GONE);
                layout_edit_profile.setVisibility(View.VISIBLE);
            }
        } else {
            layout_create_profile.setVisibility(View.GONE);
            layout_edit_profile.setVisibility(View.VISIBLE);
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(profile_photo);
        }
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference();
            StorageReference imagesRef = storageRef.child("profile_images");
            StorageReference fileRef = imagesRef.child(System.currentTimeMillis()
                    + "." + getFileExtension(mImageUri));

            fileRef.putFile(mImageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String downloadUrl = uri.toString();

                                            CollectionReference usersRef = firebaseFirestore.collection("users");

                                            DocumentReference userDocRef = usersRef.document(uPhone);
                                            Map<String, Object> profileRef = new HashMap<>();
                                            profileRef.put("fProfileUrl", downloadUrl);
                                            userDocRef.update(profileRef)
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void unused) {
                                                            getUserData();
                                                            build_ok_alert_dialog("Successful", "Your profile photo updated successfully", Boolean.FALSE);
                                                        }
                                                    });

                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e(TAG, "File Upload Failure: ", e);
                            build_ok_alert_dialog("Failure", "Some error occured while uploading", Boolean.FALSE);
                        }
                    });
        } else {
            //build_ok_alert_dialog("No image selected", "Please pick a image", Boolean.FALSE);
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }


}