package com.example.modernmall;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

public class CheckOutCounterActivity extends AppCompatActivity {
    private TextView customer1;
    private TextView customer2;
    private Button getCustomer;
    private Button takePayment;
    private DatabaseReference dbRef;
    private ArrayList<String>customer=new ArrayList<>();
    private ArrayList<String>queueId=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_counter);
        Toolbar toolbar=(Toolbar) findViewById(R.id.acoc_toolbar);
        setSupportActionBar(toolbar);
        setTitle("CheckOutCounter");
        ActionBar actionBar=getSupportActionBar();
        customer1=(TextView)findViewById(R.id.acoc_textView2);
        customer2=(TextView)findViewById(R.id.acoc_textView1);
        getCustomer=(Button)findViewById(R.id.acoc_Button2);
        takePayment=(Button)findViewById(R.id.acoc_Button1);
        dbRef=FirebaseDatabase.getInstance().getReference("/counterQueue");
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCustomer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                funcionCall();
            }
        });
        if(customer.size()>0)
        {
            takePayment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseDatabase.getInstance().getReference("/counterQueue/"+queueId.get(0)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                funcionCall();
                            }
                        }
                    });

                }
            });
        }

    }

    void funcionCall()
    {
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> snapshotIterator = dataSnapshot.getChildren();
                Iterator<DataSnapshot> iterator = snapshotIterator.iterator();
                int count=0;
                customer.clear();queueId.clear();
                while (iterator.hasNext() && count<2) {           //we need to get the current no of user from here using DataSnapshot;
                    DataSnapshot next = (DataSnapshot) iterator.next();
                    customer.add(next.getValue(String.class));
                    queueId.add(next.getKey());
                    count++;
                }
                if(count==2)
                {
                    customer1.setText("1-> "+customer.get(0));
                    customer2.setText("2-> "+customer.get(1));
                }
                if(count==1)
                {
                    customer1.setText("1-> "+customer.get(0));
                    customer2.setText("2-> --");
                }
                if(count==0)
                {
                    customer1.setText("1-> --");
                    customer2.setText("2-> --");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
