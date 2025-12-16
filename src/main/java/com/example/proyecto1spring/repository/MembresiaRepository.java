package com.example.proyecto1spring.repository;

import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembresiaRepository extends JpaRepository<Membresia, Long> {
    
    Optional<Membresia> findByUsuarioAndActivaTrue(Usuario usuario);
    
    List<Membresia> findByUsuario(Usuario usuario);
    
    List<Membresia> findByActivaTrue();
    
    boolean existsByUsuarioAndActivaTrue(Usuario usuario);
}
