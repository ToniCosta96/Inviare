package com.example.prova.inviare.servicios;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;

import com.example.prova.inviare.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * BroadcastReceiver de la alarma persistente
 */

public class AlarmaPersistenteReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final String mensaje=intent.getStringExtra(context.getResources().getString(R.string.intent_alarma_mensaje));
        final int codigoAlarma=intent.getIntExtra(context.getResources().getString(R.string.intent_alarma_codigo),0);
        final boolean empezarNotificacion=intent.getBooleanExtra(context.getString(R.string.intent_empezar_notificacion_bool),false);
        //Crea una notificación para terminar la notificación persistente
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_access_alarm_24dp);
        if(empezarNotificacion){
            mBuilder.setContentTitle(context.getString(R.string.tipo_notificacion_persistente));
            mBuilder.setContentText(mensaje);
            mBuilder.setOngoing(true);
        }else{
            mBuilder.setContentTitle("Tarea fuera de plazo");
            mBuilder.setContentText(context.getString(R.string.tipo_notificacion_persistente)+": "+mensaje);
            mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setLights(Color.GREEN,500,1000);
            long[] pattern = {500,500};
            mBuilder.setVibrate(pattern);
        }
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(codigoAlarma, mBuilder.build());
        new ControladorAlarma(context,null).eliminarAlarmasArray(codigoAlarma);
    }
}
