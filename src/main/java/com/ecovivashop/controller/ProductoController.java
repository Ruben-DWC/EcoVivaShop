package com.ecovivashop.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecovivashop.dto.ProductoBulkUploadResult;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.service.ExportService;
import com.ecovivashop.service.JasperExportService;
import com.ecovivashop.service.ProductoBulkService;
import com.ecovivashop.service.ProductoService;

@Controller
@RequestMapping("/admin/productos")
public class ProductoController extends BaseAdminController {
    
    private final ProductoService productoService;
    private final ProductoBulkService productoBulkService;
    private final ExportService exportService;
    private final JasperExportService jasperExportService;
    
    public ProductoController(ProductoService productoService, 
                             ProductoBulkService productoBulkService,
                             ExportService exportService,
                             JasperExportService jasperExportService) {
        this.productoService = productoService;
        this.productoBulkService = productoBulkService;
        this.exportService = exportService;
        this.jasperExportService = jasperExportService;
    }
    
    /**
     * Página principal de gestión de productos
     */
    @GetMapping("")
    public String gestionProductos(Model model, Principal principal,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "12") int size,
                                  @RequestParam(defaultValue = "nombre") String sortBy,
                                  @RequestParam(defaultValue = "asc") String sortDir,
                                  @RequestParam(required = false) String categoria,
                                  @RequestParam(required = false) String busqueda) {
        
        // Usuario actual para el header
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        
        // Configurar paginación y ordenamiento
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Obtener productos según filtros
        Page<Producto> productos;
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            // Implementar búsqueda por texto
            productos = productoService.buscarProductosPaginado(busqueda, pageable);
            System.out.println("DEBUG: Búsqueda por texto: " + busqueda + ", encontrados: " + productos.getTotalElements());
        } else if (categoria != null && !categoria.trim().isEmpty()) {
            productos = productoService.obtenerPorCategoriaPaginado(categoria, pageable);
            System.out.println("DEBUG: Búsqueda por categoría: " + categoria + ", encontrados: " + productos.getTotalElements());
        } else {
            productos = productoService.obtenerProductosPaginados(pageable);
            System.out.println("DEBUG: Búsqueda general, encontrados: " + productos.getTotalElements());
        }
        
        System.out.println("DEBUG: Total productos en página: " + productos.getNumberOfElements());
        System.out.println("DEBUG: Has content: " + productos.hasContent());
        
        // Obtener categorías disponibles
        List<String> categorias = productoService.obtenerCategoriasDisponibles();
        
        // Agregar al modelo
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categorias);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", productos.getTotalPages());
        model.addAttribute("totalItems", productos.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("categoria", categoria);
        model.addAttribute("busqueda", busqueda);
        
        return "admin/productos/gestion";
    }
    
    /**
     * Página de carga masiva de productos
     */
    @GetMapping("/bulk-upload")
    public String bulkUploadForm(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        
        // Obtener categorías existentes
        List<String> categorias = productoService.obtenerCategoriasDisponibles();
        model.addAttribute("categorias", categorias);
        
        return "admin/productos/bulk-upload";
    }
    
    /**
     * Procesar archivo de carga masiva
     */
    @PostMapping("/bulk-upload")
    public String procesarBulkUpload(@RequestParam("archivo") MultipartFile archivo,
                                   @RequestParam(required = false) String crearCategorias,
                                   RedirectAttributes redirectAttributes,
                                   Principal principal) {
        try {
            if (archivo.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Por favor seleccione un archivo");
                return "redirect:/admin/productos/bulk-upload";
            }
            
            // Validar tipo de archivo
            String nombreArchivo = archivo.getOriginalFilename();
            if (nombreArchivo == null || 
                (!nombreArchivo.toLowerCase().endsWith(".csv") && 
                 !nombreArchivo.toLowerCase().endsWith(".xlsx") && 
                 !nombreArchivo.toLowerCase().endsWith(".xls"))) {
                redirectAttributes.addFlashAttribute("error", 
                    "Formato de archivo no válido. Solo se permiten archivos CSV y Excel");
                return "redirect:/admin/productos/bulk-upload";
            }
            
            // Procesar archivo
            boolean permitirNuevasCategorias = "on".equals(crearCategorias);
            ProductoBulkUploadResult resultado = productoBulkService.procesarArchivo(
                archivo, permitirNuevasCategorias);
            
            // Preparar mensaje de resultado
            String mensaje = String.format(
                "Carga completada: %d productos procesados exitosamente",
                resultado.getProductosProcesados()
            );
            
            if (!resultado.getErrores().isEmpty()) {
                mensaje += String.format(", %d errores encontrados", resultado.getErrores().size());
            }
            
            redirectAttributes.addFlashAttribute("success", mensaje);
            redirectAttributes.addFlashAttribute("resultado", resultado);
            
        } catch (IOException | RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al procesar la carga masiva: " + e.getMessage());
        }
        
        return "redirect:/admin/productos/bulk-upload";
    }
    
    /**
     * Descargar plantilla CSV para carga masiva
     */
    @GetMapping("/plantilla-csv")
    @ResponseBody
    public ResponseEntity<byte[]> descargarPlantillaCSV() {
        try {
            byte[] csvData = productoBulkService.generarPlantillaCSV();
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=plantilla_productos.csv")
                .header("Content-Type", "text/csv; charset=UTF-8")
                .body(csvData);
                
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Descargar plantilla Excel para carga masiva
     */
    @GetMapping("/plantilla-excel")
    @ResponseBody
    public ResponseEntity<byte[]> descargarPlantillaExcel() {
        try {
            byte[] excelData = productoBulkService.generarPlantillaExcel();
            
            return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=plantilla_productos.xlsx")
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelData);
                
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Formulario para agregar producto individual
     */
    @GetMapping("/agregar")
    public String formularioAgregar(Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        
        model.addAttribute("producto", new Producto());
        model.addAttribute("categorias", productoService.obtenerCategoriasDisponibles());
        
        return "admin/productos/formulario";
    }
    
    /**
     * Formulario para editar producto
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Integer id, Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }
        
        Producto producto = productoService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", productoService.obtenerCategoriasDisponibles());
        
        return "admin/productos/formulario";
    }
    
    /**
     * Guardar producto (nuevo o editado)
     */
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute Producto producto,
                                 @RequestParam(required = false) MultipartFile imagen,
                                 RedirectAttributes redirectAttributes,
                                 Principal principal) {
        try {
            // Procesar imagen si se proporcionó
            if (imagen != null && !imagen.isEmpty()) {
                String urlImagen = productoBulkService.procesarImagen(imagen);
                producto.setImagenUrl(urlImagen);
            }
            
            // Guardar producto
            productoService.save(producto);
            
            // Crear inventario inicial si es producto nuevo
            if (producto.getIdProducto() == null || producto.getInventario() == null) {
                productoService.crearInventarioInicial(producto);
            }
            
            redirectAttributes.addFlashAttribute("success", 
                "Producto guardado exitosamente");
                
        } catch (IOException | RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al guardar producto: " + e.getMessage());
        }
        
        return "redirect:/admin/productos";
    }
    
    /**
     * Eliminar producto
     */
    @PostMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id, 
                                  RedirectAttributes redirectAttributes) {
        try {
            productoService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", 
                "Producto eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al eliminar producto: " + e.getMessage());
        }
        
        return "redirect:/admin/productos";
    }
    
    /**
     * API: Obtener categorías para formularios dinámicos
     */
    @GetMapping("/api/categorias")
    @ResponseBody
    public List<String> obtenerCategorias() {
        return productoService.obtenerCategoriasDisponibles();
    }
    
    /**
     * API: Validar SKU único
     */
    @GetMapping("/api/validar-sku")
    @ResponseBody
    public ResponseEntity<Boolean> validarSKU(@RequestParam String sku, 
                                             @RequestParam(required = false) Integer idProducto) {
        boolean esUnico = productoService.validarSKUUnico(sku, idProducto);
        return ResponseEntity.ok(esUnico);
    }
    
    /**
     * MÉTODO TEMPORAL - Crear productos de prueba
     * Solo para debugging - eliminar después
     */
    @GetMapping("/crear-prueba")
    public String crearProductosPrueba(RedirectAttributes redirectAttributes) {
        try {
            // Crear producto de prueba 1
            Producto producto1 = new Producto();
            producto1.setNombre("Producto de Prueba 1");
            producto1.setDescripcion("Descripción de prueba para verificar el sistema");
            producto1.setPrecio(new BigDecimal("99.99"));
            producto1.setCategoria("Prueba");
            producto1.setMarca("TestMarca");
            producto1.setModelo("TEST-001");
            producto1.setPuntuacionEco(new BigDecimal("8.5"));
            producto1.setEstado(true);
            
            productoService.save(producto1);
            
            // Crear producto de prueba 2
            Producto producto2 = new Producto();
            producto2.setNombre("Producto de Prueba 2");
            producto2.setDescripcion("Segundo producto de prueba");
            producto2.setPrecio(new BigDecimal("149.99"));
            producto2.setCategoria("Electrodomésticos");
            producto2.setMarca("EcoTest");
            producto2.setModelo("ECO-002");
            producto2.setPuntuacionEco(new BigDecimal("9.0"));
            producto2.setEstado(true);
            
            productoService.save(producto2);
            
            redirectAttributes.addFlashAttribute("success", "Productos de prueba creados exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear productos de prueba: " + e.getMessage());
        }
        
        return "redirect:/admin/productos";
    }
    
    /**
     * Procesar archivo de carga masiva (endpoint alternativo para compatibilidad)
     */
    @PostMapping("/cargar-masivo")
    public String procesarCargaMasiva(@RequestParam("archivo") MultipartFile archivo,
                                     @RequestParam(required = false) String crearCategorias,
                                     @RequestParam(required = false) String actualizarExistentes,
                                     RedirectAttributes redirectAttributes,
                                     Principal principal) {
        // Redirigir al método principal
        return procesarBulkUpload(archivo, crearCategorias, redirectAttributes, principal);
    }
    
    /**
     * Exportar reporte de productos en formato PDF
     */
    @GetMapping("/exportar/pdf")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<byte[]> exportarProductosPDF() {
        try {
            List<Producto> productos = productoService.findAll();
            byte[] pdfBytes = exportService.exportarProductosPDF(productos);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("reporte_productos.pdf").build());
            headers.setContentLength(pdfBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Exportar reporte de productos en formato Excel
     */
    @GetMapping("/exportar/excel")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<byte[]> exportarProductosExcel() {
        try {
            List<Producto> productos = productoService.findAll();
            byte[] excelBytes = exportService.exportarProductosExcel(productos);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("reporte_productos.xlsx").build());
            headers.setContentLength(excelBytes.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(excelBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Exportar reporte de productos via JasperReports (POC) en formato PDF
     */
    @GetMapping("/exportar/jasper/pdf")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<byte[]> exportarProductosJasperPDF() {
        try {
            List<Producto> productos = productoService.findAll();
            byte[] pdfBytes = jasperExportService.exportarProductosJasperPDF(productos);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("reporte_productos_jasper.pdf").build());
            headers.setContentLength(pdfBytes.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
