package com.example.proyecto1spring.controllers;

import com.example.proyecto1spring.entity.HorarioEntrenamiento;
import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.entity.Plan;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.service.HorarioEntrenamientoService;
import com.example.proyecto1spring.service.MembresiaService;
import com.example.proyecto1spring.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mi-plan")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MiPlanController {

    private final MembresiaService membresiaService;
    private final PlanService planService;
    private final HorarioEntrenamientoService horarioService;

    @Autowired
    public MiPlanController(MembresiaService membresiaService, PlanService planService, HorarioEntrenamientoService horarioService) {
        this.membresiaService = membresiaService;
        this.planService = planService;
        this.horarioService = horarioService;
    }

    @GetMapping
    public String verMiPlan(@AuthenticationPrincipal Usuario usuario, Model model) {
        Optional<Membresia> membresiaActual = membresiaService.findActiveMembresiaByUsuario(usuario);
        List<Membresia> historial = membresiaService.findByUsuario(usuario);
        List<Plan> planesDisponibles = planService.findActivePlans();

        model.addAttribute("membresiaActual", membresiaActual.orElse(null));
        model.addAttribute("historialMembresias", historial);
        model.addAttribute("planesDisponibles", planesDisponibles);
        model.addAttribute("tieneMembresia", membresiaActual.isPresent());
        
        // Cargar horarios si hay membresía activa
        if (membresiaActual.isPresent()) {
            List<HorarioEntrenamiento> horarios = horarioService.findByMembresia(membresiaActual.get());
            model.addAttribute("horarios", horarios);
        }

        return "mi_plan";
    }

    @PostMapping("/contratar")
    public String contratarPlan(@AuthenticationPrincipal Usuario usuario,
                                 @RequestParam("planId") Long planId,
                                 RedirectAttributes redirectAttributes) {
        try {
            membresiaService.createMembresia(usuario.getId(), planId);
            redirectAttributes.addFlashAttribute("successMessage", "¡Plan contratado exitosamente!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/mi-plan";
    }

    @PostMapping("/cambiar")
    public String cambiarPlan(@AuthenticationPrincipal Usuario usuario,
                               @RequestParam("membresiaId") Long membresiaId,
                               @RequestParam("nuevoPlanId") Long nuevoPlanId,
                               RedirectAttributes redirectAttributes) {
        try {
            membresiaService.changePlan(membresiaId, nuevoPlanId);
            redirectAttributes.addFlashAttribute("successMessage", "¡Plan cambiado exitosamente!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/mi-plan";
    }

    @PostMapping("/cancelar")
    public String cancelarMembresia(@AuthenticationPrincipal Usuario usuario,
                                     @RequestParam("membresiaId") Long membresiaId,
                                     RedirectAttributes redirectAttributes) {
        try {
            membresiaService.cancelMembresia(membresiaId);
            redirectAttributes.addFlashAttribute("successMessage", "Membresía cancelada exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/mi-plan";
    }
    
    @PostMapping("/horario/agregar")
    public String agregarHorario(@AuthenticationPrincipal Usuario usuario,
                                  @RequestParam("membresiaId") Long membresiaId,
                                  @RequestParam("dia") String dia,
                                  @RequestParam("horario") String horario,
                                  RedirectAttributes redirectAttributes) {
        try {
            String[] horarioSplit = horario.split("-");
            if (horarioSplit.length == 2) {
                horarioService.createHorario(membresiaId, dia, horarioSplit[0], horarioSplit[1]);
                redirectAttributes.addFlashAttribute("successMessage", "Horario agregado exitosamente");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/mi-plan";
    }
    
    @PostMapping("/horario/eliminar/{horarioId}")
    public String eliminarHorario(@PathVariable Long horarioId,
                                   RedirectAttributes redirectAttributes) {
        try {
            horarioService.deleteHorario(horarioId);
            redirectAttributes.addFlashAttribute("successMessage", "Horario eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/mi-plan";
    }
}
