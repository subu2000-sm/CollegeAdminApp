package lakshya.org.collegeadmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
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

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class UploadPdf extends AppCompatActivity {
    private CardView addPdf;
    private final int REQ = 1;
    private Uri pdfData;
    private EditText PdfTitle;
    private Button btnUploadPdf;
    private DatabaseReference reference;
    private StorageReference storageReference;
    String downloadUrl = "";
    private String pdfName,title;
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_pdf);
        reference = FirebaseDatabase.getInstance().getReference();
        storageReference = FirebaseStorage.getInstance().getReference();
        addPdf = findViewById(R.id.addPdf);
        PdfTitle = findViewById(R.id.PdfTitle);

        btnUploadPdf = findViewById(R.id.btnUploadPdf);
        addPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        btnUploadPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                title = PdfTitle.getText().toString();
                if(title.isEmpty()){
                    PdfTitle.setError("Empty");
                    PdfTitle.requestFocus();
                } else if (pdfData == null) {
                    Toast.makeText(UploadPdf.this, "Please upload pdf", Toast.LENGTH_SHORT).show();
                }else {
                    uploadPdf();
                }
            }
        });

    }




    private void openGallery() {
        Intent i = new Intent();
        i.setType("pdf/docs/ppt");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select pdf file"), REQ);
    }
    private void uploadData (String downloadUrl){
        String uniqueKey = reference.child("pdf").push().getKey();

        HashMap data = new HashMap();
        data.put("pdfTitle", title);
        data.put("pdfUrl", downloadUrl);

        reference.child("pdf").child(uniqueKey).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, "Pdf uploaded successfully", Toast.LENGTH_SHORT).show();
                PdfTitle.setText("");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, "Failed to upload pdf", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void uploadPdf () {
        pd.setTitle("Please wait...");
        pd.setMessage("Uploading pdf");
        pd.show();
        StorageReference reference = storageReference.child("pdf/" + pdfName + "-" + System.currentTimeMillis() + ".pdf");
        reference.putFile(pdfData)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isComplete()) ;
                        Uri uri = uriTask.getResult();
                        uploadData(String.valueOf(uri));
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(UploadPdf.this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ && resultCode == RESULT_OK){
            pdfData = data.getData();

            if(pdfData.toString().startsWith("content://")){
                Cursor cursor = null;
                try {
                    cursor = UploadPdf.this.getContentResolver().query(pdfData,null,null,null,null);
                    if(cursor != null && cursor.moveToFirst()){
                        pdfName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else if (pdfData.toString().startsWith("file://")) {
                pdfName = new File(pdfData.toString()).getName();
            }
            PdfTextView.setText(pdfName);
    }
}