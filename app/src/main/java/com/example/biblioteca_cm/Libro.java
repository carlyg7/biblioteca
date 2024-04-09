package com.example.biblioteca_cm;

import android.media.Image;

public class Libro {

    private String autor;
    private Boolean disponible;
    private String editorial;
    private String sinopsis;
    private String titulo;
    private String portada;

    private String isbn;

    public Libro() {
    }

    public Libro(String autor, Boolean disponible, String editorial, String sinopsis, String titulo, String isbn, String portada) {
        this.autor = autor;
        this.disponible = disponible;
        this.editorial = editorial;
        this.sinopsis = sinopsis;
        this.titulo = titulo;
        this.isbn = isbn;
        this.portada = portada;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }

    public String getEditorial() {
        return editorial;
    }

    public String getPortada() {
        return portada;
    }

    public void setPortada(String portada) {
        this.portada = portada;
    }

    public void setEditorial(String editorial) {
        this.editorial = editorial;
    }

    public String getSinopsis() {
        return sinopsis;
    }

    public void setSinopsis(String sinopsis) {
        this.sinopsis = sinopsis;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

}
