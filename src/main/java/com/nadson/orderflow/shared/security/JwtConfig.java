package com.nadson.orderflow.shared.security;

import java.nio.charset.StandardCharsets;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

@Configuration
public class JwtConfig {
    private static final int MIN_SECRET_LENGTH = 32;

    @Bean
    public JwtDecoder jwtDecoder(@Value("${app.jwt.secret}") String jwtSecret) {
        return NimbusJwtDecoder.withSecretKey(secretKey(jwtSecret))
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource(@Value("${app.jwt.secret}") String jwtSecret) {
        SecretKey key = secretKey(jwtSecret);
        OctetSequenceKey jwk = new OctetSequenceKey.Builder(key.getEncoded())
                .algorithm(com.nimbusds.jose.JWSAlgorithm.HS256)
                .build();
        return (jwkSelector, context) -> jwkSelector.select(new JWKSet(jwk));
    }

    @Bean
    public JwtEncoder jwtEncoder(JWKSource<SecurityContext> jwkSource) {
        return new NimbusJwtEncoder(jwkSource);
    }

    private SecretKey secretKey(String jwtSecret) {
        if (jwtSecret == null || jwtSecret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("app.jwt.secret must have at least 32 characters");
        }
        return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}