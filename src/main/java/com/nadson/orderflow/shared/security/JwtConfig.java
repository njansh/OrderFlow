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

import com.nimbusds.jose.jwk.source.ImmutableSecret;

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
    public JwtEncoder jwtEncoder(@Value("${app.jwt.secret}") String jwtSecret) {
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey(jwtSecret)));
    }

    private SecretKey secretKey(String jwtSecret) {
        if (jwtSecret == null || jwtSecret.length() < MIN_SECRET_LENGTH) {
            throw new IllegalStateException("app.jwt.secret must have at least 32 characters");
        }

        return new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
