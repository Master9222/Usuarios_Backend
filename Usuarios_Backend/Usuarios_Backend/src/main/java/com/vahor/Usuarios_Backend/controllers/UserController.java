package com.vahor.Usuarios_Backend.controllers;

import com.vahor.Usuarios_Backend.entities.User;
import com.vahor.Usuarios_Backend.models.UserRequest;
import com.vahor.Usuarios_Backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService service;

    // Listar todos los usuarios
    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", service.findAll());
        return "users/list"; // Renderiza la plantilla list.html
    }

    // Mostrar formulario para crear un usuario
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("user", new User()); // Objeto vacío para el formulario
        return "users/create"; // Renderiza la plantilla create.html
    }

    // Crear un usuario
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("user") User user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "users/create"; // Si hay errores, vuelve al formulario
        }
        service.save(user);
        return "redirect:/users"; // Redirige a la lista de usuarios
    }

    // Mostrar formulario para editar un usuario
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        User user = service.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));

        model.addAttribute("user", user);
        model.addAttribute("admin", user.isAdmin()); // Añadir si es administrador para el formulario
        return "users/edit"; // Renderiza la plantilla edit.html
    }

    // Actualizar un usuario
    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id, @Valid @ModelAttribute("user") UserRequest userRequest,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("admin", userRequest.isAdmin());
            return "users/edit"; // Si hay errores, vuelve al formulario de edición
        }

        service.update(userRequest, id);
        return "redirect:/users"; // Redirige a la lista de usuarios
    }

    // Eliminar un usuario
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/users"; // Redirige a la lista de usuarios tras eliminar
    }
}
