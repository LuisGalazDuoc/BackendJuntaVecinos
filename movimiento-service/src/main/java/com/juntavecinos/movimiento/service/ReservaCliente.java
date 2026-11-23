package com.juntavecinos.movimiento.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juntavecinos.movimiento.dto.ReservaDTO;
import com.juntavecinos.movimiento.exception.BadRequestException;
import com.juntavecinos.movimiento.exception.ServicioExternoException;

@Component
public class ReservaCliente {

    private final WebClient webClient;
    private final String url;

    public ReservaCliente(WebClient webClient, @Value("${api.reserva}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

    public ReservaDTO obtener(Long id) {
        try {
            return webClient.get().uri(url, id).retrieve().bodyToMono(ReservaDTO.class).block(Duration.ofSeconds(5));
        } catch (WebClientResponseException.NotFound e) {
            throw new BadRequestException("La reserva no existe");
        } catch (Exception e) {
            throw new ServicioExternoException("No se pudo validar la reserva. Intente nuevamente", e);
        }
    }
}
