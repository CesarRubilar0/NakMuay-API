package com.example.proyecto1spring.repository;

import com.example.proyecto1spring.entity.HorarioEntrenamiento;
import com.example.proyecto1spring.entity.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HorarioEntrenamientoRepository extends JpaRepository<HorarioEntrenamiento, Long> {
    
    List<HorarioEntrenamiento> findByMembresiaAndActivoTrue(Membresia membresia);
    
    List<HorarioEntrenamiento> findByMembresia(Membresia membresia);
    
    void deleteByMembresia(Membresia membresia);
}
