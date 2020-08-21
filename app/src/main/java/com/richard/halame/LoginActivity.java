package com.richard.halame;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText m_UserEmail, m_UserPassword;
    private ImageView m_btnLogin;
    private Toolbar toolbar;

    private TextView m_gotoSignUp;

    private ProgressDialog progressDialog;
    private FirebaseAuth f_Auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize ui components
        initComponents();

        //get activity support action bar
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //Add Onclick listener to the login button
        m_btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = m_UserEmail.getText().toString().trim();
                String password = m_UserPassword.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    m_UserEmail.setError("Email Cannot be Empty");
                }else if(TextUtils.isEmpty(password)){
                    m_UserPassword.setError("Password Cannot be Empty");
                }else {
                    progressDialog.setMessage("Processing...");
                    progressDialog.setCanceledOnTouchOutside(true);
                    progressDialog.show();
                    login(email, password);
                }
            }
        });

        m_gotoSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

    }



    ///////////////////////////////////////////////////////////////
    /// This function is intended to log the user into firebase and switch activity
    ///////////////
    private void initComponents(){
        //Initialize ui components
        m_UserEmail = findViewById(R.id.userEmail);
        m_UserPassword = findViewById(R.id.userPassword);
        toolbar = findViewById(R.id.toolBar);
        m_btnLogin = findViewById(R.id.btnLogin);

        m_gotoSignUp = findViewById(R.id.gotoSignUp);

        progressDialog = new ProgressDialog(LoginActivity.this);

        //Initialize firebase components
        f_Auth = FirebaseAuth.getInstance();
    }


    ///////////////////////////////////////////////////////////////////
    //// This function logs the user into to firebase and into the application
    //////////////
    private void login(String email, String password){
        f_Auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("userid", f_Auth.getCurrentUser().getUid());
                        progressDialog.dismiss();
                        startActivity(intent);
                        finish();
                    }else{
                        progressDialog.dismiss();
                        //could check for error code and display appropriate error messages
                        Toast.makeText(LoginActivity.this, "Failed to Login", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
