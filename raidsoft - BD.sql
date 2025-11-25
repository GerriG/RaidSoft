-- ==========================================================
-- SCRIPT FINAL RAIDSOFT (USUARIOS LIMPIOS + ESTRUCTURA) - CORREGIDO
-- ==========================================================

SET NAMES utf8mb4;
SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO';
SET @OLD_TIME_ZONE=@@TIME_ZONE;

-- ZONA HORARIA DE EL SALVADOR
SET time_zone = '-06:00';

START TRANSACTION;

-- 1. LIMPIEZA DE BASE DE DATOS
DROP DATABASE IF EXISTS `raidsoft`;
CREATE DATABASE IF NOT EXISTS `raidsoft` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `raidsoft`;

-- --------------------------------------------------------
-- 2. ESTRUCTURA DE TABLAS
-- --------------------------------------------------------

-- TABLA CATEGORIAS
DROP TABLE IF EXISTS `categorias`;
CREATE TABLE `categorias` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

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

-- TABLA USUARIOS (SOLO ADMIN, VENDEDOR Y PEDRO)
DROP TABLE IF EXISTS `usuarios`;
CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(255) NOT NULL,
  `rol` enum('ADMINISTRADOR','VENDEDOR') NOT NULL,
  `estado` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `usuarios` (`id_usuario`, `username`, `password`, `rol`, `estado`, `created_at`) VALUES
(6, 'admin', '$2a$10$mu17XyvvbwY4LTMXrZVDXu5vj4/udIxEeUpDq8u/WYzNKvL8JdEQW', 'ADMINISTRADOR', 1, '2025-11-23 04:18:07'),
(7, 'vendedor', '$2a$10$wOFc263FTx.Rbrd5hrDHLOcrP/PcMpwTGGtf5UnV528j0nVRfTKqu', 'VENDEDOR', 1, '2025-11-23 04:18:07'),
(16, 'pedro', '$2a$10$pRQFiSt/LGC2wvrAeoLIm.TqGdSczfpwajKllEmB37mHMINIr5LVm', 'VENDEDOR', 1, '2025-11-25 03:31:56');

