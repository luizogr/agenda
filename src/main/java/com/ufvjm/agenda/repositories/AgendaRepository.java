package com.ufvjm.agenda.repositories;

import com.ufvjm.agenda.entities.Agenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AgendaRepository extends JpaRepository<Agenda, UUID> {
    List<Agenda> findByUsuarioId(UUID usuarioId);

    List<Agenda> findByUsuarioIdAndDataBetween(UUID usuarioId, LocalDateTime inicio, LocalDateTime fim);
}
