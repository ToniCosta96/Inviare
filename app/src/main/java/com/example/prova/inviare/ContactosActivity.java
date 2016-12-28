package com.example.prova.inviare;

import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.prova.inviare.adapters.AdaptadorConversaciones;
import com.example.prova.inviare.elementos.Contacto;

import java.util.ArrayList;

public class ContactosActivity extends AppCompatActivity {
    private ArrayList<Contacto> arrayContactos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        final RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_contactos);

        arrayContactos= new ArrayList<>();
        arrayContactos.add(new Contacto("Contacto1","sub1","1"));
        arrayContactos.add(new Contacto("Contacto2","sub2","2"));

        // specify an adapter (see also next example)
        final AdaptadorConversaciones adaptador = new AdaptadorConversaciones(this,arrayContactos);
        recyclerView.setAdapter(adaptador);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }
}
