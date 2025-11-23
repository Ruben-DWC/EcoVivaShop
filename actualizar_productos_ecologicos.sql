-- Script para poblar información ecológica completa en productos existentes
-- Actualiza todos los productos con datos ecológicos del archivo productos_eco_60_completos_utf8.csv

-- Actualizar productos con información ecológica completa
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de bambú'
WHERE nombre LIKE '%Camiseta%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Cáñamo orgánico'
WHERE nombre LIKE '%Pantalón%' AND nombre LIKE '%Cáñamo%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Corcho reciclado'
WHERE nombre LIKE '%Zapatillas%' AND nombre LIKE '%Corcho%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Acero inoxidable'
WHERE nombre LIKE '%Botella%' AND nombre LIKE '%Acero%';

UPDATE tb_producto SET
    puntuacion_eco = 7.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'PET reciclado'
WHERE nombre LIKE '%Mochila%' AND nombre LIKE '%PET%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceites esenciales'
WHERE nombre LIKE '%Jabón%' AND nombre LIKE '%Lavanda%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceite de coco'
WHERE nombre LIKE '%Champú%' AND nombre LIKE '%Coco%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú biodegradable'
WHERE nombre LIKE '%Pasta%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 10.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Quinoa orgánica'
WHERE nombre LIKE '%Quinoa%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Miel natural'
WHERE nombre LIKE '%Miel%' AND nombre LIKE '%Abeja%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Café orgánico'
WHERE nombre LIKE '%Café%' AND nombre LIKE '%Orgánico%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceitunas orgánicas'
WHERE nombre LIKE '%Aceite%' AND nombre LIKE '%Oliva%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Arroz integral'
WHERE nombre LIKE '%Arroz%' AND nombre LIKE '%Integral%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Frijoles orgánicos'
WHERE nombre LIKE '%Frijoles%' AND nombre LIKE '%Negros%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Avena orgánica'
WHERE nombre LIKE '%Avena%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Almendras orgánicas'
WHERE nombre LIKE '%Almendras%' AND nombre LIKE '%Orgánicas%';

-- Productos con eficiencia energética
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A++',
    material = 'Plástico reciclado'
WHERE nombre LIKE '%Lámpara%' AND nombre LIKE '%LED%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A+',
    material = 'Silicio policristalino'
WHERE nombre LIKE '%Cargador%' AND nombre LIKE '%Solar%';

UPDATE tb_producto SET
    puntuacion_eco = 10.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A+++',
    material = 'Silicio monocristalino'
WHERE nombre LIKE '%Panel%' AND nombre LIKE '%Solar%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A++',
    material = 'Aluminio reflectante'
WHERE nombre LIKE '%Estufa%' AND nombre LIKE '%Solar%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A+',
    material = 'Plástico ABS'
WHERE nombre LIKE '%Ventilador%' AND nombre LIKE '%Solar%';

-- Productos de limpieza ecológica
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Surfactantes vegetales'
WHERE nombre LIKE '%Detergente%' AND nombre LIKE '%Biodegradable%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceites esenciales'
WHERE nombre LIKE '%Suavizante%' AND nombre LIKE '%Natural%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Tensioactivos vegetales'
WHERE nombre LIKE '%Lavavajillas%' AND nombre LIKE '%Ecológico%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Vinagre blanco'
WHERE nombre LIKE '%Desinfectante%' AND nombre LIKE '%Natural%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Alcohol etílico'
WHERE nombre LIKE '%Limpiador%' AND nombre LIKE '%Vidrios%';

-- Productos de higiene sostenible
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de bambú'
WHERE nombre LIKE '%Papel%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Viscosa biodegradable'
WHERE nombre LIKE '%Toallas%' AND nombre LIKE '%Biodegradables%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Celulosa biodegradable'
WHERE nombre LIKE '%Pañales%' AND nombre LIKE '%Biodegradables%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú biodegradable'
WHERE nombre LIKE '%Cepillo%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de coco'
WHERE nombre LIKE '%Esponja%' AND nombre LIKE '%Coco%';

