package com.example.prova.inviare.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prova.inviare.AlarmasActivity;
import com.example.prova.inviare.MainActivity;
import com.example.prova.inviare.R;
import com.example.prova.inviare.elementos.Alarma;

import java.util.List;


public class AdaptadorAlarmas extends RecyclerView.Adapter <AdaptadorAlarmas.AlarmasViewHolder> {
    private List<Alarma> llistaAlarmas;
    AlarmasActivity aA;

    public AdaptadorAlarmas(List<Alarma> alarmas, AlarmasActivity aA) {
        llistaAlarmas = alarmas;
        this.aA=aA;
    }


    //Creamos el ViewHolder y creamos y iniciamos cada elemento del card
    public static class AlarmasViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView tipo, mensaje, hora_inicial, dia_duracion, frecuencia;
        String mItem;
        ImageView img;
        AlarmasActivity aA;
        List<Alarma> alarmas;


        public AlarmasViewHolder(View v, AlarmasActivity aA, List<Alarma> alarmas) {
            super(v);
            v.setOnClickListener(this);
            mensaje = (TextView) v.findViewById(R.id.mensaje);
            img = (ImageView) v.findViewById(R.id.imgAlarma);
            hora_inicial = (TextView) v.findViewById(R.id.inicio_h);
            tipo = (TextView) v.findViewById(R.id.tipo_a);
            dia_duracion = (TextView) v.findViewById(R.id.dia_duracion);
            frecuencia = (TextView) v.findViewById(R.id.frecuencia);
            this.aA=aA;
            this.alarmas=alarmas;



        }
        public void setItem(String item) {
            mItem = item;
        }
        public void onClick(View view) {
            //se le llama al método que guarda las alarmas desde el AlarmasActivity.
            //aA.guardarAlarmas(alarmas.get(getPosition()).getMensaje(), alarmas.get(getPosition()).getTipo(), alarmas.get(getPosition()).getHora_inicio(), alarmas.get(getPosition()).getHora_duracion(), alarmas.get(getPosition()).getFrecuencia(), alarmas.get(getPosition()).getFecha());

        }
    }



    @Override
    public int getItemCount() {

        return this.llistaAlarmas.size();
    }

    @Override
    //le inflamos al holder el xml del diseño de cada card
    public AlarmasViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lista_alarma, parent, false);
        return new AlarmasViewHolder(v, aA, llistaAlarmas);
    }


    @Override
    //Enlazamos la información que queremos mostrar a un holder
    public void onBindViewHolder(final AlarmasViewHolder holder, final int position) {
        holder.mensaje.setText(llistaAlarmas.get(position).getMensaje());
        if (llistaAlarmas.get(position).getTipo()==4){
            //holder.img.setImageResource();
            holder.tipo.setText(R.string.tipo_alarma_fija);
            holder.hora_inicial.setText(llistaAlarmas.get(position).getHora_inicio());
            holder.dia_duracion.setText(llistaAlarmas.get(position).getFecha());
            holder.frecuencia.setAlpha(0);
        }else if (llistaAlarmas.get(position).getTipo()==3){
            //holder.img.setImageResource();
            holder.tipo.setText(R.string.tipo_alarma_persistente);
            if (llistaAlarmas.get(position).getHora_inicio().equals("")){
                holder.hora_inicial.setText(R.string.instantanea);
            }else{
                holder.hora_inicial.setText("Empieza en "+llistaAlarmas.get(position).getHora_inicio());
            }
            holder.dia_duracion.setText("Dura: "+llistaAlarmas.get(position).getHora_duracion());
            holder.frecuencia.setAlpha(0);
        }else if (llistaAlarmas.get(position).getTipo()==2){
            //holder.img.setImageResource();
            holder.frecuencia.setAlpha(1f);
            holder.tipo.setText(R.string.tipo_alarma_repetitiva);
            if (llistaAlarmas.get(position).getHora_inicio().equals("")){
                holder.hora_inicial.setText(R.string.instantanea);
            }else{
                holder.hora_inicial.setText("Empieza en "+llistaAlarmas.get(position).getHora_inicio());
            }
            //holder.h_i.setText(llistaAlarmas.get(position).getHora_inicio());
            holder.dia_duracion.setText("Dura: "+llistaAlarmas.get(position).getHora_duracion());
            holder.frecuencia.setText("Suena cada "+llistaAlarmas.get(position).getFrecuencia());
        }
    }
}
