-- Verificación rápida de productos en EcoMaxTienda
-- Ejecutar en PostgreSQL

-- 1. Contar productos totales
SELECT 'Total de productos:' as info, COUNT(*) as cantidad FROM tb_producto;

-- 2. Contar productos activos
SELECT 'Productos activos:' as info, COUNT(*) as cantidad FROM tb_producto WHERE activo = true;

-- 3. Mostrar productos con inventario
SELECT 
    p.id_producto,
    p.nombre,
    p.categoria,
    p.precio,
    p.activo,
    i.stock,
    i.stock_maximo
FROM tb_producto p 
LEFT JOIN tb_inventario i ON p.id_producto = i.id_producto 
ORDER BY p.id_producto
LIMIT 10;

-- 4. Verificar categorías disponibles
SELECT categoria, COUNT(*) as productos
FROM tb_producto 
WHERE activo = true
GROUP BY categoria
ORDER BY productos DESC;
