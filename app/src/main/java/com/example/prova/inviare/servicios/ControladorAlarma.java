package com.example.prova.inviare.servicios;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import com.example.prova.inviare.ConversacionActivity;
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
    private static final int NOTIFICACION_REQUEST=1;
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
        final String mensaje = alarma.getMensaje();
        final int codigoAlarma = alarma.getId();

        arrayListAlarmas.add(alarma);

        final String EMPEZAR_NOTIFICACION = context.getString(R.string.intent_empezar_notificacion_bool);
        // Fecha límite hasta la qual sonará la alarma
        long timeDuracionAlarma = new GregorianCalendar().getTimeInMillis()+Long.valueOf(alarma.getHora_duracion());

        switch(alarma.getTipo()){
            case DBAdapter.TIPO_ALARMA_PERSISTENTE:
                // Create an Intent and set the class that will execute when the Alarm triggers. Here we have
                // specified AlarmaPersistenteReceiver in the Intent. The onReceive() method of this class will execute when the broadcast from your alarm is received.
                Intent intentAlarmaP = new Intent(context, AlarmaPersistenteReceiver.class);
                intentAlarmaP.putExtra(context.getResources().getString(R.string.intent_alarma_mensaje),mensaje);
                intentAlarmaP.putExtra(context.getResources().getString(R.string.intent_alarma_codigo),codigoAlarma);

                // Get the Alarm Service.
                AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                //Si no se ha indicado tiempo de inicio crea una notificación persistente
                if(alarma.getHora_inicio()==null) {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_access_alarm_24dp)
                                    .setContentTitle(context.getString(R.string.tipo_notificacion_persistente))
                                    .setContentText(mensaje)
                                    .setOngoing(true);

                    // Pending intent para que la notificación abra ConversacionActivity.class
                    Intent resultIntent = new Intent(context, ConversacionActivity.class);
                    Intent intentProvisional = ((Activity)context).getIntent();
                    Bundle bundle = intentProvisional.getExtras();
                    bundle.putInt(context.getString(R.string.intent_alarma_codigo),intentProvisional.getIntExtra(context.getString(R.string.intent_alarma_codigo),0));
                    resultIntent.putExtras(bundle);
                    //resultIntent.putExtra();
                    PendingIntent resultPendingIntent = PendingIntent.getActivity(context, NOTIFICACION_REQUEST, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    mBuilder.setContentIntent(resultPendingIntent);

                    // Coje una instancia del servicio de NotificationManager
                    NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
                    // Builds the notification and issues it.
                    mNotifyMgr.notify(codigoAlarma, mBuilder.build());

                    //Intent (intentAlarm)
                    intentAlarmaP.putExtra(EMPEZAR_NOTIFICACION,false);
                }else{
                    Long timeInicioAlarma= new GregorianCalendar().getTimeInMillis()+Long.valueOf(alarma.getHora_inicio());
                    intentAlarmaP.putExtra(EMPEZAR_NOTIFICACION,true);
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeInicioAlarma, PendingIntent.getBroadcast(context, codigoAlarma*-1, intentAlarmaP, PendingIntent.FLAG_UPDATE_CURRENT));
                }

                // ALARMA - Set the alarm for a particular time.
                intentAlarmaP.putExtra(EMPEZAR_NOTIFICACION,false);
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeDuracionAlarma, PendingIntent.getBroadcast(context, codigoAlarma, intentAlarmaP, PendingIntent.FLAG_UPDATE_CURRENT));

            break;
            case DBAdapter.TIPO_ALARMA_REPETITIVA:
                Intent intentAlarmaR = new Intent(context, AlarmaPersistenteReceiver.class);
                intentAlarmaR.putExtra(context.getResources().getString(R.string.intent_alarma_mensaje),mensaje);
                intentAlarmaR.putExtra(context.getResources().getString(R.string.intent_alarma_codigo),codigoAlarma);
                AlarmManager alarmManager2 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                alarmManager2.setRepeating(AlarmManager.RTC_WAKEUP, timeDuracionAlarma, 60000,PendingIntent.getBroadcast(context, codigoAlarma, intentAlarmaR, PendingIntent.FLAG_UPDATE_CURRENT));

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
