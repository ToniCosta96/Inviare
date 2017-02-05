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
    private static final int AJUSTES_REQUEST=0;
    private ArrayList<Contacto> arrayConversaciones;
    private AdaptadorContactos adaptador;
    private static int USUARIO_ACCESO_DIRECTO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Se carga el SharedPreferences
        final int ID_PROPIETARIO=getResources().getInteger(R.integer.id_propietario);
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        USUARIO_ACCESO_DIRECTO = sharedPref.getInt(getResources().getString(R.string.preferences_usuario_acceso_directo),ID_PROPIETARIO);
        final int posicionChatPersonal = sharedPref.getInt(getResources().getString(R.string.preferences_anclar_chat_personal),ID_PROPIETARIO);

        final RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_conversaciones);

        arrayConversaciones= new ArrayList<>();
        if(posicionChatPersonal==-1){
            //Se añade primero el chat personal y luego el resto de chats
            arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo","último uso"));
        }else{
            //Se añaden los chats y se intercala el chat personal en la posición correspondiente
            arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo","último uso"));
            arrayConversaciones.add(new Contacto(arrayConversaciones.size(),"Conversacion2","sub2","2"));
        }

        // specify an adapter (see also next example)
        adaptador = new AdaptadorContactos(this,arrayConversaciones);
        recyclerView.setAdapter(adaptador);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AJUSTES_REQUEST) {
            if(resultCode == AppCompatActivity.RESULT_OK){
                arrayConversaciones.clear();
                final int ID_PROPIETARIO=getResources().getInteger(R.integer.id_propietario);
                if(data.getIntExtra(getApplicationContext().getString(R.string.preferences_anclar_chat_personal),ID_PROPIETARIO)==ID_PROPIETARIO){
                    //Se añade primero el chat personal y luego el resto de chats
                    arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo","último uso"));
                }else{
                    //Se añaden los chats y se intercala el chat personal en la posición correspondiente
                    arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo","último uso"));
                    arrayConversaciones.add(new Contacto(arrayConversaciones.size(),"Conversacion2","sub2","2"));
                }
                //Se le notifican los cambios al adaptador (AdaptadorContactos)
                adaptador.notifyDataSetChanged();
            }
        }
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
                i.putExtra(getResources().getString(R.string.intent_conversacion_id),USUARIO_ACCESO_DIRECTO);
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
                startActivityForResult(i, AJUSTES_REQUEST);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
