package com.vahor.Usuarios_Backend.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class AuthController {

    // Página de login
    @GetMapping("/login")
    public String loginPage() {
        return "login"; // Renderiza login.html
    }

    // Dashboard para usuarios autenticados
    @GetMapping("/dashboard")
    public String dashboardPage(Model model, Principal principal, Authentication authentication) {
        model.addAttribute("username", principal.getName()); // Añade el nombre del usuario autenticado

        // Extraer y añadir los roles del usuario autenticado
        model.addAttribute("roles", authentication.getAuthorities());

        // Debug opcional: Imprime los roles en la consola (puedes eliminar esto después
        // de verificar)
        authentication.getAuthorities().forEach(role -> System.out.println("Rol asignado: " + role.getAuthority()));

        return "dashboard"; // Renderiza dashboard.html
    }

    // Página de administración (solo para administradores)
    @GetMapping("/admin")
    public String adminPage(Model model, Authentication authentication) {
        // Verificar si el usuario tiene el rol "ADMIN"
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(role -> role.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin) {
            return "redirect:/access-denied"; // Redirige a la página de acceso denegado
        }

        model.addAttribute("username", authentication.getName()); // Añade el nombre del usuario
        return "admin"; // Renderiza admin.html
    }

    // Página de acceso denegado
    @GetMapping("/access-denied")
    public String accessDeniedPage() {
        return "access-denied"; // Renderiza access-denied.html
    }
}
