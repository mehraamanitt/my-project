package com.exmple.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    TextView signupText;

    // firebase stuff
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput=findViewById(R.id.email_input);
        passwordInput=findViewById(R.id.password_input);
        loginButton=findViewById(R.id.login_button);
        signupText=findViewById(R.id.signup_text);

        //firebase init
        auth=FirebaseAuth.getInstance();//this is single tone class that means we can make its object only once.

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=emailInput.getText().toString();
                String password=passwordInput.getText().toString();
                if (emailNotNull(email) && emailNotNull(password)){
                    auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "SuccessFully Login", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                String failedMessage=task.getException().getLocalizedMessage();
                                Toast.makeText(LoginActivity.this, failedMessage, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    public Boolean emailNotNull(String email){
        if (email.trim().length()==0){
            return false;
        }
        else{
            return true;
        }
    }
}
