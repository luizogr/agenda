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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
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

        List<Agenda> agendas = agendaRepository.findByUsuarioId(user.getId());

        agendas.sort(Comparator.comparing(Agenda::getData)); // Ordena por data

        return agendas;
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

    public List<Agenda> findByMonthAndYear(int ano, int mes) {
        // 1. Define o primeiro dia do mês/ano
        LocalDateTime primeiroDia = LocalDate.of(ano, mes, 1).atStartOfDay();

        // 2. Define o último dia do mês (usando TemporalAdjusters)
        LocalDateTime ultimoDia = primeiroDia.with(TemporalAdjusters.lastDayOfMonth()).withHour(23).withMinute(59).withSecond(59);

        // 3. Busca todos os compromissos do usuário e filtra no Java (operação rápida)
        return findAllByUsuario().stream()
                .filter(agenda -> agenda.getData().isAfter(primeiroDia.minusNanos(1)) && agenda.getData().isBefore(ultimoDia.plusNanos(1)))
                .sorted(Comparator.comparing(Agenda::getData)) // Garante a ordenação
                .collect(Collectors.toList());
    }
}
