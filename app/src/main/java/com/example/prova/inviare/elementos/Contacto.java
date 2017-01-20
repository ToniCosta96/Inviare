package com.example.prova.inviare.elementos;

/**
 * Created by user on 24/12/2016.
 */

public class Contacto {
    private int id;
    private String titulo;
    private String subtitulo;
    private String infoExtra;

    public Contacto(int id, String titulo, String subtitulo, String infoExtra){
        this.id=id;
        this.titulo=titulo;
        this.subtitulo=subtitulo;
        this.infoExtra=infoExtra;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getInfoExtra() {
        return infoExtra;
    }

    public void setInfoExtra(String infoExtra) {
        this.infoExtra = infoExtra;
    }
}
