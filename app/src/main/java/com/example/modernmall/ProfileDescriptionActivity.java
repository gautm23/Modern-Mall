package com.example.modernmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileDescriptionActivity extends AppCompatActivity {
    private FirebaseDatabase firebaseDatabase;
    private TextView name;
    private TextView email;
    private TextView contact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_description);
        firebaseDatabase=FirebaseDatabase.getInstance();
        name=(TextView)findViewById(R.id.apd_textView1);
        email=(TextView)findViewById(R.id.apd_textView2);
        contact=(TextView)findViewById(R.id.apd_textView3);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseDatabase.getReference("/users/customers/"+LoginActivity.currentUserUID+"/personalData").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name.setText("Name: "+dataSnapshot.child("name").getValue(String.class));
                email.setText("Email: "+dataSnapshot.child("email").getValue(String.class));
                contact.setText("Contact: "+dataSnapshot.child("contact_number").getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
