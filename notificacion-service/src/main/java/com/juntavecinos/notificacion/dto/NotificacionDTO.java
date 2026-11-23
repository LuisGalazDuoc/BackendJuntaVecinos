package com.juntavecinos.notificacion.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.juntavecinos.notificacion.model.Notificacion;
import com.juntavecinos.notificacion.model.TipoNotificacion;

public record NotificacionDTO(Long id, TipoNotificacion tipo, String titulo, String mensaje, Long reservaId,
        Long movimientoId, @JsonFormat(pattern = "yyyy-MM-dd HH:mm") LocalDateTime fechaCreacion, boolean leida) {

    public static NotificacionDTO fromModel(Notificacion n, boolean leida) {
        return new NotificacionDTO(n.getId(), n.getTipo(), n.getTitulo(), n.getMensaje(), n.getReservaId(),
                n.getMovimientoId(), n.getFechaCreacion(), leida);
    }
}
