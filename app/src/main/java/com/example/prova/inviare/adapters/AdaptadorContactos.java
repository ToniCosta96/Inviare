package com.example.prova.inviare.adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prova.inviare.ConversacionActivity;
import com.example.prova.inviare.R;
import com.example.prova.inviare.elementos.Contacto;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdaptadorContactos extends RecyclerView.Adapter<AdaptadorContactos.ListaViewHolder>{
    private Activity activity;
    private ArrayList<Contacto> listData;

    public AdaptadorContactos(Activity a, ArrayList<Contacto> listData) {
        this.activity=a;
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
                Intent i= new Intent(activity.getApplicationContext(), ConversacionActivity.class);
                Bundle b = new Bundle();
                b.putInt(activity.getResources().getString(R.string.intent_conversacion_posicion_array),holder.getAdapterPosition());
                b.putString(activity.getResources().getString(R.string.intent_conversacion_id),item.getId());
                b.putString(activity.getResources().getString(R.string.intent_conversacion_titulo),item.getTitulo());
                i.putExtras(b);
                activity.startActivity(i);
                activity.finish();
            }
        });
        //Método bindLista de la clase ListaViewHolder
        holder.bindLlista(item);
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

        public ListaViewHolder(View itemView) {
            super(itemView);

            textViewTitulo = (TextView) itemView.findViewById(R.id.textViewTitulo);
            textViewSubtitulo = (TextView) itemView.findViewById(R.id.textViewSubtitulo);
            textViewInfoExtra = (TextView) itemView.findViewById(R.id.textViewInfoExtra);
        }

        public void bindLlista(Contacto elementLlista) {
            textViewTitulo.setText(elementLlista.getTitulo());
            textViewSubtitulo.setText(elementLlista.getSubtitulo());
            textViewInfoExtra.setText(elementLlista.getInfoExtra());
        }
    }
}
