package com.example.proyecto1spring.dao;

import com.example.proyecto1spring.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    List<Usuario> findAll();

    Optional<Usuario> findById(Long id);

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRut(String rut);

    Usuario save(Usuario usuario);

    void deleteById(Long id);
}
