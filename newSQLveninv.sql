-- 1. CREAR BASE DE DATOS (Si no existe)
CREATE DATABASE IF NOT EXISTS sistema_ventas;
USE sistema_ventas;

-- 2. TABLA PRODUCTOS (Inventario)
-- Esta es la que lee tu C_Inventario_Controller
DROP TABLE IF EXISTS productos;
CREATE TABLE productos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigo_barras VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    precio_compra DECIMAL(10,2) DEFAULT 0.00,
    precio_venta DECIMAL(10,2) NOT NULL,
    stock INT DEFAULT 0,
    categoria VARCHAR(50)
);

-- 3. TABLA VENTAS (Para guardar el total del ticket)
DROP TABLE IF EXISTS ventas;
CREATE TABLE ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2),
    pago DECIMAL(10,2),
    cambio DECIMAL(10,2)
);

-- 4. TABLA DETALLE DE VENTAS (Qué productos se vendieron en cada ticket)
DROP TABLE IF EXISTS detalle_ventas;
CREATE TABLE detalle_ventas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT,
    id_producto INT,
    cantidad INT,
    precio_unitario DECIMAL(10,2),
    subtotal DECIMAL(10,2),
    FOREIGN KEY (id_venta) REFERENCES ventas(id),
    FOREIGN KEY (id_producto) REFERENCES productos(id)
);

-- 5. INSERTAR DATOS DE PRUEBA (Para que veas algo en la tabla)
INSERT INTO productos (codigo_barras, nombre, precio_compra, precio_venta, stock, categoria) VALUES 
('750105531188', 'Coca Cola 600ml', 12.00, 18.00, 50, 'Bebidas'),
('750100013303', 'Sabritas Sal 45g', 14.50, 20.00, 30, 'Botanas'),
('750103046221', 'Emperador Chocolate', 15.00, 22.00, 40, 'Galletas'),
('123456789', 'Leche Lala 1L', 24.00, 29.00, 20, 'Lacteos'),
('11223344', 'Cloralex 1L', 18.00, 25.00, 15, 'Limpieza');

-- VERIFICAR QUE SE GUARDARON
SELECT * FROM productos;/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  isabe
 * Created: Dec 6, 2025
 */

