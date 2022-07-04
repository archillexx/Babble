package com.example.babble;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {

    String ReceiverImage,ReceiverUID,ReceiverName;
    CircleImageView profileImage;
    TextView receiverName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        ReceiverName=getIntent().getStringExtra("name");
        ReceiverImage=getIntent().getStringExtra("ReceiverImage");
        ReceiverUID=getIntent().getStringExtra("uid");


        profileImage=findViewById(R.id.profile_image);
        Picasso.get().load(ReceiverImage).into(profileImage);
      //  receiverName.setText(""+ReceiverName);
    }
}