package com.example.prova.inviare;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class LoginActivity extends AppCompatActivity {

    @InjectView(R.id.input_email)
    EditText _emailText;
    @InjectView(R.id.input_contrasena)
    EditText _contrasenaText;
    Button registro;
    Button iniciar;
    RelativeLayout activity_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ActionBar actionBar = getSupportActionBar();
        ButterKnife.inject(this);
        ImageView back_image = (ImageView) findViewById(R.id.back_image);
        Picasso.with(getApplicationContext()).load(R.drawable.back_image).fit().centerCrop().into(back_image);
        registro=(Button)findViewById(R.id.registrarse);
        iniciar=(Button)findViewById(R.id.iniciar);
        actionBar.hide();

        activity_log = (RelativeLayout) findViewById(R.id.activity_login);
        _emailText = (EditText) findViewById(R.id.input_email);
        _contrasenaText = (EditText) findViewById(R.id.input_contrasena);

        registro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), RegistroActivity.class);
                startActivity(intent);
            }
        });

        iniciar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = _emailText.getText().toString();
                String contrasena = _contrasenaText.getText().toString();

                if (email.isEmpty() || contrasena.isEmpty()){
                    Snackbar.make(activity_log, R.string.campos_vacios, Snackbar.LENGTH_SHORT).show();
                }else {
                    login(email, contrasena);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private void login(String email, String contrasena){
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, contrasena).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Log.i("VER", "usuario logueado correctamente");

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);

                    finish();
                }else {
                    Log.i("VER", task.getException().getMessage()+"");
                    Snackbar.make(activity_log, R.string.error_login, Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }
}
