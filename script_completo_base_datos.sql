-- ============================================================================
-- SCRIPT COMPLETO DE BASE DE DATOS - ECOMAXTIENDA
-- Sistema de Gestión de Tienda Virtual con Portal Administrativo
-- ============================================================================
-- Fecha de Creación: Junio 2025
-- Base de Datos: PostgreSQL 14+
-- Descripción: Schema completo para sistema de ecommerce con gestión administrativa
-- 
-- NOTA IMPORTANTE SOBRE VALORES NULL:
-- Los valores NULL en este sistema son NORMALES y ESPERADOS:
-- - apellido: Campo opcional en registro de usuarios
-- - telefono: Se puede agregar después del registro inicial  
-- - direccion: Se completa al hacer el primer pedido
-- - fecha_nacimiento: Campo opcional por privacidad
-- - ultimo_acceso: NULL hasta el primer login del usuario
-- Estos valores están manejados apropiadamente en el código backend.
-- ============================================================================

-- ============================================================================
-- CONFIGURACIÓN INICIAL DE BASE DE DATOS
-- ============================================================================

-- Crear base de datos (ejecutar como superusuario)
-- CREATE DATABASE ecomaxtienda_db;
-- \c ecomaxtienda_db;

-- Configurar zona horaria
SET timezone = 'America/Lima';

-- ============================================================================
-- ELIMINACIÓN DE TABLAS EXISTENTES (Para recrear schema limpio)
-- ============================================================================

-- Eliminar tablas en orden correcto para respetar foreign keys
DROP TABLE IF EXISTS tb_suscripcion CASCADE;
DROP TABLE IF EXISTS tb_pago CASCADE;
DROP TABLE IF EXISTS tb_pedido_detalle CASCADE;
DROP TABLE IF EXISTS tb_pedido CASCADE;
DROP TABLE IF EXISTS tb_inventario CASCADE;
DROP TABLE IF EXISTS tb_producto CASCADE;
DROP TABLE IF EXISTS tb_usuario CASCADE;
DROP TABLE IF EXISTS tb_rol CASCADE;

-- Eliminar secuencias si existen
DROP SEQUENCE IF EXISTS tb_rol_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tb_usuario_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tb_producto_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tb_inventario_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tb_pedido_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tb_pedido_detalle_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tb_pago_id_seq CASCADE;
DROP SEQUENCE IF EXISTS tb_suscripcion_id_seq CASCADE;

-- ============================================================================
-- CREACIÓN DE SECUENCIAS
-- ============================================================================

CREATE SEQUENCE tb_rol_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE tb_usuario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE tb_producto_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE tb_inventario_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE tb_pedido_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE tb_pedido_detalle_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE tb_pago_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE tb_suscripcion_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

-- ============================================================================
-- TABLA: tb_rol
-- Descripción: Define los roles del sistema (ADMIN, CLIENTE, SUPER_ADMIN)
-- ============================================================================

CREATE TABLE tb_rol (
    id INTEGER NOT NULL DEFAULT nextval('tb_rol_id_seq'),
    nombre VARCHAR(50) NOT NULL UNIQUE,
    descripcion TEXT,
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT pk_tb_rol PRIMARY KEY (id),
    CONSTRAINT chk_rol_nombre CHECK (nombre IN ('ADMIN', 'CLIENTE', 'SUPER_ADMIN'))
);

-- Comentarios para documentación
COMMENT ON TABLE tb_rol IS 'Tabla de roles del sistema para control de acceso';
COMMENT ON COLUMN tb_rol.id IS 'Identificador único del rol';
COMMENT ON COLUMN tb_rol.nombre IS 'Nombre del rol (ADMIN, CLIENTE, SUPER_ADMIN)';
COMMENT ON COLUMN tb_rol.descripcion IS 'Descripción detallada del rol';
COMMENT ON COLUMN tb_rol.activo IS 'Indica si el rol está activo';

-- ============================================================================
-- TABLA: tb_usuario
-- Descripción: Almacena información de usuarios (clientes y administradores)
-- NOTA: Varios campos son NULLABLE por diseño para permitir registro flexible
-- ============================================================================

CREATE TABLE tb_usuario (
    id INTEGER NOT NULL DEFAULT nextval('tb_usuario_id_seq'),
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100), -- NULLABLE: Usuario puede registrarse solo con nombre
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20), -- NULLABLE: Campo opcional en registro
    direccion TEXT, -- NULLABLE: Se puede completar después del registro
    fecha_nacimiento DATE, -- NULLABLE: Campo opcional por privacidad
    activo BOOLEAN NOT NULL DEFAULT true,
    email_verificado BOOLEAN NOT NULL DEFAULT false,
    token_verificacion VARCHAR(255), -- NULLABLE: Solo se usa durante verificación
    ultimo_acceso TIMESTAMP, -- NULLABLE: NULL hasta primer login
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    rol_id INTEGER NOT NULL,
    
    -- Constraints
    CONSTRAINT pk_tb_usuario PRIMARY KEY (id),
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES tb_rol(id),
    CONSTRAINT chk_email_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_telefono_format CHECK (telefono IS NULL OR telefono ~* '^[+]?[0-9\s\-()]{7,20}$')
);

-- Índices para optimización
CREATE INDEX idx_usuario_email ON tb_usuario(email);
CREATE INDEX idx_usuario_rol ON tb_usuario(rol_id);
CREATE INDEX idx_usuario_activo ON tb_usuario(activo);

-- Comentarios para documentación
COMMENT ON TABLE tb_usuario IS 'Tabla principal de usuarios del sistema (clientes y administradores)';
COMMENT ON COLUMN tb_usuario.apellido IS 'Apellido del usuario - NULLABLE: opcional en registro';
COMMENT ON COLUMN tb_usuario.telefono IS 'Teléfono del usuario - NULLABLE: campo opcional';
COMMENT ON COLUMN tb_usuario.direccion IS 'Dirección del usuario - NULLABLE: se completa después';
COMMENT ON COLUMN tb_usuario.fecha_nacimiento IS 'Fecha de nacimiento - NULLABLE: campo opcional por privacidad';
COMMENT ON COLUMN tb_usuario.token_verificacion IS 'Token para verificación de email - NULLABLE: temporal';
COMMENT ON COLUMN tb_usuario.ultimo_acceso IS 'Último acceso del usuario - NULLABLE: NULL hasta primer login';

-- ============================================================================
-- TABLA: tb_producto
-- Descripción: Catálogo de productos de la tienda
-- ============================================================================

CREATE TABLE tb_producto (
    id INTEGER NOT NULL DEFAULT nextval('tb_producto_id_seq'),
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL,
    categoria VARCHAR(100) NOT NULL,
    marca VARCHAR(100),
    imagen_url VARCHAR(500),
    activo BOOLEAN NOT NULL DEFAULT true,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    usuario_creador_id INTEGER,
    
    -- Constraints
    CONSTRAINT pk_tb_producto PRIMARY KEY (id),
    CONSTRAINT fk_producto_usuario_creador FOREIGN KEY (usuario_creador_id) REFERENCES tb_usuario(id),
    CONSTRAINT chk_precio_positivo CHECK (precio > 0)
);

-- Índices para optimización
CREATE INDEX idx_producto_categoria ON tb_producto(categoria);
CREATE INDEX idx_producto_marca ON tb_producto(marca);
CREATE INDEX idx_producto_activo ON tb_producto(activo);

-- Comentarios para documentación
COMMENT ON TABLE tb_producto IS 'Catálogo de productos disponibles en la tienda';
COMMENT ON COLUMN tb_producto.precio IS 'Precio del producto en moneda local';
COMMENT ON COLUMN tb_producto.imagen_url IS 'URL de la imagen principal del producto';

