package com.juntavecinos.reserva.model;

import java.util.Set;

/** Estados y transiciones permitidas. RECHAZADA y CANCELADA son finales y liberan el horario. */
public enum EstadoReserva {
    PENDIENTE,
    REQUIERE_AJUSTES,
    CONFIRMADA,
    RECHAZADA,
    CANCELADA;

    public boolean puedePasarA(EstadoReserva nuevo) {
        return switch (this) {
            case PENDIENTE -> Set.of(CONFIRMADA, RECHAZADA, REQUIERE_AJUSTES, CANCELADA).contains(nuevo);
            case REQUIERE_AJUSTES -> Set.of(RECHAZADA, CANCELADA).contains(nuevo);
            case CONFIRMADA -> nuevo == CANCELADA;
            case RECHAZADA, CANCELADA -> false;
        };
    }

    public boolean liberaHorario() {
        return this == RECHAZADA || this == CANCELADA;
    }
}
