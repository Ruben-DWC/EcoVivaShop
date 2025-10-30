-- Script de migración para corregir valores NULL existentes antes de agregar restricciones NOT NULL
-- Ejecutar esto antes de iniciar la aplicación

-- Actualizar tabla tb_rol
UPDATE tb_rol SET estado = true WHERE estado IS NULL;
UPDATE tb_rol SET fecha_creacion = CURRENT_TIMESTAMP WHERE fecha_creacion IS NULL;

-- Actualizar tabla tb_usuario  
UPDATE tb_usuario SET apellido = 'Sin Apellido' WHERE apellido IS NULL;
UPDATE tb_usuario SET email = CONCAT('user_', id, '@ecovivashop.com') WHERE email IS NULL;
UPDATE tb_usuario SET estado = true WHERE estado IS NULL;

-- Alternativa: si quieres empezar de cero con una base de datos limpia, puedes eliminar y recrear las tablas
-- DROP TABLE IF EXISTS tb_pago CASCADE;
-- DROP TABLE IF EXISTS tb_pedido_detalle CASCADE;
-- DROP TABLE IF EXISTS tb_pedido CASCADE;
-- DROP TABLE IF EXISTS tb_suscripcion CASCADE;
-- DROP TABLE IF EXISTS tb_inventario CASCADE;
-- DROP TABLE IF EXISTS tb_producto CASCADE;
-- DROP TABLE IF EXISTS tb_usuario CASCADE;
-- DROP TABLE IF EXISTS tb_rol CASCADE;
