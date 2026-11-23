package com.juntavecinos.movimiento.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.juntavecinos.movimiento.dto.MovimientoDTO;
import com.juntavecinos.movimiento.service.MovimientoService;

/** Uso exclusivo de otros microservicios (rol SERVICIO). */
@RestController
@RequestMapping("/interno/movimientos")
public class MovimientoInternoController {

    private final MovimientoService service;

    public MovimientoInternoController(MovimientoService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovimientoDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtener(id));
    }
}
