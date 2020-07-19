package com.example.modernmall;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    public static final String intentstring1="intentstring1";
    public static final String isAnonymous="isAnonymous";
    public static final String sellerOrCustomer="SellerOrCustomer";
    public static final String currentUserUID="currentUserUID";
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();

        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(LoginActivity.isAnonymous,"no").apply();
        firebaseListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged( FirebaseAuth firebaseAuth) {
                FirebaseUser user	=	firebaseAuth.getCurrentUser();
                if(user!=null)
                {
                    String sellerCustomer=PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString(LoginActivity.sellerOrCustomer,"");
                    if(sellerCustomer.equals("seller")) {
                        Intent intent = new Intent(LoginActivity.this, CatalogSellerActivity.class);//
                        startActivity(intent);
                    }
                    else
                    {
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString(LoginActivity.currentUserUID,user.getUid()).apply();
                        PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).edit().putString(CartActivity.userShoppingCart,"/users/customers/"+user.getUid()+"/shopping").apply();
                        Intent intent = new Intent(LoginActivity.this, CatalogCustomerActivity.class);//
                        startActivity(intent);
                    }
                }
            }
        };
    }
    public void Login(View view)
    {
        switch (view.getId())
        {
            case R.id.button1 :{ Intent intent=new Intent(LoginActivity.this,LoginSellerActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.button2 : { Intent intent=new Intent(LoginActivity.this,LoginCustomerActivity.class);
                startActivity(intent);
            }
            break;
            case R.id.button3 :{
                PreferenceManager.getDefaultSharedPreferences(this).edit().putString(LoginActivity.isAnonymous,"yes").apply();
                // To be implemented
                /*firebaseAuth.signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete( Task<AuthResult> task) {
                        if	(!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Authentication	failed.	" + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Intent intent = new Intent(LoginActivity.this, CatalogCustomerActivity.class);
                            //intent.putExtra(LoginActivity.intentstring1,"loginanonymous");
                            startActivity(intent);
                        }
                    }
                });*/

            }
            break;
            case R.id.button4 : {
                new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("SignUp").
                        setMessage("Are you a Seller or Customer??").setPositiveButton("Seller", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1=new Intent(LoginActivity.this,SignUpSellerActivity.class);
                        startActivity(intent1);
                    }
                }).setNegativeButton("Customer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent1=new Intent(LoginActivity.this,SignUpCustomerActivity.class);
                        startActivity(intent1);
                    }
                }).show();
            }
        }

    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(firebaseListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if	(firebaseListener	!=	null)
            firebaseAuth.removeAuthStateListener(firebaseListener);
    }
}
