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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {
    private EditText edEmail, edPassword, edName;
    private Button btnRegister;
    private RadioGroup rgGender;

    private FirebaseAuth myAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        myAuth = FirebaseAuth.getInstance();

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

        edEmail = (EditText) findViewById(R.id.edEmail);
        edPassword = (EditText) findViewById(R.id.edPassword);
        edName = (EditText) findViewById(R.id.edName);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        rgGender = (RadioGroup) findViewById(R.id.rgGender);



        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectId = rgGender.getCheckedRadioButtonId();

                final RadioButton radioButton = (RadioButton) findViewById(selectId);

                if(radioButton.getText() == null)
                {
                    return;
                }

                final String name = edName.getText().toString();
                final String email = edEmail.getText().toString();
                final String password = edPassword.getText().toString();

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

                            DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference().child("Users").child(radioButton.getText().toString()).child(userId).child("Name");
                            currentUserDb.setValue(name);
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