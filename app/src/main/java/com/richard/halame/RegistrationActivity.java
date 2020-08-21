package com.richard.halame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.HashMap;

public class RegistrationActivity extends AppCompatActivity {

    private EditText m_UserName, m_UserEmail, m_UserPassword;
    private ImageView m_btnRegister;
    private FirebaseAuth f_Auth;
    private FirebaseUser f_User;
    private DatabaseReference ref_db;

    private TextView m_goToLogin;

    private ProgressDialog progressDialog;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        //Initialize components
        initComponents();

        //Set Support Action Bar Settings
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //Add Onclick listener to btnRegister
        m_btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = m_UserName.getText().toString().trim();
                String txt_email = m_UserEmail.getText().toString().trim();
                String txt_password = m_UserPassword.getText().toString().trim();

                if(TextUtils.isEmpty(txt_username)){
                    m_UserName.setError("Username Field Cannot be Empty");
                }else if(TextUtils.isEmpty(txt_password)){
                    m_UserPassword.setError("Password Field Cannot be Empty");
                }else if(TextUtils.isEmpty(txt_email)){
                    m_UserEmail.setError("Email Field Cannot be Empty");
                }else{

                    progressDialog.setMessage("Processing...");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    register(txt_username, txt_email, txt_password);
                }

            }
        });

        m_goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

    }


    //////////////////////////////////////////////
    // This function initialises the ui components
    ///
    private void initComponents() {
        //Initialising Ui items
        m_btnRegister = findViewById(R.id.btnRegister);
        m_UserEmail = findViewById(R.id.userEmail);
        m_UserName = findViewById(R.id.userName);
        m_UserPassword = findViewById(R.id.userPassword);

        m_goToLogin = findViewById(R.id.goToLogin);

        toolbar = findViewById(R.id.toolBar);
        progressDialog = new ProgressDialog(RegistrationActivity.this);


        //Initialising Firebase Components
        f_Auth = FirebaseAuth.getInstance();
        ref_db = FirebaseDatabase.getInstance().getReference("Users");
    }

    ///////////////////////////////////////////////////////////////////////////
    // This method is responsible for registering the users and pushing their
    // Details into firebase realtime database for future reference
    public void register(final String username, String email, String password){

        f_Auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                    assert f_User != null;
                    f_User = f_Auth.getCurrentUser();
                    final String uid = f_User.getUid();

                    //Now Lets Push the user details into firebase realtime database
                    HashMap<String, String> hashMap = new HashMap<>();
                    hashMap.put("id", uid);
                    hashMap.put("imageURL", "default");
                    hashMap.put("username", username);

                    ref_db.child(uid).setValue(hashMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                                progressDialog.dismiss();
                                startActivity(intent);
                                finish();
                            }
                        }
                    });

                }else{
                    progressDialog.dismiss();
                    if(task.getException() != null)
                        Toast.makeText(RegistrationActivity.this, task.getException().getMessage() , Toast.LENGTH_SHORT).show();
                }
            }
        });
    }/////
    //Register User function ends here
    //////////////////////////////////////////////////////////////
}
