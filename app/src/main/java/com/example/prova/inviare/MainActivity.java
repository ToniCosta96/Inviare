package com.example.prova.inviare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.prova.inviare.adapters.AdaptadorContactos;
import com.example.prova.inviare.elementos.Contacto;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<Contacto> arrayConversaciones;
    private static int USUARIO_ACCESO_DIRECTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se carga el SharedPreferences
        final int ID_PROPIETARIO=getResources().getInteger(R.integer.id_propietario);
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        USUARIO_ACCESO_DIRECTO = sharedPref.getInt(getResources().getString(R.string.preferences_acceso_directo),ID_PROPIETARIO);

        final RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_conversaciones);

        arrayConversaciones= new ArrayList<>();
        arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo","último uso"));
        arrayConversaciones.add(new Contacto(arrayConversaciones.size(),"Conversacion2","sub2","2"));

        // specify an adapter (see also next example)
        final AdaptadorContactos adaptador = new AdaptadorContactos(this,arrayConversaciones);
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
            case R.id.item_acceso_directo:
                //Intent -> Acceso directo
                i= new Intent(getApplicationContext(),ConversacionActivity.class);
                i.putExtra(getResources().getString(R.string.conversacion_id),USUARIO_ACCESO_DIRECTO);
                startActivity(i);
                return true;
            case R.id.item_perfil:
                //Intent -> Perfil
                i= new Intent(getApplicationContext(),PerfilActivity.class);
                startActivity(i);
                return true;
            case R.id.item_ajustes:
                //Intent -> Ajustes
                i= new Intent(getApplicationContext(),AjustesActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
