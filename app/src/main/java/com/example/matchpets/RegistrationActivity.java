package com.example.matchpets;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {
    private TextInputLayout edEmail, edPassword, edName;
    private Button btnRegister , btnSignUp;
    private RadioGroup rgType;

    private FirebaseAuth myAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        myAuth = FirebaseAuth.getInstance();
        getSupportActionBar().setTitle("Pet Registration");

        //when we register successfully user automatically loged in and we can move on the main page.

        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener(){

            //if everytime author will change than it call this functon
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                //if user is logged in
                if(user != null)
                {
                    Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    return;
                }
            }
        };

        edEmail = (TextInputLayout) findViewById(R.id.edEmail);
        edPassword = (TextInputLayout) findViewById(R.id.edPassword);
        edName = (TextInputLayout) findViewById(R.id.edName);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        rgType = (RadioGroup) findViewById(R.id.rgPetType);

        btnSignUp = (Button) findViewById(R.id.signUpScreen);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectId = rgType.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(radioButton.getText() == null)
                {
                    return;
                }

                final String name = edName.getEditText().getText().toString().trim();
                final String email = edEmail.getEditText().getText().toString().trim();
                final String password = edPassword.getEditText().getText().toString().trim();

                myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //if registration is not successful
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(RegistrationActivity.this, "sign up error", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                            //save data in database if successful
                            String userId = myAuth.getCurrentUser().getUid();

                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Pets").child(userId);
                            Map userInfo = new HashMap<>();
                            userInfo.put("type",radioButton.getText().toString());
                            userInfo.put("name", name);
                            userInfo.put("profileImageUrl", "default");


                            currentUserDb.updateChildren(userInfo);
                        }
                    }
                });
            }
        });
    }

    //for start the listener when activity starts
    @Override
    protected void onStart() {
        super.onStart();
        myAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    //for remove the listener when activity stops
    @Override
    protected void onStop() {
        super.onStop();
        myAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}