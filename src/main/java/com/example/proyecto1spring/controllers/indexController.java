package com.example.proyecto1spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class indexController {

    // Almacenamiento simple en memoria para inscripciones (temporal)
    private final List<Inscripcion> inscripciones = new ArrayList<>();

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
        return "inscripcion";
    }

    @PostMapping("/inscripcion")
    public String handleInscripcion(@RequestParam String nombre,
                                    @RequestParam(required = false) Integer edad,
                                    @RequestParam(required = false) String email,
                                    @RequestParam(required = false) String direccion,
                                    @RequestParam(required = false) String trabajaEstudia,
                                    @RequestParam(required = false) String tutor,
                                    @RequestParam(required = false) String plan,
                                    @RequestParam(required = false) String comentarios,
                                    @RequestParam(required = false) String fechaPrueba,
                                    @RequestParam(required = false) String horaPrueba) {

        Inscripcion i = new Inscripcion(nombre, edad, email, direccion, trabajaEstudia, tutor, plan, comentarios, fechaPrueba, horaPrueba);
        inscripciones.add(i);

        // Redirige a la misma página con parámetro success
        return "redirect:/inscripcion?success=true";
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

    // Clase simple para almacenar datos de inscripción en memoria
    public static class Inscripcion {
        public String nombre;
        public Integer edad;
        public String email;
        public String direccion;
        public String trabajaEstudia;
        public String tutor;
        public String plan;
        public String comentarios;
        public String fechaPrueba;
        public String horaPrueba;

        public Inscripcion(String nombre, Integer edad, String email, String direccion, String trabajaEstudia, String tutor, String plan, String comentarios, String fechaPrueba, String horaPrueba) {
            this.nombre = nombre;
            this.edad = edad;
            this.email = email;
            this.direccion = direccion;
            this.trabajaEstudia = trabajaEstudia;
            this.tutor = tutor;
            this.plan = plan;
            this.comentarios = comentarios;
            this.fechaPrueba = fechaPrueba;
            this.horaPrueba = horaPrueba;
        }

        // Getters (opcional - Thymeleaf puede acceder a campos públicos directamente)
        public String getNombre() { return nombre; }
        public Integer getEdad() { return edad; }
        public String getEmail() { return email; }
        public String getDireccion() { return direccion; }
        public String getTrabajaEstudia() { return trabajaEstudia; }
        public String getTutor() { return tutor; }
        public String getPlan() { return plan; }
        public String getComentarios() { return comentarios; }
        public String getFechaPrueba() { return fechaPrueba; }
        public String getHoraPrueba() { return horaPrueba; }
    }
}
