--liquibase formatted sql

--changeset juntavecinos:1
CREATE TABLE comprobante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    movimiento_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(100) NOT NULL,
    tipo_archivo VARCHAR(30) NOT NULL,
    contenido LONGBLOB NOT NULL,
    usuario_registro_id BIGINT NOT NULL,
    fecha_adjunto DATETIME NOT NULL,
    INDEX ix_comprobante_movimiento (movimiento_id)
);
