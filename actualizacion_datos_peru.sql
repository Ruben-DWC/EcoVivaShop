-- ============================================================================
-- SCRIPT DE ACTUALIZACIÓN PARA DATOS ESPECÍFICOS DE PERÚ
-- ============================================================================
-- Fecha: Junio 2025
-- Propósito: Actualizar datos de usuarios y productos para el mercado peruano
-- ============================================================================

-- ACTUALIZACIÓN DE DATOS DE USUARIOS PARA PERÚ
-- ============================================================================

-- Actualizar números de teléfono a formato peruano
UPDATE tb_usuario 
SET telefono = CASE 
    WHEN id = 1 THEN '987654321'  -- Usuario de Prueba
    WHEN id = 2 THEN '+51 999 123 456'  -- Administrador
    WHEN id = 3 THEN '+51 987 654 321'  -- Juan Carlos
    ELSE telefono
END
WHERE telefono IS NOT NULL;

-- Actualizar direcciones a ubicaciones peruanas
UPDATE tb_usuario 
SET direccion = CASE 
    WHEN id = 1 THEN NULL  -- Mantener NULL para usuario de prueba
    WHEN id = 2 THEN 'Av. Javier Prado Este 4200, San Isidro, Lima'  -- Administrador
    WHEN id = 3 THEN 'Av. El Sol 315, Cusco, Perú'  -- Juan Carlos
    ELSE direccion
END
WHERE id IN (1, 2, 3);

-- Actualizar emails a dominios más apropiados para el mercado peruano
UPDATE tb_usuario 
SET email = CASE 
    WHEN email = 'cliente@test.com' THEN 'cliente@gmail.com'
    WHEN email = 'admin@ecovivashop.com' THEN 'admin@ecovivashop.pe'
    ELSE email
END
WHERE email IN ('cliente@test.com', 'admin@ecovivashop.com');

-- ACTUALIZACIÓN DE PRECIOS DE PRODUCTOS
-- ============================================================================

-- Convertir precios de pesos colombianos a soles peruanos (aproximadamente 1 COP = 0.0011 PEN)
-- También incluir precios en dólares como alternativa

-- Productos tecnológicos - Precios en soles peruanos (PEN)
UPDATE tb_producto 
SET precio = CASE 
    WHEN nombre LIKE '%Samsung Galaxy%' THEN 3299.00  -- ~$890 USD
    WHEN nombre LIKE '%HP Pavilion%' THEN 2799.00     -- ~$755 USD  
    WHEN nombre LIKE '%Sony WH-1000XM4%' THEN 999.00  -- ~$270 USD
    WHEN nombre LIKE '%Apple Watch%' THEN 1599.00     -- ~$430 USD
    WHEN nombre LIKE '%iPad Air%' THEN 2199.00        -- ~$595 USD
    ELSE precio
END
WHERE categoria IN ('Electrónicos', 'Computadoras', 'Audio', 'Wearables', 'Tablets');

-- Actualizar descripciones para incluir información de precios
UPDATE tb_producto 
SET descripcion = CASE 
    WHEN nombre LIKE '%Samsung Galaxy%' THEN 'Teléfono inteligente de última generación con cámara de 108MP. Precio en soles peruanos.'
    WHEN nombre LIKE '%HP Pavilion%' THEN 'Laptop para trabajo y entretenimiento con procesador Intel i7. Precio competitivo en el mercado peruano.'
    WHEN nombre LIKE '%Sony WH-1000XM4%' THEN 'Auriculares inalámbricos con cancelación de ruido. Disponible en Lima y provincias.'
    WHEN nombre LIKE '%Apple Watch%' THEN 'Reloj inteligente con monitoreo de salud avanzado. Garantía oficial en Perú.'
    WHEN nombre LIKE '%iPad Air%' THEN 'Tablet de alto rendimiento para creatividad y productividad. Soporte técnico en español.'
    ELSE descripcion
END
WHERE categoria IN ('Electrónicos', 'Computadoras', 'Audio', 'Wearables', 'Tablets');

-- INSERTAR PRODUCTOS ADICIONALES ESPECÍFICOS PARA EL MERCADO PERUANO
-- ============================================================================

-- Productos con precios en dólares para comparación
INSERT INTO tb_producto (nombre, descripcion, precio, categoria, marca, activo, usuario_creador_id) VALUES 
('Smartphone Xiaomi Redmi Note 12', 'Teléfono económico con excelente relación calidad-precio. Precio: $199 USD', 739.00, 'Electrónicos', 'Xiaomi', true,
 (SELECT id FROM tb_usuario WHERE email LIKE '%admin%' LIMIT 1)),
('Laptop Lenovo IdeaPad 3', 'Laptop ideal para estudiantes y trabajo remoto. Precio: $449 USD', 1669.00, 'Computadoras', 'Lenovo', true,
 (SELECT id FROM tb_usuario WHERE email LIKE '%admin%' LIMIT 1)),
('Auriculares JBL Tune 500BT', 'Auriculares inalámbricos económicos con buena calidad de sonido. Precio: $35 USD', 130.00, 'Audio', 'JBL', true,
 (SELECT id FROM tb_usuario WHERE email LIKE '%admin%' LIMIT 1)),
('Smartwatch Amazfit Bip 3', 'Reloj inteligente con GPS y batería de larga duración. Precio: $59 USD', 219.00, 'Wearables', 'Amazfit', true,
 (SELECT id FROM tb_usuario WHERE email LIKE '%admin%' LIMIT 1)),
