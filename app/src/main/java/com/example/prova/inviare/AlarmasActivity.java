package com.example.prova.inviare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import com.example.prova.inviare.adapters.AdaptadorAlarmas;
import com.example.prova.inviare.db_adapters.DBAdapter;
import com.example.prova.inviare.elementos.Alarma;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;

public class AlarmasActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private Spinner spinnerTipoAlarmas;
    private boolean chClicked;
    private String resultado;
    private Spinner spinnerDuracion;
    private String selected;
    private Spinner spinnerFrecuencia;
    private Spinner spinnerTiempoInicio;
    private Button btnHora,btnDia;
    private String resultadoDia;
    private TextView textViewFreq;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmas);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        final String tipo[] = getResources().getStringArray(R.array.tipo_alarma);
        final String frecuencia_array[] = getResources().getStringArray(R.array.frecuencia);
        final String hora_inicio_duracion[] = getResources().getStringArray(R.array.duracion_h_inicio);

        final LinearLayout layoutD = (LinearLayout) findViewById(R.id.layoutD);
        final LinearLayout layoutH = (LinearLayout) findViewById(R.id.layoutH);
        final LinearLayout layoutFrecuencia = (LinearLayout) findViewById(R.id.layoutFrecuencia);
        /*CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Alarmas");*/

        spinnerTipoAlarmas = (Spinner) findViewById(R.id.spinnerTipo);
        final EditText editTextMensaje = (EditText) findViewById(R.id.etxtMensaje);
        btnHora = (Button) findViewById(R.id.btnHora);
        spinnerFrecuencia = (Spinner) findViewById(R.id.spinnerFrecuencia);
        btnDia = (Button) findViewById(R.id.btnDia);
        final CheckBox chkInstante = (CheckBox) findViewById(R.id.chkInstante);
        final TextView textViewDuracion = (TextView) findViewById(R.id.txtDuracion);
        textViewFreq = (TextView) findViewById(R.id.txtFrecuencia);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        spinnerDuracion = (Spinner) findViewById(R.id.spinnerDuracion);
        spinnerTiempoInicio = (Spinner) findViewById(R.id.spinnerInicio);
        final RecyclerView recyclerViewA = (RecyclerView) findViewById(R.id.recyclerAlarmas);

        ArrayAdapter<String> adapterTipoAlarmas,adapterFrecuencia,adapterHoraInicioDuracion;

        // ADAPTER TIPO DE ALARMA
        adapterTipoAlarmas = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, tipo);
        adapterTipoAlarmas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoAlarmas.setAdapter(adapterTipoAlarmas);

        // ADAPTER HORA INICIO - HORA DURACION
        adapterHoraInicioDuracion = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, hora_inicio_duracion);
        adapterHoraInicioDuracion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTiempoInicio.setAdapter(adapterHoraInicioDuracion);
        spinnerDuracion.setAdapter(adapterHoraInicioDuracion);

        // ADAPTER FRECUENCIA
        adapterFrecuencia = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, frecuencia_array);
        adapterFrecuencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFrecuencia.setAdapter(adapterFrecuencia);


        //ArrayList de alarmas
        ArrayList<Alarma> listaAlarmas = new ArrayList<>();
        for (int i=0;i<10;i++) {
            listaAlarmas.add(new Alarma("Alarma 1", "12", 1,"jk","hj","jk", "", "", true));
        }
        listaAlarmas.add(new Alarma("Alarma 2", "12", 2,"jk","hj","jk", "", "", true));

        AdaptadorAlarmas adaptadorAlarmas;
        adaptadorAlarmas = new AdaptadorAlarmas(listaAlarmas, AlarmasActivity.this);
        // LinearLayoutManager
        LinearLayoutManager rvLM = new LinearLayoutManager(this);
        recyclerViewA.setLayoutManager(rvLM);
        recyclerViewA.setAdapter(adaptadorAlarmas);
        recyclerViewA.setNestedScrollingEnabled(false);

        //Es el listener del checkBox.
        chkInstante.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chkInstante.isChecked()){
                    spinnerTiempoInicio.setAlpha(0.5f);
                    spinnerTiempoInicio.setEnabled(false);
                    spinnerTiempoInicio.setClickable(false);
                    chClicked = true;
                }else{
                    spinnerTiempoInicio.setAlpha(1f);
                    spinnerTiempoInicio.setEnabled(true);
                    spinnerTiempoInicio.setClickable(true);
                    chClicked = false;
                }
            }
        });

        //Cuando le das al boton de dia entra
        btnDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(AlarmasActivity.this, AlarmasActivity.this, year, month, day);
                datePicker.show();

            }
        });
        //Cuando le das al boton de hora entra
        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(AlarmasActivity.this, AlarmasActivity.this, hour, minute, DateFormat.is24HourFormat(getApplicationContext()));
                timePicker.show();

            }
        });

        //Es el listener del spinner de tipo de alarma.
        spinnerTipoAlarmas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //Cuando se selecciona un item del spinner entra aqui
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected = adapterView.getItemAtPosition(i).toString();
                //Se coltrola el tipo de alarma para ir haciendo desaparecer o aparecer las cosas.
                if (selected.compareTo(getString(R.string.tipo_fija))==0){
                    btnHora.setVisibility(View.VISIBLE);
                    btnDia.setVisibility(View.VISIBLE);
                    layoutD.setVisibility(View.VISIBLE);
                    layoutH.setVisibility(View.VISIBLE);
                    chkInstante.setVisibility(View.GONE);
                    spinnerTiempoInicio.setVisibility(View.GONE);
                    textViewDuracion.setVisibility(View.GONE);
                    spinnerDuracion.setVisibility(View.GONE);
                    layoutFrecuencia.setVisibility(View.GONE);
                }else if(selected.compareTo(getString(R.string.tipo_repetitiva))==0) {
                    chkInstante.setVisibility(View.VISIBLE);
                    spinnerTiempoInicio.setVisibility(View.VISIBLE);
                    textViewDuracion.setVisibility(View.VISIBLE);
                    spinnerDuracion.setVisibility(View.VISIBLE);
                    btnHora.setVisibility(View.GONE);
                    btnDia.setVisibility(View.GONE);
                    layoutD.setVisibility(View.GONE);
                    layoutH.setVisibility(View.GONE);
                    layoutFrecuencia.setVisibility(View.VISIBLE);
                }else if(selected.compareTo(getString(R.string.tipo_persistente))==0){
                    chkInstante.setVisibility(View.VISIBLE);
                    spinnerTiempoInicio.setVisibility(View.VISIBLE);
                    textViewDuracion.setVisibility(View.VISIBLE);
                    spinnerDuracion.setVisibility(View.VISIBLE);
                    textViewFreq.setVisibility(View.VISIBLE);
                    btnHora.setVisibility(View.GONE);
                    btnDia.setVisibility(View.GONE);
                    layoutD.setVisibility(View.GONE);
                    layoutH.setVisibility(View.GONE);
                    layoutFrecuencia.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Cuando se pulsa el FAB entra
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra(getResources().getString(R.string.intent_alarma_mensaje),editTextMensaje.getText().toString());
                i.putExtra(getResources().getString(R.string.intent_alarma_fecha),resultadoDia);
                i.putExtra(getResources().getString(R.string.intent_alarma_hora_duracion),spinnerDuracion.getSelectedItem().toString());
                i.putExtra(getResources().getString(R.string.intent_alarma_frecuencia),spinnerFrecuencia.getSelectedItem().toString());

                if (selected.equals(getString(R.string.tipo_fija))){
                    i.putExtra(getResources().getString(R.string.intent_alarma_tipo), DBAdapter.TIPO_ALARMA_FIJA);
                    i.putExtra(getResources().getString(R.string.intent_alarma_hora_inicio),btnHora.getText().toString());
                }else if(selected.equals(getString(R.string.tipo_repetitiva))){
                    i.putExtra(getResources().getString(R.string.intent_alarma_tipo),DBAdapter.TIPO_ALARMA_REPETITIVA);
                    i.putExtra(getResources().getString(R.string.intent_alarma_hora_inicio),resultado);
                }else if(selected.equals(getString(R.string.tipo_persistente))){
                    i.putExtra(getResources().getString(R.string.intent_alarma_tipo),DBAdapter.TIPO_ALARMA_PERSISTENTE);
                    i.putExtra(getResources().getString(R.string.intent_alarma_hora_inicio),resultado);
                }
                setResult(RESULT_OK, i);
                finish();
            }
        });
    }

    public void guardarAlarmas(String mensaje, int tipo,  String  h_i, String  h_f, String feecuencia, String dia) {
        //Aqui recibimos los datos al pulsar el boton de la alarma
        Log.d(TAG, tipo + " " + mensaje);
        /*if (tipo.equals("Persistente")) {

        } else if (tipo.equals("Fija")) {

        } else if (tipo.equals("Repetitiva")) {

        }*/
    }

    @Override
    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar(i, i1 + 1, i2-1);

        int dayOfWeek=gregorianCalendar.get(GregorianCalendar.DAY_OF_WEEK);

        switch (dayOfWeek){
            case 1:
                resultadoDia=getString(R.string.dia_lunes);
                break;
            case 2:
                resultadoDia=getString(R.string.dia_martes);
                break;
            case 3:
                resultadoDia=getString(R.string.dia_miercoles);
                break;
            case 4:
                resultadoDia=getString(R.string.dia_jueves);
                break;
            case 5:
                resultadoDia=getString(R.string.dia_viernes);
                break;
            case 6:
                resultadoDia=getString(R.string.dia_sabado);
                break;
            case 7:
                resultadoDia=getString(R.string.dia_domingo);
                break;
        }
        btnDia.setText("     Dia:  "+resultadoDia);
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourFinal, int minuteFinal) {
        String resultadoHora=String.valueOf(hourFinal);
        String resultadoMinutos =String.valueOf(minuteFinal);

        if (hourFinal<10){
            resultadoHora="0"+String.valueOf(hourFinal);

        }
        if (minuteFinal<10) {
            resultadoMinutos = "0" + String.valueOf(minuteFinal);
        }
        String resultadoH =(resultadoHora + " : : " + resultadoMinutos);
        resultado = (String.valueOf(hourFinal)+ "::" + String.valueOf(minuteFinal));
        btnHora.setText("     Hora:  "+resultadoH);
    }
}



