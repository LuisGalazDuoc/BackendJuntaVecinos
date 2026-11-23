package com.juntavecinos.auth.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import com.juntavecinos.auth.dto.ActivoRequest;
import com.juntavecinos.auth.dto.ContrasenaRequest;
import com.juntavecinos.auth.dto.CrearUsuarioRequest;
import com.juntavecinos.auth.dto.EditarUsuarioRequest;
import com.juntavecinos.auth.dto.UsuarioDTO;
import com.juntavecinos.auth.service.UsuarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/usuarios")
@PreAuthorize("hasRole('ADMIN')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<List<UsuarioDTO>> listar(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(usuarioService.listar(uid(jwt)));
    }

    @PostMapping
    public ResponseEntity<UsuarioDTO> crear(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CrearUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioService.crear(uid(jwt), request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioDTO> editar(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
            @Valid @RequestBody EditarUsuarioRequest request) {
        return ResponseEntity.ok(usuarioService.editar(uid(jwt), id, request));
    }

    @PatchMapping("/{id}/activo")
    public ResponseEntity<UsuarioDTO> cambiarActivo(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
            @Valid @RequestBody ActivoRequest request) {
        return ResponseEntity.ok(usuarioService.cambiarActivo(uid(jwt), id, request.activo()));
    }

    @PatchMapping("/{id}/contrasena")
    public ResponseEntity<Void> cambiarContrasena(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id,
            @Valid @RequestBody ContrasenaRequest request) {
        usuarioService.cambiarContrasena(uid(jwt), id, request.password());
        return ResponseEntity.noContent().build();
    }

    private Long uid(Jwt jwt) {
        return jwt.<Number>getClaim("uid").longValue();
    }
}
