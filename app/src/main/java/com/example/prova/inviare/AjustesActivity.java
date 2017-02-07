package com.example.prova.inviare;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;

import com.example.prova.inviare.db_adapters.DBAdapter;

public class AjustesActivity extends AppCompatActivity implements View.OnClickListener{
    private View ajuste2;
    private Switch switchSonido;
    private Switch switchHorarioNocturno;
    private Switch switchAnclarChat;
    private Switch switchLedNotificacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajustes);

        if(getSupportActionBar()!=null)getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final View ajuste1= findViewById(R.id.layout_ajuste_1);
        ajuste2= findViewById(R.id.layout_ajuste_2);
        final View ajuste3= findViewById(R.id.layout_ajuste_3);
        final View ajuste4= findViewById(R.id.layout_ajuste_4);
        final View ajuste5= findViewById(R.id.layout_ajuste_5);
        final View ajuste6= findViewById(R.id.layout_ajuste_6);
        switchSonido= (Switch) findViewById(R.id.switch_sonido);
        switchHorarioNocturno= (Switch) findViewById(R.id.switch_sonido_noche);
        switchAnclarChat= (Switch) findViewById(R.id.switch_anclar_chat);
        switchLedNotificacion= (Switch) findViewById(R.id.switch_led_notificacion);

        ajuste1.setOnClickListener(this);
        ajuste2.setOnClickListener(this); //Se le puede cambiar la opacidad
        ajuste3.setOnClickListener(this);
        ajuste4.setOnClickListener(this);
        ajuste5.setOnClickListener(this);
        ajuste6.setOnClickListener(this);

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
                Intent returnIntent = new Intent();
                returnIntent.putExtra(context.getResources().getString(R.string.preferences_anclar_chat_personal),anclarChat);
                setResult(AppCompatActivity.RESULT_OK,returnIntent);
                break;
            case R.id.layout_ajuste_5:
                //Ajuste_5 -> Contactos/Permisos

                break;
            case R.id.layout_ajuste_6:
                //Ajuste_6 -> General/Eliminar datos de la aplicación
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(context.getResources().getString(R.string.dialog_eliminar_DB_titulo))
                        .setMessage(context.getResources().getString(R.string.dialog_eliminar_DB))
                        .setPositiveButton(context.getResources().getString(R.string.dialog_de_acuerdo), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Eliminar base de datos
                                DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                                dbAdapter.open();
                                dbAdapter.eliminarDB();
                            }
                        })
                        .setNegativeButton(context.getResources().getString(R.string.dialog_no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                            }
                        });
                // Create the AlertDialog object
                builder.create().show();
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
