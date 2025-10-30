-- =====================================================
-- SCRIPT DE CLONADO: eco_maxtienda -> ecovivashop_db
-- EcoVivaShop Database Creation and Data Migration
-- =====================================================

-- 1. Crear la nueva base de datos
DROP DATABASE IF EXISTS ecovivashop_db;
CREATE DATABASE ecovivashop_db
    WITH OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'Spanish_Spain.1252'
    LC_CTYPE = 'Spanish_Spain.1252'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

-- Conectar a la nueva base de datos
\c ecovivashop_db;

-- 2. Crear el esquema de tablas (estructura completa)
-- Tabla de roles
CREATE TABLE IF NOT EXISTS public.rol (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de usuarios
CREATE TABLE IF NOT EXISTS public.usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    direccion TEXT,
    estado BOOLEAN DEFAULT true,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    id_rol INTEGER REFERENCES rol(id_rol)
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS public.producto (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(100),
    stock INTEGER DEFAULT 0,
    imagen_url VARCHAR(500),
    estado BOOLEAN DEFAULT true,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    peso DECIMAL(8,2),
    dimensiones VARCHAR(100),
    marca VARCHAR(100),
    codigo_producto VARCHAR(50) UNIQUE,
    descuento DECIMAL(5,2) DEFAULT 0.00,
    tags TEXT,
    calificacion_promedio DECIMAL(3,2) DEFAULT 0.00,
    numero_reviews INTEGER DEFAULT 0
);

-- Tabla de inventario
CREATE TABLE IF NOT EXISTS public.inventario (
    id_inventario SERIAL PRIMARY KEY,
    id_producto INTEGER REFERENCES producto(id_producto),
    stock_actual INTEGER DEFAULT 0,
    stock_minimo INTEGER DEFAULT 10,
    stock_maximo INTEGER DEFAULT 1000,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ubicacion VARCHAR(100),
    lote VARCHAR(50),
    fecha_vencimiento DATE
);

-- Tabla de pedidos
CREATE TABLE IF NOT EXISTS public.pedido (
    id_pedido SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES usuario(id_usuario),
    fecha_pedido TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) DEFAULT 'PENDIENTE',
    total DECIMAL(10,2) NOT NULL,
    direccion_entrega TEXT,
    telefono_contacto VARCHAR(20),
    notas TEXT,
    fecha_entrega_estimada DATE,
    metodo_pago VARCHAR(50)
);

-- Tabla de detalles de pedido
CREATE TABLE IF NOT EXISTS public.pedido_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_pedido INTEGER REFERENCES pedido(id_pedido),
    id_producto INTEGER REFERENCES producto(id_producto),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    descuento_aplicado DECIMAL(5,2) DEFAULT 0.00
);

-- Tabla de pagos
CREATE TABLE IF NOT EXISTS public.pago (
    id_pago SERIAL PRIMARY KEY,
    id_pedido INTEGER REFERENCES pedido(id_pedido),
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    estado_pago VARCHAR(50) DEFAULT 'PENDIENTE',
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    referencia_transaccion VARCHAR(100),
    detalles_adicionales TEXT
);

-- Tabla de transacciones de pago
CREATE TABLE IF NOT EXISTS public.transaccion_pago (
    id_transaccion SERIAL PRIMARY KEY,
    id_pago INTEGER REFERENCES pago(id_pago),
    codigo_transaccion VARCHAR(100) UNIQUE,
    estado VARCHAR(50) DEFAULT 'INICIADA',
    monto DECIMAL(10,2) NOT NULL,
    divisa VARCHAR(10) DEFAULT 'PEN',
    proveedor_pago VARCHAR(50),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metadata_adicional TEXT
);

-- Tabla de suscripciones
CREATE TABLE IF NOT EXISTS public.suscripcion (
    id_suscripcion SERIAL PRIMARY KEY,
    id_usuario INTEGER REFERENCES usuario(id_usuario),
    email VARCHAR(150) NOT NULL,
    estado BOOLEAN DEFAULT true,
    fecha_suscripcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo_suscripcion VARCHAR(50) DEFAULT 'NEWSLETTER',
    preferencias TEXT
);

-- Tabla de imágenes de producto
CREATE TABLE IF NOT EXISTS public.imagen_producto (
    id_imagen SERIAL PRIMARY KEY,
    id_producto INTEGER REFERENCES producto(id_producto),
    url_imagen VARCHAR(500) NOT NULL,
    alt_texto VARCHAR(200),
    es_principal BOOLEAN DEFAULT false,
    orden_display INTEGER DEFAULT 1,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de imágenes de perfil
CREATE TABLE IF NOT EXISTS public.imagen_perfil (
    id_imagen SERIAL PRIMARY KEY,
    id_usuario BIGINT NOT NULL,
    tipo_usuario VARCHAR(20) NOT NULL CHECK (tipo_usuario IN ('ADMIN', 'CLIENTE')),
    nombre_archivo VARCHAR(255) NOT NULL,
    ruta_archivo VARCHAR(500) NOT NULL,
    tipo_contenido VARCHAR(100),
    tamaño_archivo BIGINT,
    fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(id_usuario, tipo_usuario)
);

-- 3. Crear índices para optimización
CREATE INDEX idx_usuario_email ON usuario(email);
CREATE INDEX idx_usuario_rol ON usuario(id_rol);
CREATE INDEX idx_producto_categoria ON producto(categoria);
CREATE INDEX idx_producto_estado ON producto(estado);
CREATE INDEX idx_pedido_usuario ON pedido(id_usuario);
CREATE INDEX idx_pedido_estado ON pedido(estado);
CREATE INDEX idx_pedido_detalle_pedido ON pedido_detalle(id_pedido);
CREATE INDEX idx_pedido_detalle_producto ON pedido_detalle(id_producto);
CREATE INDEX idx_inventario_producto ON inventario(id_producto);
CREATE INDEX idx_pago_pedido ON pago(id_pedido);
CREATE INDEX idx_imagen_producto_producto ON imagen_producto(id_producto);
CREATE INDEX idx_imagen_perfil_usuario_tipo ON imagen_perfil(id_usuario, tipo_usuario);

-- 4. Insertar datos iniciales básicos
-- Roles del sistema
INSERT INTO rol (nombre, descripcion, estado) VALUES 
    ('ROLE_ADMIN', 'Administrador del sistema EcoVivaShop', true),
    ('ROLE_CLIENTE', 'Cliente de EcoVivaShop', true)
ON CONFLICT (nombre) DO NOTHING;

-- Usuario administrador por defecto
INSERT INTO usuario (nombre, apellido, email, password, telefono, estado, id_rol) 
SELECT 
    'Admin', 
    'EcoVivaShop', 
    'admin@ecovivashop.com', 
    '.WpEqmxPShwz8rCqsXSrO2vE1qcJpaC6.RVJxkz3fcP7gSURiWuC', -- password: admin123
    '+51999999999', 
    true, 
    r.id_rol 
FROM rol r 
WHERE r.nombre = 'ROLE_ADMIN'
ON CONFLICT (email) DO NOTHING;

COMMIT;
