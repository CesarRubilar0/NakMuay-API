package com.example.proyecto1spring.controllers;

import com.example.proyecto1spring.entity.Plan;
import com.example.proyecto1spring.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/admin/planes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPlanesController {

    private final PlanService planService;

    @Autowired
    public AdminPlanesController(PlanService planService) {
        this.planService = planService;
    }

    @GetMapping
    public String listarPlanes(Model model) {
        List<Plan> planes = planService.findAll();
        model.addAttribute("planes", planes);
        return "admin_planes_list";
    }

    @GetMapping("/crear")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("plan", new Plan());
        model.addAttribute("accion", "crear");
        return "admin_planes_form";
    }

    @PostMapping("/crear")
    public String crearPlan(@Valid @ModelAttribute("plan") Plan plan,
                             BindingResult result,
                             RedirectAttributes redirectAttributes,
                             Model model) {
        if (result.hasErrors()) {
            model.addAttribute("accion", "crear");
            return "admin_planes_form";
        }

        try {
            planService.createPlan(plan);
            redirectAttributes.addFlashAttribute("successMessage", "Plan creado exitosamente");
            return "redirect:/admin/planes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accion", "crear");
            return "admin_planes_form";
        }
    }

    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return planService.findById(id)
                .map(plan -> {
                    model.addAttribute("plan", plan);
                    model.addAttribute("accion", "editar");
                    return "admin_planes_form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Plan no encontrado");
                    return "redirect:/admin/planes";
                });
    }

    @PostMapping("/editar/{id}")
    public String editarPlan(@PathVariable Long id,
                              @Valid @ModelAttribute("plan") Plan plan,
                              BindingResult result,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        if (result.hasErrors()) {
            model.addAttribute("accion", "editar");
            return "admin_planes_form";
        }

        try {
            planService.updatePlan(id, plan);
            redirectAttributes.addFlashAttribute("successMessage", "Plan actualizado exitosamente");
            return "redirect:/admin/planes";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("accion", "editar");
            return "admin_planes_form";
        }
    }

    @PostMapping("/eliminar/{id}")
    public String eliminarPlan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            planService.deletePlan(id);
            redirectAttributes.addFlashAttribute("successMessage", "Plan eliminado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/planes";
    }

    @PostMapping("/toggle-active/{id}")
    public String toggleActivePlan(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            planService.toggleActive(id);
            redirectAttributes.addFlashAttribute("successMessage", "Estado del plan actualizado");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/planes";
    }
}
