package com.example.prova.inviare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;

public class AlarmasActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private Spinner spinnerDuracion;
    private String selected;
    private Spinner spinnerFrecuencia;
    private Spinner spinnerTiempoInicio;
    private Button btnHora,btnDia;
    private TextView textViewFreq;
    private GregorianCalendar calendarFechaAlarmaFija;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmas);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        final String tipo[] = getResources().getStringArray(R.array.tipo_alarma);
        final String frecuencia_array[] = getResources().getStringArray(R.array.frecuencia);
        final String hora_inicio_duracion[] = getResources().getStringArray(R.array.duracion_h_inicio);
        calendarFechaAlarmaFija= new GregorianCalendar();

        final LinearLayout layoutFrecuencia = (LinearLayout) findViewById(R.id.layoutFrecuencia);
        /*CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Alarmas");*/

        final Spinner spinnerTipoAlarmas = (Spinner) findViewById(R.id.spinnerTipo);
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
            listaAlarmas.add(new Alarma("Alarma 1", "Lunes", DBAdapter.TIPO_ALARMA_FIJA,"12::00","","jk", "", "", true));
        }
        listaAlarmas.add(new Alarma("Alarma 2", "", DBAdapter.TIPO_ALARMA_REPETITIVA,"2h","3h","20 min", "", "", true));

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
                }else{
                    spinnerTiempoInicio.setAlpha(1f);
                    spinnerTiempoInicio.setEnabled(true);
                    spinnerTiempoInicio.setClickable(true);
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
                    layoutFrecuencia.setVisibility(View.VISIBLE);
                }else if(selected.compareTo(getString(R.string.tipo_persistente))==0){
                    chkInstante.setVisibility(View.VISIBLE);
                    spinnerTiempoInicio.setVisibility(View.VISIBLE);
                    textViewDuracion.setVisibility(View.VISIBLE);
                    spinnerDuracion.setVisibility(View.VISIBLE);
                    textViewFreq.setVisibility(View.VISIBLE);
                    btnHora.setVisibility(View.GONE);
                    btnDia.setVisibility(View.GONE);
                    layoutFrecuencia.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // FLOATING_ACTION_BUTTON listener
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String mensaje=editTextMensaje.getText().toString();
                final SimpleDateFormat dfDataBase = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_DB), java.util.Locale.getDefault());
                final SimpleDateFormat dfMuestra = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_MENSAJE), java.util.Locale.getDefault());
                final Date horaActual = Calendar.getInstance().getTime();
                final String fechaDataBase = dfDataBase.format(horaActual);
                final String fechaMuestra = dfMuestra.format(horaActual);
                int tipoAlarma=0;
                String hora_inicio=null;
                String hora_duracion="-1";
                String frecuencia=null;

                if (selected.equals(getString(R.string.tipo_fija))){
                    tipoAlarma=DBAdapter.TIPO_ALARMA_FIJA;
                    SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_MENSAJE), java.util.Locale.getDefault());
                    hora_duracion=dateFormat.format(calendarFechaAlarmaFija.getTime());
                }else if(selected.equals(getString(R.string.tipo_repetitiva))){
                    tipoAlarma=DBAdapter.TIPO_ALARMA_REPETITIVA;
                    if(!chkInstante.isChecked())hora_inicio=spinnerTiempoInicio.getSelectedItem().toString();
                    hora_duracion=spinnerDuracion.getSelectedItem().toString();
                    frecuencia=spinnerFrecuencia.getSelectedItem().toString();
                }else if(selected.equals(getString(R.string.tipo_persistente))){
                    tipoAlarma=DBAdapter.TIPO_ALARMA_PERSISTENTE;
                    if(!chkInstante.isChecked())hora_inicio=spinnerTiempoInicio.getSelectedItem().toString();
                    hora_duracion=spinnerDuracion.getSelectedItem().toString();
                }

                // INTENT - Se muestra en el activity anterior
                Intent i = getIntent();
                i.putExtra(getResources().getString(R.string.intent_alarma_mensaje),mensaje);
                i.putExtra(getResources().getString(R.string.intent_alarma_fecha),fechaMuestra);
                i.putExtra(getResources().getString(R.string.intent_alarma_tipo),tipoAlarma);
                i.putExtra(getResources().getString(R.string.intent_alarma_hora_inicio),hora_inicio);
                i.putExtra(getResources().getString(R.string.intent_alarma_hora_duracion),hora_duracion);
                i.putExtra(getResources().getString(R.string.intent_alarma_frecuencia),frecuencia);
                setResult(RESULT_OK, i);
                // DB_ADAPTER - Se guarda en la base de datos local
                final String idConversacion=i.getStringExtra(getResources().getString(R.string.intent_conversacion_id)); //ID_conversacion
                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                dbAdapter.open();
                dbAdapter.insertarMensaje(mensaje,fechaDataBase,tipoAlarma,hora_inicio,hora_duracion,frecuencia,Alarma.TAREA_EN_CURSO,idConversacion,idConversacion);
                // FIREBASE - Se guarda en Firebase


                finish();
            }
        });
    }

    public void guardarAlarmas(String mensaje, String fecha, int tipo, String hora_inicio, String hora_duracion, String frecuencia) {
        //Aqui recibimos los datos al pulsar el cardview
        Log.d(TAG, tipo + " " + mensaje);
        /*if (tipo.equals("Persistente")) {

        } else if (tipo.equals("Fija")) {

        } else if (tipo.equals("Repetitiva")) {

        }*/
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
        calendarFechaAlarmaFija.set(year,month,dayOfMonth);

        final SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_FECHA), java.util.Locale.getDefault());
        btnDia.setText(getResources().getString(R.string.alarma_fecha,dateFormat.format(calendarFechaAlarmaFija.getTime())));

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourFinal, int minuteFinal) {
        calendarFechaAlarmaFija.set(Calendar.HOUR_OF_DAY, hourFinal);
        calendarFechaAlarmaFija.set(Calendar.MINUTE, minuteFinal);

        final SimpleDateFormat dateFormat = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_HORA), java.util.Locale.getDefault());
        btnHora.setText(getResources().getString(R.string.alarma_hora,dateFormat.format(calendarFechaAlarmaFija.getTime())));
    }
}



