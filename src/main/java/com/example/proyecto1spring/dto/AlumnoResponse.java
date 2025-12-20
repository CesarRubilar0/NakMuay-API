package com.example.proyecto1spring.dto;

import com.example.proyecto1spring.entity.Alumno;

public class AlumnoResponse {
    private Long id;
    private String nombre;
    private Alumno.Nivel nivel;
    private String fotoPerfil;
    private Double latitud;
    private Double longitud;

    public AlumnoResponse() {}

    public AlumnoResponse(Alumno alumno) {
        this.id = alumno.getId();
        this.nombre = alumno.getNombre();
        this.nivel = alumno.getNivel();
        this.fotoPerfil = alumno.getFotoPerfil();
        this.latitud = alumno.getLatitud();
        this.longitud = alumno.getLongitud();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Alumno.Nivel getNivel() { return nivel; }
    public void setNivel(Alumno.Nivel nivel) { this.nivel = nivel; }
    public String getFotoPerfil() { return fotoPerfil; }
    public void setFotoPerfil(String fotoPerfil) { this.fotoPerfil = fotoPerfil; }
    public Double getLatitud() { return latitud; }
    public void setLatitud(Double latitud) { this.latitud = latitud; }
    public Double getLongitud() { return longitud; }
    public void setLongitud(Double longitud) { this.longitud = longitud; }
}
