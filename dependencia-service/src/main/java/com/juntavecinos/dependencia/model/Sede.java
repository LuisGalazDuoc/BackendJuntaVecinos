package com.juntavecinos.dependencia.model;

import jakarta.persistence.*;
import lombok.*;

/** Informacion general de la sede: existe un unico registro con id 1. */
@Entity
@Table(name = "sede")
@Getter
@Setter
@NoArgsConstructor
public class Sede {

    public static final Long ID = 1L;

    @Id
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String reglamento;

    @Column(name = "instrucciones_pago", nullable = false)
    private String instruccionesPago;
}
