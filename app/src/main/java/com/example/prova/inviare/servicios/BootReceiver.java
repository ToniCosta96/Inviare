package com.example.prova.inviare.servicios;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.prova.inviare.AlarmasActivity;
import com.example.prova.inviare.R;

/**
 * BroadCastReceiver para iniciar el servicio después de haber vuelto a encender el dispositivo
 */

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Intent pushIntent = new Intent(context, ServicioAlarmas.class);
            pushIntent.setAction(context.getString(R.string.servicio_empezar));
            context.startService(pushIntent);
        }
    }
}
