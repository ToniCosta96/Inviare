package com.example.prova.inviare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.prova.inviare.adapters.AdaptadorConversaciones;
import com.example.prova.inviare.elementos.Contacto;
import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerAuthenticationListener;
import com.layer.sdk.listeners.LayerConnectionListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Contacto> arrayConversaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_conversaciones);

        arrayConversaciones= new ArrayList<>();
        arrayConversaciones.add(new Contacto("Conversacion1","sub1","1"));
        arrayConversaciones.add(new Contacto("Conversacion2","sub2","2"));

        // specify an adapter (see also next example)
        final AdaptadorConversaciones adaptador = new AdaptadorConversaciones(this,arrayConversaciones);
        recyclerView.setAdapter(adaptador);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.item_add:
                //Intent -> Añadir nuevo contacto
                i= new Intent(getApplicationContext(),ContactosActivity.class);
                startActivity(i);
                return true;
            case R.id.item_ajustes:
                //Intent -> Ajustes
                //i= new Intent(getApplicationContext(),AjustesActivity.class); //esto aun no existe
                //startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
