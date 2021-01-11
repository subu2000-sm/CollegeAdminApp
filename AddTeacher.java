package lakshya.org.collegeadmin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import java.io.IOException;

public class AddTeacher extends AppCompatActivity {
private ImageView addTeacher;
private EditText addTeacherName,addTeacherPost,addTeacherEmail;
private Spinner addTeacherCategory;
private int REQ=1;
private Bitmap bitmap;
private String name,email,post,category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_teacher);
        addTeacher=findViewById(R.id.addTeacherImage);
        addTeacherName=findViewById(R.id.addTeacherName);
        addTeacherPost=findViewById(R.id.addTeacherPost);
        addTeacherEmail=findViewById(R.id.addTeacherEmail);
        addTeacherCategory=findViewById(R.id.addTeacherCategory);
        String[] item={"Select Category","Computer Science","Mechanical","EEE","ECE"};
        addTeacherCategory.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,item));
        addTeacherCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                category=addTeacherCategory.getSelectedItem().toString();
            }
        });
        addTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }

            private void openGallery() {
                Intent i = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i,REQ);
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
            addTeacher.setImageBitmap(bitmap);
        }
    }
}