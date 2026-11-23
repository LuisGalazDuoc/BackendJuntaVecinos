package com.juntavecinos.movimiento.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "categoria_movimiento")
@Getter
@Setter
@NoArgsConstructor
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private TipoMovimiento tipo;

    @Column(nullable = false)
    private String descripcion = "";

    @Column(nullable = false)
    private boolean activa = true;
}
