package com.juntavecinos.movimiento.dto;

import java.time.LocalDate;

import com.juntavecinos.movimiento.model.Movimiento;
import com.juntavecinos.movimiento.model.TipoMovimiento;

/** Version para vecinos: sin descripcion, reserva, referencia ni comprobante. */
public record MovimientoPublicoDTO(Long id, TipoMovimiento tipo, String categoria, Integer monto, LocalDate fecha) {

    public static MovimientoPublicoDTO fromModel(Movimiento m) {
        return new MovimientoPublicoDTO(m.getId(), m.getTipo(), m.getCategoria().getNombre(), m.getMonto(), m.getFecha());
    }
}
