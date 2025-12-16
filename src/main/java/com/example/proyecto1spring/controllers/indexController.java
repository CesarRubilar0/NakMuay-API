package com.example.proyecto1spring.controllers;

import com.example.proyecto1spring.entity.Membresia;
import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.service.HorarioEntrenamientoService;
import com.example.proyecto1spring.service.MembresiaService;
import com.example.proyecto1spring.service.PlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class indexController {

    @Autowired
    private PlanService planService;
    
    @Autowired
    private MembresiaService membresiaService;
    
    @Autowired
    private HorarioEntrenamientoService horarioService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/inscripcion")
    public String inscripcionForm(@RequestParam(value = "success", required = false) String success, Model model) {
        model.addAttribute("success", success != null);
        model.addAttribute("planes", planService.findActivePlans());
        return "inscripcion";
    }

    @PostMapping("/inscripcion")
    public String handleInscripcion(@AuthenticationPrincipal Usuario usuario,
                                    @RequestParam Long planId,
                                    @RequestParam(value = "dias[]") String[] dias,
                                    @RequestParam(value = "horarios[]") String[] horarios,
                                    @RequestParam(required = false) String comentarios,
                                    RedirectAttributes redirectAttributes) {
        try {
            // Validar que haya al menos un horario
            if (dias == null || horarios == null || dias.length == 0 || dias.length != horarios.length) {
                redirectAttributes.addFlashAttribute("errorMessage", "Debes seleccionar al menos un horario");
                return "redirect:/inscripcion";
            }

            // Crear la membresía
            Membresia membresia = membresiaService.createMembresia(usuario.getId(), planId);
            
            // Crear los horarios de entrenamiento
            for (int i = 0; i < dias.length; i++) {
                String[] horarioSplit = horarios[i].split("-");
                if (horarioSplit.length == 2) {
                    horarioService.createHorario(membresia.getId(), dias[i], horarioSplit[0], horarioSplit[1]);
                }
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "¡Inscripción completada! Tu plan ha sido activado con los horarios seleccionados.");
            return "redirect:/mi-plan";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/inscripcion";
        }
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "nosotros";
    }

    @GetMapping("/registro")
    public String registro() {
        // Muestra el formulario de registro (usa la plantilla user_create)
        return "user_create";
    }

    @GetMapping("/galeria")
    public String galeria() {
        return "galeria";
    }
}
