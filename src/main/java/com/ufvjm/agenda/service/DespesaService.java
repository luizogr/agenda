package com.ufvjm.agenda.service;

import com.ufvjm.agenda.dto.DespesaRequestDTO;
import com.ufvjm.agenda.entities.Despesa;
import com.ufvjm.agenda.entities.Usuario;
import com.ufvjm.agenda.repositories.DespesaRepository;
import com.ufvjm.agenda.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class DespesaService {

    @Autowired
    private DespesaRepository despesaRepository;

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

    // CREATE
    public Despesa create(DespesaRequestDTO dto) {
        Usuario usuarioLogado = getAuthenticatedUser();

        Despesa novaDespesa = new Despesa();
        novaDespesa.setNome(dto.nome());
        novaDespesa.setDescricao(dto.descricao());
        novaDespesa.setData(dto.data());
        novaDespesa.setValor(dto.valor());
        novaDespesa.setUsuario(usuarioLogado); // Vínculo obrigatório

        return despesaRepository.save(novaDespesa);
    }

    // READ ALL
    public List<Despesa> findAllByCurrentUser() {
        Usuario usuarioLogado = getAuthenticatedUser();

        // 1. Busca todos os itens
        List<Despesa> despesas = despesaRepository.findByUsuarioId(usuarioLogado.getId());

        // 2. Ordena a lista pela data (do mais antigo para o mais recente)
        despesas.sort(Comparator.comparing(Despesa::getData));

        return despesas;
    }

    // READ ONE e CHECK SECURITY
    public Despesa findById(UUID id) {
        UUID userId = getAuthenticatedUser().getId();

        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Despesa não encontrada."));

        // SEGURANÇA: Garante que a despesa pertence ao usuário logado
        if (!despesa.getUsuario().getId().equals(userId)) {
            throw new RuntimeException("Acesso negado. Esta despesa não pertence a este usuário.");
        }
        return despesa;
    }

    public List<Despesa> findTodayByCurrentUser() {
        Usuario user = getAuthenticatedUser();
        // Busca pela data de hoje
        return despesaRepository.findByUsuarioIdAndData(user.getId(), java.time.LocalDate.now());
    }

    // UPDATE
    public Despesa update(UUID id, DespesaRequestDTO dto) {
        Despesa despesa = findById(id); // Já verifica a segurança

        despesa.setNome(dto.nome());
        despesa.setDescricao(dto.descricao());
        despesa.setData(dto.data());
        despesa.setValor(dto.valor());

        return despesaRepository.save(despesa);
    }

    // DELETE
    public void delete(UUID id) {
        Despesa despesa = findById(id); // Já verifica a segurança
        despesaRepository.delete(despesa);
    }

    // CÁLCULO DE BALANÇO MENSAL (Recupera todos e faz a soma)
    public Double calculateTotalGasto(List<Despesa> despesas) {
        // Retorna a soma de todos os campos 'valor' na lista
        return despesas.stream()
                .mapToDouble(Despesa::getValor)
                .sum();
    }

    public List<Despesa> findByMonthAndYear(int ano, int mes) {
        // 1. Define o primeiro dia e o último dia do mês/ano
        LocalDate primeiroDia = LocalDate.of(ano, mes, 1);
        LocalDate ultimoDia = primeiroDia.with(TemporalAdjusters.lastDayOfMonth());

        // 2. Busca todas as despesas do usuário e filtra no Java
        return findAllByCurrentUser().stream()
                .filter(despesa -> !despesa.getData().isBefore(primeiroDia) && !despesa.getData().isAfter(ultimoDia))
                .collect(Collectors.toList());
    }


}