package com.juntavecinos.comprobante.controller;

import java.util.List;

import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.juntavecinos.comprobante.dto.ComprobanteDTO;
import com.juntavecinos.comprobante.model.Comprobante;
import com.juntavecinos.comprobante.service.ComprobanteService;

/** Respaldos restringidos a Directiva y Tesoreria; los vecinos nunca los ven. */
@RestController
@RequestMapping("/comprobantes")
public class ComprobanteController {

    private final ComprobanteService service;

    public ComprobanteController(ComprobanteService service) {
        this.service = service;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('DIRECTIVA', 'TESORERIA')")
    public ResponseEntity<List<ComprobanteDTO>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{id}/archivo")
    @PreAuthorize("hasAnyRole('DIRECTIVA', 'TESORERIA')")
    public ResponseEntity<byte[]> archivo(@PathVariable Long id) {
        Comprobante c = service.obtener(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(c.getTipoArchivo()))
                .cacheControl(CacheControl.noStore())
                .body(c.getContenido());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('TESORERIA')")
    public ResponseEntity<ComprobanteDTO> adjuntar(@AuthenticationPrincipal Jwt jwt,
            @RequestParam Long movimientoId, @RequestPart MultipartFile archivo) {
        Long usuarioId = jwt.<Number>getClaim("uid").longValue();
        return ResponseEntity.status(HttpStatus.CREATED).body(service.adjuntar(usuarioId, movimientoId, archivo));
    }
}
