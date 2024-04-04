package com.example.biblioteca_cm;

public class Usuario {
    private String nombre;
    private String apellidos;
    private String dni;
    private String usuario;
    private String password;
    private String correo;
    private String telefono;

    private String tipo_user;

    public Usuario() {
    }

    // Constructor
    public Usuario(String nombre, String apellidos, String dni, String usuario, String password, String correo, String telefono, String tipo_user) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.usuario = usuario;
        this.password = password;
        this.correo = correo;
        this.telefono = telefono;
        this.tipo_user = "cliente";
    }

    public String getTipo_user() {
        return tipo_user;
    }

    public void setTipo_user(String tipo_user) {
        this.tipo_user = tipo_user;
    }

    // Métodos getters y setters
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
}

