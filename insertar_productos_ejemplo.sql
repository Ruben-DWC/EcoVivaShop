-- Script para insertar productos de ejemplo si no existen
-- Este script verifica si hay productos y solo inserta si la tabla está vacía

INSERT INTO producto (nombre, descripcion, precio, categoria, marca, modelo, color, peso, dimensiones, material, garantia_meses, eficiencia_energetica, impacto_ambiental, puntuacion_eco, imagen_url, estado, fecha_creacion)
SELECT * FROM (
    SELECT 
        'Laptop EcoTech Sustainability Pro' as nombre,
        'Laptop ultra eficiente con procesador de bajo consumo, fabricada con materiales reciclados. Ideal para trabajo profesional y diseño.' as descripcion,
        3299.00 as precio,
        'Electrónicos' as categoria,
        'EcoTech' as marca,
        'Sustainability Pro' as modelo,
        'Gris Espacial' as color,
        1.8 as peso,
        '35.6x23.7x1.9 cm' as dimensiones,
        'Aluminio reciclado' as material,
        24 as garantia_meses,
        'A+++' as eficiencia_energetica,
        'Bajo' as impacto_ambiental,
        9.2 as puntuacion_eco,
        'https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400' as imagen_url,
        true as estado,
        NOW() as fecha_creacion
    
    UNION ALL
    
    SELECT 
        'Smartphone Verde Max 5G',
        'Teléfono inteligente con batería solar integrada y carcasa biodegradable. Tecnología 5G y cámara profesional.',
        1899.50,
        'Electrónicos',
        'VerdeMax',
        '5G Solar',
        'Verde Bosque',
        0.18,
        '15.1x7.5x0.8 cm',
        'Bioplástico',
        18,
        'A++',
        'Bajo',
        8.8,
        'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400',
        true,
        NOW()
    
    UNION ALL
    
    SELECT 
        'Mochila Solar Explorer',
        'Mochila con paneles solares incorporados para cargar dispositivos. Fabricada con botellas plásticas recicladas.',
        289.99,
        'Accesorios',
        'SolarGear',
        'Explorer X1',
        'Negro',
        1.2,
        '45x30x15 cm',
        'PET reciclado',
        12,
        '',
        'Bajo',
        8.5,
        'https://images.unsplash.com/photo-1553062407-98eeb64c6a62?w=400',
        true,
        NOW()
    
    UNION ALL
    
    SELECT 
        'Auriculares Bamboo Wireless',
        'Auriculares inalámbricos con cancelación de ruido, fabricados con bambú sostenible y biocomponentes.',
        199.00,
        'Electrónicos',
        'EcoSound',
        'Bamboo Pro',
        'Marrón Natural',
        0.25,
        '18x16x8 cm',
        'Bambú',
        12,
        '',
        'Bajo',
        7.8,
        'https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400',
        true,
        NOW()
    
    UNION ALL
    
    SELECT 
        'Cargador Solar Portátil 25W',
        'Cargador solar plegable de alta eficiencia. Compatible con laptops, tablets y smartphones.',
        159.90,
        'Electrónicos',
        'SunCharge',
        'PowerFlex 25',
        'Negro',
        0.8,
        '28x20x3 cm',
        'Silicio monocristalino',
        24,
        'A+++',
        'Bajo',
        9.0,
        'https://images.unsplash.com/photo-1593941707882-a5bac6861d75?w=400',
        true,
        NOW()
    
    UNION ALL
    
    SELECT 
        'Botella Térmica Inteligente',
        'Botella térmica con sensor de temperatura y recordatorio de hidratación vía app. Acero inoxidable reciclado.',
        89.50,
        'Hogar',
        'AquaTech',
        'SmartBottle',
        'Azul Océano',
        0.45,
        '26x7x7 cm',
        'Acero inoxidable reciclado',
        24,
        '',
        'Bajo',
        7.5,
        'https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400',
        true,
        NOW()
        
) AS nuevos_productos
WHERE NOT EXISTS (SELECT 1 FROM producto WHERE estado = true);

-- Insertar inventarios para los productos si no existen
INSERT INTO inventario (id_producto, stock, stock_minimo, stock_maximo, fecha_actualizacion)
SELECT 
    p.id_producto,
    CASE 
        WHEN p.nombre LIKE '%Laptop%' THEN 25
        WHEN p.nombre LIKE '%Smartphone%' THEN 40
        WHEN p.nombre LIKE '%Mochila%' THEN 60
        WHEN p.nombre LIKE '%Auriculares%' THEN 50
        WHEN p.nombre LIKE '%Cargador%' THEN 35
        WHEN p.nombre LIKE '%Botella%' THEN 100
        ELSE 30
    END as stock,
    CASE 
        WHEN p.nombre LIKE '%Laptop%' THEN 5
        WHEN p.nombre LIKE '%Smartphone%' THEN 10
        ELSE 8
    END as stock_minimo,
    CASE 
        WHEN p.nombre LIKE '%Laptop%' THEN 100
        WHEN p.nombre LIKE '%Smartphone%' THEN 200
        WHEN p.nombre LIKE '%Mochila%' THEN 300
        WHEN p.nombre LIKE '%Botella%' THEN 500
        ELSE 150
    END as stock_maximo,
    NOW() as fecha_actualizacion
FROM producto p
WHERE p.estado = true 
  AND NOT EXISTS (
      SELECT 1 FROM inventario i WHERE i.id_producto = p.id_producto
  );
