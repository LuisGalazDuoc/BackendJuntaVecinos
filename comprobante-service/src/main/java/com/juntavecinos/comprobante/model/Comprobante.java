package com.juntavecinos.comprobante.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "comprobante")
@Getter
@Setter
@NoArgsConstructor
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "movimiento_id", nullable = false)
    private Long movimientoId;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(name = "tipo_archivo", nullable = false)
    private String tipoArchivo;

    @Lob
    @Column(nullable = false, columnDefinition = "LONGBLOB")
    private byte[] contenido;

    @Column(name = "usuario_registro_id", nullable = false)
    private Long usuarioRegistroId;

    @Column(name = "fecha_adjunto", nullable = false)
    private LocalDateTime fechaAdjunto = LocalDateTime.now();
}
