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
        public TextView tipo, mensaje, h_i;
        String mItem;
        ImageView img;
        AlarmasActivity aA;
        List<Alarma> alarmas;


        public AlarmasViewHolder(View v, AlarmasActivity aA, List<Alarma> alarmas) {
            super(v);
            v.setOnClickListener(this);
            mensaje = (TextView) v.findViewById(R.id.mensaje);
            img = (ImageView) v.findViewById(R.id.imgAlarma);
            h_i = (TextView) v.findViewById(R.id.inicio_h);
            tipo = (TextView) v.findViewById(R.id.tipo_a);
            this.aA=aA;
            this.alarmas=alarmas;



        }
        public void setItem(String item) {
            mItem = item;
        }
        public void onClick(View view) {
            aA.guardarAlarmas(alarmas.get(getPosition()).getMensaje(), alarmas.get(getPosition()).getTipo(), alarmas.get(getPosition()).getHora_inicio(), alarmas.get(getPosition()).getHora_duracion(), alarmas.get(getPosition()).getFrecuencia(), alarmas.get(getPosition()).getFecha());

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
        holder.h_i.setText(llistaAlarmas.get(position).getHora_inicio());
        //holder.tipo.setText(llistaAlarmas.get(position).getTipo());





    }
}
