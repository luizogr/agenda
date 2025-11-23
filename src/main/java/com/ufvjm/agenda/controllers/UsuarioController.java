package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.dto.UserResponseDTO;
import com.ufvjm.agenda.dto.UserUpdateDTO;
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user") // Endpoint: /users
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping()
    public ResponseEntity<UserResponseDTO> getAuthenticatedUser() {
        Usuario usuario = usuarioService.getAuthenticatedUser();
        return ResponseEntity.ok(new UserResponseDTO(usuario));
    }

    @PutMapping("/atualizar")
    public ResponseEntity<UserResponseDTO> updateAuthenticatedUser(@RequestBody UserUpdateDTO request) {
        Usuario usuarioAtualizado = usuarioService.updateUser(
                request.nome(),
                request.email(),
                request.novaSenha()
        );
        return ResponseEntity.ok(new UserResponseDTO(usuarioAtualizado));
    }

    @DeleteMapping("/deletar")
    public ResponseEntity<Void> deleteAuthenticatedUser() {
        usuarioService.deleteUser();
        return ResponseEntity.noContent().build(); // Status 204 No Content
    }
}