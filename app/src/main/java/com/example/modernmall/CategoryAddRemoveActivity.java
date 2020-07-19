package com.example.modernmall;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class CategoryAddRemoveActivity extends AppCompatActivity {
        public static final String addRemove="addRemove";
        private EditText editText;
        private ImageView imageView;
        private Button   button;
        private String intentText;
        private StorageReference storageReference;
        private byte[]	uploadData;
        private int PERMISSION_REQUEST_CODE=1211;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_add_remove);
        Toolbar toolbar=(Toolbar) findViewById(R.id.acar_toolbar);
        setSupportActionBar(toolbar);
       editText=(EditText)findViewById(R.id.acar_textView1);
       imageView=(ImageView)findViewById(R.id.acar_imageView1);
       button=(Button)findViewById(R.id.acar_button1);
       intentText=getIntent().getStringExtra(CategoryAddRemoveActivity.addRemove);
       storageReference= FirebaseStorage.getInstance().getReference();
       if(intentText.equals("yes"))
       {
           setTitle("Add Categories");
       }
       else
       {
           setTitle("Remove Categories");
           imageView.setVisibility(View.GONE);
       }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 4444 && resultCode == RESULT_OK && data !=null ) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap= BitmapFactory.decodeFile(picturePath);
            imageView.setImageBitmap(bitmap);
            ByteArrayOutputStream stream	=	new	ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,	100,	stream);
            uploadData	=	stream.toByteArray();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(hasRuntimePermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Intent i = new Intent(
                            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, 4444);
                }
                else
                {
                    ActivityCompat.requestPermissions(CategoryAddRemoveActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
                    /*
                                    private void requestRuntimePermission(Activity activity, String runtimePermission, int requestCode)
                                    {
                                        ActivityCompat.requestPermissions(activity, new String[]{runtimePermission}, requestCode);
                                    }
                                 */
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editText.getText().length()>0)
                {    final StorageReference photoRef=	storageReference.child("/categories/"+editText.getText()+".jpg");
                    if(intentText.equals("yes"))
                    {
                       photoRef.putBytes(uploadData).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                photoRef.delete();
                                Toast.makeText(CategoryAddRemoveActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(CategoryAddRemoveActivity.this,CatalogSellerActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                if(task.isSuccessful())
                                {
                                    photoRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if(task.isSuccessful())
                                            {
                                                FirebaseDatabase.getInstance().getReference("/categories/"+editText.getText()+"/imageUrl").setValue(task.getResult().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        finish();
                                                        Intent intent=new Intent(CategoryAddRemoveActivity.this,CatalogSellerActivity.class);
                                                        startActivity(intent);
                                                    }
                                                });
                                            }
                                        }
                                    });



                                }
                            }
                        });
                    }
                    else
                    {
                        photoRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    FirebaseDatabase.getInstance().getReference("/categories/"+editText.getText()).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent=new Intent(CategoryAddRemoveActivity.this,CatalogSellerActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });

                                }
                            }
                        });
                    }


                }
            }
        });
    }

    private boolean hasRuntimePermission(Context context, String runtimePermission)     {
        boolean ret = false;
        //Get current android os version.
        int currentAndroidVersion = Build.VERSION.SDK_INT;

        // Build.VERSION_CODES.M's value is 23.
        if(currentAndroidVersion > 22)
        {
            // Only android version 23+ need to check runtime permission.
            if(ContextCompat.checkSelfPermission(context, runtimePermission) == PackageManager.PERMISSION_GRANTED)
                ret = true;
        }
        else
        {
            ret = true;
        }
        return ret;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,    String[] permissions, int[] grantResults)
    {         super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // If this is our permission request result.
        if(requestCode==PERMISSION_REQUEST_CODE)
        {
            if(grantResults.length > 0)
            {                 // Construct result message.
                StringBuffer msgBuf = new StringBuffer();
                int grantResult = grantResults[0];
                if(grantResult==PackageManager.PERMISSION_GRANTED)
                {
                    Intent i = new Intent(
                            Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(i, 4444);
                }
                else
                {
                    msgBuf.append("You denied below permissions : ");
                    Toast.makeText(getApplicationContext(), msgBuf.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}





