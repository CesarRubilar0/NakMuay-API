package com.example.proyecto1spring.controllers.api;

import com.example.proyecto1spring.dto.MembresiaDTO;
import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.entity.Plan;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.service.MembresiaService;
import com.example.proyecto1spring.service.PlanService;
import com.example.proyecto1spring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/membresias")
@Tag(name = "Membresías", description = "Gestión de membresías de usuarios")
public class MembresiaRestController {

    private final MembresiaService membresiaService;
    private final PlanService planService;
    private final UserService userService;

    public MembresiaRestController(MembresiaService membresiaService, PlanService planService, UserService userService) {
        this.membresiaService = membresiaService;
        this.planService = planService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Obtener todas las membresías", description = "Retorna una lista de todas las membresías")
    @ApiResponse(responseCode = "200", description = "Lista de membresías obtenida exitosamente")
    public ResponseEntity<List<MembresiaDTO>> getAllMembresias() {
        List<Membresia> membresias = membresiaService.findAll();
        List<MembresiaDTO> dtos = membresias.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener membresía activa del usuario", description = "Retorna la membresía activa de un usuario específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membresía encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = MembresiaDTO.class))),
            @ApiResponse(responseCode = "404", description = "Membresía no encontrada")
    })
    public ResponseEntity<MembresiaDTO> getMembresiaActiva(@PathVariable Long usuarioId) {
        Membresia membresia = membresiaService.findByUsuarioIdAndActiva(usuarioId, true);
        if (membresia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(membresia));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener membresía por ID", description = "Retorna los detalles de una membresía específica")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membresía encontrada"),
            @ApiResponse(responseCode = "404", description = "Membresía no encontrada")
    })
    public ResponseEntity<MembresiaDTO> getMembresiaById(@PathVariable Long id) {
        Membresia membresia = membresiaService.findById(id).orElse(null);
        if (membresia == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(membresia));
    }

    @PostMapping
    @Operation(summary = "Crear nueva membresía", description = "Crea una nueva membresía para un usuario")
    @ApiResponse(responseCode = "201", description = "Membresía creada exitosamente")
    public ResponseEntity<MembresiaDTO> createMembresia(@RequestBody MembresiaDTO membresiaDTO) {
        Usuario usuario = userService.findById(membresiaDTO.getUsuarioId()).orElse(null);
        Plan plan = planService.findById(membresiaDTO.getPlanId()).orElse(null);

        if (usuario == null || plan == null) {
            return ResponseEntity.badRequest().build();
        }

        Membresia membresia = new Membresia();
        membresia.setUsuario(usuario);
        membresia.setPlan(plan);
        membresia.setFechaInicio(membresiaDTO.getFechaInicio());
        membresia.setFechaFin(membresiaDTO.getFechaFin());
        membresia.setActiva(membresiaDTO.getActiva() != null ? membresiaDTO.getActiva() : true);

        Membresia savedMembresia = membresiaService.save(membresia);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedMembresia));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar membresía", description = "Actualiza los detalles de una membresía")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Membresía actualizada"),
            @ApiResponse(responseCode = "404", description = "Membresía no encontrada")
    })
    public ResponseEntity<MembresiaDTO> updateMembresia(@PathVariable Long id, @RequestBody MembresiaDTO membresiaDTO) {
        Membresia membresia = membresiaService.findById(id).orElse(null);
        if (membresia == null) {
            return ResponseEntity.notFound().build();
        }

        membresia.setFechaInicio(membresiaDTO.getFechaInicio());
        membresia.setFechaFin(membresiaDTO.getFechaFin());
        membresia.setActiva(membresiaDTO.getActiva());

        Membresia updatedMembresia = membresiaService.save(membresia);
        return ResponseEntity.ok(convertToDTO(updatedMembresia));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar membresía", description = "Elimina una membresía")
    @ApiResponse(responseCode = "204", description = "Membresía eliminada")
    public ResponseEntity<Void> deleteMembresia(@PathVariable Long id) {
        membresiaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private MembresiaDTO convertToDTO(Membresia membresia) {
        return new MembresiaDTO(
                membresia.getId(),
                membresia.getUsuario().getId(),
                membresia.getUsuario().getNombre() + " " + membresia.getUsuario().getApellido(),
                membresia.getUsuario().getEmail(),
                membresia.getPlan().getId(),
                membresia.getPlan().getNombre(),
                membresia.getFechaInicio(),
                membresia.getFechaFin(),
                membresia.getActiva()
        );
    }
}
