-- CONFIGURACIÓN INICIAL
SET NAMES utf8mb4;
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
SET @OLD_TIME_ZONE=@@TIME_ZONE;

-- ZONA HORARIA DE EL SALVADOR
SET time_zone = '-06:00';

START TRANSACTION;

-- CREACIÓN DE BD
CREATE DATABASE IF NOT EXISTS `raidsoft` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `raidsoft`;

-- ==========================================
-- 1. TABLA CATEGORIAS
-- ==========================================
DROP TABLE IF EXISTS `categorias`;
CREATE TABLE `categorias` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertando 27 registros (2 originales + 25 nuevos)
INSERT INTO `categorias` (`id_categoria`, `nombre`, `descripcion`) VALUES
(1, 'Laptops', 'Equipos portátiles de todas las gamas'),
(2, 'Periféricos', 'Teclados, mouse, audífonos'),
(3, 'Monitores', 'Pantallas LED, IPS y Curvas'),
(4, 'Almacenamiento', 'SSD, HDD, M.2 y USB'),
(5, 'Memorias RAM', 'DDR4 y DDR5 para PC y Laptop'),
(6, 'Procesadores', 'Intel y AMD'),
(7, 'Tarjetas de Video', 'NVIDIA y Radeon'),
(8, 'Fuentes de Poder', 'Certificadas 80 Plus y Genéricas'),
(9, 'Gabinetes', 'Torres ATX, Micro ATX y Mini ITX'),
(10, 'Sillas Gamer', 'Ergonómicas y de oficina'),
(11, 'Escritorios', 'Mesas gaming y estaciones de trabajo'),
(12, 'Redes', 'Routers, Switches y Cables Ethernet'),
(13, 'Impresoras', 'Multifuncionales y Tóner'),
(14, 'Cámaras Web', 'HD y 4K para streaming'),
(15, 'Micrófonos', 'Condensador y USB'),
(16, 'Software', 'Licencias de Windows y Antivirus'),
(17, 'Cables y Adaptadores', 'HDMI, DisplayPort, VGA'),
(18, 'Mochilas', 'Para laptops de 15 y 17 pulgadas'),
(19, 'Limpieza', 'Aire comprimido y alcohol isopropílico'),
(20, 'Refrigeración', 'Ventiladores y Disipadores líquidos'),
(21, 'Tablets', 'Android y iPad'),
(22, 'Servidores', 'Equipos para empresas'),
(23, 'UPS y Regletas', 'Protección eléctrica'),
(24, 'Proyectores', 'Para oficina y cine en casa'),
(25, 'Parlantes', 'Sistemas 2.1 y Barras de sonido'),
(26, 'Consolas', 'PlayStation, Xbox y Nintendo'),
(27, 'Accesorios Celular', 'Cargadores y Cables');

