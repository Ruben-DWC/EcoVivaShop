package com.ecovivashop.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.InventarioHistorial;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.InventarioHistorialRepository;
import com.ecovivashop.service.ExportService;
import com.ecovivashop.service.InventarioService;
import com.itextpdf.text.DocumentException;

@Controller
@RequestMapping("/admin/inventario")
public class InventarioController extends BaseAdminController {

    private final InventarioService inventarioService;
    private final ExportService exportService;
    private final InventarioHistorialRepository inventarioHistorialRepository;

    public InventarioController(InventarioService inventarioService, ExportService exportService, InventarioHistorialRepository inventarioHistorialRepository) {
        this.inventarioService = inventarioService;
        this.exportService = exportService;
        this.inventarioHistorialRepository = inventarioHistorialRepository;
    }

    /**
     * Página principal de gestión de inventario
     */
    @GetMapping("")
    public String gestionInventario(Model model, Principal principal,
                                  @RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "15") int size,
                                  @RequestParam(defaultValue = "fechaActualizacion") String sortBy,
                                  @RequestParam(defaultValue = "desc") String sortDir,
                                  @RequestParam(required = false) String filtro) {

        // Usuario actual
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }

        // Configurar paginación y ordenamiento
        Sort sort = sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        // Obtener inventarios según filtro
        Page<Inventario> inventarios;
        String filtroActual = filtro != null ? filtro : "";
        inventarios = switch (filtroActual) {
            case "alertas" -> inventarioService.obtenerAlertasInventario(pageable);
            case "agotados" -> inventarioService.obtenerAgotados(pageable);
            case "stock-bajo" -> inventarioService.obtenerConStockBajo(pageable);
            default -> inventarioService.obtenerInventariosActivos(pageable);
        };

        // Estadísticas
        Long totalProductos = inventarioService.contarConStock();
        Long productosAgotados = inventarioService.contarAgotados();
        Long productosStockBajo = inventarioService.contarConStockBajo();
        Long stockTotal = inventarioService.obtenerStockTotal();

        // Manejar valores null para estadísticas
        totalProductos = totalProductos != null ? totalProductos : 0L;
        productosAgotados = productosAgotados != null ? productosAgotados : 0L;
        productosStockBajo = productosStockBajo != null ? productosStockBajo : 0L;
        stockTotal = stockTotal != null ? stockTotal : 0L;

        // Agregar al modelo
        model.addAttribute("inventarios", inventarios);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", inventarios.getTotalPages());
        model.addAttribute("totalItems", inventarios.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("filtro", filtro);

        // Estadísticas
        model.addAttribute("totalProductos", totalProductos);
        model.addAttribute("productosAgotados", productosAgotados);
        model.addAttribute("productosStockBajo", productosStockBajo);
        model.addAttribute("stockTotal", stockTotal);

        return "admin/inventario/gestion";
    }

    /**
     * Formulario para editar inventario
     */
    @GetMapping("/editar/{id}")
    public String formularioEditar(@PathVariable Integer id, Model model, Principal principal) {
        if (principal != null) {
            Usuario usuario = usuarioService.findByEmail(principal.getName());
            model.addAttribute("usuario", usuario);
        }

        Inventario inventario = inventarioService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));

        List<String> ubicaciones = inventarioService.obtenerUbicaciones();
        ubicaciones = ubicaciones != null ? ubicaciones : new ArrayList<>();

        // Obtener historial de cambios recientes (últimos 10)
        List<InventarioHistorial> historialReciente = inventarioHistorialRepository
            .findTop10ByInventarioOrderByFechaCambioDesc(inventario);

        model.addAttribute("inventario", inventario);
        model.addAttribute("ubicaciones", ubicaciones);
        model.addAttribute("historialReciente", historialReciente);

        return "admin/inventario/formulario";
    }

    /**
     * Guardar cambios en inventario
     */
    @PostMapping("/guardar")
    public String guardarInventario(@ModelAttribute Inventario inventario,
                                   @RequestParam(required = false) String motivo,
                                   RedirectAttributes redirectAttributes,
                                   Principal principal) {

        try {
            String usuarioActualizacion = principal != null ? principal.getName() : "SISTEMA";

            // Obtener inventario actual antes de cambios para historial
            Inventario inventarioActual = inventarioService.findById(inventario.getIdInventario())
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado"));

            // Actualizar stock con trazabilidad
            inventarioService.actualizarStock(
                inventario.getProducto().getIdProducto(),
                inventario.getStock(),
                usuarioActualizacion + (motivo != null ? " - " + motivo : "")
            );

            // Actualizar configuración si cambió
            inventarioService.actualizarConfiguracion(
                inventario.getIdInventario(),
                inventario.getStockMinimo(),
                inventario.getStockMaximo(),
                inventario.getUbicacion(),
                usuarioActualizacion
            );

            // Registrar cambios en el historial
            registrarCambiosHistorial(inventarioActual, inventario, usuarioActualizacion, motivo);

            redirectAttributes.addFlashAttribute("success",
                "Inventario actualizado exitosamente");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al actualizar inventario: " + e.getMessage());
        }

        return "redirect:/admin/inventario";
    }

    /**
     * Ajuste rápido de stock
     */
    @PostMapping("/ajustar-stock")
    public String ajustarStock(@RequestParam Integer idProducto,
                              @RequestParam Integer cantidadAjuste,
                              @RequestParam String motivo,
                              RedirectAttributes redirectAttributes,
                              Principal principal) {

        try {
            String usuarioActualizacion = principal != null ? principal.getName() : "SISTEMA";

            // Obtener inventario actual antes del cambio
            Inventario inventarioActual = inventarioService.findByProductoId(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para el producto"));

            // Capturar valores antes del cambio para el historial
            Integer stockAnterior = inventarioActual.getStock();

            // Realizar el ajuste
            inventarioService.ajustarStock(idProducto, cantidadAjuste, motivo, usuarioActualizacion);

            // Obtener el nuevo stock después del cambio
            Integer stockNuevo = inventarioService.obtenerStockDisponible(idProducto);

            // Registrar en el historial
            String tipoCambio = cantidadAjuste > 0 ? "AUMENTO" : "DISMINUCION";
            InventarioHistorial historial = new InventarioHistorial(
                inventarioActual,
                tipoCambio,
                stockAnterior,
                stockNuevo,
                cantidadAjuste,
                motivo,
                usuarioActualizacion
            );
            inventarioHistorialRepository.save(historial);

            redirectAttributes.addFlashAttribute("success",
                "Stock ajustado exitosamente");

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al ajustar stock: " + e.getMessage());
        }

        return "redirect:/admin/inventario";
    }

    /**
     * API: Obtener stock disponible de un producto
     */
    @GetMapping("/api/stock/{idProducto}")
    public String obtenerStockDisponible(@PathVariable Integer idProducto) {
        Integer stock = inventarioService.obtenerStockDisponible(idProducto);
        return String.valueOf(stock);
    }

    /**
     * API: Verificar disponibilidad de stock
     */
    @GetMapping("/api/disponibilidad/{idProducto}/{cantidad}")
    public String verificarDisponibilidad(@PathVariable Integer idProducto,
                                         @PathVariable Integer cantidad) {
        boolean disponible = inventarioService.verificarDisponibilidad(idProducto, cantidad);
        return String.valueOf(disponible);
    }

    /**
     * API: Aumentar stock de un producto (cantidad específica)
     */
    @PostMapping("/api/aumentar-stock")
    @ResponseBody
    @SuppressWarnings("CallToPrintStackTrace")
    public String aumentarStock(@RequestParam Integer idProducto,
                               @RequestParam Integer cantidad,
                               @RequestParam(required = false) String motivo,
                               Principal principal) {
        System.out.println("🔄 [CONTROLLER] aumentarStock called - idProducto: " + idProducto + ", cantidad: " + cantidad + ", motivo: " + motivo);
        System.out.println("🔄 [CONTROLLER] Principal: " + (principal != null ? principal.getName() : "NULL"));

        try {
            String usuarioActualizacion = principal != null ? principal.getName() : "SISTEMA";
            String motivoFinal = motivo != null ? motivo : "Aumento manual de stock";

            // Obtener inventario actual antes del cambio
            Inventario inventarioActual = inventarioService.findByProductoId(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para el producto"));

            // Capturar valores antes del cambio para el historial
            Integer stockAnterior = inventarioActual.getStock();

            System.out.println("🔄 [CONTROLLER] Calling inventarioService.ajustarStock with cantidad: " + cantidad);
            inventarioService.ajustarStock(idProducto, cantidad, motivoFinal, usuarioActualizacion);

            // Obtener el nuevo stock después del cambio
            Integer stockNuevo = inventarioService.obtenerStockDisponible(idProducto);

            InventarioHistorial historial = new InventarioHistorial(
                inventarioActual,
                "AUMENTO",
                stockAnterior,
                stockNuevo,
                cantidad,
                motivoFinal,
                usuarioActualizacion
            );
            inventarioHistorialRepository.save(historial);

            System.out.println("✅ [CONTROLLER] aumentarStock completed successfully");
            return "OK";
        } catch (Exception e) {
            System.err.println("❌ [CONTROLLER] Error in aumentarStock: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let Spring handle the error response
        }
    }

    /**
     * API: Disminuir stock de un producto (cantidad específica)
     */
    @PostMapping("/api/disminuir-stock")
    @ResponseBody
    @SuppressWarnings("CallToPrintStackTrace")
    public String disminuirStock(@RequestParam Integer idProducto,
                                @RequestParam Integer cantidad,
                                @RequestParam(required = false) String motivo,
                                Principal principal) {
        System.out.println("🔄 [CONTROLLER] disminuirStock called - idProducto: " + idProducto + ", cantidad: " + cantidad + ", motivo: " + motivo);
        System.out.println("🔄 [CONTROLLER] Principal: " + (principal != null ? principal.getName() : "NULL"));

        try {
            String usuarioActualizacion = principal != null ? principal.getName() : "SISTEMA";
            String motivoFinal = motivo != null ? motivo : "Disminución manual de stock";

            // Obtener inventario actual antes del cambio
            Inventario inventarioActual = inventarioService.findByProductoId(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para el producto"));

            // Capturar valores antes del cambio para el historial
            Integer stockAnterior = inventarioActual.getStock();

            System.out.println("🔄 [CONTROLLER] Calling inventarioService.ajustarStock with cantidad: " + (-cantidad));
            inventarioService.ajustarStock(idProducto, -cantidad, motivoFinal, usuarioActualizacion);

            // Obtener el nuevo stock después del cambio
            Integer stockNuevo = inventarioService.obtenerStockDisponible(idProducto);

            InventarioHistorial historial = new InventarioHistorial(
                inventarioActual,
                "DISMINUCION",
                stockAnterior,
                stockNuevo,
                -cantidad,
                motivoFinal,
                usuarioActualizacion
            );
            inventarioHistorialRepository.save(historial);

            System.out.println("✅ [CONTROLLER] disminuirStock completed successfully");
            return "OK";
        } catch (Exception e) {
            System.err.println("❌ [CONTROLLER] Error in disminuirStock: " + e.getMessage());
            e.printStackTrace();
            throw e; // Re-throw to let Spring handle the error response
        }
    }

    /**
     * API: Obtener información actualizada de un inventario en formato JSON por ID de producto
     */
    @GetMapping("/api/inventario/producto/{idProducto}")
    @ResponseBody
    public String obtenerInventarioInfoPorProducto(@PathVariable Integer idProducto) {
        try {
            Inventario inventario = inventarioService.findByProductoId(idProducto)
                .orElseThrow(() -> new IllegalArgumentException("Inventario no encontrado para el producto"));

            // Formatear fecha para JavaScript
            String fechaFormateada = inventario.getFechaActualizacion()
                .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            return String.format(
                "{\"idInventario\": %d, \"stock\": %d, \"estadoStock\": \"%s\", \"fechaActualizacion\": \"%s\", \"usuarioActualizacion\": \"%s\", \"agotado\": %b, \"stockCritico\": %b, \"necesitaReposicion\": %b}",
                inventario.getIdInventario(),
                inventario.getStock(),
                inventario.getEstadoStock(),
                fechaFormateada,
                inventario.getUsuarioActualizacion() != null ? inventario.getUsuarioActualizacion() : "Sistema",
                inventario.agotado(),
                inventario.stockCritico(),
                inventario.necesitaReposicion()
            );
        } catch (RuntimeException e) {
            return String.format("{\"error\": \"%s\"}", e.getMessage().replace("\"", "\\\""));
        }
    }

    /**
     * Exportar inventario a PDF
     */
    @GetMapping("/export/pdf")
    public org.springframework.http.ResponseEntity<byte[]> exportarInventarioPDF(
            @RequestParam(required = false) String filtro) {

        try {
            // Obtener inventarios según filtro
            List<Inventario> inventarios;
            String filtroActual = filtro != null ? filtro : "";
            inventarios = switch (filtroActual) {
                case "alertas" -> inventarioService.obtenerAlertasInventario();
                case "agotados" -> inventarioService.obtenerAgotados();
                case "stock-bajo" -> inventarioService.obtenerConStockBajo();
                default -> inventarioService.obtenerInventariosActivos();
            };

            // Generar PDF
            byte[] pdfBytes = exportService.exportarInventarioPDF(inventarios);

            // Configurar headers para descarga
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_PDF);
            headers.setContentDisposition(org.springframework.http.ContentDisposition.attachment()
                .filename("inventario_ecovivashop_" + java.time.LocalDate.now().toString() + ".pdf").build());

            return new org.springframework.http.ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (IOException | DocumentException e) {
            throw new RuntimeException("Error al generar PDF de inventario: " + e.getMessage());
        }
    }

    /**
     * Exportar inventario a Excel
     */
    @GetMapping("/export/excel")
    public org.springframework.http.ResponseEntity<byte[]> exportarInventarioExcel(
            @RequestParam(required = false) String filtro) {

        try {
            // Obtener inventarios según filtro
            List<Inventario> inventarios;
            String filtroActual = filtro != null ? filtro : "";
            inventarios = switch (filtroActual) {
                case "alertas" -> inventarioService.obtenerAlertasInventario();
                case "agotados" -> inventarioService.obtenerAgotados();
                case "stock-bajo" -> inventarioService.obtenerConStockBajo();
                default -> inventarioService.obtenerInventariosActivos();
            };

            // Generar Excel
            byte[] excelBytes = exportService.exportarInventarioExcel(inventarios);

            // Configurar headers para descarga
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDisposition(org.springframework.http.ContentDisposition.attachment()
                .filename("inventario_ecovivashop_" + java.time.LocalDate.now().toString() + ".xlsx").build());

            return new org.springframework.http.ResponseEntity<>(excelBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (IOException e) {
            throw new RuntimeException("Error al generar Excel de inventario: " + e.getMessage());
        }
    }

    /**
     * Exportar inventario a CSV
     */
    @GetMapping("/export/csv")
    public org.springframework.http.ResponseEntity<byte[]> exportarInventarioCSV(
            @RequestParam(required = false) String filtro) {

        try {
            // Obtener inventarios según filtro
            List<Inventario> inventarios;
            String filtroActual = filtro != null ? filtro : "";
            inventarios = switch (filtroActual) {
                case "alertas" -> inventarioService.obtenerAlertasInventario();
                case "agotados" -> inventarioService.obtenerAgotados();
                case "stock-bajo" -> inventarioService.obtenerConStockBajo();
                default -> inventarioService.obtenerInventariosActivos();
            };

            // Generar CSV
            byte[] csvBytes = exportService.exportarInventarioCSV(inventarios);

            // Configurar headers para descarga
            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.TEXT_PLAIN);
            headers.setContentDisposition(org.springframework.http.ContentDisposition.attachment()
                .filename("inventario_ecovivashop_" + java.time.LocalDate.now().toString() + ".csv").build());

            return new org.springframework.http.ResponseEntity<>(csvBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (IOException e) {
            throw new RuntimeException("Error al generar CSV de inventario: " + e.getMessage());
        }
    }

    /**
     * Método auxiliar para registrar cambios en el historial
     */
    private void registrarCambiosHistorial(Inventario anterior, Inventario nuevo, String usuario, String motivo) {
        // Registrar cambio de stock si cambió
        if (!anterior.getStock().equals(nuevo.getStock())) {
            int cambioCantidad = nuevo.getStock() - anterior.getStock();
            String tipoCambio = cambioCantidad > 0 ? "AUMENTO" : "DISMINUCION";
            InventarioHistorial historialStock = new InventarioHistorial(
                anterior,
                tipoCambio,
                anterior.getStock(),
                nuevo.getStock(),
                cambioCantidad,
                motivo != null ? motivo : "Actualización manual de stock",
                usuario
            );
            inventarioHistorialRepository.save(historialStock);
        }

        // Registrar cambio de configuración si cambió (stock mínimo, máximo o ubicación)
        boolean configCambio = !anterior.getStockMinimo().equals(nuevo.getStockMinimo()) ||
                              !anterior.getStockMaximo().equals(nuevo.getStockMaximo()) ||
                              !java.util.Objects.equals(anterior.getUbicacion(), nuevo.getUbicacion());

        if (configCambio) {
            InventarioHistorial historialConfig = new InventarioHistorial(
                anterior,
                "ACTUALIZACION",
                anterior.getStockMinimo(),
                nuevo.getStockMinimo(),
                anterior.getStockMaximo(),
                nuevo.getStockMaximo(),
                anterior.getUbicacion(),
                nuevo.getUbicacion(),
                motivo != null ? motivo : "Actualización de configuración de inventario",
                usuario
            );
            inventarioHistorialRepository.save(historialConfig);
        }
    }
}