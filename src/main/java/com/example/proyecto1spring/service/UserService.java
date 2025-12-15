package com.example.proyecto1spring.service;

import com.example.proyecto1spring.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Usuario createUser(Usuario user, String roleName);
    List<Usuario> findAll();
    Optional<Usuario> findById(Long id);
    void deleteById(Long id);
    Optional<Usuario> findByEmail(String email);
    Usuario toggleEnabled(Long id);
    Usuario updateUser(Long id, Usuario user);
}