-- ==========================================
-- 2. TABLA USUARIOS (SOLO LOS EXISTENTES)
-- ==========================================
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `imagen_url` varchar(255) DEFAULT NULL,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` enum('ADMINISTRADOR','VENDEDOR') NOT NULL,
  `estado` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Mantenemos tus usuarios originales cifrados
INSERT INTO `usuarios` (`id_usuario`, `nombre`, `apellido`, `imagen_url`, `username`, `password`, `rol`, `estado`, `created_at`) VALUES
(5, 'Test', 'Test', NULL, 'Test', '$2a$10$SANBbxD/uHeIaZMvL40GSe.MeAgj8No.mxax1V5CTcWFdEDBDAcLm', 'VENDEDOR', 1, '2025-11-19 22:45:12'),
(6, 'Administrador', 'Administrador', NULL, 'admin', '$2a$10$mu17XyvvbwY4LTMXrZVDXu5vj4/udIxEeUpDq8u/WYzNKvL8JdEQW', 'ADMINISTRADOR', 1, '2025-11-19 22:47:32'),
(7, 'Vendedor', 'Vendedor', NULL, 'vendedor', '$2a$10$wOFc263FTx.Rbrd5hrDHLOcrP/PcMpwTGGtf5UnV528j0nVRfTKqu', 'VENDEDOR', 1, '2025-11-19 22:47:54');

-- ==========================================
-- 3. TABLA PRODUCTOS
-- ==========================================
DROP TABLE IF EXISTS `productos`;
CREATE TABLE `productos` (
  `id_producto` int(11) NOT NULL AUTO_INCREMENT,
  `codigo_barras` varchar(50) DEFAULT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  `precio_compra` decimal(10,2) NOT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) NOT NULL DEFAULT 5,
  `imagen_url` varchar(255) DEFAULT NULL,
  `estado` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo_barras` (`codigo_barras`),
  KEY `id_categoria` (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertando 30 productos variados
INSERT INTO `productos` (`id_producto`, `nombre`, `id_categoria`, `precio_compra`, `precio_venta`, `stock`, `stock_minimo`) VALUES
(1, 'Laptop HP Pavilion 15', 1, 750.00, 920.00, 10, 2),
(2, 'Mouse Logitech G203', 2, 15.00, 25.00, 50, 5),
(3, 'Monitor Samsung 24\" IPS', 3, 110.00, 145.00, 15, 3),
(4, 'SSD Kingston 480GB', 4, 25.00, 35.00, 40, 5),
(5, 'RAM Corsair Vengeance 8GB', 5, 30.00, 45.00, 30, 5),
(6, 'Procesador Ryzen 5 5600G', 6, 120.00, 160.00, 8, 2),
(7, 'Tarjeta de Video RTX 3060', 7, 280.00, 350.00, 5, 1),
(8, 'Fuente Corsair CV650', 8, 45.00, 65.00, 12, 3),
(9, 'Gabinete NZXT H510', 9, 70.00, 95.00, 6, 2),
(10, 'Silla Gamer Cougar Armor', 10, 180.00, 230.00, 4, 1),
(11, 'Router TP-Link Archer C6', 12, 35.00, 50.00, 20, 3),
(12, 'Impresora Epson L3210', 13, 160.00, 195.00, 7, 2),
(13, 'Webcam Logitech C920', 14, 60.00, 85.00, 10, 2),
(14, 'Microfono HyperX Quadcast', 15, 100.00, 135.00, 5, 2),
(15, 'Licencia Windows 11 Pro', 16, 15.00, 25.00, 100, 10),
(16, 'Cable HDMI 2.0 2m', 17, 3.00, 8.00, 60, 10),
(17, 'Mochila Targo Targus', 18, 25.00, 40.00, 15, 3),
(18, 'Aire Comprimido 500ml', 19, 4.00, 7.50, 30, 5),
(19, 'Disipador Cooler Master 212', 20, 35.00, 50.00, 10, 2),
(20, 'Tablet Samsung Tab A8', 21, 180.00, 220.00, 8, 2),
(21, 'Teclado Mecánico Redragon', 2, 35.00, 55.00, 25, 4),
(22, 'Monitor LG 27\" 144Hz', 3, 200.00, 260.00, 6, 2),
(23, 'HDD Seagate 1TB', 4, 35.00, 50.00, 20, 5),
(24, 'RAM Kingston Fury 16GB', 5, 50.00, 75.00, 18, 4),
(25, 'Procesador Intel i5 12400F', 6, 140.00, 180.00, 7, 2),
(26, 'Regleta APC 6 Tomas', 23, 12.00, 18.00, 35, 5),
(27, 'Proyector Epson X49', 24, 350.00, 420.00, 3, 1),
(28, 'Parlantes Logitech Z313', 25, 40.00, 60.00, 12, 3),
(29, 'Nintendo Switch OLED', 26, 300.00, 380.00, 4, 1),
(30, 'Cargador Carga Rápida Samsung', 27, 15.00, 25.00, 40, 5);

-- ==========================================
-- 4. TABLA COMPRAS
-- ==========================================
DROP TABLE IF EXISTS `compras`;
CREATE TABLE `compras` (
  `id_compra` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `proveedor` varchar(150) DEFAULT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `total` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_compra`),
  KEY `id_usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertando 25 compras (Asociadas al usuario ID 6 - Administrador)
INSERT INTO `compras` (`id_usuario`, `proveedor`, `total`) VALUES
(6, 'Ingram Micro', 7500.00),
(6, 'Intcomex', 300.00),
(6, 'Tecnología S.A.', 1100.00),
(6, 'PC Mayorista', 1000.00),
(6, 'Amazon Imports', 1500.00),
(6, 'Global Parts', 2400.00),
(6, 'Logitech Direct', 500.00),
(6, 'Kingston Latam', 600.00),
(6, 'AMD Distributors', 3000.00),
(6, 'Oficina Total', 900.00),
(6, 'Ingram Micro', 1200.00),
(6, 'Intcomex', 850.00),
(6, 'Tecnología S.A.', 450.00),
(6, 'PC Mayorista', 3200.00),
(6, 'Amazon Imports', 150.00),
(6, 'Global Parts', 2100.00),
(6, 'Logitech Direct', 75.00),
(6, 'Kingston Latam', 400.00),
(6, 'AMD Distributors', 280.00),
(6, 'Oficina Total', 5000.00),
(6, 'Ingram Micro', 620.00),
(6, 'Intcomex', 990.00),
(6, 'Tecnología S.A.', 150.00),
(6, 'PC Mayorista', 300.00),
(6, 'Amazon Imports', 2500.00);

-- ==========================================
-- 5. TABLA DETALLE_COMPRAS
-- ==========================================
DROP TABLE IF EXISTS `detalle_compras`;
CREATE TABLE `detalle_compras` (
  `id_detalle` int(11) NOT NULL AUTO_INCREMENT,
  `id_compra` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `costo_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) GENERATED ALWAYS AS (`cantidad` * `costo_unitario`) STORED,
  PRIMARY KEY (`id_detalle`),
  KEY `id_compra` (`id_compra`),
  KEY `id_producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Detalles aleatorios para las compras generadas
INSERT INTO `detalle_compras` (`id_compra`, `id_producto`, `cantidad`, `costo_unitario`) VALUES
(1, 1, 10, 750.00),
(2, 2, 20, 15.00),
(3, 3, 10, 110.00),
(4, 4, 40, 25.00),
(5, 5, 50, 30.00),
(6, 6, 20, 120.00),
(7, 21, 10, 35.00),
(8, 24, 12, 50.00),
(9, 7, 10, 280.00),
(10, 9, 10, 70.00),
(11, 12, 5, 160.00),
(12, 13, 10, 60.00),
(13, 8, 10, 45.00),
(14, 22, 10, 200.00),
(15, 16, 50, 3.00),
(16, 10, 5, 180.00),
(17, 18, 10, 4.00),
(18, 23, 10, 35.00),
(19, 19, 8, 35.00),
(20, 27, 10, 350.00),
(21, 11, 15, 35.00),
(22, 14, 5, 100.00),
(23, 26, 10, 12.00),
(24, 30, 20, 15.00),
(25, 29, 5, 300.00);

-- ==========================================
-- 6. TABLA VENTAS
-- ==========================================
DROP TABLE IF EXISTS `ventas`;
CREATE TABLE `ventas` (
  `id_venta` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `total` decimal(10,2) NOT NULL,
  `cliente_nombre` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`id_venta`),
  KEY `id_usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertando 25 ventas (Usuario 7 es Vendedor, Usuario 6 es Admin)
INSERT INTO `ventas` (`id_usuario`, `total`, `cliente_nombre`) VALUES
(7, 920.00, 'Juan Pérez'),
(7, 50.00, 'María López'),
(7, 145.00, 'Carlos Ruiz'),
(7, 70.00, 'Ana Martínez'),
(7, 90.00, 'Pedro Aguilar'),
(7, 320.00, 'Empresa XYZ'),
(7, 350.00, 'Gamer Pro'),
(7, 65.00, 'Sofía Méndez'),
(7, 190.00, 'Roberto Gómez'),
(7, 460.00, 'Lucía Torres'),
(7, 50.00, 'Miguel Ángel'),
(7, 195.00, 'Escuela Local'),
(7, 170.00, 'Streamer 01'),
(7, 135.00, 'Podcaster Joy'),
(7, 25.00, 'Usuario Casual'),
(7, 16.00, 'Cliente Rápido'),
(7, 40.00, 'Estudiante UTEC'),
(7, 15.00, 'Mantenimiento PC'),
(7, 50.00, 'Overclock Fan'),
(7, 220.00, 'Diseñador Gráfico'),
(7, 110.00, 'Oficina Contable'),
(7, 520.00, 'Editor de Video'),
(7, 50.00, 'Backup Cliente'),
(7, 150.00, 'Upgrade PC'),
(6, 180.00, 'Venta Directa Admin');

-- ==========================================
-- 7. TABLA DETALLE_VENTAS
-- ==========================================
DROP TABLE IF EXISTS `detalle_ventas`;
CREATE TABLE `detalle_ventas` (
  `id_detalle` int(11) NOT NULL AUTO_INCREMENT,
  `id_venta` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) GENERATED ALWAYS AS (`cantidad` * `precio_unitario`) STORED,
  PRIMARY KEY (`id_detalle`),
  KEY `id_venta` (`id_venta`),
  KEY `id_producto` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `detalle_ventas` (`id_venta`, `id_producto`, `cantidad`, `precio_unitario`) VALUES
(1, 1, 1, 920.00),
(2, 2, 2, 25.00),
(3, 3, 1, 145.00),
(4, 4, 2, 35.00),
(5, 5, 2, 45.00),
(6, 6, 2, 160.00),
(7, 7, 1, 350.00),
(8, 8, 1, 65.00),
(9, 9, 2, 95.00),
(10, 10, 2, 230.00),
(11, 11, 1, 50.00),
(12, 12, 1, 195.00),
(13, 13, 2, 85.00),
(14, 14, 1, 135.00),
(15, 15, 1, 25.00),
(16, 16, 2, 8.00),
(17, 17, 1, 40.00),
(18, 18, 2, 7.50),
(19, 19, 1, 50.00),
(20, 20, 1, 220.00),
(21, 21, 2, 55.00),
(22, 22, 2, 260.00),
(23, 23, 1, 50.00),
(24, 24, 2, 75.00),
(25, 25, 1, 180.00);

-- ==========================================
-- 8. TABLA MOVIMIENTOS
-- ==========================================
DROP TABLE IF EXISTS `movimientos`;
CREATE TABLE `movimientos` (
  `id_movimiento` int(11) NOT NULL AUTO_INCREMENT,
  `id_producto` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `tipo` enum('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  `cantidad` int(11) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `observacion` text DEFAULT NULL,
  PRIMARY KEY (`id_movimiento`),
  KEY `id_producto` (`id_producto`),
  KEY `id_usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Insertando 25 movimientos (Mezcla de entradas por compra y salidas por venta)
INSERT INTO `movimientos` (`id_producto`, `id_usuario`, `tipo`, `cantidad`, `observacion`) VALUES
(1, 6, 'ENTRADA', 10, 'Compra Inicial'),
(2, 6, 'ENTRADA', 50, 'Stock Periféricos'),
(3, 6, 'ENTRADA', 15, 'Llegada de monitores'),
(4, 6, 'ENTRADA', 40, 'Lote SSD'),
(5, 6, 'ENTRADA', 30, 'Lote RAM'),
(1, 7, 'SALIDA', 1, 'Venta Factura #001'),
(2, 7, 'SALIDA', 2, 'Venta Factura #002'),
(3, 7, 'SALIDA', 1, 'Venta Factura #003'),
(6, 6, 'ENTRADA', 20, 'Procesadores nuevos'),
(7, 6, 'ENTRADA', 10, 'Tarjetas gráficas'),
(4, 7, 'SALIDA', 2, 'Venta Cliente Frecuente'),
(5, 7, 'SALIDA', 2, 'Armado de PC'),
(8, 6, 'ENTRADA', 12, 'Fuentes de poder'),
(9, 6, 'ENTRADA', 10, 'Gabinetes'),
(10, 6, 'ENTRADA', 5, 'Sillas muestra'),
(10, 6, 'AJUSTE', -1, 'Silla dañada en bodega'),
(11, 6, 'ENTRADA', 20, 'Routers'),
(12, 6, 'ENTRADA', 7, 'Impresoras'),
(20, 6, 'ENTRADA', 8, 'Tablets'),
(20, 7, 'SALIDA', 1, 'Venta Tablet'),
(21, 6, 'ENTRADA', 25, 'Teclados mecánicos'),
(25, 6, 'ENTRADA', 7, 'Procesadores Intel'),
(29, 6, 'ENTRADA', 4, 'Consolas'),
(29, 7, 'SALIDA', 1, 'Venta Consola'),
(15, 6, 'AJUSTE', 10, 'Licencias digitales agregadas');

-- ==========================================
-- RELACIONES (CLAVES FORÁNEAS)
-- ==========================================
ALTER TABLE `compras`
  ADD CONSTRAINT `compras_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`);

ALTER TABLE `detalle_compras`
  ADD CONSTRAINT `detalle_compras_ibfk_1` FOREIGN KEY (`id_compra`) REFERENCES `compras` (`id_compra`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_compras_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`);

ALTER TABLE `detalle_ventas`
  ADD CONSTRAINT `detalle_ventas_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`) ON DELETE CASCADE,
  ADD CONSTRAINT `detalle_ventas_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`);

ALTER TABLE `movimientos`
  ADD CONSTRAINT `movimientos_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  ADD CONSTRAINT `movimientos_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`);

ALTER TABLE `productos`
  ADD CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`) ON DELETE SET NULL;

ALTER TABLE `ventas`
  ADD CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`);

COMMIT;

-- RESTAURAR CONFIGURACIÓN ORIGINAL
SET TIME_ZONE=@OLD_TIME_ZONE;
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;