package com.example.prova.inviare.elementos;

public class Contacto {
    private int id;
    private String titulo;
    private String subtitulo;
    private String infoExtra;
    private String imagen;

    public Contacto(int id, String titulo, String subtitulo, String infoExtra, String imagen){
        this.id=id;
        this.titulo=titulo;
        this.subtitulo=subtitulo;
        this.infoExtra=infoExtra;
        this.imagen=imagen;
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

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }
}
