package com.juntavecinos.dependencia.dto;

import com.juntavecinos.dependencia.model.Dependencia;

public record DependenciaDTO(Long id, String nombre, String descripcion, String ubicacion, Integer capacidad,
        boolean activa) {

    public static DependenciaDTO fromModel(Dependencia d) {
        return new DependenciaDTO(d.getId(), d.getNombre(), d.getDescripcion(), d.getUbicacion(), d.getCapacidad(),
                d.isActiva());
    }
}
