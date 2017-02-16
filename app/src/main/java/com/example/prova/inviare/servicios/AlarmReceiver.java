package com.example.prova.inviare.servicios;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.prova.inviare.R;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * BroadcastReceiver de la alarma
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Crea una notificación para terminar la notificación persistente
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_access_alarm_24dp)
                        .setContentTitle(context.getString(R.string.tipo_alarma_persistente))
                        .setContentText("Ya está");
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(0, mBuilder.build());
    }
}
