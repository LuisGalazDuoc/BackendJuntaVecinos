package com.juntavecinos.movimiento.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.juntavecinos.movimiento.dto.CategoriaDTO;
import com.juntavecinos.movimiento.dto.CategoriaRequest;
import com.juntavecinos.movimiento.service.CategoriaService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaService service;

    public CategoriaController(CategoriaService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTIVA', 'TESORERIA', 'ADMIN')")
    public ResponseEntity<List<CategoriaDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(service.listar("ADMIN".equals(jwt.getClaimAsString("rol"))));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDTO> crear(@Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CategoriaDTO> editar(@PathVariable Long id, @Valid @RequestBody CategoriaRequest request) {
        return ResponseEntity.ok(service.editar(id, request));
    }
}
