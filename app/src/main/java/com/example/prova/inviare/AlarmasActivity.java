package com.example.prova.inviare;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import com.example.prova.inviare.servicios.ControladorAlarma;
import com.example.prova.inviare.servicios.ServicioAlarmas;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static android.content.ContentValues.TAG;

public class AlarmasActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private Spinner spinnerTipoAlarmas,spinnerTiempoInicio,spinnerDuracion,spinnerFrecuencia;
    private EditText editTextMensaje;
    private CheckBox chkInstante;
    private String selected;
    private Button btnHora,btnDia;
    private TextView textViewFreq;
    private GregorianCalendar calendarFechaAlarmaFija;

    // service
    private ServicioAlarmas mService;
    private boolean mBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmas);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(getString(R.string.TABLE_ALARMAS));

        final DBAdapter myDB = new DBAdapter(this);
        myDB.open();

        final String tipo[] = getResources().getStringArray(R.array.tipo_alarma);
        final String frecuencia_array[] = getResources().getStringArray(R.array.frecuencia);
        final String hora_inicio_duracion[] = getResources().getStringArray(R.array.h_duracion_inicio);
        calendarFechaAlarmaFija= new GregorianCalendar();

        final LinearLayout layoutFrecuencia = (LinearLayout) findViewById(R.id.layoutFrecuencia);
        /*CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Alarmas");*/

        spinnerTipoAlarmas = (Spinner) findViewById(R.id.spinnerTipo);
        editTextMensaje = (EditText) findViewById(R.id.etxtMensaje);
        btnHora = (Button) findViewById(R.id.btnHora);
        spinnerFrecuencia = (Spinner) findViewById(R.id.spinnerFrecuencia);
        btnDia = (Button) findViewById(R.id.btnDia);
        chkInstante = (CheckBox) findViewById(R.id.chkInstante);
        final TextView textViewDuracion = (TextView) findViewById(R.id.txtDuracion);
        textViewFreq = (TextView) findViewById(R.id.txtFrecuencia);
        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        spinnerDuracion = (Spinner) findViewById(R.id.spinnerDuracion);
        spinnerTiempoInicio = (Spinner) findViewById(R.id.spinnerInicio);
        spinnerTiempoInicio.setEnabled(false);
        spinnerTiempoInicio.setClickable(false);
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
        List<Alarma> alarmas = myDB.recuperarALARMA();
        //ArrayList de alarmas
        final ArrayList<Alarma> listaAlarmas = new ArrayList<>();
        for (int i = 0; i < alarmas.size(); i++) {
            listaAlarmas.add(alarmas.get(i));
        }
        //listaAlarmas.add(new Alarma(11,"Alarma 2", "", DBAdapter.TIPO_ALARMA_REPETITIVA,"2h","3h","20 min", "", "", true));

        final AdaptadorAlarmas adaptadorAlarmas;
        adaptadorAlarmas = new AdaptadorAlarmas(listaAlarmas, AlarmasActivity.this, getApplicationContext());
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
                final String[] duracionInicioSpinner=getResources().getStringArray(R.array.h_duracion_inicio_milisegundos);
                final String[] frecuenciaSpinner=getResources().getStringArray(R.array.frecuencia_milisegundos);

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
                    hora_duracion=dateFormat.format(calendarFechaAlarmaFija.getTimeInMillis());
                }else if(selected.equals(getString(R.string.tipo_repetitiva))){
                    tipoAlarma=DBAdapter.TIPO_ALARMA_REPETITIVA;
                    if(!chkInstante.isChecked())hora_inicio = duracionInicioSpinner[(int)spinnerTiempoInicio.getSelectedItemId()];
                    hora_duracion = duracionInicioSpinner[(int)spinnerDuracion.getSelectedItemId()];
                    frecuencia = frecuenciaSpinner[(int)spinnerFrecuencia.getSelectedItemId()];
                }else if(selected.equals(getString(R.string.tipo_persistente))){
                    tipoAlarma=DBAdapter.TIPO_ALARMA_PERSISTENTE;
                    if(!chkInstante.isChecked())hora_inicio = duracionInicioSpinner[(int)spinnerTiempoInicio.getSelectedItemId()];
                    hora_duracion = duracionInicioSpinner[(int)spinnerDuracion.getSelectedItemId()];
                }

                Intent i = getIntent();
                // DB_ADAPTER - Se guarda en la base de datos local
                final String idConversacion=i.getExtras().getString(getResources().getString(R.string.intent_conversacion_id)); //ID_conversacion
                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                dbAdapter.open();
                final long idAlarma = dbAdapter.insertarMensaje(mensaje,fechaDataBase,tipoAlarma,hora_inicio,hora_duracion,frecuencia,Alarma.TAREA_EN_CURSO,idConversacion,idConversacion);

                // INTENT - Se muestra en el activity anterior
                i.putExtra(getResources().getString(R.string.intent_alarma_codigo),(int)idAlarma);
                i.putExtra(getResources().getString(R.string.intent_alarma_mensaje),mensaje);
                i.putExtra(getResources().getString(R.string.intent_alarma_fecha),fechaMuestra);
                i.putExtra(getResources().getString(R.string.intent_alarma_tipo),tipoAlarma);
                i.putExtra(getResources().getString(R.string.intent_alarma_hora_inicio),hora_inicio);
                i.putExtra(getResources().getString(R.string.intent_alarma_hora_duracion),hora_duracion);
                i.putExtra(getResources().getString(R.string.intent_alarma_frecuencia),frecuencia);
                // Se le pasa el Bundle al intent
                i.putExtras(i.getExtras());
                setResult(RESULT_OK, i);

                // ALARM_MANAGER
                Alarma alarma = new Alarma((int)idAlarma,mensaje,fechaMuestra,tipoAlarma,hora_inicio,hora_duracion,frecuencia,Alarma.TAREA_EN_CURSO,null,true);
                ControladorAlarma controladorAlarma = new ControladorAlarma(AlarmasActivity.this,alarma);
                controladorAlarma.ponerAlarma();
                controladorAlarma.guardarAlarmasPuestas();

                Intent startIntent = new Intent(AlarmasActivity.this, ServicioAlarmas.class);
                startIntent.setAction(getString(R.string.servicio_empezar));
                startService(startIntent);

                // FIREBASE - Se guarda en Firebase
                myRef.push().setValue(alarma);

                finish();
            }
        });


    }

    public void rellenarCampos(Alarma a){
        editTextMensaje.setText(a.getMensaje());
        switch(a.getTipo()){
            case DBAdapter.TIPO_ALARMA_PERSISTENTE:
                spinnerTipoAlarmas.setSelection(2,true);
                break;
            case DBAdapter.TIPO_ALARMA_REPETITIVA:
                spinnerTipoAlarmas.setSelection(1,true);
                spinnerFrecuencia.setSelection(1,true);
                break;
            case DBAdapter.TIPO_ALARMA_FIJA:
                spinnerTipoAlarmas.setSelection(0,true);
                break;
        }
        chkInstante.setChecked(a.getHoraInicio()==null);
        spinnerTiempoInicio.setSelection(1,true);
        spinnerDuracion.setSelection(1,true);

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

    @Override
    protected void onStart() {
        super.onStart();
        // Bind to LocalService
        Intent intent = new Intent(this, ServicioAlarmas.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    /** Defines callbacks for service binding, passed to bindService() */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            ServicioAlarmas.LocalBinder binder = (ServicioAlarmas.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };
}



