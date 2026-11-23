package com.juntavecinos.reserva.dto;

import com.juntavecinos.reserva.model.EstadoCatalogo;

public record EstadoDTO(String codigo, String etiqueta) {

    public static EstadoDTO fromModel(EstadoCatalogo e) {
        return new EstadoDTO(e.getCodigo(), e.getEtiqueta());
    }
}
