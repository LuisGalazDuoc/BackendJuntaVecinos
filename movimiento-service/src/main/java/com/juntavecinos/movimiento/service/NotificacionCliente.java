package com.juntavecinos.movimiento.service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

/** Envia avisos a notificacion-service. Si falla, la operacion principal no se interrumpe. */
@Component
public class NotificacionCliente {
    private static final Logger logger = LoggerFactory.getLogger(NotificacionCliente.class);

    private final WebClient webClient;
    private final String url;

    public NotificacionCliente(WebClient webClient, @Value("${api.notificacion}") String url) {
        this.webClient = webClient;
        this.url = url;
    }

    public void aUsuario(Long usuarioId, String tipo, String titulo, String mensaje, Long reservaId, Long movimientoId) {
        enviar(usuarioId, null, tipo, titulo, mensaje, reservaId, movimientoId);
    }

    public void aRol(String rol, String tipo, String titulo, String mensaje, Long reservaId, Long movimientoId) {
        enviar(null, rol, tipo, titulo, mensaje, reservaId, movimientoId);
    }

    private void enviar(Long usuarioId, String rol, String tipo, String titulo, String mensaje,
            Long reservaId, Long movimientoId) {
        Map<String, Object> body = new HashMap<>();
        body.put("usuarioId", usuarioId);
        body.put("rolDestino", rol);
        body.put("tipo", tipo);
        body.put("titulo", titulo);
        body.put("mensaje", mensaje);
        body.put("reservaId", reservaId);
        body.put("movimientoId", movimientoId);
        try {
            webClient.post().uri(url).bodyValue(body).retrieve().toBodilessEntity().block(Duration.ofSeconds(3));
        } catch (Exception e) {
            logger.warn("No se pudo enviar el aviso {}: {}", tipo, e.getMessage());
        }
    }
}
