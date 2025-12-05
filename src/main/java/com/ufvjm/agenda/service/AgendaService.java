package com.ufvjm.agenda.service;

import com.ufvjm.agenda.dto.AgendaRequestDTO;
import com.ufvjm.agenda.entities.Agenda;
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.repositories.AgendaRepository;
import com.ufvjm.agenda.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AgendaService {

    @Autowired
    private AgendaRepository agendaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Usuario getAuthenticatedUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String email;

        if (principal instanceof UserDetails) {
            email = ((UserDetails)principal).getUsername();
        } else {
            email = principal.toString();
        }

        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));
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

    public List<Agenda> findTodayByUsuario() {
        Usuario user = getAuthenticatedUser();

        // Define o intervalo: Hoje 00:00:00 até Hoje 23:59:59
        java.time.LocalDateTime inicioDia = java.time.LocalDate.now().atStartOfDay();
        java.time.LocalDateTime fimDia = java.time.LocalDate.now().atTime(java.time.LocalTime.MAX);

        return agendaRepository.findByUsuarioIdAndDataBetween(user.getId(), inicioDia, fimDia);
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

    public List<Agenda> findTasksForCurrentWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime sevenDaysFromNow = now.plusDays(7);

        // Busca todas as tarefas do usuário e filtra por data
        return findAllByUsuario().stream()
                .filter(agenda -> agenda.getData().isAfter(now.minusMinutes(1)) && agenda.getData().isBefore(sevenDaysFromNow))
                .sorted(Comparator.comparing(Agenda::getData)) // Ordena por data
                .collect(Collectors.toList());
    }
}
