package com.example.modernmall;

import androidx.annotation.NonNull;
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
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class LoginCustomerActivity extends AppCompatActivity {
    private EditText contact_number;
    private EditText otp;
    private Button sendbutton;
    private Button login;
    private FirebaseAuth firebaseAuth;
    private String pnum;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String> active_users_list=new ArrayList<>();
    private DatabaseReference dbRef;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken forceResendToken;
    private String verificationId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_customer);
        firebaseDatabase=FirebaseDatabase.getInstance();
        dbRef=firebaseDatabase.getReference("/contact_search");
        firebaseAuth=FirebaseAuth.getInstance();
        contact_number = (EditText) findViewById(R.id.flc_editText1);
        sendbutton=(Button) findViewById(R.id.flc_button1);
        otp = (EditText) findViewById(R.id.flc_editText2);
        login = (Button) findViewById(R.id.flc_button2);
        login.setEnabled(false);
    }
    private ValueEventListener singleValueEventListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
            active_users_list.clear();
            int abc = 0;

            if(dataSnapshot.exists()) {
                while (iterator.hasNext()) {           //we need to get the current no of user from here using DataSnapshot;
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    active_users_list.add(next.getKey());
                }

                for (int i = 0; i < active_users_list.size(); i++) {
                    if ((active_users_list.get(i)).equals(pnum)) {
                        abc = 1;
                        break;


                    }
                }
            }
            if(abc==0)
            {
                popUp("User doesn't exists please\n try signup !!");
                return;
            }
            else{
                // Toast.makeText(getApplicationContext(), "cancelled_1", Toast.LENGTH_SHORT).show();
                setUpVerificationCallbacks();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        pnum,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        LoginCustomerActivity.this,               // Activity (for callback binding)
                        verificationStateChangedCallbacks);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            popUp(databaseError.getMessage());
        }
    };
    private void setUpVerificationCallbacks()
    {
        verificationStateChangedCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                if	(e	instanceof FirebaseAuthInvalidCredentialsException)	{
                    //	Invalid	request
                    popUp("Invalid	credential:	"+e.getLocalizedMessage());
                }	else	if	(e	instanceof FirebaseTooManyRequestsException)	{
                    popUp("SMS	Quota	exceeded.");
                }
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId=s;
                forceResendToken=forceResendingToken;
                login.setEnabled(true);
            }
        };
    }
    private  void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential)
    {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    FirebaseUser user	=	task.getResult().getUser();
                    finishAffinity();
                    PreferenceManager.getDefaultSharedPreferences(LoginCustomerActivity.this).edit().putString(LoginActivity.currentUserUID,user.getUid()).apply();
                    PreferenceManager.getDefaultSharedPreferences(LoginCustomerActivity.this).edit().putString(CartActivity.userShoppingCart,"/users/customers/"+user.getUid()+"/shopping").apply();
                    PreferenceManager.getDefaultSharedPreferences(LoginCustomerActivity.this).edit().putString(LoginActivity.sellerOrCustomer,"customer").apply();
                    Intent intent=new Intent(LoginCustomerActivity.this,CatalogCustomerActivity.class);
                    startActivity(intent);
                }
                else	{
                    if	(task.getException()instanceof FirebaseAuthInvalidCredentialsException)	{
                        popUp("The	verification	code	entered	was	invalid");
                    }																				}
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pnum=contact_number.getText().toString();
                dbRef.addListenerForSingleValueEvent(singleValueEventListener);

            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(verificationId,otp.getText().toString());
                signInWithPhoneAuthCredential(phoneAuthCredential);
            }
        });
    }

    private  void popUp(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
