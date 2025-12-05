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

import java.util.List;
import java.util.UUID;

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
        return despesaRepository.findByUsuarioId(usuarioLogado.getId());
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
        return despesas.stream()
                .mapToDouble(Despesa::getValor)
                .sum();
    }
}