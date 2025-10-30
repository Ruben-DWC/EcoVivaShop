-- =====================================================
-- SCRIPT PARA LIMPIAR USUARIOS Y PREPARAR TEST GMAIL
-- =====================================================
-- Fecha: 12 de Junio 2025
-- Propósito: Mantener solo admin y maria.test para probar con cuenta real de Gmail
-- =====================================================

-- Mostrar usuarios actuales
SELECT 'USUARIOS ANTES DE LIMPIAR:' as info;
SELECT id_usuario, nombre, apellido, email, 
       CASE WHEN id_rol = 1 THEN 'ADMIN' ELSE 'CLIENTE' END as rol
FROM tb_usuario 
ORDER BY id_usuario;

-- =====================================================
-- ELIMINAR USUARIOS EXCEPTO LOS ESPECIFICADOS
-- =====================================================

-- Eliminar todos los usuarios EXCEPTO admin@ecomaxtienda.com y maria.test@example.com
DELETE FROM tb_usuario 
WHERE email NOT IN (
    'admin@ecomaxtienda.com',
    'maria.test@example.com'
);

-- Mostrar cuántos usuarios se eliminaron
SELECT ROW_COUNT() as usuarios_eliminados;

-- =====================================================
-- VERIFICAR USUARIOS RESTANTES
-- =====================================================

SELECT 'USUARIOS DESPUÉS DE LIMPIAR:' as info;
SELECT id_usuario, nombre, apellido, email, 
       CASE WHEN id_rol = 1 THEN 'ADMIN' ELSE 'CLIENTE' END as rol,
       fecha_registro
FROM tb_usuario 
ORDER BY id_usuario;

-- =====================================================
-- RESETEAR SECUENCIA DE IDs (OPCIONAL)
-- =====================================================

-- Obtener el próximo ID disponible
SELECT 'PRÓXIMO ID DISPONIBLE:' as info;
SELECT COALESCE(MAX(id_usuario), 0) + 1 as proximo_id FROM tb_usuario;

-- Resetear la secuencia para que el próximo usuario tenga un ID limpio
-- (Esto es opcional, pero mantiene los IDs ordenados)
SELECT setval('tb_usuario_id_usuario_seq', COALESCE(MAX(id_usuario), 0)) FROM tb_usuario;

-- =====================================================
-- VERIFICACIÓN FINAL
-- =====================================================

SELECT 'RESUMEN FINAL:' as info;
SELECT 
    COUNT(*) as total_usuarios,
    COUNT(CASE WHEN id_rol = 1 THEN 1 END) as admins,
    COUNT(CASE WHEN id_rol = 2 THEN 1 END) as clientes
FROM tb_usuario;

SELECT 'USUARIOS FINALES:' as info;
SELECT 
    id_usuario,
    nombre || ' ' || apellido as nombre_completo,
    email,
    CASE WHEN id_rol = 1 THEN 'ADMIN' ELSE 'CLIENTE' END as rol,
    fecha_registro
FROM tb_usuario 
ORDER BY id_usuario;

-- =====================================================
-- LISTO PARA PROBAR CON subiendovideos903@gmail.com
-- =====================================================

SELECT '¡BASE DE DATOS LIMPIA!' as status;
SELECT 'Ahora puedes registrar: subiendovideos903@gmail.com' as siguiente_paso;
