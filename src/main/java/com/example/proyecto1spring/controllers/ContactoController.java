package com.example.proyecto1spring.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ContactoController {
    @GetMapping("/contacto")
    public String contacto() {
        return "contacto";
    }
}
