package com.juntavecinos.comprobante.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juntavecinos.comprobante.dto.MovimientoDTO;
import com.juntavecinos.comprobante.exception.BadRequestException;
import com.juntavecinos.comprobante.exception.ServicioExternoException;

@Component
public class MovimientoCliente {

    private final WebClient webClient;
    private final String url;

    public MovimientoCliente(WebClient webClient, @Value("${api.movimiento}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

    public MovimientoDTO obtener(Long id) {
        try {
            return webClient.get().uri(url, id).retrieve().bodyToMono(MovimientoDTO.class).block(Duration.ofSeconds(5));
        } catch (WebClientResponseException.NotFound e) {
            throw new BadRequestException("El movimiento no existe");
        } catch (Exception e) {
            throw new ServicioExternoException("No se pudo validar el movimiento. Intente nuevamente", e);
        }
    }
}
