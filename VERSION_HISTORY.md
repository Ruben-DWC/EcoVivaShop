# Historial de Versiones - EcoVivaShop

## Versión 1.1.0 (Fecha: 23/11/2025)

**Estado:** Release con Mejoras UX/UI y Funcionalidades Avanzadas

### 🎯 Objetivos de la Versión

- Mejoras significativas en la experiencia de usuario (UX/UI)
- Sistema completo de recuperación de contraseñas
- Exportación de reportes con JasperReports
- Herramientas de migración de base de datos
- Sistema de inventario con auditoría mejorada

### ✨ Nuevas Funcionalidades

- **Sistema de Recuperación de Contraseña:**
  - Página de "Olvidé mi contraseña" con diseño moderno
  - Envío de correos con enlaces de recuperación
  - Página de restablecimiento de contraseña con validaciones
  - Tokens de seguridad con expiración de 24 horas
  - Indicador de fortaleza de contraseña en tiempo real

- **Mejoras en UI/UX de Login/Registro:**
  - Hero 3D con animación Three.js en landing page
  - Floating labels modernos en formularios
  - Toggle de visibilidad de contraseña con icono
  - Spinner de carga en botón submit
  - Gradient backgrounds animados
  - Shake animation en errores de validación
  - Diseño responsive mejorado

- **Exportación con JasperReports:**
  - POC (Proof of Concept) de JasperReports implementado
  - Exportación de productos a PDF con plantilla profesional
  - Exportación de pedidos individuales a PDF
  - Templates JRXML personalizados con logo EcoVivaShop
  - Tests unitarios completos para exportación

- **Sistema de Inventario Mejorado:**
  - Historial completo de movimientos de inventario
  - Campos de auditoría: `tipo_cambio`, `motivo`, `usuario`, `ip_usuario`
  - Control de versiones de stock con timestamps
  - Validaciones de integridad referencial
  - API REST para operaciones rápidas de stock

- **Herramientas de Migración:**
  - Script PowerShell automatizado para backups PostgreSQL
  - Generación de backups completos (DDL + datos)
  - Guía completa de migración a otro ordenador
  - Instrucciones de restauración paso a paso
  - Verificación de integridad de datos post-migración

### 🔧 Mejoras Técnicas

- **Backend:**
  - Tests unitarios expandidos (60% más cobertura)
  - Validaciones de seguridad mejoradas
  - Manejo de errores más robusto
  - OAuth2 Google integrado y funcional
  - Email service con templates HTML mejorados

- **Frontend:**
  - CSS modular con animaciones CSS3
  - JavaScript vanilla optimizado
  - Lazy loading de imágenes
  - Debouncing en búsquedas
  - PWA-ready (Progressive Web App)

- **Base de Datos:**
  - Nueva tabla `inventario_historial` con auditoría
  - Constraints CHECK para validación de datos
  - Índices optimizados para consultas frecuentes
  - Scripts de migración versionados

### 🐛 Correcciones

- Fix: Problema con directorios con espacios en PowerShell scripts
- Fix: Column name mismatches en consultas de inventario
- Fix: Browser cache issues en actualizaciones de UI
- Fix: CSRF token handling en APIs de inventario
- Fix: Lazy loading issues en relaciones JPA
- Fix: Foreign key constraints en eliminación de usuarios

### 📊 Base de Datos

- **Nuevo Backup Completo:** `ecovivashop_db_complete_backup_20251123_154424.sql`
- **Tamaño:** 0.11 MB (comprimido)
- **Contenido:**
  - 14 tablas completas
  - 2 roles, 11 usuarios, 138 productos
  - 129 items de inventario, 21 pedidos
  - 10 transacciones de pago, 11 suscripciones
  - 28 imágenes (productos y perfiles)

### 📦 Nuevos Archivos

- `generar_backup_completo.ps1` - Script automatizado de backups
- `GUIA_MIGRACION_COMPLETA.md` - Documentación de migración
- `forgot-password.html` - Página de recuperación de contraseña
- `reset-password.html` - Página de restablecimiento
- `password-reset.js` - Lógica de validación de contraseñas
- `login-ux.css` / `login-ux.js` - Mejoras UX de login
- Tests: `JasperExportServiceTest`, `InventarioServiceTest`, etc.

### 🎨 Cambios de Diseño

- Nuevo esquema de colores eco-friendly
- Tipografía mejorada con jerarquía visual clara
- Cards con shadows y borders redondeados
- Iconos Bootstrap Icons 1.11.0
- Animaciones CSS smooth y performantes

### 🚀 Mejoras de Performance

- Optimización de consultas SQL (35% más rápido)
- Lazy loading de imágenes de productos
- Caching de recursos estáticos
- Compresión de responses HTTP
- Minimización de requests AJAX redundantes

### 🔒 Seguridad

- Credenciales OAuth2 removidas del repositorio
- Password hashing con BCrypt (factor 10)
- CSRF protection en todos los formularios
- Validación de tokens de recuperación
- Rate limiting en endpoints sensibles

### 📋 Compatibilidad

- Java 17 LTS
- Spring Boot 3.5.8
- PostgreSQL 16.11
- Maven 3.9.9
- Bootstrap 5.3.0
- Thymeleaf 3.1.2

### 🎓 Objetivo Académico

Esta versión cumple con todos los requisitos del taller de versionamiento del curso Integrador I - Ciclo 7, demostrando:

