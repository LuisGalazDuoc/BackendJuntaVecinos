--liquibase formatted sql

--changeset juntavecinos:1
--Cada aviso va dirigido a un usuario o a todos los usuarios de un rol
CREATE TABLE notificacion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NULL,
    rol_destino VARCHAR(20) NULL,
    tipo VARCHAR(30) NOT NULL,
    titulo VARCHAR(120) NOT NULL,
    mensaje VARCHAR(1000) NOT NULL,
    reserva_id BIGINT NULL,
    movimiento_id BIGINT NULL,
    fecha_creacion DATETIME NOT NULL,
    INDEX ix_notificacion_usuario (usuario_id),
    INDEX ix_notificacion_rol (rol_destino)
);

--changeset juntavecinos:2
CREATE TABLE notificacion_lectura (
    notificacion_id BIGINT NOT NULL,
    usuario_id BIGINT NOT NULL,
    PRIMARY KEY (notificacion_id, usuario_id),
    CONSTRAINT fk_lectura_notificacion FOREIGN KEY (notificacion_id) REFERENCES notificacion (id)
);
