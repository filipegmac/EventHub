package com.eventhub.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // Desabilita CSRF para APIs REST (não usamos forms).
                .authorizeHttpRequests(auth -> auth
                        // Permite acesso livre ao console H2 (para debug).
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html**", "/h2-console/**").permitAll()
                        // Para endpoints de eventos e participantes:
                        // - GET: Permitido para USER e ADMIN (leitura).
                        // - Outros (POST, PUT, DELETE): Apenas ADMIN (escrita).
                        .requestMatchers("/api/events/**", "/api/participants/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/events/**", "/api/participants/**").hasAnyRole("USER", "ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/events/**", "/api/participants/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/events/**", "/api/participants/**").hasRole("ADMIN")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/events/**", "/api/participants/**").hasRole("ADMIN")
                        // Qualquer outro request exige autenticação.
                        .anyRequest().authenticated()
                )
                .headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()))  // Permite frames para H2 console.
                .httpBasic(httpBasic -> {})  // Ativa Basic Auth (usuário/senha no header).
                .sessionManagement(session -> session.sessionCreationPolicy(STATELESS));  // Sem sessões stateful.

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Cria usuário "admin" com role ADMIN.
        UserDetails admin = User.withDefaultPasswordEncoder()  // Codificador simples (não use em prod!).
                .username("admin")
                .password("admin")
                .roles("ADMIN")
                .build();

        // Cria usuário "user" com role USER.
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("user")
                .roles("USER")
                .build();

        // Armazena os usuários em memória.
        return new InMemoryUserDetailsManager(admin, user);
    }
}