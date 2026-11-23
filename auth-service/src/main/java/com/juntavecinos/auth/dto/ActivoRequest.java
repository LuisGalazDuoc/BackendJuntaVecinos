package com.juntavecinos.auth.dto;

import jakarta.validation.constraints.NotNull;

public record ActivoRequest(@NotNull(message = "Debe indicar si está activo") Boolean activo) {
}
