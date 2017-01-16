package com.example.prova.inviare;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.example.prova.inviare.adapters.AdaptadorChat;
import com.example.prova.inviare.elementos.Mensaje;

import java.util.ArrayList;

public class ConversacionActivity extends AppCompatActivity{
    private ArrayList<Mensaje> arrayMensajes;
    private boolean seleccionarAlarma=true;
    //private AtlasMessagesRecyclerView messagesList;
    //private Conversation conversation;
    //private Picasso picasso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversacion);

        final RecyclerView recyclerView= (RecyclerView) findViewById(R.id.recycler_view_conversacion);
        final EditText editTextConversacion= (EditText) findViewById(R.id.editTextConversacion);
        final FloatingActionButton floatingActionButton= (FloatingActionButton) findViewById(R.id.floatingActionButton);

        arrayMensajes= new ArrayList<>();
        arrayMensajes.add(new Mensaje("mensaje1","6:23 PM", true));
        arrayMensajes.add(new Mensaje("mensaje2","8:32 PM", false));

        //Adaptador - AdaptadorConversaciones
        final AdaptadorChat adaptador = new AdaptadorChat(arrayMensajes, getApplicationContext());
        recyclerView.setAdapter(adaptador);
        //Use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

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
                }

            }
        });

        /*// Create a LayerClient ready to receive push notifications through GCM. App IDs are specific to your application and should be kept private.
        LayerClient layerClient = LayerClient.newInstance(getApplicationContext(), "APP ID",
                new LayerClient.Options().googleCloudMessagingSenderId("GCM Project Number"));

        messagesList = ((AtlasMessagesRecyclerView) findViewById(R.id.messages_list))
                .init(layerClient, picasso)
                .setConversation(conversation)
                .addCellFactories(
                        new TextCellFactory(),
                        new ThreePartImageCellFactory(this, layerClient, picasso),
                        new LocationCellFactory(this, picasso));*/
    }
}
