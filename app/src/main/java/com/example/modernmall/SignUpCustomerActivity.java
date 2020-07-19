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
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

public class SignUpCustomerActivity extends AppCompatActivity {
    private	static	final	String	TAG	=	"PhoneAuth";
    private EditText username;
    private EditText email;
    private EditText contact_number;
    private EditText verifycode;
    private String verificationId;
    private Button verifybutton;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private ArrayList<String> active_users_list=new ArrayList<>();
    private DatabaseReference dbRef;
    private String contact;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks verificationStateChangedCallbacks;
    private PhoneAuthProvider.ForceResendingToken forceResendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_customer);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        dbRef=firebaseDatabase.getReference("/contact_search");
        username = (EditText) findViewById(R.id.asup_editText1);
        email = (EditText) findViewById(R.id.asup_editText2);
        contact_number = (EditText) findViewById(R.id.asup_editText3);
        verifycode = (EditText) findViewById(R.id.asup_editText4);
        verifybutton = (Button) findViewById(R.id.asup_button2);
        verifybutton.setEnabled(false);
    }
    private ValueEventListener singleValueEventListener=new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
            Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
            active_users_list.clear();
            while (iterator.hasNext()) {           //we need to get the current no of user from here using DataSnapshot;
                DataSnapshot next = (DataSnapshot) iterator.next();
                active_users_list.add(next.getKey());
            }
            int abc = 0;

                for (int i = 0; i < active_users_list.size(); i++) {
                    if ((active_users_list.get(i)).equals(contact)) {
                        abc = 1;
                        break;
                    }
                }

            if(abc==1)
            {
                popUp("User exists please\n try signin !!");
                return;
            }
            else{
                // Toast.makeText(getApplicationContext(), "cancelled_1", Toast.LENGTH_SHORT).show();
                setUpVerificationCallbacks();
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        contact,        // Phone number to verify
                        60,                 // Timeout duration
                        TimeUnit.SECONDS,   // Unit of timeout
                        SignUpCustomerActivity.this,               // Activity (for callback binding)
                        verificationStateChangedCallbacks);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

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
                verifybutton.setEnabled(true);
            }
        };
    }

    private  void signInWithPhoneAuthCredential(PhoneAuthCredential phoneAuthCredential)
    {
        firebaseAuth.signInWithCredential(phoneAuthCredential).addOnCompleteListener(SignUpCustomerActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    final FirebaseUser user	=	task.getResult().getUser();
                    PreferenceManager.getDefaultSharedPreferences(SignUpCustomerActivity.this).edit().putString(LoginActivity.currentUserUID,user.getUid()).apply();
                    PreferenceManager.getDefaultSharedPreferences(SignUpCustomerActivity.this).edit().putString(CartActivity.userShoppingCart,"/users/customers/"+user.getUid()+"/shopping").apply();
                    firebaseDatabase.getReference("/users/customers/"+user.getUid()+"/personalData")
                            .setValue(new Profile(username.getText().toString(),contact,email.getText().toString())).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                               firebaseDatabase.getReference("/contact_search/"+contact).setValue(user.getUid()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Void> task) {
                                       if(task.isSuccessful())
                                       {
                                           finishAffinity();
                                           PreferenceManager.getDefaultSharedPreferences(SignUpCustomerActivity.this).edit().putString(LoginActivity.sellerOrCustomer,"customer").apply();
                                           Intent intent=new Intent(SignUpCustomerActivity.this,CatalogCustomerActivity.class);
                                           startActivity(intent);
                                       }
                                       else
                                       {
                                           user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                               @Override
                                               public void onComplete(@NonNull Task<Void> task) {
                                                   if(task.isSuccessful())
                                                   {
                                                       firebaseDatabase.getReference("/contact_search/"+contact).setValue(null);
                                                       firebaseDatabase.getReference("/users/customers/"+user.getUid()+"/personalData").setValue(null);
                                                       popUp("Error during creating account");
                                                   }
                                               }
                                           });
                                       }
                                   }
                               });
                            }
                            else
                            {
                               // firebaseAuth.getCurrentUser().
                                        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(task.isSuccessful())
                                        {   firebaseDatabase.getReference("/users/customers/"+user.getUid()+"/personalData").setValue(null);
                                            popUp("Error during creating account");
                                        }
                                    }
                                });
                            }
                        }

                    });

                }
                else	{
                    if	(task.getException()instanceof FirebaseAuthInvalidCredentialsException)	{
                        popUp("The	verification	code	entered	was	invalid");
                    }																				}
            }
        });
    }

    public void send_Button(View view)
    {
        String user=username.getText().toString();
        String em=email.getText().toString();
        contact=contact_number.getText().toString();
        if	(user.length()	==	0)	{
            popUp("Enter	an Username");
            return;				}
        else {
            if (em.length() < 6) {
                popUp("Email	must	be	at	least	6	characters");
                return;
            }
            else
            {

                if (contact.length() != 13)
                {   popUp("Enter correct Contact Details");
                    return;
                }
                else
                {
                    dbRef.addListenerForSingleValueEvent(singleValueEventListener);
                }
            }
        }
    }
    public void verify_Button(View view)
    {
        String text =verifycode.getText().toString();
        PhoneAuthCredential phoneAuthCredential= PhoneAuthProvider.getCredential(verificationId,text);
        signInWithPhoneAuthCredential(phoneAuthCredential);
    }
    private  void popUp(String msg)
    {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }
}
