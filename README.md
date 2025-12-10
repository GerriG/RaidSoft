# 🛒 RaidSoft: Sistema de Punto de Venta y Gestión de Inventario

![Java](https://img.shields.io/badge/Java-24+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.7-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-00000F?style=for-the-badge&logo=mysql&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?style=for-the-badge&logo=thymeleaf&logoColor=white)
![Bootstrap](https://img.shields.io/badge/Bootstrap-563D7C?style=for-the-badge&logo=bootstrap&logoColor=white)

---

## 📖 Descripción del Proyecto

**RaidSoft** es una solución integral de Punto de Venta y Gestión de Inventario desarrollada en Java con Spring Boot, diseñada para optimizar el flujo comercial de negocios minoristas mediante una arquitectura robusta que centraliza la operación en dos roles fundamentales para garantizar la seguridad y eficiencia del negocio.

El sistema permite al **Administrador** ejercer un control total sobre el catálogo mediante la gestión de productos con soporte para imágenes y alertas de stock mínimo, administrar el acceso del personal y utilizar herramientas de inteligencia de negocios para generar reportes PDF detallados sobre la valoración del inventario, rankings de desempeño de vendedores y órdenes de reabastecimiento automático para productos críticos.

Por otro lado, habilita al **Vendedor** con una interfaz de punto de venta ágil para procesar transacciones rápidamente, consultar disponibilidad en tiempo real y emitir comprobantes de venta instantáneos, asegurando así una operación fluida que protege la integridad financiera del comercio mediante validaciones estrictas y roles de seguridad definidos.

---

## 🚀 Características Principales

### 🛡️ Módulo de Administración (Back-Office)
* **Gestión de Inventario:** CRUD completo de productos con imágenes y control de precios (Costo vs. Venta).
* **Inteligencia de Negocios (Reportes PDF):**
    * 📊 **Ranking de Vendedores:** Métricas de desempeño por monto y cantidad.
    * 💰 **Inventario Valorado:** Análisis financiero del stock actual.
    * 📉 **Reabastecimiento Inteligente:** Generación automática de órdenes de compra para productos con stock crítico.
* **Gestión de Usuarios:** Control de acceso basado en roles y auditoría de personal.
* **Seguridad:** Validaciones estrictas para evitar inconsistencias (ej. precios negativos).

### 💼 Módulo de Vendedor (Punto de Venta)
* **POS Ágil:** Interfaz optimizada para ventas rápidas.
* **Facturación:** Emisión instantánea de recibos de venta en PDF.
* **Consulta en Tiempo Real:** Verificación de stock disponible al instante.
* **Historial Personal:** Registro de transacciones propias.

---

## 🛠️ Tecnologías Utilizadas

* **Lenguaje:** Java 24+
* **Framework:** Spring Boot 3.5.7 (Spring Security, Spring Data JPA, Spring Web)
* **Frontend:** Thymeleaf, HTML5, CSS3 (Diseño personalizado), JavaScript
* **Base de Datos:** MySQL
* **Librerías Adicionales:**
    * *OpenPDF:* Para la generación de reportes y recibos.
    * *SweetAlert2:* Para alertas interactivas.
    * *FontAwesome:* Iconografía.

---

## 👥 Equipo de Desarrollo

Este proyecto fue desarrollado durante el ciclo **02-2025** para la cátedra de **Programación IV**.

| Integrante | Rol |
| :--- | :--- |
| **Gerardo Salmerón** | Desarrollador Fullstack |
| **Osaki Arévalo** | Desarrollador Fullstack |
| **Diego Molina** | Desarrollador Fullstack |
| **Alex Martínez** | Desarrollador Fullstack |
| **Adonay García** | Desarrollador Fullstack |

---

## ⚙️ Instalación y Despliegue

1.  Clonar el repositorio:
    ```bash
    git clone [https://github.com/usuario/raidsoft.git](https://github.com/usuario/raidsoft.git)
    ```
2.  Configurar la base de datos MySQL ejecutando el script:
    `raidsoft - BD.sql`
3.  Configurar las credenciales en `application.properties`.
4.  Ejecutar el proyecto:
    ```bash
    mvn spring-boot:run
    ```
5.  Acceder en el navegador: `http://localhost:8080`

---
© 2025 RaidSoft - Todos los derechos reservados.
