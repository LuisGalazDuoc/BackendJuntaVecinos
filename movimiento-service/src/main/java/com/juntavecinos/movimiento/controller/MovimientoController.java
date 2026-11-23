package com.juntavecinos.movimiento.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.juntavecinos.movimiento.dto.*;
import com.juntavecinos.movimiento.service.MovimientoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/movimientos")
public class MovimientoController {

    private final MovimientoService service;

    public MovimientoController(MovimientoService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTIVA', 'TESORERIA')")
    public ResponseEntity<List<MovimientoDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasAnyRole('DIRECTIVA', 'TESORERIA')")
    public ResponseEntity<ResumenDTO> resumen() {
        return ResponseEntity.ok(service.resumen());
    }

    @GetMapping("/publico")
    @PreAuthorize("hasRole('VECINO')")
    public ResponseEntity<FinanzasPublicasDTO> publico() {
        return ResponseEntity.ok(service.publico());
    }

    @GetMapping("/informe")
    @PreAuthorize("hasRole('TESORERIA')")
    public ResponseEntity<InformeDTO> informe(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(service.informe(desde, hasta));
    }

    @GetMapping("/visibilidad")
    @PreAuthorize("hasRole('DIRECTIVA')")
    public ResponseEntity<VisibilidadDTO> visibilidad() {
        return ResponseEntity.ok(service.obtenerVisibilidad());
    }

    @PutMapping("/visibilidad")
    @PreAuthorize("hasRole('DIRECTIVA')")
    public ResponseEntity<VisibilidadDTO> editarVisibilidad(@Valid @RequestBody VisibilidadDTO request) {
        return ResponseEntity.ok(service.editarVisibilidad(request));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('DIRECTIVA', 'TESORERIA')")
    public ResponseEntity<MovimientoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('TESORERIA')")
    public ResponseEntity<MovimientoDTO> registrar(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody MovimientoRequest request) {
        Long usuarioId = jwt.<Number>getClaim("uid").longValue();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registrar(usuarioId, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TESORERIA')")
    public ResponseEntity<Void> anular(@PathVariable Long id) {
        service.anular(id);
        return ResponseEntity.noContent().build();
    }
}
