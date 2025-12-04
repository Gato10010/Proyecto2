/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Other/SQLTemplate.sql to edit this template
 */
/**
 * Author:  isabe
 * Created: Dec 2, 2025
 */
CREATE DATABASE sistema_ventas;
USE sistema_ventas;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50),
    password VARCHAR(50)
);

INSERT INTO usuarios (usuario, password) VALUES ('admin', '1234');

CREATE DATABASE sistema_ventas;
USE sistema_ventas;

CREATE TABLE usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario VARCHAR(50),
    password VARCHAR(50)
);

-- Usuario de prueba
INSERT INTO usuarios (usuario, password) VALUES ('admin', '1234');