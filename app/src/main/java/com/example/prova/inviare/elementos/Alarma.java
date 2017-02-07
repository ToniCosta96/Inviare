package com.example.prova.inviare.elementos;

public class Alarma {
    private String mensaje;
    private String fecha;
    private String tipo;
    private String hora_inicio;
    private String hora_duracion;
    private String dia;
    private String frecuencia;
    private boolean propietario;

    public Alarma(String mensaje, String fecha, String tipo, String hora_inicio, String hora_duracion, String dia, String frecuencia, boolean propietario) {
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.tipo = tipo;
        this.hora_inicio = hora_inicio;
        this.hora_duracion = hora_duracion;
        this.dia = dia;
        this.frecuencia = frecuencia;
        this.propietario = propietario;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getHora_inicio() {
        return hora_inicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.hora_inicio = hora_inicio;
    }

    public String getHora_duracion() {
        return hora_duracion;
    }

    public void setHora_duracion(String hora_duracion) {
        this.hora_duracion = hora_duracion;
    }

    public String getDia() {
        return dia;
    }

    public void setDia(String dia) {
        this.dia = dia;
    }

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public boolean isPropietario() {
        return propietario;
    }

    public void setPropietario(boolean propietario) {
        this.propietario = propietario;
    }
}
