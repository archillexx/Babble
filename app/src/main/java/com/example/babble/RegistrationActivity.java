package com.example.babble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import de.hdodenhof.circleimageview.CircleImageView;

public class RegistrationActivity extends AppCompatActivity {

    TextView txt_login,signup_btn;
    CircleImageView profile_image;
    EditText reg_name,reg_email,reg_password,reg_cpassword;
    FirebaseAuth auth;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Uri imageUri;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String imageURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        txt_login=findViewById(R.id.txt_login);
        profile_image=findViewById(R.id.profile_image);
        reg_email=findViewById(R.id.reg_email);
        reg_name=findViewById(R.id.reg_name);
        reg_password=findViewById(R.id.reg_password);
        reg_cpassword=findViewById(R.id.reg_cpassword);
        signup_btn=findViewById(R.id.signup_btn);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name=reg_name.getText().toString();
                String email=reg_email.getText().toString();
                String password=reg_password.getText().toString();
                String cpassword=reg_cpassword.getText().toString();
                String status="hey there I'm using Babble";


                if(TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(cpassword)){
                    Toast.makeText(RegistrationActivity.this,"Please enter valid data",Toast.LENGTH_SHORT).show();
                }else if(!email.matches(emailPattern)){
                    reg_email.setError("Please enter valid email");
                    Toast.makeText(RegistrationActivity.this,"Please enter valid email",Toast.LENGTH_SHORT).show();
                }else if(!password.equals(cpassword)){
                    Toast.makeText(RegistrationActivity.this,"Password does not match",Toast.LENGTH_SHORT).show();
                }else if(password.length()<6){
                    Toast.makeText(RegistrationActivity.this,"Please enter password having more than 6 characters",Toast.LENGTH_SHORT).show();
                }else{
                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {

                                DatabaseReference reference = database.getReference().child("user").child(auth.getUid());
                                StorageReference storageReference = storage.getReference().child("upload").child(auth.getUid());
                                if (imageUri != null) {
                                    storageReference.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    imageURI=uri.toString();
                                                    Users users=new Users(auth.getUid(), name,email,imageURI,status);
                                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                                            }else{
                                                                Toast.makeText(RegistrationActivity.this,"Error in creating user",Toast.LENGTH_SHORT).show();

                                                            }
                                                        }
                                                    });

                                                }
                                            });
                                        }
                                    });
                                }else{
                                    String status="hey there I'm using Babble";
                                    imageURI="https://firebasestorage.googleapis.com/v0/b/babble-9b008.appspot.com/o/profile_image.png?alt=media&token=bcdd4929-1b68-4d3d-9252-8348fc70a694";
                                    Users users=new Users(auth.getUid(), name,email,imageURI,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()){
                                                startActivity(new Intent(RegistrationActivity.this,HomeActivity.class));
                                            }else{
                                                Toast.makeText(RegistrationActivity.this,"Error in creating user",Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                                }

                            }
                            else{
                                Toast.makeText(RegistrationActivity.this,"Something went wrong",Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });
        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegistrationActivity.this,LoginActivity.class));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10){
            if(data!=null){
                imageUri=data.getData();
                profile_image.setImageURI(imageUri);
            }
        }
    }
}