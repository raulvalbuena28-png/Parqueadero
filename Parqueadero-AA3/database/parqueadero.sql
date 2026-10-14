-- =====================================================
-- Script de base de datos: Sistema de gestión de parqueadero
-- Motor: MySQL 5.7 / 8.x
-- Módulos: ingresos, salidas, cobros y apartado de espacios
-- =====================================================

DROP DATABASE IF EXISTS parqueadero;
CREATE DATABASE parqueadero CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE parqueadero;

-- -----------------------------------------------------
-- Tabla: tipos_vehiculo (carro, moto) con su tarifa por hora
-- -----------------------------------------------------
CREATE TABLE tipos_vehiculo (
    id_tipo        INT AUTO_INCREMENT PRIMARY KEY,
    nombre         VARCHAR(20)   NOT NULL UNIQUE,
    tarifa_hora    DECIMAL(10,2) NOT NULL CHECK (tarifa_hora > 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: espacios (cupos del parqueadero)
-- estado: DISPONIBLE, OCUPADO, APARTADO
-- -----------------------------------------------------
CREATE TABLE espacios (
    id_espacio     INT AUTO_INCREMENT PRIMARY KEY,
    codigo         VARCHAR(10)   NOT NULL UNIQUE,
    id_tipo        INT           NOT NULL,
    estado         ENUM('DISPONIBLE','OCUPADO','APARTADO') NOT NULL DEFAULT 'DISPONIBLE',
    CONSTRAINT fk_espacio_tipo FOREIGN KEY (id_tipo) REFERENCES tipos_vehiculo(id_tipo)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: vehiculos
-- -----------------------------------------------------
CREATE TABLE vehiculos (
    id_vehiculo    INT AUTO_INCREMENT PRIMARY KEY,
    placa          VARCHAR(6)    NOT NULL UNIQUE,
    id_tipo        INT           NOT NULL,
    CONSTRAINT fk_vehiculo_tipo FOREIGN KEY (id_tipo) REFERENCES tipos_vehiculo(id_tipo)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: ingresos (entrada y salida de vehículos)
-- fecha_salida NULL = el vehículo sigue dentro
-- -----------------------------------------------------
CREATE TABLE ingresos (
    id_ingreso     INT AUTO_INCREMENT PRIMARY KEY,
    id_vehiculo    INT           NOT NULL,
    id_espacio     INT           NOT NULL,
    fecha_ingreso  DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_salida   DATETIME      NULL,
    CONSTRAINT fk_ingreso_vehiculo FOREIGN KEY (id_vehiculo) REFERENCES vehiculos(id_vehiculo),
    CONSTRAINT fk_ingreso_espacio  FOREIGN KEY (id_espacio)  REFERENCES espacios(id_espacio),
    CONSTRAINT chk_fechas CHECK (fecha_salida IS NULL OR fecha_salida >= fecha_ingreso)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: cobros (pago asociado a un ingreso)
-- -----------------------------------------------------
CREATE TABLE cobros (
    id_cobro         INT AUTO_INCREMENT PRIMARY KEY,
    id_ingreso       INT           NOT NULL UNIQUE,
    horas_cobradas   INT           NOT NULL CHECK (horas_cobradas > 0),
    valor_total      DECIMAL(10,2) NOT NULL CHECK (valor_total >= 0),
    valor_recibido   DECIMAL(10,2) NOT NULL,
    cambio           DECIMAL(10,2) NOT NULL DEFAULT 0,
    metodo_pago      ENUM('EFECTIVO','TARJETA','TRANSFERENCIA') NOT NULL,
    fecha_pago       DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_cobro_ingreso FOREIGN KEY (id_ingreso) REFERENCES ingresos(id_ingreso),
    CONSTRAINT chk_recibido CHECK (valor_recibido >= valor_total)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Tabla: apartados (reserva de espacios)
-- estado: ACTIVO, CUMPLIDO, CANCELADO
-- -----------------------------------------------------
CREATE TABLE apartados (
    id_apartado      INT AUTO_INCREMENT PRIMARY KEY,
    nombre_cliente   VARCHAR(50)  NOT NULL,
    placa            VARCHAR(6)   NOT NULL,
    id_espacio       INT          NOT NULL,
    fecha_apartado   DATE         NOT NULL,
    hora_apartado    TIME         NOT NULL,
    estado           ENUM('ACTIVO','CUMPLIDO','CANCELADO') NOT NULL DEFAULT 'ACTIVO',
    fecha_registro   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_apartado_espacio FOREIGN KEY (id_espacio) REFERENCES espacios(id_espacio)
) ENGINE=InnoDB;

-- -----------------------------------------------------
-- Datos iniciales
-- -----------------------------------------------------
INSERT INTO tipos_vehiculo (nombre, tarifa_hora) VALUES
    ('CARRO', 3000.00),
    ('MOTO',  1500.00);

INSERT INTO espacios (codigo, id_tipo) VALUES
    ('A1', 1), ('A2', 1), ('A3', 1), ('A4', 1), ('A5', 1),
    ('B1', 2), ('B2', 2), ('B3', 2), ('B4', 2), ('B5', 2);

-- Datos de ejemplo para pruebas
INSERT INTO vehiculos (placa, id_tipo) VALUES
    ('ABC123', 1),
    ('XYZ98A', 2);

INSERT INTO ingresos (id_vehiculo, id_espacio, fecha_ingreso) VALUES
    (1, 1, NOW() - INTERVAL 2 HOUR);

UPDATE espacios SET estado = 'OCUPADO' WHERE id_espacio = 1;

INSERT INTO apartados (nombre_cliente, placa, id_espacio, fecha_apartado, hora_apartado) VALUES
    ('Juan Pérez', 'DEF456', 2, CURDATE() + INTERVAL 1 DAY, '08:00:00');

UPDATE espacios SET estado = 'APARTADO' WHERE id_espacio = 2;

-- -----------------------------------------------------
-- Consultas útiles para probar
-- -----------------------------------------------------
-- Espacios disponibles
-- SELECT codigo, estado FROM espacios WHERE estado = 'DISPONIBLE';

-- Vehículos que están dentro
-- SELECT v.placa, e.codigo, i.fecha_ingreso
--   FROM ingresos i
--   JOIN vehiculos v ON v.id_vehiculo = i.id_vehiculo
--   JOIN espacios  e ON e.id_espacio  = i.id_espacio
--  WHERE i.fecha_salida IS NULL;

-- Valor a pagar de un ingreso (hora o fracción)
-- SELECT i.id_ingreso,
--        CEIL(TIMESTAMPDIFF(MINUTE, i.fecha_ingreso, NOW()) / 60) AS horas,
--        CEIL(TIMESTAMPDIFF(MINUTE, i.fecha_ingreso, NOW()) / 60) * t.tarifa_hora AS valor
--   FROM ingresos i
--   JOIN vehiculos v ON v.id_vehiculo = i.id_vehiculo
--   JOIN tipos_vehiculo t ON t.id_tipo = v.id_tipo
--  WHERE i.fecha_salida IS NULL;
