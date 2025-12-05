package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.dto.AgendaRequestDTO;
import com.ufvjm.agenda.entities.Agenda;
import com.ufvjm.agenda.entities.enums.StatusAgenda;
import com.ufvjm.agenda.service.AgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/agenda")
public class AgendaWebController {

    @Autowired
    private AgendaService agendaService;

    // READ ALL & VIEW (Rota principal /agenda)
    @GetMapping
    public String showAgenda(
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "mes", required = false) Integer mes,
            Model model) {

        // 1. Define a data de referência (padrão é o mês atual)
        LocalDate hoje = LocalDate.now();
        int anoBusca = (ano != null) ? ano : hoje.getYear();
        int mesBusca = (mes != null) ? mes : hoje.getMonthValue();

        // --- CÁLCULO DE NAVEGAÇÃO PARA PRÓXIMO E ANTERIOR ---

        // 2. Cria uma data de referência para o mês atual de busca
        LocalDate dataBusca = LocalDate.of(anoBusca, mesBusca, 1);

        // Calcula o mês e ano anterior/próximo
        LocalDate mesAnterior = dataBusca.minusMonths(1);
        LocalDate mesProximo = dataBusca.plusMonths(1);

        // 3. Filtra os compromissos usando o novo método de serviço
        List<Agenda> compromissosDoMes = agendaService.findByMonthAndYear(anoBusca, mesBusca);

        // --- Lógica do Calendário (Para a grade de 42 dias) ---
        LocalDate primeiroDiaVisivel = dataBusca.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
        List<LocalDate> diasDoCalendario = new java.util.ArrayList<>();
        for (int i = 0; i < 42; i++) {
            diasDoCalendario.add(primeiroDiaVisivel.plusDays(i));
        }

        // --- Adiciona os dados para a View ---
        model.addAttribute("compromissos", compromissosDoMes);
        model.addAttribute("newCompromisso", new AgendaRequestDTO(null, null, null, null));
        model.addAttribute("statusValues", StatusAgenda.values());

        // Variáveis de Navegação
        model.addAttribute("anoAtual", anoBusca);
        model.addAttribute("mesAtualValor", mesBusca);
        model.addAttribute("mesAtualNome", dataBusca.format(DateTimeFormatter.ofPattern("MMMM yyyy")));

        // Variáveis para os botões Anterior/Próximo
        model.addAttribute("anoAnterior", mesAnterior.getYear());
        model.addAttribute("mesAnteriorValor", mesAnterior.getMonthValue());
        model.addAttribute("anoProximo", mesProximo.getYear());
        model.addAttribute("mesProximoValor", mesProximo.getMonthValue());

        // Variáveis para o Calendário (Grade)
        model.addAttribute("diasDoCalendario", diasDoCalendario);
        model.addAttribute("mesDeReferencia", mesBusca);

        return "agenda";
    }

    // CREATE (Rota POST /agenda)
    @PostMapping
    public String createCompromisso(@ModelAttribute("newCompromisso") AgendaRequestDTO request,
                                    RedirectAttributes redirectAttributes) {
        try {
            agendaService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Tarefa adicionada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar tarefa.");
        }
        return "redirect:/agenda";
    }

    // DELETE (Rota POST /agenda/delete/{id})
    @PostMapping("/delete/{id}")
    public String deleteCompromisso(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            agendaService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tarefa removida com sucesso!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/agenda";
    }

    // REDIRECIONAR PARA EDIÇÃO
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Agenda compromisso = agendaService.findById(id);

        AgendaRequestDTO editDto = new AgendaRequestDTO(
                compromisso.getNome(),
                compromisso.getDescricao(),
                compromisso.getData(),
                compromisso.getStatusAgenda()
        );

        model.addAttribute("compromissoParaEditar", editDto);
        model.addAttribute("compromissoId", id);
        model.addAttribute("statusValues", StatusAgenda.values());

        return "agenda-edit";
    }

    // PROCESSAR EDIÇÃO
    @PostMapping("/update/{id}")
    public String updateCompromisso(@PathVariable UUID id,
                                    @ModelAttribute("compromissoParaEditar") AgendaRequestDTO request,
                                    RedirectAttributes redirectAttributes) {
        try {
            agendaService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Tarefa atualizada com sucesso!");
            return "redirect:/agenda";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/agenda/edit/" + id;
        }
    }
}