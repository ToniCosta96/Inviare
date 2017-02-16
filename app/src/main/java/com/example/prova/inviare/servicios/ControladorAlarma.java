package com.example.prova.inviare.servicios;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.example.prova.inviare.R;
import com.example.prova.inviare.db_adapters.DBAdapter;
import com.example.prova.inviare.elementos.Alarma;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Añade o quita la alarma del AlarmManager
 */

public class ControladorAlarma {
    private static final String FILENAME="listaAlarmas.data";
    private Context context;
    private Alarma alarma;
    private ArrayList<Alarma> arrayListAlarmas;

    public ControladorAlarma(Context context,Alarma alarma){
        this.context = context;
        this.alarma = alarma;
        arrayListAlarmas=null;
        cargarAlarmas();
        if(arrayListAlarmas==null){
            arrayListAlarmas = new ArrayList<>();
        }

    }

    public void ponerAlarma(){
        final int codigoAlarma=alarma.getId();

        arrayListAlarmas.add(alarma);
        switch(alarma.getTipo()){
            case DBAdapter.TIPO_ALARMA_PERSISTENTE:
                // Fecha en la que sonará la alarma
                long time = new GregorianCalendar().getTimeInMillis()+5000;


                // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
                // specified AlarmReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
                Intent intentAlarm = new Intent(context, AlarmReceiver.class);
                intentAlarm.putExtra(context.getResources().getString(R.string.intent_alarma_mensaje),alarma.getMensaje());
                intentAlarm.putExtra(context.getResources().getString(R.string.intent_alarma_codigo),codigoAlarma);

                // Get the Alarm Service.
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                // Set the alarm for a particular time.
                alarmManager.set(AlarmManager.RTC_WAKEUP, time, PendingIntent.getBroadcast(context, codigoAlarma, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));

                //Crea una notificación persistente
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(context)
                                .setSmallIcon(R.drawable.ic_access_alarm_24dp)
                                .setContentTitle(context.getString(R.string.tipo_alarma_persistente))
                                .setContentText(alarma.getMensaje())
                                .setOngoing(true);
                // Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                // Builds the notification and issues it.
                mNotifyMgr.notify(codigoAlarma, mBuilder.build());
            break;
            case DBAdapter.TIPO_ALARMA_REPETITIVA:
                break;
            case DBAdapter.TIPO_ALARMA_FIJA:
                break;
        }
    }

    public void detenerAlarma(){

    }

    public void guardarAlarmasPuestas(){
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(arrayListAlarmas);
            oos.close();
            fos.close();
        } catch ( Exception ex ) {
            ex.printStackTrace ();
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarAlarmas(){
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object object=is.readObject();
            if(object instanceof ArrayList){
                arrayListAlarmas = (ArrayList<Alarma>) object;
            }
            is.close();
            fis.close();
        } catch ( Exception ex ) {
            ex.printStackTrace ();
        }
    }

    public void eliminarAlarmasArray(int codigoAlarma){
        for(int i=0;i<arrayListAlarmas.size();i++){
            if(arrayListAlarmas.get(i).getId()==codigoAlarma){
                arrayListAlarmas.remove(i);
            }
        }
    }
}
