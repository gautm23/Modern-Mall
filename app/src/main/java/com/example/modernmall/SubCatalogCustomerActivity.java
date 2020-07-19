package com.example.modernmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SubCatalogCustomerActivity extends AppCompatActivity {
    public static  final String intentExtra="sccaintent";
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private ArrayList<String> inventories=new ArrayList<>();
    private ArrayList<String> inventoriesImages=new ArrayList<>();
    private ArrayList<String> inventoriesQuantity=new ArrayList<>();
    private ArrayList<String> inventoriesPrice=new ArrayList<>();
    private AdapterInventoryRecyclerView  adapterInventoryRecyclerView;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_catalog_customer);
        Toolbar toolbar=(Toolbar) findViewById(R.id.ascc_toolbar);
        floatingActionButton=(FloatingActionButton)findViewById(R.id.ascc_floatingActionButton);
        setSupportActionBar(toolbar);
        setTitle("inventories");  // inventories
        ActionBar actionBar=getSupportActionBar();
        firebaseAuth=FirebaseAuth.getInstance();
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.ascc_recyclerview);
        inventories.clear();inventoriesImages.clear();inventoriesPrice.clear();inventoriesQuantity.clear();
        String intentCategory= PreferenceManager.getDefaultSharedPreferences(SubCatalogCustomerActivity.this).getString(InventoryAddRemoveActivity.currentCategory,"");//getIntent().getStringExtra(SubCatalogCustomerActivity.intentExtra);
        adapterInventoryRecyclerView=new AdapterInventoryRecyclerView(inventories,inventoriesImages,inventoriesQuantity,inventoriesPrice);
        firebaseDatabase=FirebaseDatabase.getInstance();
        dbRef=firebaseDatabase.getReference("/categories/"+intentCategory+"/inventories");
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapterInventoryRecyclerView);
    }
    private ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    String str1=dataSnapshot.getKey();
                    String str2=dataSnapshot.child("imageUrl").getValue(String.class);
                    String str3=dataSnapshot.child("price").getValue(String.class);
                    String str4=dataSnapshot.child("quantity").getValue(String.class);
                    inventories.add(str1);
                    inventoriesImages.add(str2);
                    inventoriesPrice.add(str3);
                    inventoriesQuantity.add(str4);
                    adapterInventoryRecyclerView.notifyDataSetChanged();
        }

        @Override
        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.inventory_customer_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.cmi_profile :{
                Intent intent=new Intent(SubCatalogCustomerActivity.this,ProfileDescriptionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.cmi_sign_out :{  firebaseAuth.signOut();
                startActivity(new Intent(SubCatalogCustomerActivity.this,LoginActivity.class));
                finishAffinity();
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbRef.addChildEventListener(childEventListener);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SubCatalogCustomerActivity.this,FinalCartActivity.class));
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        dbRef.removeEventListener(childEventListener);
        inventories.clear();inventoriesImages.clear();inventoriesPrice.clear();inventoriesQuantity.clear();
    }
    void popUp(String s)
    {
        Toast.makeText(SubCatalogCustomerActivity.this,s, Toast.LENGTH_SHORT).show();
    }
}
