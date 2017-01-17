package com.example.prova.inviare;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import java.io.IOException;

import siclo.com.ezphotopicker.api.EZPhotoPick;
import siclo.com.ezphotopicker.api.EZPhotoPickStorage;
import siclo.com.ezphotopicker.api.models.EZPhotoPickConfig;
import siclo.com.ezphotopicker.api.models.PhotoSource;

public class PerfilActivity extends AppCompatActivity {
    private ImageView imageViewPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imageViewPerfil= (ImageView) findViewById(R.id.imageView_perfil);

        imageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EZPhotoPickConfig config = new EZPhotoPickConfig();
                config.photoSource = PhotoSource.GALERY; // or PhotoSource.CAMERA
                config.exportingSize = 1000;
                EZPhotoPick.startPhotoPickActivity(PerfilActivity.this, config);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK){
            return;
        }

        if(requestCode == EZPhotoPick.PHOTO_PICK_REQUEST_CODE){
            try {
                Bitmap pickedPhoto = new EZPhotoPickStorage(this).loadLatestStoredPhotoBitmap();
                imageViewPerfil.setImageBitmap(pickedPhoto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
