package com.example.prova.inviare.servicios;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prova.inviare.MainActivity;
import com.example.prova.inviare.R;

public class AlarmaFijaActivity extends AppCompatActivity {
    private Ringtone ringtone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma_fija);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        final TextView textViewPropietarioAlarma = (TextView) findViewById(R.id.textView_propietario_alarma);
        final TextView textViewMensaje = (TextView) findViewById(R.id.textView_mensaje);
        final Button btnDescartar = (Button) findViewById(R.id.button_descartar);
        final TextView textViewFechaAlarma = (TextView) findViewById(R.id.textView_fecha_alarma);

        Bundle b = getIntent().getExtras();
        textViewPropietarioAlarma.setText(b.getString(getString(R.string.intent_conversacion_titulo)));
        textViewMensaje.setText(b.getString(getString(R.string.intent_alarma_mensaje)));
        textViewFechaAlarma.setText(b.getString(getString(R.string.intent_alarma_hora_duracion)));

        btnDescartar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Se detiene la alarma
                if(ringtone!=null) if(ringtone.isPlaying()) ringtone.stop();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                Toast.makeText(getApplicationContext(),"Alarma detenida",Toast.LENGTH_SHORT).show();
            }
        });

        // Hacer sonar la alarma
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
           ringtone = RingtoneManager.getRingtone(getApplicationContext(), notification);
            ringtone.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
