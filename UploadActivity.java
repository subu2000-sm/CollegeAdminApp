package lakshya.org.collegeadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class UploadActivity extends AppCompatActivity {
    private CardView addImage;
    private final int REQ=1;
    private Bitmap bitmap;
    private ImageView ivNotice;
    private EditText noticeTitle;
    private Button btnUpload;
    private DatabaseReference reference;
    private StorageReference stoarageReference;
    String downloadUrl="";
    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference= FirebaseDatabase.getInstance().getReference();
        stoarageReference= FirebaseStorage.getInstance().getReference();
        pd=new ProgressDialog( this);
        setContentView(R.layout.activity_upload);

        addImage=findViewById(R.id.addImage);
        ivNotice=findViewById(R.id.ivNotice);
        noticeTitle=findViewById(R.id.noticeTitle);
        btnUpload=findViewById(R.id.btnUpload);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }


        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noticeTitle.getText().toString().isEmpty())
                {
                    noticeTitle.setError("Empty");
                    noticeTitle.requestFocus();
                }else if(bitmap==null){
                    uploadData();
                }else{
                    uploadImage();
                }
            }


        });
    }
    private void uploadData() {
        reference=reference.child("Notice");
        final String uniqueKey=reference.push().getKey();
        String title=noticeTitle.getText().toString();
        Calendar calForDate=Calendar.getInstance();
        SimpleDateFormat currentDate=new SimpleDateFormat("dd-MM-yy");
        String Date=currentDate.format(calForDate.getTime());
        Calendar calForTime=Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        String time=currentTime.format(calForTime.getTime());
        NoticeData noticeData= new NoticeData(title,downloadUrl,Date,time,uniqueKey);
        reference.child(uniqueKey).setValue(noticeData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                pd.dismiss();
                Toast.makeText(UploadActivity.this,"Notice Uploaded Successfully",Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();

                Toast.makeText(UploadActivity.this, e.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
    private void uploadImage() {
        pd.setMessage("Uploading");
        pd.show();
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50 ,baos);
        byte[] finalImage=baos.toByteArray();
        final StorageReference filePath;
        filePath=stoarageReference.child("Notice").child(finalImage+"jpg");
        final UploadTask uploadTask=filePath.putBytes(finalImage);
        uploadTask.addOnCompleteListener(UploadActivity.this, new OnCompleteListener<UploadTask.TaskSnapshot>() {
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

                   Toast.makeText(UploadActivity.this,"Something Wrong",Toast.LENGTH_LONG).show();
               }
            }
        });
    }

    private void openGallery() {
        Intent pickimage=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickimage,REQ);
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
            ivNotice.setImageBitmap(bitmap);
        }
    }
}