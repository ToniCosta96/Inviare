package com.example.prova.inviare.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.prova.inviare.R;
import com.example.prova.inviare.elementos.Contacto;

import java.util.ArrayList;

/**
 * Created by user on 24/12/2016.
 */

public class AdaptadorConversaciones extends RecyclerView.Adapter<AdaptadorConversaciones.ListaViewHolder>{
    private ArrayList<Contacto> listData;

    public AdaptadorConversaciones(ArrayList<Contacto> listData) {
        this.listData = listData;
    }

    @Override
    public ListaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_contacto, parent, false);
        ListaViewHolder lvh = new ListaViewHolder(itemView);
        return lvh;
    }

    @Override
    public void onBindViewHolder(ListaViewHolder holder, int position) {
        Contacto item = listData.get(position);
        //Método bindLlista de la clase ListaViewHolder
        holder.bindLlista(item);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ListaViewHolder extends RecyclerView.ViewHolder {
        //Datos del view
        private TextView textViewTitulo;

        public ListaViewHolder(View itemView) {
            super(itemView);

            textViewTitulo = (TextView) itemView.findViewById(R.id.textViewTitulo);
        }

        public void bindLlista(Contacto elementLlista) {
            textViewTitulo.setText(elementLlista.getTitulo());
        }
    }
}
