package com.example.babble;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    TextView txt_signup;
    EditText login_email,login_password;
    TextView login_btn;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth=FirebaseAuth.getInstance();
        txt_signup=findViewById(R.id.txt_signup);
        login_btn=findViewById(R.id.login_btn);
        login_email=findViewById(R.id.login_email);
        login_password=findViewById(R.id.login_password);
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = login_email.getText().toString();
                String password = login_password.getText().toString();


                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"Enter valid credentials",Toast.LENGTH_SHORT).show();
                }else if(!email.matches(emailPattern)){
                    login_email.setError("Invalid Email");
                    Toast.makeText(LoginActivity.this,"Invalid Email",Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    login_password.setError("Invalid Password");
                    Toast.makeText(LoginActivity.this,"Invalid Password",Toast.LENGTH_SHORT).show();
                }else{
                    auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                            }
                            else{
                                Toast.makeText(LoginActivity.this,"Error when logging in",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });

        txt_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegistrationActivity.class));
            }
        });
    }
}