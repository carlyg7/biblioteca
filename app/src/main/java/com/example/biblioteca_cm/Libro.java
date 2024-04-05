package com.example.biblioteca_cm;

public class Libro {

    private String autor;
    private Boolean disponible;
    private String editorial;
    private String sinopsis;
    private String titulo;

    private String isbn;

    public Libro() {
    }

    public Libro(String autor, Boolean disponible, String editorial, String sinopsis, String titulo, String isbn) {
        this.autor = autor;
        this.disponible = disponible;
        this.editorial = editorial;
        this.sinopsis = sinopsis;
        this.titulo = titulo;
        this.isbn = isbn;
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
