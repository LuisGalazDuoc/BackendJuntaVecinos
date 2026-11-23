package com.juntavecinos.auth.dto;

import com.juntavecinos.auth.model.Usuario;

/** Nunca incluye la contraseña. */
public record UsuarioDTO(Long id, String nombre, String email, String telefono, String rol, boolean activo) {

    public static UsuarioDTO fromModel(Usuario u) {
        return new UsuarioDTO(u.getId(), u.getNombre(), u.getEmail(), u.getTelefono(),
                u.getRol().getCodigo(), u.isActivo());
    }
}
