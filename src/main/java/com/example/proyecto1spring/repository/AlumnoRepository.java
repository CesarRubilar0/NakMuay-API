package com.example.proyecto1spring.repository;

import com.example.proyecto1spring.entity.Alumno;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlumnoRepository extends JpaRepository<Alumno, Long> {
}
