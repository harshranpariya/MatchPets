package com.example.matchpets;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ChooseLoginRegistrationActivity extends AppCompatActivity {

    private Button btnLogin, btnRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_login_registration);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }

        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChooseLoginRegistrationActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });
    }
}