package com.juntavecinos.auth.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.juntavecinos.auth.model.Rol;

import jakarta.persistence.LockModeType;

public interface RolRepository extends JpaRepository<Rol, Long> {

    Optional<Rol> findByCodigo(String codigo);

    /** Bloquea el rol ADMIN para que dos cambios simultaneos no dejen el sistema sin administradores. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT r FROM Rol r WHERE r.codigo = 'ADMIN'")
    List<Rol> bloquearAdmin();
}
