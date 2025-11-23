package com.ufvjm.agenda.dto;

import com.ufvjm.agenda.entities.enums.StatusAgenda;

import java.time.LocalDateTime;

public record AgendaRequestDTO(String nome, String descricao, LocalDateTime data, StatusAgenda status) {
}
