package com.example.secureauthgateway.repository;

import com.example.secureauthgateway.entity.OAuthAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface OAuthAccountRepository
        extends JpaRepository<OAuthAccount, Long> {

    Optional<OAuthAccount> findByProviderAndProviderUserId(
            String provider,
            String providerUserId);

    boolean existsByProviderAndProviderUserId(
            String provider,
            String providerUserId);

    @Query("""
            SELECT oa FROM OAuthAccount oa
            JOIN FETCH oa.user
            WHERE oa.provider = :provider
            AND oa.providerUserId = :providerUserId
            """)
    Optional<OAuthAccount> findByProviderAndProviderUserIdWithUser(
            @Param("provider") String provider,
            @Param("providerUserId") String providerUserId);
}