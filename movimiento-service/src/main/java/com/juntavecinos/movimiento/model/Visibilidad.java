package com.juntavecinos.movimiento.model;

import jakarta.persistence.*;
import lombok.*;

/** Informacion financiera que la directiva autoriza para los vecinos: registro unico con id 1. */
@Entity
@Table(name = "visibilidad_financiera")
@Getter
@Setter
@NoArgsConstructor
public class Visibilidad {

    public static final Long ID = 1L;

    @Id
    private Long id;

    @Column(name = "mostrar_resumen", nullable = false)
    private boolean mostrarResumen;

    @Column(name = "mostrar_movimientos", nullable = false)
    private boolean mostrarMovimientos;
}
