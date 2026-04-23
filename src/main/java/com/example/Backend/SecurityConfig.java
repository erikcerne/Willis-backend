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
        http.csrf(csrf -> csrf.disable()) // Inaktivera CSRF för API-tester
                .authorizeHttpRequests(auth -> auth.requestMatchers("/api/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll() // Öppna dessa
                        .anyRequest().authenticated() // Kräv inlogg för resten sen
                ).formLogin(form -> form.permitAll()); // Detta är den som skapar din /login sida

        return http.build();
    }
}