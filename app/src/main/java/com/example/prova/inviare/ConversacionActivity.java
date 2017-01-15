package com.example.prova.inviare;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prova.inviare.adapters.AdaptadorChat;
import com.example.prova.inviare.adapters.AdaptadorConversaciones;
import com.example.prova.inviare.elementos.Contacto;
import com.example.prova.inviare.elementos.Mensaje;
import com.layer.atlas.AtlasMessagesRecyclerView;
import com.layer.atlas.messagetypes.location.LocationCellFactory;
import com.layer.atlas.messagetypes.text.TextCellFactory;
import com.layer.atlas.messagetypes.threepartimage.ThreePartImageCellFactory;
import com.layer.sdk.LayerClient;
import com.layer.sdk.messaging.Conversation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ConversacionActivity extends AppCompatActivity{
    private ArrayList<Mensaje> arrayMensajes;
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
        // use a linear layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));

        //TextWatcher
        TextWatcher tw = new TextWatcher() {
            public void afterTextChanged(Editable s){
                Log.d("aaa","aaa");
                if(editTextConversacion.getText().toString().isEmpty()){
                    floatingActionButton.setImageResource(R.drawable.ic_access_alarm_24dp);
                }else{
                    floatingActionButton.setImageResource(R.drawable.ic_send_button);
                }
            }
            public void  beforeTextChanged(CharSequence s, int start, int count, int after){
            }
            public void  onTextChanged (CharSequence s, int start, int before,int count) {
            }
        };
        editTextConversacion.addTextChangedListener(tw);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),AlarmasActivity.class));
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
