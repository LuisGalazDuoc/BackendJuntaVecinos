package com.juntavecinos.movimiento.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juntavecinos.movimiento.model.Categoria;
import com.juntavecinos.movimiento.model.TipoMovimiento;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findAllByOrderByTipoDescIdAsc();

    List<Categoria> findByActivaTrueOrderByTipoDescIdAsc();

    Optional<Categoria> findByNombreIgnoreCaseAndTipo(String nombre, TipoMovimiento tipo);
}
