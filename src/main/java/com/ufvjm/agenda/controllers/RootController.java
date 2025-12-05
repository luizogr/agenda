package com.ufvjm.agenda.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController {

    @GetMapping("/")
    public String redirectToAgenda() {
        /*
         * Este método é chamado quando o usuário acessa https://chronos.squareweb.app/
         * * 1. SE o usuário NÃO ESTIVER AUTENTICADO:
         * O Spring Security intercepta esta requisição (devido ao .anyRequest().authenticated())
         * e força o redirecionamento para a página de login (/login).
         * * 2. SE o usuário ESTIVER AUTENTICADO:
         * O Spring Security permite que ele continue, e o redirecionamos para o dashboard (/agenda).
         */

        return "redirect:/agenda";
    }
}