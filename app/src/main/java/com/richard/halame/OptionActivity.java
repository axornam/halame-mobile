package com.richard.halame;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OptionActivity extends AppCompatActivity {

    private Button m_btnLogin, m_btnRegister, m_btnAuthenticate;    
    private FirebaseUser f_User;

    @Override
    protected void onStart() {
        super.onStart();

        //Check if User is already authenticated
        f_User = FirebaseAuth.getInstance().getCurrentUser();
        if (f_User != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        //init components
        m_btnRegister = findViewById(R.id.btnRegisterOption);
        m_btnLogin = findViewById(R.id.btnLoginOption);
        m_btnAuthenticate = findViewById(R.id.btnAuthenticateOption);

        //Add Onclick listeners to the buttons
        m_btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });

        m_btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            }
        });

        m_btnAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PhoneAuthentication.class));
            }
        });


    }
}
