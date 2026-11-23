package com.juntavecinos.notificacion.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.juntavecinos.notificacion.model.Notificacion;

public interface NotificacionRepository extends JpaRepository<Notificacion, Long> {

    @Query("SELECT n FROM Notificacion n WHERE n.usuarioId = :usuarioId OR n.rolDestino = :rol ORDER BY n.id DESC")
    List<Notificacion> visibles(Long usuarioId, String rol, Limit limite);

    @Query(value = "SELECT notificacion_id FROM notificacion_lectura WHERE usuario_id = :usuarioId", nativeQuery = true)
    Set<Long> leidas(Long usuarioId);

    @Modifying
    @Query(value = "INSERT IGNORE INTO notificacion_lectura (notificacion_id, usuario_id) VALUES (:id, :usuarioId)",
            nativeQuery = true)
    void marcarLeida(Long id, Long usuarioId);
}
