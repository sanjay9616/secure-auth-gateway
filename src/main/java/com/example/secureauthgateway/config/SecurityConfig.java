
package com.example.secureauthgateway.config;

import com.example.secureauthgateway.security.AccessDeniedHandlerJwt;
import com.example.secureauthgateway.security.AuthEntryPointJwt;
import com.example.secureauthgateway.security.JwtAuthenticationFilter;
import com.example.secureauthgateway.security.OAuth2LoginSuccessHandler;
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
    private final AuthEntryPointJwt authEntryPointJwt;
    private final AccessDeniedHandlerJwt accessDeniedHandlerJwt;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;

    public SecurityConfig(CustomUserDetailsService userDetailsService,
        PasswordEncoder passwordEncoder,
        JwtAuthenticationFilter jwtAuthenticationFilter,
        AuthEntryPointJwt authEntryPointJwt,
        AccessDeniedHandlerJwt accessDeniedHandlerJwt,
        OAuth2LoginSuccessHandler oauth2LoginSuccessHandler) {
            this.userDetailsService = userDetailsService;
            this.passwordEncoder = passwordEncoder;
            this.jwtAuthenticationFilter = jwtAuthenticationFilter;
            this.authEntryPointJwt = authEntryPointJwt;
            this.accessDeniedHandlerJwt = accessDeniedHandlerJwt;
            this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
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
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authEntryPointJwt)
                .accessDeniedHandler(accessDeniedHandlerJwt)
            )
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    org.springframework.http.HttpMethod.POST,
                    "/api/auth/signup",
                    "/api/auth/login"
                ).permitAll()
                .requestMatchers("/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/users/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .successHandler(oauth2LoginSuccessHandler)
            )

            .addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

}
