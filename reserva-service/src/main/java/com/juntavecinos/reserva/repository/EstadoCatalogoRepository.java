package com.juntavecinos.reserva.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juntavecinos.reserva.model.EstadoCatalogo;

public interface EstadoCatalogoRepository extends JpaRepository<EstadoCatalogo, String> {

    List<EstadoCatalogo> findAllByOrderByOrdenAsc();
}
