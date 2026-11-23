package com.juntavecinos.reserva.config;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.WebClient;

import com.nimbusds.jose.jwk.source.ImmutableSecret;

/** WebClient para rutas /interno de otros microservicios. Cada llamada lleva un JWT corto con rol SERVICIO. */
@Configuration
public class WebClientConfig {

    @Bean
    public WebClient webClient(@Value("${SECRET}") String secret,
            @Value("${spring.application.name}") String servicio) {
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(
                new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256")));
        return WebClient.builder()
                .filter((peticion, siguiente) -> siguiente.exchange(ClientRequest.from(peticion)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token(encoder, servicio))
                        .build()))
                .build();
    }

    private String token(JwtEncoder encoder, String servicio) {
        Instant ahora = Instant.now();
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(servicio)
                .claim("rol", "SERVICIO")
                .issuedAt(ahora)
                .expiresAt(ahora.plusSeconds(60))
                .build();
        return encoder.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
                .getTokenValue();
    }
}
