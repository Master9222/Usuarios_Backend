package com.vahor.Usuarios_Backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GlobalErrorController {

    @GetMapping("/error")
    public String handleError(Model model) {
        model.addAttribute("error", "Ocurrió un error. Intenta nuevamente más tarde.");
        return "error/generic"; // Renderiza una vista genérica
    }
}
