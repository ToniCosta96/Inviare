package com.example.prova.inviare.servicios;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.prova.inviare.R;

import java.util.GregorianCalendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * BroadcastReceiver de la alarma repetitiva
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
                        .setContentText(mensaje)
                        .setLights(Color.GREEN,500,1000)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(codigoAlarma, mBuilder.build());

        intent.getIntExtra(context.getResources().getString(R.string.intent_alarma_codigo),-1);
        if(intent.getLongExtra(context.getResources().getString(R.string.intent_alarma_duracion_milisegundos),-1)<=new GregorianCalendar().getTimeInMillis()){
            ControladorAlarma controladorAlarma = new ControladorAlarma(context,null);
            controladorAlarma.eliminarAlarmasArray(codigoAlarma);
            controladorAlarma.detenerAlarma(codigoAlarma,getClass());
        }
    }
}
