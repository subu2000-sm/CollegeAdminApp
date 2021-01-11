package lakshya.org.collegeadmin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
MaterialCardView uploadNotice;
MaterialCardView addGalleryImage;
MaterialCardView uploadPdf,addFaculty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, "android.permission.ACCESS.INTERNET") != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{ "android.permission.INTERNET"}, 1);
            }
        }
        uploadNotice=findViewById(R.id.addNotice);
        uploadNotice.setOnClickListener( this);
        addGalleryImage=findViewById(R.id.addGalleryImage);
        addGalleryImage.setOnClickListener(this);
        uploadPdf=findViewById(R.id.addEbook);
        uploadPdf.setOnClickListener(this);
        addFaculty=findViewById(R.id.addFaculty);
        addFaculty.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.addNotice:
                i=new Intent(MainActivity.this,UploadActivity.class);
            startActivity(i);
            break;
            case R.id.addGalleryImage:
                 i=new Intent(MainActivity.this,UploadImage.class);
                startActivity(i);
                break;
            case  R.id.addEbook:
                i=new Intent(MainActivity.this,UploadPdf.class);
                startActivity(i);
                break;
            case R.id.addFaculty:
                i=new Intent(MainActivity.this,UpdateFaculty.class);
                startActivity(i);
                break;




        }
    }
}