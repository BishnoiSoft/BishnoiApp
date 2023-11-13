package com.bss.bishnoi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private EditText etPhoneNumber, etOTP;
    private Button btnSendOtp, btnSubmitOtp;
    private LinearLayout layoutSendOtp, layoutSubmitOtp;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser currentUser;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressBar mProgressBar;

    private TextView tvSeeTerms;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks =
            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                @Override
                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }

                @Override
                public void onVerificationFailed(@NonNull FirebaseException e) {
                    Toast.makeText(Login.this, "Verification failed ! " + e, Toast.LENGTH_SHORT).show();
                    Log.d("LoginActivity", "FirebaseException" + e);
                }

                @Override
                public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                    mVerificationId = verificationId;
                    mResendToken = token;
                    layoutSendOtp.setVisibility(View.GONE);
                    layoutSubmitOtp.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.GONE);
                }
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setWindow();
        initViews();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(Login.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            btnSendOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String phoneNum = etPhoneNumber.getText().toString();
                    if (phoneNum.length() != 10) {
                        Toast.makeText(Login.this, "Please Enter a valid Phone number", Toast.LENGTH_SHORT).show();
                    } else {
                        phoneNum = "+91" + phoneNum;
                        Log.d("Login Activity", "phone num : "+phoneNum);

                        mProgressBar.setVisibility(View.VISIBLE);
                        sendVerificationCode(phoneNum);
                    }
                }
            });

            btnSubmitOtp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String verificationCode = etOTP.getText().toString();
                    if (verificationCode.length() != 6) {
                        Toast.makeText(Login.this, "Please Enter a valid OTP", Toast.LENGTH_SHORT).show();
                    } else {
                        mProgressBar.setVisibility(View.VISIBLE);
                        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, verificationCode);
                        signInWithPhoneAuthCredential(credential);
                    }
                }
            });
        }
    }

    private void sendVerificationCode(String phoneNum) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNum)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            launchEditProfileActivity();
                        } else {
                            Toast.makeText(Login.this, "SignIn Failed", Toast.LENGTH_SHORT).show();
                            mProgressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void launchEditProfileActivity() {
        mProgressBar.setVisibility(View.GONE);
        Intent intent = new Intent(Login.this, EditProfile.class);
        intent.putExtra("ACTIVITY", "LOGIN");
        startActivity(intent);
        finish();
    }

    private void initViews() {
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etOTP = findViewById(R.id.etOtp);
        btnSendOtp = findViewById(R.id.btnSendOtp);
        btnSubmitOtp = findViewById(R.id.btnSubmitOtp);
        layoutSendOtp = findViewById(R.id.layout_send_otp);
        layoutSubmitOtp = findViewById(R.id.layout_submit_otp);
        mProgressBar = findViewById(R.id.mprogressBar);
        tvSeeTerms = findViewById(R.id.see_terms);

        etPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // When the EditText gains focus (is selected), clear the hint text
                    etPhoneNumber.setHint("");
                } else {
                    // When the EditText loses focus (is deselected), restore the hint text if no text has been entered
                    if (etPhoneNumber.getText().toString().isEmpty()) {
                        etPhoneNumber.setHint("फ़ोन नंबर दर्ज करे");
                    }
                }
            }
        });

        etOTP.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // When the EditText gains focus (is selected), clear the hint text
                    etOTP.setHint("");
                } else {
                    // When the EditText loses focus (is deselected), restore the hint text if no text has been entered
                    if (etOTP.getText().toString().isEmpty()) {
                        etOTP.setHint("ओटीपी दर्ज करे");
                    }
                }
            }
        });

        tvSeeTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, TermsConditions.class);
                startActivity(intent);
            }
        });

    }

    private void setWindow() {
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.saffron));
        window.setNavigationBarColor(ContextCompat.getColor(this, R.color.saffron));

        View decorView = window.getDecorView();
        int flags = decorView.getSystemUiVisibility();

        flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
        decorView.setSystemUiVisibility(flags);
    }

    @Override
    public void onBackPressed() {
        if (layoutSubmitOtp.getVisibility() == View.VISIBLE) {
            Intent intent = new Intent(this, Login.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }
}