package com.example.proyecto1spring.controllers;

import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.service.MembresiaService;
import com.example.proyecto1spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/alumnos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAlumnosController {

    private final UserService userService;
    private final MembresiaService membresiaService;

    @Autowired
    public AdminAlumnosController(UserService userService, MembresiaService membresiaService) {
        this.userService = userService;
        this.membresiaService = membresiaService;
    }

    @GetMapping
    public String listarAlumnos(Model model) {
        List<Usuario> alumnos = userService.findAll();
        model.addAttribute("alumnos", alumnos);
        return "admin_alumnos_list";
    }

    @GetMapping("/ver/{id}")
    public String verAlumno(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Usuario> usuarioOpt = userService.findById(id);
        
        if (usuarioOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Alumno no encontrado");
            return "redirect:/admin/alumnos";
        }
        
        Usuario usuario = usuarioOpt.get();
        Optional<Membresia> membresiaActual = membresiaService.findActiveMembresiaByUsuario(usuario);
        List<Membresia> historialMembresias = membresiaService.findByUsuario(usuario);
        
        model.addAttribute("alumno", usuario);
        model.addAttribute("membresiaActual", membresiaActual.orElse(null));
        model.addAttribute("historialMembresias", historialMembresias);
        model.addAttribute("tieneMembresia", membresiaActual.isPresent());
        
        return "admin_alumno_detail";
    }

    @PostMapping("/toggle-enabled/{id}")
    public String toggleEnabled(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleEnabled(id);
            redirectAttributes.addFlashAttribute("successMessage", "Estado del alumno actualizado");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/alumnos";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarAlumno(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Alumno eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/alumnos";
    }
}
