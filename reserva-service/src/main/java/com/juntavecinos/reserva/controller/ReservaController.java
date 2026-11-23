package com.juntavecinos.reserva.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.juntavecinos.reserva.dto.*;
import com.juntavecinos.reserva.service.ReservaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/reservas")
public class ReservaController {

    private final ReservaService service;

    public ReservaController(ReservaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasRole('DIRECTIVA')")
    public ResponseEntity<List<ReservaDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/mias")
    @PreAuthorize("hasRole('VECINO')")
    public ResponseEntity<List<ReservaDTO>> mias(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.listarPropias(uid(jwt)));
    }

    @GetMapping("/confirmadas")
    @PreAuthorize("hasAnyRole('DIRECTIVA', 'TESORERIA')")
    public ResponseEntity<List<ReservaDTO>> confirmadas() {
        return ResponseEntity.ok(service.listarConfirmadas());
    }

    @GetMapping("/disponibilidad")
    public ResponseEntity<List<HorarioDTO>> disponibilidad(@RequestParam Long dependenciaId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {
        return ResponseEntity.ok(service.disponibilidad(dependenciaId, fecha));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('VECINO', 'DIRECTIVA', 'TESORERIA')")
    public ResponseEntity<ReservaDTO> obtener(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.obtener(id, uid(jwt), jwt.getClaimAsString("rol")));
    }

    @GetMapping("/{id}/historial")
    @PreAuthorize("hasRole('DIRECTIVA')")
    public ResponseEntity<List<HistorialDTO>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(service.historial(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('VECINO')")
    public ResponseEntity<ReservaDTO> crear(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(uid(jwt), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('VECINO')")
    public ResponseEntity<ReservaDTO> corregir(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReservaRequest request) {
        return ResponseEntity.ok(service.corregir(id, uid(jwt), request));
    }

    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('DIRECTIVA')")
    public ResponseEntity<ReservaDTO> cambiarEstado(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CambioEstadoRequest request) {
        return ResponseEntity.ok(service.cambiarEstado(id, uid(jwt), request));
    }

    @PatchMapping("/{id}/observacion")
    @PreAuthorize("hasRole('DIRECTIVA')")
    public ResponseEntity<ReservaDTO> observar(@PathVariable Long id, @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ObservacionRequest request) {
        return ResponseEntity.ok(service.observar(id, uid(jwt), request.observacion()));
    }

    @GetMapping("/estados")
    public ResponseEntity<List<EstadoDTO>> estados() {
        return ResponseEntity.ok(service.estados());
    }

    @PutMapping("/estados/{codigo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstadoDTO> editarEstado(@PathVariable String codigo,
            @Valid @RequestBody EtiquetaRequest request) {
        return ResponseEntity.ok(service.editarEstado(codigo, request.etiqueta()));
    }

    private Long uid(Jwt jwt) {
        return jwt.<Number>getClaim("uid").longValue();
    }
}