-- TABLA PERFILES (SOLO PERFILES DE LOS USUARIOS ANTERIORES)
DROP TABLE IF EXISTS `perfiles`;
CREATE TABLE `perfiles` (
  `id_perfil` bigint(20) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(100) NOT NULL,
  `apellido` varchar(100) NOT NULL,
  `email` varchar(150) DEFAULT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `avatar_url` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_perfil`),
  UNIQUE KEY `id_usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `perfiles` (`id_perfil`, `id_usuario`, `nombre`, `apellido`, `email`, `fecha_nacimiento`, `avatar_url`) VALUES
(2, 6, 'Super', 'Administrador', 'admin@raidsoft.com', '2005-04-17', '/Profiles/f04ee2b5-8e76-4373-9c6b-c87f58a49cc1_133969316527262984.jpg'),
(3, 7, 'Juan', 'Vendedor', 'vendedor@raidsoft.com', NULL, NULL),
(6, 16, 'Pedro', 'Aguilar', 'pedro@raidsoft.com', '2000-01-15', NULL);

-- TABLA PRODUCTOS
DROP TABLE IF EXISTS `productos`;
CREATE TABLE `productos` (
  `id_producto` int(11) NOT NULL AUTO_INCREMENT,
  `codigo_barras` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  `precio_compra` decimal(38,2) DEFAULT NULL,
  `precio_venta` decimal(38,2) DEFAULT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) NOT NULL DEFAULT 5,
  `imagen_url` varchar(255) DEFAULT NULL,
  `estado` tinyint(1) DEFAULT 1,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo_barras` (`codigo_barras`),
  KEY `id_categoria` (`id_categoria`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `productos` (`id_producto`, `codigo_barras`, `nombre`, `descripcion`, `id_categoria`, `precio_compra`, `precio_venta`, `stock`, `stock_minimo`, `imagen_url`, `estado`, `created_at`) VALUES
(1, '196068345001', 'Laptop HP Pavilion 15', 'Procesador Intel Core i5, 8GB RAM, 256GB SSD, Pantalla 15.6\" FHD Antirreflejo', 1, 750.00, 920.00, 10, 2, '/Productos/e7fccd87-11bd-4cb6-9550-c8e1daf99198_d6fcb0d9-5e74-44c4-bbc7-48a9dfd59fef_11651-producto-21598-11.jpg', 1, '2025-11-23 04:18:07'),
(2, '0019291019', 'Mouse Logitech G203', 'Mouse Gamer RGB Lightsync, 6 botones programables, sensor 8000 DPI', 2, 15.00, 25.00, 50, 5, '/Productos/c5d6aa80-2888-4b16-8ca7-a07e33d15c26_11651-producto-21598-11.jpg', 1, '2025-11-23 04:18:07'),
(3, '0021212222', 'Monitor Samsung 24\" IPS', 'Monitor plano sin bordes, resolución 1920x1080, tasa de refresco 75Hz', 3, 110.00, 145.00, 15, 3, '/Productos/5c55683e-b453-4459-9c1f-2e0bf50e0bac_monitor-samsung-24-ips-fhd-100hz-flat.jpg', 1, '2025-11-23 04:18:07'),
(4, '740617305001', 'SSD Kingston 480GB', 'Unidad de estado sólido A400 SATA 3 2.5\", velocidad de lectura 500MB/s', 4, 25.00, 35.00, 40, 5, '/Productos/4e63f91a-d6e0-4090-ad66-629150e2bc77_1701195976_480.png', 1, '2025-11-23 04:18:07'),
(5, '843591095001', 'RAM Corsair Vengeance 8GB', 'Módulo de memoria DDR4 3200MHz LPX con disipador de calor negro', 5, 30.00, 45.00, 30, 5, '/Productos/8239bd74-f4b2-416b-b1b9-8c4a3069e602_corsair_vengeance_rgb_pro_sl_16gb_2x_8gb_ddr4_3600mhz_cl18_memory_black_ac41757_2.webp', 1, '2025-11-23 04:18:07'),
(6, '730143312001', 'Procesador Ryzen 5 5600G', '6 núcleos, 12 hilos, 3.9GHz base, gráficos Radeon integrados, socket AM4', 6, 120.00, 160.00, 8, 2, '/Productos/7c6b2464-7fef-45b0-97d7-6707509389e5_51KHD9nx51S._AC_SL1395_.jpg', 1, '2025-11-23 04:18:07'),
(7, '835168002001', 'Tarjeta de Video RTX 3060', '12GB GDDR6, Dual Fan, Ray Tracing, DLSS, ideal para gaming 1080p/1440p', 7, 280.00, 350.00, 5, 1, '/Productos/83f73b40-70a3-4dc4-853b-b17e02d94715_descarga-18.png', 1, '2025-11-23 04:18:07'),
(8, '843591050002', 'Fuente Corsair CV650', 'Fuente de poder 650W certificada 80 Plus Bronze, ventilador silencioso', 8, 45.00, 65.00, 12, 3, '/Productos/04a7ac5e-e2f7-403d-a56c-5f843a4358ae_corsair_vengeance_rgb_pro_sl_16gb_2x_8gb_ddr4_3600mhz_cl18_memory_black_ac41757_2.webp', 1, '2025-11-23 04:18:07'),
(9, '815671015001', 'Gabinete NZXT H510', 'Torre media ATX compacta, vidrio templado lateral, gestión de cables premium', 9, 70.00, 95.00, 6, 2, '/Productos/104f8cd0-9a61-4431-b709-eed213f2e2c7_sw_sw_07_422.jpg', 1, '2025-11-23 04:18:07'),
(10, '192554001001', 'Silla Gamer Cougar Armor', 'Cuero PVC transpirable, reclinable 180 grados, estructura de acero, 4D', 10, 180.00, 230.00, 4, 1, '/Productos/6cfa80fb-bcb4-461e-9c2a-75d2e33c54ba_Armor-One-V2-1-1.jpg', 1, '2025-11-23 04:18:07'),
(11, '693536408001', 'Router TP-Link Archer C6', 'Router Gigabit Doble banda AC1200, 4 antenas externas, tecnología MU-MIMO', 12, 35.00, 50.00, 20, 3, '/Productos/c2ec07a8-51ab-40d8-92c0-383fb853657d_Archer-AX10-box.webp', 1, '2025-11-23 04:18:07'),
(12, '103439567001', 'Impresora Epson L1250', 'EcoTank sistema de tanque de tinta, impresión inalámbrica Wi-Fi Direct', 13, 160.00, 195.00, 7, 2, '/Productos/37b27e7c-17d9-40d9-9448-6e8ae338be77_jT0860GmtO2hk1Cpb8Sp3cP5vSCeOiDqd7jIVZxx.png', 1, '2025-11-23 04:18:07'),
(13, '097855145001', 'Webcam Logitech C920', 'Resolución Full HD 1080p/30fps, enfoque automático, micrófono estéreo dual', 14, 60.00, 85.00, 10, 2, '/Productos/4ea3960e-d9d4-440f-92f5-ed265f3d8d68_c920.jpg', 1, '2025-11-23 04:18:07'),
(14, '740617305002', 'Microfono HyperX Quadcast', 'Micrófono de condensador USB, suspensión elástica, iluminación RGB roja', 15, 100.00, 135.00, 5, 2, '/Productos/1c732441-cfbf-41fc-b34a-f7c9a11c276c_Comprar-aquí-5.png', 1, '2025-11-23 04:18:07'),
(15, '885370922001', 'Licencia Windows 11 Pro', 'Licencia digital original OEM para 1 PC, 64 bits, activación permanente', 16, 15.00, 25.00, 100, 10, '/Productos/412f1638-eb78-43b2-8e6e-29fa71df211f_PT_RGB_Windows11_Pro_EN_375x375.avif', 1, '2025-11-23 04:18:07'),
(16, '754554888001', 'Cable HDMI 2.0 4K 2m', 'Cable reforzado de alta velocidad, soporta 4K 60Hz, conectores chapados', 17, 3.00, 8.00, 60, 10, '/Productos/6caa6e0f-9c14-439d-b928-b9e4402b2792_999127339-3.jpg', 1, '2025-11-23 04:18:07'),
(17, '092636333001', 'Mochila Targo Targus', 'Diseño ergonómico, compartimentos acolchados para laptop hasta 15.6\"', 18, 25.00, 40.00, 15, 3, '/Productos/ac75e5eb-56e3-4150-9826-717751b5250d_34818.jpg', 1, '2025-11-23 04:18:07'),
(18, '750105999001', 'Aire Comprimido 500ml', 'Gas comprimido de alta pureza, ideal para limpieza de teclados y PCs', 19, 4.00, 7.50, 30, 5, '/Productos/ab6d4048-57e5-4032-a826-1dd0e1da0357_image.jpg', 1, '2025-11-23 04:18:07'),
(19, '884102044001', 'Disipador Cooler Master 212', 'Ventilador 120mm PWM, 4 heatpipes de contacto directo, LED rojo', 20, 35.00, 50.00, 10, 2, '/Productos/d875f586-2191-492e-ac0d-cc22e243cae8_Cooler Master Disipador Hyper 212 Halo Black RR-S4KK-20PA-R1 .png', 1, '2025-11-23 04:18:07'),
(20, '887276555001', 'Samsung Galaxy Tab S8 Ultra', 'Pantalla 14.6\" Super AMOLED, S Pen incluido, 128GB almacenamiento', 21, 180.00, 820.00, 8, 2, '/Productos/220e0c36-f1bf-4bde-b6bf-b29b2c9741e1_51sWD2-949L.jpg', 1, '2025-11-23 04:18:07'),
(21, '695037673001', 'Teclado Mecánico Redragon', 'Switch Blue mecánico, retroiluminación RGB, diseño TKL compacto', 2, 35.00, 55.00, 25, 4, '/Productos/f549a120-970f-4637-9df5-6d7d39fd79df_c7uk0qcs_d57502a4_thumbnail_512.jpg', 1, '2025-11-23 04:18:07'),
(22, '880609122002', 'Monitor LG 27\" 144Hz', 'Monitor Ultragear, 1ms de respuesta, AMD FreeSync, panel TN', 3, 200.00, 260.00, 6, 2, '/Productos/b7240d2e-a4ac-4dff-8cb5-f419e3302795_1702305854747-MKZC7JFPP6-1-1.webp', 1, '2025-11-23 04:18:07'),
(23, '763649114001', 'HDD Seagate SkyHawk 1TB', 'Disco duro optimizado para videovigilancia y trabajo continuo 24/7', 4, 35.00, 50.00, 20, 5, '/Productos/0004dd0b-9712-467a-9a31-611cdf7e86cb_images.jpg', 1, '2025-11-23 04:18:07'),
(24, '740617305003', 'RAM DDR4 Kingston Fury 16GB', 'Kit 16GB (2x8) DDR4 3600MHz Beast Black, perfil XMP 2.0', 5, 50.00, 75.00, 18, 4, '/Productos/babdb060-e115-40f5-98b2-0b3bae78f458_6d168b274b8f88f265a497d4fdff8604.jpg', 1, '2025-11-23 04:18:07'),
(25, '735858447001', 'Procesador Intel i5 12400F', '6 núcleos, 12 hilos, hasta 4.4GHz, requiere tarjeta gráfica dedicada', 6, 140.00, 180.00, 15, 2, '/Productos/a7f910a3-455b-4052-8bd5-cbf4e2d69405_product-jpeg-500x500-1.webp', 1, '2025-11-23 04:18:07'),
(26, '731304333001', 'Regleta APC 6 Tomas', 'Protección contra sobretensiones, interruptor de rearme, cable 1.8m', 23, 12.00, 18.00, 35, 5, '/Productos/1bfd0e0c-bde0-4de7-9aa0-563e8f7b91a7_Regleta_APC_SurgeArrest_PE63_02.jpg', 1, '2025-11-23 04:18:07'),
(27, '103439555002', 'Proyector Epson X49', '3600 lúmenes, resolución XGA nativa, conectividad HDMI y VGA', 24, 350.00, 420.00, 3, 1, '/Productos/75e3aba0-18f2-49b5-adf2-05fb5225ab5a_X49-4.jpg', 1, '2025-11-23 04:18:07'),
(28, '097855444002', 'Parlantes Logitech Z313', 'Sistema 2.1 con subwoofer compacto, pod de control de volumen y auriculares', 25, 40.00, 60.00, 12, 3, '/Productos/59656814-6a89-4106-9863-06fb43589f8e_z313m.webp', 1, '2025-11-23 04:18:07'),
(29, '045496882001', 'Nintendo Switch OLED', 'Pantalla OLED 7 pulgadas, 64GB almacenamiento, dock con puerto LAN', 26, 300.00, 380.00, 4, 1, '/Productos/a6a3cb34-279c-43fb-8c17-0d174baaa499_Nintendo-Switch-Oled-White_1.jpg.webp', 1, '2025-11-23 04:18:07'),
(30, '887276111002', 'Cargador Carga Rápida (25W) Samsung', 'Adaptador de viaje USB-C, Super Fast Charging, incluye cable C a C', 27, 15.00, 25.00, 40, 5, '/Productos/5507d8ed-1c9c-4915-90ef-381c91010b1e_466108500017_2.webp', 1, '2025-11-23 04:18:07');

-- TABLA COMPRAS
DROP TABLE IF EXISTS `compras`;
CREATE TABLE `compras` (
  `id_compra` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) NOT NULL,
  `proveedor` varchar(150) DEFAULT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `total` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_compra`),
  KEY `id_usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT INTO `compras` (`id_compra`, `id_usuario`, `proveedor`, `total`) VALUES
(1, 6, 'Ingram Micro', 7500.00),
(2, 6, 'Intcomex', 300.00),
(3, 6, 'Tecnología S.A.', 1100.00),
(4, 6, 'PC Mayorista', 1000.00),
(5, 6, 'Amazon Imports', 1500.00),
(6, 6, 'Global Parts', 2400.00),
(7, 6, 'Logitech Direct', 500.00),
(8, 6, 'Kingston Latam', 600.00),
(9, 6, 'AMD Distributors', 3000.00),
(10, 6, 'Oficina Total', 900.00),
(11, 6, 'Ingram Micro', 1200.00),
(12, 6, 'Intcomex', 850.00),
(13, 6, 'Tecnología S.A.', 450.00),
(14, 6, 'PC Mayorista', 3200.00),
(15, 6, 'Amazon Imports', 150.00),
(16, 6, 'Global Parts', 2100.00),
(17, 6, 'Logitech Direct', 75.00),
(18, 6, 'Kingston Latam', 400.00),
(19, 6, 'AMD Distributors', 280.00),
(20, 6, 'Oficina Total', 5000.00),
(21, 6, 'Ingram Micro', 620.00),
(22, 6, 'Intcomex', 990.00),
(23, 6, 'Tecnología S.A.', 150.00),
(24, 6, 'PC Mayorista', 300.00),
(25, 6, 'Amazon Imports', 2500.00);

-- TABLA DETALLE_COMPRAS
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

-- TABLA VENTAS
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

INSERT INTO `ventas` (`id_venta`, `id_usuario`, `fecha`, `total`, `cliente_nombre`) VALUES
(1, 7, '2025-11-23 04:18:07', 920.00, 'Juan Pérez'),
(2, 7, '2025-11-23 04:18:07', 50.00, 'María López'),
(3, 7, '2025-11-23 04:18:07', 145.00, 'Carlos Ruiz'),
(4, 7, '2025-11-23 04:18:07', 70.00, 'Ana Martínez'),
(5, 7, '2025-11-23 04:18:07', 90.00, 'Pedro Aguilar'),
(6, 7, '2025-11-23 04:18:07', 320.00, 'Empresa XYZ'),
(7, 7, '2025-11-23 04:18:07', 350.00, 'Gamer Pro'),
(8, 7, '2025-11-23 04:18:07', 65.00, 'Sofía Méndez'),
(9, 7, '2025-11-23 04:18:07', 190.00, 'Roberto Gómez'),
(10, 7, '2025-11-23 04:18:07', 460.00, 'Lucía Torres'),
(11, 7, '2025-11-23 04:18:07', 50.00, 'Miguel Ángel'),
(12, 7, '2025-11-23 04:18:07', 195.00, 'Escuela Local'),
(13, 7, '2025-11-23 04:18:07', 170.00, 'Streamer 01'),
(14, 7, '2025-11-23 04:18:07', 135.00, 'Podcaster Joy'),
(15, 7, '2025-11-23 04:18:07', 25.00, 'Usuario Casual'),
(16, 7, '2025-11-23 04:18:07', 16.00, 'Cliente Rápido'),
(17, 7, '2025-11-23 04:18:07', 40.00, 'Estudiante UTEC'),
(18, 7, '2025-11-23 04:18:07', 15.00, 'Mantenimiento PC'),
(19, 7, '2025-11-23 04:18:07', 50.00, 'Overclock Fan'),
(20, 7, '2025-11-23 04:18:07', 220.00, 'Diseñador Gráfico'),
(21, 7, '2025-11-23 04:18:07', 110.00, 'Oficina Contable'),
(22, 7, '2025-11-23 04:18:07', 520.00, 'Editor de Video'),
(23, 7, '2025-11-23 04:18:07', 50.00, 'Backup Cliente'),
(24, 7, '2025-11-23 04:18:07', 150.00, 'Upgrade PC'),
(25, 6, '2025-11-23 04:18:07', 180.00, 'Venta Directa Admin'),
(26, 7, '2025-11-24 03:06:49', 15.00, NULL),
(27, 7, '2025-11-24 03:46:55', 8.00, NULL);

-- TABLA DETALLE_VENTAS
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
-- Nota: se eliminaron dos filas malformadas que tenían 5 valores. Si deseas que las ventas 26 y 27 tengan detalle, indícame producto/cantidad/precio y las agrego.

-- TABLA MOVIMIENTOS
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

-- --------------------------------------------------------
-- 3. RELACIONES (CLAVES FORÁNEAS)
-- --------------------------------------------------------

ALTER TABLE `perfiles`
  ADD CONSTRAINT `perfiles_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`) ON DELETE CASCADE;

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

SET TIME_ZONE=@OLD_TIME_ZONE;
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;