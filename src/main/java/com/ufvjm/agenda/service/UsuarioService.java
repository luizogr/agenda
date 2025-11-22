package com.ufvjm.agenda.service;

import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
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
        // O SecurityContextHolder armazena o objeto Usuario que foi colocado lá pelo SecurityFilter
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return usuarioLogado.getId();
    }

    public Usuario getAuthenticatedUser() {
        UUID userId = getAuthenticatedUserId();
        return usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
    }

    public Usuario updateUser(String nome, String email, String novaSenha) {
        Usuario usuario = getAuthenticatedUser();

        // Atualiza campos
        if (nome != null && !nome.isBlank()) {
            usuario.setNome(nome);
        }
        if (email != null && !email.isBlank()) {
            usuario.setEmail(email);
        }

        // Trata a nova senha, se fornecida
        if (novaSenha != null && !novaSenha.isBlank()) {
            // Criptografa a nova senha antes de salvar
            usuario.setSenha(passwordEncoder.encode(novaSenha));
        }

        return usuarioRepository.save(usuario);
    }

    public void deleteUser() {
        UUID userId = getAuthenticatedUserId();
        usuarioRepository.deleteById(userId);
    }
}