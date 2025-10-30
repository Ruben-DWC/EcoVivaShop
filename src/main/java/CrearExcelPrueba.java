import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CrearExcelPrueba {
    public static void main(String[] args) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Productos");

            // Crear fila de encabezados
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                "nombre", "descripcion", "precio", "categoria", "marca", "modelo",
                "color", "peso", "dimensiones", "material", "garantia_meses",
                "eficiencia_energetica", "impacto_ambiental", "puntuacion_eco",
                "imagen_url", "stock_inicial", "stock_minimo", "stock_maximo"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            // Crear datos de ejemplo
            Object[][] data = {
                {"Laptop EcoTech Pro", "Laptop ecológica con procesador eficiente y batería de larga duración", 2999.99, "Electrónicos", "EcoTech", "ET-2024", "Gris", 1.8, "35x25x2 cm", "Aluminio reciclado", 24, "A+++", "Bajo", 9.2, "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400", 15, 3, 50},
                {"Smartphone Verde Plus", "Teléfono inteligente con carcasa biodegradable y carga solar", 1899.50, "Electrónicos", "VerdeMax", "VP-5G", "Verde", 0.2, "15x7x0.8 cm", "Bioplástico", 18, "A++", "Bajo", 8.8, "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400", 25, 5, 100},
                {"Auriculares Bamboo Sound", "Auriculares inalámbricos fabricados con bambú sostenible", 249.99, "Electrónicos", "BambooSound", "BS-100", "Marrón", 0.15, "18x16x8 cm", "Bambú", 12, "", "Bajo", 8.5, "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400", 40, 8, 150},
                {"Cargador Solar 20W", "Cargador solar plegable para dispositivos móviles", 149.90, "Electrónicos", "SolarTech", "ST-20W", "Negro", 0.3, "15x10x2 cm", "Silicio", 24, "A+++", "Bajo", 9.0, "https://images.unsplash.com/photo-1593941707882-a5bac6861d75?w=400", 30, 5, 80},
                {"Botella Térmica Eco", "Botella térmica de acero inoxidable reciclado", 89.95, "Hogar", "EcoBottle", "EB-500", "Azul", 0.5, "8x8x25 cm", "Acero reciclado", 36, "", "Bajo", 8.7, "https://images.unsplash.com/photo-1602143407151-7111542de6e8?w=400", 50, 10, 200}
            };

            // Crear filas de datos
            for (int i = 0; i < data.length; i++) {
                Row row = sheet.createRow(i + 1);
                for (int j = 0; j < data[i].length; j++) {
                    Cell cell = row.createCell(j);
                    if (data[i][j] instanceof String) {
                        cell.setCellValue((String) data[i][j]);
                    } else if (data[i][j] instanceof Double) {
                        cell.setCellValue((Double) data[i][j]);
                    } else if (data[i][j] instanceof Integer) {
                        cell.setCellValue((Integer) data[i][j]);
                    }
                }
            }

            // Autoajustar columnas
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Guardar archivo
            try (FileOutputStream fileOut = new FileOutputStream("productos_prueba_apache_poi.xlsx")) {
                workbook.write(fileOut);
            }

            System.out.println("✅ Archivo Excel creado exitosamente: productos_prueba_apache_poi.xlsx");
            System.out.println("📊 Total de columnas: " + headers.length);
            System.out.println("📝 Total de filas de datos: " + data.length);

        } catch (IOException e) {
            System.err.println("❌ Error al crear el archivo Excel: " + e.getMessage());
        }
    }
}