('Tablet Samsung Galaxy Tab A8', 'Tablet Android para entretenimiento y tareas básicas. Precio: $179 USD', 665.00, 'Tablets', 'Samsung', true,
 (SELECT id FROM tb_usuario WHERE email LIKE '%admin%' LIMIT 1))
ON CONFLICT DO NOTHING;

-- ACTUALIZAR INVENTARIO PARA NUEVOS PRODUCTOS
-- ============================================================================
INSERT INTO tb_inventario (producto_id, cantidad_actual, cantidad_minima, cantidad_maxima, ubicacion) 
SELECT p.id, 
    CASE 
        WHEN p.precio < 500 THEN 100  -- Más stock para productos económicos
        WHEN p.precio < 1500 THEN 75  -- Stock medio para productos de gama media
        ELSE 25                       -- Menos stock para productos premium
    END as cantidad_actual,
    10 as cantidad_minima,
    CASE 
        WHEN p.precio < 500 THEN 300
        WHEN p.precio < 1500 THEN 150
        ELSE 50
    END as cantidad_maxima,
    'Almacén Lima Centro' as ubicacion
FROM tb_producto p
WHERE NOT EXISTS (SELECT 1 FROM tb_inventario i WHERE i.producto_id = p.id)
AND p.fecha_creacion > CURRENT_DATE - INTERVAL '1 day';

-- ACTUALIZAR DATOS DE EJEMPLO PARA REFLEJAR EL MERCADO PERUANO
-- ============================================================================

-- Actualizar totales de pedidos para reflejar nuevos precios
UPDATE tb_pedido 
SET total = (
    SELECT SUM(pd.subtotal) 
    FROM tb_pedido_detalle pd 
    WHERE pd.pedido_id = tb_pedido.id
),
direccion_entrega = CASE 
    WHEN direccion_entrega LIKE '%Lima%' THEN 'Av. El Sol 315, Cusco, Perú'
    ELSE 'Av. Javier Prado Este 4200, San Isidro, Lima'
END,
telefono_contacto = CASE 
    WHEN telefono_contacto = '987654321' THEN '+51 987 654 321'
    ELSE telefono_contacto
END;

-- Actualizar detalles de pedido con nuevos precios
UPDATE tb_pedido_detalle 
SET precio_unitario = p.precio,
    subtotal = cantidad * p.precio
FROM tb_producto p 
WHERE tb_pedido_detalle.producto_id = p.id;

-- Actualizar pagos para reflejar nuevos totales
UPDATE tb_pago 
SET monto = (
    SELECT total 
    FROM tb_pedido 
    WHERE tb_pedido.id = tb_pago.pedido_id
),
referencia_transaccion = CONCAT('PER_', DATE_PART('year', CURRENT_DATE), '_', 
                               LPAD(id::text, 6, '0'));

-- INSERTAR USUARIOS ADICIONALES CON DATOS PERUANOS
-- ============================================================================
INSERT INTO tb_usuario (nombre, apellido, email, password, telefono, direccion, activo, email_verificado, rol_id) VALUES 
('Carlos', 'Mendoza', 'carlos.mendoza@gmail.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP', 
 '+51 998 765 432', 'Jr. Unión 456, Arequipa, Perú', true, true,
 (SELECT id FROM tb_rol WHERE nombre = 'CLIENTE')),
('Ana', 'Quispe', 'ana.quispe@hotmail.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP', 
 '+51 987 123 456', 'Av. Grau 789, Trujillo, Perú', true, true,
 (SELECT id FROM tb_rol WHERE nombre = 'CLIENTE')),
('Miguel', 'Torres', 'miguel.torres@outlook.com', '$2a$10$8K1p/a0dL2LkRk2Q3n4X5u8VjR7jS9tA2cD6fE7gH8iJ9kL0mN1oP', 
 '+51 999 888 777', 'Av. San Martín 321, Chiclayo, Perú', true, true,
 (SELECT id FROM tb_rol WHERE nombre = 'CLIENTE'))
ON CONFLICT (email) DO NOTHING;

-- COMENTARIOS INFORMATIVOS
-- ============================================================================
/*
RESUMEN DE CAMBIOS APLICADOS:

1. USUARIOS:
   - Números de teléfono actualizados a formato peruano (+51 XXX XXX XXX)
   - Direcciones cambiadas a ciudades peruanas (Lima, Cusco, Arequipa, etc.)
   - Emails actualizados con dominios más apropiados

2. PRODUCTOS:
   - Precios convertidos a soles peruanos (PEN)
   - Rangos de precios ajustados al mercado peruano
   - Productos adicionales con precios en dólares para comparación
   - Descripciones actualizadas con información relevante para Perú

3. INVENTARIO:
   - Stock ajustado según categoría de precio
   - Ubicación de almacén actualizada a "Almacén Lima Centro"

4. PEDIDOS Y PAGOS:
   - Totales recalculados con nuevos precios
   - Referencias de transacción con prefijo "PER_"
   - Direcciones de entrega actualizadas

NOTA: Los precios están en soles peruanos (PEN) aproximadamente:
- 1 USD = 3.71 PEN (tipo de cambio referencial)
- Productos económicos: 130-739 PEN
- Productos gama media: 999-1669 PEN  
- Productos premium: 2199-3299 PEN
*/
