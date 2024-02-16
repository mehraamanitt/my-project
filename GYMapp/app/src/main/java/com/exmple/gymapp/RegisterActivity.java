package com.exmple.gymapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText nameInput,emailInput,phoneInput,passwordInput,confirmPasswordInput;
    RadioGroup cityInput;
    Button createAccountButton;

    //firebase stuff
    DatabaseReference reference;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //uid refernce
        emailInput=findViewById(R.id.email_input);
        passwordInput=findViewById(R.id.password_input);
        createAccountButton=findViewById(R.id.create_account_button);
        nameInput=findViewById(R.id.name_input);
        phoneInput=findViewById(R.id.phone_input);
        confirmPasswordInput=findViewById(R.id.confirm_password_input);
        cityInput=findViewById(R.id.city_input);

        //database objects
        auth=FirebaseAuth.getInstance();
        reference = FirebaseDatabase.getInstance().getReference();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email=emailInput.getText().toString();
                String password=passwordInput.getText().toString();
                String name=nameInput.getText().toString();
                String phone=phoneInput.getText().toString();
                String confirmPassword=confirmPasswordInput.getText().toString();

                //only proceed if when none of field are empty.
                if (notEmpty(name) && notEmpty(email) && notEmpty(phone) && notEmpty(password) && notEmpty(confirmPassword)){

                    //condition for registration fields validation
                    if (checkNameField(name) && checkEmailField(email) && password.equals(confirmPassword) && phone.length()==10){

                        //use firebase auth to create account
                        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    //creating dictionary for new user info
                                    HashMap<String,String> dict=new HashMap<>();
                                    dict.put("fullName",name);
                                    dict.put("email",email);
                                    dict.put("phone",phone);
                                    String city="";
                                    switch (cityInput.getCheckedRadioButtonId()){
                                        case R.id.jaipur_choice:
                                            city="jaipur";
                                            break;
                                        case R.id.bangalore_choice:
                                            city="bangalore";
                                            break;
                                    }
                                    dict.put("city",city);
                                    dict.put("admin","false");
                                    dict.put("password",password);
                                    reference.child("all_users_info").child(auth.getCurrentUser().getUid()).setValue(dict).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                Toast.makeText(RegisterActivity.this, "Account created", Toast.LENGTH_SHORT).show();
                                                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                            else{

                                                Toast.makeText(RegisterActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                    else
                    {
                        Toast.makeText(RegisterActivity.this, "Invalid details entered", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(RegisterActivity.this, "Fields can't be empty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //check any feild is empty or not.
    public Boolean notEmpty(String field)
    {
        if (field.trim().length()==0)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    //method to check name field having only characters.
     public Boolean checkNameField(String nameField)
     {
        boolean track=false;
        for (int i=0;i<nameField.length();i++)
        {
            char chr=nameField.charAt(i);
            if ((chr>64 && chr<91) || (chr>96 && chr<123) || (chr==32))
            {
                track = true;
            }
            else{
                track=false;
                break;
            }
        }
        return track;
     }

    //method to check email field having only intimetec.com as domain.
    public Boolean checkEmailField(String emailField)
    {
        for(int i=0;i<emailField.length();i++)
        {
            if (emailField.charAt(i)=='@')
            {
                String domain=emailField.substring(i,emailField.length());
                if (domain.equals("@intimetec.com"))
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        }
        return false;
    }
}