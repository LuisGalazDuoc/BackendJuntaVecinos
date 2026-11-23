package com.juntavecinos.comprobante.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.juntavecinos.comprobante.dto.ComprobanteDTO;
import com.juntavecinos.comprobante.model.Comprobante;

public interface ComprobanteRepository extends JpaRepository<Comprobante, Long> {

    /** Lista sin cargar el contenido de los archivos. */
    @Query("""
            SELECT new com.juntavecinos.comprobante.dto.ComprobanteDTO(c.id, c.movimientoId, c.nombreArchivo, c.tipoArchivo)
            FROM Comprobante c ORDER BY c.id""")
    List<ComprobanteDTO> listarMetadatos();
}
