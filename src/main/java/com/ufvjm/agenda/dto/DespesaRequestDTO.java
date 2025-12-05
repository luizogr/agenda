package com.ufvjm.agenda.dto;

import java.time.LocalDate;

public record DespesaRequestDTO(String nome, String descricao, LocalDate data, Double valor) {
}
