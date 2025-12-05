package com.ufvjm.agenda.dto;

import com.ufvjm.agenda.entities.Despesa;

import java.time.LocalDate;
import java.util.UUID;

public record DespesaResponseDTO(UUID id, String nome, String descricao, LocalDate data, Double valor, UUID usuarioId) {
    public DespesaResponseDTO(Despesa despesa) {
        this(despesa.getId(), despesa.getNome(), despesa.getDescricao(),
                despesa.getData(), despesa.getValor(), despesa.getUsuario().getId());
    }
}
