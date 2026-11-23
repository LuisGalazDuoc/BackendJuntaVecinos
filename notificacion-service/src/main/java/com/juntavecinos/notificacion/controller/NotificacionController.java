package com.juntavecinos.notificacion.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.juntavecinos.notificacion.dto.NotificacionDTO;
import com.juntavecinos.notificacion.dto.NotificacionRequest;
import com.juntavecinos.notificacion.service.NotificacionService;

import jakarta.validation.Valid;

@RestController
public class NotificacionController {

    private final NotificacionService service;

    public NotificacionController(NotificacionService service) {
        this.service = service;
    }

    @GetMapping("/notificaciones")
    public ResponseEntity<List<NotificacionDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.listar(uid(jwt), jwt.getClaimAsString("rol")));
    }

    @PatchMapping("/notificaciones/{id}/leida")
    public ResponseEntity<Void> marcarLeida(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        service.marcarLeida(id, uid(jwt), jwt.getClaimAsString("rol"));
        return ResponseEntity.noContent().build();
    }

    /** Uso exclusivo de otros microservicios (rol SERVICIO). */
    @PostMapping("/interno/notificaciones")
    public ResponseEntity<Void> crear(@Valid @RequestBody NotificacionRequest request) {
        service.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    private Long uid(Jwt jwt) {
        return jwt.<Number>getClaim("uid").longValue();
    }
}
