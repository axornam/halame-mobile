package com.richard.halame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.common.internal.Objects;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class PhoneAuthentication extends AppCompatActivity {

    private EditText m_phoneNumber, m_Code;
    private ImageView m_sendNumberBtn, m_verifyBtn;
    private String codeSent;

    public static final String GHANA_COUNTRY_CODE = "+233";

    private FirebaseAuth m_Auth;
    private FirebaseUser m_User;
    private DatabaseReference ref_db;

    private ProgressDialog progressDialog;
    private Toolbar toolbar;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks stateChangedCallbacks;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_authenctication);

        //Initialize UI Components
        initComponents();

        //Set Support Action Bar Settings
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Phone Authentication");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Get The verification Number
        m_sendNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //This function will send the user the verification code
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setMessage("Processing...");
                progressDialog.show();
                sendVerificationCode();
            }
        });

        m_verifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setCanceledOnTouchOutside(true);
                progressDialog.setMessage("Processing...");
                progressDialog.show();
                manualSignInWithPhone();
            }
        });


        //Handle All Callbacks Sent By The PhoneAuthProvider.getInstance.VerifyPhonenumber method
        stateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                Toast.makeText(getApplicationContext(), "Auto Verification Completed Successfully", Toast.LENGTH_LONG).show();
                // This function automatically signs the user into the application provided the auto verification was complete
                signInWithPhoneAuthCredentials(phoneAuthCredential); //This function is called when verification is done automatically
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("Failed Request", "onVerificationFailed", e);

                // Show a message and update the UI
                Toast.makeText(getApplicationContext(), "Something Went Wrong, Try Again", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);

                codeSent = s;
            }
        };
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential phoneAuthCredential) {

        m_Auth.signInWithCredential(phoneAuthCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //Push User Information into firebase realtime database
                            assert m_User != null;
                            m_User = m_Auth.getCurrentUser();
                            final String uid = m_User.getUid();

                            HashMap<String, String> hashMap = new HashMap<>();
                            hashMap.put("id", uid);
                            hashMap.put("imageURL", "default");
                            hashMap.put("username", GHANA_COUNTRY_CODE + m_phoneNumber.getText().toString());

                            ref_db.child(uid).setValue(hashMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {

                                                Toast.makeText(PhoneAuthentication.this, "Successful Verification", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                        progressDialog.dismiss();
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                Toast.makeText(PhoneAuthentication.this, "Could Not Push Data Into The DataBase", Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });

                        } else {
//                        progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed Verification", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private void manualSignInWithPhone() {
        String code = m_Code.getText().toString();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(getApplicationContext(), "Please Enter the Code Sent Via SMS", Toast.LENGTH_SHORT).show();
            return;
        } else {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(codeSent, code);
            m_Auth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        //Push User Information into firebase realtime database
                        assert m_User != null;
                        m_User = m_Auth.getCurrentUser();
                        final String uid = m_User.getUid();

                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("id", uid);
                        hashMap.put("imageURL", "default");
                        hashMap.put("username", GHANA_COUNTRY_CODE + m_phoneNumber.getText().toString());

                        ref_db.child(uid).setValue(hashMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {

                                            Toast.makeText(getApplicationContext(), "Successful Verification", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            progressDialog.dismiss();
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(PhoneAuthentication.this, "Could Not Push Data Into The DataBase", Toast.LENGTH_SHORT).show();

                                        }
                                    }
                                });

                    } else {
//                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), "Failed Verification", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    ///This function is responsible for sending the verification Code to the user
    private void sendVerificationCode() {
        String phoneNumber = m_phoneNumber.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            m_phoneNumber.setError("This Field Cannot Be Empty");
            return;
        } else {

            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    GHANA_COUNTRY_CODE + phoneNumber,
                    60,
                    TimeUnit.SECONDS,
                    this,
                    stateChangedCallbacks
            );
            progressDialog.dismiss();
        }
    }


    //t/////////////////////////////////////////////////////////////////////////////////////
    // This function Will initialize the ui components
    private void initComponents() {
        // initialize text fields
        m_phoneNumber = findViewById(R.id.phoneNumber);
        m_Code = findViewById(R.id.verificationCode);

        toolbar = findViewById(R.id.toolBar);

        //initialize buttons
        m_sendNumberBtn = findViewById(R.id.getCodeBtn);
        m_verifyBtn = findViewById(R.id.verifyBtn);

        progressDialog = new ProgressDialog(PhoneAuthentication.this);

        m_Auth = FirebaseAuth.getInstance();
        ref_db = FirebaseDatabase.getInstance().getReference("Users");
    }
}
