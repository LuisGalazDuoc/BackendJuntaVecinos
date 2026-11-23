package com.juntavecinos.auth.service;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.juntavecinos.auth.model.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey key;
    private final long duracionMs;

    public JwtService(@Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-minutes}") long minutos) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.duracionMs = minutos * 60 * 1000;
    }

    public Date nuevaExpiracion() {
        return new Date(System.currentTimeMillis() + duracionMs);
    }

    public String generateToken(Usuario usuario, Date expiracion) {
        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("uid", usuario.getId())
                .claim("nombre", usuario.getNombre())
                .claim("rol", usuario.getRol().getCodigo())
                .issuedAt(new Date())
                .expiration(expiracion)
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }
}
