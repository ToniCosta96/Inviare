package com.example.prova.inviare.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prova.inviare.ConversacionActivity;
import com.example.prova.inviare.MainActivity;
import com.example.prova.inviare.R;
import com.example.prova.inviare.elementos.Contacto;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class AdaptadorConversaciones extends RecyclerView.Adapter<AdaptadorConversaciones.ListaViewHolder>{
    private static final int CHAT_REQUEST=2;
    private MainActivity activity;
    private ArrayList<Contacto> listData;

    public AdaptadorConversaciones(MainActivity mainActivity, ArrayList<Contacto> listData) {
        this.activity=mainActivity;
        this.listData = listData;
    }

    @Override
    public ListaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_contacto, parent, false);
        return new ListaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ListaViewHolder holder, final int position) {
        final Contacto item = listData.get(position);
        //
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(activity, ConversacionActivity.class);
                Bundle b = new Bundle();
                b.putInt(activity.getResources().getString(R.string.intent_conversacion_posicion_array),holder.getAdapterPosition());
                b.putString(activity.getResources().getString(R.string.intent_conversacion_id),item.getId());
                b.putString(activity.getResources().getString(R.string.intent_conversacion_titulo),item.getTitulo());
                i.putExtras(b);
                activity.startActivityForResult(i,CHAT_REQUEST);
            }
        });
        //Método bindLista de la clase ListaViewHolder
        holder.bindLlista(item, activity);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ListaViewHolder extends RecyclerView.ViewHolder{
        //Datos del view
        private TextView textViewTitulo;
        private TextView textViewSubtitulo;
        private TextView textViewInfoExtra;
        private ImageView imageViewContacto;

        public ListaViewHolder(View itemView) {
            super(itemView);

            textViewTitulo = (TextView) itemView.findViewById(R.id.textViewTitulo);
            textViewSubtitulo = (TextView) itemView.findViewById(R.id.textViewSubtitulo);
            textViewInfoExtra = (TextView) itemView.findViewById(R.id.textViewInfoExtra);
            imageViewContacto = (ImageView) itemView.findViewById(R.id.imageViewContacto);
        }

        public void bindLlista(Contacto elementLlista, Activity activity) {
            textViewTitulo.setText(elementLlista.getTitulo());
            textViewSubtitulo.setText(elementLlista.getSubtitulo());
            textViewInfoExtra.setText(elementLlista.getInfoExtra());
            String imagen;
            if((imagen=elementLlista.getImagen())==null){
                // Imagen predeterminada
                imageViewContacto.setImageDrawable(android.support.v4.content.ContextCompat.getDrawable(activity, R.drawable.unknown));
            }else{
                Picasso.with(activity).load(new File(imagen)).into(imageViewContacto);
            }
        }
    }
}
