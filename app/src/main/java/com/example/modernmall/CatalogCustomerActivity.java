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
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class CatalogCustomerActivity extends AppCompatActivity {
    public  static  final String categorySelected="categorySelected";
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private ArrayList<String> category=new ArrayList<>();
    private ArrayList<String>categoryImages=new ArrayList<>();
    private  CatalogRecylcerView catalogRecylcerView;
    private int backPressCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog_customer);
        Toolbar toolbar=(Toolbar) findViewById(R.id.acc_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Categories");  // inventories
        ActionBar actionBar=getSupportActionBar();
        firebaseAuth=FirebaseAuth.getInstance();
        category.clear();categoryImages.clear();
        RecyclerView recyclerView=(RecyclerView) findViewById(R.id.acc_recyclerview);
        catalogRecylcerView=new CatalogRecylcerView(category,categoryImages);
        firebaseDatabase=FirebaseDatabase.getInstance();
        dbRef=firebaseDatabase.getReference("/categories");
        GridLayoutManager gridLayoutManager=new GridLayoutManager(this,2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(catalogRecylcerView);
    }
    private ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String str1=dataSnapshot.getKey();
            String str2=dataSnapshot.child("imageUrl").getValue(String.class);
            category.add(str1);
            categoryImages.add(str2);
            catalogRecylcerView.notifyDataSetChanged();
           // catalogRecylcerView.notifyItemInserted(category.size());
            // adapter.notifyDataSetChanged();
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
        getMenuInflater().inflate(R.menu.catalog_customer_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.ccm_profile :{
                Intent intent=new Intent(CatalogCustomerActivity.this,ProfileDescriptionActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.ccm_sign_out :{
                firebaseAuth.signOut();
                finishAffinity();
                startActivity(new Intent(CatalogCustomerActivity.this,LoginActivity.class));
                break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        backPressCount=0;
        dbRef.addChildEventListener(childEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbRef.removeEventListener(childEventListener);
        category.clear();categoryImages.clear();
    }

    @Override
    public void onBackPressed() {
        backPressCount++;
        if(backPressCount>1)
        {
            finishAffinity();
            super.onBackPressed();
        }
    }
}

