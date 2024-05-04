package com.example.biblioteca_cm;

public class Reserva {

    private String dni;
    private String isbn;

    private String titulo_libro;
    private String nombre_usuario;

    public Reserva() {
    }

    public Reserva(String dni, String isbn, String titulo_libro, String nombre_usuario) {
        this.dni = dni;
        this.isbn = isbn;
        this.titulo_libro = titulo_libro;
        this.nombre_usuario = nombre_usuario;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitulo_libro() {
        return titulo_libro;
    }

    public void setTitulo_libro(String titulo_libro) {
        this.titulo_libro = titulo_libro;
    }

    public String getNombre_usuario() {
        return nombre_usuario;
    }

    public void setNombre_usuario(String nombre_usuario) {
        this.nombre_usuario = nombre_usuario;
    }

    @Override
    public String toString() {
        return "Reserva{" +
                "dni='" + dni + '\'' +
                ", isbn='" + isbn + '\'' +
                ", titulo_libro='" + titulo_libro + '\'' +
                ", nombre_usuario='" + nombre_usuario + '\'' +
                '}';
    }
}
