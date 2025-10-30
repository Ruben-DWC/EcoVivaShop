-- Verificar y actualizar el usuario diego123@test.com
-- Primero verificar el estado actual
SELECT id_usuario, nombre, apellido, email, provider, provider_id, id_rol
FROM tb_usuario
WHERE email = 'diego123@test.com';

-- Actualizar el usuario con nombre completo si está vacío
UPDATE tb_usuario
SET nombre = 'Diego', apellido = 'Pérez'
WHERE email = 'diego123@test.com'
  AND (nombre IS NULL OR nombre = '' OR apellido IS NULL OR apellido = '');

-- Verificar que se actualizó correctamente
SELECT id_usuario, nombre, apellido, email, provider, provider_id, id_rol
FROM tb_usuario
WHERE email = 'diego123@test.com';