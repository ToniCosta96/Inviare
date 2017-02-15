package com.example.prova.inviare;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prova.inviare.asynctasks.GuardarImagen;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;

public class PerfilActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 0;
    private static final int PHOTO_PICK_REQUEST_CODE = 0;
    private static final int CAMERA_REQUEST_CODE = 1;
    private Button btnGuardar;
    private ImageView imageViewPerfil;
    private ImageView imageViewEliminarFoto;
    private String direccionImagenPerfil=null;
    private int intentImagenCambiada=AppCompatActivity.RESULT_CANCELED;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        // Toolbar
        final Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar_perfil);
        setSupportActionBar(toolbar);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null) {
            actionBar.setTitle(getResources().getString(R.string.perfil));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btnGuardar.isClickable()) finish();
            }
        });
        btnGuardar= (Button) findViewById(R.id.btn_guardar_perfil);
        imageViewEliminarFoto= (ImageView) findViewById(R.id.imageView_eliminar);
        final Button btnImagenGaleria= (Button) findViewById(R.id.btn_imagen_galeria);
        final Button btnImagenFoto= (Button) findViewById(R.id.btn_imagen_camara);
        final EditText editTextNombre= (EditText) findViewById(R.id.editText_perfil_nombre);
        final EditText editTextEstado= (EditText) findViewById(R.id.editText_perfil_estado);
        final EditText editTextTelefono= (EditText) findViewById(R.id.editText_perfil_tlfn);
        final TextView textViewCorreo= (TextView) findViewById(R.id.textView_correo);
        imageViewPerfil= (ImageView) findViewById(R.id.imageView_perfil);

        // Se carga el SharedPreferences
        final SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        direccionImagenPerfil = sharedPref.getString(getResources().getString(R.string.preferences_imagen_perfil),null);
        final String nombrePerfil = sharedPref.getString(getResources().getString(R.string.preferences_nombre_perfil),"");
        final String estadoPerfil = sharedPref.getString(getResources().getString(R.string.preferences_estado_perfil),"");
        final String telefonoPerfil = sharedPref.getString(getResources().getString(R.string.preferences_telefono_perfil),"");
        final String emailPerfil = sharedPref.getString(getResources().getString(R.string.preferences_email_perfil),"");
        editTextNombre.setText(nombrePerfil);
        editTextEstado.setText(estadoPerfil);
        editTextTelefono.setText(telefonoPerfil);
        textViewCorreo.setText(emailPerfil);

        // Se carga en el imageViewPerfil la imagen del sharedPreferences
        if(direccionImagenPerfil==null) {
            imageViewEliminarFoto.setVisibility(View.GONE);
        }else{
            //imageViewPerfil.setImageBitmap(BitmapFactory.decodeFile(imagenPerfil,options));
            Picasso.with(getApplicationContext()).load(new File(direccionImagenPerfil)).into(imageViewPerfil);
        }

        // Se añaden los listeners
        imageViewEliminarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                direccionImagenPerfil=sharedPref.getString(getResources().getString(R.string.preferences_imagen_perfil),null);
                if(direccionImagenPerfil!=null){
                    if(new File(direccionImagenPerfil).delete()){
                        Toast.makeText(getApplicationContext(),"Imagen eliminada correctamente",Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE).edit();
                        editor.remove(getResources().getString(R.string.preferences_imagen_perfil));
                        editor.apply();
                        imageViewEliminarFoto.setVisibility(View.GONE);
                        // Establece una imagen predeterminada como Drawable de 'imageViewPerfil'
                        imageViewPerfil.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.unknown));
                        // Devuelve la dirección de la imagen cambiada al activity anterior
                        direccionImagenPerfil=null;
                        intentImagenCambiada=AppCompatActivity.RESULT_OK;
                    }else{
                        Toast.makeText(getApplicationContext(),"Ha habido algún problema en eliminar la imagen",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        // GALERÍA
        btnImagenGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI); //Intent.ACTION_GET_CONTENT
                intent.setType("image/*");
                startActivityForResult(intent, PHOTO_PICK_REQUEST_CODE);
                intentImagenCambiada=AppCompatActivity.RESULT_OK;
            }
        });
        // CÁMARA
        btnImagenFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Pide permiso de lectura de almacenamiento externo si no lo tiene
                askForReadExternalStorage();
                intentImagenCambiada=AppCompatActivity.RESULT_OK;
            }
        });
        // Botón GUARDAR
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Si hay cambios se guardan
                if(editTextNombre.getText().toString().compareTo(nombrePerfil)!=0 || editTextEstado.getText().toString().compareTo(estadoPerfil)!=0 ||
                editTextTelefono.getText().toString().compareTo(telefonoPerfil)!=0) {

                    Context context=getApplicationContext();
                    SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString(context.getString(R.string.preferences_nombre_perfil),editTextNombre.getText().toString());
                    editor.putString(context.getString(R.string.preferences_estado_perfil),editTextEstado.getText().toString());
                    editor.putString(context.getString(R.string.preferences_telefono_perfil),editTextTelefono.getText().toString());
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Cambios guardados correctamente.",Toast.LENGTH_SHORT).show();
                }
                // Se guarda la imagen
                addIntentExtras();
                // Cerrar activity
                finish();
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Intent returnIntent= getIntent();
        outState.putString(getResources().getString(R.string.preferences_imagen_perfil), direccionImagenPerfil);
        outState.putInt("imagenCambiada", intentImagenCambiada);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        direccionImagenPerfil = savedInstanceState.getString(getResources().getString(R.string.preferences_imagen_perfil),null);
        intentImagenCambiada = savedInstanceState.getInt("imagenCambiada");
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void addIntentExtras(){
        if(intentImagenCambiada==AppCompatActivity.RESULT_OK){
            Intent returnIntent = getIntent();
            returnIntent.putExtra(getResources().getString(R.string.preferences_imagen_perfil),direccionImagenPerfil);
            setResult(intentImagenCambiada,returnIntent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == AppCompatActivity.RESULT_OK){
            if (requestCode == PHOTO_PICK_REQUEST_CODE) {
                if (data == null) {
                    // Display an error
                    return;
                }
                // Se carga la imagen y se muestra la imagen de eliminar foto
                Picasso.with(getApplicationContext()).load(data.getData()).into(imageViewPerfil);
            }else if(requestCode == CAMERA_REQUEST_CODE){
                // Se carga la imagen y se muestra la imagen de eliminar foto
                Picasso.with(getApplicationContext()).load(data.getData()).into(imageViewPerfil);
            }
            // Se guarda la imagen en el almacenamiento interno y la URI en el SharedPreferences (shared_preferences)
            new GuardarImagen(this, imageViewEliminarFoto, btnGuardar).execute(data.getData());
        }
    }

    public void askForReadExternalStorage(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle(getResources().getString(R.string.permisos_pedir_lectura));
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage(getResources().getString(R.string.permisos_confirma_lectura));
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_EXTERNAL_STORAGE}
                                    , PERMISSION_REQUEST_EXTERNAL_STORAGE);
                        }
                    });
                    builder.show();
                    // Show an expanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSION_REQUEST_EXTERNAL_STORAGE);
                }
            }else{
                cargarCamara();
            }
        }
        else{
            cargarCamara();
        }
    }

    private void cargarCamara(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cargarCamara();
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    Toast.makeText(this, getResources().getString(R.string.permisos_sin_permiso), Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onBackPressed()
    {
        if (btnGuardar.isClickable()) {
            addIntentExtras();
            finish();
        }
    }

    public void setDireccionImagenPerfil(String direccionImagenPerfil) {
        this.direccionImagenPerfil = direccionImagenPerfil;
    }

}
