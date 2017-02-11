package com.example.prova.inviare.asynctasks;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.example.prova.inviare.PerfilActivity;
import com.example.prova.inviare.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GuardarImagen extends AsyncTask<Uri, Integer, Boolean> {
    private PerfilActivity perfilActivity;
    private Context context;
    private View viewEliminarFoto;
    private View viewBotonGuardar;
    private boolean imagenGuardada;

    public GuardarImagen(PerfilActivity perfilActivity, View viewEliminarFoto, View viewBotonGuardar){
        this.perfilActivity=perfilActivity;
        this.context=perfilActivity.getApplicationContext();
        this.viewEliminarFoto=viewEliminarFoto;
        this.viewBotonGuardar=viewBotonGuardar;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        viewBotonGuardar.setClickable(false);
        viewBotonGuardar.setAlpha(0.5f);
    }

    @Override
    protected Boolean doInBackground(Uri... uris) {
        try{
            // Si se carga el bitmap se guarda la URI en el SharedPreferences (shared_preferences)
            SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            // Se guarda la imagen y se recoge su dirección en tipo String
            final String direccionImagen=saveImageToInternalStorage("perfil.jpg",MediaStore.Images.Media.getBitmap(context.getContentResolver(), uris[0]));
            editor.putString(context.getString(R.string.preferences_imagen_perfil), direccionImagen);
            // Se elimina la posible foto anterior de la caché de Picasso
            Picasso.with(context).invalidate(new File(direccionImagen));
            imagenGuardada = editor.commit();
            //Se añade la direccion de la imagen al activity anterior
            perfilActivity.anyadirImagenActivityResult(direccionImagen, android.support.v7.app.AppCompatActivity.RESULT_OK);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imagenGuardada;
    }

    @Override
    protected void onPostExecute(Boolean imagenGuardada) {
        super.onPostExecute(imagenGuardada);
        viewEliminarFoto.setVisibility(View.VISIBLE);
        viewBotonGuardar.setAlpha(1);
        viewBotonGuardar.setClickable(true);
        if(imagenGuardada) Toast.makeText(context, "Imagen guardada", Toast.LENGTH_SHORT).show();
    }

    private String saveImageToInternalStorage(String nombre, Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,nombre);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 80, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fos!=null) fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mypath.getAbsolutePath();
    }
}
