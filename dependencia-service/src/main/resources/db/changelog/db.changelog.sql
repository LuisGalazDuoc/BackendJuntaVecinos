--liquibase formatted sql

--changeset juntavecinos:1
CREATE TABLE dependencia (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255) NOT NULL DEFAULT '',
    ubicacion VARCHAR(120) NOT NULL DEFAULT '',
    capacidad INT NOT NULL,
    activa BOOLEAN NOT NULL DEFAULT TRUE
);

--changeset juntavecinos:2
CREATE TABLE sede (
    id BIGINT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    direccion VARCHAR(200) NOT NULL,
    descripcion VARCHAR(1000) NOT NULL,
    reglamento VARCHAR(1000) NOT NULL,
    instrucciones_pago VARCHAR(1000) NOT NULL
);

--changeset juntavecinos:3
INSERT INTO dependencia (nombre, descripcion, ubicacion, capacidad) VALUES
('Salón multiuso', 'Salón principal para celebraciones y actividades', 'Primer piso', 80),
('Sala de reuniones', 'Sala para reuniones y talleres', 'Segundo piso', 25),
('Patio techado', 'Espacio exterior techado', 'Patio', 120);

--changeset juntavecinos:4
--informacion para demostracion
INSERT INTO sede (id, nombre, direccion, descripcion, reglamento, instrucciones_pago) VALUES
(1, 'Junta de Vecinos Zaror', 'Dirección de demostración 123',
 'Sede comunitaria con dependencias disponibles para actividades de los vecinos. Datos ficticios con fines académicos.',
 'Respetar el horario solicitado, la capacidad máxima y entregar la dependencia limpia.',
 'Una vez confirmada la reserva, informe su pago a Tesorería. La aplicación solo registra pagos informados: no procesa transacciones reales.');
