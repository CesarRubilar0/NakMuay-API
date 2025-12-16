package com.example.proyecto1spring.controllers.api;

import com.example.proyecto1spring.dto.HorarioDTO;
import com.example.proyecto1spring.entity.HorarioEntrenamiento;
import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.service.HorarioEntrenamientoService;
import com.example.proyecto1spring.service.MembresiaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:4200", "*"})
@Tag(name = "Horarios", description = "Gestión de horarios de entrenamiento")
public class HorarioRestController {

    private final HorarioEntrenamientoService horarioService;
    private final MembresiaService membresiaService;

    public HorarioRestController(HorarioEntrenamientoService horarioService, MembresiaService membresiaService) {
        this.horarioService = horarioService;
        this.membresiaService = membresiaService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los horarios", description = "Retorna una lista de todos los horarios")
    @ApiResponse(responseCode = "200", description = "Lista de horarios obtenida exitosamente")
    public ResponseEntity<List<HorarioDTO>> getAllHorarios() {
        List<HorarioEntrenamiento> horarios = horarioService.findAll();
        List<HorarioDTO> dtos = horarios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/membresia/{membresiaId}")
    @Operation(summary = "Obtener horarios de membresía", description = "Retorna los horarios asignados a una membresía específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horarios obtenidos exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = HorarioDTO.class))),
            @ApiResponse(responseCode = "404", description = "Membresía no encontrada")
    })
    public ResponseEntity<List<HorarioDTO>> getHorariosByMembresia(@PathVariable Long membresiaId) {
        Membresia membresia = membresiaService.findById(membresiaId).orElse(null);
        if (membresia == null) {
            return ResponseEntity.notFound().build();
        }

        List<HorarioEntrenamiento> horarios = horarioService.findByMembresia(membresia);
        List<HorarioDTO> dtos = horarios.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener horario por ID", description = "Retorna los detalles de un horario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario encontrado"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public ResponseEntity<HorarioDTO> getHorarioById(@PathVariable Long id) {
        HorarioEntrenamiento horario = horarioService.findById(id).orElse(null);
        if (horario == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(horario));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo horario", description = "Crea un nuevo horario de entrenamiento para una membresía")
    @ApiResponse(responseCode = "201", description = "Horario creado exitosamente")
    public ResponseEntity<HorarioDTO> createHorario(@RequestBody HorarioDTO horarioDTO) {
        Membresia membresia = membresiaService.findById(horarioDTO.getMembresiaId()).orElse(null);
        if (membresia == null) {
            return ResponseEntity.badRequest().build();
        }

        HorarioEntrenamiento horario = new HorarioEntrenamiento();
        horario.setMembresia(membresia);
        horario.setDiaSemana(horarioDTO.getDiaSemana());
        horario.setHoraInicio(horarioDTO.getHoraInicio());
        horario.setHoraFin(horarioDTO.getHoraFin());
        horario.setActivo(horarioDTO.getActivo() != null ? horarioDTO.getActivo() : true);

        HorarioEntrenamiento savedHorario = horarioService.save(horario);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedHorario));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar horario", description = "Actualiza los detalles de un horario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Horario actualizado"),
            @ApiResponse(responseCode = "404", description = "Horario no encontrado")
    })
    public ResponseEntity<HorarioDTO> updateHorario(@PathVariable Long id, @RequestBody HorarioDTO horarioDTO) {
        HorarioEntrenamiento horario = horarioService.findById(id).orElse(null);
        if (horario == null) {
            return ResponseEntity.notFound().build();
        }

        horario.setDiaSemana(horarioDTO.getDiaSemana());
        horario.setHoraInicio(horarioDTO.getHoraInicio());
        horario.setHoraFin(horarioDTO.getHoraFin());
        horario.setActivo(horarioDTO.getActivo());

        HorarioEntrenamiento updatedHorario = horarioService.save(horario);
        return ResponseEntity.ok(convertToDTO(updatedHorario));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar horario", description = "Elimina un horario de entrenamiento")
    @ApiResponse(responseCode = "204", description = "Horario eliminado")
    public ResponseEntity<Void> deleteHorario(@PathVariable Long id) {
        horarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private HorarioDTO convertToDTO(HorarioEntrenamiento horario) {
        return new HorarioDTO(
                horario.getId(),
                horario.getMembresia().getId(),
                horario.getDiaSemana(),
                horario.getHoraInicio(),
                horario.getHoraFin(),
                horario.getActivo()
        );
    }
}
