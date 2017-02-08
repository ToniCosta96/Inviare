package com.example.prova.inviare;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        ButterKnife.inject(this);
        ImageView back_image = (ImageView) findViewById(R.id.back_image);
        Picasso.with(getApplicationContext()).load(R.drawable.back_image).fit().centerCrop().into(back_image);
        aceptar=(Button)findViewById(R.id.aceptar);
        cancelar=(Button)findViewById(R.id.cancelar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

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
}
