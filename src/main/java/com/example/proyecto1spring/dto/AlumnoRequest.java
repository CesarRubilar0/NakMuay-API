package com.example.proyecto1spring.dto;

import com.example.proyecto1spring.entity.Alumno;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class AlumnoRequest {
    @NotBlank
    private String nombre;
    @NotNull
    private Alumno.Nivel nivel;
    private String fotoPerfil; // base64 opcional
    private Double latitud;
    private Double longitud;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Alumno.Nivel getNivel() {
        return nivel;
    }

    public void setNivel(Alumno.Nivel nivel) {
        this.nivel = nivel;
    }

    public String getFotoPerfil() {
        return fotoPerfil;
    }

    public void setFotoPerfil(String fotoPerfil) {
        this.fotoPerfil = fotoPerfil;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }
}
