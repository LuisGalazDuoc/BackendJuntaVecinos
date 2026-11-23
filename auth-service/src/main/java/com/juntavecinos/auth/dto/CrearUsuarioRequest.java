package com.juntavecinos.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CrearUsuarioRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
        @Pattern(regexp = "[\\p{L} ]+", message = "El nombre solo permite letras y espacios") String nombre,
        @NotBlank(message = "El correo es obligatorio")
        @Email(message = "El correo no es válido")
        @Size(max = 150, message = "El correo es demasiado largo") String email,
        @NotNull(message = "El teléfono es obligatorio")
        @Pattern(regexp = "9\\d{8}", message = "El teléfono debe tener 9 dígitos y comenzar con 9") String telefono,
        @NotNull(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres") String password,
        @NotBlank(message = "El perfil es obligatorio") String rol) {
}
