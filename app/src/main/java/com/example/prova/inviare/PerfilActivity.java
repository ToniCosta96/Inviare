package com.example.prova.inviare;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prova.inviare.asynctasks.GuardarImagen;
import com.squareup.picasso.Picasso;

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

        //Toolbar
        final Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Button btnGuardar= (Button) findViewById(R.id.btn_guardar_perfil);
        final EditText editTextNombre= (EditText) findViewById(R.id.editText_perfil_nombre);
        final EditText editTextEstado= (EditText) findViewById(R.id.editText_perfil_estado);
        final EditText editTextTelefono= (EditText) findViewById(R.id.editText_perfil_tlfn);
        final TextView textViewCorreo= (TextView) findViewById(R.id.textView_correo);
        imageViewPerfil= (ImageView) findViewById(R.id.imageView_perfil);

        //Se carga el SharedPreferences
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        String imagenPerfil = sharedPref.getString(getResources().getString(R.string.preferences_imagen_perfil),"");
        final String nombrePerfil = sharedPref.getString(getResources().getString(R.string.preferences_nombre_perfil),"");
        final String estadoPerfil = sharedPref.getString(getResources().getString(R.string.preferences_estado_perfil),"");
        final String telefonoPerfil = sharedPref.getString(getResources().getString(R.string.preferences_telefono_perfil),"");
        final String emailPerfil = sharedPref.getString(getResources().getString(R.string.preferences_email_perfil),"");
        editTextNombre.setText(nombrePerfil);
        editTextEstado.setText(estadoPerfil);
        editTextTelefono.setText(telefonoPerfil);
        textViewCorreo.setText(emailPerfil);

        //Se añade el listener al imageView y se carga la imagen del sharedPreferences
        imageViewPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Intent.ACTION_GET_CONTENT
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
            }
        });
        if(!imagenPerfil.isEmpty()) {
            //imageViewPerfil.setImageBitmap(BitmapFactory.decodeFile(imagenPerfil,options));
            File fileImagen= new File(imagenPerfil);
            Picasso.with(this).invalidate(fileImagen);
            Picasso.with(getApplicationContext()).load(fileImagen).into(imageViewPerfil);
        }

        //Boton GUARDAR
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Si hay cambios se guardan
                if(editTextNombre.getText().toString().compareTo(nombrePerfil)!=0 || editTextEstado.getText().toString().compareTo(estadoPerfil)!=0 ||
                editTextTelefono.getText().toString().compareTo(telefonoPerfil)!=0){
                    Context context=getApplicationContext();
                    SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(context.getString(R.string.preferences_nombre_perfil),editTextNombre.getText().toString());
                    editor.putString(context.getString(R.string.preferences_estado_perfil),editTextEstado.getText().toString());
                    editor.putString(context.getString(R.string.preferences_telefono_perfil),editTextTelefono.getText().toString());
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Cambios guardados correctamente.",Toast.LENGTH_SHORT).show();
                }
                //Cerrar activity
                finish();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_PICK_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }
                Picasso.with(getApplicationContext()).load(data.getData()).into(imageViewPerfil);
                //Si se carga el bitmap se guarda la URI en el SharedPreferences (shared_preferences)
                new GuardarImagen(getApplicationContext()).execute(data.getData());

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
