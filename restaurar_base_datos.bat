@echo off
echo ============================================
echo    EcoVivaShop - Restauracion de Base de Datos
echo    Fecha: 2 de noviembre de 2025
echo ============================================
echo.

set /p PGPASSWORD="Ingresa la contraseña de PostgreSQL: "
set DB_NAME=ecovivashop_db
set DB_USER=postgres
set DB_HOST=localhost
set DB_PORT=5432
set BACKUP_FILE=ecovivashop_db_complete_backup_20251102_213257.sql

echo.
echo Verificando conexion a PostgreSQL...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d postgres -c "SELECT version();" >nul 2>&1
if %errorlevel% neq 0 (
    echo ❌ Error: No se puede conectar a PostgreSQL
    echo    - Verifica que PostgreSQL este ejecutandose
    echo    - Verifica las credenciales
    pause
    exit /b 1
)

echo ✅ Conexion a PostgreSQL exitosa
echo.

echo Creando base de datos (si no existe)...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d postgres -c "DROP DATABASE IF EXISTS %DB_NAME%;" >nul 2>&1
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d postgres -c "CREATE DATABASE %DB_NAME%;" >nul 2>&1

if %errorlevel% neq 0 (
    echo ❌ Error creando la base de datos
    pause
    exit /b 1
)

echo ✅ Base de datos creada exitosamente
echo.

echo Restaurando backup completo...
echo Este proceso puede tomar varios minutos...
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -f "%BACKUP_FILE%"

if %errorlevel% neq 0 (
    echo ❌ Error durante la restauracion
    echo Revisa los mensajes de error arriba
    pause
    exit /b 1
)

echo.
echo ✅ Restauracion completada exitosamente!
echo.

echo Verificando restauracion...
echo ============================================
psql -h %DB_HOST% -p %DB_PORT% -U %DB_USER% -d %DB_NAME% -c "
SELECT 'Usuarios:' as tabla, COUNT(*) as cantidad FROM tb_usuario
UNION ALL
SELECT 'Productos:', COUNT(*) FROM tb_producto
UNION ALL
SELECT 'Inventario:', COUNT(*) FROM tb_inventario
UNION ALL
SELECT 'Pedidos:', COUNT(*) FROM tb_pedido
UNION ALL
SELECT 'Imagenes productos:', COUNT(*) FROM imagenes_producto
UNION ALL
SELECT 'Imagenes perfiles:', COUNT(*) FROM imagenes_perfil;
"

echo.
echo ============================================
echo    RESUMEN DE RESTAURACION
echo ============================================
echo ✅ Base de datos: %DB_NAME%
echo ✅ Archivo backup: %BACKUP_FILE%
echo ✅ Estado: COMPLETAMENTE OPERATIVA
echo.
echo 📋 Proximos pasos:
echo 1. Copia el directorio 'uploads' con las imagenes
echo 2. Configura application.properties con tu password
echo 3. Ejecuta: mvn spring-boot:run
echo 4. Accede a: http://localhost:8081
echo.
echo 🔐 Credenciales de prueba:
echo Admin: admin@ecovivashop.com / admin123
echo Cliente: cliente@ecovivashop.com / cliente123
echo.
pause