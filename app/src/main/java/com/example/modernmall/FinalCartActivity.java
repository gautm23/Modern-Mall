package com.example.modernmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.lang.Integer.parseInt;

public class FinalCartActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {
    private ArrayList<String> item=new ArrayList<>();
    private ArrayList<String> unitPrice=new ArrayList<>();
    private ArrayList<String> quantity=new ArrayList<>();
    private ArrayList<String> totalPrice=new ArrayList<>();
    private ArrayList<String> firebaseAddressShopping=new ArrayList<>();
    private ArrayList<String> firebaseAddressInventory=new ArrayList<>();
    private ArrayList<String> actualQuantity=new ArrayList<>();
    private CheckoutRecyclerView checkoutRecyclerView;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference dbRef;
    private TextView textView;
    private Button   button;
    private int totalPay;
    private String custId,orderId,mId,generateChecksumUrl,verifyChecksumUrl;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private String inResponses1;
    private String inResponses2;
    private String inResponses3;
    private String inResponseStatus="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_cart);
        Toolbar toolbar=(Toolbar) findViewById(R.id.fcart_toolbar);
        textView=(TextView)findViewById(R.id.fcart_textView1);
        button=(Button)findViewById(R.id.fcart_button1);
        setSupportActionBar(toolbar);
        setTitle("Payment");  // payment
        mId="iIYyaQ88770341720249";
        generateChecksumUrl="https://paymentproject.000webhostapp.com/paytm/generateChecksum.php";
        verifyChecksumUrl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        ActionBar actionBar=getSupportActionBar();
        firebaseDatabase= FirebaseDatabase.getInstance();
        dbRef=firebaseDatabase.getReference(PreferenceManager.getDefaultSharedPreferences(FinalCartActivity.this).getString(CartActivity.userShoppingCart,""));
        recyclerView=(RecyclerView) findViewById(R.id.fcart_recyclerview);
        checkoutRecyclerView=new CheckoutRecyclerView(item,unitPrice,quantity,totalPrice);
        linearLayoutManager=new LinearLayoutManager(FinalCartActivity.this,LinearLayoutManager.VERTICAL,false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(checkoutRecyclerView);
    }
    private ChildEventListener childEventListener=new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            String s1=dataSnapshot.getKey();
            String s2=dataSnapshot.child("price").getValue(String.class);
            String s3=dataSnapshot.child("quantityInCart").getValue(String.class);
            String s4=dataSnapshot.child("purchasedValue").getValue(String.class);
            String fai=dataSnapshot.child("firebaseAddressInventory").getValue(String.class);
            String fas=dataSnapshot.child("firebaseAddressShopping").getValue(String.class);
            String aq=dataSnapshot.child("actualQuantity").getValue(String.class);
            item.add(s1);
            unitPrice.add(s2);
            quantity.add(s3);
            totalPrice.add(s4);
            firebaseAddressInventory.add(fai);
            firebaseAddressShopping.add(fas);
            actualQuantity.add(aq);
            totalPay+=parseInt(s4);
            textView.setText("Net Total: ₹"+totalPay);
            checkoutRecyclerView.notifyDataSetChanged();
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
    protected void onStart() {
        super.onStart();
        if(inResponseStatus.equals("TXN_SUCCESS"))
        {
            Intent intent=new Intent(FinalCartActivity.this,CatalogCustomerActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return;
        }
        item.clear();unitPrice.clear();quantity.clear();actualQuantity.clear();
        totalPrice.clear();firebaseAddressShopping.clear();firebaseAddressInventory.clear();
        totalPay=0;
        dbRef.addChildEventListener(childEventListener);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(item.size()!=0)
                {
                    if(PreferenceManager.getDefaultSharedPreferences(FinalCartActivity.this).getString(LoginActivity.isAnonymous,"").equals("yes")) {
                        new AlertDialog.Builder(FinalCartActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("ANONYMOUS USER").
                                setMessage("Register to proceed for payment?").setPositiveButton("LATER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                popUp("Register to proceed with the payment!!");
                            }
                        }).setNegativeButton("REGISTER", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               // startActivityForResult(new Intent(FinalCartActivity.this,SignUpCustomerActivity.class),1000);
                                //work to be done here
                            }
                        }).show();
                    }
                    else {
                        prePaymentDialogue();
                    }
                }
                else {
                    popUp("Currently nothing in the cart!!");
                }
            }
        });

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(inResponseStatus.equals("TXN_SUCCESS"))
        {
            for(int i=0;i<item.size();i++)
            { if(quantity.get(i).equals(actualQuantity.get(i)))
                {
                    FirebaseDatabase.getInstance().getReference(firebaseAddressInventory.get(i)).removeValue();
                    FirebaseDatabase.getInstance().getReference(firebaseAddressShopping.get(i)).removeValue();
                }
                else
                {
                    FirebaseDatabase.getInstance().getReference(firebaseAddressInventory.get(i)+"/quantity").setValue(Integer.toString(parseInt(actualQuantity.get(i))-parseInt(quantity.get(i))));
                    FirebaseDatabase.getInstance().getReference(firebaseAddressShopping.get(i)).removeValue();
                }


            }
        }

    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        inResponseStatus=inResponse.get("STATUS").toString();
         inResponses1=inResponse.get("TXNID").toString();
         inResponses2=inResponse.getString("TXNAMOUNT");
         inResponses3=inResponse.getString("TXNDATE");
       // Log.e("txn:",inResponses1+" "+inResponses2+" "+inResponses3);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(FinalCartActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(FinalCartActivity.this,new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},1234);
        }
        else
        {

            createPdf();
        }
        //popUp("Payment Transaction response: " + inResponse.toString());
       // Log.e("txn:",inResponse.toString());
    }

    @Override
    public void networkNotAvailable() {
        popUp("Network connection error: Check your internet connectivity");
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        popUp("Authentication failed: Server error" + inErrorMessage);
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        popUp("UI Error " + inErrorMessage);
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        popUp("Unable to load webpage " + inErrorMessage);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        popUp("Transaction cancelled");
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        popUp("Transaction cancelled: "+inErrorMessage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode)
        {
            case 1000 :{
                 if(resultCode==1)
                 {
                     prePaymentDialogue();
                 }
                 else
                 {
                     popUp("Register to continue with the transaction");
                 }
            }
            break;
            default : super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case 1234 :{
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    createPdf();
                }
            }
            break;
            case 101 : {
                if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    makePayment();
                }
            }
            break;

            default: {super.onRequestPermissionsResult(requestCode, permissions, grantResults);}
        }

    }
    private void prePaymentDialogue()
    {
        new AlertDialog.Builder(FinalCartActivity.this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Payment Mode").
                setMessage("Counter Payment or Self Banking?").setPositiveButton("SELF", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(FinalCartActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(FinalCartActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
                }
                else
                {
                    makePayment();
                }
            }
        }).setNegativeButton("COUNTER", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void makePayment()
    {
        custId= FirebaseAuth.getInstance().getUid();
        orderId= UUID.randomUUID().toString().substring(0,28);
        RequestQueue requestQueue= Volley.newRequestQueue(FinalCartActivity.this);
        StringRequest stringRequest=new StringRequest(Request.Method.POST, generateChecksumUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String CHECKSUMHASH=jsonObject.getString("CHECKSUMHASH");
                    PaytmPGService paytmPGService=PaytmPGService.getStagingService();
                    HashMap<String, String> paramMap = new HashMap<String, String>();
                    paramMap.put("MID", mId); //MID provided by paytm
                    paramMap.put("ORDER_ID", orderId);
                    paramMap.put("CUST_ID", custId);
                    paramMap.put("CHANNEL_ID", "WAP");
                    paramMap.put("TXN_AMOUNT", Integer.toString(totalPay));
                    paramMap.put("WEBSITE", "WEBSTAGING");
                    paramMap.put("CALLBACK_URL" ,verifyChecksumUrl);
                    paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                    paramMap.put("CHECKSUMHASH",CHECKSUMHASH);
                    PaytmOrder paytmOrder=new PaytmOrder(paramMap);
                    paytmPGService.initialize(paytmOrder,null);
                    paytmPGService.startPaymentTransaction(FinalCartActivity.this,true,true,FinalCartActivity.this);
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                popUp(error.getLocalizedMessage());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> paramMap = new HashMap<String, String>();
                //these are mandatory parameters
                paramMap.put("MID", mId); //MID provided by paytm
                paramMap.put("ORDER_ID", orderId);
                paramMap.put("CUST_ID", custId);
                paramMap.put("CHANNEL_ID", "WAP");
                paramMap.put("TXN_AMOUNT", Integer.toString(totalPay));
                paramMap.put("WEBSITE", "WEBSTAGING");
                paramMap.put("CALLBACK_URL" ,verifyChecksumUrl);
                paramMap.put("INDUSTRY_TYPE_ID", "Retail");
                return paramMap;
            }
        };
        requestQueue.add(stringRequest);
    }


    private void createPdf(){
        // create a new document
        PdfDocument document = new PdfDocument();
        // crate a page description
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        // start a page
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(15);
        DateFormat df = new SimpleDateFormat("dd-MM-yyyy");;
       // Date dateobj = new Date();
       // String dd=df.format(dateobj);
        canvas.drawText("Date: "+inResponses3.substring(0,10), 460, 40, paint);
        paint.setTextSize(30);
        //canvas.drawCircle(200,70,15,paint);
        canvas.drawText("MODERN MALL",220,80,paint);
        // canvas.drawCircle(450,70,15,paint);
        paint.setTextSize(15);
        canvas.drawLine(220,85,430,85,paint);
        paint.setTextSize(23);
        canvas.drawText("Cash Memo",250,120,paint);
        paint.setTextSize(17);
        canvas.drawText("Name:abc", 50, 150, paint);
        canvas.drawText("Txn Id:", 280, 150, paint);
        paint.setTextSize(10);
        canvas.drawText(inResponses1, 335, 150, paint);
        paint.setTextSize(17);
        canvas.drawText("Payment Mode: Self", 50, 175, paint);
        canvas.drawText("Counter Server: Nil", 280, 175, paint);
        canvas.drawLine(40,190,550,190,paint);
        canvas.drawLine(40,670,550,670,paint);
        canvas.drawLine(40,190,40,700,paint);
        canvas.drawLine(550,190,550,700,paint);
        paint.setTextSize(10);
        canvas.drawText("S.NO",60,205,paint);
        canvas.drawText("PRODUCT NAME",150,205,paint);
        canvas.drawText("QUANTITY",370,205,paint);
        canvas.drawText("UNIT PRICE",430,205,paint);
        canvas.drawText("TOTAL COST",490,205,paint);
        canvas.drawLine(40,215,550,215,paint);
        canvas.drawLine(90,190,90,670,paint);
        canvas.drawLine(365,190,365,670,paint);
        canvas.drawLine(425,190,425,670,paint);
        canvas.drawLine(485,190,485,700,paint);
        canvas.drawLine(40,700,550,700,paint);
        canvas.drawText("NET TOTAL COST(₹)",200,690,paint);
        canvas.drawText(inResponses2,490,690,paint);
        for(int i=0;i<item.size();i++)
        {
            canvas.drawText(Integer.toString(i+1),60,230+15*i,paint);
            canvas.drawText(item.get(i),100,230+15*i,paint);
            canvas.drawText(quantity.get(i),375,230+15*i,paint);
            canvas.drawText(unitPrice.get(i),435,230+15*i,paint);
            canvas.drawText(totalPrice.get(i),495,230+15*i,paint);
        }
        paint.setTextSize(23);
        canvas.drawText(" PAID",480,750,paint);
        Bitmap bitmap = ((BitmapDrawable)getResources().getDrawable(R.drawable.barcode)).getBitmap();
        canvas.drawBitmap(bitmap,50,720,paint);
        // finish the page
        document.finishPage(page);
        // write the document content
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/mypdf/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path+inResponses1+".pdf";
        File filePath = new File(targetPdf);
        try {
            document.writeTo(new FileOutputStream(filePath));
            Toast.makeText(this, "Cash Memo Generated", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("main", "error "+e.toString());
            Toast.makeText(this, "Something wrong: " + e.toString(),  Toast.LENGTH_LONG).show();
        }
        // close the document
        document.close();
    }

    void popUp(String s)
    {
        Toast.makeText(FinalCartActivity.this,s, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbRef.removeEventListener(childEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        item.clear();unitPrice.clear();quantity.clear();actualQuantity.clear();
        totalPrice.clear();firebaseAddressShopping.clear();firebaseAddressInventory.clear();
    }
}
