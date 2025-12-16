package com.example.proyecto1spring.repository;

import com.example.proyecto1spring.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {
    
    Optional<Plan> findByNombre(String nombre);
    
    List<Plan> findByActivoTrue();
    
    boolean existsByNombre(String nombre);
    
    boolean existsByNombreAndIdNot(String nombre, Long id);
}
