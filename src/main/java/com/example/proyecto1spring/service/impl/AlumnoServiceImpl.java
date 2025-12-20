package com.example.proyecto1spring.service.impl;

import com.example.proyecto1spring.dto.AlumnoRequest;
import com.example.proyecto1spring.dto.AlumnoResponse;
import com.example.proyecto1spring.entity.Alumno;
import com.example.proyecto1spring.repository.AlumnoRepository;
import com.example.proyecto1spring.service.AlumnoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class AlumnoServiceImpl implements AlumnoService {

    private final AlumnoRepository alumnoRepository;

    public AlumnoServiceImpl(AlumnoRepository alumnoRepository) {
        this.alumnoRepository = alumnoRepository;
    }

    @Override
    public List<AlumnoResponse> findAll() {
        return alumnoRepository.findAll().stream().map(AlumnoResponse::new).toList();
    }

    @Override
    public AlumnoResponse findById(Long id) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado"));
        return new AlumnoResponse(alumno);
    }

    @Override
    @Transactional
    public AlumnoResponse create(AlumnoRequest request) {
        Alumno alumno = new Alumno();
        alumno.setNombre(request.getNombre());
        alumno.setNivel(request.getNivel());
        alumno.setFotoPerfil(request.getFotoPerfil());
        alumno.setLatitud(request.getLatitud());
        alumno.setLongitud(request.getLongitud());
        Alumno saved = alumnoRepository.save(alumno);
        return new AlumnoResponse(saved);
    }

    @Override
    @Transactional
    public AlumnoResponse update(Long id, AlumnoRequest request) {
        Alumno alumno = alumnoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado"));
        alumno.setNombre(request.getNombre());
        alumno.setNivel(request.getNivel());
        alumno.setFotoPerfil(request.getFotoPerfil());
        alumno.setLatitud(request.getLatitud());
        alumno.setLongitud(request.getLongitud());
        Alumno saved = alumnoRepository.save(alumno);
        return new AlumnoResponse(saved);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!alumnoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Alumno no encontrado");
        }
        alumnoRepository.deleteById(id);
    }
}
