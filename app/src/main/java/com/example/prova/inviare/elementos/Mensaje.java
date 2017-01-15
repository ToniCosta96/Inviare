package com.example.prova.inviare.elementos;

/**
 * Created by user on 15/01/2017.
 */

public class Mensaje {
    private String mensaje;
    private String hora;
    private boolean propietario;

    public Mensaje(String mensaje, String hora, boolean propietario) {
        this.mensaje=mensaje;
        this.hora=hora;
        this.propietario=propietario;

    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public boolean isPropietario() {
        return propietario;
    }

    public void setPropietario(boolean propietario) {
        this.propietario = propietario;
    }
}
