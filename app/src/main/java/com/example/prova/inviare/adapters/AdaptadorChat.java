package com.example.prova.inviare.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.prova.inviare.R;
import com.example.prova.inviare.elementos.Alarma;
import com.example.prova.inviare.elementos.Mensaje;

import java.util.ArrayList;

/**
 * Created by user on 14/01/2017.
 */

public class AdaptadorChat extends RecyclerView.Adapter<AdaptadorChat.ListaViewHolder> {
    private ArrayList<Object> listData;
    private static Context context;

    private final static int MENSAJE=0;
    private final static int MENSAJE_FANTASMA=1;
    private final static int ALARMA_REPETITIVA=2;
    private final static int ALARMA_PERSISTENTE=3;
    private final static int ALARMA_UNICA=4;

    public AdaptadorChat(ArrayList<Object> listData, Context context) {
        this.listData = listData;
        this.context=context;
    }

    @Override
    public AdaptadorChat.ListaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_chat1, parent, false);
        AdaptadorChat.ListaViewHolder lvh = new AdaptadorChat.ListaViewHolder(itemView);
        return lvh;
    }

    @Override
    public void onBindViewHolder(AdaptadorChat.ListaViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case USER:
                ViewHolder1 vh1 = (ViewHolder1) viewHolder;
                configureViewHolder1(vh1, position);
                break;
            case IMAGE:
                ViewHolder2 vh2 = (ViewHolder2) viewHolder;
                configureViewHolder2(vh2, position);
                break;
            default:
                RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) viewHolder;
                configureDefaultViewHolder(vh, position);
                break;
        }

        Mensaje item = listData.get(position);
        //Método bindLista de la clase ListaViewHolder
        holder.bindLlista(item);
    }

    @Override
    public int getItemViewType(int position) {
        Object objeto=listData.get(position);
        if (objeto instanceof Mensaje) {
            return MENSAJE;
        } else if (objeto instanceof Alarma) {
            if(((Alarma) objeto).getTipo().compareTo("alarma1")==0){
                return ALARMA_REPETITIVA;
            }else if(((Alarma) objeto).getTipo().compareTo("alarma2")==0){
                return ALARMA_PERSISTENTE;
            }
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ListaViewHolder extends RecyclerView.ViewHolder{
        //Datos del view
        private TextView textViewMensaje;
        private TextView textViewHora;
        private CardView cardView;

        public ListaViewHolder(View itemView) {
            super(itemView);

            textViewMensaje = (TextView) itemView.findViewById(R.id.textViewMensaje);
            textViewHora = (TextView) itemView.findViewById(R.id.textViewHora);
            cardView= (CardView) itemView.findViewById(R.id.card_view_mensaje);
        }

        public void bindLlista(Mensaje elementLlista) {
            textViewMensaje.setText(elementLlista.getMensaje());
            textViewHora.setText(elementLlista.getHora());

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            if(elementLlista.isPropietario()){
                int margin = context.getResources().getDimensionPixelSize(R.dimen.chat_margin1);
                layoutParams.setMargins(dpToPixel(60), margin, margin, dpToPixel(5));
            }else{
                int margin = context.getResources().getDimensionPixelSize(R.dimen.chat_margin1);
                layoutParams.setMargins(margin, margin, dpToPixel(60), dpToPixel(5));
            }
            cardView.setLayoutParams(layoutParams);
        }

        private int dpToPixel(float dp){
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    }
}
