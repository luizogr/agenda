package com.ufvjm.agenda.service;

import com.ufvjm.agenda.dto.RegisterRequestDTO;
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private UUID getAuthenticatedUserId() {
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioLogado.getId();
    }

    public Usuario getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails)principal).getUsername();
        } else {
            email = principal.toString();
        }

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public Usuario updateUser(String nome, String email, String novaSenha) {
        Usuario usuario = getAuthenticatedUser();

        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
        }
        if (email != null && !email.isBlank()) {
            usuario.setEmail(email);
        }
        if (novaSenha != null && !novaSenha.isBlank()) {
            usuario.setSenha(passwordEncoder.encode(novaSenha));
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteUser() {
        UUID userId = getAuthenticatedUserId();
        usuarioRepository.deleteById(userId);
    }

    // UsuarioService.java (ADICIONAR ESTE MÉTODO)
    public Usuario register(RegisterRequestDTO body) {
        if (usuarioRepository.findByEmail(body.email()).isPresent()) {
            throw new RuntimeException("Este e-mail já está cadastrado.");
        }

        Usuario newUsuario = new Usuario();
        newUsuario.setEmail(body.email());
        newUsuario.setSenha(passwordEncoder.encode(body.senha())); // Criptografa a senha
        newUsuario.setNome(body.nome());

        return usuarioRepository.save(newUsuario);
    }
}