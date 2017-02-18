package com.example.prova.inviare.servicios;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

/**
 * Servicio que gestiona las alarmas
 */

public class ServicioAlarmas extends Service {
    private NotificationManager notificationManager;

    private static Alarma[] arrayAlarmas;
    private HashMap<Alarma,TiemposAlarma> hashMapTiemposAlarma;
    private static Thread thread;
    private Bundle bundle;

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
        hashMapTiemposAlarma = new HashMap<>();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("LocalService", "Received start id " + startId + ": " + intent);
        bundle = intent.getExtras();
        actualizarAlarmas();
        crearThreadAlarmas();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
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
                            logicaServicioAlarmas();
                            // Se espera por 20 segundos
                            Thread.sleep(20000);
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

    private void logicaServicioAlarmas(){
        for (Alarma arrayAlarma : arrayAlarmas) {
            switch (arrayAlarma.getTipo()) {
                case DBAdapter.TIPO_ALARMA_FIJA:
                    SimpleDateFormat df = new SimpleDateFormat(getResources().getString(R.string.simple_date_format_MENSAJE), Locale.getDefault());
                    try {
                        GregorianCalendar gc = new GregorianCalendar();
                        gc.setTime(df.parse(arrayAlarma.getHoraDuracion()));
                        if (gc.getTimeInMillis() < new GregorianCalendar().getTimeInMillis()) {
                            Intent i = new Intent(getApplicationContext(),AlarmaFijaActivity.class);
                            bundle.putString(getString(R.string.intent_alarma_mensaje),arrayAlarma.getMensaje());
                            bundle.putString(getString(R.string.intent_alarma_hora_duracion),arrayAlarma.getHoraDuracion());
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            i.putExtras(bundle);
                            startActivity(i);
                            // Eliminar alarma de la base de datos
                            DBAdapter dbAdapter = new DBAdapter(getApplicationContext());
                            dbAdapter.open();
                            dbAdapter.actualizarMensaje(arrayAlarma.getId(),Alarma.TAREA_REALIZADA,getString(R.string.alarma_finalizada));
                            arrayAlarmas = dbAdapter.seleccionarAlarmas(true);
                            dbAdapter.close();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                case DBAdapter.TIPO_ALARMA_REPETITIVA:
                    if(hashMapTiemposAlarma.get(arrayAlarma)==null){
                        int horaInicio=0;
                        if(arrayAlarma.getHoraInicio()!=null){
                            horaInicio = (int) (Long.parseLong(arrayAlarma.getHoraInicio()) + new GregorianCalendar().getTimeInMillis());
                        }
                        int horaDuracion = (int) (Long.parseLong(arrayAlarma.getHoraDuracion()) + new GregorianCalendar().getTimeInMillis());
                        hashMapTiemposAlarma.put(arrayAlarma, new TiemposAlarma(horaInicio,horaDuracion,0));
                    }
                    if(hashMapTiemposAlarma.get(arrayAlarma).getHoraInicio()!=-1){
                        if(hashMapTiemposAlarma.get(arrayAlarma).getHoraInicio()<new GregorianCalendar().getTimeInMillis()){
                            mostrarNotificationPersistente(arrayAlarma.getMensaje(),arrayAlarma.getId(),true);
                            hashMapTiemposAlarma.get(arrayAlarma).setHoraInicio(-1);
                        }
                    }

                    if(hashMapTiemposAlarma.get(arrayAlarma).getHoraDuracion()<new GregorianCalendar().getTimeInMillis()){
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
                    if(hashMapTiemposAlarma.get(arrayAlarma).getHoraInicio()!=-1){
                        if(hashMapTiemposAlarma.get(arrayAlarma).getHoraInicio()<new GregorianCalendar().getTimeInMillis()){
                            mostrarNotificationPersistente(arrayAlarma.getMensaje(),arrayAlarma.getId(),true);
                            hashMapTiemposAlarma.get(arrayAlarma).setHoraInicio(-1);
                        }
                    }
                    if(hashMapTiemposAlarma.get(arrayAlarma).getHoraDuracion()<new GregorianCalendar().getTimeInMillis()){
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
        for(Alarma alarma : dbAdapter.seleccionarAlarmas(false)){
            notificationManager.cancel(alarma.getId());
        }
        dbAdapter.close();
    }

    private void mostrarNotificationPersistente(final String mensaje,final int codigoAlarma,final boolean empezarNotificacion) {
        //Crea una notificación para terminar la notificación persistente
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_access_alarm_24dp);
        if(empezarNotificacion){
            mBuilder.setContentTitle(getString(R.string.tipo_notificacion_persistente));
            mBuilder.setContentText(mensaje);
            mBuilder.setOngoing(true);
        }else{
            mBuilder.setContentTitle("Tarea fuera de plazo");
            mBuilder.setContentText(getResources().getString(R.string.tipo_notificacion_persistente)+": "+mensaje);
            mBuilder.setPriority(Notification.PRIORITY_DEFAULT);
            mBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
            mBuilder.setLights(Color.GREEN,500,1000);
            long[] pattern = {500,500};
            mBuilder.setVibrate(pattern);
        }
        // Gets an instance of the NotificationManager service
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        notificationManager.notify(codigoAlarma, mBuilder.build());
    }

    private class TiemposAlarma {
        private long horaInicio;
        private long horaDuracion;
        private int frecuenciaActual;

        public TiemposAlarma(long horaInicio, long horaDuracion, int frecuenciaActual) {
            this.frecuenciaActual = frecuenciaActual;
            this.horaInicio = horaInicio;
            this.horaDuracion = horaDuracion;
        }
        public void setHoraInicio(long horaInicio) {
            this.horaInicio = horaInicio;
        }
        public long getHoraInicio() {
            return horaInicio;
        }
        public long getHoraDuracion() {
            return horaDuracion;
        }
        public int getFrecuenciaActual() {
            return frecuenciaActual;
        }
        public void setFrecuenciaActual(int frecuenciaActual) {
            this.frecuenciaActual = frecuenciaActual;
        }
    }
}