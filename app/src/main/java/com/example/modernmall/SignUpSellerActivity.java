package com.example.modernmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpSellerActivity extends AppCompatActivity {
    private EditText username;
    private EditText email;
    private EditText contact_number;
    private EditText password;
    private Button button;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private String em;
    private String co;
    private String us;
    private String pa;
    private DatabaseReference dbRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_seller);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        dbRef=firebaseDatabase.getReference("/users/sellers");
        username = (EditText) findViewById(R.id.sus_editText11);
        email = (EditText) findViewById(R.id.sus_editText2);
        contact_number = (EditText) findViewById(R.id.sus_editText3);
        password = (EditText) findViewById(R.id.sus_editText4);
        button = (Button) findViewById(R.id.sus_butt3);
    }
    public  void signUpSeller(View view) {
        us= username.getText().toString();
        em= email.getText().toString();
        co= contact_number.getText().toString();
        pa= password.getText().toString();
        if (us.length() == 0) {
            popUp("Enter	an Username");
            return;
        } else if (em.length() < 6) {
            popUp("Email	must	be	at	least	6	characters");
            return;
        } else if (co.length() != 13) {
            popUp("Enter correct Contact Details");
            return;
        } else if (pa.length() < 6) {
            popUp("Password must be atleast 6 characters");
            return;
        } else {

            firebaseAuth.createUserWithEmailAndPassword(em, pa).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        popUp(task.getException().getMessage());
                    }
                    else {
                        final FirebaseUser fuser=task.getResult().getUser();
                        fuser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful())
                                {
                                    popUp("Account Created, Please verify by clicking the link send on mail");
                                    firebaseDatabase.getReference("/users/sellers/" + fuser.getUid() + "/personalData")
                                            .setValue(new Profile(us, co, em), new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                    if (databaseError == null) {

                                                        finishAffinity();
                                                        PreferenceManager.getDefaultSharedPreferences(SignUpSellerActivity.this).edit().putString(LoginActivity.sellerOrCustomer,"seller").apply();
                                                        Intent intent = new Intent(SignUpSellerActivity.this, CatalogSellerActivity.class);
                                                        startActivity(intent);
                                                    } else {
                                                        popUp(databaseError.getMessage());
                                                    }
                                                }
                                            });
                                }
                                else
                                {   final String ss=task.getException().getMessage();
                                    fuser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful())
                                            {   firebaseDatabase.getReference("/users/sellers/" + fuser.getUid() + "/personalData").setValue(null);
                                                popUp(ss);
                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void onStop() {
        super.onStop();
    }
    private  void popUp( String msg)
    {
        Toast.makeText(SignUpSellerActivity.this,msg,Toast.LENGTH_SHORT).show();
    }
}
