Set objExcel = CreateObject("Excel.Application")
objExcel.Visible = False
objExcel.DisplayAlerts = False

Set objWorkbook = objExcel.Workbooks.Add()
Set objWorksheet = objWorkbook.Worksheets(1)
objWorksheet.Name = "Productos"

' Encabezados
objWorksheet.Cells(1, 1).Value = "nombre"
objWorksheet.Cells(1, 2).Value = "descripcion"
objWorksheet.Cells(1, 3).Value = "precio"
objWorksheet.Cells(1, 4).Value = "categoria"
objWorksheet.Cells(1, 5).Value = "marca"
objWorksheet.Cells(1, 6).Value = "modelo"
objWorksheet.Cells(1, 7).Value = "color"
objWorksheet.Cells(1, 8).Value = "peso"
objWorksheet.Cells(1, 9).Value = "dimensiones"
objWorksheet.Cells(1, 10).Value = "material"
objWorksheet.Cells(1, 11).Value = "garantia_meses"
objWorksheet.Cells(1, 12).Value = "eficiencia_energetica"
objWorksheet.Cells(1, 13).Value = "impacto_ambiental"
objWorksheet.Cells(1, 14).Value = "puntuacion_eco"
objWorksheet.Cells(1, 15).Value = "imagen_url"
objWorksheet.Cells(1, 16).Value = "stock_inicial"
objWorksheet.Cells(1, 17).Value = "stock_minimo"
objWorksheet.Cells(1, 18).Value = "stock_maximo"

' Datos de productos
objWorksheet.Cells(2, 1).Value = "Laptop EcoTech Pro"
objWorksheet.Cells(2, 2).Value = "Laptop ecológica con procesador eficiente y batería de larga duración"
objWorksheet.Cells(2, 3).Value = 2999.99
objWorksheet.Cells(2, 4).Value = "Electrónicos"
objWorksheet.Cells(2, 5).Value = "EcoTech"
objWorksheet.Cells(2, 6).Value = "ET-2024"
objWorksheet.Cells(2, 7).Value = "Gris"
objWorksheet.Cells(2, 8).Value = 1.8
objWorksheet.Cells(2, 9).Value = "35x25x2 cm"
objWorksheet.Cells(2, 10).Value = "Aluminio reciclado"
objWorksheet.Cells(2, 11).Value = 24
objWorksheet.Cells(2, 12).Value = "A+++"
objWorksheet.Cells(2, 13).Value = "Bajo"
objWorksheet.Cells(2, 14).Value = 9.2
objWorksheet.Cells(2, 15).Value = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400"
objWorksheet.Cells(2, 16).Value = 15
objWorksheet.Cells(2, 17).Value = 3
objWorksheet.Cells(2, 18).Value = 50

objWorksheet.Cells(3, 1).Value = "Smartphone Verde Plus"
objWorksheet.Cells(3, 2).Value = "Teléfono inteligente con carcasa biodegradable y carga solar"
objWorksheet.Cells(3, 3).Value = 1899.50
objWorksheet.Cells(3, 4).Value = "Electrónicos"
objWorksheet.Cells(3, 5).Value = "VerdeMax"
objWorksheet.Cells(3, 6).Value = "VP-5G"
objWorksheet.Cells(3, 7).Value = "Verde"
objWorksheet.Cells(3, 8).Value = 0.2
objWorksheet.Cells(3, 9).Value = "15x7x0.8 cm"
objWorksheet.Cells(3, 10).Value = "Bioplástico"
objWorksheet.Cells(3, 11).Value = 18
objWorksheet.Cells(3, 12).Value = "A++"
objWorksheet.Cells(3, 13).Value = "Bajo"
objWorksheet.Cells(3, 14).Value = 8.8
objWorksheet.Cells(3, 15).Value = "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400"
objWorksheet.Cells(3, 16).Value = 25
objWorksheet.Cells(3, 17).Value = 5
objWorksheet.Cells(3, 18).Value = 100

