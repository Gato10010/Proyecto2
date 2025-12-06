-- 1. Crear la base de datos (si no existe aún)
CREATE DATABASE IF NOT EXISTS sistema_ventas;

-- 2. Usar la base de datos
USE sistema_ventas;

-- 3. Crear la tabla 'personal'
-- Nota: Los nombres de las columnas coinciden EXACTAMENTE con tu código Java
CREATE TABLE IF NOT EXISTS personal (
    id INT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100),
    edad INT,
    sexo VARCHAR(20),
    telefono VARCHAR(20),
    categoria VARCHAR(50) NOT NULL,
    PRIMARY KEY (id)
);

-- (OPCIONAL) Insertar un empleado de prueba para ver datos de inmediato
INSERT INTO personal (nombre, apellido_paterno, apellido_materno, edad, sexo, telefono, categoria)
VALUES ('Juan', 'Perez', 'Lopez', 30, 'Masculino', '5551234567', 'Administrador');/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  isabe
 * Created: Dec 6, 2025
 */

