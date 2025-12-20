package com.example.proyecto1spring.controllers.api;

import com.example.proyecto1spring.dto.PlanAsignacionRequest;
import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.service.MembresiaService;
import com.example.proyecto1spring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Alumnos", description = "Operaciones para el usuario autenticado")
public class AlumnoRestController {

    private final UserService userService;
    private final MembresiaService membresiaService;

    public AlumnoRestController(UserService userService, MembresiaService membresiaService) {
        this.userService = userService;
        this.membresiaService = membresiaService;
    }

    @GetMapping("/alumnos/me")
    @Operation(summary = "Obtener perfil propio")
    public ResponseEntity<?> me(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        return ResponseEntity.ok(Map.of(
                "id", usuario.getId(),
                "nombre", usuario.getNombre(),
                "apellido", usuario.getApellido(),
                "email", usuario.getEmail(),
                "rol", usuario.getRol()
        ));
    }

    @PutMapping("/alumnos/me")
    @Operation(summary = "Actualizar perfil propio")
    public ResponseEntity<?> updateMe(@AuthenticationPrincipal Usuario usuario,
                                      @RequestBody Map<String, String> body) {
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        Usuario updates = new Usuario();
        updates.setNombre(body.getOrDefault("nombre", usuario.getNombre()));
        updates.setApellido(body.getOrDefault("apellido", usuario.getApellido()));
        updates.setRut(body.getOrDefault("rut", usuario.getRut()));
        updates.setEmail(usuario.getEmail());
        updates.setRol(usuario.getRol());
        updates.setPassword(null); // no cambiar contraseña aquí
        userService.updateUser(usuario.getId(), updates);
        return ResponseEntity.ok(Map.of("message", "Perfil actualizado"));
    }

    @PostMapping("/planes/asignaciones")
    @Operation(summary = "Asignarse a un plan")
    public ResponseEntity<?> asignarPlan(@AuthenticationPrincipal Usuario usuario,
                                         @RequestBody PlanAsignacionRequest request) {
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        Membresia membresia = membresiaService.createMembresia(usuario.getId(), request.getPlanId());
        return ResponseEntity.ok(Map.of(
                "membresiaId", membresia.getId(),
                "planId", membresia.getPlan().getId(),
                "mensaje", "Plan asignado"
        ));
    }

    @GetMapping("/planes/asignaciones/mias")
    @Operation(summary = "Listar mis asignaciones de planes")
    public ResponseEntity<?> misAsignaciones(@AuthenticationPrincipal Usuario usuario) {
        if (usuario == null) {
            return ResponseEntity.status(401).build();
        }
        List<Membresia> membresias = membresiaService.findByUsuario(usuario);
        return ResponseEntity.ok(membresias.stream().map(m -> Map.of(
                "membresiaId", m.getId(),
                "planId", m.getPlan().getId(),
                "planNombre", m.getPlan().getNombre(),
                "fechaInicio", m.getFechaInicio(),
                "fechaFin", m.getFechaFin(),
                "activa", m.getActiva()
        )));
    }
}
