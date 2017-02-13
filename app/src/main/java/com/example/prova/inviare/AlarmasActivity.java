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
import com.example.prova.inviare.elementos.Alarma;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import static android.content.ContentValues.TAG;

public class AlarmasActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener{
    private ArrayList listaAlarmas;
    private RecyclerView rvA;
    private CheckBox chkInstante;
    private Calendar c = Calendar.getInstance();
    private Spinner tipoAlarmas;
    private String h_inicial;
    private boolean chClicked;
    private String resultadoH;
    private String resultado="";
    private Spinner spn_Duracion;
    private FloatingActionButton FAB;
    private String selected="";
    private Spinner frecuencia;
    private EditText mensaje;
    private Spinner spnh_i;
    private TextView dur;
    private String duracion_spiner;
    private String freq_spiner;
    private String resultadoDia="";
    private TextView freq;
    private Button btnHora;
    private Button btnDia;
    private LinearLayout layoutH;
    private LinearLayout layoutD;
    private int contadorTipo=0;
    private int year, month, day, hour, minute;
    private int yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarmas);
        getWindow().setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));

        AdaptadorAlarmas apdAlarmas;
        ArrayAdapter<String> adapterTipoAlarmas;
        ArrayAdapter<String> adapterFrecuencia;
        ArrayAdapter<String> adapterHoraInicioDuracion;
        LinearLayoutManager rvLM;
        String tipo[] = getResources().getStringArray(R.array.tipo);
        String frecuencia_array[] = getResources().getStringArray(R.array.frecuencia);
        String hora_inicio_duracion[] = getResources().getStringArray(R.array.duracion_h_inicio);



        layoutD = (LinearLayout) findViewById(R.id.layoutD);
        layoutH = (LinearLayout) findViewById(R.id.layoutH);
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("Alarmas");

        tipoAlarmas = (Spinner) findViewById(R.id.spinnerTipo);
        mensaje = (EditText) findViewById(R.id.etxtMensaje);
        btnHora = (Button) findViewById(R.id.btnHora);
        frecuencia = (Spinner) findViewById(R.id.spinnerFrecuencia);
        btnDia = (Button) findViewById(R.id.btnDia);
        chkInstante = (CheckBox) findViewById(R.id.chkInstante);
        dur = (TextView) findViewById(R.id.txtDuracion);
        freq = (TextView) findViewById(R.id.txtFrecuencia);
        FAB = (FloatingActionButton) findViewById(R.id.fab);
        spn_Duracion = (Spinner) findViewById(R.id.spinnerDuracion);
        spnh_i = (Spinner) findViewById(R.id.spinnerInicio);
        rvA = (RecyclerView) findViewById(R.id.recyclerAlarmas);

        adapterHoraInicioDuracion = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, hora_inicio_duracion);
        adapterHoraInicioDuracion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterHoraInicioDuracion.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnh_i.setAdapter(adapterHoraInicioDuracion);
        spn_Duracion.setAdapter(adapterHoraInicioDuracion);

        adapterFrecuencia = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, frecuencia_array);
        adapterFrecuencia.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        frecuencia.setAdapter(adapterFrecuencia);


        adapterTipoAlarmas = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tipo);
        adapterTipoAlarmas.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoAlarmas.setAdapter(adapterTipoAlarmas);


        listaAlarmas = new ArrayList();
        for (int i=0;i<10;i++) {
            listaAlarmas.add(new Alarma("Alarma 1", "12", 1,"jk","hj","jk", "", "", true));
        }
        listaAlarmas.add(new Alarma("Alarma 2", "12", 2,"jk","hj","jk", "", "", true));

        rvLM = new LinearLayoutManager(this);
        apdAlarmas = new AdaptadorAlarmas(listaAlarmas, AlarmasActivity.this);
        rvA.setLayoutManager(rvLM);
        rvA.setAdapter(apdAlarmas);
        rvA.setNestedScrollingEnabled(false);


        //Es el listener del checkBox.
        chkInstante.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                if (chkInstante.isChecked()){
                    spnh_i.setAlpha(0.5f);
                    spnh_i.setEnabled(false);
                    spnh_i.setClickable(false);
                    chClicked = true;
                }else{
                    spnh_i.setAlpha(1f);
                    spnh_i.setEnabled(true);
                    spnh_i.setClickable(true);
                    chClicked = false;
                }
            }
        });

        //Cuando le das al boton de dia entra
        btnDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePicker = new DatePickerDialog(AlarmasActivity.this, AlarmasActivity.this, year, month, day);
                datePicker.show();

            }
        });
        //Cuando le das al boton de hora entra
        btnHora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                hour = c.get(Calendar.HOUR_OF_DAY);
                minute = c.get(Calendar.MINUTE);

                TimePickerDialog timePicker = new TimePickerDialog(AlarmasActivity.this, AlarmasActivity.this, hour, minute, DateFormat.is24HourFormat(getApplicationContext()));
                timePicker.show();

            }
        });

        //Es el listener del spinner de tipo de alarma.
        tipoAlarmas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //Cuando se selecciona un item del spinner entra aqui
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected = adapterView.getItemAtPosition(i).toString();
                contadorTipo=i;
                //Se coltrola el tipo de alarma para ir haciendo desaparecer o aparecer las cosas.
                if (selected.equals(getString(R.string.tipo_fija))){
                    btnHora.setVisibility(View.VISIBLE);
                    btnDia.setVisibility(View.VISIBLE);
                    layoutD.setVisibility(View.VISIBLE);
                    layoutH.setVisibility(View.VISIBLE);
                    chkInstante.setVisibility(View.GONE);
                    spnh_i.setVisibility(View.GONE);
                    dur.setVisibility(View.GONE);
                    spn_Duracion.setVisibility(View.GONE);
                    freq.setVisibility(View.GONE);
                    frecuencia.setVisibility(View.GONE);
                }
                if (selected.equals(getString(R.string.tipo_repetitiva))) {
                    chkInstante.setVisibility(View.VISIBLE);
                    spnh_i.setVisibility(View.VISIBLE);
                    dur.setVisibility(View.VISIBLE);
                    spn_Duracion.setVisibility(View.VISIBLE);
                    freq.setVisibility(View.VISIBLE);
                    frecuencia.setVisibility(View.VISIBLE);
                    btnHora.setVisibility(View.GONE);
                    btnDia.setVisibility(View.GONE);
                    layoutD.setVisibility(View.GONE);
                    layoutH.setVisibility(View.GONE);
                }
                if (selected.equals(getString(R.string.tipo_persistente))){
                    chkInstante.setVisibility(View.VISIBLE);
                    spnh_i.setVisibility(View.VISIBLE);
                    dur.setVisibility(View.VISIBLE);
                    spn_Duracion.setVisibility(View.VISIBLE);
                    freq.setVisibility(View.VISIBLE);
                    frecuencia.setVisibility(View.GONE);
                    freq.setVisibility(View.GONE);
                    btnHora.setVisibility(View.GONE);
                    btnDia.setVisibility(View.GONE);
                    layoutD.setVisibility(View.GONE);
                    layoutH.setVisibility(View.GONE);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Listener del spinner de hora inicio en el caso de habilitarlo.
        spnh_i.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override

            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                h_inicial = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Listener del spinner de duracion.
        spn_Duracion.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //Cuando se selecciona un item del spinner entra aqui
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                duracion_spiner = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //Listener del spinner de frecuencia
        frecuencia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            //Cuando se selecciona un item del spinner entra aqui
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                freq_spiner = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        //Cuando se pulsa el FAB entra
        FAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                Bundle b = new Bundle();
                if (selected.equals(getString(R.string.tipo_fija))){
                    b.putString("fecha", resultadoDia);
                    b.putInt("tipo", contadorTipo+1);
                    b.putString("hora_inicial", resultado);
                    b.putString("hora_duracion", "");
                    b.putString("frecuencia", "");
                    Log.d("Hola" , mensaje.getText().toString()+ " "+ resultadoDia);
                    i.putExtras(b);
                    setResult(RESULT_OK, i);
                    finish();

                }
                if (selected.equals(getString(R.string.tipo_repetitiva))){
                    if (chClicked==false) {
                        b.putString("mensaje", mensaje.getText().toString());
                        b.putString("fecha", "");
                        b.putInt("tipo", contadorTipo+1);
                        b.putString("hora_inicial", "");
                        b.putString("hora_duracion", duracion_spiner);
                        b.putString("frecuencia", freq_spiner);
                        i.putExtras(b);
                        Log.d("Hola" , mensaje + " "+frecuencia);
                        setResult(RESULT_OK, i);
                        finish();
                    }else{
                        b.putString("mensaje", mensaje.getText().toString());
                        b.putString("fecha", "");
                        b.putInt("tipo", contadorTipo+1);
                        b.putString("hora_inicial", h_inicial);
                        b.putString("hora_duracion", duracion_spiner);
                        b.putString("frecuencia", freq_spiner);
                        i.putExtras(b);
                        setResult(RESULT_OK, i);
                        finish();
                    }
                }
                if (selected.equals(getString(R.string.tipo_persistente))){
                    if (chClicked==false) {
                        b.putString("mensaje", mensaje.getText().toString());
                        b.putString("fecha", "");
                        b.putInt("tipo", contadorTipo+1);
                        b.putString("hora_inicial", "");
                        b.putString("hora_duracion", duracion_spiner);
                        b.putString("frecuencia", "");
                        i.putExtras(b);
                        setResult(RESULT_OK, i);
                        finish();
                    }else{
                        b.putString("mensaje", mensaje.getText().toString());
                        b.putString("fecha", "");
                        b.putInt("tipo", contadorTipo+1);
                        b.putString("hora_inicial", h_inicial);
                        b.putString("hora_duracion", duracion_spiner);
                        b.putString("frecuencia", "");
                        i.putExtras(b);

                        setResult(RESULT_OK, i);
                        finish();
                    }
                }
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

    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
        yearFinal=i;
        monthFinal=i1 + 1;
        dayFinal=i2;
        GregorianCalendar GregorianCalendar = new GregorianCalendar(yearFinal, monthFinal, dayFinal-1);

        int dayOfWeek=GregorianCalendar.get(GregorianCalendar.DAY_OF_WEEK);

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
    public void onTimeSet(TimePicker timePicker, int i, int i1) {
        hourFinal = i;
        minuteFinal = i1;

        String resultadoHora=String.valueOf(hourFinal);
        String resultadoMinutos =String.valueOf(minuteFinal);

        if (hourFinal<10){
            resultadoHora="0"+String.valueOf(hourFinal);

        }
        if (minuteFinal<10) {
            resultadoMinutos = "0" + String.valueOf(minuteFinal);
        }
        resultadoH =(resultadoHora + " : : " + resultadoMinutos);
        resultado = (String.valueOf(hourFinal)+ "::" + String.valueOf(minuteFinal));
        btnHora.setText("     Hora:  "+resultadoH);
    }
}



