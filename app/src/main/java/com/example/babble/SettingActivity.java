package com.example.babble;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingActivity extends AppCompatActivity {

    CircleImageView setting_image;
    EditText settings_name,setting_status;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    ImageView save;
    Uri setImageUri;
    String email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        setting_image=findViewById(R.id.setting_image);
        setting_status=findViewById(R.id.setting_status);
        settings_name=findViewById(R.id.setting_name);

        save=findViewById(R.id.save);

        DatabaseReference reference=database.getReference().child("user").child(auth.getUid());
        StorageReference storageReference=storage.getReference().child("upload").child(auth.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                email=snapshot.child("email").getValue().toString();
                String name=snapshot.child("name").getValue().toString();
                String status=snapshot.child("status").getValue().toString();
                String image=snapshot.child("imageUri").getValue().toString();

                settings_name.setText(name);
                setting_status.setText(status);
                Picasso.get().load(image).into(setting_image);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setting_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 10);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=settings_name.getText().toString();
                String status=setting_status.getText().toString();
                if(setImageUri!=null){
                    storageReference.putFile(setImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String finalImageUri=uri.toString();
                                    Users users= new Users(auth.getUid(),name,email,finalImageUri,status);
                                    reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Toast.makeText(SettingActivity.this,"Data Successfully Updated",Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                                            }
                                            else{
                                                Toast.makeText(SettingActivity.this,"Something went WRONG",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });
                }else{
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String finalImageUri=uri.toString();
                            Users users= new Users(auth.getUid(),name,email,finalImageUri,status);
                            reference.setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(SettingActivity.this,"Data Successfully Updated",Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(SettingActivity.this,HomeActivity.class));
                                    }
                                    else{
                                        Toast.makeText(SettingActivity.this,"Something went WRONG",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }


            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==10){
            if(data!=null){
                setImageUri=data.getData();
                setting_image.setImageURI(setImageUri);
            }
        }
    }
}


