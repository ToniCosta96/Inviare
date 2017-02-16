package com.example.prova.inviare.elementos;

import java.io.Serializable;
import java.util.Objects;

public class Alarma implements Serializable{
    private int id;
    private String mensaje;
    private String fecha;
    private int tipo;
    private String hora_inicio;
    private String hora_duracion;
    private String frecuencia;
    private String cursoTarea;
    private String contestacion;
    private boolean propietario;

    public static final String TAREA_EN_CURSO="0";
    public static final String TAREA_RECHAZADA="1";
    public static final String TAREA_REALIZADA="2";

    public Alarma(int id, String mensaje, String fecha, int tipo, String hora_inicio, String hora_duracion, String frecuencia, String cursoTarea, String contestacion, boolean propietario) {
        this.id = id;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.tipo = tipo;
        this.hora_inicio = hora_inicio;
        this.hora_duracion = hora_duracion;
        this.frecuencia = frecuencia;
        this.cursoTarea = cursoTarea;
        this.contestacion = contestacion;
        this.propietario = propietario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
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

    public String getFrecuencia() {
        return frecuencia;
    }

    public void setFrecuencia(String frecuencia) {
        this.frecuencia = frecuencia;
    }

    public String getCursoTarea() {
        return cursoTarea;
    }

    public void setCursoTarea(String cursoTarea) {
        this.cursoTarea = cursoTarea;
    }

    public String getContestacion() {
        return contestacion;
    }

    public void setContestacion(String contestacion) {
        this.contestacion = contestacion;
    }

    public boolean isPropietario() {
        return propietario;
    }

    public void setPropietario(boolean propietario) {
        this.propietario = propietario;
    }

    /*@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Alarma alarma = (Alarma) o;
        return tipo == alarma.tipo &&
                propietario == alarma.propietario &&
                Objects.equals(mensaje, alarma.mensaje) &&
                Objects.equals(fecha, alarma.fecha) &&
                Objects.equals(hora_inicio, alarma.hora_inicio) &&
                Objects.equals(hora_duracion, alarma.hora_duracion) &&
                Objects.equals(frecuencia, alarma.frecuencia) &&
                Objects.equals(cursoTarea, alarma.cursoTarea) &&
                Objects.equals(contestacion, alarma.contestacion);
    }*/
}