-- Productos de jardinería orgánica
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de coco'
WHERE nombre LIKE '%Maceta%' AND nombre LIKE '%Coco%';

UPDATE tb_producto SET
    puntuacion_eco = 10.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Semillas orgánicas'
WHERE nombre LIKE '%Semillas%' AND nombre LIKE '%Orgánicas%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Extractos vegetales'
WHERE nombre LIKE '%Fertilizante%' AND nombre LIKE '%Orgánico%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Compost orgánico'
WHERE nombre LIKE '%Tierra%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 7.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Plástico reciclado'
WHERE nombre LIKE '%Regadera%' AND nombre LIKE '%Reciclada%';

-- Más alimentos orgánicos
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Huevos orgánicos'
WHERE nombre LIKE '%Huevos%' AND nombre LIKE '%Orgánicos%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Leche orgánica'
WHERE nombre LIKE '%Yogurt%' AND nombre LIKE '%Orgánico%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Leche de cabra'
WHERE nombre LIKE '%Queso%' AND nombre LIKE '%Cabra%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Almendras orgánicas'
WHERE nombre LIKE '%Leche%' AND nombre LIKE '%Almendra%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Maní orgánico'
WHERE nombre LIKE '%Mantequilla%' AND nombre LIKE '%Maní%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Avena orgánica'
WHERE nombre LIKE '%Granola%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Hojas de té orgánico'
WHERE nombre LIKE '%Té%' AND nombre LIKE '%Verde%';

UPDATE tb_producto SET
    puntuacion_eco = 10.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Manzanas orgánicas'
WHERE nombre LIKE '%Manzanas%' AND nombre LIKE '%Orgánicas%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Plátanos orgánicos'
WHERE nombre LIKE '%Plátanos%' AND nombre LIKE '%Orgánicos%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Naranjas orgánicas'
WHERE nombre LIKE '%Naranjas%' AND nombre LIKE '%Orgánicas%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Limones orgánicos'
WHERE nombre LIKE '%Limones%' AND nombre LIKE '%Orgánicos%';

-- Accesorios ecológicos adicionales
UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Algodón orgánico'
WHERE nombre LIKE '%Bolsa%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú natural'
WHERE nombre LIKE '%Vaso%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú prensado'
WHERE nombre LIKE '%Platos%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú natural'
WHERE nombre LIKE '%Cubiertos%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de bambú'
WHERE nombre LIKE '%Servilletas%' AND nombre LIKE '%Bambú%';

-- Productos de cuidado personal adicionales
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Óxido de zinc'
WHERE nombre LIKE '%Protector%' AND nombre LIKE '%Mineral%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bicarbonato de sodio'
WHERE nombre LIKE '%Desodorante%' AND nombre LIKE '%Natural%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aloe vera orgánico'
WHERE nombre LIKE '%Crema%' AND nombre LIKE '%Facial%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceite de coco'
WHERE nombre LIKE '%Aceite%' AND nombre LIKE '%Coco%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Café orgánico'
WHERE nombre LIKE '%Exfoliante%' AND nombre LIKE '%Café%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Avena molida'
WHERE nombre LIKE '%Shampoo%' AND nombre LIKE '%Seco%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Arcilla verde'
WHERE nombre LIKE '%Mascarilla%' AND nombre LIKE '%Arcilla%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Cera de abeja'
WHERE nombre LIKE '%Bálsamo%' AND nombre LIKE '%Labial%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Agua de lavanda'
WHERE nombre LIKE '%Perfume%' AND nombre LIKE '%Lavanda%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú natural'
WHERE nombre LIKE '%Cepillo%' AND nombre LIKE '%Cabello%';

-- Verificar que se actualizaron los productos
SELECT COUNT(*) as productos_con_info_ecologica FROM tb_producto
WHERE puntuacion_eco IS NOT NULL;

