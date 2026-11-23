package com.juntavecinos.movimiento.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.juntavecinos.movimiento.model.EstadoMovimiento;
import com.juntavecinos.movimiento.model.Movimiento;

public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    @Query("""
            SELECT m FROM Movimiento m JOIN FETCH m.categoria
            WHERE m.estado = com.juntavecinos.movimiento.model.EstadoMovimiento.VIGENTE
            ORDER BY m.fecha DESC, m.id DESC""")
    List<Movimiento> listarVigentes();

    @Query("""
            SELECT m FROM Movimiento m JOIN FETCH m.categoria
            WHERE m.estado = com.juntavecinos.movimiento.model.EstadoMovimiento.VIGENTE
            AND m.fecha BETWEEN :desde AND :hasta
            ORDER BY m.fecha DESC, m.id DESC""")
    List<Movimiento> listarVigentesEntre(LocalDate desde, LocalDate hasta);

    boolean existsByReservaIdAndEstado(Long reservaId, EstadoMovimiento estado);
}
