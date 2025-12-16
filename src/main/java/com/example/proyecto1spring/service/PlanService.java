package com.example.proyecto1spring.service;

import com.example.proyecto1spring.entity.Plan;

import java.util.List;
import java.util.Optional;

public interface PlanService {
    
    List<Plan> findAll();
    
    List<Plan> findActivePlans();
    
    Optional<Plan> findById(Long id);
    
    Plan createPlan(Plan plan);
    
    Plan updatePlan(Long id, Plan plan);
    
    void deletePlan(Long id);
    
    void toggleActive(Long id);
}