objWorksheet.Cells(4, 1).Value = "Auriculares Bamboo Sound"
objWorksheet.Cells(4, 2).Value = "Auriculares inalámbricos fabricados con bambú sostenible"
objWorksheet.Cells(4, 3).Value = 249.99
objWorksheet.Cells(4, 4).Value = "Electrónicos"
objWorksheet.Cells(4, 5).Value = "BambooSound"
objWorksheet.Cells(4, 6).Value = "BS-100"
objWorksheet.Cells(4, 7).Value = "Marrón"
objWorksheet.Cells(4, 8).Value = 0.15
objWorksheet.Cells(4, 9).Value = "18x16x8 cm"
objWorksheet.Cells(4, 10).Value = "Bambú"
objWorksheet.Cells(4, 11).Value = 12
objWorksheet.Cells(4, 12).Value = ""
objWorksheet.Cells(4, 13).Value = "Bajo"
objWorksheet.Cells(4, 14).Value = 8.5
objWorksheet.Cells(4, 15).Value = "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400"
objWorksheet.Cells(4, 16).Value = 40
objWorksheet.Cells(4, 17).Value = 8
objWorksheet.Cells(4, 18).Value = 150

objWorksheet.Cells(5, 1).Value = "Cargador Solar 20W"
objWorksheet.Cells(5, 2).Value = "Cargador solar plegable para dispositivos móviles"
objWorksheet.Cells(5, 3).Value = 149.90
objWorksheet.Cells(5, 4).Value = "Electrónicos"
objWorksheet.Cells(5, 5).Value = "SolarTech"
objWorksheet.Cells(5, 6).Value = "ST-20W"
objWorksheet.Cells(5, 7).Value = "Negro"
objWorksheet.Cells(5, 8).Value = 0.3
objWorksheet.Cells(5, 9).Value = "15x10x2 cm"
objWorksheet.Cells(5, 10).Value = "Silicio"
objWorksheet.Cells(5, 11).Value = 24
objWorksheet.Cells(5, 12).Value = "A+++"
objWorksheet.Cells(5, 13).Value = "Bajo"
objWorksheet.Cells(5, 14).Value = 9.0
objWorksheet.Cells(5, 15).Value = "https://images.unsplash.com/photo-1593941707882-a5bac6861d75?w=400"
objWorksheet.Cells(5, 16).Value = 30
objWorksheet.Cells(5, 17).Value = 5
objWorksheet.Cells(5, 18).Value = 80

objWorksheet.Cells(6, 1).Value = "Botella Térmica Eco"
objWorksheet.Cells(6, 2).Value = "Botella térmica de acero inoxidable reciclado"
objWorksheet.Cells(6, 3).Value = 89.95
objWorksheet.Cells(6, 4).Value = "Hogar"
objWorksheet.Cells(6, 5).Value = "EcoBottle"
objWorksheet.Cells(6, 6).Value = "EB-500"
objWorksheet.Cells(6, 7).Value = "Azul"
objWorksheet.Cells(6, 8).Value = 0.5
objWorksheet.Cells(6, 9).Value = "8x8x25 cm"
objWorksheet.Cells(6, 10).Value = "Acero reciclado"
objWorksheet.Cells(6, 11).Value = 36
objWorksheet.Cells(6, 12).Value = ""
objWorksheet.Cells(6, 13).Value = "Bajo"
objWorksheet.Cells(6, 14).Value = 8.7
objWorksheet.Cells(6, 15).Value = "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400"
objWorksheet.Cells(6, 16).Value = 50
objWorksheet.Cells(6, 17).Value = 10
objWorksheet.Cells(6, 18).Value = 200

' Autoajustar columnas
objWorksheet.Columns("A:R").AutoFit

' Guardar archivo
objWorkbook.SaveAs "productos_prueba_apache_poi.xlsx", 51 ' 51 = xlOpenXMLWorkbook
objWorkbook.Close
objExcel.Quit

WScript.Echo "✅ Archivo Excel creado exitosamente: productos_prueba_apache_poi.xlsx"
WScript.Echo "📊 Total de columnas: 18"
WScript.Echo "📝 Total de filas de datos: 5"