-- ============================================================================
-- TABLA: tb_inventario
-- Descripción: Control de stock y inventario de productos
-- ============================================================================

CREATE TABLE tb_inventario (
    id INTEGER NOT NULL DEFAULT nextval('tb_inventario_id_seq'),
    producto_id INTEGER NOT NULL,
    cantidad_actual INTEGER NOT NULL DEFAULT 0,
    cantidad_minima INTEGER NOT NULL DEFAULT 10,
    cantidad_maxima INTEGER NOT NULL DEFAULT 1000,
    ubicacion VARCHAR(100),
    fecha_ultima_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT pk_tb_inventario PRIMARY KEY (id),
    CONSTRAINT fk_inventario_producto FOREIGN KEY (producto_id) REFERENCES tb_producto(id) ON DELETE CASCADE,
    CONSTRAINT uk_inventario_producto UNIQUE (producto_id),
    CONSTRAINT chk_cantidad_actual CHECK (cantidad_actual >= 0),
    CONSTRAINT chk_cantidad_minima CHECK (cantidad_minima >= 0),
    CONSTRAINT chk_cantidad_maxima CHECK (cantidad_maxima >= cantidad_minima)
);

-- Índices para optimización
CREATE INDEX idx_inventario_producto ON tb_inventario(producto_id);
CREATE INDEX idx_inventario_cantidad ON tb_inventario(cantidad_actual);

-- Comentarios para documentación
COMMENT ON TABLE tb_inventario IS 'Control de inventario y stock de productos';
COMMENT ON COLUMN tb_inventario.cantidad_minima IS 'Cantidad mínima para alertas de stock bajo';
COMMENT ON COLUMN tb_inventario.cantidad_maxima IS 'Capacidad máxima de almacenamiento';

-- ============================================================================
-- TABLA: tb_pedido
-- Descripción: Órdenes de compra realizadas por los clientes
-- ============================================================================

CREATE TABLE tb_pedido (
    id INTEGER NOT NULL DEFAULT nextval('tb_pedido_id_seq'),
    usuario_id INTEGER NOT NULL,
    fecha_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    total DECIMAL(10,2) NOT NULL,
    direccion_entrega TEXT NOT NULL,
    telefono_contacto VARCHAR(20),
    notas TEXT,
    fecha_estimada_entrega DATE,
    fecha_entrega_real TIMESTAMP, -- NULLABLE: NULL hasta entrega real
    metodo_pago VARCHAR(50),
    
    -- Constraints
    CONSTRAINT pk_tb_pedido PRIMARY KEY (id),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id),
    CONSTRAINT chk_pedido_estado CHECK (estado IN ('PENDIENTE', 'CONFIRMADO', 'ENVIADO', 'ENTREGADO', 'CANCELADO')),
    CONSTRAINT chk_total_positivo CHECK (total > 0)
);

-- Índices para optimización
CREATE INDEX idx_pedido_usuario ON tb_pedido(usuario_id);
CREATE INDEX idx_pedido_estado ON tb_pedido(estado);
CREATE INDEX idx_pedido_fecha ON tb_pedido(fecha_pedido);

-- Comentarios para documentación
COMMENT ON TABLE tb_pedido IS 'Órdenes de compra realizadas por los clientes';
COMMENT ON COLUMN tb_pedido.estado IS 'Estado del pedido: PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO';
COMMENT ON COLUMN tb_pedido.fecha_estimada_entrega IS 'Fecha estimada de entrega';
COMMENT ON COLUMN tb_pedido.fecha_entrega_real IS 'Fecha real de entrega - NULLABLE: NULL hasta entrega';

-- ============================================================================
-- TABLA: tb_pedido_detalle
-- Descripción: Detalle de productos en cada pedido (líneas de pedido)
-- ============================================================================

CREATE TABLE tb_pedido_detalle (
    id INTEGER NOT NULL DEFAULT nextval('tb_pedido_detalle_id_seq'),
    pedido_id INTEGER NOT NULL,
    producto_id INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    
    -- Constraints
    CONSTRAINT pk_tb_pedido_detalle PRIMARY KEY (id),
    CONSTRAINT fk_pedido_detalle_pedido FOREIGN KEY (pedido_id) REFERENCES tb_pedido(id) ON DELETE CASCADE,
    CONSTRAINT fk_pedido_detalle_producto FOREIGN KEY (producto_id) REFERENCES tb_producto(id),
    CONSTRAINT chk_cantidad_positiva CHECK (cantidad > 0),
    CONSTRAINT chk_precio_unitario_positivo CHECK (precio_unitario > 0),
    CONSTRAINT chk_subtotal_positivo CHECK (subtotal > 0)
);

-- Índices para optimización
CREATE INDEX idx_pedido_detalle_pedido ON tb_pedido_detalle(pedido_id);
CREATE INDEX idx_pedido_detalle_producto ON tb_pedido_detalle(producto_id);

-- Comentarios para documentación
COMMENT ON TABLE tb_pedido_detalle IS 'Detalle de productos incluidos en cada pedido';
COMMENT ON COLUMN tb_pedido_detalle.precio_unitario IS 'Precio del producto al momento del pedido';

-- ============================================================================
-- TABLA: tb_pago
-- Descripción: Registro de pagos realizados para los pedidos
-- ============================================================================

