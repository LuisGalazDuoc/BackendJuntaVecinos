--liquibase formatted sql

--changeset juntavecinos:1
CREATE TABLE categoria_movimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(60) NOT NULL,
    tipo VARCHAR(10) NOT NULL,
    descripcion VARCHAR(255) NOT NULL DEFAULT '',
    activa BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uk_categoria UNIQUE (nombre, tipo),
    CONSTRAINT ck_categoria_tipo CHECK (tipo IN ('INGRESO', 'GASTO'))
);

--changeset juntavecinos:2
--comment: reserva_vigente impide registrar dos pagos vigentes para la misma reserva
CREATE TABLE movimiento (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(10) NOT NULL,
    categoria_id BIGINT NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    monto INT NOT NULL,
    fecha DATE NOT NULL,
    reserva_id BIGINT NULL,
    medio_pago VARCHAR(30) NULL,
    referencia VARCHAR(100) NULL,
    usuario_registro_id BIGINT NOT NULL,
    estado VARCHAR(10) NOT NULL DEFAULT 'VIGENTE',
    fecha_creacion DATETIME NOT NULL,
    reserva_vigente BIGINT GENERATED ALWAYS AS (CASE WHEN estado = 'VIGENTE' THEN reserva_id END) STORED,
    CONSTRAINT fk_movimiento_categoria FOREIGN KEY (categoria_id) REFERENCES categoria_movimiento (id),
    CONSTRAINT ck_movimiento_monto CHECK (monto > 0),
    CONSTRAINT uk_pago_reserva UNIQUE (reserva_vigente),
    INDEX ix_movimiento_fecha (fecha)
);

--changeset juntavecinos:3
CREATE TABLE visibilidad_financiera (
    id BIGINT PRIMARY KEY,
    mostrar_resumen BOOLEAN NOT NULL,
    mostrar_movimientos BOOLEAN NOT NULL
);

--changeset juntavecinos:4
INSERT INTO visibilidad_financiera (id, mostrar_resumen, mostrar_movimientos) VALUES (1, TRUE, FALSE);

--changeset juntavecinos:5
INSERT INTO categoria_movimiento (nombre, tipo, descripcion) VALUES
('Arriendo de sede', 'INGRESO', 'Pagos informativos de arriendos'),
('Cuota vecinal', 'INGRESO', 'Cuotas de los vecinos'),
('Otro ingreso', 'INGRESO', 'Otros ingresos'),
('Luz', 'GASTO', 'Consumo básico'),
('Agua', 'GASTO', 'Consumo básico'),
('Gas', 'GASTO', 'Consumo básico'),
('Internet', 'GASTO', 'Consumo básico'),
('Mantención', 'GASTO', 'Mantención de la sede'),
('Aseo', 'GASTO', 'Artículos y servicios de aseo'),
('Otro gasto', 'GASTO', 'Otros gastos');

--changeset juntavecinos:6
--comment: movimientos para demostracion
INSERT INTO movimiento (tipo, categoria_id, descripcion, monto, fecha, usuario_registro_id, estado, fecha_creacion) VALUES
('INGRESO', (SELECT id FROM categoria_movimiento WHERE nombre = 'Cuota vecinal'), 'Cuotas de demostración', 90000, '2026-10-05', 3, 'VIGENTE', NOW()),
('GASTO', (SELECT id FROM categoria_movimiento WHERE nombre = 'Luz'), 'Cuenta de luz de demostración', 35000, '2026-10-10', 3, 'VIGENTE', NOW()),
('GASTO', (SELECT id FROM categoria_movimiento WHERE nombre = 'Aseo'), 'Productos de limpieza de demostración', 12500, '2026-10-15', 3, 'VIGENTE', NOW());
