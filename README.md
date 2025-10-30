
# 🌱 EcoVivaShop - Sistema de Gestión de Tienda Virtual

**EcoVivaShop** es un sistema completo de gestión de tienda virtual enfocado en productos eco-sostenibles, desarrollado con Spring Boot, PostgreSQL y tecnologías web modernas.

## 📋 Características Principales

### 🛒 **Portal de Cliente**

- ✅ Registro y autenticación de usuarios
- ✅ Catálogo de productos eco-sostenibles
- ✅ Carrito de compras inteligente
- ✅ Gestión de perfil y configuración
- ✅ Historial de pedidos
- ✅ Sistema de suscripciones premium
- ✅ Subida y gestión de foto de perfil

### 🔧 **Panel Administrativo**

- ✅ Dashboard con estadísticas en tiempo real
- ✅ Gestión completa de productos
- ✅ Control de inventario y stock
- ✅ Administración de usuarios y roles
- ✅ Gestión de pedidos y pagos
- ✅ Reportes y análisis de ventas
- ✅ Sistema de alertas automáticas

### 📧 **Sistema de Comunicaciones**

- ✅ Envío de emails automatizado
- ✅ Notificaciones de bienvenida
- ✅ Confirmaciones de pedido
- ✅ Integración con Gmail SMTP

## 🛠️ Tecnologías Utilizadas

- **Backend:** Spring Boot 3.x, Spring Security, Spring Data JPA
- **Base de Datos:** PostgreSQL 14+
- **Frontend:** Thymeleaf, Bootstrap 5, JavaScript
- **Seguridad:** BCrypt para encriptación de contraseñas
- **Email:** Spring Mail con soporte SMTP
- **Build Tool:** Maven
- **Java:** JDK 17+

## 📦 Estructura del Proyecto

```bash
ecovivashop/
├── src/main/java/com/ecovivashop/
│   ├── controller/          # Controladores REST y Web
│   ├── entity/             # Entidades JPA
│   ├── repository/         # Repositorios de datos
│   ├── service/            # Lógica de negocio
│   └── config/             # Configuraciones
├── src/main/resources/
│   ├── templates/          # Plantillas Thymeleaf
│   ├── static/             # CSS, JS, imágenes
│   └── application.properties
├── script_completo_base_datos.sql  # Script completo de BD
└── documentación/          # Archivos de documentación
```

## 🚀 Instalación y Configuración

### Prerrequisitos

- Java JDK 17+
- PostgreSQL 16+
- Maven 3.6+
- Git

### 1. Clonar el repositorio

```bash
git clone https://github.com/Ruben-DWC/EcoVivaShop.git
cd EcoVivaShop
```

### 2. Configurar la base de datos

```sql
-- Crear base de datos
CREATE DATABASE ecovivashop_db;

-- Ejecutar el script completo
\i script_completo_base_datos.sql
```

### 3. Configurar application.properties

```properties
# Base de datos
spring.datasource.url=jdbc:postgresql://localhost:5432/ecovivashop_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña

# Email (opcional)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_app_password
```

### 4. Ejecutar la aplicación

```bash
mvn spring-boot:run
```

### 5. Acceder al sistema

- **URL:** <http://localhost:8081>
- **Admin:** <admin@ecovivashop.com> / admin123
- **Cliente:** <cliente@test.com> / cliente123

## 📊 Base de Datos

El proyecto incluye un script SQL completo (`script_completo_base_datos.sql`) con:

- ✅ 8 tablas principales con relaciones
- ✅ Índices para optimización
- ✅ Triggers para auditoría
- ✅ Vistas para reportes
- ✅ Datos iniciales de prueba
- ✅ Funciones útiles para el sistema

### Principales Tablas

- `tb_usuario` - Gestión de usuarios
- `tb_producto` - Catálogo de productos
- `tb_pedido` - Órdenes de compra
- `tb_inventario` - Control de stock
- `tb_pago` - Transacciones
- `tb_rol` - Roles del sistema

## 🔐 Seguridad

- **Autenticación:** Spring Security con formularios
- **Autorización:** Control de acceso basado en roles
- **Passwords:** Encriptación BCrypt
- **CSRF:** Protección habilitada
- **SQL Injection:** Prevención con JPA/Hibernate

## 📱 Funcionalidades Destacadas

### Para Clientes

- Registro con datos completos (nombre, apellido, DNI, teléfono, dirección)
- Subida de foto de perfil
- Edición de información personal
- Navegación intuitiva y responsive

### Para Administradores

- Dashboard con métricas en tiempo real
- Gestión completa de productos e inventario
- Control de usuarios y pedidos
- Reportes de ventas y estadísticas

## 🌱 Enfoque Eco-Sostenible

EcoVivaShop está diseñado específicamente para:

- Productos eco-sostenibles y amigables con el medio ambiente
- Métricas de impacto ambiental
- Puntuaciones ecológicas
- Promoción de consumo responsable

## � Avances del Proyecto

### 🎯 **Avance 80% - Sistema Completo (v0.8.0)**

**Estado:** ✅ Completado (80% del proyecto total)

**Funcionalidades Implementadas:**

- ✅ Sistema de autenticación completo (registro, login, logout)
- ✅ Catálogo de productos con filtros dinámicos
- ✅ Carrito de compras inteligente
- ✅ Panel administrativo con dashboard
- ✅ Gestión completa de productos e inventario
- ✅ Sistema de pedidos y pagos
- ✅ Envío automatizado de emails
- ✅ Exportación de datos a Excel/PDF
- ✅ Interfaz responsive y moderna
- ✅ Base de datos PostgreSQL completa
- ✅ Seguridad implementada con Spring Security

**Funcionalidades Pendientes (20% restante):**

- 🔄 Integración con pasarelas de pago reales
- 🔄 Sistema de notificaciones push
- 🔄 API REST completa para móviles
- 🔄 Análisis avanzado de datos
- 🔄 Optimizaciones de rendimiento

**Archivos de Documentación:**

- `ADMIN_SYSTEM_DOCUMENTATION.md` - Documentación del panel admin
- `CLIENT_FLOW_COMPLETION.md` - Flujo completo del cliente
- `SISTEMA_EMAIL_COMPLETO_FINAL.md` - Sistema de emails
- `EXPLICACION_VALORES_NULL.md` - Manejo de valores NULL

**Tag Git:** `v0.8.0` - [Ver en GitHub](https://github.com/Ruben-DWC/EcoVivaShop/releases/tag/v0.8.0)

### 📋 **Avances Anteriores**

- **Avance 1 (20%):** Diseño de prototipos UX/UI en Balsamiq, modelado de base de datos
- **Avance 2 (40%):** Implementación inicial del backend y estructura del proyecto
- **Avance 3 (80%):** Sistema completo con todas las funcionalidades principales

## 👨‍� Autor

**Ruben Walter*

- GitHub: [@Ruben-DWC](https://github.com/Ruben-DWC)
- Proyecto: Sistema de Gestión EcoVivaShop
- Institución: Universidad Nacional de Ingeniería - Ciclo 7

## 📧 Contacto

Para consultas sobre el proyecto, puedes contactar a través de GitHub Issues.

---

🌱 **EcoVivaShop** - *Tecnología verde para un futuro sostenible*
 8ea10c5c45050e39b246aabf8084e06b6d70dd6a
