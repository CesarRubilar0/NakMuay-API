package com.example.proyecto1spring.controllers.api;

import com.example.proyecto1spring.dto.UsuarioDTO;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints de autenticación y registro")
public class AuthRestController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    public AuthRestController(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar nuevo usuario", description = "Crea un nuevo usuario en el sistema")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado exitosamente",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UsuarioDTO.class))),
            @ApiResponse(responseCode = "400", description = "Email ya existe o datos inválidos")
    })
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            // Validar que el email no exista
            if (userService.findByEmailAsUsuario(request.getEmail()) != null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "El email ya está registrado");
                return ResponseEntity.badRequest().body(error);
            }

            // Crear nuevo usuario
            Usuario usuario = new Usuario();
            usuario.setNombre(request.getNombre());
            usuario.setApellido(request.getApellido());
            usuario.setEmail(request.getEmail());
            usuario.setPassword(passwordEncoder.encode(request.getPassword()));
            usuario.setRut(request.getRut());
            usuario.setRol(request.getRol() != null ? request.getRol() : "ALUMNO");

            Usuario savedUsuario = userService.save(usuario);

            // Retornar DTO sin password
            UsuarioDTO dto = new UsuarioDTO(
                    savedUsuario.getId(),
                    savedUsuario.getNombre(),
                    savedUsuario.getApellido(),
                    savedUsuario.getEmail(),
                    savedUsuario.getRut(),
                    savedUsuario.getRol()
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(dto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error al registrar usuario: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Autentica un usuario y retorna sus datos")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login exitoso"),
            @ApiResponse(responseCode = "401", description = "Credenciales inválidas")
    })
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            Usuario usuario = userService.findByEmailAsUsuario(request.getEmail());
            
            if (usuario == null || !passwordEncoder.matches(request.getPassword(), usuario.getPassword())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Credenciales inválidas");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
            }

            UsuarioDTO dto = new UsuarioDTO(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getRut(),
                    usuario.getRol()
            );

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Error en login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Clases internas para requests
    public static class RegisterRequest {
        private String nombre;
        private String apellido;
        private String email;
        private String password;
        private String rut;
        private String rol;

        // Getters y Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        
        public String getApellido() { return apellido; }
        public void setApellido(String apellido) { this.apellido = apellido; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
        
        public String getRut() { return rut; }
        public void setRut(String rut) { this.rut = rut; }
        
        public String getRol() { return rol; }
        public void setRol(String rol) { this.rol = rol; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        // Getters y Setters
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}
