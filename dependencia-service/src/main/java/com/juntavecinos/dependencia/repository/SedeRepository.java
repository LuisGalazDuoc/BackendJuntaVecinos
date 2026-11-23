package com.juntavecinos.dependencia.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.juntavecinos.dependencia.model.Sede;

public interface SedeRepository extends JpaRepository<Sede, Long> {
}
