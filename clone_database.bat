@echo off
echo =====================================================
echo CLONADO DE BASE DE DATOS: eco_maxtienda -> ecovivashop_db
echo EcoVivaShop Database Clone Script
echo =====================================================

echo.
echo [1/4] Creando backup de la base de datos original...
pg_dump -h localhost -U postgres -d eco_maxtienda -f eco_maxtienda_backup.sql --verbose

echo.
echo [2/4] Creando nueva base de datos ecovivashop_db...
psql -h localhost -U postgres -c "DROP DATABASE IF EXISTS ecovivashop_db;"
psql -h localhost -U postgres -c "CREATE DATABASE ecovivashop_db WITH ENCODING='UTF8';"

echo.
echo [3/4] Restaurando datos en ecovivashop_db...
psql -h localhost -U postgres -d ecovivashop_db -f eco_maxtienda_backup.sql

echo.
echo [4/4] Actualizando configuraciones para EcoVivaShop...
psql -h localhost -U postgres -d ecovivashop_db -c "UPDATE usuario SET email = 'admin@ecovivashop.com' WHERE email = 'admin@ecovivashop.com';"

echo.
echo =====================================================
echo CLONADO COMPLETADO EXITOSAMENTE!
echo Base de datos: ecovivashop_db
echo =====================================================
pause
