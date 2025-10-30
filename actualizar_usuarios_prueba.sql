-- Script para actualizar usuarios de prueba con nombres completos
-- Esto asegura que los usuarios de prueba muestren nombres correctos en lugar de "Cliente"

-- Actualizar usuario diego123@test.com si existe
UPDATE tb_usuario
SET nombre = 'Diego', apellido = 'Pérez'
WHERE email = 'diego123@test.com' AND (nombre IS NULL OR nombre = '');

-- Actualizar usuario churin123@test.com si existe
UPDATE tb_usuario
SET nombre = 'Diego', apellido = 'Churín'
WHERE email = 'churin123@test.com' AND (nombre IS NULL OR nombre = '');

-- Insertar usuario de prueba adicional si no existe
INSERT INTO tb_usuario (nombre, apellido, email, password, telefono, direccion, activo, email_verificado, rol_id)
SELECT 'Carlos', 'Rodríguez', 'carlos@test.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP',
       '+51 987 123 456', 'Av. Principal 123, Lima, Perú', true, true, r.id
FROM tb_rol r WHERE r.nombre = 'CLIENTE'
AND NOT EXISTS (SELECT 1 FROM tb_usuario WHERE email = 'carlos@test.com');

-- Insertar usuario de prueba adicional si no existe
INSERT INTO tb_usuario (nombre, apellido, email, password, telefono, direccion, activo, email_verificado, rol_id)
SELECT 'Ana', 'Martínez', 'ana@test.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP',
       '+51 987 654 321', 'Jr. Los Olivos 456, Cusco, Perú', true, true, r.id
FROM tb_rol r WHERE r.nombre = 'CLIENTE'
AND NOT EXISTS (SELECT 1 FROM tb_usuario WHERE email = 'ana@test.com');

-- Verificar que todos los usuarios tengan nombre
SELECT id_usuario, nombre, apellido, email, rol_id
FROM tb_usuario
WHERE rol_id = (SELECT id FROM tb_rol WHERE nombre = 'CLIENTE')
ORDER BY fecha_registro DESC
LIMIT 10;