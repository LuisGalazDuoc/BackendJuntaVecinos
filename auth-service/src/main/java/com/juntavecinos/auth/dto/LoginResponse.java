package com.juntavecinos.auth.dto;

/** expiraEn: fecha de vencimiento del token en milisegundos. */
public record LoginResponse(String status, String token, long expiraEn, UsuarioDTO usuario) {
}
