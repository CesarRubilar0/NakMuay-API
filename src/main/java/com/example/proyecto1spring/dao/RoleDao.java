package com.example.proyecto1spring.dao;

import com.example.proyecto1spring.entity.Role;

import java.util.List;
import java.util.Optional;

public interface RoleDao {

    List<Role> findAll();

    Optional<Role> findById(Long id);

    Optional<Role> findByName(String name);

    Role save(Role role);

    void deleteById(Long id);
}
