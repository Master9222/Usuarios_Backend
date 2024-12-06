package com.vahor.Usuarios_Backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/403")
    public String accessDeniedPage(Model model) {
        model.addAttribute("error", "No tienes permiso para acceder a este recurso.");
        return "error/403"; // Renderiza la vista 403.html
    }
}
