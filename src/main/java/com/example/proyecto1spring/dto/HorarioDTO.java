package com.example.proyecto1spring.dto;

import java.io.Serializable;

public class HorarioDTO implements Serializable {
    private Long id;
    private Long membresiaId;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;
    private Boolean activo;

    public HorarioDTO() {
    }

    public HorarioDTO(Long id, Long membresiaId, String diaSemana, String horaInicio, String horaFin, Boolean activo) {
        this.id = id;
        this.membresiaId = membresiaId;
        this.diaSemana = diaSemana;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.activo = activo;
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMembresiaId() {
        return membresiaId;
    }

    public void setMembresiaId(Long membresiaId) {
        this.membresiaId = membresiaId;
    }

    public String getDiaSemana() {
        return diaSemana;
    }

    public void setDiaSemana(String diaSemana) {
        this.diaSemana = diaSemana;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public String getHoraFin() {
        return horaFin;
    }

    public void setHoraFin(String horaFin) {
        this.horaFin = horaFin;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
