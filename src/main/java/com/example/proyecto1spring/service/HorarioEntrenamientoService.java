package com.example.proyecto1spring.service;

import com.example.proyecto1spring.entity.HorarioEntrenamiento;
import com.example.proyecto1spring.entity.Membresia;

import java.util.List;

public interface HorarioEntrenamientoService {
    
    List<HorarioEntrenamiento> findByMembresia(Membresia membresia);
    
    HorarioEntrenamiento createHorario(Long membresiaId, String diaSemana, String horaInicio, String horaFin);
    
    void deleteHorario(Long horarioId);
    
    void deleteHorariosByMembresia(Membresia membresia);
}