CREATE TABLE tb_pago (
    id INTEGER NOT NULL DEFAULT nextval('tb_pago_id_seq'),
    pedido_id INTEGER NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    estado_pago VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    referencia_transaccion VARCHAR(200), -- NULLABLE: Depende del método de pago
    notas TEXT,
    
    -- Constraints
    CONSTRAINT pk_tb_pago PRIMARY KEY (id),
    CONSTRAINT fk_pago_pedido FOREIGN KEY (pedido_id) REFERENCES tb_pedido(id) ON DELETE CASCADE,
    CONSTRAINT chk_monto_positivo CHECK (monto > 0),
    CONSTRAINT chk_metodo_pago CHECK (metodo_pago IN ('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'PAYPAL', 'YAPE', 'PLIN')),
    CONSTRAINT chk_estado_pago CHECK (estado_pago IN ('PENDIENTE', 'COMPLETADO', 'FALLIDO', 'REEMBOLSADO'))
);

-- Índices para optimización
CREATE INDEX idx_pago_pedido ON tb_pago(pedido_id);
CREATE INDEX idx_pago_estado ON tb_pago(estado_pago);
CREATE INDEX idx_pago_fecha ON tb_pago(fecha_pago);

-- Comentarios para documentación
COMMENT ON TABLE tb_pago IS 'Registro de pagos asociados a los pedidos';
COMMENT ON COLUMN tb_pago.referencia_transaccion IS 'Referencia externa del sistema de pago - NULLABLE';

-- ============================================================================
-- TABLA: tb_suscripcion
-- Descripción: Suscripciones de usuarios para newsletter y promociones
-- ============================================================================

CREATE TABLE tb_suscripcion (
    id INTEGER NOT NULL DEFAULT nextval('tb_suscripcion_id_seq'),
    usuario_id INTEGER, -- NULLABLE: Permite suscripciones de no usuarios
    email VARCHAR(150) NOT NULL,
    tipo_suscripcion VARCHAR(50) NOT NULL DEFAULT 'NEWSLETTER',
    activa BOOLEAN NOT NULL DEFAULT true,
    fecha_suscripcion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cancelacion TIMESTAMP, -- NULLABLE: NULL mientras esté activa
    
    -- Constraints
    CONSTRAINT pk_tb_suscripcion PRIMARY KEY (id),
    CONSTRAINT fk_suscripcion_usuario FOREIGN KEY (usuario_id) REFERENCES tb_usuario(id) ON DELETE SET NULL,
    CONSTRAINT chk_email_suscripcion_format CHECK (email ~* '^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$'),
    CONSTRAINT chk_tipo_suscripcion CHECK (tipo_suscripcion IN ('NEWSLETTER', 'PROMOCIONES', 'OFERTAS_ESPECIALES'))
);

-- Índices para optimización
CREATE INDEX idx_suscripcion_email ON tb_suscripcion(email);
CREATE INDEX idx_suscripcion_activa ON tb_suscripcion(activa);
CREATE INDEX idx_suscripcion_tipo ON tb_suscripcion(tipo_suscripcion);

-- Comentarios para documentación
COMMENT ON TABLE tb_suscripcion IS 'Suscripciones de usuarios para comunicaciones por email';
COMMENT ON COLUMN tb_suscripcion.usuario_id IS 'ID del usuario - NULLABLE: permite suscripciones de no usuarios';
COMMENT ON COLUMN tb_suscripcion.fecha_cancelacion IS 'Fecha de cancelación - NULLABLE: NULL mientras esté activa';

-- ============================================================================
-- TRIGGERS PARA AUDITORÍA Y AUTOMATIZACIÓN
-- ============================================================================

-- Función para actualizar fecha_actualizacion automáticamente
CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Triggers para actualización automática de fechas
CREATE TRIGGER tr_usuario_fecha_actualizacion
    BEFORE UPDATE ON tb_usuario
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER tr_producto_fecha_actualizacion
    BEFORE UPDATE ON tb_producto
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

-- Función para actualizar inventario automáticamente
CREATE OR REPLACE FUNCTION actualizar_inventario_tras_pedido()
RETURNS TRIGGER AS $$
BEGIN
    -- Actualizar cantidad en inventario cuando se confirma un pedido
    IF NEW.estado = 'CONFIRMADO' AND OLD.estado = 'PENDIENTE' THEN
        UPDATE tb_inventario 
        SET cantidad_actual = cantidad_actual - pd.cantidad,
            fecha_ultima_actualizacion = CURRENT_TIMESTAMP
        FROM tb_pedido_detalle pd
        WHERE pd.pedido_id = NEW.id 
        AND tb_inventario.producto_id = pd.producto_id;
    END IF;
    
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger para actualización automática de inventario
CREATE TRIGGER tr_actualizar_inventario_pedido
    AFTER UPDATE ON tb_pedido
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_inventario_tras_pedido();

-- ============================================================================
-- VISTAS PARA REPORTES Y CONSULTAS FRECUENTES
-- ============================================================================

-- Vista: Resumen de usuarios activos por rol
CREATE OR REPLACE VIEW v_usuarios_por_rol AS
SELECT 
    r.nombre as rol,
    COUNT(u.id) as total_usuarios,
    COUNT(CASE WHEN u.activo = true THEN 1 END) as usuarios_activos,
    COUNT(CASE WHEN u.email_verificado = true THEN 1 END) as emails_verificados,
    COUNT(CASE WHEN u.apellido IS NULL THEN 1 END) as sin_apellido,
    COUNT(CASE WHEN u.telefono IS NULL THEN 1 END) as sin_telefono,
    COUNT(CASE WHEN u.direccion IS NULL THEN 1 END) as sin_direccion
FROM tb_rol r
LEFT JOIN tb_usuario u ON r.id = u.rol_id
GROUP BY r.id, r.nombre
ORDER BY r.nombre;

-- Vista: Productos con información de inventario
CREATE OR REPLACE VIEW v_productos_inventario AS
SELECT 
    p.id,
    p.nombre,
    p.categoria,
    p.marca,
    p.precio,
    COALESCE(i.cantidad_actual, 0) as stock_actual,
    COALESCE(i.cantidad_minima, 0) as stock_minimo,
    CASE 
        WHEN COALESCE(i.cantidad_actual, 0) <= COALESCE(i.cantidad_minima, 0) THEN 'STOCK_BAJO'
        WHEN COALESCE(i.cantidad_actual, 0) = 0 THEN 'SIN_STOCK'
        ELSE 'DISPONIBLE'
    END as estado_stock,
    p.activo
FROM tb_producto p
LEFT JOIN tb_inventario i ON p.id = i.producto_id
ORDER BY p.nombre;

-- Vista: Resumen de pedidos por usuario (manejo de campos NULL)
CREATE OR REPLACE VIEW v_resumen_pedidos_usuario AS
SELECT 
    u.id as usuario_id,
    u.nombre,
    COALESCE(u.apellido, '') as apellido,
    u.email,
    COALESCE(u.telefono, 'No especificado') as telefono,
    COUNT(p.id) as total_pedidos,
    COALESCE(SUM(p.total), 0) as monto_total_compras,
    MAX(p.fecha_pedido) as ultima_compra,
    COUNT(CASE WHEN p.estado = 'PENDIENTE' THEN 1 END) as pedidos_pendientes
FROM tb_usuario u
LEFT JOIN tb_pedido p ON u.id = p.usuario_id
WHERE u.rol_id = (SELECT id FROM tb_rol WHERE nombre = 'CLIENTE')
GROUP BY u.id, u.nombre, u.apellido, u.email, u.telefono
ORDER BY monto_total_compras DESC;

-- Vista: Productos más vendidos
CREATE OR REPLACE VIEW v_productos_mas_vendidos AS
SELECT 
    p.id,
    p.nombre,
    p.categoria,
    p.precio,
    COALESCE(SUM(pd.cantidad), 0) as total_vendido,
    COALESCE(SUM(pd.subtotal), 0) as ingresos_generados,
    COUNT(DISTINCT pd.pedido_id) as numero_pedidos
FROM tb_producto p
LEFT JOIN tb_pedido_detalle pd ON p.id = pd.producto_id
LEFT JOIN tb_pedido pe ON pd.pedido_id = pe.id
WHERE pe.estado != 'CANCELADO' OR pe.estado IS NULL
GROUP BY p.id, p.nombre, p.categoria, p.precio
ORDER BY total_vendido DESC;

-- ============================================================================
-- FUNCIONES ÚTILES PARA EL SISTEMA
-- ============================================================================

-- Función para obtener estadísticas del dashboard
CREATE OR REPLACE FUNCTION obtener_estadisticas_dashboard()
RETURNS TABLE (
    total_usuarios INTEGER,
    total_clientes INTEGER,
    total_admins INTEGER,
    usuarios_con_info_completa INTEGER,
    usuarios_sin_telefono INTEGER,
    total_productos INTEGER,
    productos_activos INTEGER,
    total_pedidos INTEGER,
    pedidos_pendientes INTEGER,
    ingresos_totales DECIMAL(10,2),
    productos_bajo_stock INTEGER
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        (SELECT COUNT(*)::INTEGER FROM tb_usuario WHERE activo = true),
        (SELECT COUNT(*)::INTEGER FROM tb_usuario u JOIN tb_rol r ON u.rol_id = r.id WHERE r.nombre = 'CLIENTE' AND u.activo = true),
        (SELECT COUNT(*)::INTEGER FROM tb_usuario u JOIN tb_rol r ON u.rol_id = r.id WHERE r.nombre IN ('ADMIN', 'SUPER_ADMIN') AND u.activo = true),
        (SELECT COUNT(*)::INTEGER FROM tb_usuario WHERE telefono IS NOT NULL AND direccion IS NOT NULL AND activo = true),
        (SELECT COUNT(*)::INTEGER FROM tb_usuario WHERE telefono IS NULL AND activo = true),
        (SELECT COUNT(*)::INTEGER FROM tb_producto),
        (SELECT COUNT(*)::INTEGER FROM tb_producto WHERE activo = true),
        (SELECT COUNT(*)::INTEGER FROM tb_pedido),
        (SELECT COUNT(*)::INTEGER FROM tb_pedido WHERE estado = 'PENDIENTE'),
        (SELECT COALESCE(SUM(total), 0) FROM tb_pedido WHERE estado != 'CANCELADO'),
        (SELECT COUNT(*)::INTEGER FROM tb_inventario WHERE cantidad_actual <= cantidad_minima);
END;
$$ LANGUAGE plpgsql;

-- Función para obtener información completa de usuario (maneja NULLs)
CREATE OR REPLACE FUNCTION obtener_info_usuario(usuario_id_param INTEGER)
RETURNS TABLE (
    id INTEGER,
    nombre_completo VARCHAR(201),
    email VARCHAR(150),
    telefono_display VARCHAR(25),
    direccion_display TEXT,
    tiene_info_completa BOOLEAN,
    ultimo_acceso_display VARCHAR(30),
    rol_nombre VARCHAR(50)
) AS $$
BEGIN
    RETURN QUERY
    SELECT 
        u.id,
        (u.nombre || CASE WHEN u.apellido IS NOT NULL THEN ' ' || u.apellido ELSE '' END)::VARCHAR(201),
        u.email,
        CASE WHEN u.telefono IS NOT NULL THEN u.telefono ELSE 'No especificado' END::VARCHAR(25),
        CASE WHEN u.direccion IS NOT NULL THEN u.direccion ELSE 'No especificada' END,
        (u.telefono IS NOT NULL AND u.direccion IS NOT NULL),
        CASE 
            WHEN u.ultimo_acceso IS NOT NULL THEN u.ultimo_acceso::VARCHAR(30)
            ELSE 'Nunca'
        END::VARCHAR(30),
        r.nombre
    FROM tb_usuario u
    JOIN tb_rol r ON u.rol_id = r.id
    WHERE u.id = usuario_id_param;
END;
$$ LANGUAGE plpgsql;

-- ============================================================================
-- INSERCIÓN DE DATOS INICIALES
-- ============================================================================

-- Insertar roles del sistema
INSERT INTO tb_rol (nombre, descripcion) VALUES 
('SUPER_ADMIN', 'Administrador principal con acceso completo al sistema'),
('ADMIN', 'Administrador con permisos de gestión de productos y pedidos'),
('CLIENTE', 'Usuario cliente con permisos de compra y gestión de perfil')
ON CONFLICT (nombre) DO NOTHING;

-- Insertar usuario super administrador por defecto
INSERT INTO tb_usuario (nombre, apellido, email, password, activo, email_verificado, rol_id) VALUES 
('Super', 'Admin', 'admin@ecomaxtienda.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP', true, true, 
 (SELECT id FROM tb_rol WHERE nombre = 'SUPER_ADMIN'))
ON CONFLICT (email) DO NOTHING;

-- Insertar usuario administrador de ejemplo
INSERT INTO tb_usuario (nombre, apellido, email, password, activo, email_verificado, rol_id) VALUES 
('Ana', 'García', 'ana.garcia@ecomaxtienda.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP', true, true,
 (SELECT id FROM tb_rol WHERE nombre = 'ADMIN'))
ON CONFLICT (email) DO NOTHING;

-- Insertar cliente con información completa (datos para Perú)
INSERT INTO tb_usuario (nombre, apellido, email, password, telefono, direccion, activo, email_verificado, rol_id) VALUES 
('Juan Carlos', 'Pérez García', 'juan.perez@gmail.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP', 
 '+51 987 654 321', 'Av. El Sol 315, Cusco, Perú', true, true,
 (SELECT id FROM tb_rol WHERE nombre = 'CLIENTE'))
ON CONFLICT (email) DO NOTHING;

-- Insertar cliente con información parcial (demostrar manejo de NULLs)
INSERT INTO tb_usuario (nombre, email, password, activo, email_verificado, rol_id) VALUES 
('María', 'maria.lopez@hotmail.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP', 
 true, true, (SELECT id FROM tb_rol WHERE nombre = 'CLIENTE'))
ON CONFLICT (email) DO NOTHING;

-- Insertar productos de ejemplo (precios en soles peruanos - PEN)
INSERT INTO tb_producto (nombre, descripcion, precio, categoria, marca, activo, usuario_creador_id) VALUES 
('Smartphone Samsung Galaxy S23', 'Teléfono inteligente de última generación con cámara de 108MP. Precio: S/ 3,299 (~$890 USD)', 3299.00, 'Electrónicos', 'Samsung', true,
 (SELECT id FROM tb_usuario WHERE email = 'admin@ecomaxtienda.com')),
('Laptop HP Pavilion 15', 'Laptop para trabajo y entretenimiento con procesador Intel i7. Precio: S/ 2,799 (~$755 USD)', 2799.00, 'Computadoras', 'HP', true,
 (SELECT id FROM tb_usuario WHERE email = 'admin@ecomaxtienda.com')),
('Auriculares Sony WH-1000XM4', 'Auriculares inalámbricos con cancelación de ruido. Precio: S/ 999 (~$270 USD)', 999.00, 'Audio', 'Sony', true,
 (SELECT id FROM tb_usuario WHERE email = 'admin@ecomaxtienda.com')),
('Smartwatch Apple Watch Series 8', 'Reloj inteligente con monitoreo de salud avanzado. Precio: S/ 1,599 (~$430 USD)', 1599.00, 'Wearables', 'Apple', true,
 (SELECT id FROM tb_usuario WHERE email = 'admin@ecomaxtienda.com')),
('Tablet iPad Air', 'Tablet de alto rendimiento para creatividad y productividad. Precio: S/ 2,199 (~$595 USD)', 2199.00, 'Tablets', 'Apple', true,
 (SELECT id FROM tb_usuario WHERE email = 'admin@ecomaxtienda.com'))
ON CONFLICT DO NOTHING;

-- Insertar inventario para los productos (almacén en Lima)
INSERT INTO tb_inventario (producto_id, cantidad_actual, cantidad_minima, cantidad_maxima, ubicacion) 
SELECT p.id, 50, 10, 200, 'Almacén Lima Centro - Perú'
FROM tb_producto p
WHERE NOT EXISTS (SELECT 1 FROM tb_inventario i WHERE i.producto_id = p.id);

-- Insertar pedido de ejemplo (dirección peruana)
INSERT INTO tb_pedido (usuario_id, estado, total, direccion_entrega, telefono_contacto, metodo_pago) VALUES 
((SELECT id FROM tb_usuario WHERE email = 'juan.perez@gmail.com'), 'ENTREGADO', 4298.00, 
 'Av. El Sol 315, Cusco, Perú', '+51 987 654 321', 'TARJETA')
ON CONFLICT DO NOTHING;

-- Insertar detalles del pedido de ejemplo
INSERT INTO tb_pedido_detalle (pedido_id, producto_id, cantidad, precio_unitario, subtotal) 
SELECT 
    (SELECT id FROM tb_pedido ORDER BY id LIMIT 1),
    p.id,
    1,
    p.precio,
    p.precio
FROM tb_producto p 
WHERE p.nombre IN ('Smartphone Samsung Galaxy S23', 'Auriculares Sony WH-1000XM4')
AND NOT EXISTS (SELECT 1 FROM tb_pedido_detalle pd WHERE pd.producto_id = p.id);

-- Insertar pago del pedido de ejemplo (actualizado con nuevos totales)
INSERT INTO tb_pago (pedido_id, monto, metodo_pago, estado_pago, referencia_transaccion) VALUES 
((SELECT id FROM tb_pedido ORDER BY id LIMIT 1), 4298.00, 'TARJETA', 'COMPLETADO', 'PER_20250611_001')
ON CONFLICT DO NOTHING;

-- Insertar suscripciones de ejemplo (correos peruanos)
INSERT INTO tb_suscripcion (usuario_id, email, tipo_suscripcion, activa) VALUES 
((SELECT id FROM tb_usuario WHERE email = 'juan.perez@gmail.com'), 'juan.perez@gmail.com', 'NEWSLETTER', true),
(NULL, 'suscriptor.lima@gmail.com', 'PROMOCIONES', true)
ON CONFLICT DO NOTHING;

-- ============================================================================
-- CONSULTAS DE VERIFICACIÓN Y DIAGNÓSTICO
-- ============================================================================

-- Verificar que todas las tablas fueron creadas
DO $$
DECLARE
    tabla_count INTEGER;
    vista_count INTEGER;
    funcion_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO tabla_count FROM information_schema.tables 
    WHERE table_schema = 'public' AND table_name LIKE 'tb_%';
    
    SELECT COUNT(*) INTO vista_count FROM information_schema.views 
    WHERE table_schema = 'public' AND table_name LIKE 'v_%';
    
    SELECT COUNT(*) INTO funcion_count FROM information_schema.routines 
    WHERE routine_schema = 'public' AND routine_type = 'FUNCTION';
    
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'VERIFICACIÓN DE CREACIÓN DE OBJETOS:';
    RAISE NOTICE 'Tablas creadas: % de 8 esperadas', tabla_count;
    RAISE NOTICE 'Vistas creadas: % de 4 esperadas', vista_count;
    RAISE NOTICE 'Funciones creadas: % esperadas', funcion_count;
    RAISE NOTICE '============================================================================';
END $$;

-- Consulta de diagnóstico de valores NULL
CREATE OR REPLACE VIEW v_diagnostico_nulls AS
SELECT 
    'tb_usuario' as tabla,
    'apellido' as campo,
    COUNT(*) as total_registros,
    COUNT(*) FILTER (WHERE apellido IS NULL) as valores_null,
    ROUND(COUNT(*) FILTER (WHERE apellido IS NULL) * 100.0 / COUNT(*), 2) as porcentaje_null,
    'Campo opcional en registro' as motivo
FROM tb_usuario

UNION ALL

SELECT 
    'tb_usuario',
    'telefono',
    COUNT(*),
    COUNT(*) FILTER (WHERE telefono IS NULL),
    ROUND(COUNT(*) FILTER (WHERE telefono IS NULL) * 100.0 / COUNT(*), 2),
    'Campo opcional, se completa después'
FROM tb_usuario

UNION ALL

SELECT 
    'tb_usuario',
    'direccion',
    COUNT(*),
    COUNT(*) FILTER (WHERE direccion IS NULL),
    ROUND(COUNT(*) FILTER (WHERE direccion IS NULL) * 100.0 / COUNT(*), 2),
    'Se completa al hacer primer pedido'
FROM tb_usuario

UNION ALL

SELECT 
    'tb_usuario',
    'ultimo_acceso',
    COUNT(*),
    COUNT(*) FILTER (WHERE ultimo_acceso IS NULL),
    ROUND(COUNT(*) FILTER (WHERE ultimo_acceso IS NULL) * 100.0 / COUNT(*), 2),
    'NULL hasta primer login'
FROM tb_usuario;

-- ============================================================================
-- CONFIGURACIÓN DE PERMISOS Y SEGURIDAD (OPCIONAL PARA PRODUCCIÓN)
-- ============================================================================

-- Crear usuario de aplicación (comentado para desarrollo)
/*
CREATE USER ecomax_app WITH PASSWORD 'password_seguro_aqui';
GRANT CONNECT ON DATABASE ecomaxtienda_db TO ecomax_app;
GRANT USAGE ON SCHEMA public TO ecomax_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO ecomax_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO ecomax_app;
*/

-- ============================================================================
-- FINALIZACIÓN DEL SCRIPT
-- ============================================================================

-- Actualizar estadísticas de la base de datos
ANALYZE;

-- Mensaje de confirmación
DO $$
DECLARE
    stats_usuarios RECORD;
    stats_productos RECORD;
    stats_pedidos RECORD;
BEGIN
    -- Obtener estadísticas
    SELECT 
        COUNT(*) as total,
        COUNT(*) FILTER (WHERE apellido IS NULL) as sin_apellido,
        COUNT(*) FILTER (WHERE telefono IS NULL) as sin_telefono,
        COUNT(*) FILTER (WHERE direccion IS NULL) as sin_direccion
    INTO stats_usuarios FROM tb_usuario;
    
    SELECT COUNT(*) as total INTO stats_productos FROM tb_producto;
    SELECT COUNT(*) as total INTO stats_pedidos FROM tb_pedido;
    
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'SCRIPT DE BASE DE DATOS ECOMAXTIENDA EJECUTADO EXITOSAMENTE';
    RAISE NOTICE '============================================================================';
    RAISE NOTICE 'OBJETOS CREADOS:';
    RAISE NOTICE '- Tablas: 8 (tb_rol, tb_usuario, tb_producto, tb_inventario, tb_pedido, tb_pedido_detalle, tb_pago, tb_suscripcion)';
    RAISE NOTICE '- Vistas: 5 (estadísticas, reportes, diagnóstico)';
    RAISE NOTICE '- Triggers: 3 (auditoría de fechas, actualización de inventario)';
    RAISE NOTICE '- Funciones: 4 (actualización de fechas, inventario, estadísticas, info usuario)';
    RAISE NOTICE '';
    RAISE NOTICE 'DATOS INICIALES:';
    RAISE NOTICE '- Roles: 3 (SUPER_ADMIN, ADMIN, CLIENTE)';
    RAISE NOTICE '- Usuarios: % (% sin apellido, % sin teléfono, % sin dirección)', 
                 stats_usuarios.total, stats_usuarios.sin_apellido, 
                 stats_usuarios.sin_telefono, stats_usuarios.sin_direccion;
    RAISE NOTICE '- Productos: %', stats_productos.total;
    RAISE NOTICE '- Pedidos de ejemplo: %', stats_pedidos.total;
    RAISE NOTICE '';
    RAISE NOTICE 'VALORES NULL EN LA BASE DE DATOS:';
    RAISE NOTICE '- Los valores NULL son NORMALES y ESPERADOS';
    RAISE NOTICE '- Permiten flexibilidad en el registro de usuarios';
    RAISE NOTICE '- Están manejados apropiadamente en el código backend';
    RAISE NOTICE '- Ver archivo EXPLICACION_VALORES_NULL.md para detalles';
    RAISE NOTICE '';
    RAISE NOTICE 'SISTEMA LISTO PARA USAR:';
    RAISE NOTICE '- URL: http://localhost:8081';
    RAISE NOTICE '- Super Admin: admin@ecomaxtienda.com / password123';
    RAISE NOTICE '- Cliente de prueba: juan.perez@email.com / password123';
    RAISE NOTICE '============================================================================';
END $$;

-- ================================================================
-- 2. ELIMINACIÓN DE TABLAS EXISTENTES (SI EXISTEN)
-- ================================================================

-- Eliminar restricciones de clave foránea primero
ALTER TABLE IF EXISTS tb_inventario DROP CONSTRAINT IF EXISTS FK2h5s8b11uaf6xxa00fsi7vamq;
ALTER TABLE IF EXISTS tb_pago DROP CONSTRAINT IF EXISTS FKq31me8j5no2cdyk8bjx0sbqn0;
ALTER TABLE IF EXISTS tb_pedido DROP CONSTRAINT IF EXISTS FK7y9oyauqoiyretowwfy54531g;
ALTER TABLE IF EXISTS tb_pedido_detalle DROP CONSTRAINT IF EXISTS FK1t45p5mu89h160lr4s4d470mm;
ALTER TABLE IF EXISTS tb_pedido_detalle DROP CONSTRAINT IF EXISTS FKefovy87da63a9w6nkqktu3s6n;
ALTER TABLE IF EXISTS tb_suscripcion DROP CONSTRAINT IF EXISTS FKcpp0ibmwioetkpq92980ikitv;
ALTER TABLE IF EXISTS tb_usuario DROP CONSTRAINT IF EXISTS FK6mxbmhthgow2y4dv0l13yngc5;

-- Eliminar tablas en orden correcto
DROP TABLE IF EXISTS tb_inventario CASCADE;
DROP TABLE IF EXISTS tb_pago CASCADE;
DROP TABLE IF EXISTS tb_pedido_detalle CASCADE;
DROP TABLE IF EXISTS tb_pedido CASCADE;
DROP TABLE IF EXISTS tb_suscripcion CASCADE;
DROP TABLE IF EXISTS tb_producto CASCADE;
DROP TABLE IF EXISTS tb_usuario CASCADE;
DROP TABLE IF EXISTS tb_rol CASCADE;

-- ================================================================
-- 3. CREACIÓN DE TABLAS
-- ================================================================

-- ----------------------------------------------------------------
-- 3.1 TABLA DE ROLES
-- ----------------------------------------------------------------
CREATE TABLE tb_rol (
    id_rol SERIAL PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Comentarios para la tabla tb_rol
COMMENT ON TABLE tb_rol IS 'Tabla para gestión de roles del sistema';
COMMENT ON COLUMN tb_rol.id_rol IS 'Identificador único del rol';
COMMENT ON COLUMN tb_rol.nombre IS 'Nombre del rol (ej: ROLE_ADMIN, ROLE_CLIENTE)';
COMMENT ON COLUMN tb_rol.descripcion IS 'Descripción del rol y sus permisos';
COMMENT ON COLUMN tb_rol.estado IS 'Estado activo/inactivo del rol';
COMMENT ON COLUMN tb_rol.fecha_creacion IS 'Fecha de creación del rol';

-- ----------------------------------------------------------------
-- 3.2 TABLA DE USUARIOS
-- ----------------------------------------------------------------
CREATE TABLE tb_usuario (
    id_usuario SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL DEFAULT '',
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(500),
    fecha_nacimiento TIMESTAMP,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP,
    foto_perfil VARCHAR(500),
    id_rol INTEGER NOT NULL
);

-- Comentarios para la tabla tb_usuario
COMMENT ON TABLE tb_usuario IS 'Tabla principal de usuarios del sistema';
COMMENT ON COLUMN tb_usuario.id_usuario IS 'Identificador único del usuario';
COMMENT ON COLUMN tb_usuario.nombre IS 'Nombre del usuario';
COMMENT ON COLUMN tb_usuario.apellido IS 'Apellido del usuario';
COMMENT ON COLUMN tb_usuario.email IS 'Correo electrónico único del usuario';
COMMENT ON COLUMN tb_usuario.password IS 'Contraseña encriptada del usuario';
COMMENT ON COLUMN tb_usuario.telefono IS 'Número de teléfono del usuario (opcional)';
COMMENT ON COLUMN tb_usuario.direccion IS 'Dirección física del usuario (opcional)';
COMMENT ON COLUMN tb_usuario.fecha_nacimiento IS 'Fecha de nacimiento del usuario (opcional)';
COMMENT ON COLUMN tb_usuario.estado IS 'Estado activo/inactivo del usuario';
COMMENT ON COLUMN tb_usuario.fecha_registro IS 'Fecha de registro en el sistema';
COMMENT ON COLUMN tb_usuario.ultimo_acceso IS 'Fecha del último acceso al sistema';
COMMENT ON COLUMN tb_usuario.foto_perfil IS 'URL de la foto de perfil del usuario';
COMMENT ON COLUMN tb_usuario.id_rol IS 'Referencia al rol del usuario';

-- ----------------------------------------------------------------
-- 3.3 TABLA DE PRODUCTOS
-- ----------------------------------------------------------------
CREATE TABLE tb_producto (
    id_producto SERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(1000),
    precio NUMERIC(10,2) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    marca VARCHAR(50),
    modelo VARCHAR(50),
    color VARCHAR(30),
    material VARCHAR(50),
    dimensiones VARCHAR(100),
    peso NUMERIC(8,2),
    garantia_meses INTEGER,
    eficiencia_energetica VARCHAR(10),
    puntuacion_eco NUMERIC(3,2),
    impacto_ambiental VARCHAR(20),
    imagen_url VARCHAR(500),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP
);

-- Comentarios para la tabla tb_producto
COMMENT ON TABLE tb_producto IS 'Catálogo de productos eco-sostenibles';
COMMENT ON COLUMN tb_producto.id_producto IS 'Identificador único del producto';
COMMENT ON COLUMN tb_producto.nombre IS 'Nombre comercial del producto';
COMMENT ON COLUMN tb_producto.descripcion IS 'Descripción detallada del producto';
COMMENT ON COLUMN tb_producto.precio IS 'Precio actual del producto';
COMMENT ON COLUMN tb_producto.categoria IS 'Categoría del producto';
COMMENT ON COLUMN tb_producto.eficiencia_energetica IS 'Calificación energética (A++, A+, etc.)';
COMMENT ON COLUMN tb_producto.puntuacion_eco IS 'Puntuación ecológica del 1 al 5';
COMMENT ON COLUMN tb_producto.impacto_ambiental IS 'Nivel de impacto ambiental';

-- ----------------------------------------------------------------
-- 3.4 TABLA DE INVENTARIO
-- ----------------------------------------------------------------
CREATE TABLE tb_inventario (
    id_inventario SERIAL PRIMARY KEY,
    id_producto INTEGER NOT NULL UNIQUE,
    stock INTEGER NOT NULL DEFAULT 0,
    stock_minimo INTEGER NOT NULL DEFAULT 10,
    stock_maximo INTEGER,
    ubicacion VARCHAR(100),
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_actualizacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_actualizacion VARCHAR(100)
);

-- Comentarios para la tabla tb_inventario
COMMENT ON TABLE tb_inventario IS 'Control de inventario y stock de productos';
COMMENT ON COLUMN tb_inventario.id_inventario IS 'Identificador único del registro de inventario';
COMMENT ON COLUMN tb_inventario.id_producto IS 'Referencia al producto';
COMMENT ON COLUMN tb_inventario.stock IS 'Cantidad actual en inventario';
COMMENT ON COLUMN tb_inventario.stock_minimo IS 'Cantidad mínima para alertas';
COMMENT ON COLUMN tb_inventario.stock_maximo IS 'Cantidad máxima en inventario';
COMMENT ON COLUMN tb_inventario.ubicacion IS 'Ubicación física del producto';

-- ----------------------------------------------------------------
-- 3.5 TABLA DE PEDIDOS
-- ----------------------------------------------------------------
CREATE TABLE tb_pedido (
    id_pedido SERIAL PRIMARY KEY,
    numero_pedido VARCHAR(20) NOT NULL UNIQUE,
    id_usuario INTEGER NOT NULL,
    fecha_pedido TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    subtotal NUMERIC(10,2) NOT NULL,
    impuestos NUMERIC(10,2),
    descuento NUMERIC(10,2),
    costo_envio NUMERIC(10,2),
    total NUMERIC(10,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    direccion_envio VARCHAR(500) NOT NULL,
    telefono_contacto VARCHAR(20),
    fecha_estimada_entrega TIMESTAMP,
    fecha_entrega TIMESTAMP,
    transportadora VARCHAR(50),
    numero_seguimiento VARCHAR(50),
    notas VARCHAR(1000)
);

-- Comentarios para la tabla tb_pedido
COMMENT ON TABLE tb_pedido IS 'Gestión de pedidos de clientes';
COMMENT ON COLUMN tb_pedido.numero_pedido IS 'Número único del pedido (formato: PED-YYYYMMDD-XXX)';
COMMENT ON COLUMN tb_pedido.estado IS 'Estado del pedido: PENDIENTE, CONFIRMADO, ENVIADO, ENTREGADO, CANCELADO';
COMMENT ON COLUMN tb_pedido.metodo_pago IS 'Método de pago utilizado';

-- ----------------------------------------------------------------
-- 3.6 TABLA DE DETALLES DE PEDIDO
-- ----------------------------------------------------------------
CREATE TABLE tb_pedido_detalle (
    id_detalle SERIAL PRIMARY KEY,
    id_pedido INTEGER NOT NULL,
    id_producto INTEGER NOT NULL,
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(10,2) NOT NULL,
    descuento_unitario NUMERIC(10,2),
    subtotal NUMERIC(10,2) NOT NULL,
    notas VARCHAR(500)
);

-- Comentarios para la tabla tb_pedido_detalle
COMMENT ON TABLE tb_pedido_detalle IS 'Líneas de productos en cada pedido';
COMMENT ON COLUMN tb_pedido_detalle.id_detalle IS 'Identificador único del detalle';
COMMENT ON COLUMN tb_pedido_detalle.cantidad IS 'Cantidad del producto pedido';
COMMENT ON COLUMN tb_pedido_detalle.precio_unitario IS 'Precio unitario al momento del pedido';
COMMENT ON COLUMN tb_pedido_detalle.subtotal IS 'Subtotal de la línea (cantidad * precio_unitario - descuento)';

-- ----------------------------------------------------------------
-- 3.7 TABLA DE PAGOS
-- ----------------------------------------------------------------
CREATE TABLE tb_pago (
    id_pago SERIAL PRIMARY KEY,
    id_pedido INTEGER NOT NULL,
    monto NUMERIC(10,2) NOT NULL,
    metodo_pago VARCHAR(30) NOT NULL,
    estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE',
    fecha_pago TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_aprobacion TIMESTAMP,
    numero_transaccion VARCHAR(100),
    numero_aprobacion VARCHAR(50),
    codigo_respuesta VARCHAR(10),
    mensaje_respuesta VARCHAR(500),
    entidad_financiera VARCHAR(100),
    referencia_pago VARCHAR(100),
    ip_cliente VARCHAR(45),
    user_agent VARCHAR(500),
    datos_adicionales VARCHAR(1000)
);

-- Comentarios para la tabla tb_pago
COMMENT ON TABLE tb_pago IS 'Registro de transacciones de pago';
COMMENT ON COLUMN tb_pago.estado IS 'Estado del pago: PENDIENTE, APROBADO, RECHAZADO, CANCELADO';
COMMENT ON COLUMN tb_pago.numero_transaccion IS 'Número de transacción de la pasarela de pago';
COMMENT ON COLUMN tb_pago.ip_cliente IS 'Dirección IP del cliente para auditoría';

-- ----------------------------------------------------------------
-- 3.8 TABLA DE SUSCRIPCIONES
-- ----------------------------------------------------------------
CREATE TABLE tb_suscripcion (
    id_suscripcion SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    tipo_suscripcion VARCHAR(20) NOT NULL,
    precio_mensual NUMERIC(10,2) NOT NULL,
    descuento_porcentaje NUMERIC(5,2),
    fecha_inicio TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_fin TIMESTAMP,
    fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cancelacion TIMESTAMP,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    auto_renovacion BOOLEAN NOT NULL DEFAULT TRUE,
    motivo_cancelacion VARCHAR(500),
    beneficios VARCHAR(1000)
);

-- Comentarios para la tabla tb_suscripcion
COMMENT ON TABLE tb_suscripcion IS 'Gestión de suscripciones premium';
COMMENT ON COLUMN tb_suscripcion.tipo_suscripcion IS 'Tipo: BASICA, PREMIUM, EMPRESARIAL';
COMMENT ON COLUMN tb_suscripcion.precio_mensual IS 'Precio mensual de la suscripción';
COMMENT ON COLUMN tb_suscripcion.auto_renovacion IS 'Renovación automática activa/inactiva';

-- ================================================================
-- 4. CREACIÓN DE RESTRICCIONES DE CLAVE FORÁNEA
-- ================================================================

-- Clave foránea: tb_usuario -> tb_rol
ALTER TABLE tb_usuario 
    ADD CONSTRAINT FK6mxbmhthgow2y4dv0l13yngc5 
    FOREIGN KEY (id_rol) REFERENCES tb_rol(id_rol);

-- Clave foránea: tb_inventario -> tb_producto
ALTER TABLE tb_inventario 
    ADD CONSTRAINT FK2h5s8b11uaf6xxa00fsi7vamq 
    FOREIGN KEY (id_producto) REFERENCES tb_producto(id_producto);

-- Clave foránea: tb_pedido -> tb_usuario
ALTER TABLE tb_pedido 
    ADD CONSTRAINT FK7y9oyauqoiyretowwfy54531g 
    FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario);

-- Clave foránea: tb_pedido_detalle -> tb_pedido
ALTER TABLE tb_pedido_detalle 
    ADD CONSTRAINT FK1t45p5mu89h160lr4s4d470mm 
    FOREIGN KEY (id_pedido) REFERENCES tb_pedido(id_pedido);

-- Clave foránea: tb_pedido_detalle -> tb_producto
ALTER TABLE tb_pedido_detalle 
    ADD CONSTRAINT FKefovy87da63a9w6nkqktu3s6n 
    FOREIGN KEY (id_producto) REFERENCES tb_producto(id_producto);

-- Clave foránea: tb_pago -> tb_pedido
ALTER TABLE tb_pago 
    ADD CONSTRAINT FKq31me8j5no2cdyk8bjx0sbqn0 
    FOREIGN KEY (id_pedido) REFERENCES tb_pedido(id_pedido);

-- Clave foránea: tb_suscripcion -> tb_usuario
ALTER TABLE tb_suscripcion 
    ADD CONSTRAINT FKcpp0ibmwioetkpq92980ikitv 
    FOREIGN KEY (id_usuario) REFERENCES tb_usuario(id_usuario);

-- ================================================================
-- 5. CREACIÓN DE ÍNDICES PARA OPTIMIZACIÓN
-- ================================================================

-- Índices para mejorar rendimiento de consultas frecuentes
CREATE INDEX idx_usuario_email ON tb_usuario(email);
CREATE INDEX idx_usuario_estado ON tb_usuario(estado);
CREATE INDEX idx_producto_categoria ON tb_producto(categoria);
CREATE INDEX idx_producto_estado ON tb_producto(estado);
CREATE INDEX idx_pedido_usuario ON tb_pedido(id_usuario);
CREATE INDEX idx_pedido_fecha ON tb_pedido(fecha_pedido);
CREATE INDEX idx_pedido_estado ON tb_pedido(estado);
CREATE INDEX idx_pago_estado ON tb_pago(estado);
CREATE INDEX idx_pago_fecha ON tb_pago(fecha_pago);

-- ================================================================
-- 6. INSERCIÓN DE DATOS INICIALES
-- ================================================================

-- ----------------------------------------------------------------
-- 6.1 INSERTAR ROLES DEL SISTEMA
-- ----------------------------------------------------------------
INSERT INTO tb_rol (nombre, descripcion, estado, fecha_creacion) VALUES
('ROLE_ADMIN', 'Administrador del sistema con acceso completo', TRUE, CURRENT_TIMESTAMP),
('ROLE_CLIENTE', 'Cliente del sistema con acceso limitado', TRUE, CURRENT_TIMESTAMP);

-- ----------------------------------------------------------------
-- 6.2 INSERTAR USUARIO ADMINISTRADOR POR DEFECTO
-- ----------------------------------------------------------------
INSERT INTO tb_usuario (
    nombre, apellido, email, password, telefono, direccion, 
    estado, fecha_registro, id_rol
) VALUES (
    'Administrador', 
    'Sistema',    'admin@ecomaxtienda.com', 
    '$2a$10$N.zmdr9k7uOCQQVbhv1.C.vOhkqMGEYr96c4KcVW.cShB6Z.DKSDS', -- password: admin123
    '+51 987 654 321', 
    'Oficina Principal EcoMaxTienda, Lima - Perú',
    TRUE, 
    CURRENT_TIMESTAMP, 
    (SELECT id_rol FROM tb_rol WHERE nombre = 'ROLE_ADMIN')
);

-- ----------------------------------------------------------------
-- 6.3 INSERTAR USUARIO CLIENTE DE PRUEBA
-- ----------------------------------------------------------------
INSERT INTO tb_usuario (
    nombre, apellido, email, password, telefono, direccion, 
    estado, fecha_registro, id_rol
) VALUES (
    'Juan Carlos', 
    'Pérez García',    'cliente@test.com', 
    '$2a$10$2QDQXuMRhFXhv1.zBKrYKOQvtCBQ7VC.PXGS0XRXN.Y4kCw8DKSD6', -- password: cliente123
    '+51 999 888 777', 
    'Av. Javier Prado 456, San Isidro - Lima',
    TRUE, 
    CURRENT_TIMESTAMP, 
    (SELECT id_rol FROM tb_rol WHERE nombre = 'ROLE_CLIENTE')
);

-- ----------------------------------------------------------------
-- 6.4 INSERTAR PRODUCTOS DE MUESTRA
-- ----------------------------------------------------------------
INSERT INTO tb_producto (
    nombre, descripcion, precio, categoria, marca, color, 
    eficiencia_energetica, puntuacion_eco, impacto_ambiental, 
    estado, fecha_creacion
) VALUES 
(
    'Panel Solar Eficiente 300W', 
    'Panel solar de alta eficiencia para uso residencial y comercial', 
    450000.00, 
    'Energía Solar', 
    'EcoTech', 
    'Azul oscuro',
    'A+++', 
    4.8, 
    'MUY_BAJO', 
    TRUE, 
    CURRENT_TIMESTAMP
),
(
    'Batería Litio Recargable 100Ah', 
    'Batería de litio de larga duración para sistemas solares', 
    780000.00, 
    'Almacenamiento', 
    'GreenPower', 
    'Negro',
    'A++', 
    4.5, 
    'BAJO', 
    TRUE, 
    CURRENT_TIMESTAMP
),
(
    'Inversor Solar Híbrido 5KW', 
    'Inversor híbrido con conexión a red y respaldo de batería', 
    1200000.00, 
    'Inversores', 
    'SolarMax', 
    'Gris',
    'A++', 
    4.7, 
    'BAJO', 
    TRUE, 
    CURRENT_TIMESTAMP
);

-- ----------------------------------------------------------------
-- 6.5 INSERTAR INVENTARIO INICIAL
-- ----------------------------------------------------------------
INSERT INTO tb_inventario (id_producto, stock, stock_minimo, stock_maximo, ubicacion, estado) VALUES
(1, 50, 10, 100, 'Bodega A - Estante 1', TRUE),
(2, 30, 5, 80, 'Bodega A - Estante 2', TRUE),
(3, 20, 3, 50, 'Bodega B - Estante 1', TRUE);

-- ================================================================
-- 7. CREACIÓN DE VISTAS ÚTILES
-- ================================================================

-- Vista de usuarios con información de rol
CREATE OR REPLACE VIEW vw_usuarios_completo AS
SELECT 
    u.id_usuario,
    u.nombre,
    u.apellido,
    u.email,
    u.telefono,
    u.estado as usuario_activo,
    u.fecha_registro,
    u.ultimo_acceso,
    r.nombre as rol,
    r.descripcion as descripcion_rol
FROM tb_usuario u
INNER JOIN tb_rol r ON u.id_rol = r.id_rol;

-- Vista de productos con inventario
CREATE OR REPLACE VIEW vw_productos_inventario AS
SELECT 
    p.id_producto,
    p.nombre,
    p.precio,
    p.categoria,
    p.marca,
    p.estado as producto_activo,
    i.stock,
    i.stock_minimo,
    i.ubicacion,
    CASE 
        WHEN i.stock <= i.stock_minimo THEN 'STOCK_BAJO'
        WHEN i.stock > (i.stock_minimo * 3) THEN 'STOCK_ALTO'
        ELSE 'STOCK_NORMAL'
    END as estado_stock
FROM tb_producto p
LEFT JOIN tb_inventario i ON p.id_producto = i.id_producto;

-- Vista de resumen de pedidos
CREATE OR REPLACE VIEW vw_resumen_pedidos AS
SELECT 
    p.id_pedido,
    p.numero_pedido,
    u.nombre || ' ' || u.apellido as cliente,
    u.email,
    p.fecha_pedido,
    p.estado,
    p.total,
    p.metodo_pago,
    COUNT(pd.id_detalle) as cantidad_items
FROM tb_pedido p
INNER JOIN tb_usuario u ON p.id_usuario = u.id_usuario
LEFT JOIN tb_pedido_detalle pd ON p.id_pedido = pd.id_pedido
GROUP BY p.id_pedido, u.nombre, u.apellido, u.email;

-- ================================================================
-- 8. FUNCIONES ÚTILES
-- ================================================================

-- Función para generar número de pedido
CREATE OR REPLACE FUNCTION generar_numero_pedido()
RETURNS VARCHAR(20) AS $$
DECLARE
    nuevo_numero VARCHAR(20);
    contador INTEGER;
BEGIN
    -- Obtener el contador del día actual
    SELECT COUNT(*) + 1 INTO contador
    FROM tb_pedido 
    WHERE DATE(fecha_pedido) = CURRENT_DATE;
    
    -- Generar el número de pedido
    nuevo_numero := 'PED-' || TO_CHAR(CURRENT_DATE, 'YYYYMMDD') || '-' || LPAD(contador::TEXT, 3, '0');
    
    RETURN nuevo_numero;
END;
$$ LANGUAGE plpgsql;

-- ================================================================
-- 9. TRIGGERS PARA AUDITORÍA
-- ================================================================

-- Trigger para actualizar fecha_actualizacion en tb_producto
CREATE OR REPLACE FUNCTION actualizar_fecha_producto()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_producto_fecha_actualizacion
    BEFORE UPDATE ON tb_producto
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_producto();

-- Trigger para actualizar fecha_actualizacion en tb_inventario
CREATE OR REPLACE FUNCTION actualizar_fecha_inventario()
RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER tr_inventario_fecha_actualizacion
    BEFORE UPDATE ON tb_inventario
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_inventario();

-- ================================================================
-- 10. CONSULTAS DE VERIFICACIÓN
-- ================================================================

-- Verificar que todas las tablas fueron creadas
SELECT 
    table_name as "Tabla", 
    table_type as "Tipo"
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_name LIKE 'tb_%'
ORDER BY table_name;

-- Verificar datos iniciales
SELECT 'Roles creados:' as verificacion, COUNT(*) as cantidad FROM tb_rol
UNION ALL
SELECT 'Usuarios creados:', COUNT(*) FROM tb_usuario
UNION ALL
SELECT 'Productos creados:', COUNT(*) FROM tb_producto
UNION ALL
SELECT 'Inventario inicial:', COUNT(*) FROM tb_inventario;

-- ================================================================
-- FIN DEL SCRIPT
-- ================================================================

-- Mensaje de confirmación
DO $$ 
BEGIN 
    RAISE NOTICE 'Base de datos EcoMaxTienda creada exitosamente!';
    RAISE NOTICE 'Total de tablas: 8';
    RAISE NOTICE 'Total de vistas: 3';
    RAISE NOTICE 'Total de funciones: 1';
    RAISE NOTICE 'Sistema listo para usar!';
END $$;
