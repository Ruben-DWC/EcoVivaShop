# 🚀 EcoVivaShop - Guía de Replicación de Base de Datos

## 📋 Información del Backup

- **Fecha de creación**: 2 de noviembre de 2025, 21:32:57
- **Archivo**: `ecovivashop_db_complete_backup_20251102_213257.sql`
- **Estado**: Base de datos completa y operativa con todas las funcionalidades implementadas

## 🎯 Estado Actual del Sistema

Este backup incluye el sistema EcoVivaShop completamente funcional con:

### ✅ Funcionalidades Implementadas

- **Sistema de Usuarios**: Registro, login, roles (ADMIN, CLIENTE)
- **Catálogo de Productos**: 60+ productos ecológicos con imágenes
- **Sistema de Inventario**: Control de stock, historial, ubicaciones
- **Carrito de Compras**: Funcionalidad completa
- **Sistema de Pedidos**: Creación, seguimiento, detalles
- **Sistema de Pagos**: Integración con múltiples pasarelas
- **Sistema de Imágenes**: Perfiles de usuario y productos
- **Sistema de Email**: Configuración Gmail completa
- **Sistema de Suscripciones**: Gestión de membresías
- **Panel de Administración**: Gestión completa del sistema

### 📊 Datos Incluidos

- **Usuarios**: Administradores y clientes de prueba
- **Productos**: Catálogo completo con imágenes y descripciones
- **Inventario**: Stocks, ubicaciones, historial de movimientos
- **Pedidos**: Historial de transacciones
- **Configuraciones**: Email, sistema, etc.

## 🔧 Instrucciones de Restauración

### Paso 1: Instalar PostgreSQL

Asegúrate de tener PostgreSQL instalado en el nuevo ordenador:

```bash
# Windows - Descargar desde postgresql.org
# macOS - brew install postgresql
# Linux - sudo apt install postgresql postgresql-contrib
```

### Paso 2: Crear Base de Datos

```sql
-- Ejecutar en psql o pgAdmin
CREATE DATABASE ecovivashop_db;
```

### Paso 3: Restaurar el Backup

```bash
# Opción 1: Usando psql (recomendado)
psql -h localhost -p 5432 -U postgres -d ecovivashop_db -f ecovivashop_db_complete_backup_20251102_213257.sql

# Opción 2: Usando pg_restore (si usas el archivo comprimido)
pg_restore -h localhost -p 5432 -U postgres -d ecovivashop_db ecovivashop_db_current_state_20251102_212834.sql
```

### Paso 4: Verificar Restauración

```sql
-- Conectar a la base de datos
psql -h localhost -p 5432 -U postgres -d ecovivashop_db

-- Verificar tablas
\dt

-- Verificar datos en tablas principales
SELECT COUNT(*) FROM tb_usuario;
SELECT COUNT(*) FROM tb_producto;
SELECT COUNT(*) FROM tb_inventario;
SELECT COUNT(*) FROM tb_pedido;
```

### Paso 5: Configurar Aplicación

1. **Clonar el proyecto**:

   ```bash
   git clone [URL_DEL_REPOSITORIO]
   cd ecovivashop
   ```

2. **Configurar application.properties**:

   ```properties
   # Asegúrate de que coincida con tu configuración de PostgreSQL
   spring.datasource.url=jdbc:postgresql://localhost:5432/ecovivashop_db
   spring.datasource.username=postgres
   spring.datasource.password=TU_PASSWORD_POSTGRESQL
   ```

3. **Compilar y ejecutar**:

   ```bash
   mvn clean compile
   mvn spring-boot:run
   ```

## 🔐 Credenciales de Prueba

### Usuario Administrador

- **Email**: <admin@ecovivashop.com>
- **Password**: admin123

### Usuario Cliente

- **Email**: <cliente@ecovivashop.com>
- **Password**: cliente123

## 📁 Archivos Importantes

- `ecovivashop_db_complete_backup_20251102_213257.sql` - Backup SQL completo
- `ecovivashop_db_current_state_20251102_212834.sql` - Backup comprimido
- `uploads/` - Directorio con imágenes de productos y perfiles
- `src/main/resources/application.properties` - Configuración de la aplicación

## ⚠️ Notas Importantes

1. **Contraseña de PostgreSQL**: Asegúrate de usar la misma contraseña que tienes configurada
2. **Directorio uploads**: Copia también el directorio `uploads/` con todas las imágenes
3. **Configuración de Email**: Las credenciales de Gmail están configuradas para testing
4. **Puerto**: La aplicación corre en el puerto 8081 por defecto

## 🆘 Solución de Problemas

### Error de conexión

```bash
# Verificar que PostgreSQL esté corriendo
sudo systemctl status postgresql  # Linux
brew services list                # macOS
```

### Error de permisos

```sql
-- Otorgar permisos al usuario postgres
GRANT ALL PRIVILEGES ON DATABASE ecovivashop_db TO postgres;
```

### Error de codificación

```bash
# Si hay problemas con caracteres especiales
psql -h localhost -p 5432 -U postgres -d ecovivashop_db -f ecovivashop_db_complete_backup_20251102_213257.sql --set ON_ERROR_STOP=on
```

## 📞 Soporte

Si tienes problemas con la restauración, verifica:

1. Versión de PostgreSQL (recomendado: 16+)
2. Credenciales de conexión
3. Permisos de usuario
4. Espacio en disco disponible

---
**Estado del Sistema**: ✅ Completamente operativo y funcional
**Fecha del Backup**: 2 de noviembre de 2025
**Versión**: EcoVivaShop v1.0.0
