package com.example.prova.inviare.servicios;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.prova.inviare.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by user on 17/02/2017.
 */

public class AlarmaRepetitivaReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        final String mensaje=intent.getStringExtra(context.getResources().getString(R.string.intent_alarma_mensaje));
        final int codigoAlarma=intent.getIntExtra(context.getResources().getString(R.string.intent_alarma_codigo),0);
        final boolean empezarNotificacion=intent.getBooleanExtra(context.getString(R.string.intent_empezar_notificacion_bool),false);
        //Crea una notificación para terminar la notificación persistente
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_access_alarm_24dp)
                        .setContentTitle(context.getString(R.string.tipo_alarma_repetitiva))
                        .setContentText(mensaje);
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(codigoAlarma, mBuilder.build());
        new ControladorAlarma(context,null).eliminarAlarmasArray(codigoAlarma);
    }
}
