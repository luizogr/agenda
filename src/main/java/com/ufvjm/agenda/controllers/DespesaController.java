package com.ufvjm.agenda.controllers;

import com.ufvjm.agenda.dto.DespesaRequestDTO;
import com.ufvjm.agenda.entities.Despesa;
import com.ufvjm.agenda.service.DespesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/financas")
public class DespesaController {

    @Autowired
    private DespesaService despesaService;

    // 1. READ ALL & VIEW (Exibe a página principal de finanças)
    @GetMapping
    public String showFinancasPage(
            @RequestParam(value = "ano", required = false) Integer ano,
            @RequestParam(value = "mes", required = false) Integer mes,
            Model model) {

        // 1. Define a data de referência (padrão é o mês atual)
        LocalDate hoje = LocalDate.now();
        int anoBusca = (ano != null) ? ano : hoje.getYear();
        int mesBusca = (mes != null) ? mes : hoje.getMonthValue();

        // 2. Cria a data de busca e calcula a navegação
        LocalDate dataBusca = LocalDate.of(anoBusca, mesBusca, 1);
        LocalDate mesAnterior = dataBusca.minusMonths(1);
        LocalDate mesProximo = dataBusca.plusMonths(1);

        // 3. Filtra as despesas usando o novo metodo de serviço
        List<Despesa> despesasDoMes = despesaService.findByMonthAndYear(anoBusca, mesBusca);

        // 4. Recalcula o total gasto apenas com as despesas do mês
        Double totalGasto = despesaService.calculateTotalGasto(despesasDoMes);

        // --- Adiciona os dados para a View ---
        model.addAttribute("despesas", despesasDoMes);
        model.addAttribute("totalGasto", totalGasto);
        model.addAttribute("newDespesa", new DespesaRequestDTO(null, null, null, null));

        // Variáveis de Navegação
        model.addAttribute("anoAtual", anoBusca);
        model.addAttribute("mesAtualValor", mesBusca);
        model.addAttribute("mesAtualNome", dataBusca.format(DateTimeFormatter.ofPattern("MMMM yyyy")));
        model.addAttribute("anoAnterior", mesAnterior.getYear());
        model.addAttribute("mesAnteriorValor", mesAnterior.getMonthValue());
        model.addAttribute("anoProximo", mesProximo.getYear());
        model.addAttribute("mesProximoValor", mesProximo.getMonthValue());

        return "financas";
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