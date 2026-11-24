--liquibase formatted sql

--changeset juntavecinos:1
CREATE TABLE estado_reserva (
    codigo VARCHAR(20) PRIMARY KEY,
    etiqueta VARCHAR(40) NOT NULL,
    orden INT NOT NULL
);

--changeset juntavecinos:2
CREATE TABLE reserva (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    solicitante VARCHAR(100) NOT NULL,
    telefono VARCHAR(9) NOT NULL,
    dependencia_id BIGINT NOT NULL,
    dependencia VARCHAR(80) NOT NULL,
    fecha DATE NOT NULL,
    hora_inicio TIME NOT NULL,
    hora_fin TIME NOT NULL,
    personas INT NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    usa_cocina BOOLEAN NOT NULL,
    monto INT NOT NULL,
    estado VARCHAR(20) NOT NULL,
    observacion VARCHAR(500) NOT NULL DEFAULT '',
    fecha_creacion DATETIME NOT NULL,
    fecha_actualizacion DATETIME NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT fk_reserva_estado FOREIGN KEY (estado) REFERENCES estado_reserva (codigo),
    CONSTRAINT ck_reserva_horas CHECK (hora_inicio < hora_fin),
    CONSTRAINT ck_reserva_monto CHECK (monto > 0),
    INDEX ix_reserva_usuario (usuario_id),
    INDEX ix_reserva_fecha (fecha, hora_inicio)
);

--changeset juntavecinos:3
--comment: una fila por hora ocupada. La clave primaria impide reservas superpuestas.
CREATE TABLE reserva_bloque (
    dependencia_id BIGINT NOT NULL,
    fecha DATE NOT NULL,
    hora INT NOT NULL,
    reserva_id BIGINT NOT NULL,
    PRIMARY KEY (dependencia_id, fecha, hora),
    CONSTRAINT fk_bloque_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id)
);

--changeset juntavecinos:4
CREATE TABLE historial_reserva (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    reserva_id BIGINT NOT NULL,
    estado_anterior VARCHAR(20) NULL,
    estado_nuevo VARCHAR(20) NOT NULL,
    usuario_responsable_id BIGINT NOT NULL,
    observacion VARCHAR(500) NOT NULL DEFAULT '',
    fecha_cambio DATETIME NOT NULL,
    CONSTRAINT fk_historial_reserva FOREIGN KEY (reserva_id) REFERENCES reserva (id)
);

--changeset juntavecinos:5
INSERT INTO estado_reserva (codigo, etiqueta, orden) VALUES
('PENDIENTE', 'Pendiente', 1),
('REQUIERE_AJUSTES', 'Requiere ajustes', 2),
('CONFIRMADA', 'Confirmada', 3),
('RECHAZADA', 'Rechazada', 4),
('CANCELADA', 'Cancelada', 5);
