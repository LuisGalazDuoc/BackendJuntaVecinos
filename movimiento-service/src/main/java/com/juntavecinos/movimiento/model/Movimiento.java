package com.juntavecinos.movimiento.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movimiento")
@Getter
@Setter
@NoArgsConstructor
public class Movimiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private TipoMovimiento tipo;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Integer monto;

    @Column(nullable = false)
    private LocalDate fecha;

    /** Presente solo en los pagos informativos asociados a una reserva. */
    @Column(name = "reserva_id")
    private Long reservaId;

    @Column(name = "medio_pago")
    private String medioPago;

    private String referencia;

    @Column(name = "usuario_registro_id", nullable = false)
    private Long usuarioRegistroId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(10)")
    private EstadoMovimiento estado = EstadoMovimiento.VIGENTE;

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();
}
