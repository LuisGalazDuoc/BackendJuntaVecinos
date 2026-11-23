package com.juntavecinos.movimiento.dto;

import java.time.LocalDate;

import com.juntavecinos.movimiento.model.EstadoMovimiento;
import com.juntavecinos.movimiento.model.Movimiento;
import com.juntavecinos.movimiento.model.TipoMovimiento;

public record MovimientoDTO(Long id, TipoMovimiento tipo, Long categoriaId, String categoria, String descripcion,
        Integer monto, LocalDate fecha, Long reservaId, String medioPago, String referencia, EstadoMovimiento estado) {

    public static MovimientoDTO fromModel(Movimiento m) {
        return new MovimientoDTO(m.getId(), m.getTipo(), m.getCategoria().getId(), m.getCategoria().getNombre(),
                m.getDescripcion(), m.getMonto(), m.getFecha(), m.getReservaId(), m.getMedioPago(), m.getReferencia(),
                m.getEstado());
    }
}
