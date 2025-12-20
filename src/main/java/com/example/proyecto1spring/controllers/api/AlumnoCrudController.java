package com.example.proyecto1spring.controllers.api;

import com.example.proyecto1spring.dto.AlumnoRequest;
import com.example.proyecto1spring.dto.AlumnoResponse;
import com.example.proyecto1spring.service.AlumnoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@Tag(name = "Alumnos CRUD", description = "CRUD de alumnos para la app Ionic")
public class AlumnoCrudController {

    private final AlumnoService alumnoService;

    public AlumnoCrudController(AlumnoService alumnoService) {
        this.alumnoService = alumnoService;
    }

    @GetMapping
    @Operation(summary = "Listar alumnos")
    public ResponseEntity<List<AlumnoResponse>> findAll() {
        return ResponseEntity.ok(alumnoService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener alumno por id")
    public ResponseEntity<AlumnoResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(alumnoService.findById(id));
    }

    @PostMapping
    @Operation(summary = "Crear alumno")
    public ResponseEntity<AlumnoResponse> create(@Valid @RequestBody AlumnoRequest request) {
        AlumnoResponse created = alumnoService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar alumno")
    public ResponseEntity<AlumnoResponse> update(@PathVariable Long id, @Valid @RequestBody AlumnoRequest request) {
        AlumnoResponse updated = alumnoService.update(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar alumno")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        alumnoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
