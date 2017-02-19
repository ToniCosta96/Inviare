package com.example.prova.inviare.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.prova.inviare.AlarmasActivity;
import com.example.prova.inviare.MainActivity;
import com.example.prova.inviare.R;
import com.example.prova.inviare.db_adapters.DBAdapter;
import com.example.prova.inviare.elementos.Alarma;

import java.util.List;


public class AdaptadorAlarmas extends RecyclerView.Adapter <AdaptadorAlarmas.AlarmasViewHolder> {
    private List<Alarma> llistaAlarmas;
    AlarmasActivity aA;
    Context c;

    public AdaptadorAlarmas(List<Alarma> alarmas, AlarmasActivity aA, Context c) {
        llistaAlarmas = alarmas;
        this.aA=aA;
        this.c=c;
    }


    //Creamos el ViewHolder y creamos y iniciamos cada elemento del card
    public static class AlarmasViewHolder extends RecyclerView.ViewHolder{
        public TextView tipo, mensaje, hora_inicial, dia_duracion, frecuencia;
        String mItem;
        ImageView img;
        AlarmasActivity aA;
        List<Alarma> alarmas;


        public AlarmasViewHolder(View v, AlarmasActivity aA, List<Alarma> alarmas) {
            super(v);
            //v.setOnClickListener(this);
            mensaje = (TextView) v.findViewById(R.id.mensaje);
            img = (ImageView) v.findViewById(R.id.imgAlarma);
            hora_inicial = (TextView) v.findViewById(R.id.inicio_h);
            tipo = (TextView) v.findViewById(R.id.tipo_a);
            dia_duracion = (TextView) v.findViewById(R.id.dia_duracion);
            frecuencia = (TextView) v.findViewById(R.id.frecuencia);
            this.aA=aA;
            this.alarmas=alarmas;
        }
        /*public void setItem(String item) {
            mItem = item;
        }
        public void onClick(View view) {

            //se le llama al método que guarda las alarmas desde el AlarmasActivity.
            //aA.guardarAlarmas(alarmas.get(getPosition()).getMensaje(), alarmas.get(getPosition()).getTipo(), alarmas.get(getPosition()).getHora_inicio(), alarmas.get(getPosition()).getHora_duracion(), alarmas.get(getPosition()).getFrecuencia(), alarmas.get(getPosition()).getFecha());

        }*/


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
        final String mensaje=llistaAlarmas.get(position).getMensaje();
        final String fecha=llistaAlarmas.get(position).getMensaje();
        final int tipo= llistaAlarmas.get(position).getTipo();
        final String h_i=llistaAlarmas.get(position).getHoraInicio();
        final String h_d=llistaAlarmas.get(position).getHoraDuracion();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (llistaAlarmas.get(position).getTipo()== DBAdapter.TIPO_ALARMA_REPETITIVA) {
                    final String frecuencia = llistaAlarmas.get(position).getFrecuencia();
                    holder.aA.rellenarCampos(mensaje, fecha, tipo, h_i, h_d, frecuencia);
                }else{
                    holder.aA.rellenarCampos(mensaje, fecha, tipo, h_i, h_d, "");
                }
            }
        });
        holder.mensaje.setText(llistaAlarmas.get(position).getMensaje());
        if (llistaAlarmas.get(position).getTipo()==4){
            holder.hora_inicial.setVisibility(View.GONE);
            //holder.img.setImageResource();
            holder.tipo.setText(R.string.tipo_alarma_fija);
            //holder.hora_inicial.setText(llistaAlarmas.get(position).getFecha());
            holder.dia_duracion.setText(llistaAlarmas.get(position).getFecha());
            holder.frecuencia.setAlpha(0);
        }else if (llistaAlarmas.get(position).getTipo()==3){
            holder.hora_inicial.setAlpha(1f);
            //holder.img.setImageResource();
            holder.tipo.setText(R.string.tipo_alarma_persistente);
            if (llistaAlarmas.get(position).getHoraInicioFormateada(c)==null){
                holder.hora_inicial.setText(R.string.instantanea);
            }else{
                holder.hora_inicial.setText(c.getString(R.string.text_inicio)+" "+llistaAlarmas.get(position).getHoraInicioFormateada(c));
            }
            holder.dia_duracion.setText(c.getString(R.string.text_duracion)+" "+llistaAlarmas.get(position).getHoraDuracionFormateada(c));
            holder.frecuencia.setAlpha(0);
        }else if (llistaAlarmas.get(position).getTipo()==2){
            holder.hora_inicial.setAlpha(1f);
            //holder.img.setImageResource();
            holder.frecuencia.setAlpha(1f);
            holder.tipo.setText(R.string.tipo_alarma_repetitiva);
            if (llistaAlarmas.get(position).getHoraInicioFormateada(c)==null){
                holder.hora_inicial.setText(R.string.instantanea);
            }else{
                holder.hora_inicial.setText(c.getString(R.string.text_inicio)+" "+llistaAlarmas.get(position).getHoraInicioFormateada(c));
            }
            //holder.h_i.setText(llistaAlarmas.get(position).getHora_inicio());
            holder.dia_duracion.setText(c.getString(R.string.text_duracion)+" "+llistaAlarmas.get(position).getHoraDuracionFormateada(c));
            holder.frecuencia.setText(c.getString(R.string.text_frecuencia)+" "+llistaAlarmas.get(position).getFrecuenciaFormateada(c));
        }
    }
}
