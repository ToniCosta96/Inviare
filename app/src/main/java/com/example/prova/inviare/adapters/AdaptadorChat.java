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
import com.example.prova.inviare.db_adapters.DBAdapter;
import com.example.prova.inviare.elementos.Alarma;
import com.example.prova.inviare.elementos.Mensaje;

import java.util.ArrayList;

public class AdaptadorChat extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<Object> listData;
    private Context context;

    public AdaptadorChat(ArrayList<Object> listData, Context context) {
        this.listData = listData;
        this.context=context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case DBAdapter.TIPO_TEXTO:
                View v1 = inflater.inflate(R.layout.lista_chat1, parent, false);
                viewHolder = new AdaptadorChat.ViewHolderMensaje(v1);
                break;
            case DBAdapter.TIPO_ALARMA_REPETITIVA:
                View v2 = inflater.inflate(R.layout.lista_chat_alarma_persistente, parent, false);
                viewHolder = new AdaptadorChat.ViewHolderAlarmaPersistente(v2);
                break;
            case DBAdapter.TIPO_ALARMA_PERSISTENTE:
                View v3 = inflater.inflate(R.layout.lista_chat_alarma_persistente, parent, false);
                viewHolder = new AdaptadorChat.ViewHolderAlarmaPersistente(v3);
                break;
            default:
                //View v = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                //viewHolder = new RecyclerViewSimpleTextViewHolder(v);
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case DBAdapter.TIPO_TEXTO:
                Mensaje item = (Mensaje) listData.get(position);
                ((ViewHolderMensaje)holder).bindLlista(item);
                //configureViewHolder1(vh1, position);
                break;
            case DBAdapter.TIPO_ALARMA_REPETITIVA:
                Alarma alarma1 = (Alarma) listData.get(position);
                ((ViewHolderAlarmaPersistente)holder).bindLlista(alarma1);
                break;
            case DBAdapter.TIPO_ALARMA_PERSISTENTE:
                Alarma alarma2 = (Alarma) listData.get(position);
                ((ViewHolderAlarmaPersistente)holder).bindLlista(alarma2);
                break;
            default:
                //RecyclerViewSimpleTextViewHolder vh = (RecyclerViewSimpleTextViewHolder) viewHolder;
                //configureDefaultViewHolder(vh, position);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Object objeto=listData.get(position);
        if (objeto instanceof Mensaje) {
            return DBAdapter.TIPO_TEXTO;
        } else if (objeto instanceof Alarma) {
            return ((Alarma) objeto).getTipo();
        }
        return -1;
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public static class ViewHolderMensaje extends RecyclerView.ViewHolder{
        //Datos del view
        private TextView textViewMensaje;
        private TextView textViewHora;
        private CardView cardView;

        public ViewHolderMensaje(View itemView) {
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
                int margin = this.itemView.getResources().getDimensionPixelSize(R.dimen.chat_margin1);
                layoutParams.setMargins(dpToPixel(60), margin, margin, dpToPixel(5));
            }else{
                int margin = this.itemView.getResources().getDimensionPixelSize(R.dimen.chat_margin1);
                layoutParams.setMargins(margin, margin, dpToPixel(60), dpToPixel(5));
            }
            cardView.setLayoutParams(layoutParams);
        }

        private int dpToPixel(float dp){
            DisplayMetrics displayMetrics = this.itemView.getResources().getDisplayMetrics();
            return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        }
    }

    public class ViewHolderAlarmaPersistente extends RecyclerView.ViewHolder {
        private TextView textViewTitulo,textViewMensaje,textViewHoraInicio,textViewHoraDuracion,textViewFrecuencia;
        private CardView cardView;

        public ViewHolderAlarmaPersistente(View itemView) {
            super(itemView);

            textViewTitulo = (TextView) itemView.findViewById(R.id.textView_titulo_AP);
            textViewMensaje = (TextView) itemView.findViewById(R.id.textView_mensaje_AP);
            textViewHoraInicio = (TextView) itemView.findViewById(R.id.textView_horaInicio_AP);
            textViewHoraDuracion = (TextView) itemView.findViewById(R.id.textView_horaDuracion_AP);
            textViewFrecuencia = (TextView) itemView.findViewById(R.id.textView_frecuencia_AP);
            cardView= (CardView) itemView.findViewById(R.id.card_view_AP);
        }

        public void bindLlista(Alarma elementLlista) {
            if(elementLlista.getTipo()==DBAdapter.TIPO_ALARMA_REPETITIVA){
                textViewTitulo.setText("Alarma repetitiva");
            }else if (elementLlista.getTipo()==DBAdapter.TIPO_ALARMA_PERSISTENTE){
                textViewTitulo.setText("Alarma persistente");
            }
            textViewMensaje.setText(elementLlista.getMensaje());
            textViewHoraInicio.setText(elementLlista.getHora_inicio());
            textViewHoraDuracion.setText(elementLlista.getHora_duracion());
            textViewFrecuencia.setText(elementLlista.getFrecuencia());

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
