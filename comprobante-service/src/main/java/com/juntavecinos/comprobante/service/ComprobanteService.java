package com.juntavecinos.comprobante.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.juntavecinos.comprobante.dto.ComprobanteDTO;
import com.juntavecinos.comprobante.exception.BadRequestException;
import com.juntavecinos.comprobante.exception.ResourceNotFoundException;
import com.juntavecinos.comprobante.model.Comprobante;
import com.juntavecinos.comprobante.repository.ComprobanteRepository;

@Service
public class ComprobanteService {

    private static final int[] FIRMA_JPEG = { 0xFF, 0xD8, 0xFF };
    private static final int[] FIRMA_PNG = { 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A };

    private final ComprobanteRepository comprobantes;
    private final MovimientoCliente movimientos;

    public ComprobanteService(ComprobanteRepository comprobantes, MovimientoCliente movimientos) {
        this.comprobantes = comprobantes;
        this.movimientos = movimientos;
    }

    public List<ComprobanteDTO> listar() {
        return comprobantes.listarMetadatos();
    }

    public Comprobante obtener(Long id) {
        return comprobantes.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comprobante no encontrado"));
    }

    public ComprobanteDTO adjuntar(Long usuarioId, Long movimientoId, MultipartFile archivo) {
        byte[] contenido;
        try {
            contenido = archivo.getBytes();
        } catch (IOException e) {
            throw new BadRequestException("No se pudo leer el archivo");
        }
        String tipo = tipoReal(contenido);
        if (!"VIGENTE".equals(movimientos.obtener(movimientoId).estado())) {
            throw new BadRequestException("El movimiento está anulado");
        }
        Comprobante c = new Comprobante();
        c.setMovimientoId(movimientoId);
        c.setTipoArchivo(tipo);
        // Nombre generado: no se usa el que envia el cliente
        c.setNombreArchivo("comprobante_" + movimientoId + ("image/png".equals(tipo) ? ".png" : ".jpg"));
        c.setContenido(contenido);
        c.setUsuarioRegistroId(usuarioId);
        c = comprobantes.save(c);
        return new ComprobanteDTO(c.getId(), c.getMovimientoId(), c.getNombreArchivo(), c.getTipoArchivo());
    }

    /** El tipo se determina por el contenido, no por la extension ni por la cabecera enviada. */
    private String tipoReal(byte[] contenido) {
        if (comienzaCon(contenido, FIRMA_JPEG)) return "image/jpeg";
        if (comienzaCon(contenido, FIRMA_PNG)) return "image/png";
        throw new BadRequestException("El comprobante debe ser una imagen JPEG o PNG");
    }

    private boolean comienzaCon(byte[] contenido, int[] firma) {
        if (contenido.length < firma.length) return false;
        for (int i = 0; i < firma.length; i++) {
            if ((contenido[i] & 0xFF) != firma[i]) return false;
        }
        return true;
    }
}
