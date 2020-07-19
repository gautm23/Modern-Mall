package com.example.modernmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Iterator;

import static java.lang.Integer.parseInt;

public class CartActivity extends AppCompatActivity {
    public static final String cartintentstring1="cartintentstring1";
    public static final String cartintentstring2="cartintentstring2";
    public static final String cartintentstring3="cartintentstring3";
    public static final String userShoppingCart="userShoppingCart";
    private EditText quantityText;
    private Button continueWithShopping;
    private Button proceedToCheckout;
    private FirebaseDatabase firebaseDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar=(Toolbar) findViewById(R.id.cart_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add to cart...");  // inventories
        ActionBar actionBar=getSupportActionBar();
        quantityText=(EditText)findViewById(R.id.cart_editTextView1);
        continueWithShopping=(Button)findViewById(R.id.cart_button1);
        proceedToCheckout=(Button)findViewById(R.id.cart_button2);
        firebaseDatabase=FirebaseDatabase.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        continueWithShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String quantity=quantityText.getText().toString();
                if(quantity.length()>0)
                {
                 final   String currentUserUID1= PreferenceManager.getDefaultSharedPreferences(CartActivity.this).getString(LoginActivity.currentUserUID,""); // FirebaseAuth.getInstance().getCurrentUser().getUid()
                 final   String currentCategory1=PreferenceManager.getDefaultSharedPreferences(CartActivity.this).getString(InventoryAddRemoveActivity.currentCategory,"");
                 final   String currentInventory1=PreferenceManager.getDefaultSharedPreferences(CartActivity.this).getString(InventoryAddRemoveActivity.currentInventory,"");
                 final         String firebaseAddressInventory1="/categories/"+currentCategory1+"/inventories/"+currentInventory1;


                    firebaseDatabase.getReference(firebaseAddressInventory1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String arg1,arg3,arg4;
                            arg1=dataSnapshot.child("quantity").getValue(String.class);
                            arg3=dataSnapshot.child("price").getValue(String.class);
                            if(parseInt(quantity)>parseInt(arg1))
                            {
                                popUp("Please insert quantity less than"+arg1);
                                return;
                            }
                            arg4=Integer.toString(parseInt(arg3)*parseInt(quantity));
                            String firebaseAddressShopping1="/users/customers/"+currentUserUID1+"/shopping/"+currentInventory1;
                           // popUp("hello:"+firebaseAddressShopping1);
                            firebaseDatabase.getReference(firebaseAddressShopping1).setValue(new CartItems(arg1,firebaseAddressShopping1,firebaseAddressInventory1,arg3,arg4,quantity)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        Intent intent=new Intent(CartActivity.this,SubCatalogCustomerActivity.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);// add intentextra for returning back to inventory instead

                                    }

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
        proceedToCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String quantity=quantityText.getText().toString();
                if(quantity.length()>0 )
                {
                    final   String currentUserUID1= PreferenceManager.getDefaultSharedPreferences(CartActivity.this).getString(LoginActivity.currentUserUID,""); // FirebaseAuth.getInstance().getCurrentUser().getUid()
                    final   String currentCategory1=PreferenceManager.getDefaultSharedPreferences(CartActivity.this).getString(InventoryAddRemoveActivity.currentCategory,"");
                    final   String currentInventory1=PreferenceManager.getDefaultSharedPreferences(CartActivity.this).getString(InventoryAddRemoveActivity.currentInventory,"");
                    final   String firebaseAddressInventory1="/categories/"+currentCategory1+"/inventories/"+currentInventory1;


                    firebaseDatabase.getReference(firebaseAddressInventory1).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            String arg1,arg3,arg4;
                            arg1=dataSnapshot.child("quantity").getValue(String.class);
                            arg3=dataSnapshot.child("price").getValue(String.class);
                            if(parseInt(quantity)>parseInt(arg1))
                            {
                                popUp("Please insert quantity less than"+arg1);
                                return;
                            }
                            arg4=Integer.toString(parseInt(arg3)*parseInt(quantity));
                            String firebaseAddressShopping1="/users/customers/"+currentUserUID1+"/shopping/"+currentInventory1;
                            // popUp("hello:"+firebaseAddressShopping1);
                            firebaseDatabase.getReference(firebaseAddressShopping1).setValue(new CartItems(arg1,firebaseAddressShopping1,firebaseAddressInventory1,arg3,arg4,quantity)).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful())
                                    {
                                        startActivity(new Intent(CartActivity.this,FinalCartActivity.class));
                                        finish();

                                    }

                                }
                            });

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    void popUp(String s)
    {
        Toast.makeText(CartActivity.this,s, Toast.LENGTH_SHORT).show();
    }
}
