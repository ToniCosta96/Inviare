package com.example.prova.inviare.elementos;

import android.content.Context;

import com.example.prova.inviare.R;

import java.io.Serializable;

public class Alarma implements Serializable{
    private int id;
    private String mensaje;
    private String fecha;
    private int tipo;
    private String horaInicio;
    private String horaDuracion;
    private String frecuencia;
    private String cursoTarea;
    private String contestacion;
    private boolean propietario;

    public static final String TAREA_EN_CURSO="0";
    public static final String TAREA_RECHAZADA="1";
    public static final String TAREA_REALIZADA="2";

    public Alarma(int id, String mensaje, String fecha, int tipo, String horaInicio, String horaDuracion, String frecuencia, String cursoTarea, String contestacion, boolean propietario) {
        this.id = id;
        this.mensaje = mensaje;
        this.fecha = fecha;
        this.tipo = tipo;
        this.horaInicio = horaInicio;
        this.horaDuracion = horaDuracion;
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
        return horaInicio;
    }

    public void setHora_inicio(String hora_inicio) {
        this.horaInicio = hora_inicio;
    }

    /**
     * Devuelve la horaInicio en una forma presentable para la interfaz o null si se produce algún error
     * @param c Context necesario para el método
     * @return String - Texto para mostrar en la interfaz
     */
    public String getHora_inicioFormateada(Context c){
        String resultado=null;
        if(horaInicio!=null) {
            final String[] tiempoInicioSpinnerMilis = c.getResources().getStringArray(R.array.h_duracion_inicio_milisegundos);
            final String[] tiempoInicioSpinnerText = c.getResources().getStringArray(R.array.h_duracion_inicio);

            for (int i = 0; i < tiempoInicioSpinnerMilis.length; i++) {
                if (tiempoInicioSpinnerMilis[i].compareTo(horaInicio) == 0)
                    resultado = tiempoInicioSpinnerText[i];
            }
        }
        return resultado;
    }

    public String getHora_duracion() {
        return horaDuracion;
    }

    public void setHora_duracion(String hora_duracion) {
        this.horaDuracion = hora_duracion;
    }

    /**
     * Devuelve la horaDuracion en una forma presentable para la interfaz o null si se produce algún error
     * @param c Context necesario para el método
     * @return String - Texto para mostrar en la interfaz
     */
    public String getHora_duracionFormateada(Context c){
        String resultado=null;
        if(horaDuracion!=null) {
            final String[] tiempoInicioSpinnerMilis = c.getResources().getStringArray(R.array.frecuencia_milisegundos);
            final String[] tiempoInicioSpinnerText = c.getResources().getStringArray(R.array.frecuencia);

            for (int i = 0; i < tiempoInicioSpinnerMilis.length; i++) {
                if (tiempoInicioSpinnerMilis[i].compareTo(horaDuracion) == 0)
                    resultado = tiempoInicioSpinnerText[i];
            }
        }
        return resultado;
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
}
