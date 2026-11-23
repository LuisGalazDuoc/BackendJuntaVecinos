package com.juntavecinos.reserva.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juntavecinos.reserva.model.HistorialReserva;

public interface HistorialRepository extends JpaRepository<HistorialReserva, Long> {

    List<HistorialReserva> findByReservaIdOrderByIdAsc(Long reservaId);
}
