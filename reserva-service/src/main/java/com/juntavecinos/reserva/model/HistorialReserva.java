package com.juntavecinos.reserva.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "historial_reserva")
@Getter
@Setter
@NoArgsConstructor
public class HistorialReserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "reserva_id", nullable = false)
    private Long reservaId;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_anterior", columnDefinition = "VARCHAR(20)")
    private EstadoReserva estadoAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_nuevo", nullable = false, columnDefinition = "VARCHAR(20)")
    private EstadoReserva estadoNuevo;

    @Column(name = "usuario_responsable_id", nullable = false)
    private Long usuarioResponsableId;

    @Column(nullable = false)
    private String observacion = "";

    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio = LocalDateTime.now();

    public HistorialReserva(Reserva reserva, EstadoReserva anterior, Long responsableId) {
        this.reservaId = reserva.getId();
        this.estadoAnterior = anterior;
        this.estadoNuevo = reserva.getEstado();
        this.usuarioResponsableId = responsableId;
        this.observacion = reserva.getObservacion();
    }
}
