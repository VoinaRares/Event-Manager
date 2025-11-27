package com.example.event_manager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())  // Pentru API + Angular
            .cors(cors -> {})              // Permite frontend acces
            .authorizeHttpRequests(auth -> auth

                // 🔓 Public frontend
                .requestMatchers(
                        "/",
                        "/index.html",
                        "/assets/**",
                        "/images/**",
                        "/favicon.ico"
                ).permitAll()

                // 🔓 Public API endpoints
                .requestMatchers(
                        "/api/auth/**",          // login + signup
                        "/api/invite/**",        // accept/decline invite
                        "/api/public/**"         // orice endpoint public
                ).permitAll()

                // 🔐 Admin/Organizer area
                .requestMatchers("/api/admin/**")
                        .hasRole("ORGANIZER")

                // 🔐 User authenticated area
                .requestMatchers("/api/user/**")
                        .authenticated()

                // restul cererilor → trebuie login
                .anyRequest().authenticated()
            )
            .logout(logout -> logout
                    .logoutRequestMatcher(new AntPathRequestMatcher("/api/auth/logout"))
                    .logoutSuccessUrl("/")
                    .permitAll()
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
