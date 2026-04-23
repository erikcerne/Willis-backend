package com.example.Backend;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable()) // Inaktivera CSRF för att kunna posta data
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll() // Detta öppnar ALLA urler (inga fler redirects till /login)
                );

        return http.build();
    }
}