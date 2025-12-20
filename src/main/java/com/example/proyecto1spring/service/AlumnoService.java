package com.example.proyecto1spring.service;

import com.example.proyecto1spring.dto.AlumnoRequest;
import com.example.proyecto1spring.dto.AlumnoResponse;

import java.util.List;

public interface AlumnoService {
    List<AlumnoResponse> findAll();
    AlumnoResponse findById(Long id);
    AlumnoResponse create(AlumnoRequest request);
    AlumnoResponse update(Long id, AlumnoRequest request);
    void delete(Long id);
}
