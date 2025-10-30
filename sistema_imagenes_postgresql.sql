-- Script SQL para PostgreSQL - Sistema de Imágenes
-- Crear tabla para imágenes de perfil
CREATE TABLE IF NOT EXISTS imagenes_perfil (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL CHECK (tipo_usuario IN ('ADMIN', 'CLIENTE')),
    nombre_archivo VARCHAR(255) NOT NULL,
    nombre_original VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_mime VARCHAR(100) NOT NULL,
    tamaño BIGINT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE
);

-- Crear índices para imagenes_perfil
CREATE INDEX IF NOT EXISTS idx_imagenes_perfil_usuario_tipo ON imagenes_perfil(usuario_id, tipo_usuario);
CREATE INDEX IF NOT EXISTS idx_imagenes_perfil_activo ON imagenes_perfil(activo);
CREATE INDEX IF NOT EXISTS idx_imagenes_perfil_fecha_subida ON imagenes_perfil(fecha_subida);

-- Crear tabla para imágenes de productos
CREATE TABLE IF NOT EXISTS imagenes_producto (
    id BIGSERIAL PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    nombre_original VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_mime VARCHAR(100) NOT NULL,
    tamaño BIGINT,
    orden INTEGER DEFAULT 0,
    es_principal BOOLEAN DEFAULT FALSE,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    alt_text VARCHAR(255)
);

-- Crear índices para imagenes_producto
CREATE INDEX IF NOT EXISTS idx_imagenes_producto_producto_activo ON imagenes_producto(producto_id, activo);
CREATE INDEX IF NOT EXISTS idx_imagenes_producto_producto_orden ON imagenes_producto(producto_id, orden);
CREATE INDEX IF NOT EXISTS idx_imagenes_producto_principal ON imagenes_producto(producto_id, es_principal);
CREATE INDEX IF NOT EXISTS idx_imagenes_producto_fecha_subida ON imagenes_producto(fecha_subida);

-- Agregar comentarios
COMMENT ON TABLE imagenes_perfil IS 'Tabla para almacenar metadatos de imágenes de perfil de usuarios';
COMMENT ON TABLE imagenes_producto IS 'Tabla para almacenar metadatos de imágenes de productos';

-- Mostrar mensaje de éxito
DO $$
BEGIN
    RAISE NOTICE '✅ Tablas del sistema de imágenes creadas exitosamente';
    RAISE NOTICE '📁 Directorio de uploads: uploads/';
    RAISE NOTICE '🔧 Configuración completada para PostgreSQL';
END $$;
