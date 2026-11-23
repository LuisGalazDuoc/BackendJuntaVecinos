package com.juntavecinos.comprobante.dto;

/** Metadatos del comprobante. El contenido se obtiene en /comprobantes/{id}/archivo. */
public record ComprobanteDTO(Long id, Long movimientoId, String nombreArchivo, String tipoArchivo) {
}
