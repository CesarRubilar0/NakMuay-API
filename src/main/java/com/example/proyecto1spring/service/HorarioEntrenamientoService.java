package com.example.proyecto1spring.service;

import com.example.proyecto1spring.entity.HorarioEntrenamiento;
import com.example.proyecto1spring.entity.Membresia;

import java.util.List;
import java.util.Optional;

public interface HorarioEntrenamientoService {
    
    List<HorarioEntrenamiento> findAll();

    List<HorarioEntrenamiento> findByMembresia(Membresia membresia);
    
    HorarioEntrenamiento createHorario(Long membresiaId, String diaSemana, String horaInicio, String horaFin);
    
    void deleteHorario(Long horarioId);

    void deleteById(Long id);
    
    void deleteHorariosByMembresia(Membresia membresia);

    Optional<HorarioEntrenamiento> findById(Long id);

    HorarioEntrenamiento save(HorarioEntrenamiento horario);
}
