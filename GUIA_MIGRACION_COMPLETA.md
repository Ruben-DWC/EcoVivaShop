# 📦 GUÍA COMPLETA DE MIGRACIÓN - ECOVIVASHOP

**Fecha**: 23 de noviembre de 2025  
**Versión del Proyecto**: EcoVivaShop v1.0  
**Base de Datos**: PostgreSQL 16.11  

---

## 📋 TABLA DE CONTENIDOS

   [Requisitos Previos](#requisitos-previos

   [Archivos Necesarios](#archivos-necesarios
   [Instalación en el Nuevo Ordenador](#instalación-en-el-nuevo-ordenador
   [Restauración de la Base de Datos](#restauración-de-la-base-de-datos
   [Configuración del Proyecto](#configuración-del-proyecto
   [Verificación](#verificación
   [Solución de Problemas](#solución-de-problemas

---

## 🔧 REQUISITOS PREVIOS

### Software necesario en el nuevo ordenador

1. **Java Development Kit (JDK) 17 o superior**
   - Descargar: <https://adoptium.net/>
   - Verificar instalación: `java -version`

2. **Maven 3.8+ (incluido con la mayoría de IDEs)**
   - Descargar: <https://maven.apache.org/download.cgi>
   - Verificar instalación: `mvn -version`

3. **PostgreSQL 16.x**
   - Descargar: <https://www.postgresql.org/download/>
   - Durante la instalación, recordar la contraseña del usuario `postgres`

4. **IDE recomendado: IntelliJ IDEA o Eclipse**
   - IntelliJ IDEA: <https://www.jetbrains.com/idea/download/>
   - Eclipse: <https://www.eclipse.org/downloads/>

5. **Git** (opcional, para control de versiones)
   - Descargar: <https://git-scm.com/downloads>

---

## 📁 ARCHIVOS NECESARIOS

Debes copiar los siguientes archivos/carpetas al nuevo ordenador:

### 1. Código fuente del proyecto completo

``
📂 ecovivashop/
   ├── 📂 src/
   ├── 📂 target/ (opcional, se regenera con Maven)
   ├── 📂 uploads/ (IMPORTANTE: contiene imágenes de productos y perfiles)
   ├── 📄 pom.xml
   ├── 📄 mvnw / mvnw.cmd
   └── 📄 lombok.config
``

### 2. Archivo de backup de la base de datos

``
📄 ecovivashop_db_complete_backup_20251123_154424.sql
``

### 3. Carpeta de uploads (imágenes)

``
📂 uploads/
   ├── 📂 products/ (4 imágenes de productos)
   └── 📂 profiles/ (24 imágenes de perfil)
``

---

## 💻 INSTALACIÓN EN EL NUEVO ORDENADOR

### Paso 1: Instalar PostgreSQL

1. Ejecutar el instalador de PostgreSQL 16
2. Durante la instalación:
   - Puerto: `5432` (predeterminado)
   - Usuario: `postgres`
   - Contraseña: **Anota esta contraseña, la necesitarás**
   - Locale: `Spanish, Spain` o `Default locale`
3. Marcar la opción "Stack Builder" (opcional, para herramientas adicionales)
4. Verificar instalación:

   ```powershell
   psql --version
   ```

### Paso 2: Copiar archivos del proyecto

1. Copiar toda la carpeta `ecovivashop` al nuevo ordenador
2. Ubicación sugerida: `C:\Proyectos\ecovivashop\` (sin espacios para evitar problemas)
3. Verificar que la carpeta `uploads` esté completa con todas las imágenes

### Paso 3: Instalar Java JDK 17

1. Descargar e instalar JDK 17 desde <https://adoptium.net/>
2. Configurar variable de entorno `JAVA_HOME`:

   ``
   JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.x.x
   PATH=%JAVA_HOME%\bin;%PATH%
   ``

3. Verificar:

   ```powershell
   java -version
   javac -version
   ```

---

## 🗄️ RESTAURACIÓN DE LA BASE DE DATOS

### Opción A: Usando PowerShell (Recomendado)

1. Abrir **PowerShell** como Administrador

2. Navegar a la carpeta del proyecto:

   ```powershell
   cd "C:\ruta\a\ecovivashop"
   ```

3. Crear la base de datos:

   ```powershell
   # Crear base de datos vacía
   & "C:\Program Files\PostgreSQL\16\bin\createdb.exe" -U postgres ecovivashop_db
   ```

   Ingresar la contraseña de `postgres` cuando se solicite.

4. Restaurar el backup:

   ```powershell
   # Restaurar datos desde el archivo SQL
   & "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -d ecovivashop_db -f "ecovivashop_db_complete_backup_20251123_154424.sql"
   ```

5. Verificar restauración:

   ```powershell
   & "C:\Program Files\PostgreSQL\16\bin\psql.exe" -U postgres -d ecovivashop_db -c "\dt"
   ```

   Deberías ver las 14 tablas del sistema.

### Opción B: Usando pgAdmin (GUI)

1. Abrir **pgAdmin 4** (instalado con PostgreSQL)
2. Conectarse al servidor PostgreSQL local
3. Click derecho en "Databases" → "Create" → "Database"
   - Nombre: `ecovivashop_db`
   - Owner: `postgres`
   - Encoding: `UTF8`
4. Click derecho en `ecovivashop_db` → "Query Tool"
5. Menú "File" → "Open" → Seleccionar `ecovivashop_db_complete_backup_20251123_154424.sql`
6. Click en "Execute" (F5)
7. Verificar en el panel izquierdo: "Schemas" → "public" → "Tables" (deben aparecer 14 tablas)

---

## ⚙️ CONFIGURACIÓN DEL PROYECTO

### Paso 1: Configurar credenciales de PostgreSQL

1. Abrir el archivo `src/main/resources/application.properties`

2. Actualizar las credenciales de la base de datos:

   ```properties
   # DATABASE CONFIGURATION
   spring.datasource.url=jdbc:postgresql://localhost:5432/ecovivashop_db
   spring.datasource.username=postgres
   spring.datasource.password=TU_CONTRASEÑA_AQUI
   spring.datasource.driver-class-name=org.postgresql.Driver
   ```

3. **IMPORTANTE**: Reemplazar `TU_CONTRASEÑA_AQUI` con la contraseña que configuraste durante la instalación de PostgreSQL.

### Paso 2: Configurar rutas de archivos (si es necesario)

Si cambiaste la ubicación del proyecto, actualizar las rutas en `application.properties`:

```properties
# FILE UPLOAD CONFIGURATION
upload.path=C:/nueva/ruta/ecovivashop/uploads
upload.products.path=${upload.path}/products
upload.profiles.path=${upload.path}/profiles
```

### Paso 3: Configurar email (Gmail)

Si deseas que el sistema envíe correos electrónicos (recuperación de contraseña, confirmaciones):

```properties
# EMAIL CONFIGURATION (GMAIL)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=tu-email@gmail.com
spring.mail.password=tu-contraseña-aplicacion
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

**Nota**: Para Gmail, debes generar una "Contraseña de aplicación" en <https://myaccount.google.com/apppasswords>

---

## ✅ VERIFICACIÓN

### Paso 1: Compilar el proyecto

Abrir terminal/PowerShell en la carpeta del proyecto:

```powershell
# Limpiar y compilar
mvn clean compile

# Verificar que no haya errores
```

### Paso 2: Ejecutar el proyecto

```powershell
# Opción 1: Con Maven
mvn spring-boot:run

# Opción 2: Desde tu IDE
# Buscar la clase principal: EcovivashopApplication.java
# Click derecho → "Run"
```

### Paso 3: Verificar conexión a la base de datos

1. Observar los logs en la consola, deberías ver:

   ``
   HikariPool-1 - Start completed.
   Started EcovivashopApplication in X.XXX seconds
   ``

2. Abrir navegador en: <http://localhost:8081>

3. Verificar que la página principal cargue correctamente

### Paso 4: Probar login

**Usuario Administrador:**

- Email: `admin@ecovivashop.com`
- Contraseña: `admin123` (o la que hayas configurado)

**Usuario Cliente de prueba:**

- Email: `diego123@test.com`
- Contraseña: `123456`

### Paso 5: Verificar datos

1. Ir a: <http://localhost:8081/admin/productos>
2. Deberías ver **138 productos** en el catálogo
3. Ir a: <http://localhost:8081/admin/usuarios>
4. Deberías ver **11 usuarios** registrados
5. Ir a: <http://localhost:8081/admin/pedidos>
6. Deberías ver **21 pedidos**

---

## 🔍 VERIFICACIÓN DE LA INTEGRIDAD DEL BACKUP

Para asegurarte de que todos los datos se restauraron correctamente, ejecuta estas consultas en PostgreSQL:

```sql
-- Contar registros en cada tabla
SELECT 'Roles' as tabla, COUNT(*) as cantidad FROM tb_rol
UNION ALL
SELECT 'Usuarios', COUNT(*) FROM tb_usuario
UNION ALL
SELECT 'Productos', COUNT(*) FROM tb_producto
UNION ALL
SELECT 'Inventario', COUNT(*) FROM tb_inventario
UNION ALL
SELECT 'Pedidos', COUNT(*) FROM tb_pedido
UNION ALL
SELECT 'Detalles Pedido', COUNT(*) FROM tb_pedido_detalle
UNION ALL
SELECT 'Imágenes Producto', COUNT(*) FROM imagenes_producto
UNION ALL
SELECT 'Imágenes Perfil', COUNT(*) FROM imagenes_perfil
UNION ALL
SELECT 'Transacciones Pago', COUNT(*) FROM transacciones_pago
UNION ALL
SELECT 'Suscripciones', COUNT(*) FROM tb_suscripcion;
```

**Resultados esperados:**

``

Tabla                 | Cantidad
----------------------|----------
Roles                 | 2
Usuarios              | 11
Productos             | 138
Inventario            | 129
Pedidos               | 21
Detalles Pedido       | 25
Imágenes Producto     | 4
Imágenes Perfil       | 24
Transacciones Pago    | 10
Suscripciones         | 11

``

---

## 🛠️ SOLUCIÓN DE PROBLEMAS

### Error: "Could not connect to database"

**Causa**: PostgreSQL no está ejecutándose o las credenciales son incorrectas.

**Solución**:

1. Verificar que PostgreSQL esté ejecutándose:

   ```powershell
   Get-Service -Name postgresql*
   ```

2. Si no está activo, iniciarlo:

   ```powershell
   Start-Service -Name "postgresql-x64-16"
   ```

3. Verificar credenciales en `application.properties`
4. Probar conexión manual:

   ```powershell
   psql -U postgres -d ecovivashop_db
   ```

### Error: "Table does not exist"

**Causa**: El backup no se restauró correctamente.

**Solución**:

1. Eliminar la base de datos:

   ```sql
   DROP DATABASE ecovivashop_db;
   ```

2. Recrearla:

   ```sql
   CREATE DATABASE ecovivashop_db;
   ```

3. Restaurar el backup nuevamente

### Error: "Port 8081 already in use"

**Causa**: Otra aplicación está usando el puerto 8081.

**Solución**:

1. Cambiar el puerto en `application.properties`:

   ```properties
   server.port=8082
   ```

2. O detener la aplicación que usa el puerto 8081

### Error: "Failed to load ApplicationContext"

**Causa**: Dependencias de Maven no descargadas o JDK incorrecto.

**Solución**:

1. Actualizar dependencias:

   ```powershell
   mvn clean install -U
   ```

2. Verificar JDK 17:

   ```powershell
   java -version
   ```

3. Limpiar caché de Maven:

   ```powershell
   mvn dependency:purge-local-repository
   ```

### Imágenes no cargan

**Causa**: Ruta de uploads incorrecta.

**Solución**:

1. Verificar que la carpeta `uploads/` esté en la raíz del proyecto
2. Verificar configuración en `application.properties`:

   ```properties
   upload.path=./uploads
   ```

3. Reiniciar la aplicación

---

## 📊 CONTENIDO DEL BACKUP

El archivo de backup contiene:

### Esquema completo (DDL)

- ✅ 14 tablas con sus estructuras completas
- ✅ Índices para optimización de consultas
- ✅ Claves foráneas y restricciones
- ✅ Secuencias para auto-incremento
- ✅ Comentarios en columnas importantes
- ✅ Constraints (CHECK, UNIQUE, NOT NULL)

### Datos completos (DML)

- ✅ **2 roles**: ROLE_ADMIN, ROLE_CLIENTE
- ✅ **11 usuarios**: incluye administradores y clientes de prueba
- ✅ **138 productos**: catálogo completo de productos ecológicos
- ✅ **129 registros de inventario**: stock actual de todos los productos
- ✅ **21 pedidos**: histórico de pedidos con todos sus detalles
- ✅ **25 detalles de pedidos**: items individuales de cada pedido
- ✅ **10 transacciones de pago**: registro de pagos procesados
- ✅ **11 suscripciones**: planes de suscripción activos
- ✅ **4 imágenes de productos**: referencias a archivos físicos
- ✅ **24 imágenes de perfil**: fotos de usuarios

### Características especiales incluidas

- ✅ **Sistema de inventario con historial de auditoría**: tabla `inventario_historial`
- ✅ **Mejoras en login/registro**: validaciones y campos adicionales
- ✅ **Sistema de reset de contraseñas**: tokens de recuperación
- ✅ **Integración con Google OAuth**: usuarios con provider social
- ✅ **Sistema de suscripciones**: planes mensuales, bimestrales y trimestrales
- ✅ **Transacciones de pago**: integración con pasarelas de pago

---

## 📞 SOPORTE

Si encuentras problemas durante la migración:

1. **Revisar logs de la aplicación** en la consola
2. **Verificar logs de PostgreSQL**:
   - Windows: `C:\Program Files\PostgreSQL\16\data\log\`
3. **Consultar documentación**:
   - Spring Boot: <https://spring.io/projects/spring-boot>
   - PostgreSQL: <https://www.postgresql.org/docs/16/>
4. **Usuarios de prueba para testing**:
   - Admin: `admin@ecovivashop.com` / `admin123`
   - Cliente: `diego123@test.com` / `123456`
   - Cliente Google: `zeroarkkahonara@gmail.com` (OAuth)

---

## ✨ CAMBIOS RECIENTES INCLUIDOS EN ESTE BACKUP

Este backup incluye todas las mejoras recientes del proyecto:

### UI/UX Improvements

- ✅ Hero 3D con Three.js en landing page
- ✅ Login page con floating labels
- ✅ Password toggle visibility
- ✅ Submit button con spinner loading
- ✅ Gradient background animado
- ✅ Shake animation en errores de login

### Backend Improvements

- ✅ JasperReports POC para exportación PDF
- ✅ Sistema de inventario con auditoría completa
- ✅ Historial de cambios de stock con usuario y IP
- ✅ Mejoras en seguridad de contraseñas
- ✅ Sistema de reset de contraseñas con tokens
- ✅ Integración con Google OAuth2

### Database Enhancements

- ✅ Tabla `inventario_historial` con campos de auditoría
- ✅ Campos de tracking en todas las tablas principales
- ✅ Constraints mejorados para integridad de datos
- ✅ Índices optimizados para consultas frecuentes

---

## 📝 NOTAS FINALES

- **El backup fue generado el**: 23 de noviembre de 2025, 15:44:24
- **Tamaño del backup**: 0.11 MB (solo estructura y datos, sin archivos binarios)
- **Versión de PostgreSQL**: 16.11
- **Encoding**: UTF-8
- **Locale**: Spanish

### Archivos que NO están en el backup SQL

Los siguientes archivos deben copiarse manualmente:

- 📂 `uploads/products/` - Imágenes de productos (4 archivos)
- 📂 `uploads/profiles/` - Imágenes de perfil (24 archivos)
- 📄 Archivos de configuración locales (si los tienes)

### Recomendaciones

1. **Hacer backup regular**: Ejecutar el script `generar_backup_completo.ps1` semanalmente
2. **Mantener versiones**: Guardar backups antiguos con fecha en el nombre
3. **Probar restauración**: Verificar periódicamente que los backups se pueden restaurar
4. **Documentar cambios**: Mantener un log de cambios importantes en la base de datos

---

**¡Migración exitosa! 🎉*

Si todos los pasos se siguieron correctamente, tu aplicación EcoVivaShop debería estar funcionando perfectamente en el nuevo ordenador con todos los datos intactos.
