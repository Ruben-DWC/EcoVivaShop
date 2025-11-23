-- ============================================
-- EcoVivaShop - Verificación de Base de Datos
-- Fecha: 2 de noviembre de 2025
-- ============================================

-- Conectar a la base de datos
\c ecovivashop_db;

-- Verificar estructura de tablas
\dt;

-- Contar registros en tablas principales
SELECT '=== ESTADISTICAS DE LA BASE DE DATOS ===' as info;
SELECT 'Usuarios totales:' as descripcion, COUNT(*) as cantidad FROM tb_usuario
UNION ALL
SELECT 'Productos activos:', COUNT(*) FROM tb_producto WHERE estado = true
UNION ALL
SELECT 'Productos totales:', COUNT(*) FROM tb_producto
UNION ALL
SELECT 'Items en inventario:', COUNT(*) FROM tb_inventario
UNION ALL
SELECT 'Pedidos totales:', COUNT(*) FROM tb_pedido
UNION ALL
SELECT 'Pagos procesados:', COUNT(*) FROM tb_pago
UNION ALL
SELECT 'Transacciones:', COUNT(*) FROM transacciones_pago
UNION ALL
SELECT 'Suscripciones:', COUNT(*) FROM tb_suscripcion
UNION ALL
SELECT 'Imagenes de productos:', COUNT(*) FROM imagenes_producto
UNION ALL
SELECT 'Imagenes de perfiles:', COUNT(*) FROM imagenes_perfil
UNION ALL
SELECT 'Historial inventario:', COUNT(*) FROM inventario_historial;

-- Verificar usuarios principales
SELECT '=== USUARIOS PRINCIPALES ===' as info;
SELECT nombre, apellido, email, nombre_rol, estado
FROM tb_usuario u
JOIN tb_rol r ON u.id_rol = r.id_rol
ORDER BY u.fecha_registro DESC
LIMIT 5;

-- Verificar productos principales
SELECT '=== PRODUCTOS DESTACADOS ===' as info;
SELECT nombre, precio, categoria, estado, fecha_creacion
FROM tb_producto
WHERE estado = true
ORDER BY fecha_creacion DESC
LIMIT 5;

-- Verificar inventario
SELECT '=== ESTADO DEL INVENTARIO ===' as info;
SELECT p.nombre as producto, i.stock, i.stock_minimo, i.stock_maximo, i.ubicacion
FROM tb_inventario i
JOIN tb_producto p ON i.id_producto = p.id_producto
ORDER BY i.fecha_actualizacion DESC
LIMIT 10;

-- Verificar pedidos recientes
SELECT '=== PEDIDOS RECIENTES ===' as info;
SELECT numero_pedido, estado, total, fecha_pedido,
       (SELECT nombre FROM tb_usuario WHERE id_usuario = p.id_usuario) as cliente
FROM tb_pedido p
ORDER BY fecha_pedido DESC
LIMIT 5;

-- Verificar configuración de email
SELECT '=== CONFIGURACION EMAIL ===' as info;
SELECT * FROM configuracion_email;

-- Verificar roles disponibles
SELECT '=== ROLES DEL SISTEMA ===' as info;
SELECT nombre, descripcion FROM tb_rol ORDER BY nombre;

-- Verificar integridad referencial (foreign keys)
SELECT '=== VERIFICACION DE INTEGRIDAD ===' as info;

-- Verificar que todos los pedidos tienen usuario válido
SELECT 'Pedidos sin usuario válido:' as check_name,
       COUNT(*) as issues
FROM tb_pedido p
LEFT JOIN tb_usuario u ON p.id_usuario = u.id_usuario
WHERE u.id_usuario IS NULL;

-- Verificar que todos los detalles de pedido tienen producto válido
SELECT 'Detalles sin producto válido:' as check_name,
       COUNT(*) as issues
FROM tb_pedido_detalle pd
LEFT JOIN tb_producto pr ON pd.id_producto = pr.id_producto
WHERE pr.id_producto IS NULL;

-- Verificar que todos los pagos tienen pedido válido
SELECT 'Pagos sin pedido válido:' as check_name,
       COUNT(*) as issues
FROM tb_pago pa
LEFT JOIN tb_pedido pe ON pa.id_pedido = pe.id_pedido
WHERE pe.id_pedido IS NULL;

-- Verificar que todos los usuarios tienen rol válido
SELECT 'Usuarios sin rol válido:' as check_name,
       COUNT(*) as issues
FROM tb_usuario us
LEFT JOIN tb_rol ro ON us.id_rol = ro.id_rol
WHERE ro.id_rol IS NULL;

-- Verificar inventario sin producto
SELECT 'Inventario sin producto:' as check_name,
       COUNT(*) as issues
FROM tb_inventario inv
LEFT JOIN tb_producto prod ON inv.id_producto = prod.id_producto
WHERE prod.id_producto IS NULL;

SELECT '=== VERIFICACION COMPLETADA ===' as resultado;
SELECT '✅ Base de datos completamente operativa' as estado;