package com.juntavecinos.notificacion.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Limit;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.juntavecinos.notificacion.dto.NotificacionDTO;
import com.juntavecinos.notificacion.dto.NotificacionRequest;
import com.juntavecinos.notificacion.exception.BadRequestException;
import com.juntavecinos.notificacion.exception.ResourceNotFoundException;
import com.juntavecinos.notificacion.model.Notificacion;
import com.juntavecinos.notificacion.repository.NotificacionRepository;

@Service
public class NotificacionService {

    private final NotificacionRepository notificaciones;

    public NotificacionService(NotificacionRepository notificaciones) {
        this.notificaciones = notificaciones;
    }

    @Transactional
    public void crear(NotificacionRequest request) {
        if ((request.usuarioId() == null) == (request.rolDestino() == null)) {
            throw new BadRequestException("Debe indicar un usuario o un rol de destino");
        }
        Notificacion n = new Notificacion();
        n.setUsuarioId(request.usuarioId());
        n.setRolDestino(request.rolDestino());
        n.setTipo(request.tipo());
        n.setTitulo(request.titulo());
        n.setMensaje(request.mensaje());
        n.setReservaId(request.reservaId());
        n.setMovimientoId(request.movimientoId());
        notificaciones.save(n);
    }

    @Transactional(readOnly = true)
    public List<NotificacionDTO> listar(Long usuarioId, String rol) {
        Set<Long> leidas = notificaciones.leidas(usuarioId);
        return notificaciones.visibles(usuarioId, rol, Limit.of(50)).stream()
                .map(n -> NotificacionDTO.fromModel(n, leidas.contains(n.getId()))).toList();
    }

    @Transactional
    public void marcarLeida(Long id, Long usuarioId, String rol) {
        notificaciones.findById(id)
                .filter(n -> usuarioId.equals(n.getUsuarioId()) || rol.equals(n.getRolDestino()))
                .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));
        notificaciones.marcarLeida(id, usuarioId);
    }
}
