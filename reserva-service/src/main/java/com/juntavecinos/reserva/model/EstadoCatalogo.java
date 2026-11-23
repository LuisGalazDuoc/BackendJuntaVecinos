package com.juntavecinos.reserva.model;

import jakarta.persistence.*;
import lombok.*;

/** Nombre visible de cada estado. El codigo es fijo; solo se puede editar la etiqueta. */
@Entity
@Table(name = "estado_reserva")
@Getter
@Setter
@NoArgsConstructor
public class EstadoCatalogo {

    @Id
    private String codigo;

    @Column(nullable = false)
    private String etiqueta;

    @Column(nullable = false)
    private Integer orden;
}
