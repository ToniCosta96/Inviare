package com.example.prova.inviare;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

public class AjustesActivity extends AppCompatActivity implements View.OnClickListener{
    View ajuste2;
    private Switch switchSonido;
    private Switch switchHorarioNocturno;
    private Switch switchAnclarChat;
    private Switch switchLedNotificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View ajuste1= (View) findViewById(R.id.layout_ajuste_1);
        ajuste2= (View) findViewById(R.id.layout_ajuste_2);
        final View ajuste3= (View) findViewById(R.id.layout_ajuste_3);
        final View ajuste4= (View) findViewById(R.id.layout_ajuste_4);
        switchSonido= (Switch) findViewById(R.id.switch_sonido);
        switchHorarioNocturno= (Switch) findViewById(R.id.switch_sonido_noche);
        switchAnclarChat= (Switch) findViewById(R.id.switch_anclar_chat);
        switchLedNotificacion= (Switch) findViewById(R.id.switch_led_notificacion);

        ajuste1.setOnClickListener(this);
        ajuste2.setOnClickListener(this);
        ajuste3.setOnClickListener(this);
        ajuste4.setOnClickListener(this);

        //Se carga el SharedPreferences
        SharedPreferences sharedPref = this.getSharedPreferences(getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        switchSonido.setChecked(sharedPref.getBoolean(getResources().getString(R.string.preferences_sonido_notificacion),true));
        switchHorarioNocturno.setChecked(sharedPref.getBoolean(getResources().getString(R.string.preferences_horario_nocturno),false));
        if(sharedPref.getInt(getResources().getString(R.string.preferences_anclar_chat_personal),-1)!=-1){
            switchAnclarChat.setChecked(false);
        }
        switchLedNotificacion.setChecked(sharedPref.getBoolean(getResources().getString(R.string.preferences_led_notificacion),true));
    }

    @Override
    public void onClick(View view) {
        //Se guardan los cambios en el SharedPreference
        Context context = getApplicationContext();
        SharedPreferences sharedPref = context.getSharedPreferences(context.getString(R.string.shared_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        switch (view.getId()) {
            case R.id.layout_ajuste_1:
                //Ajuste_1 -> Notificación/Sonido
                if(switchSonido.isChecked()){
                    switchSonido.setChecked(false);
                    //switchHorarioNocturno.setChecked(false);
                    ajuste2.setAlpha(0.5f);
                }else{
                    switchSonido.setChecked(true);
                    ajuste2.setAlpha(1);
                }
                editor.putBoolean(context.getString(R.string.preferences_sonido_notificacion),switchSonido.isChecked());
                break;
            case R.id.layout_ajuste_2:
                //Ajuste_2 -> Notificación/Horario nocturno
                if(switchSonido.isChecked()) switchHorarioNocturno.setChecked(!switchHorarioNocturno.isChecked());
                editor.putBoolean(context.getString(R.string.preferences_horario_nocturno),switchHorarioNocturno.isChecked());
                break;
            case R.id.layout_ajuste_3:
                //Ajuste_2 -> Notificación/Led de notificación
                switchLedNotificacion.setChecked(!switchLedNotificacion.isChecked());
                editor.putBoolean(context.getString(R.string.preferences_led_notificacion),switchLedNotificacion.isChecked());
                break;
            case R.id.layout_ajuste_4:
                //Ajuste_4 -> Contactos/Anclar chat personal
                switchAnclarChat.setChecked(!switchAnclarChat.isChecked());
                int anclarChat=0;
                if (switchAnclarChat.isChecked()) anclarChat=-1;
                editor.putInt(context.getString(R.string.preferences_anclar_chat_personal),anclarChat);
                break;
            default:
                break;
        }
        //Guardar las preferencias
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Tornar al activity anterior.
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
