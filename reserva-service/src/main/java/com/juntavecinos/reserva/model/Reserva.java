package com.juntavecinos.reserva.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reserva")
@Getter
@Setter
@NoArgsConstructor
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false)
    private String solicitante;

    @Column(nullable = false)
    private String telefono;

    @Column(name = "dependencia_id", nullable = false)
    private Long dependenciaId;

    /** Nombre de la dependencia al momento de reservar. */
    @Column(nullable = false)
    private String dependencia;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fin", nullable = false)
    private LocalTime horaFin;

    @Column(nullable = false)
    private Integer personas;

    @Column(nullable = false)
    private String motivo;

    @Column(name = "usa_cocina", nullable = false)
    private boolean usaCocina;

    @Column(nullable = false)
    private Integer monto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private EstadoReserva estado = EstadoReserva.PENDIENTE;

    @Column(nullable = false)
    private String observacion = "";

    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion", nullable = false)
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @Version
    private Long version;
}
