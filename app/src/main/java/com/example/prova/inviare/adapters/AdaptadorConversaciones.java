package com.example.prova.inviare.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prova.inviare.ConversacionActivity;
import com.example.prova.inviare.R;
import com.example.prova.inviare.elementos.Contacto;

import java.util.ArrayList;

/**
 * Created by user on 24/12/2016.
 */

public class AdaptadorConversaciones extends RecyclerView.Adapter<AdaptadorConversaciones.ListaViewHolder>{
    private Activity activity;
    private ArrayList<Contacto> listData;

    public AdaptadorConversaciones(Activity a, ArrayList<Contacto> listData) {
        this.activity=a;
        this.listData = listData;
    }

    @Override
    public ListaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_contacto, parent, false);
        ListaViewHolder lvh = new ListaViewHolder(itemView);
        return lvh;
    }

    @Override
    public void onBindViewHolder(ListaViewHolder holder, final int position) {
        Contacto item = listData.get(position);
        //
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(activity.getApplicationContext(), ConversacionActivity.class);
                activity.startActivity(i);
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
