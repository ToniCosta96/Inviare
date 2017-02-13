package com.example.prova.inviare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.prova.inviare.elementos_firebase.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

import static com.example.prova.inviare.R.layout.activity_registro;

public class RegistroActivity extends AppCompatActivity {

    @InjectView(R.id.input_nombre)
    EditText _nombreText;
    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_contra)
    EditText _contraText;
    @InjectView(R.id.input_telefono)
    EditText _telefonoText;
    Button aceptar;
    Button cancelar;

    RelativeLayout activity_reg;
    FirebaseDatabase database;
    public static final String TABLE_CONTACTOS = "contactos";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_registro);

        ButterKnife.inject(this);
        ImageView back_image = (ImageView) findViewById(R.id.back_image);
        Picasso.with(getApplicationContext()).load(R.drawable.back_image).fit().centerCrop().into(back_image);
        aceptar=(Button)findViewById(R.id.aceptar);
        cancelar=(Button)findViewById(R.id.cancelar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)actionBar.hide();


        activity_reg = (RelativeLayout) findViewById(R.id.activity_registro);
        database = FirebaseDatabase.getInstance();
        _nombreText = (EditText) findViewById(R.id.input_nombre);
        _emailText = (EditText) findViewById(R.id.input_email);
        _contraText = (EditText) findViewById(R.id.input_contra);
        _telefonoText = (EditText) findViewById(R.id.input_telefono);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = _nombreText.getText().toString();
                String email = _emailText.getText().toString();
                String pass = _contraText.getText().toString();
                String tel = _telefonoText.getText().toString();

                if (name.isEmpty() || email.isEmpty() || pass.isEmpty() || tel.isEmpty()){
                    Snackbar.make(activity_reg, R.string.campos_vacios, Snackbar.LENGTH_SHORT).show();
                }else {
                    registro(name, email, pass, Integer.parseInt(tel));
                }
            }
        });


        cancelar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void registro(String nombre, String email, String contrasena, int telefono){
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.i("TestInvi", "Añadido a autentificación de BBDD");
                }else {
                    Log.i("TestInvi", task.getException().getMessage()+"");
                }
            }
        });

        final DatabaseReference tutorialRef = database.getReference(TABLE_CONTACTOS);

        //añadir contactos a la bbdd

        Usuario usuario = new Usuario(nombre, email, contrasena, telefono);
        tutorialRef.push().setValue(usuario);
    }
}