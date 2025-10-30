package com.ecovivashop.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ecovivashop.dto.ProductoBulkUploadResult;
import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.Producto;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class ProductoBulkService {
    
    private final ProductoService productoService;
    private final InventarioService inventarioService;
    
    @Value("${app.upload.dir:uploads/}")
    private String uploadDir;
    
    // Encabezados esperados para CSV/Excel
    private static final String[] HEADERS = {
        "nombre", "descripcion", "precio", "categoria", "marca", "modelo", 
        "color", "peso", "dimensiones", "material", "garantia_meses",
        "eficiencia_energetica", "impacto_ambiental", "puntuacion_eco",
        "imagen_url", "stock_inicial", "stock_minimo", "stock_maximo"
    };
    
    // Encabezados obligatorios
    private static final List<String> HEADERS_OBLIGATORIOS = Arrays.asList(
        "nombre", "descripcion", "precio", "categoria"
    );
    
    public ProductoBulkService(ProductoService productoService, InventarioService inventarioService) {
        this.productoService = productoService;
        this.inventarioService = inventarioService;
    }
    
    /**
     * Procesa un archivo de carga masiva (CSV o Excel)
     */
    public ProductoBulkUploadResult procesarArchivo(MultipartFile archivo, boolean permitirNuevasCategorias) 
            throws IOException {
        
        ProductoBulkUploadResult resultado = new ProductoBulkUploadResult();
        String nombreArchivo = archivo.getOriginalFilename();
        
        if (nombreArchivo == null) {
            resultado.agregarError("Nombre de archivo no válido");
            return resultado;
        }
        
        try {
            if (nombreArchivo.toLowerCase().endsWith(".csv")) {
                procesarCSV(archivo.getInputStream(), resultado, permitirNuevasCategorias);
            } else if (nombreArchivo.toLowerCase().endsWith(".xlsx") || 
                      nombreArchivo.toLowerCase().endsWith(".xls")) {
                procesarExcel(archivo.getInputStream(), resultado, permitirNuevasCategorias);
            } else {
                resultado.agregarError("Formato de archivo no soportado");
            }
        } catch (IOException | CsvValidationException | RuntimeException e) {
            resultado.agregarError("Error al procesar archivo: " + e.getMessage());
        }
        
        return resultado;
    }
    
    /**
     * Procesa archivo CSV
     */
    private void procesarCSV(InputStream inputStream, ProductoBulkUploadResult resultado, 
                           boolean permitirNuevasCategorias) throws IOException, CsvValidationException {
        
        // Leer con BOM handling
        try (InputStreamReader isr = new InputStreamReader(inputStream, java.nio.charset.StandardCharsets.UTF_8);
             CSVReader reader = new CSVReader(isr)) {
            
            String[] headers = reader.readNext();
            
            if (headers == null) {
                resultado.agregarError("El archivo está vacío");
                return;
            }
            
            // Limpiar el primer header del posible BOM
            if (headers.length > 0 && headers[0].startsWith("\uFEFF")) {
                headers[0] = headers[0].substring(1);
            }
            
            // Validar encabezados
            if (!validarEncabezados(headers, resultado)) {
                return;
            }
            
            String[] linea;
            int numeroFila = 2; // Empezar desde la fila 2 (después del header)
            
            while ((linea = reader.readNext()) != null) {
                try {
                    procesarFilaProducto(headers, linea, numeroFila, resultado, permitirNuevasCategorias);
                } catch (Exception e) {
                    resultado.agregarError(numeroFila, "Error al procesar fila: " + e.getMessage());
                }
                numeroFila++;
            }
        }
    }
    
    /**
     * Procesa archivo Excel
     */
    private void procesarExcel(InputStream inputStream, ProductoBulkUploadResult resultado, 
                             boolean permitirNuevasCategorias) throws IOException {
        
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            if (sheet.getPhysicalNumberOfRows() == 0) {
                resultado.agregarError("La hoja de Excel está vacía");
                return;
            }
            
            // Leer encabezados
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                resultado.agregarError("No se encontraron encabezados en la hoja");
                return;
            }
            
            String[] headers = new String[headerRow.getPhysicalNumberOfCells()];
            for (int i = 0; i < headers.length; i++) {
                headers[i] = headerRow.getCell(i).getStringCellValue().trim().toLowerCase();
            }
            
            // Validar encabezados
            if (!validarEncabezados(headers, resultado)) {
                return;
            }
            
            // Procesar filas de datos
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                String[] datos = new String[headers.length];
                for (int j = 0; j < headers.length; j++) {
                    if (row.getCell(j) != null) {
                        datos[j] = row.getCell(j).toString().trim();
                    } else {
                        datos[j] = "";
                    }
                }
                
                try {
                    procesarFilaProducto(headers, datos, i + 1, resultado, permitirNuevasCategorias);
                } catch (Exception e) {
                    resultado.agregarError(i + 1, "Error al procesar fila: " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Valida que los encabezados contengan los campos obligatorios
     */
    private boolean validarEncabezados(String[] headers, ProductoBulkUploadResult resultado) {
        List<String> headersEncontrados = new ArrayList<>();
        
        // Debug: Log headers originales
        System.out.println("=== DEBUG HEADERS ===");
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            System.out.println("Header[" + i + "]: '" + header + "' (length: " + header.length() + ")");
            // Limpiar header: trim, lowercase y remover caracteres invisibles
            String headerLimpio = header.trim().toLowerCase().replaceAll("[\\p{Cntrl}\\p{Space}]", "");
            headersEncontrados.add(headerLimpio);
            System.out.println("  -> Limpio: '" + headerLimpio + "'");
        }
        
        System.out.println("Headers encontrados: " + headersEncontrados);
        System.out.println("Headers obligatorios: " + HEADERS_OBLIGATORIOS);
        
        List<String> faltantes = new ArrayList<>();
        for (String obligatorio : HEADERS_OBLIGATORIOS) {
            if (!headersEncontrados.contains(obligatorio)) {
                faltantes.add(obligatorio);
            }
        }
        
        if (!faltantes.isEmpty()) {
            resultado.agregarError("Faltan encabezados obligatorios: " + String.join(", ", faltantes));
            resultado.agregarError("Headers encontrados: " + String.join(", ", headersEncontrados));
            return false;
        }
        
        return true;
    }
    
    /**
     * Procesa una fila individual del archivo
     */
    @SuppressWarnings("EnhancedSwitchMigration")
    private void procesarFilaProducto(String[] headers, String[] datos, int numeroFila, 
                                    ProductoBulkUploadResult resultado, boolean permitirNuevasCategorias) {
        
        // Validar que la fila no esté vacía
        boolean filaVacia = true;
        for (String dato : datos) {
            if (dato != null && !dato.trim().isEmpty()) {
                filaVacia = false;
                break;
            }
        }
        
        if (filaVacia) {
            return; // Saltar fila vacía silenciosamente
        }
        
        try {
            Producto producto = new Producto();
            Inventario inventario = new Inventario();
            
            // Mapear datos según encabezados
            for (int i = 0; i < headers.length && i < datos.length; i++) {
                String header = headers[i].trim().toLowerCase();
                String valor = datos[i] != null ? datos[i].trim() : "";
                
                // Mapear datos según encabezados
                switch (header) {
                    case "nombre" -> {
                        if (valor.isEmpty()) {
                            resultado.agregarError(numeroFila, "El nombre es obligatorio");
                            return;
                        }
                        producto.setNombre(valor);
                    }
                    case "descripcion" -> {
                        if (valor.isEmpty()) {
                            resultado.agregarError(numeroFila, "La descripción es obligatoria");
                            return;
                        }
                        producto.setDescripcion(valor);
                    }
                    case "precio" -> {
                        if (valor.isEmpty()) {
                            resultado.agregarError(numeroFila, "El precio es obligatorio");
                            return;
                        }
                        try {
                            // Limpiar formato de precio (quitar S/, comas, etc.)
                            String precioLimpio = valor.replaceAll("[^\\d.,]", "").replace(",", ".");
                            BigDecimal precio = new BigDecimal(precioLimpio);
                            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                                resultado.agregarError(numeroFila, "El precio debe ser mayor a 0");
                                return;
                            }
                            // Validar precisión (10,2) y truncar si es necesario
                            if (!validarPrecisionBigDecimal(precio, 10, 2)) {
                                precio = truncarBigDecimal(precio, 10, 2);
                                resultado.agregarAdvertencia(numeroFila, "Precio truncado para cumplir restricciones de BD: " + precio);
                            }
                            producto.setPrecio(precio);
                        } catch (NumberFormatException e) {
                            resultado.agregarError(numeroFila, "Formato de precio inválido: " + valor);
                            return;
                        }
                    }
                    case "categoria" -> {
                        if (valor.isEmpty()) {
                            resultado.agregarError(numeroFila, "La categoría es obligatoria");
                            return;
                        }

                        // Verificar si la categoría existe o crearla si se permite
                        List<String> categoriasExistentes = productoService.obtenerCategoriasDisponibles();
                        if (!categoriasExistentes.contains(valor)) {
                            if (permitirNuevasCategorias) {
                                resultado.agregarCategoriaCreada(valor);
                                resultado.agregarAdvertencia(numeroFila,
                                        "Se creará nueva categoría: " + valor);
                            } else {
                                resultado.agregarError(numeroFila,
                                        "Categoría no existe: " + valor + ". Categorías disponibles: " +
                                                String.join(", ", categoriasExistentes));
                                return;
                            }
                        }
                        producto.setCategoria(valor);
                    }
                    case "marca" -> producto.setMarca(valor.isEmpty() ? null : valor);
                    case "modelo" -> producto.setModelo(valor.isEmpty() ? null : valor);
                    case "color" -> producto.setColor(valor.isEmpty() ? null : valor);
                    case "peso" -> {
                        if (!valor.isEmpty()) {
                            try {
                                String pesoLimpio = valor.replaceAll("[^\\d.,]", "").replace(",", ".");
                                BigDecimal peso = new BigDecimal(pesoLimpio);
                                if (peso.compareTo(BigDecimal.ZERO) < 0) {
                                    resultado.agregarError(numeroFila, "El peso no puede ser negativo");
                                    return;
                                }
                                // Validar precisión (8,2) y truncar si es necesario
                                if (!validarPrecisionBigDecimal(peso, 8, 2)) {
                                    peso = truncarBigDecimal(peso, 8, 2);
                                    resultado.agregarAdvertencia(numeroFila, "Peso truncado para cumplir restricciones de BD: " + peso);
                                }
                                producto.setPeso(peso);
                            } catch (NumberFormatException e) {
                                resultado.agregarError(numeroFila, "Formato de peso inválido: " + valor);
                                return;
                            }
                        }
                    }
                    case "dimensiones" -> producto.setDimensiones(valor.isEmpty() ? null : valor);
                    case "material" -> producto.setMaterial(valor.isEmpty() ? null : valor);
                    case "garantia_meses" -> {
                        if (!valor.isEmpty()) {
                            try {
                                producto.setGarantiaMeses(Integer.valueOf(valor));
                            } catch (NumberFormatException e) {
                                resultado.agregarAdvertencia(numeroFila, "Formato de garantía inválido: " + valor);
                            }
                        }
                    }
                    case "eficiencia_energetica" -> producto.setEficienciaEnergetica(valor.isEmpty() ? null : valor);
                    case "impacto_ambiental" -> producto.setImpactoAmbiental(valor.isEmpty() ? null : valor);
                    case "puntuacion_eco" -> {
                        if (!valor.isEmpty()) {
                            try {
                                String puntuacionLimpia = valor.replace(",", ".");
                                BigDecimal puntuacion = new BigDecimal(puntuacionLimpia);
                                if (puntuacion.compareTo(BigDecimal.ZERO) < 0) {
                                    resultado.agregarError(numeroFila, "La puntuación eco no puede ser negativa");
                                    return;
                                }
                                // Validar precisión (3,2) y truncar si es necesario
                                if (!validarPrecisionBigDecimal(puntuacion, 3, 2)) {
                                    puntuacion = truncarBigDecimal(puntuacion, 3, 2);
                                    resultado.agregarAdvertencia(numeroFila, "Puntuación eco truncada para cumplir restricciones de BD: " + puntuacion);
                                }
                                producto.setPuntuacionEco(puntuacion);
                            } catch (NumberFormatException e) {
                                resultado.agregarError(numeroFila, "Formato de puntuación eco inválido: " + valor);
                                return;
                            }
                        }
                    }
                    case "imagen_url" -> producto.setImagenUrl(valor.isEmpty() ? null : valor);

                    // Campos de inventario
                    case "stock_inicial" -> {
                        if (!valor.isEmpty()) {
                            try {
                                inventario.setStock(Integer.valueOf(valor));
                            } catch (NumberFormatException e) {
                                resultado.agregarAdvertencia(numeroFila,
                                        "Formato de stock inicial inválido: " + valor);
                                inventario.setStock(0);
                            }
                        } else {
                            inventario.setStock(0);
                        }
                    }
                    case "stock_minimo" -> {
                        if (!valor.isEmpty()) {
                            try {
                                inventario.setStockMinimo(Integer.valueOf(valor));
                            } catch (NumberFormatException e) {
                                resultado.agregarAdvertencia(numeroFila,
                                        "Formato de stock mínimo inválido: " + valor);
                                inventario.setStockMinimo(5);
                            }
                        } else {
                            inventario.setStockMinimo(5);
                        }
                    }
                    case "stock_maximo" -> {
                        if (!valor.isEmpty()) {
                            try {
                                inventario.setStockMaximo(Integer.valueOf(valor));
                            } catch (NumberFormatException e) {
                                resultado.agregarAdvertencia(numeroFila,
                                        "Formato de stock máximo inválido: " + valor);
                                inventario.setStockMaximo(100);
                            }
                        } else {
                            inventario.setStockMaximo(100);
                        }
                    }
                }
            }
            
            // Solo guardar si no hay errores en esta fila
            if (resultado.getErrores().stream().noneMatch(error -> error.contains("Fila " + numeroFila))) {
                guardarProductoConInventario(producto, inventario, numeroFila, resultado);
            }
            
        } catch (Exception e) {
            resultado.agregarError(numeroFila, "Error al procesar fila: " + e.getMessage());
        }
    }
    
    /**
     * Genera plantilla CSV para carga masiva
     */
    public byte[] generarPlantillaCSV() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, "UTF-8");
             CSVWriter csvWriter = new CSVWriter(writer)) {
            
            // Escribir encabezados
            csvWriter.writeNext(HEADERS);
            
            // Escribir fila de ejemplo
            String[] ejemplo = {
                "Laptop Eco Friendly",                    // nombre
                "Laptop ecológica con bajo consumo energético",  // descripcion
                "2500.00",                                // precio
                "Electrónicos",                          // categoria
                "EcoTech",                               // marca
                "ET-2024",                               // modelo
                "Gris",                                  // color
                "1.5",                                   // peso
                "35x25x2 cm",                           // dimensiones
                "Aluminio reciclado",                    // material
                "24",                                    // garantia_meses
                "A+++",                                  // eficiencia_energetica
                "Bajo",                                  // impacto_ambiental
                "8.5",                                   // puntuacion_eco
                "https://ejemplo.com/imagen.jpg",        // imagen_url
                "50",                                    // stock_inicial
                "5",                                     // stock_minimo
                "100"                                    // stock_maximo
            };
            csvWriter.writeNext(ejemplo);
        }
        
        return outputStream.toByteArray();
    }
    
    /**
     * Genera plantilla Excel para carga masiva
     */
    public byte[] generarPlantillaExcel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Productos");
            
            // Crear fila de encabezados
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                headerRow.createCell(i).setCellValue(HEADERS[i]);
            }
            
            // Crear fila de ejemplo
            Row ejemploRow = sheet.createRow(1);
            String[] ejemplo = {
                "Laptop Eco Friendly",
                "Laptop ecológica con bajo consumo energético",
                "2500.00",
                "Electrónicos",
                "EcoTech",
                "ET-2024",
                "Gris",
                "1.5",
                "35x25x2 cm",
                "Aluminio reciclado",
                "24",
                "A+++",
                "Bajo",
                "8.5",
                "https://ejemplo.com/imagen.jpg",
                "50",
                "5",
                "100"
            };
            
            for (int i = 0; i < ejemplo.length; i++) {
                ejemploRow.createCell(i).setCellValue(ejemplo[i]);
            }
            
            // Ajustar ancho de columnas
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }
    
    /**
     * Procesa y guarda una imagen subida
     */
    public String procesarImagen(MultipartFile imagen) throws IOException {
        if (imagen.isEmpty()) {
            return null;
        }
        
        // Validar tipo de archivo
        String contentType = imagen.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("El archivo debe ser una imagen");
        }
        
        // Generar nombre único
        String nombreOriginal = imagen.getOriginalFilename();
        String extension = nombreOriginal != null && nombreOriginal.contains(".") 
            ? nombreOriginal.substring(nombreOriginal.lastIndexOf("."))
            : ".jpg";
        String nombreUnico = UUID.randomUUID().toString() + extension;
        
        // Crear directorio si no existe
        Path directorioUpload = Paths.get(uploadDir);
        if (!Files.exists(directorioUpload)) {
            Files.createDirectories(directorioUpload);
        }
        
        // Guardar archivo
        Path rutaArchivo = directorioUpload.resolve(nombreUnico);
        Files.copy(imagen.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
        
        // Retornar URL relativa
        return "/uploads/" + nombreUnico;
    }
    
    /**
     * Guarda un producto individual sin transacciones globales
     */
    private Producto guardarProductoIndividual(Producto producto) {
        try {
            return productoService.save(producto);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar producto: " + e.getMessage(), e);
        }
    }
    
    /**
     * Guarda un inventario individual sin transacciones globales
     */
    private void guardarInventarioIndividual(Inventario inventario) {
        try {
            inventarioService.save(inventario);
        } catch (Exception e) {
            throw new RuntimeException("Error al guardar inventario: " + e.getMessage(), e);
        }
    }
    
    /**
     * Guarda un producto con su inventario en una sola operación
     */
    private void guardarProductoConInventario(Producto producto, Inventario inventario, 
                                            int numeroFila, ProductoBulkUploadResult resultado) {
        try {
            // Configurar valores por defecto
            producto.setEstado(true);
            producto.setFechaCreacion(LocalDateTime.now());
            
            // Guardar producto
            Producto productoGuardado = guardarProductoIndividual(producto);
            
            // Configurar y guardar inventario
            inventario.setProducto(productoGuardado);
            inventario.setFechaActualizacion(LocalDateTime.now());
            guardarInventarioIndividual(inventario);
            
            resultado.incrementarProductosProcesados();
            
        } catch (Exception e) {
            resultado.agregarError(numeroFila, "Error al guardar producto: " + e.getMessage());
        }
    }
    
    /**
     * Valida que un BigDecimal respete las restricciones de precisión de la BD
     */
    private boolean validarPrecisionBigDecimal(BigDecimal valor, int precision, int scale) {
        if (valor == null) return true;
        
        // Convertir a string para contar dígitos
        String valorStr = valor.stripTrailingZeros().toPlainString();
        
        // Remover signo negativo si existe
        if (valorStr.startsWith("-")) {
            valorStr = valorStr.substring(1);
        }
        
        // Dividir en parte entera y decimal
        String[] partes = valorStr.split("\\.");
        String parteEntera = partes[0];
        String parteDecimal = partes.length > 1 ? partes[1] : "";
        
        // Validar restricciones
        int digitosTotales = parteEntera.length() + parteDecimal.length();
        int digitosDecimales = parteDecimal.length();
        
        return digitosTotales <= precision && digitosDecimales <= scale;
    }
    
    /**
     * Trunca un BigDecimal a la precisión permitida
     */
    private BigDecimal truncarBigDecimal(BigDecimal valor, int precision, int scale) {
        if (valor == null) return null;
        
        // Ajustar a la escala permitida
        valor = valor.setScale(scale, java.math.RoundingMode.HALF_UP);
        
        // Calcular el máximo valor permitido
        StringBuilder maxStr = new StringBuilder();
        int digitosEnteros = precision - scale;
        
        for (int i = 0; i < digitosEnteros; i++) {
            maxStr.append("9");
        }
        
        if (scale > 0) {
            maxStr.append(".");
            for (int i = 0; i < scale; i++) {
                maxStr.append("9");
            }
        }
        
        BigDecimal maxValor = new BigDecimal(maxStr.toString());
        
        // Retornar el menor entre el valor y el máximo permitido
        return valor.compareTo(maxValor) > 0 ? maxValor : valor;
    }
}
