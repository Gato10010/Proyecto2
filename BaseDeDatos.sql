/* * Respaldo de Base de Datos - Proyecto Ventas
 * Author: isabe
 */

-- 1. Crear la base de datos si no existe
CREATE DATABASE IF NOT EXISTS sistema_ventas;
USE sistema_ventas;

-- 2. Crear la tabla de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50),
    password VARCHAR(50)
);

-- 3. Insertar usuario de prueba
INSERT INTO usuarios (usuario, password) VALUES ('admin', '1234');