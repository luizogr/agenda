package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.dto.RegisterRequestDTO; // Usaremos o DTO de registro existente
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegisterController {

    @Autowired
    private UsuarioService usuarioService;

    // 1. Mapeia a URL para exibir a página de registro
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        // Adiciona um objeto vazio para o Thymeleaf preencher
        model.addAttribute("registerData", new RegisterRequestDTO(null, null, null));
        return "register"; // Retorna o nome da view: src/main/resources/templates/register.html
    }

    // 2. Mapeia o POST do formulário de registro
    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("registerData") RegisterRequestDTO registerRequest,
            RedirectAttributes redirectAttributes) {
        try {
            // Chamamos o serviço de criação de usuário (que criptografa a senha)
            // No modo Sessão, o usuário não é logado automaticamente após o cadastro, ele deve fazer login.
            Usuario newUsuario = usuarioService.register(registerRequest);

            // Mensagem de sucesso para exibir na tela de login
            redirectAttributes.addFlashAttribute("successMessage", "Cadastro realizado com sucesso! Faça seu login.");
            return "redirect:/login";

        } catch (RuntimeException e) {
            // Em caso de erro (ex: email já existe)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            // Retorna para o formulário de registro com a mensagem de erro
            return "redirect:/register";
        }
    }
}