package com.example.prova.inviare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.example.prova.inviare.adapters.AdaptadorChat;
import com.example.prova.inviare.db_adapters.DBAdapter;
import com.example.prova.inviare.elementos.Alarma;
import com.example.prova.inviare.elementos.Mensaje;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ConversacionActivity extends AppCompatActivity{
    private DBAdapter dbAdapter;
    private int id_conversacion;
    private ArrayList<Object> arrayMensajes;
    private AdaptadorChat adaptador;
    private boolean seleccionarAlarma=true;

    private int oldBottom=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion);

        final RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_conversacion);
        final EditText editTextConversacion= (EditText) findViewById(R.id.editTextConversacion);
        final FloatingActionButton floatingActionButton= (FloatingActionButton) findViewById(R.id.floatingActionButton);
        arrayMensajes= new ArrayList<>();

        //Se obtiene el ID de la conversación y se carga de la base de datos ([-2] no carga nada).
        id_conversacion = getIntent().getIntExtra(getResources().getString(R.string.intent_conversacion_id),-2);
        dbAdapter = new DBAdapter(getApplicationContext());
        dbAdapter.open();
        arrayMensajes.add(new Alarma("Mensaje","fecha","alarma1","hora_i","hora_d","dia","frecuencia",true));
        arrayMensajes.add(new Alarma("Mensaje","fecha","alarma1","hora_i","hora_d","dia","frecuencia",false));
        if(id_conversacion==-1) dbAdapter.seleccionarMensaje(arrayMensajes,id_conversacion,DBAdapter.ID_CONTACTO,DBAdapter.TABLE_MENSAJES);
        //RecyclerView
        //Adaptador - AdaptadorConversaciones
        adaptador = new AdaptadorChat(arrayMensajes, getApplicationContext());
        recyclerView.setAdapter(adaptador);
        //Use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        //Recycler scroll
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

        //TextWatcher
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

        //FloatingButton
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seleccionarAlarma){
                    //Seleccionar alarma
                    startActivity(new Intent(getApplicationContext(),AlarmasActivity.class));
                }else{
                    //Enviar mensaje
                    //Guardar en la base de datos local
                    SimpleDateFormat dfDataBase = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_DB), java.util.Locale.getDefault());
                    SimpleDateFormat dfMuestra = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_INTERFAZ), java.util.Locale.getDefault());
                    Date horaActual = Calendar.getInstance().getTime();
                    String date = dfDataBase.format(horaActual);
                    String dateMuestra = dfMuestra.format(horaActual);
                    dbAdapter.insertarMensaje(editTextConversacion.getText().toString(),date,DBAdapter.TIPO_TEXTO,null,null,null,null,id_conversacion);
                    //Mostrar por pantalla (A la derecha si es propietario [-1])
                    boolean propietario=false;
                    if(id_conversacion==getResources().getInteger(R.integer.id_propietario)) propietario=true;
                    arrayMensajes.add(new Mensaje(editTextConversacion.getText().toString(),dateMuestra, propietario));
                    //Notificar cambios a la interfaz
                    adaptador.notifyItemInserted(arrayMensajes.size()-1);
                    recyclerView.scrollToPosition(arrayMensajes.size()-1);
                    //Eliminar del editText
                    editTextConversacion.setText("");
                    //Enviar a FireBbase

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_contacto, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i;
        switch (item.getItemId()) {
            case R.id.item_eliminar_mensajes:
                //Se eliminan todos los mensajes de la base de datos (DBAdapter) y del arrayMensajes.
                arrayMensajes.clear();
                adaptador.notifyDataSetChanged();
                dbAdapter.eliminarMensajesPorContacto(id_conversacion);
                return true;
            case R.id.item_silenciar:
                //
                return true;
            case R.id.item_bloquear:
                //
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
