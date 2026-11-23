package com.juntavecinos.reserva.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.juntavecinos.reserva.model.EstadoReserva;
import com.juntavecinos.reserva.model.Reserva;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findAllByOrderByFechaAscHoraInicioAsc();

    List<Reserva> findByUsuarioIdOrderByFechaAscHoraInicioAsc(Long usuarioId);

    List<Reserva> findByEstadoOrderByFechaAscHoraInicioAsc(EstadoReserva estado);

    List<Reserva> findByDependenciaIdAndFechaAndEstadoNotInOrderByHoraInicioAsc(Long dependenciaId, LocalDate fecha,
            Collection<EstadoReserva> estados);

    /** INSERT directo: si la hora ya esta ocupada, la clave primaria lo rechaza. */
    @Modifying
    @Query(value = "INSERT INTO reserva_bloque (dependencia_id, fecha, hora, reserva_id) VALUES (?1, ?2, ?3, ?4)",
            nativeQuery = true)
    void ocuparHora(Long dependenciaId, LocalDate fecha, int hora, Long reservaId);

    @Modifying
    @Query(value = "DELETE FROM reserva_bloque WHERE reserva_id = ?1", nativeQuery = true)
    void liberarHoras(Long reservaId);
}
