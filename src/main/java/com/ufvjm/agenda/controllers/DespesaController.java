package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.dto.DespesaRequestDTO;
import com.ufvjm.agenda.entities.Despesa;
import com.ufvjm.agenda.service.DespesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/financas")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;

    // 1. READ ALL & VIEW (Exibe a página principal de finanças)
    @GetMapping
    public String showFinancasPage(Model model) {
        List<Despesa> despesas = despesaService.findAllByCurrentUser();
        Double totalGasto = despesaService.calculateTotalGasto(despesas);

        // Adiciona dados para o Thymeleaf usar
        model.addAttribute("despesas", despesas);
        model.addAttribute("totalGasto", totalGasto);
        model.addAttribute("newDespesa", new DespesaRequestDTO(null, null, null, null)); // Para o formulário de criação

        return "financas"; // Retorna src/main/resources/templates/financas.html
    }

    // 2. CREATE (Processa o formulário de criação)
    @PostMapping
    public String createDespesa(@ModelAttribute("newDespesa") DespesaRequestDTO request,
                                RedirectAttributes redirectAttributes) {
        try {
            despesaService.create(request);
            redirectAttributes.addFlashAttribute("successMessage", "Despesa adicionada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Erro ao adicionar despesa.");
        }
        return "redirect:/financas"; // Redireciona para a página de lista após o envio
    }

    // 3. DELETE
    @PostMapping("/delete/{id}")
    public String deleteDespesa(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            despesaService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Despesa removida.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/financas";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        Despesa despesa = despesaService.findById(id); // Já tem checagem de segurança

        // Converte a Entidade para DTO de Edição
        DespesaRequestDTO editDto = new DespesaRequestDTO(
                despesa.getNome(),
                despesa.getDescricao(),
                despesa.getData(),
                despesa.getValor()
        );

        model.addAttribute("despesaParaEditar", editDto);
        model.addAttribute("despesaId", id);

        return "financas-edit"; // Retorna a página de edição dedicada
    }

    // --- UPDATE (Processar Edição) ---
    @PostMapping("/update/{id}")
    public String updateDespesa(@PathVariable UUID id,
                                @ModelAttribute("despesaParaEditar") DespesaRequestDTO request,
                                RedirectAttributes redirectAttributes) {
        try {
            despesaService.update(id, request);
            redirectAttributes.addFlashAttribute("successMessage", "Despesa atualizada com sucesso!");
            return "redirect:/financas";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/financas/edit/" + id;
        }
    }
}