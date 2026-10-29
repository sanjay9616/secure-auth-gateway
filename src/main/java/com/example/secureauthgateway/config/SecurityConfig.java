
package com.example.secureauthgateway.config;

import com.example.secureauthgateway.security.JwtAuthenticationFilter;
import com.example.secureauthgateway.service.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder,
        JwtAuthenticationFilter jwtAuthenticationFilter) {
            this.userDetailsService = userDetailsService;
            this.passwordEncoder = passwordEncoder;
            this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    org.springframework.http.HttpMethod.POST,
                    "/api/auth/signup",
                    "/api/auth/login"
                ).permitAll()
                .requestMatchers("/users/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

}
