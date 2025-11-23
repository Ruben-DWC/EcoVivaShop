-- Script para agregar campos adicionales para administradores
-- Ejecutar este script en la base de datos PostgreSQL de EcoVivaShop

-- Agregar columna departamento
ALTER TABLE tb_usuario ADD COLUMN IF NOT EXISTS departamento VARCHAR(100);

-- Agregar columna observaciones
ALTER TABLE tb_usuario ADD COLUMN IF NOT EXISTS observaciones TEXT;

-- Agregar columna permisos (almacenará JSON)
ALTER TABLE tb_usuario ADD COLUMN IF NOT EXISTS permisos TEXT;

-- Comentarios para documentación
COMMENT ON COLUMN tb_usuario.departamento IS 'Departamento al que pertenece el administrador';
COMMENT ON COLUMN tb_usuario.observaciones IS 'Observaciones adicionales sobre el administrador';
COMMENT ON COLUMN tb_usuario.permisos IS 'Permisos del administrador en formato JSON';