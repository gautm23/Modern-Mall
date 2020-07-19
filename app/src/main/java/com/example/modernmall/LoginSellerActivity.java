package com.example.modernmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;




public class LoginSellerActivity extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private Button login;
    private TextView forgot;
    FirebaseAuth firebaseAuth;
    private String em;
    private String pa;
    FirebaseAuth.AuthStateListener firebaseListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_seller);
        firebaseAuth=FirebaseAuth.getInstance();
        email = (EditText) findViewById(R.id.fls_editText1);
        password = (EditText) findViewById(R.id.fls_editText2);
        login = (Button) findViewById(R.id.fls_button1);
        forgot=(TextView) findViewById(R.id.fls_textview1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                em=email.getText().toString();
                pa=password.getText().toString();
                firebaseAuth.signInWithEmailAndPassword(em,pa).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful())
                        {   finishAffinity();
                            PreferenceManager.getDefaultSharedPreferences(LoginSellerActivity.this).edit().putString(LoginActivity.sellerOrCustomer,"seller").apply();
                            Intent intent = new Intent(LoginSellerActivity.this, CatalogSellerActivity.class);//
                            startActivity(intent);
                        }
                        else
                        {
                            Toast.makeText(LoginSellerActivity.this,"Login Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(email.getText().toString().length()==0)
                { Toast.makeText(LoginSellerActivity.this,"enter the email id",Toast.LENGTH_SHORT);
                    return;
                }
                firebaseAuth.sendPasswordResetEmail(em).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(LoginSellerActivity.this,"Password reset link sent to your emil id",Toast.LENGTH_SHORT);
                        }
                        else
                        {
                            Toast.makeText(LoginSellerActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT);
                        }
                    }
                });
            }
        });
    }
}