-- Mostrar algunos ejemplos de productos actualizados
SELECT nombre, puntuacion_eco, impacto_ambiental, eficiencia_energetica, material
FROM tb_producto
WHERE puntuacion_eco IS NOT NULL
ORDER BY puntuacion_eco DESC
LIMIT 10;

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Cáñamo orgánico'
WHERE nombre LIKE '%Pantalón%' AND nombre LIKE '%Cáñamo%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Corcho reciclado'
WHERE nombre LIKE '%Zapatillas%' AND nombre LIKE '%Corcho%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Acero inoxidable'
WHERE nombre LIKE '%Botella%' AND nombre LIKE '%Acero%';

UPDATE tb_producto SET
    puntuacion_eco = 7.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'PET reciclado'
WHERE nombre LIKE '%Mochila%' AND nombre LIKE '%PET%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceites esenciales'
WHERE nombre LIKE '%Jabón%' AND nombre LIKE '%Lavanda%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceite de coco'
WHERE nombre LIKE '%Champú%' AND nombre LIKE '%Coco%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú biodegradable'
WHERE nombre LIKE '%Pasta%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 10.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Quinoa orgánica'
WHERE nombre LIKE '%Quinoa%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Miel natural'
WHERE nombre LIKE '%Miel%' AND nombre LIKE '%Abeja%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Café orgánico'
WHERE nombre LIKE '%Café%' AND nombre LIKE '%Orgánico%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceitunas orgánicas'
WHERE nombre LIKE '%Aceite%' AND nombre LIKE '%Oliva%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Arroz integral'
WHERE nombre LIKE '%Arroz%' AND nombre LIKE '%Integral%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Frijoles orgánicos'
WHERE nombre LIKE '%Frijoles%' AND nombre LIKE '%Negros%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Avena orgánica'
WHERE nombre LIKE '%Avena%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Almendras orgánicas'
WHERE nombre LIKE '%Almendras%' AND nombre LIKE '%Orgánicas%';

-- tb_productos con eficiencia energética
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A++',
    material = 'Plástico reciclado'
WHERE nombre LIKE '%Lámpara%' AND nombre LIKE '%LED%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A+',
    material = 'Silicio policristalino'
WHERE nombre LIKE '%Cargador%' AND nombre LIKE '%Solar%';

UPDATE tb_producto SET
    puntuacion_eco = 10.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A+++',
    material = 'Silicio monocristalino'
WHERE nombre LIKE '%Panel%' AND nombre LIKE '%Solar%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A++',
    material = 'Aluminio reflectante'
WHERE nombre LIKE '%Estufa%' AND nombre LIKE '%Solar%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = 'A+',
    material = 'Plástico ABS'
WHERE nombre LIKE '%Ventilador%' AND nombre LIKE '%Solar%';

-- tb_productos de limpieza ecológica
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Surfactantes vegetales'
WHERE nombre LIKE '%Detergente%' AND nombre LIKE '%Biodegradable%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceites esenciales'
WHERE nombre LIKE '%Suavizante%' AND nombre LIKE '%Natural%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Tensioactivos vegetales'
WHERE nombre LIKE '%Lavavajillas%' AND nombre LIKE '%Ecológico%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Vinagre blanco'
WHERE nombre LIKE '%Desinfectante%' AND nombre LIKE '%Natural%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Alcohol etílico'
WHERE nombre LIKE '%Limpiador%' AND nombre LIKE '%Vidrios%';

-- tb_productos de higiene sostenible
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de bambú'
WHERE nombre LIKE '%Papel%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Viscosa biodegradable'
WHERE nombre LIKE '%Toallas%' AND nombre LIKE '%Biodegradables%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Celulosa biodegradable'
WHERE nombre LIKE '%Pañales%' AND nombre LIKE '%Biodegradables%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú biodegradable'
WHERE nombre LIKE '%Cepillo%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de coco'
WHERE nombre LIKE '%Esponja%' AND nombre LIKE '%Coco%';

-- tb_productos de jardinería orgánica
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de coco'
WHERE nombre LIKE '%Maceta%' AND nombre LIKE '%Coco%';

UPDATE tb_producto SET
    puntuacion_eco = 10.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Semillas orgánicas'
