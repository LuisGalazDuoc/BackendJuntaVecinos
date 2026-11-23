package com.juntavecinos.reserva.dto;

import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;

/** Horario ocupado, sin datos personales. */
public record HorarioDTO(@JsonFormat(pattern = "HH:mm") LocalTime horaInicio,
        @JsonFormat(pattern = "HH:mm") LocalTime horaFin) {
}
