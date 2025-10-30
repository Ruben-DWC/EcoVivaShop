-- Script para crear pedidos de prueba para el usuario Diego
-- Obtener el ID del usuario Diego
DO $$
DECLARE
    usuario_diego_id INTEGER;
    pedido1_id INTEGER;
    pedido2_id INTEGER;
    pedido3_id INTEGER;
    producto1_id INTEGER;
    producto2_id INTEGER;
    producto3_id INTEGER;
BEGIN
    -- Obtener ID del usuario Diego
    SELECT id_usuario INTO usuario_diego_id 
    FROM tb_usuario 
    WHERE email = 'diego123@test.com';
    
    -- Obtener algunos productos para los detalles
    SELECT id_producto INTO producto1_id FROM tb_producto WHERE estado = true LIMIT 1 OFFSET 0;
    SELECT id_producto INTO producto2_id FROM tb_producto WHERE estado = true LIMIT 1 OFFSET 1;
    SELECT id_producto INTO producto3_id FROM tb_producto WHERE estado = true LIMIT 1 OFFSET 2;
    
    -- Pedido 1: ENTREGADO (hace 1 mes)
    INSERT INTO tb_pedido (
        id_usuario, numero_pedido, fecha_pedido, estado, 
        subtotal, impuestos, costo_envio, total,
        direccion_envio, telefono_contacto, metodo_pago,
        fecha_estimada_entrega, fecha_entrega, notas
    ) VALUES (
        usuario_diego_id, 
        'EM' || extract(epoch from now())::bigint || '001',
        now() - interval '30 days',
        'ENTREGADO',
        120.00, 22.80, 15.00, 157.80,
        'Av. Javier Prado 123, San Isidro, Lima',
        '+51 987654321',
        'TARJETA_CREDITO',
        now() - interval '23 days',
        now() - interval '25 days',
        'Entrega rápida por favor'
    ) RETURNING id_pedido INTO pedido1_id;
    
    -- Detalles del Pedido 1
    INSERT INTO tb_pedido_detalle (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
    SELECT pedido1_id, producto1_id, 2, p.precio, 2 * p.precio
    FROM tb_producto p WHERE p.id_producto = producto1_id;
    
    INSERT INTO tb_pedido_detalle (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
    SELECT pedido1_id, producto2_id, 1, p.precio, 1 * p.precio
    FROM tb_producto p WHERE p.id_producto = producto2_id;
    
    -- Pedido 2: ENVIADO (hace 5 días)
    INSERT INTO tb_pedido (
        id_usuario, numero_pedido, fecha_pedido, estado,
        subtotal, impuestos, costo_envio, total,
        direccion_envio, telefono_contacto, metodo_pago,
        fecha_estimada_entrega, numero_seguimiento, transportadora, notas
    ) VALUES (
        usuario_diego_id,
        'EM' || extract(epoch from now())::bigint || '002',
        now() - interval '5 days',
        'ENVIADO',
        85.50, 16.25, 12.00, 113.75,
        'Calle Los Olivos 456, Miraflores, Lima',
        '+51 987654321',
        'TRANSFERENCIA_BANCARIA',
        now() + interval '2 days',
        'OLV123456789PE',
        'Olva Courier',
        'Producto frágil, manejar con cuidado'
    ) RETURNING id_pedido INTO pedido2_id;
    
    -- Detalles del Pedido 2
    INSERT INTO tb_pedido_detalle (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
    SELECT pedido2_id, producto2_id, 1, p.precio, 1 * p.precio
    FROM tb_producto p WHERE p.id_producto = producto2_id;
    
    INSERT INTO tb_pedido_detalle (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
    SELECT pedido2_id, producto3_id, 2, p.precio, 2 * p.precio
    FROM tb_producto p WHERE p.id_producto = producto3_id;
    
    -- Pedido 3: PENDIENTE (hace 2 días)
    INSERT INTO tb_pedido (
        id_usuario, numero_pedido, fecha_pedido, estado,
        subtotal, impuestos, costo_envio, total,
        direccion_envio, telefono_contacto, metodo_pago,
        fecha_estimada_entrega, notas
    ) VALUES (
        usuario_diego_id,
        'EM' || extract(epoch from now())::bigint || '003',
        now() - interval '2 days',
        'PENDIENTE',
        95.00, 18.05, 10.00, 123.05,
        'Jr. Amazonas 789, Cercado de Lima, Lima',
        '+51 987654321',
        'EFECTIVO',
        now() + interval '7 days',
        'Confirmar disponibilidad antes del envío'
    ) RETURNING id_pedido INTO pedido3_id;
    
    -- Detalles del Pedido 3
    INSERT INTO tb_pedido_detalle (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
    SELECT pedido3_id, producto1_id, 1, p.precio, 1 * p.precio
    FROM tb_producto p WHERE p.id_producto = producto1_id;
    
    INSERT INTO tb_pedido_detalle (id_pedido, id_producto, cantidad, precio_unitario, subtotal)
    SELECT pedido3_id, producto3_id, 3, p.precio, 3 * p.precio
    FROM tb_producto p WHERE p.id_producto = producto3_id;
    
    -- Actualizar los totales de los pedidos basándose en los detalles reales
    UPDATE tb_pedido 
    SET subtotal = (
        SELECT COALESCE(SUM(pd.subtotal), 0) 
        FROM tb_pedido_detalle pd 
        WHERE pd.id_pedido = tb_pedido.id_pedido
    ),
    total = (
        SELECT COALESCE(SUM(pd.subtotal), 0) * 1.19 + costo_envio - COALESCE(descuento, 0)
        FROM tb_pedido_detalle pd 
        WHERE pd.id_pedido = tb_pedido.id_pedido
    ),
    impuestos = (
        SELECT COALESCE(SUM(pd.subtotal), 0) * 0.19
        FROM tb_pedido_detalle pd 
        WHERE pd.id_pedido = tb_pedido.id_pedido
    )
    WHERE id_usuario = usuario_diego_id;
    
    RAISE NOTICE 'Se crearon 3 pedidos de prueba para el usuario Diego (ID: %)', usuario_diego_id;
    RAISE NOTICE 'Pedido 1 ID: % (ENTREGADO)', pedido1_id;
    RAISE NOTICE 'Pedido 2 ID: % (ENVIADO)', pedido2_id;
    RAISE NOTICE 'Pedido 3 ID: % (PENDIENTE)', pedido3_id;
END $$;