WHERE nombre LIKE '%Semillas%' AND nombre LIKE '%Orgánicas%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Extractos vegetales'
WHERE nombre LIKE '%Fertilizante%' AND nombre LIKE '%Orgánico%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Compost orgánico'
WHERE nombre LIKE '%Tierra%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 7.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Plástico reciclado'
WHERE nombre LIKE '%Regadera%' AND nombre LIKE '%Reciclada%';

-- Más alimentos orgánicos
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Huevos orgánicos'
WHERE nombre LIKE '%Huevos%' AND nombre LIKE '%Orgánicos%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Leche orgánica'
WHERE nombre LIKE '%Yogurt%' AND nombre LIKE '%Orgánico%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Leche de cabra'
WHERE nombre LIKE '%Queso%' AND nombre LIKE '%Cabra%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Almendras orgánicas'
WHERE nombre LIKE '%Leche%' AND nombre LIKE '%Almendra%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Maní orgánico'
WHERE nombre LIKE '%Mantequilla%' AND nombre LIKE '%Maní%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Avena orgánica'
WHERE nombre LIKE '%Granola%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Hojas de té orgánico'
WHERE nombre LIKE '%Té%' AND nombre LIKE '%Verde%';

UPDATE tb_producto SET
    puntuacion_eco = 10.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Manzanas orgánicas'
WHERE nombre LIKE '%Manzanas%' AND nombre LIKE '%Orgánicas%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Plátanos orgánicos'
WHERE nombre LIKE '%Plátanos%' AND nombre LIKE '%Orgánicos%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Naranjas orgánicas'
WHERE nombre LIKE '%Naranjas%' AND nombre LIKE '%Orgánicas%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Limones orgánicos'
WHERE nombre LIKE '%Limones%' AND nombre LIKE '%Orgánicos%';

-- Accesorios ecológicos adicionales
UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Algodón orgánico'
WHERE nombre LIKE '%Bolsa%' AND nombre LIKE '%Orgánica%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú natural'
WHERE nombre LIKE '%Vaso%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú prensado'
WHERE nombre LIKE '%Platos%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú natural'
WHERE nombre LIKE '%Cubiertos%' AND nombre LIKE '%Bambú%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Fibra de bambú'
WHERE nombre LIKE '%Servilletas%' AND nombre LIKE '%Bambú%';

-- tb_productos de cuidado personal adicionales
UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Óxido de zinc'
WHERE nombre LIKE '%Protector%' AND nombre LIKE '%Mineral%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bicarbonato de sodio'
WHERE nombre LIKE '%Desodorante%' AND nombre LIKE '%Natural%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aloe vera orgánico'
WHERE nombre LIKE '%Crema%' AND nombre LIKE '%Facial%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Aceite de coco'
WHERE nombre LIKE '%Aceite%' AND nombre LIKE '%Coco%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Café orgánico'
WHERE nombre LIKE '%Exfoliante%' AND nombre LIKE '%Café%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Avena molida'
WHERE nombre LIKE '%Shampoo%' AND nombre LIKE '%Seco%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Arcilla verde'
WHERE nombre LIKE '%Mascarilla%' AND nombre LIKE '%Arcilla%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Cera de abeja'
WHERE nombre LIKE '%Bálsamo%' AND nombre LIKE '%Labial%';

UPDATE tb_producto SET
    puntuacion_eco = 9.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Agua de lavanda'
WHERE nombre LIKE '%Perfume%' AND nombre LIKE '%Lavanda%';

UPDATE tb_producto SET
    puntuacion_eco = 8.0,
    impacto_ambiental = 'Bajo',
    eficiencia_energetica = NULL,
    material = 'Bambú natural'
WHERE nombre LIKE '%Cepillo%' AND nombre LIKE '%Cabello%';

-- Verificar que se actualizaron los tb_productos
SELECT COUNT(*) as tb_productos_con_info_ecologica FROM tb_producto
WHERE puntuacion_eco IS NOT NULL;

-- Mostrar algunos ejemplos de tb_productos actualizados
SELECT nombre, puntuacion_eco, impacto_ambiental, eficiencia_energetica, material
FROM tb_producto
WHERE puntuacion_eco IS NOT NULL
ORDER BY puntuacion_eco DESC
LIMIT 10;
