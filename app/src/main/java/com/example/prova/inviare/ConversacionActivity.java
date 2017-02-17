package com.example.prova.inviare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.prova.inviare.adapters.AdaptadorChat;
import com.example.prova.inviare.db_adapters.DBAdapter;
import com.example.prova.inviare.elementos.Alarma;
import com.example.prova.inviare.elementos.Mensaje;

import com.example.prova.inviare.elementos_firebase.Usuario;
import com.example.prova.inviare.servicios.ControladorAlarma;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ConversacionActivity extends AppCompatActivity{
    private static final int ALARMA_REQUEST=0;
    private RecyclerView recyclerView;
    private DBAdapter dbAdapter;
    private String id_conversacion;
    private ArrayList<Object> arrayMensajes;
    private AdaptadorChat adaptador;
    private boolean seleccionarAlarma=true;
    FirebaseDatabase database;
    private int oldBottom=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion);

        database = FirebaseDatabase.getInstance();
        final EditText editTextConversacion= (EditText) findViewById(R.id.editTextConversacion);
        final FloatingActionButton floatingActionButton= (FloatingActionButton) findViewById(R.id.floatingActionButton);
        recyclerView= (RecyclerView) findViewById(R.id.recycler_view_conversacion);
        arrayMensajes= new ArrayList<>();

        // Se obtiene el ID de la conversación y se carga de la base de datos ([-2] no carga nada).
        id_conversacion = getIntent().getExtras().getString(getResources().getString(R.string.intent_conversacion_id));
        android.support.v7.app.ActionBar actionBar=getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getExtras().getString(getResources().getString(R.string.intent_conversacion_titulo)));
        }
        dbAdapter = new DBAdapter(getApplicationContext());
        dbAdapter.open();
        dbAdapter.seleccionarMensaje(arrayMensajes,id_conversacion,DBAdapter.ID_CONTACTO,DBAdapter.TABLE_MENSAJES);
        // RecyclerView
        // Adaptador - AdaptadorConversaciones
        adaptador = new AdaptadorChat(arrayMensajes, getApplicationContext());
        recyclerView.setAdapter(adaptador);
        //Use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        // Recycler scroll
        recyclerView.scrollToPosition(arrayMensajes.size()-1);
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                if(recyclerView.getBottom() < oldBottom && !arrayMensajes.isEmpty()) {
                    recyclerView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.smoothScrollToPosition(arrayMensajes.size()-1);
                        }
                    }, 100);
                }
                oldBottom=recyclerView.getBottom();
            }
        });

        // Moverse a la alarma indicada si este activity procede de una Notificación
        int codigoAlarma = getIntent().getExtras().getInt(getString(R.string.intent_alarma_codigo));
        if(codigoAlarma!=0){
            for(int i=0;i<arrayMensajes.size();i++){
                if(arrayMensajes.get(i) instanceof Alarma){
                    if(((Alarma)arrayMensajes.get(i)).getId()==codigoAlarma){
                        recyclerView.scrollToPosition(i);
                    }
                }
            }
        }

        // TextWatcher
        TextWatcher tw = new TextWatcher() {
            public void afterTextChanged(Editable s){
                //Cambiar floating_button -> Alarma-Enviar
                if(editTextConversacion.getText().toString().isEmpty()){
                    seleccionarAlarma=true;
                    floatingActionButton.setImageResource(R.drawable.ic_access_alarm_24dp);
                }else{
                    seleccionarAlarma=false;
                    floatingActionButton.setImageResource(R.drawable.ic_send_button);
                }
            }
            public void  beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void  onTextChanged (CharSequence s, int start, int before,int count) {
            }
        };
        editTextConversacion.addTextChangedListener(tw);

        // FloatingButton
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seleccionarAlarma){
                    // SELECCIONAR ALARMA - Intent-> ID del contacto de este chat
                    Intent returnIntent = new Intent(getApplicationContext(),AlarmasActivity.class);
                    returnIntent.putExtras(getIntent().getExtras());
                    startActivityForResult(returnIntent, ALARMA_REQUEST);
                }else{
                    // ENVIAR MENSAJE
                    // Guardar mensaje en la base de datos local
                    SimpleDateFormat dfDataBase = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_DB), java.util.Locale.getDefault());
                    SimpleDateFormat dfMuestra = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_MENSAJE), java.util.Locale.getDefault());
                    Date horaActual = Calendar.getInstance().getTime();
                    String date = dfDataBase.format(horaActual);
                    String dateMuestra = dfMuestra.format(horaActual);
                    dbAdapter.insertarMensaje(editTextConversacion.getText().toString(),date,DBAdapter.TIPO_TEXTO,null,null,null,null,getResources().getString(R.string.id_propietario),id_conversacion);
                    // Mostrar por pantalla (A la derecha si es propietario [-1])
                    boolean propietario=false;
                    if (id_conversacion.compareTo(getResources().getString(R.string.id_propietario))==0) propietario=true;
                    arrayMensajes.add(new Mensaje(editTextConversacion.getText().toString(),dateMuestra, propietario));
                    // Notificar cambios a la interfaz
                    adaptador.notifyItemInserted(arrayMensajes.size()-1);
                    recyclerView.scrollToPosition(arrayMensajes.size()-1);
                    // Eliminar del editText
                    String mensaje=editTextConversacion.getText().toString();
                    editTextConversacion.setText("");
                    // Enviar a FireBase
                    //GuardarBBDD(mensaje);

                }
            }
        });
    }

    /*public void GuardarBBDD(final String mensaje) {
        String uid;

        final DatabaseReference MensajesRef = database.getReference(getResources().getString(R.string.TABLE_MENSAJES));
        //añadir mensajes a la bbdd

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            Mensajes mensajes = new Mensajes("", uid, mensaje);
            MensajesRef.push().setValue(mensajes);

        }*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode==ALARMA_REQUEST){
                // Añadir una alarma a la interfaz
                final int idAlarma = data.getIntExtra(getResources().getString(R.string.intent_alarma_codigo),0);
                final String mensaje = data.getStringExtra(getResources().getString(R.string.intent_alarma_mensaje));
                final String fecha = data.getStringExtra(getResources().getString(R.string.intent_alarma_fecha));
                final int tipo = data.getIntExtra(getResources().getString(R.string.intent_alarma_tipo),0);
                final String hora_inicial = data.getStringExtra(getResources().getString(R.string.intent_alarma_hora_inicio));
                final String hora_duracion = data.getStringExtra(getResources().getString(R.string.intent_alarma_hora_duracion));
                final String frecuencia = data.getStringExtra(getResources().getString(R.string.intent_alarma_frecuencia));

                arrayMensajes.add(new Alarma(idAlarma,mensaje,fecha,tipo,hora_inicial,hora_duracion,frecuencia,Alarma.TAREA_EN_CURSO,null,true));
                // Notificar cambios a la interfaz
                adaptador.notifyItemInserted(arrayMensajes.size()-1);
                recyclerView.scrollToPosition(arrayMensajes.size()-1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contacto, menu);
        //Si la conversación es del chat personal no se puede silenciar, bloquear ni eliminar
        if (getIntent().getExtras().getString(getResources().getString(R.string.intent_conversacion_id)).compareTo(getResources().getString(R.string.id_propietario))==0){
            menu.removeItem(R.id.item_silenciar);
            menu.removeItem(R.id.item_bloquear);
            menu.removeItem(R.id.item_eliminar_contacto);
        }else{
            int permisosContactoActual = dbAdapter.seleccionarPermisoContacto(id_conversacion,DBAdapter.ID);
            switch(permisosContactoActual){
                case DBAdapter.PERMISOS_TODOS:
                    //No se hace nada
                    break;
                case DBAdapter.PERMISOS_SILENCIADO:
                    //'Silenciar' pasa a llamarse 'Desactivar silencio'
                    menu.findItem(R.id.item_silenciar).setTitle(getResources().getString(R.string.menu_desactivar_silencio));
                    break;
                case DBAdapter.PERMISOS_BLOQUEADO:
                    //Se desactiva el item 'Silenciar' y 'Bloquear' pasa a llamarse 'Desactivar bloqueo'
                    menu.removeItem(R.id.item_silenciar);
                    menu.findItem(R.id.item_bloquear).setTitle(getResources().getString(R.string.menu_desactivar_bloqueo));
                    break;
                default:
                    //No se hace nada
                    break;
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case android.R.id.home:
                //Tornar al activity anterior.
                retornarActivityAnterior();
                return true;
            case R.id.item_eliminar_mensajes:
                //Se eliminan todos los mensajes de la base de datos (DBAdapter) y del arrayMensajes.
                arrayMensajes.clear();
                adaptador.notifyDataSetChanged();
                dbAdapter.eliminarMensajesPorContacto(id_conversacion);
                return true;
            case R.id.item_silenciar:
                //Se silencia el contacto actual si no estaba silenciado
                if(dbAdapter.seleccionarPermisoContacto(id_conversacion,DBAdapter.ID)==DBAdapter.PERMISOS_SILENCIADO){
                    //Se le quita el silencio al contacto

                }else{
                    //Se silencia el contacto

                }
                return true;
            case R.id.item_bloquear:
                //Se bloquea el contacto actual si no estaba bloqueado
                if(dbAdapter.seleccionarPermisoContacto(id_conversacion,DBAdapter.ID)==DBAdapter.PERMISOS_BLOQUEADO){
                    //Se le quita el bloqueo al contacto

                }else{
                    //Se bloquea el contacto

                }
                return true;
            case R.id.item_eliminar_contacto:
                //
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        retornarActivityAnterior();
        super.onBackPressed();
    }

    private void retornarActivityAnterior(){
        // Al volver al activity anterior se guardan los datos del ID del contacto y la fecha del último mensaje en un Intent
        DBAdapter dbAdapter= new DBAdapter(getApplicationContext());
        dbAdapter.open();
        final String ultimaFechaContacto = dbAdapter.seleccionarUltimaFechaContacto(id_conversacion,DBAdapter.ID_CONTACTO,getResources().getString(R.string.simple_date_format_CONTACTO));
        Intent returnIntent= getIntent();
        returnIntent.putExtra(getResources().getString(R.string.intent_mensaje_fecha),ultimaFechaContacto);
        setResult(RESULT_OK,returnIntent);
        finish();
    }
}
