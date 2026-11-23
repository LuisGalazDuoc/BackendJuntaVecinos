package com.juntavecinos.reserva.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.juntavecinos.reserva.dto.ReservaDTO;
import com.juntavecinos.reserva.service.ReservaService;

/** Uso exclusivo de otros microservicios (rol SERVICIO). */
@RestController
@RequestMapping("/interno/reservas")
public class ReservaInternoController {

    private final ReservaService service;

    public ReservaInternoController(ReservaService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReservaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerInterno(id));
    }
}
