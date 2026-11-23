package com.juntavecinos.reserva.dto;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.juntavecinos.reserva.model.EstadoReserva;
import com.juntavecinos.reserva.model.Reserva;

public record ReservaDTO(Long id, Long usuarioId, String solicitante, String telefono, Long dependenciaId,
        String dependencia, LocalDate fecha, @JsonFormat(pattern = "HH:mm") LocalTime horaInicio,
        @JsonFormat(pattern = "HH:mm") LocalTime horaFin, Integer personas, String motivo, boolean usaCocina,
        Integer monto, EstadoReserva estado, String observacion) {

    public static ReservaDTO fromModel(Reserva r) {
        return new ReservaDTO(r.getId(), r.getUsuarioId(), r.getSolicitante(), r.getTelefono(), r.getDependenciaId(),
                r.getDependencia(), r.getFecha(), r.getHoraInicio(), r.getHoraFin(), r.getPersonas(), r.getMotivo(),
                r.isUsaCocina(), r.getMonto(), r.getEstado(), r.getObservacion());
    }

    /** Tesoreria solo necesita los datos para asociar el pago: se omite el telefono y la observacion. */
    public static ReservaDTO paraTesoreria(Reserva r) {
        return new ReservaDTO(r.getId(), r.getUsuarioId(), r.getSolicitante(), "", r.getDependenciaId(),
                r.getDependencia(), r.getFecha(), r.getHoraInicio(), r.getHoraFin(), r.getPersonas(), r.getMotivo(),
                r.isUsaCocina(), r.getMonto(), r.getEstado(), "");
    }
}
