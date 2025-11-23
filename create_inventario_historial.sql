-- =====================================================
-- TABLA DE HISTORIAL DE INVENTARIO
-- EcoVivaShop - Sistema de Trazabilidad de Cambios
-- =====================================================

-- Crear tabla de historial de inventario
CREATE TABLE IF NOT EXISTS public.inventario_historial (
    id_historial SERIAL PRIMARY KEY,
    id_inventario INTEGER NOT NULL REFERENCES tb_inventario(id_inventario) ON DELETE CASCADE,
    tipo_cambio VARCHAR(20) NOT NULL CHECK (tipo_cambio IN ('AUMENTO', 'DISMINUCION', 'ACTUALIZACION', 'CREACION')),
    stock_anterior INTEGER,
    stock_nuevo INTEGER,
    cambio_cantidad INTEGER,
    stock_minimo_anterior INTEGER,
    stock_minimo_nuevo INTEGER,
    stock_maximo_anterior INTEGER,
    stock_maximo_nuevo INTEGER,
    ubicacion_anterior VARCHAR(100),
    ubicacion_nueva VARCHAR(100),
    motivo TEXT,
    usuario VARCHAR(150) NOT NULL,
    fecha_cambio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ip_usuario VARCHAR(45)
);

-- Crear índices para optimización
CREATE INDEX IF NOT EXISTS idx_inventario_historial_inventario ON inventario_historial(id_inventario);
CREATE INDEX IF NOT EXISTS idx_inventario_historial_tipo_cambio ON inventario_historial(tipo_cambio);
CREATE INDEX IF NOT EXISTS idx_inventario_historial_usuario ON inventario_historial(usuario);
CREATE INDEX IF NOT EXISTS idx_inventario_historial_fecha ON inventario_historial(fecha_cambio DESC);

-- Comentarios en las columnas
COMMENT ON TABLE inventario_historial IS 'Tabla que registra todos los cambios realizados en el inventario para trazabilidad completa';
COMMENT ON COLUMN inventario_historial.id_historial IS 'Identificador único del registro de historial';
COMMENT ON COLUMN inventario_historial.id_inventario IS 'Referencia al inventario modificado';
COMMENT ON COLUMN inventario_historial.tipo_cambio IS 'Tipo de cambio: AUMENTO, DISMINUCION, ACTUALIZACION, CREACION';
COMMENT ON COLUMN inventario_historial.stock_anterior IS 'Stock antes del cambio';
COMMENT ON COLUMN inventario_historial.stock_nuevo IS 'Stock después del cambio';
COMMENT ON COLUMN inventario_historial.cambio_cantidad IS 'Cantidad del cambio (positivo para aumento, negativo para disminución)';
COMMENT ON COLUMN inventario_historial.stock_minimo_anterior IS 'Stock mínimo antes del cambio';
COMMENT ON COLUMN inventario_historial.stock_minimo_nuevo IS 'Stock mínimo después del cambio';
COMMENT ON COLUMN inventario_historial.stock_maximo_anterior IS 'Stock máximo antes del cambio';
COMMENT ON COLUMN inventario_historial.stock_maximo_nuevo IS 'Stock máximo después del cambio';
COMMENT ON COLUMN inventario_historial.ubicacion_anterior IS 'Ubicación antes del cambio';
COMMENT ON COLUMN inventario_historial.ubicacion_nueva IS 'Ubicación después del cambio';
COMMENT ON COLUMN inventario_historial.motivo IS 'Motivo o descripción del cambio';
COMMENT ON COLUMN inventario_historial.usuario IS 'Usuario que realizó el cambio';
COMMENT ON COLUMN inventario_historial.fecha_cambio IS 'Fecha y hora del cambio';
COMMENT ON COLUMN inventario_historial.ip_usuario IS 'Dirección IP del usuario que realizó el cambio';

COMMIT;