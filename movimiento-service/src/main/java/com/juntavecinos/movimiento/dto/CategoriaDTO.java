package com.juntavecinos.movimiento.dto;

import com.juntavecinos.movimiento.model.Categoria;
import com.juntavecinos.movimiento.model.TipoMovimiento;

public record CategoriaDTO(Long id, String nombre, TipoMovimiento tipo, String descripcion, boolean activa) {

    public static CategoriaDTO fromModel(Categoria c) {
        return new CategoriaDTO(c.getId(), c.getNombre(), c.getTipo(), c.getDescripcion(), c.isActiva());
    }
}
