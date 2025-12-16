package com.example.proyecto1spring.service.impl;

import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.entity.Plan;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.repository.MembresiaRepository;
import com.example.proyecto1spring.repository.PlanRepository;
import com.example.proyecto1spring.repository.UserRepository;
import com.example.proyecto1spring.service.MembresiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class MembresiaServiceImpl implements MembresiaService {

    private final MembresiaRepository membresiaRepository;
    private final UserRepository userRepository;
    private final PlanRepository planRepository;

    @Autowired
    public MembresiaServiceImpl(MembresiaRepository membresiaRepository, 
                                 UserRepository userRepository,
                                 PlanRepository planRepository) {
        this.membresiaRepository = membresiaRepository;
        this.userRepository = userRepository;
        this.planRepository = planRepository;
    }

    @Override
    public List<Membresia> findAll() {
        return membresiaRepository.findAll();
    }

    @Override
    public List<Membresia> findActiveMembresias() {
        return membresiaRepository.findByActivaTrue();
    }

    @Override
    public Optional<Membresia> findById(Long id) {
        Objects.requireNonNull(id, "ID no puede ser null");
        return membresiaRepository.findById(id);
    }

    @Override
    public Optional<Membresia> findActiveMembresiaByUsuario(Usuario usuario) {
        Objects.requireNonNull(usuario, "Usuario no puede ser null");
        return membresiaRepository.findByUsuarioAndActivaTrue(usuario);
    }

    @Override
    public List<Membresia> findByUsuario(Usuario usuario) {
        Objects.requireNonNull(usuario, "Usuario no puede ser null");
        return membresiaRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public Membresia createMembresia(Long usuarioId, Long planId) {
        Objects.requireNonNull(usuarioId, "Usuario ID no puede ser null");
        Objects.requireNonNull(planId, "Plan ID no puede ser null");
        
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
        
        if (membresiaRepository.existsByUsuarioAndActivaTrue(usuario)) {
            throw new IllegalArgumentException("El usuario ya tiene una membresía activa");
        }
        
        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusMonths(plan.getDuracionMeses());
        
        Membresia membresia = new Membresia(usuario, plan, fechaInicio, fechaFin);
        return membresiaRepository.save(membresia);
    }

    @Override
    @Transactional
    public Membresia changePlan(Long membresiaId, Long nuevoPlanId) {
        Objects.requireNonNull(membresiaId, "Membresía ID no puede ser null");
        Objects.requireNonNull(nuevoPlanId, "Plan ID no puede ser null");
        
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada"));
        
        Plan nuevoPlan = planRepository.findById(nuevoPlanId)
                .orElseThrow(() -> new IllegalArgumentException("Plan no encontrado"));
        
        if (!membresia.getActiva()) {
            throw new IllegalArgumentException("No se puede cambiar el plan de una membresía inactiva");
        }
        
        // Calcular nueva fecha de fin basada en la fecha actual y duración del nuevo plan
        LocalDate nuevaFechaInicio = LocalDate.now();
        LocalDate nuevaFechaFin = nuevaFechaInicio.plusMonths(nuevoPlan.getDuracionMeses());
        
        membresia.setPlan(nuevoPlan);
        membresia.setFechaInicio(nuevaFechaInicio);
        membresia.setFechaFin(nuevaFechaFin);
        
        return membresiaRepository.save(membresia);
    }

    @Override
    @Transactional
    public void cancelMembresia(Long membresiaId) {
        Objects.requireNonNull(membresiaId, "Membresía ID no puede ser null");
        
        Membresia membresia = membresiaRepository.findById(membresiaId)
                .orElseThrow(() -> new IllegalArgumentException("Membresía no encontrada"));
        
        membresia.setActiva(false);
        membresiaRepository.save(membresia);
    }

    @Override
    public boolean hasActiveMembresia(Usuario usuario) {
        Objects.requireNonNull(usuario, "Usuario no puede ser null");
        return membresiaRepository.existsByUsuarioAndActivaTrue(usuario);
    }
}
