package com.bss.bishnoi;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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

public class ShowProfile extends AppCompatActivity {

    TextView text_name, text_email, text_phone, text_gender, text_address, text_dob;
    ImageView profile_photo, arrow_back, edit_profile;// line_name, line_email, line_phone, line_gender, line_address, line_dob;
    Intent intent = getIntent();
    String preActivity;
    String fName, fEmail, uPhone, fPhone, fGender, fDob, fAddress, fProfileUrl;

    String full_name, email, gender, address, dob;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;



    private String MODE = "VIEW";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        setWindow();
        initViews();

        initFireStore();
        getUserData();
    }

    private void getUserData() {
        CollectionReference usersRef = firebaseFirestore.collection("users");

        DocumentReference userDocRef = usersRef.document(uPhone);
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()) {
                        // Document with the phone number already exists.
                        fName = (String) documentSnapshot.get("fName");
                        fEmail = (String) documentSnapshot.get("fEmail");
                        fPhone = (String) documentSnapshot.get("fPhone");
                        fGender = (String) documentSnapshot.get("fGender");
                        fDob = (String) documentSnapshot.get("fDob");
                        fAddress = (String) documentSnapshot.get("fAddress");
                        fProfileUrl = (String) documentSnapshot.get("fProfileUrl");

                        text_name.setText(fName);
                        text_email.setText(fEmail);
                        text_gender.setText(fGender);
                        text_phone.setText(uPhone);
                        text_dob.setText(fDob);
                        text_address.setText(fAddress);

                        Log.d("Show Profile", "Profile ImageUrl"+fProfileUrl);

                        if (fProfileUrl != null) {
                            Picasso.get().load(fProfileUrl).into(profile_photo);
                            profile_photo.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(ShowProfile.this, FullScreenImageView.class);
                                    intent.putExtra("PROFILE_URL", fProfileUrl); // Replace imageUri with the actual image URI
                                    startActivity(intent);
                                }
                            });
                        }

                    } else {
                        Toast.makeText(ShowProfile.this, "Profile does not exist.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void initFireStore() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {
            Intent intent_login = new Intent(ShowProfile.this, Login.class);
            startActivity(intent_login);
            finish();
        }

        uPhone = currentUser.getPhoneNumber();
        text_phone.setText(uPhone);
    }
    private void initViews() {
        profile_photo = findViewById(R.id.profile_image);
        arrow_back = findViewById(R.id.arrow_back);
        edit_profile = findViewById(R.id.edit_profile_btn_right);
//        line_name = findViewById(R.id.line_full_name);
//        line_email = findViewById(R.id.line_email);
//        line_phone = findViewById(R.id.line_phone);
//        line_gender = findViewById(R.id.line_gender);
//        line_dob = findViewById(R.id.line_dob);
//        line_address = findViewById(R.id.line_address);

        text_name = findViewById(R.id.full_name);
        text_email = findViewById(R.id.email);
        text_phone = findViewById(R.id.phone);
        text_gender = findViewById(R.id.gender);
        text_dob = findViewById(R.id.dob);
        text_address = findViewById(R.id.address);


        arrow_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowProfile.this, EditProfile.class);
                startActivity(intent);
            }
        });
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



    @Override
    public void onBackPressed() {
        Intent mainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(mainActivityIntent);
        finish();
    }

}