package com.ufvjm.agenda.DTO;

import com.ufvjm.agenda.entities.Usuario;

import java.util.UUID;

public record UserResponseDTO (UUID id, String nome, String email){
    public UserResponseDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
