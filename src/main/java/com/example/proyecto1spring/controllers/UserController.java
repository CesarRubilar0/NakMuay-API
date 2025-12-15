package com.example.proyecto1spring.controllers;

import com.example.proyecto1spring.entity.Usuario;
import com.example.proyecto1spring.service.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/user/list")
    @PreAuthorize("hasRole('ADMIN')")
    public String listUsers(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user_list";
    }

    @GetMapping("/usuarios_list")
    @PreAuthorize("hasRole('ADMIN')")
    public String usuariosList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "user_list"; // reutiliza la plantilla existente
    }

    @GetMapping("/user/create")
    public String createUserForm(Model model) {
        model.addAttribute("user", new Usuario());
        return "user_create";
    }

    @PostMapping("/user/create")
    public String createUserSubmit(@RequestParam("nombre") String nombre,
                                   @RequestParam("apellido") String apellido,
                                   @RequestParam("email") String email,
                                   @RequestParam("rut") String rut,
                                   @RequestParam("password") String password,
                                   @RequestParam(value = "rol", defaultValue = "USER") String rol) {

        Usuario u = new Usuario();
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setEmail(email);
        u.setRut(rut);
        u.setPassword(password);
        String normalizedRole = (rol.equalsIgnoreCase("Administrador") || rol.equalsIgnoreCase("ADMIN")) ? "ADMIN" : "USER";
        try {
            userService.createUser(u, normalizedRole);
        } catch (IllegalArgumentException ex) {
            // Para simplificar redirigimos a formulario; idealmente mostrar mensaje en la vista
            return "redirect:/user/create?error";
        }
        // Redirigir al login mostrando un mensaje de registro exitoso
        return "redirect:/login?registered";
    }

    @GetMapping("/user/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUser(@PathVariable("id") Long id) {
        userService.deleteById(id);
        return "redirect:/user/list";
    }

    @GetMapping("/user/toggle/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String toggleUser(@PathVariable("id") Long id) {
        userService.toggleEnabled(id);
        return "redirect:/user/list";
    }

    @GetMapping("/user/view/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewUser(@PathVariable("id") Long id, Model model) {
        userService.findById(id).ifPresent(user -> model.addAttribute("user", user));
        return "user_view";
    }

    @GetMapping("/user/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUser(@PathVariable("id") Long id, Model model) {
        userService.findById(id).ifPresent(user -> model.addAttribute("user", user));
        return "user_edit";
    }

    @PostMapping("/user/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(@PathVariable("id") Long id, @ModelAttribute("user") Usuario user) {
        try {
            userService.updateUser(id, user);
        } catch (IllegalArgumentException ex) {
            // En caso de error (p.ej. email duplicado) redirigimos al formulario con un flag
            return "redirect:/user/edit/" + id + "?error";
        }
        return "redirect:/user/list";
    }

}
