--liquibase formatted sql

--changeset juntavecinos:1
CREATE TABLE rol (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    nombre VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255) NOT NULL
);

--changeset juntavecinos:2
CREATE TABLE usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    telefono VARCHAR(9) NOT NULL,
    password VARCHAR(40) NOT NULL,
    rol_id BIGINT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES rol (id)
);

--changeset juntavecinos:3
INSERT INTO rol (codigo, nombre, descripcion) VALUES
('VECINO', 'Vecino/a', 'Solicita reservas y consulta informacion autorizada'),
('DIRECTIVA', 'Directiva / Secretaria', 'Revisa y resuelve las solicitudes de reserva'),
('TESORERIA', 'Tesoreria', 'Registra pagos, ingresos y gastos, y prepara informes'),
('ADMIN', 'Administrador', 'Administra cuentas, dependencias y catalogos');

--changeset juntavecinos:4
--cuentas de demostracion
INSERT INTO usuario (nombre, email, telefono, password, rol_id) VALUES
('Vecina Demo', 'vecino@zaror.test', '900000001', '8d9d3d9386c92465506325802627880ae7197514', (SELECT id FROM rol WHERE codigo = 'VECINO')),
('Directiva Demo', 'directiva@zaror.test', '900000002', '625573a637d9e5502f7aaca57347ac3b0a8fa4d6', (SELECT id FROM rol WHERE codigo = 'DIRECTIVA')),
('Tesoreria Demo', 'tesoreria@zaror.test', '900000003', '349cd396be14000ec95dfb590641dee1959d3bc0', (SELECT id FROM rol WHERE codigo = 'TESORERIA')),
('Admin Demo', 'admin@zaror.test', '900000004', 'cee99b85ed9c62dc2e4380520a6268882686bc01', (SELECT id FROM rol WHERE codigo = 'ADMIN'));
