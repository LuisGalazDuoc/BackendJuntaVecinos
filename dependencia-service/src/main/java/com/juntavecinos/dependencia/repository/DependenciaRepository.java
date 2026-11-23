package com.juntavecinos.dependencia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juntavecinos.dependencia.model.Dependencia;

public interface DependenciaRepository extends JpaRepository<Dependencia, Long> {

    List<Dependencia> findAllByOrderByNombreAsc();

    List<Dependencia> findByActivaTrueOrderByNombreAsc();

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}
