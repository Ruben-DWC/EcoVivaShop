-- =====================================================
-- SCRIPT DE MIGRACIÓN DE DATOS
-- Copiar datos de eco_maxtienda a ecovivashop_db
-- =====================================================

-- Conectar a ecovivashop_db
\c ecovivashop_db;

-- Migrar datos de productos (con verificación de existencia)
INSERT INTO producto (
    nombre, descripcion, precio, categoria, stock, imagen_url, estado, 
    fecha_creacion, peso, dimensiones, marca, codigo_producto, 
    descuento, tags, calificacion_promedio, numero_reviews
)
SELECT 
    p.nombre, 
    p.descripcion, 
    p.precio, 
    p.categoria, 
    COALESCE(p.stock, 0), 
    p.imagen_url, 
    COALESCE(p.estado, true), 
    COALESCE(p.fecha_creacion, CURRENT_TIMESTAMP),
    p.peso,
    p.dimensiones,
    p.marca,
    p.codigo_producto,
    COALESCE(p.descuento, 0.00),
    p.tags,
    COALESCE(p.calificacion_promedio, 0.00),
    COALESCE(p.numero_reviews, 0)
FROM dblink('host=localhost dbname=eco_maxtienda user=postgres password=tu_contraseña',
    'SELECT nombre, descripcion, precio, categoria, stock, imagen_url, estado, 
            fecha_creacion, peso, dimensiones, marca, codigo_producto, 
            descuento, tags, calificacion_promedio, numero_reviews 
     FROM producto WHERE estado = true') 
AS p(nombre VARCHAR, descripcion TEXT, precio DECIMAL, categoria VARCHAR, 
      stock INTEGER, imagen_url VARCHAR, estado BOOLEAN, fecha_creacion TIMESTAMP,
      peso DECIMAL, dimensiones VARCHAR, marca VARCHAR, codigo_producto VARCHAR,
      descuento DECIMAL, tags TEXT, calificacion_promedio DECIMAL, numero_reviews INTEGER)
ON CONFLICT (codigo_producto) DO NOTHING;

-- Migrar inventario (si existe)
INSERT INTO inventario (id_producto, stock_actual, stock_minimo, stock_maximo, fecha_actualizacion)
SELECT 
    p_new.id_producto,
    COALESCE(i.stock_actual, p_new.stock),
    COALESCE(i.stock_minimo, 10),
    COALESCE(i.stock_maximo, 1000),
    CURRENT_TIMESTAMP
FROM producto p_new
LEFT JOIN dblink('host=localhost dbname=eco_maxtienda user=postgres password=tu_contraseña',
    'SELECT p.codigo_producto, i.stock_actual, i.stock_minimo, i.stock_maximo
     FROM producto p
     LEFT JOIN inventario i ON p.id_producto = i.id_producto
     WHERE p.estado = true') 
AS i(codigo_producto VARCHAR, stock_actual INTEGER, stock_minimo INTEGER, stock_maximo INTEGER)
ON p_new.codigo_producto = i.codigo_producto;

-- Migrar usuarios (excluyendo admin que ya creamos)
INSERT INTO usuario (nombre, apellido, email, password, telefono, direccion, estado, fecha_registro, id_rol)
SELECT 
    u.nombre,
    u.apellido,
    u.email,
    u.password,
    u.telefono,
    u.direccion,
    COALESCE(u.estado, true),
    COALESCE(u.fecha_registro, CURRENT_TIMESTAMP),
    CASE 
        WHEN r_old.nombre = 'ROLE_ADMIN' THEN r_new_admin.id_rol
        WHEN r_old.nombre = 'ROLE_CLIENTE' THEN r_new_cliente.id_rol
        ELSE r_new_cliente.id_rol
    END
FROM dblink('host=localhost dbname=eco_maxtienda user=postgres password=tu_contraseña',
    'SELECT u.nombre, u.apellido, u.email, u.password, u.telefono, u.direccion, 
            u.estado, u.fecha_registro, r.nombre as rol_nombre
     FROM usuario u
     LEFT JOIN rol r ON u.id_rol = r.id_rol
     WHERE u.email != ''admin@ecovivashop.com''') 
AS u(nombre VARCHAR, apellido VARCHAR, email VARCHAR, password VARCHAR, 
      telefono VARCHAR, direccion TEXT, estado BOOLEAN, fecha_registro TIMESTAMP, rol_nombre VARCHAR)
JOIN rol r_new_admin ON r_new_admin.nombre = 'ROLE_ADMIN'
JOIN rol r_new_cliente ON r_new_cliente.nombre = 'ROLE_CLIENTE'
LEFT JOIN dblink('host=localhost dbname=eco_maxtienda user=postgres password=tu_contraseña',
    'SELECT nombre FROM rol') AS r_old(nombre VARCHAR) ON r_old.nombre = u.rol_nombre
ON CONFLICT (email) DO NOTHING;

-- Actualizar secuencias para evitar conflictos
SELECT setval('producto_id_producto_seq', (SELECT MAX(id_producto) FROM producto) + 1);
SELECT setval('usuario_id_usuario_seq', (SELECT MAX(id_usuario) FROM usuario) + 1);
SELECT setval('rol_id_rol_seq', (SELECT MAX(id_rol) FROM rol) + 1);

-- Mostrar resumen de migración
SELECT 
    'Productos migrados: ' || COUNT(*) as resumen
FROM producto
UNION ALL
SELECT 
    'Usuarios migrados: ' || COUNT(*) as resumen  
FROM usuario
UNION ALL
SELECT
    'Roles creados: ' || COUNT(*) as resumen
FROM rol;

COMMIT;
