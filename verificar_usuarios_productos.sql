-- Script para verificar y crear usuario administrador y productos de prueba
-- EcoMaxTienda Database Setup

-- 1. VERIFICAR USUARIOS EXISTENTES
SELECT id_usuario, nombre, email, rol 
FROM tb_usuario 
ORDER BY id_usuario;

-- 2. CREAR USUARIO ADMINISTRADOR SI NO EXISTE
-- Nota: La contraseña será hasheada por Spring Security
-- Contraseña sin hashear: admin123
INSERT INTO tb_usuario (nombre, email, contrasena, rol, fecha_registro, activo) 
SELECT 'Administrador', 'admin@ecomaxtienda.com', 
       '$2a$10$N.zmNIkG.Sj4W/H8R3G./.mhGhgGpw8jGqCf7k7LJ8/xc8.Q8Q8N2', -- admin123
       'ADMIN', CURRENT_TIMESTAMP, true
WHERE NOT EXISTS (
    SELECT 1 FROM tb_usuario WHERE email = 'admin@ecomaxtienda.com'
);

-- 3. VERIFICAR PRODUCTOS EXISTENTES
SELECT p.id_producto, p.nombre, p.categoria, p.precio, p.descripcion,
       i.stock, i.stock_maximo, p.imagen_url
FROM tb_producto p 
LEFT JOIN tb_inventario i ON p.id_producto = i.id_producto 
ORDER BY p.id_producto;

-- 4. VERIFICAR CONTEO DE PRODUCTOS POR CATEGORÍA
SELECT categoria, COUNT(*) as total_productos
FROM tb_producto 
GROUP BY categoria
ORDER BY total_productos DESC;

-- 5. INSERTAR PRODUCTOS ADICIONALES SI HAY MUY POCOS
INSERT INTO tb_producto (nombre, descripcion, precio, categoria, marca, modelo, puntuacion_eco, fecha_creacion, activo)
SELECT 'Bombilla LED Eco 15W', 'Bombilla LED de bajo consumo, 15W equivalente a 100W incandescente', 25.99, 'Iluminación', 'EcoLight', 'LED-15W', 9.2, CURRENT_TIMESTAMP, true
WHERE (SELECT COUNT(*) FROM tb_producto) < 5

UNION ALL

SELECT 'Panel Solar 300W', 'Panel solar fotovoltaico de 300W para uso residencial', 899.99, 'Energía Solar', 'SolarMax', 'PS-300W', 9.8, CURRENT_TIMESTAMP, true
WHERE (SELECT COUNT(*) FROM tb_producto) < 5

UNION ALL

SELECT 'Filtro de Agua Ecológico', 'Sistema de filtrado de agua con carbón activado natural', 149.99, 'Filtración', 'AquaPure', 'ECO-Filter-Pro', 8.7, CURRENT_TIMESTAMP, true
WHERE (SELECT COUNT(*) FROM tb_producto) < 5

UNION ALL

SELECT 'Bolsa Reutilizable Orgánica', 'Bolsa de compras hecha con algodón orgánico certificado', 12.99, 'Textiles', 'GreenBag', 'ORG-001', 8.9, CURRENT_TIMESTAMP, true
WHERE (SELECT COUNT(*) FROM tb_producto) < 5;

-- 6. INSERTAR INVENTARIO PARA NUEVOS PRODUCTOS
INSERT INTO tb_inventario (id_producto, stock, stock_minimo, stock_maximo, fecha_actualizacion)
SELECT p.id_producto, 
       CASE p.categoria 
           WHEN 'Iluminación' THEN 50
           WHEN 'Energía Solar' THEN 10
           WHEN 'Filtración' THEN 25
           WHEN 'Textiles' THEN 100
           ELSE 20
       END as stock,
       5 as stock_minimo,
       CASE p.categoria 
           WHEN 'Iluminación' THEN 100
           WHEN 'Energía Solar' THEN 20
           WHEN 'Filtración' THEN 50
           WHEN 'Textiles' THEN 200
           ELSE 50
       END as stock_maximo,
       CURRENT_TIMESTAMP
FROM tb_producto p
WHERE NOT EXISTS (
    SELECT 1 FROM tb_inventario i WHERE i.id_producto = p.id_producto
);

-- 7. VERIFICACIÓN FINAL
SELECT 'VERIFICACIÓN FINAL' as status;

SELECT 'Total Usuarios:' as info, COUNT(*) as cantidad FROM tb_usuario;
SELECT 'Total Productos:' as info, COUNT(*) as cantidad FROM tb_producto;
SELECT 'Total Inventarios:' as info, COUNT(*) as cantidad FROM tb_inventario;

SELECT 'Usuarios Admin:' as info, COUNT(*) as cantidad 
FROM tb_usuario WHERE rol = 'ADMIN';

-- 8. MOSTRAR PRODUCTOS COMPLETOS CON INVENTARIO
SELECT p.id_producto, p.nombre, p.categoria, p.precio, 
       i.stock, i.stock_maximo, p.activo,
       CASE WHEN p.imagen_url IS NOT NULL THEN 'SÍ' ELSE 'NO' END as tiene_imagen
FROM tb_producto p 
LEFT JOIN tb_inventario i ON p.id_producto = i.id_producto 
WHERE p.activo = true
ORDER BY p.categoria, p.nombre;
