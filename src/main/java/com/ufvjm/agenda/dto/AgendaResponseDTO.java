package com.ufvjm.agenda.dto;

import com.ufvjm.agenda.entities.enums.StatusAgenda;

import java.time.LocalDateTime;
import java.util.UUID;

public record AgendaResponseDTO (UUID id, String nome, String descricao, LocalDateTime data, StatusAgenda status, UUID usuarioId){
    public AgendaResponseDTO(com.ufvjm.agenda.entities.Agenda agenda) {
        this(agenda.getId(), agenda.getNome(), agenda.getDescricao(),
                agenda.getData(), agenda.getStatusAgenda(), agenda.getUsuario().getId());
    }
}