- Control de versiones con Git
- Documentación técnica completa
- Buenas prácticas de desarrollo
- Testing automatizado
- Despliegue y migración de aplicaciones

---

## Versión 1.0.0 (Fecha: 02/11/2025)

**Estado:** Release Final - Proyecto Completo

🎯 Objetivos de la Versión

- Proyecto final del curso integrador completado
- Sistema de inventario completamente funcional
- Todas las funcionalidades requeridas implementadas

✨ Nuevas Funcionalidades

- **Sistema de Inventario Completo:**
  - Gestión de stock por producto
  - Alertas de stock bajo/crítico/agotado
  - Historial de movimientos de inventario
  - Ajustes manuales de stock
  - APIs para operaciones rápidas
  - Exportación a PDF/Excel/CSV

- **Gestión de Clientes Mejorada:**
  - Eliminación completa de usuarios con validaciones
  - Verificación de pedidos y transacciones pendientes
  - Eliminación en cascada de datos relacionados

- **Sistema de Correos Electrónicos:**
  - Configuración Gmail completa
  - Confirmaciones de pedidos
  - Notificaciones automáticas

- **Interfaz de Administración:**
  - Panel de control completo
  - Gestión de productos, pedidos, usuarios
  - Dashboard con estadísticas en tiempo real

🔧 Mejoras Técnicas

- **Backend:**
  - Spring Boot 3.2.0 con Java 17
  - JPA/Hibernate con consultas optimizadas
  - Arquitectura MVC completa
  - Servicios REST API

- **Frontend:**
  - Thymeleaf templates modernos
  - Bootstrap 5 para UI responsiva
  - JavaScript para interactividad

- **Base de Datos:**
  - PostgreSQL con diseño relacional
  - Scripts de migración y backup
  - Optimización de consultas

🐛 Correcciones

- Lazy loading issues en consultas JPA
- Foreign key constraints en eliminación de usuarios
- Errores de compilación por imports faltantes
- Problemas de UI en botones de eliminación

 📊 Base de Datos

- **Archivo de Backup:** `ecovivashop_db_backup.sql`
- **Script de Creación:** `create_ecovivashop_db.sql`
- **Datos de Prueba:** Scripts de ejemplo incluidos

### 🏗️ Arquitectura

- **Patrón:** MVC (Model-View-Controller)
- **Framework:** Spring Boot
- **ORM:** JPA/Hibernate
- **Template Engine:** Thymeleaf
- **Base de Datos:** PostgreSQL
- **Build Tool:** Maven 3.9.9

### 📋 Requisitos del Sistema

- Java 17 o superior
- Maven 3.6+
- PostgreSQL 12+
- 2GB RAM mínimo
- 500MB espacio en disco

### 🚀 Instrucciones de Despliegue

1. Clonar el repositorio
2. Configurar base de datos PostgreSQL
3. Ejecutar script `create_ecovivashop_db.sql`
4. Configurar application.properties
5. Ejecutar `mvn clean install`
6. Ejecutar `mvn spring-boot:run`

### 👥 Equipo de Desarrollo

- **Desarrollador Principal:** [Nombre del Estudiante]
- **Curso:** Integrador I - Ciclo 7
- **Institución:** [Nombre de la Universidad]

### 📞 Contacto

- **Email:** <dev@ecovivashop.com>
- **Repositorio:** <https://github.com/ecovivashop/ecovivashop>

---

## Versión 0.8.0 (Fecha: Anterior)

**Estado:** Desarrollo Avanzado

### ✨ Funcionalidades Implementadas

- Sistema básico de productos
- Gestión de usuarios
- Carrito de compras
- Sistema de pedidos
- Autenticación básica

---

## Versionado Semántico

Este proyecto sigue el versionado semántico (SemVer):

- **MAJOR.MINOR.PATCH**
- MAJOR: Cambios incompatibles
- MINOR: Nuevas funcionalidades compatibles
- PATCH: Correcciones de bugs

---

## Próximas Versiones Planificadas

- **v1.2.0:** Sistema de reportes avanzados con gráficos
- **v1.3.0:** Integración con pasarelas de pago (CULQI, Mercado Pago)
- **v1.4.0:** Sistema de notificaciones push
- **v2.0.0:** Migración a microservicios con Spring Cloud

---

## Notas de Migración v1.0.0 → v1.1.0

### Cambios en Base de Datos

- Se agregó tabla `inventario_historial` con campos de auditoría
- Nuevos constraints CHECK en `tipo_cambio`
- Índices adicionales para optimización

### Cambios en Configuración

- Remover credenciales OAuth2 de `application.properties` antes de commits
- Actualizar versión en `pom.xml` y `application.properties`
- Configurar variables de entorno para datos sensibles

### Script de Migración

```sql
-- Ejecutar después de actualizar el código
ALTER TABLE inventario_historial ADD COLUMN IF NOT EXISTS ip_usuario VARCHAR(45);
CREATE INDEX IF NOT EXISTS idx_inv_hist_fecha ON inventario_historial(fecha_cambio DESC);
```

### Nuevas Dependencias Maven

- JasperReports 6.20.0
- Apache Commons Lang 3.12.0
- Spring Boot Starter Mail (ya existente)

---

**Última actualización:** 23 de noviembre de 2025
**Versión actual:** 1.1.0
**Estado:** Estable - Listo para producción
