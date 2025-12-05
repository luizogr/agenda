package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.entities.Agenda;
import com.ufvjm.agenda.entities.Despesa;
import com.ufvjm.agenda.service.AgendaService;
import com.ufvjm.agenda.service.DespesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Controller
public class HomeController {

    @Autowired
    private AgendaService agendaService;

    @Autowired
    private DespesaService despesaService;

    @GetMapping("/home") // Nova rota principal
    public String dashboard(Model model) {
        // Busca dados de HOJE
        List<Agenda> tarefasHoje = agendaService.findTodayByUsuario();
        List<Despesa> despesasHoje = despesaService.findTodayByCurrentUser();

        // Calcula total gasto hoje
        Double totalGastoHoje = despesasHoje.stream().mapToDouble(Despesa::getValor).sum();

        // Formata a data para exibir bonito na tela (Ex: 03/12/2025)
        String dataHoje = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

        model.addAttribute("tarefasHoje", tarefasHoje);
        model.addAttribute("despesasHoje", despesasHoje);
        model.addAttribute("totalGastoHoje", totalGastoHoje);
        model.addAttribute("dataHoje", dataHoje);

        return "home";
    }
}