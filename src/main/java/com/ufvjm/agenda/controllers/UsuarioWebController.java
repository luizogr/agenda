package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.dto.UserUpdateDTO;
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users/me") // Rota para o perfil do usuário logado
public class UsuarioWebController {

    @Autowired
    private UsuarioService usuarioService;

    // 1. Exibir a tela de Perfil (READ)
    @GetMapping
    public String showProfile(Model model) {
        Usuario usuario = usuarioService.getAuthenticatedUser();

        // Passa os dados atuais para o formulário de edição
        UserUpdateDTO currentData = new UserUpdateDTO(usuario.getNome(), usuario.getEmail(), null);

        model.addAttribute("userData", currentData);
        model.addAttribute("currentEmail", usuario.getEmail()); // Email é apenas leitura

        return "profile"; // Retorna src/main/resources/templates/profile.html
    }

    // 2. Processar a Edição (UPDATE)
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("userData") UserUpdateDTO request,
                                RedirectAttributes redirectAttributes) {
        try {
            // Apenas o nome será realmente usado, os outros campos serão ignorados ou nulos no request
            usuarioService.updateUser(request.nome(), null, null);

            redirectAttributes.addFlashAttribute("successMessage", "Perfil atualizado com sucesso!");
            return "redirect:/users/me";

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao atualizar: " + e.getMessage());
            return "redirect:/users/me";
        }
    }

    // 3. Processar a Exclusão (DELETE)
    @PostMapping("/delete")
    public String deleteProfile(RedirectAttributes redirectAttributes) {
        try {
            usuarioService.deleteUser();

            // O Spring Security encerrará a sessão automaticamente ao deletar o usuário.
            redirectAttributes.addFlashAttribute("logoutMessage", "Sua conta foi excluída com sucesso.");
            return "redirect:/login"; // Redireciona para a tela de login

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao excluir: " + e.getMessage());
            return "redirect:/users/me";
        }
    }
}