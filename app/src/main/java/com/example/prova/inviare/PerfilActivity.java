package com.example.prova.inviare;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PerfilActivity extends AppCompatActivity {
    int PHOTO_PICK_REQUEST_CODE = 0;
    private ImageView imageViewPerfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        imageViewPerfil= (ImageView) findViewById(R.id.imageView_perfil);

        //Se añade el listener al imageView y se carga la imagen del sharedPreferences
        imageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Intent.ACTION_GET_CONTENT
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
            }
        });
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        String imagenPerfil = sharedPref.getString(getResources().getString(R.string.preferences_imagen_perfil),"");
        if(!imagenPerfil.isEmpty())
        try {
            Log.d("aaaa", "he entrado. Valor: "+imagenPerfil);
            imageViewPerfil.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.fromFile(new File(imagenPerfil))));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
            try{
                Bitmap bitmapImage=MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageViewPerfil.setImageBitmap(bitmapImage);
                //Si se carga el bitmap se guarda la URI en el SharedPreferences (shared_preferences)
                SharedPreferences sharedPref = getApplicationContext().getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(getString(R.string.preferences_imagen_perfil), saveImageToInternalStorage("perfil.png",bitmapImage));
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String saveImageToInternalStorage(String nombre, Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,nombre);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }
}
