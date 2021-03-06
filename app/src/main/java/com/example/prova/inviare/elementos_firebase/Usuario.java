package com.example.prova.inviare.elementos_firebase;


public class Usuario {

    private String nombre, email, estado, imagen, uid;
    private int telefono;

    public Usuario() {
    }

    public Usuario(String nombre, String email, int telefono, String uid) {
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.uid = uid;
    }

    public Usuario(String nombre, String email, String estado, String imagen, int telefono) {
        this.nombre = nombre;
        this.email = email;
        this.estado = estado;
        this.imagen = imagen;
        this.telefono = telefono;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }
}
