package com.juntavecinos.dependencia.dto;

import jakarta.validation.constraints.NotNull;

public record ActivaRequest(@NotNull(message = "Debe indicar si está activa") Boolean activa) {
}
