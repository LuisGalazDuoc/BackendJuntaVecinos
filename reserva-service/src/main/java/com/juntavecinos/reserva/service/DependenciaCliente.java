package com.juntavecinos.reserva.service;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.juntavecinos.reserva.dto.DependenciaDTO;
import com.juntavecinos.reserva.exception.BadRequestException;
import com.juntavecinos.reserva.exception.ServicioExternoException;

@Component
public class DependenciaCliente {

    private final WebClient webClient;
    private final String url;

    public DependenciaCliente(WebClient webClient, @Value("${api.dependencia}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

    public DependenciaDTO obtener(Long id) {
        try {
            return webClient.get().uri(url, id).retrieve().bodyToMono(DependenciaDTO.class)
                    .block(Duration.ofSeconds(5));
        } catch (WebClientResponseException.NotFound e) {
            throw new BadRequestException("La dependencia no existe");
        } catch (Exception e) {
            throw new ServicioExternoException("No se pudo validar la dependencia. Intente nuevamente", e);
        }
    }
}
