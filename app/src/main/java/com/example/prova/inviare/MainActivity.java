package com.example.prova.inviare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.prova.inviare.adapters.AdaptadorConversaciones;
import com.example.prova.inviare.db_adapters.DBAdapter;
import com.example.prova.inviare.elementos.Contacto;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int PERFIL_REQUEST=0;
    private static final int AJUSTES_REQUEST=1;
    private static final int CHAT_REQUEST=2;
    private ArrayList<Contacto> arrayConversaciones;
    private AdaptadorConversaciones adaptador;
    private static int USUARIO_ACCESO_DIRECTO;

    private String direccionImagenPropietario;

    FirebaseAuth.AuthStateListener mAuthListener;//creamos listener para comprovar si estamos autentificados

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se carga el SharedPreferences
        final int ID_PROPIETARIO=getResources().getInteger(R.integer.id_propietario);
        final SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        //Si NO has iniciado sesión se carga el activity de inicio de sesión.
        /*if(!sharedPref.getBoolean(getResources().getString(R.string.preferences_sesion_iniciada),false)){
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }*/

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {//cuando cambiamos la sesión
                FirebaseUser user = firebaseAuth.getCurrentUser();
// Comprobación de autenticación de Firebase, en caso de no estar autenticado llevara al registro
                if (user == null) {
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                }else {
                    Log.i("TestInvi", "Sesión iniciada");
                }
            }
        };





        USUARIO_ACCESO_DIRECTO = sharedPref.getInt(getResources().getString(R.string.preferences_usuario_acceso_directo),ID_PROPIETARIO);
        direccionImagenPropietario=sharedPref.getString(getResources().getString(R.string.preferences_imagen_perfil),null);
        final int posicionChatPersonal = sharedPref.getInt(getResources().getString(R.string.preferences_anclar_chat_personal),ID_PROPIETARIO);

        final RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_conversaciones);

        arrayConversaciones= new ArrayList<>();

        if(posicionChatPersonal==ID_PROPIETARIO){
            // Se añade primero el chat personal y luego el resto de chats
            DBAdapter dbAdapter= new DBAdapter(getApplicationContext());
            dbAdapter.open();
            final String ultimaFechaContacto = dbAdapter.seleccionarUltimaFechaContacto(getResources().getInteger(R.integer.id_propietario),DBAdapter.ID_CONTACTO,getResources().getString(R.string.simple_date_format_CONTACTO));
            arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo",ultimaFechaContacto,direccionImagenPropietario));
        }else{
            // Se añaden los chats y se intercala el chat personal en la posición correspondiente
            arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo","último uso",direccionImagenPropietario));
            arrayConversaciones.add(new Contacto(arrayConversaciones.size(),"Conversacion2","sub2","2",null));
        }

        // Especificar un adaptador para el RecyclerView
        adaptador = new AdaptadorConversaciones(this,arrayConversaciones);
        recyclerView.setAdapter(adaptador);
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // ID_PROPIETARIO = -1 <-> Id del propietario del dispositivo
        final int ID_PROPIETARIO=getResources().getInteger(R.integer.id_propietario);
        if (requestCode == PERFIL_REQUEST) {
            if(resultCode == AppCompatActivity.RESULT_OK){
                for(int i=0;i<arrayConversaciones.size();i++){
                    if(arrayConversaciones.get(i).getId()==ID_PROPIETARIO){
                        arrayConversaciones.get(i).setImagen(data.getStringExtra(getResources().getString(R.string.preferences_imagen_perfil)));
                        adaptador.notifyItemChanged(i);
                    }
                }
            }
        }else if(requestCode == AJUSTES_REQUEST){
            if(resultCode == AppCompatActivity.RESULT_OK){
                // Se vacía el arrayConversaciones
                arrayConversaciones.clear();

                if(data.getIntExtra(getApplicationContext().getString(R.string.preferences_anclar_chat_personal),ID_PROPIETARIO)==ID_PROPIETARIO){
                    //Se añade primero el chat personal y luego el resto de chats
                    arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo","último uso",direccionImagenPropietario));
                }else{
                    //Se añaden los chats y se intercala el chat personal en la posición correspondiente
                    arrayConversaciones.add(new Contacto(ID_PROPIETARIO,"Tú","chat contigo","último uso",direccionImagenPropietario));
                    arrayConversaciones.add(new Contacto(arrayConversaciones.size(),"Conversacion2","sub2","2",null));
                }
                //Se le notifican los cambios al adaptador (AdaptadorContactos)
                adaptador.notifyDataSetChanged();
            }
        }else if(requestCode == CHAT_REQUEST){
            if(resultCode == AppCompatActivity.RESULT_OK){
                final int posicionConversacion = data.getIntExtra(getResources().getString(R.string.intent_conversacion_posicion_array),0);
                final String ultimaFecha = data.getStringExtra(getResources().getString(R.string.intent_mensaje_fecha));
                arrayConversaciones.get(posicionConversacion).setInfoExtra(ultimaFecha);
                adaptador.notifyItemChanged(posicionConversacion);
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
                // Intent -> Añadir nuevo contacto
                i= new Intent(getApplicationContext(),ContactosActivity.class);
                startActivity(i);
                return true;
            case R.id.item_acceso_directo:
                // Intent -> Acceso directo
                i= new Intent(getApplicationContext(),ConversacionActivity.class);
                i.putExtra(getResources().getString(R.string.intent_conversacion_id),USUARIO_ACCESO_DIRECTO);
                // Se recorre el arrayConversaciones y se selecciona el nombre del contacto que su id coincida con USUARIO_ACCESO_DIRECTO
                String nombreContacto="Tú";
                for(int j=0;j<arrayConversaciones.size();j++){
                    if(arrayConversaciones.get(j).getId()==USUARIO_ACCESO_DIRECTO){
                        nombreContacto = arrayConversaciones.get(j).getTitulo();
                    }
                }
                i.putExtra(getResources().getString(R.string.intent_conversacion_titulo),nombreContacto);
                startActivity(i);
                return true;
            case R.id.item_perfil:
                // Intent -> Perfil
                i= new Intent(getApplicationContext(),PerfilActivity.class);
                startActivityForResult(i, PERFIL_REQUEST);
                return true;
            case R.id.item_ajustes:
                // Intent -> Ajustes
                i= new Intent(getApplicationContext(),AjustesActivity.class);
                startActivityForResult(i, AJUSTES_REQUEST);
                return true;
            case R.id.item_logOut:
                AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
            default:
                return super.onOptionsItemSelected(item);
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null){
            FirebaseAuth.getInstance().removeAuthStateListener(mAuthListener);
        }
    }
}
