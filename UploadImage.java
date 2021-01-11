package lakshya.org.collegeadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class UploadImage extends AppCompatActivity {
    private Spinner imageCategory;
    private CardView selectImage;
    private Button btnUploadImage;
    private String category;
    private int REQ=1;
    private Bitmap bitmap;
    private ImageView ivImage;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl;
    ProgressDialog pd;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_image);
        pd= new ProgressDialog(this);
        reference= FirebaseDatabase.getInstance().getReference();
        storageReference=FirebaseStorage.getInstance().getReference();
        selectImage=findViewById(R.id.addGalleryImage);
        imageCategory=findViewById(R.id.imageCategory);
        btnUploadImage=findViewById(R.id.btnUploadImage);
        ivImage=findViewById(R.id.ivImage);
        String[] items =new String[]{"Select Category","Convocation","Independence Day","Other Events"};
        btnUploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bitmap==null){
                    Toast.makeText(UploadImage.this,"Please Upload Image",Toast.LENGTH_LONG).show();
                }else if(category.equals("Select Category")){
                    Toast.makeText(UploadImage.this,"Please Select Category",Toast.LENGTH_LONG).show();
                }else{
                    pd.setMessage("Uploading");
                    pd.show();
                    uploadImage();
                }
            }
        });
        imageCategory.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,items));
        imageCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                category=imageCategory.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }

            private void openGallery() {
                Intent selectImage=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(selectImage,REQ);
            }
        });


    }

    private void uploadImage() {
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50 ,baos);
        byte[] finalImage=baos.toByteArray();
        final StorageReference filePath;
        filePath=storageReference.child(finalImage+"jpg");
        final UploadTask uploadTask=filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UploadImage.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUrl=String.valueOf(uri);
                                    uploadData();
                                }
                            });
                        }
                    });
                }else {
                    pd.dismiss();

                    Toast.makeText(UploadImage.this,"Something Wrong",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void uploadData() {
        reference=reference.child(category);
        final String uniqueKey=reference.push().getKey();
        reference.child(uniqueKey).setValue(downloadUrl).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(UploadImage.this,"Uploaded Successfully",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(UploadImage.this,"Something Went wrong,",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ && resultCode==RESULT_OK){
            Uri uri=data.getData();
            try {
                bitmap=MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ivImage.setImageBitmap(bitmap);
        }
    }


    }
