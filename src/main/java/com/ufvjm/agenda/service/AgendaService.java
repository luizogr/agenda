package com.ufvjm.agenda.service;

import com.ufvjm.agenda.dto.AgendaRequestDTO;
import com.ufvjm.agenda.entities.Agenda;
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.repositories.AgendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    private Usuario getAuthenticatedUser() {
        Usuario user = (Usuario) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user;
    }

    public Agenda create(AgendaRequestDTO dto){
        Usuario user = getAuthenticatedUser();

        Agenda agenda = new Agenda();
        agenda.setNome(dto.nome());
        agenda.setDescricao(dto.descricao());
        agenda.setData(dto.data());
        agenda.setStatusAgenda(dto.status());
        agenda.setUsuario(user);

        return agendaRepository.save(agenda);
    }

    public List<Agenda> findAllByUsuario(){
        Usuario user = getAuthenticatedUser();
        return agendaRepository.findByUsuarioId(user.getId());
    }

    public Agenda findById(UUID id) {
        UUID userId = getAuthenticatedUser().getId();

        Agenda agenda = agendaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Compromisso não encontrado."));

        if (!agenda.getUsuario().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado. Este compromisso não pertence a este usuário.");
        }
        return agenda;
    }

    public Agenda update(UUID id, AgendaRequestDTO dto) {
        Agenda agenda = this.findById(id);

        agenda.setNome(dto.nome());
        agenda.setDescricao(dto.descricao());
        agenda.setData(dto.data());
        agenda.setStatusAgenda(dto.status());

        return agendaRepository.save(agenda);
    }

    public void delete(UUID id) {
        Agenda agenda = this.findById(id);
        agendaRepository.delete(agenda);
    }
}
