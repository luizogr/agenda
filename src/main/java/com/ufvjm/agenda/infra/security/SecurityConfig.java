package com.ufvjm.agenda.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //@Autowired
    //private CustomUserDetailsService userDetailsService;

    //@Autowired
    //SecurityFilter securityFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Configura a proteção CSRF (necessário para formulários em Sessão)
                .csrf(csrf -> csrf.disable()) // Deixei desabilitado por enquanto para facilitar testes, mas em produção deve ser habilitado.

                // 2. Remove a política stateless do JWT (a sessão é ativada por padrão, 'IF_REQUIRED')
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

                .authorizeHttpRequests(authorize -> authorize
                        // 3. Permite acesso público às rotas de login e registro (e arquivos estáticos)
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll() // Manter POST /auth/register para API de criação (pode ser migrada)
                        .requestMatchers("/login", "/register", "/css/**", "/js/**", "/images/**").permitAll() // Rotas para as páginas e arquivos estáticos

                        // 4. Todas as outras requisições devem ser autenticadas
                        .anyRequest().authenticated()
                )

                // 5. Configura o Login via Formulário
                .formLogin(form -> form
                        .loginPage("/login")         // URL para a página de login customizada (que vamos criar)
                        .loginProcessingUrl("/login")// O Spring intercepta este POST para processar o login
                        .defaultSuccessUrl("/home", true)
                        //.defaultSuccessUrl("/agenda", true) // Redireciona para /agenda após login de sucesso
                        .failureUrl("/login?error=true") // Redireciona para /login em caso de falha
                        .permitAll() // Permite que todos acessem o formulário
                )

                // 6. Configura o Logout
                .logout(logout -> logout
                        .logoutUrl("/logout") // URL que o Spring deve interceptar para finalizar a sessão
                        .logoutSuccessUrl("/login?logout=true") // Redireciona após logout
                        .permitAll()
                );

        // REMOVER: .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
