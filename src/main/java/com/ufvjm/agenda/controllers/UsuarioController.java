package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.DTO.UserResponseDTO;
import com.ufvjm.agenda.DTO.UserUpdateDTO;
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users") // Endpoint: /users
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    // GET - READ (Buscar informações do usuário logado)
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getAuthenticatedUser() {
        Usuario usuario = usuarioService.getAuthenticatedUser();
        return ResponseEntity.ok(new UserResponseDTO(usuario));
    }

    // PUT - UPDATE (Atualizar informações do usuário logado)
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateAuthenticatedUser(@RequestBody UserUpdateDTO request) {
        Usuario usuarioAtualizado = usuarioService.updateUser(
                request.nome(),
                request.email(),
                request.novaSenha()
        );
        return ResponseEntity.ok(new UserResponseDTO(usuarioAtualizado));
    }

    // DELETE - DELETE (Excluir a conta do usuário logado)
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteAuthenticatedUser() {
        usuarioService.deleteUser();
        return ResponseEntity.noContent().build(); // Status 204 No Content
    }
}