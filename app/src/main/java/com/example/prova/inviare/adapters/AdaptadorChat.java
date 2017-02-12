package com.example.prova.inviare.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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
            case DBAdapter.TIPO_ALARMA_PERSISTENTE:
                View v2 = inflater.inflate(R.layout.lista_chat_alarma_persistente, parent, false);
                viewHolder = new AdaptadorChat.ViewHolderAlarmaPersistente(v2);
                break;
            case DBAdapter.TIPO_ALARMA_FIJA:
                View v3 = inflater.inflate(R.layout.lista_chat_alarma_fija, parent, false);
                viewHolder = new AdaptadorChat.ViewHolderAlarmaFija(v3);
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
            case DBAdapter.TIPO_ALARMA_PERSISTENTE:
                Alarma alarma1 = (Alarma) listData.get(position);
                ((ViewHolderAlarma)holder).bindLlista(alarma1);
                addClickListener(((ViewHolderAlarma)holder),position);
                break;
            case DBAdapter.TIPO_ALARMA_FIJA:
                Alarma alarma3 = (Alarma) listData.get(position);
                ((ViewHolderAlarma)holder).bindLlista(alarma3);
                addClickListener(((ViewHolderAlarma)holder),position);
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

    private void addClickListener(final ViewHolderAlarma holder, final int position){
        //Se actualiza el estado de la alarma y se envia por Firebase
        holder.btnDeclinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Dialog para introducir el mensaje para declinar tarea.
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle(context.getResources().getString(R.string.dialog_declinar_tarea_titulo));

                // Set up the input
                EditText editTextDialog = new EditText(holder.itemView.getContext());
                editTextDialog.setSelectAllOnFocus(true);
                editTextDialog.setMaxLines(4);
                editTextDialog.setText(context.getResources().getString(R.string.dialog_mensaje_declinar_tarea));
                final EditText input = editTextDialog;
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton(context.getResources().getString(R.string.dialog_declinar_tarea), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Tarea rechazada
                        String contestacion=input.getText().toString();
                        //Actualizar interfaz
                        holder.textViewContestacion.setText(contestacion);
                        holder.layoutAlarmaBotones.setVisibility(View.GONE);
                        holder.textViewContestacion.setVisibility(View.VISIBLE);
                        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                        //Actualizar array
                        ((Alarma)listData.get(position)).setCursoTarea(Alarma.TAREA_RECHAZADA);
                        ((Alarma)listData.get(position)).setContestacion(contestacion);
                        //Actualizar base de datos

                        //Actualizar firabase
                    }
                });
                builder.setNegativeButton(context.getResources().getString(R.string.dialog_cancelar), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Cancelar acción
                        dialog.cancel();
                    }
                });
                Dialog dialog= builder.create();
                android.view.Window window=dialog.getWindow();
                if(window!=null){
                    window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
                    dialog.show();
                }
            }
        });
        holder.btnTareaRealizada.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                //Tarea realizada
                                final String textoTareaRealizada= context.getResources().getString(R.string.tarea_realizada);
                                //Actualizar interfaz
                                holder.textViewContestacion.setText(textoTareaRealizada);
                                holder.layoutAlarmaBotones.setVisibility(View.GONE);
                                holder.textViewContestacion.setVisibility(View.VISIBLE);
                                holder.cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                                //Actualizar array
                                ((Alarma)listData.get(position)).setCursoTarea(Alarma.TAREA_REALIZADA);
                                ((Alarma)listData.get(position)).setContestacion(textoTareaRealizada);
                                //Actualizar base de datos

                                //Actualizar firabase
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setTitle(context.getResources().getString(R.string.dialog_tarea_realizada_titulo))
                        .setMessage(context.getResources().getString(R.string.dialog_tarea_realizada))
                        .setPositiveButton(context.getResources().getString(R.string.dialog_si), dialogClickListener)
                        .setNegativeButton(context.getResources().getString(R.string.dialog_no), dialogClickListener).show();
            }
        });
    }

    private class ViewHolderMensaje extends RecyclerView.ViewHolder{
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

            cambiarVista(cardView, elementLlista.isPropietario());
        }
    }

    private abstract class ViewHolderAlarma extends RecyclerView.ViewHolder{
        TextView textViewMensaje,textViewHoraDuracion,textViewContestacion,textVewHora;
        Button btnDeclinar,btnTareaRealizada;
        CardView cardView;
        LinearLayout layoutAlarmaBotones;

        public ViewHolderAlarma(View itemView) {
            super(itemView);

            textViewMensaje = (TextView) itemView.findViewById(R.id.textView_mensaje_A);
            textViewHoraDuracion = (TextView) itemView.findViewById(R.id.textView_horaDuracion_A);
            textViewContestacion = (TextView) itemView.findViewById(R.id.textView_contestacion_A);
            textVewHora = (TextView) itemView.findViewById(R.id.textView_hora_A);
            btnDeclinar = (Button) itemView.findViewById(R.id.btn_declinar_tarea);
            btnTareaRealizada = (Button) itemView.findViewById(R.id.btn_tareaRealizada);
            cardView= (CardView) itemView.findViewById(R.id.card_view_A);
            layoutAlarmaBotones= (LinearLayout) itemView.findViewById(R.id.layout_alarma_botones);
        }

        abstract public void bindLlista(Alarma elementLlista);
    }

    private class ViewHolderAlarmaPersistente extends ViewHolderAlarma {
        private TextView textViewTitulo,textViewHoraInicio,textViewFrecuencia;

        public ViewHolderAlarmaPersistente(View itemView) {
            super(itemView);
            textViewTitulo = (TextView) itemView.findViewById(R.id.textView_titulo_AP);
            textViewHoraInicio = (TextView) itemView.findViewById(R.id.textView_horaInicio_AP);
            textViewFrecuencia = (TextView) itemView.findViewById(R.id.textView_frecuencia_AP);
        }

        public void bindLlista(Alarma elementLlista) {
            if (elementLlista.getTipo() == DBAdapter.TIPO_ALARMA_REPETITIVA) {
                textViewTitulo.setText(context.getResources().getString(R.string.tipo_alarma_repetitiva));
            } else if (elementLlista.getTipo() == DBAdapter.TIPO_ALARMA_PERSISTENTE) {
                textViewTitulo.setText(context.getResources().getString(R.string.tipo_alarma_persistente));
            }
            textViewMensaje.setText(elementLlista.getMensaje());
            textViewHoraInicio.setText(elementLlista.getHora_inicio());
            textViewHoraDuracion.setText(elementLlista.getHora_duracion());
            if(elementLlista.getTipo()==DBAdapter.TIPO_ALARMA_REPETITIVA){
                textViewFrecuencia.setText(elementLlista.getFrecuencia());
            }else{
                textViewFrecuencia.setVisibility(View.GONE);
            }
            textVewHora.setText(elementLlista.getFecha());
            textViewContestacion.setText(elementLlista.getContestacion());

            cambiarVista(cardView, elementLlista.isPropietario());

            //Hacer cambios en el View de la alarma en función del curso de la tarea (CursoTarea)
            if (elementLlista.getCursoTarea().compareTo(Alarma.TAREA_EN_CURSO) != 0) {
                layoutAlarmaBotones.setVisibility(View.GONE);
                textViewContestacion.setVisibility(View.VISIBLE);
                if (elementLlista.getCursoTarea().compareTo(Alarma.TAREA_RECHAZADA) == 0) {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                } else if (elementLlista.getCursoTarea().compareTo(Alarma.TAREA_REALIZADA) == 0) {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                }
            }
        }

    }

    private class ViewHolderAlarmaFija extends ViewHolderAlarma {

        public ViewHolderAlarmaFija(View itemView) {
            super(itemView);
        }

        public void bindLlista(Alarma elementLlista) {
            textViewMensaje.setText(elementLlista.getMensaje());
            textViewHoraDuracion.setText(elementLlista.getHora_duracion());
            textVewHora.setText(elementLlista.getFecha());
            textViewContestacion.setText(elementLlista.getContestacion());

            cambiarVista(cardView, elementLlista.isPropietario());

            //Hacer cambios en el View de la alarma en función del curso de la tarea (CursoTarea)
            if (elementLlista.getCursoTarea().compareTo(Alarma.TAREA_EN_CURSO) != 0) {
                layoutAlarmaBotones.setVisibility(View.GONE);
                textViewContestacion.setVisibility(View.VISIBLE);
                if (elementLlista.getCursoTarea().compareTo(Alarma.TAREA_RECHAZADA) == 0) {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_red_light));
                } else if (elementLlista.getCursoTarea().compareTo(Alarma.TAREA_REALIZADA) == 0) {
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, android.R.color.holo_green_dark));
                }
            }
        }
    }

    /**
     * Se alinea la vista a izquierda o derecha dependiendo de si es del usuario propietario o no.
     * @param view vista que se desea alinear
     * @param esPropietario 'true' si la vista es del propietario o 'false' si no lo es
     */
    private void cambiarVista(View view, boolean esPropietario){
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (esPropietario) {
            int margin = context.getResources().getDimensionPixelSize(R.dimen.chat_margin1);
            layoutParams.setMargins(dpToPixel(60), margin, margin, dpToPixel(5));
        } else {
            int margin = context.getResources().getDimensionPixelSize(R.dimen.chat_margin1);
            layoutParams.setMargins(margin, margin, dpToPixel(60), dpToPixel(5));
        }
        view.setLayoutParams(layoutParams);
    }

    private int dpToPixel(float dp){
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}
