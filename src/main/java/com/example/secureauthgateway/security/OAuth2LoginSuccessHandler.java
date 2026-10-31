package com.example.secureauthgateway.security;

import com.example.secureauthgateway.entity.User;
import com.example.secureauthgateway.service.JwtService;
import com.example.secureauthgateway.service.OAuth2Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final OAuth2Service oauth2Service;
    private final JwtService jwtService;

    public OAuth2LoginSuccessHandler(
            OAuth2Service oauth2Service,
            JwtService jwtService) {
        this.oauth2Service = oauth2Service;
        this.jwtService = jwtService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();

        // Find or create local user
        User user = oauth2Service.processGoogleUser(oidcUser);

        // Generate application JWT
        String token = jwtService.generateToken(user.getEmail());

        // Return JWT response
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write("""
                {
                    "message": "Google login successful",
                    "accessToken": "%s",
                    "tokenType": "Bearer"
                }
                """.formatted(token));
    }
}