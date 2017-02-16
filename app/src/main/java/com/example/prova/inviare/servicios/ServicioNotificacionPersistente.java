package com.example.prova.inviare.servicios;

import android.app.IntentService;
import android.content.Intent;

/**
 * Servicio para notificación persistente
 */

public class ServicioNotificacionPersistente extends IntentService {
    public ServicioNotificacionPersistente() {
        super("ServicioNotificacionPersistente");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }
}
