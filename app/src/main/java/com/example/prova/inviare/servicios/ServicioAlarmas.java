package com.example.prova.inviare.servicios;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.example.prova.inviare.AlarmasActivity;
import com.example.prova.inviare.ConversacionActivity;
import com.example.prova.inviare.R;
import com.example.prova.inviare.db_adapters.DBAdapter;
import com.example.prova.inviare.elementos.Alarma;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servicio que gestiona las alarmas
 */

public class ServicioAlarmas extends Service {
    private static final String FILENAME="hashMapAlarmasActivas.data";
    private NotificationManager notificationManager;

    private static Alarma[] arrayAlarmas;
    private HashMap<Alarma,TiemposAlarma> hashMapTiemposAlarma;
    private static Thread thread;

    // Tiempo de espera del Thread
    private static final int TIEMPO_ESPERA = 5000;

    /**
     * Class for clients to access.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with
     * IPC.
     */
    public class LocalBinder extends Binder {
        public ServicioAlarmas getService() {
            return ServicioAlarmas.this;
        }
    }

    @Override
    public void onCreate() {
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(new File(FILENAME).exists()){
            cargarHashMap();
        }else{
            hashMapTiemposAlarma = new HashMap<>();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "Received start id " + startId + ": " + intent);
        // Se cancela la notificación si ya se había iniciado
        if(intent!=null) notificationManager.cancel(intent.getIntExtra(getString(R.string.intent_alarma_codigo),-1));
        actualizarAlarmas();
        crearThreadAlarmas();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        final File file = new File(FILENAME);
        if(hashMapTiemposAlarma==null){
            if(file.exists()) if(!file.delete()) Toast.makeText(getApplicationContext(),"Error al eliminar archivo \"hashMapAlarmasActivas.data\"",Toast.LENGTH_LONG);
        }else{
            guardarHashMap();
        }
        if(hashMapTiemposAlarma!=null) guardarHashMap();
        // Tell the user we stopped.
        Toast.makeText(this, R.string.local_service_stopped, Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new LocalBinder();

    private void crearThreadAlarmas(){
        if(thread==null){
            thread = new Thread(new Runnable() {
                public void run() {

                    while (arrayAlarmas.length > 0) {
                        try {
                            logicaServicioAlarmas(hashMapTiemposAlarma);
                            // Se espera por 20 segundos
                            Thread.sleep(TIEMPO_ESPERA);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    thread=null;
                }
            });
            thread.setName("ThreadServicioAlarmas");
            thread.setPriority(3);
            thread.start();
        }
    }

    private void logicaServicioAlarmas(HashMap<Alarma,TiemposAlarma> hashMapTiemposAlarma){
        for (final Alarma arrayAlarma : arrayAlarmas) {
            switch (arrayAlarma.getTipo()) {
                case DBAdapter.TIPO_ALARMA_FIJA:
                    final SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_MENSAJE), Locale.getDefault());
                    try {
                        GregorianCalendar gc = new GregorianCalendar();
                        gc.setTime(df.parse(arrayAlarma.getHoraDuracion()));
                        if (gc.getTimeInMillis() < new GregorianCalendar().getTimeInMillis()) {
                            DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                            dbAdapter.open();

                            Intent i = new Intent(getApplicationContext(),AlarmaFijaActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString(getString(R.string.intent_conversacion_titulo),dbAdapter.seleccionarNombreContactoDeMensaje(arrayAlarma.getId()));
                            bundle.putString(getString(R.string.intent_alarma_mensaje),arrayAlarma.getMensaje());
                            bundle.putString(getString(R.string.intent_alarma_hora_duracion),arrayAlarma.getHoraDuracion());
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtras(bundle);
                            startActivity(i);
                            // Eliminar alarma de la base de datos
                            dbAdapter.actualizarMensaje(arrayAlarma.getId(),Alarma.TAREA_REALIZADA,getString(R.string.alarma_finalizada));
                            arrayAlarmas = dbAdapter.seleccionarAlarmas(true);

                            dbAdapter.close();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case DBAdapter.TIPO_ALARMA_REPETITIVA:
                    final int frecuencia = Integer.parseInt(arrayAlarma.getFrecuencia());
                    if(hashMapTiemposAlarma.get(arrayAlarma)==null){
                        long horaInicio=0;
                        if(arrayAlarma.getHoraInicio()!=null){
                            horaInicio = Long.parseLong(arrayAlarma.getHoraInicio()) + new GregorianCalendar().getTimeInMillis();
                        }
                        long horaDuracion = Long.parseLong(arrayAlarma.getHoraDuracion()) + new GregorianCalendar().getTimeInMillis();
                        hashMapTiemposAlarma.put(arrayAlarma, new TiemposAlarma(horaInicio,horaDuracion,frecuencia+TIEMPO_ESPERA));
                    }
                    if(hashMapTiemposAlarma.get(arrayAlarma).horaInicio<new GregorianCalendar().getTimeInMillis()){
                        if(hashMapTiemposAlarma.get(arrayAlarma).frecuenciaActual>frecuencia){
                            mostrarNotificationRepetitiva(arrayAlarma.getMensaje(),arrayAlarma.getId());
                            hashMapTiemposAlarma.get(arrayAlarma).frecuenciaActual = TIEMPO_ESPERA;
                        }
                        if(hashMapTiemposAlarma.get(arrayAlarma)!=null){
                            hashMapTiemposAlarma.get(arrayAlarma).frecuenciaActual = hashMapTiemposAlarma.get(arrayAlarma).frecuenciaActual+TIEMPO_ESPERA;
                        }
                    }

                    if(hashMapTiemposAlarma.get(arrayAlarma).horaDuracion<new GregorianCalendar().getTimeInMillis()){
                        // Eliminar alarma de la base de datos
                        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                        dbAdapter.open();
                        dbAdapter.actualizarMensaje(arrayAlarma.getId(),Alarma.TAREA_RECHAZADA,"Tarea fuera de plazo");
                        arrayAlarmas = dbAdapter.seleccionarAlarmas(true);
                        dbAdapter.close();
                        mostrarNotificationPersistente(arrayAlarma.getMensaje(),arrayAlarma.getId(),false);
                        hashMapTiemposAlarma.remove(arrayAlarma);
                    }
                    break;
                case DBAdapter.TIPO_ALARMA_PERSISTENTE:
                    if(hashMapTiemposAlarma.get(arrayAlarma)==null){
                        long horaInicio=0;
                        if(arrayAlarma.getHoraInicio()!=null){
                            horaInicio = Long.parseLong(arrayAlarma.getHoraInicio()) + new GregorianCalendar().getTimeInMillis();
                        }
                        long horaDuracion = Long.parseLong(arrayAlarma.getHoraDuracion()) + new GregorianCalendar().getTimeInMillis();
                        hashMapTiemposAlarma.put(arrayAlarma, new TiemposAlarma(horaInicio,horaDuracion,0));
                    }
                    if(hashMapTiemposAlarma.get(arrayAlarma).horaInicio!=-1){
                        if(hashMapTiemposAlarma.get(arrayAlarma).horaInicio<new GregorianCalendar().getTimeInMillis()){
                            mostrarNotificationPersistente(arrayAlarma.getMensaje(),arrayAlarma.getId(),true);
                            hashMapTiemposAlarma.get(arrayAlarma).horaInicio = -1;
                        }
                    }
                    if(hashMapTiemposAlarma.get(arrayAlarma).horaDuracion<new GregorianCalendar().getTimeInMillis()){
                        // Eliminar alarma de la base de datos
                        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                        dbAdapter.open();
                        dbAdapter.actualizarMensaje(arrayAlarma.getId(),Alarma.TAREA_RECHAZADA,"Tarea fuera de plazo");
                        arrayAlarmas = dbAdapter.seleccionarAlarmas(true);
                        dbAdapter.close();
                        mostrarNotificationPersistente(arrayAlarma.getMensaje(),arrayAlarma.getId(),false);
                        hashMapTiemposAlarma.remove(arrayAlarma);
                    }
                    break;
            }
        }
    }

    public void actualizarAlarmas(){
        DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
        dbAdapter.open();
        arrayAlarmas = dbAdapter.seleccionarAlarmas(true);
        /*for(Alarma alarma : dbAdapter.seleccionarAlarmas(false)){
            notificationManager.cancel(alarma.getId());
        }*/
        dbAdapter.close();
    }

    private void mostrarNotificationPersistente(final String mensaje,final int codigoAlarma,final boolean empezarNotificacion) {
        //Crea una notificación para terminar la notificación persistente
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_access_alarm_24dp)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        if(empezarNotificacion){
            mBuilder.setContentTitle(getString(R.string.tipo_notificacion_persistente));
            mBuilder.setContentText(mensaje);
            mBuilder.setOngoing(true);
        }else{
            mBuilder.setContentTitle("Tarea fuera de plazo");
            mBuilder.setContentText(getResources().getString(R.string.tipo_notificacion_persistente)+": "+mensaje);
            mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
            mBuilder.setLights(Color.GREEN,500,1000);
            long[] pattern = {500,500};
            mBuilder.setVibrate(pattern);
        }
        // Builds the notification and issues it.
        notificationManager.notify(codigoAlarma, mBuilder.build());
    }

    private void mostrarNotificationRepetitiva(final String mensaje,final int codigoAlarma) {
        //Crea una notificación para terminar la notificación persistente
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_access_alarm_24dp);
            mBuilder.setContentTitle(getString(R.string.tipo_alarma_repetitiva));
            mBuilder.setContentText(mensaje);
            mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setLights(Color.GREEN,500,1000);
            long[] pattern = {500,500};
            mBuilder.setVibrate(pattern);
        // Builds the notification and issues it.
        notificationManager.notify(codigoAlarma, mBuilder.build());
    }

    private class TiemposAlarma implements Serializable {
        long horaInicio;
        long horaDuracion;
        int frecuenciaActual;

        TiemposAlarma(long horaInicio, long horaDuracion, int frecuenciaActual) {
            this.frecuenciaActual = frecuenciaActual;
            this.horaInicio = horaInicio;
            this.horaDuracion = horaDuracion;
        }
    }

    private void guardarHashMap(){
        try {
            FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(hashMapTiemposAlarma);
            oos.close();
            fos.close();
        } catch ( Exception ex ) {
            ex.printStackTrace ();
        }
    }

    @SuppressWarnings("unchecked")
    private void cargarHashMap(){
        try {
            FileInputStream fis = openFileInput(FILENAME);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object object=is.readObject();
            if(object instanceof HashMap){
                hashMapTiemposAlarma = (HashMap<Alarma,TiemposAlarma>) object;
            }
            is.close();
            fis.close();
        } catch ( Exception ex ) {
            ex.printStackTrace ();
        }
    }
}