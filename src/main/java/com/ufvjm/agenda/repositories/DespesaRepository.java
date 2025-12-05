package com.ufvjm.agenda.repositories;

import com.ufvjm.agenda.entities.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface DespesaRepository extends JpaRepository<Despesa, UUID> {
    List<Despesa> findByUsuarioId(UUID usuarioId);

    List<Despesa> findByUsuarioIdAndData(UUID usuarioId, LocalDate data);
}
