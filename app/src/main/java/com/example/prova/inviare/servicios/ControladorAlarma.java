package com.example.prova.inviare.servicios;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.prova.inviare.R;
import com.example.prova.inviare.elementos.Alarma;

import java.util.GregorianCalendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Añade la alarma al AlarmManager
 */

public class ControladorAlarma {

    public ControladorAlarma(Activity activity, Alarma alarma){
        // The time at which the alarm will be scheduled. Here the alarm is scheduled for 1 day from the current time.
        // We fetch the current time in milliseconds and add 1 day's time
        // i.e. 24*60*60*1000 = 86,400,000 milliseconds in a day.
        long time = new GregorianCalendar().getTimeInMillis()+5000;

        // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
        // specified AlarmReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
        Intent intentAlarm = new Intent(activity, AlarmReceiver.class);

        // Get the Alarm Service.
        AlarmManager alarmManager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        // Set the alarm for a particular time.
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(activity, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

        //Crea una notificación persistente
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(activity)
                        .setSmallIcon(R.drawable.ic_access_alarm_24dp)
                        .setContentTitle(activity.getString(R.string.tipo_alarma_persistente))
                        .setContentText(alarma.getMensaje())
                .setOngoing(true);
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr = (NotificationManager) activity.getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(0, mBuilder.build());
    }
}
