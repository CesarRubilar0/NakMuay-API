package com.example.proyecto1spring.service;

import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface MembresiaService {
    
    List<Membresia> findAll();
    
    List<Membresia> findActiveMembresias();
    
    Optional<Membresia> findById(Long id);
    
    Optional<Membresia> findActiveMembresiaByUsuario(Usuario usuario);
    
    List<Membresia> findByUsuario(Usuario usuario);
    
    Membresia createMembresia(Long usuarioId, Long planId);
    
    Membresia changePlan(Long membresiaId, Long nuevoPlanId);
    
    void cancelMembresia(Long membresiaId);

    void deleteById(Long id);
    
    boolean hasActiveMembresia(Usuario usuario);

    Membresia save(Membresia membresia);

    Membresia findByUsuarioIdAndActiva(Long usuarioId, Boolean activa);
}
