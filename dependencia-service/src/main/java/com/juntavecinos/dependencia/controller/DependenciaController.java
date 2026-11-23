package com.juntavecinos.dependencia.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.juntavecinos.dependencia.dto.ActivaRequest;
import com.juntavecinos.dependencia.dto.DependenciaDTO;
import com.juntavecinos.dependencia.dto.DependenciaRequest;
import com.juntavecinos.dependencia.dto.SedeDTO;
import com.juntavecinos.dependencia.service.DependenciaService;

import jakarta.validation.Valid;

@RestController
public class DependenciaController {

    private final DependenciaService service;

    public DependenciaController(DependenciaService service) {
        this.service = service;
    }

    @GetMapping("/dependencias")
    public ResponseEntity<List<DependenciaDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.listar("ADMIN".equals(jwt.getClaimAsString("rol"))));
    }

    @GetMapping("/dependencias/{id}")
    public ResponseEntity<DependenciaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }

    @GetMapping("/dependencias/{id}/exists")
    public ResponseEntity<Boolean> existe(@PathVariable Long id) {
        return ResponseEntity.ok(service.existePorId(id));
    }

    @PostMapping("/dependencias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DependenciaDTO> crear(@Valid @RequestBody DependenciaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/dependencias/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DependenciaDTO> editar(@PathVariable Long id, @Valid @RequestBody DependenciaRequest request) {
        return ResponseEntity.ok(service.editar(id, request));
    }

    @PatchMapping("/dependencias/{id}/activa")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DependenciaDTO> cambiarActiva(@PathVariable Long id, @Valid @RequestBody ActivaRequest request) {
        return ResponseEntity.ok(service.cambiarActiva(id, request.activa()));
    }

    @GetMapping("/dependencias/sede")
    public ResponseEntity<SedeDTO> sede() {
        return ResponseEntity.ok(service.sede());
    }

    @PutMapping("/dependencias/sede")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SedeDTO> editarSede(@Valid @RequestBody SedeDTO request) {
        return ResponseEntity.ok(service.editarSede(request));
    }

    /** Uso exclusivo de otros microservicios (rol SERVICIO). */
    @GetMapping("/interno/dependencias/{id}")
    public ResponseEntity<DependenciaDTO> obtenerInterno(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }
}
