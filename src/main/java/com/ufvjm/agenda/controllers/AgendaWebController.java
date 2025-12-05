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

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/agenda")
public class AgendaWebController {

    @Autowired
    private AgendaService agendaService;

    // READ ALL & VIEW (Rota principal /agenda)
    @GetMapping
    public String showAgenda(Model model) {
        // Busca apenas a lista de compromissos do usuário logado
        List<Agenda> compromissos = agendaService.findAllByUsuario();

        model.addAttribute("compromissos", compromissos);
        model.addAttribute("newCompromisso", new AgendaRequestDTO(null, null, null, null));
        model.addAttribute("statusValues", StatusAgenda.values()); // Para o dropdown de status

        return "agenda"; // Vai renderizar agenda.html
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