package com.example.proyecto1spring.service.impl;

import com.example.proyecto1spring.entity.HorarioEntrenamiento;
import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.repository.HorarioEntrenamientoRepository;
import com.example.proyecto1spring.repository.MembresiaRepository;
import com.example.proyecto1spring.service.HorarioEntrenamientoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class HorarioEntrenamientoServiceImpl implements HorarioEntrenamientoService {

    private final HorarioEntrenamientoRepository horarioRepository;
    private final MembresiaRepository membresiaRepository;

    @Autowired
    public HorarioEntrenamientoServiceImpl(HorarioEntrenamientoRepository horarioRepository,
                                            MembresiaRepository membresiaRepository) {
        this.horarioRepository = horarioRepository;
        this.membresiaRepository = membresiaRepository;
    }

    @Override
    public List<HorarioEntrenamiento> findByMembresia(Membresia membresia) {
        Objects.requireNonNull(membresia, "Membresía no puede ser null");
        return horarioRepository.findByMembresiaAndActivoTrue(membresia);
    }

    @Override
    @Transactional
    public HorarioEntrenamiento createHorario(Long membresiaId, String diaSemana, String horaInicio, String horaFin) {
        Objects.requireNonNull(membresiaId, "Membresía ID no puede ser null");
        Objects.requireNonNull(diaSemana, "Día de la semana no puede ser null");
        Objects.requireNonNull(horaInicio, "Hora de inicio no puede ser null");
        Objects.requireNonNull(horaFin, "Hora de fin no puede ser null");

        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada"));

        HorarioEntrenamiento horario = new HorarioEntrenamiento(membresia, diaSemana, horaInicio, horaFin);
        return horarioRepository.save(horario);
    }

    @Override
    @Transactional
    public void deleteHorario(Long horarioId) {
        Objects.requireNonNull(horarioId, "Horario ID no puede ser null");
        
        if (!horarioRepository.existsById(horarioId)) {
            throw new IllegalArgumentException("Horario no encontrado");
        }
        
        horarioRepository.deleteById(horarioId);
    }

    @Override
    @Transactional
    public void deleteHorariosByMembresia(Membresia membresia) {
        Objects.requireNonNull(membresia, "Membresía no puede ser null");
        horarioRepository.deleteByMembresia(membresia);
    }
}
