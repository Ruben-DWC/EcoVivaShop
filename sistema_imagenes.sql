-- Crear tabla para imágenes de perfil
CREATE TABLE imagenes_perfil (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo_usuario ENUM('ADMIN', 'CLIENTE') NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    nombre_original VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_mime VARCHAR(100) NOT NULL,
    tamaño BIGINT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    
    INDEX idx_usuario_tipo (usuario_id, tipo_usuario),
    INDEX idx_activo (activo),
    INDEX idx_fecha_subida (fecha_subida)
);

-- Crear tabla para imágenes de productos
CREATE TABLE imagenes_producto (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    producto_id BIGINT NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
    nombre_original VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_mime VARCHAR(100) NOT NULL,
    tamaño BIGINT,
    orden INT DEFAULT 0,
    es_principal BOOLEAN DEFAULT FALSE,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN DEFAULT TRUE,
    alt_text VARCHAR(255),
    
    INDEX idx_producto_activo (producto_id, activo),
    INDEX idx_producto_orden (producto_id, orden),
    INDEX idx_principal (producto_id, es_principal),
    INDEX idx_fecha_subida (fecha_subida)
);

-- Crear directorio de uploads (esto debe hacerse desde el sistema operativo)
-- mkdir -p uploads/profiles/admin
-- mkdir -p uploads/profiles/cliente
-- mkdir -p uploads/products

-- Agregar datos de ejemplo para testing
INSERT INTO imagenes_perfil (usuario_id, tipo_usuario, nombre_archivo, nombre_original, ruta_archivo, tipo_mime, tamaño)
VALUES 
(1, 'ADMIN', 'default-admin.png', 'default-admin.png', 'uploads/profiles/admin/default-admin.png', 'image/png', 1024);

-- Comentarios sobre el uso:
-- 1. Las imágenes se almacenarán físicamente en el directorio 'uploads/'
-- 2. La URL pública será '/uploads/profiles/admin/filename.ext' o '/uploads/products/productId/filename.ext'
-- 3. El sistema redimensionará automáticamente las imágenes para optimizar el rendimiento
-- 4. Se mantiene un historial de imágenes marcando como 'activo = false' en lugar de eliminar
-- 5. Para productos, se puede tener múltiples imágenes con orden y una marcada como principal
