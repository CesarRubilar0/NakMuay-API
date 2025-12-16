package com.example.proyecto1spring.controllers.api;

import com.example.proyecto1spring.dto.PlanDTO;
import com.example.proyecto1spring.entity.Plan;
import com.example.proyecto1spring.service.PlanService;
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
@RequestMapping("/api/planes")
@CrossOrigin(origins = {"http://localhost:8100", "http://localhost:4200", "*"})
@Tag(name = "Planes", description = "Gestión de planes de entrenamiento")
public class PlanRestController {

    private final PlanService planService;

    public PlanRestController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    @Operation(summary = "Obtener todos los planes", description = "Retorna una lista de todos los planes disponibles")
    @ApiResponse(responseCode = "200", description = "Lista de planes obtenida exitosamente",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlanDTO.class)))
    public ResponseEntity<List<PlanDTO>> getAllPlanes() {
        List<Plan> planes = planService.findAll();
        List<PlanDTO> dtos = planes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener plan por ID", description = "Retorna los detalles de un plan específico")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan encontrado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlanDTO.class))),
            @ApiResponse(responseCode = "404", description = "Plan no encontrado")
    })
    public ResponseEntity<PlanDTO> getPlanById(@PathVariable Long id) {
        Plan plan = planService.findById(id).orElse(null);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(convertToDTO(plan));
    }

    @PostMapping
    @Operation(summary = "Crear nuevo plan", description = "Crea un nuevo plan de entrenamiento")
    @ApiResponse(responseCode = "201", description = "Plan creado exitosamente")
    public ResponseEntity<PlanDTO> createPlan(@RequestBody PlanDTO planDTO) {
        Plan plan = new Plan();
        plan.setNombre(planDTO.getNombre());
        plan.setDescripcion(planDTO.getDescripcion());
        plan.setPrecio(planDTO.getPrecio());
        plan.setDuracionMeses(planDTO.getDuracionMeses());
        plan.setActivo(planDTO.getActivo() != null ? planDTO.getActivo() : true);
        
        Plan savedPlan = planService.save(plan);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedPlan));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar plan", description = "Actualiza los detalles de un plan existente")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Plan actualizado"),
            @ApiResponse(responseCode = "404", description = "Plan no encontrado")
    })
    public ResponseEntity<PlanDTO> updatePlan(@PathVariable Long id, @RequestBody PlanDTO planDTO) {
        Plan plan = planService.findById(id).orElse(null);
        if (plan == null) {
            return ResponseEntity.notFound().build();
        }
        
        plan.setNombre(planDTO.getNombre());
        plan.setDescripcion(planDTO.getDescripcion());
        plan.setPrecio(planDTO.getPrecio());
        plan.setDuracionMeses(planDTO.getDuracionMeses());
        plan.setActivo(planDTO.getActivo());
        
        Plan updatedPlan = planService.save(plan);
        return ResponseEntity.ok(convertToDTO(updatedPlan));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar plan", description = "Elimina un plan de la base de datos")
    @ApiResponse(responseCode = "204", description = "Plan eliminado")
    public ResponseEntity<Void> deletePlan(@PathVariable Long id) {
        planService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private PlanDTO convertToDTO(Plan plan) {
        return new PlanDTO(
                plan.getId(),
                plan.getNombre(),
                plan.getDescripcion(),
                plan.getPrecio(),
                plan.getDuracionMeses(),
                plan.getActivo()
        );
    }
}
