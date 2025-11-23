package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.dto.AgendaRequestDTO;
import com.ufvjm.agenda.dto.AgendaResponseDTO;
import com.ufvjm.agenda.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/agenda")
public class AgendaController {

    @Autowired
    private AgendaService agendaService;

    @PostMapping(name = "/criar")
    public ResponseEntity<AgendaResponseDTO> createAgenda(@RequestBody AgendaRequestDTO request) {
        var newAgenda = agendaService.create(request);
        return ResponseEntity.ok(new AgendaResponseDTO(newAgenda));
    }

    @GetMapping(name = "/listar")
    public ResponseEntity<List<AgendaResponseDTO>> getAllAgendas() {
        var agendas = agendaService.findAllByUsuario();

        List<AgendaResponseDTO> response = agendas.stream()
                .map(AgendaResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AgendaResponseDTO> getAgendaById(@PathVariable UUID id) {
        var agenda = agendaService.findById(id);
        return ResponseEntity.ok(new AgendaResponseDTO(agenda));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<AgendaResponseDTO> updateAgenda(@PathVariable UUID id, @RequestBody AgendaRequestDTO request) {
        var updatedAgenda = agendaService.update(id, request);
        return ResponseEntity.ok(new AgendaResponseDTO(updatedAgenda));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Void> deleteAgenda(@PathVariable UUID id) {
        agendaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
