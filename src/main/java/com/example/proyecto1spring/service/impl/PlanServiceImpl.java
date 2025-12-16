package com.example.proyecto1spring.service.impl;

import com.example.proyecto1spring.entity.Plan;
import com.example.proyecto1spring.repository.PlanRepository;
import com.example.proyecto1spring.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PlanServiceImpl implements PlanService {

    private final PlanRepository planRepository;

    @Autowired
    public PlanServiceImpl(PlanRepository planRepository) {
        this.planRepository = planRepository;
    }

    @Override
    public List<Plan> findAll() {
        return planRepository.findAll();
    }

    @Override
    public List<Plan> findActivePlans() {
        return planRepository.findByActivoTrue();
    }

    @Override
    public Optional<Plan> findById(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        return planRepository.findById(id);
    }

    @Override
    @Transactional
    public Plan createPlan(Plan plan) {
        Objects.requireNonNull(plan, "Plan no puede ser null");
        
        if (planRepository.existsByNombre(plan.getNombre())) {
            throw new IllegalArgumentException("Ya existe un plan con ese nombre");
        }
        
        return planRepository.save(plan);
    }

    @Override
    @Transactional
    public Plan updatePlan(Long id, Plan plan) {
        Objects.requireNonNull(id, "ID no puede ser null");
        Objects.requireNonNull(plan, "Plan no puede ser null");
        
        Plan existingPlan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
        
        if (planRepository.existsByNombreAndIdNot(plan.getNombre(), id)) {
            throw new IllegalArgumentException("Ya existe otro plan con ese nombre");
        }
        
        existingPlan.setNombre(plan.getNombre());
        existingPlan.setDescripcion(plan.getDescripcion());
        existingPlan.setPrecio(plan.getPrecio());
        existingPlan.setDuracionMeses(plan.getDuracionMeses());
        existingPlan.setActivo(plan.getActivo());
        
        return planRepository.save(existingPlan);
    }

    @Override
    @Transactional
    public void deletePlan(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        
        if (!planRepository.existsById(id)) {
            throw new IllegalArgumentException("Plan no encontrado");
        }
        
        planRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void toggleActive(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        
        Plan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
        
        plan.setActivo(!plan.getActivo());
        planRepository.save(plan);
    }

    @Override
    public void deleteById(Long id) {
        deletePlan(id);
    }

    @Override
    public Plan save(Plan plan) {
        Objects.requireNonNull(plan, "Plan no puede ser null");
        return planRepository.save(plan);
    }
}
