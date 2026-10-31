package com.example.secureauthgateway.service;

import com.example.secureauthgateway.entity.OAuthAccount;
import com.example.secureauthgateway.entity.Role;
import com.example.secureauthgateway.entity.User;
import com.example.secureauthgateway.repository.OAuthAccountRepository;
import com.example.secureauthgateway.repository.UserRepository;

import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class OAuth2Service {

    private final OAuthAccountRepository oauthAccountRepository;
    private final UserRepository userRepository;

    public OAuth2Service(
            OAuthAccountRepository oauthAccountRepository,
            UserRepository userRepository) {
        this.oauthAccountRepository = oauthAccountRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public User processGoogleUser(OidcUser oidcUser) {

        String provider = "GOOGLE";
        String providerUserId = oidcUser.getSubject();
        String email = oidcUser.getEmail();

        Optional<OAuthAccount> existingAccount = oauthAccountRepository.findByProviderAndProviderUserIdWithUser(
                provider, providerUserId);

        if (existingAccount.isPresent()) {
            return existingAccount.get().getUser();
        }

        return registerGoogleUser(
                provider, providerUserId, email, oidcUser);
    }

    private User registerGoogleUser(
            String provider,
            String providerUserId,
            String email,
            OidcUser oidcUser) {

        if (email == null || email.isBlank()
                || !Boolean.TRUE.equals(oidcUser.getEmailVerified())) {
            throw new IllegalArgumentException(
                    "Google email must be present and verified");
        }

        String normalizedEmail = email.trim().toLowerCase();

        // Do not automatically link an existing password account
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists. "
                            + "Please sign in using your existing login method.");
        }

        // Create local user
        User user = new User();
        user.setEmail(normalizedEmail);
        user.setPassword(null);
        user.setRole(Role.USER);
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        // Link Google account to local user
        OAuthAccount oauthAccount = new OAuthAccount();
        oauthAccount.setProvider(provider);
        oauthAccount.setProviderUserId(providerUserId);
        oauthAccount.setProviderEmail(normalizedEmail);
        oauthAccount.setUser(savedUser);

        oauthAccountRepository.save(oauthAccount);

        return savedUser;
    }
}