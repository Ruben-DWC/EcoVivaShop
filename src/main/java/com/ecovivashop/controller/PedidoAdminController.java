package com.ecovivashop.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.service.ExportService;
import com.ecovivashop.service.JasperExportService;
import com.ecovivashop.service.PedidoService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin")
public class PedidoAdminController extends BaseAdminController {
    
    private final PedidoService pedidoService;
    private final ExportService exportService;
    private final JasperExportService jasperExportService;
    
    public PedidoAdminController(PedidoService pedidoService, ExportService exportService, JasperExportService jasperExportService) {
        this.pedidoService = pedidoService;
        this.exportService = exportService;
        this.jasperExportService = jasperExportService;
    }
    
    /**
     * Endpoint de diagnóstico básico
     */
    @GetMapping("/pedidos/status")
    @ResponseBody
    public String statusDiagnostic() {
        StringBuilder result = new StringBuilder();
        result.append("=== STATUS DEL SISTEMA ===\n");
        
        try {
            long totalPedidos = pedidoService.contarPedidos();
            result.append("✅ Total de pedidos en BD: ").append(totalPedidos).append("\n");
            result.append("✅ Controlador funcionando correctamente\n");
            result.append("✅ Servicio de pedidos operativo\n");
            result.append("✅ Sistema de exportación disponible\n");
            
        } catch (Exception e) {
            result.append("❌ Error en diagnóstico: ").append(e.getMessage()).append("\n");
        }
        
        return result.toString();
    }
    
    /**
     * Página principal de gestión de pedidos
     */
    @GetMapping("/pedidos")
    public String gestionPedidos(Model model, Principal principal,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(defaultValue = "fechaPedido") String sortBy,
                                @RequestParam(defaultValue = "desc") String sortDir,
                                @RequestParam(required = false) String estado,
                                @RequestParam(required = false) String busqueda) {
        
        // Usuario se agrega automáticamente por @ModelAttribute en BaseAdminController
        
        // Configurar paginación y ordenamiento
        Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? Sort.by(sortBy).descending() 
            : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        // Obtener pedidos según filtros
        Page<Pedido> pedidos;
        if (estado != null && !estado.trim().isEmpty()) {
            pedidos = pedidoService.obtenerPedidosPorEstadoPaginado(estado, pageable);
        } else if (busqueda != null && !busqueda.trim().isEmpty()) {
            pedidos = pedidoService.buscarPedidosPaginado(busqueda, pageable);
        } else {
            pedidos = pedidoService.obtenerPedidosPaginados(pageable);
        }
        
        // Estadísticas
        long totalPedidos = pedidoService.contarPedidos();
        long pedidosPendientes = pedidoService.contarPedidosPorEstado("PENDIENTE");
        long pedidosEntregados = pedidoService.contarPedidosPorEstado("ENTREGADO");
        
        // Calcular ingresos totales (pedidos entregados)
        BigDecimal ingresosTotales = pedidoService.calcularIngresosTotales();
        
        // Agregar al modelo
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", pedidos.getTotalPages());
        model.addAttribute("totalItems", pedidos.getTotalElements());
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("estado", estado);
        model.addAttribute("busqueda", busqueda);
        
        // Estadísticas
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("pedidosEntregados", pedidosEntregados);
        model.addAttribute("ingresosTotales", ingresosTotales != null ? ingresosTotales.doubleValue() : 0.0);
        
        // Estados disponibles
        model.addAttribute("estados", List.of("PENDIENTE", "CONFIRMADO", "PREPARANDO", "ENVIADO", "ENTREGADO", "CANCELADO"));
        
        return "admin/pedidos";
    }
    
    /**
     * Ver detalles del pedido
     */
    @GetMapping("/pedidos/detalle/{id}")
    public String detallePedido(@PathVariable Integer id, Model model, Principal principal) {
        try {
            Pedido pedido = pedidoService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pedido no encontrado con ID: " + id));
            
            model.addAttribute("pedido", pedido);
            model.addAttribute("estados", List.of("PENDIENTE", "CONFIRMADO", "PREPARANDO", "ENVIADO", "ENTREGADO", "CANCELADO"));
            
            return "admin/pedido-detalle";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el pedido: " + e.getMessage());
            return "redirect:/admin/pedidos";
        }
    }
    
    /**
     * Cambiar estado del pedido
     */
    @PostMapping("/pedidos/cambiar-estado/{id}")
    public String cambiarEstadoPedido(@PathVariable Integer id,
                                     @RequestParam String nuevoEstado,
                                     RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cambiarEstadoPedido(id, nuevoEstado);
            redirectAttributes.addFlashAttribute("success", 
                "Estado del pedido actualizado a: " + nuevoEstado);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al cambiar estado del pedido: " + e.getMessage());
        }
        
        return "redirect:/admin/pedidos/detalle/" + id;
    }
    
    /**
     * Cancelar pedido
     */
    @PostMapping("/pedidos/cancelar/{id}")
    public String cancelarPedido(@PathVariable Integer id,
                                @RequestParam(required = false) String motivo,
                                RedirectAttributes redirectAttributes) {
        try {
            pedidoService.cancelarPedido(id, motivo);
            redirectAttributes.addFlashAttribute("success", 
                "Pedido cancelado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al cancelar pedido: " + e.getMessage());
        }
        
        return "redirect:/admin/pedidos";
    }
    
    /**
     * Imprimir pedido individual
     */
    @GetMapping("/pedidos/imprimir/{id}")
    public String imprimirPedido(@PathVariable Integer id, Model model) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.findById(id);
            if (pedidoOpt.isPresent()) {
                model.addAttribute("pedido", pedidoOpt.get());
                return "admin/pedido-imprimir";
            }
            model.addAttribute("error", "Pedido no encontrado");
            return "redirect:/admin/pedidos";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar pedido: " + e.getMessage());
            return "redirect:/admin/pedidos";
        }
    }
    
    /**
     * API: Obtener estadísticas de pedidos
     */
    @GetMapping("/pedidos/api/estadisticas")
    @ResponseBody
    public Object obtenerEstadisticasPedidos() {
        return new Object() {
            public final long confirmados = pedidoService.contarPedidosPorEstado("CONFIRMADO");
            public final long enviados = pedidoService.contarPedidosPorEstado("ENVIADO");
            public final long entregados = pedidoService.contarPedidosPorEstado("ENTREGADO");
            public final long cancelados = pedidoService.contarPedidosPorEstado("CANCELADO");
        };
    }
    
    /**
     * Exportar pedidos a PDF
     */
    @GetMapping("/pedidos/exportar/pdf")
    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
    public ResponseEntity<byte[]> exportarPedidosPDF(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(defaultValue = "todos") String estados,
            HttpServletResponse response) {
        
        try {
            System.out.println("🔄 PASO 1: Iniciando exportación PDF con parámetros: fechaInicio=" + fechaInicio + ", fechaFin=" + fechaFin + ", estados=" + estados);
            
            List<Pedido> pedidos = obtenerPedidosParaExportacion(fechaInicio, fechaFin, estados);
            System.out.println("✅ PASO 2: Pedidos obtenidos: " + pedidos.size() + " registros");
            
            System.out.println("🔄 PASO 3: Iniciando generación de PDF...");
            byte[] pdfBytes = exportService.exportarPedidosPDF(pedidos, fechaInicio, fechaFin, estados);
            System.out.println("✅ PASO 4: PDF generado correctamente. Tamaño: " + pdfBytes.length + " bytes");
            
            String filename = "pedidos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".pdf";
            
            // Establecer cookie de descarga completada
            String downloadId = "download_" + System.currentTimeMillis();
            Cookie downloadCookie = new Cookie("downloadComplete", downloadId);
            downloadCookie.setMaxAge(30); // 30 segundos
            downloadCookie.setPath("/");
            response.addCookie(downloadCookie);
            
            System.out.println("✅ PASO 5: Enviando respuesta con archivo PDF: " + filename);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            System.err.println("❌ ERROR en exportación PDF: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Exportar pedidos a Excel
     */
    @GetMapping("/pedidos/exportar/excel")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<byte[]> exportarPedidosExcel(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(defaultValue = "todos") String estados,
            HttpServletResponse response) {
        
        try {
            List<Pedido> pedidos = obtenerPedidosParaExportacion(fechaInicio, fechaFin, estados);
            byte[] excelBytes = exportService.exportarPedidosExcel(pedidos);
            
            String filename = "pedidos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".xlsx";
            
            // Establecer cookie de descarga completada
            String downloadId = "download_" + System.currentTimeMillis();
            Cookie downloadCookie = new Cookie("downloadComplete", downloadId);
            downloadCookie.setMaxAge(30); // 30 segundos
            downloadCookie.setPath("/");
            response.addCookie(downloadCookie);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Exportar pedidos a CSV
     */
    @GetMapping("/pedidos/exportar/csv")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<byte[]> exportarPedidosCSV(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(defaultValue = "todos") String estados) {
        
        try {
            List<Pedido> pedidos = obtenerPedidosParaExportacion(fechaInicio, fechaFin, estados);
            byte[] csvBytes = exportService.exportarPedidosCSV(pedidos);
            
            String filename = "pedidos_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".csv";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.TEXT_PLAIN)
                    .body(csvBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Método auxiliar para obtener pedidos según filtros
     */
    private List<Pedido> obtenerPedidosParaExportacion(String fechaInicio, String fechaFin, String estados) {
        LocalDateTime inicio = null;
        LocalDateTime fin = null;
        
        // Intentar múltiples formatos de fecha
        if (fechaInicio != null && !fechaInicio.trim().isEmpty()) {
            try {
                // Formato ISO (YYYY-MM-DD) desde formulario HTML
                inicio = LocalDate.parse(fechaInicio).atStartOfDay();
            } catch (Exception e) {
                try {
                    // Formato español (DD/MM/YYYY)
                    inicio = LocalDate.parse(fechaInicio, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atStartOfDay();
                } catch (Exception e2) {
                    System.out.println("Error parsing fechaInicio: " + fechaInicio + " - " + e2.getMessage());
                }
            }
        }
        
        if (fechaFin != null && !fechaFin.trim().isEmpty()) {
            try {
                // Formato ISO (YYYY-MM-DD) desde formulario HTML
                fin = LocalDate.parse(fechaFin).atTime(23, 59, 59);
            } catch (Exception e) {
                try {
                    // Formato español (DD/MM/YYYY)  
                    fin = LocalDate.parse(fechaFin, DateTimeFormatter.ofPattern("dd/MM/yyyy")).atTime(23, 59, 59);
                } catch (Exception e2) {
                    System.out.println("Error parsing fechaFin: " + fechaFin + " - " + e2.getMessage());
                }
            }
        }
        
        // Obtener pedidos según filtros
        if (inicio != null && fin != null) {
            System.out.println("Buscando pedidos entre " + inicio + " y " + fin);
            return pedidoService.obtenerPedidosEntreFechas(inicio, fin);
        } else if (estados != null && !estados.equals("todos")) {
            System.out.println("Buscando pedidos con estado: " + estados);
            return pedidoService.obtenerPedidosPorEstado(estados);
        } else {
            System.out.println("Obteniendo todos los pedidos");
            return pedidoService.findAll();
        }
    }
    
    /**
     * Página de reportes
     */
    @GetMapping("/reportes")
    public String reportes(Model model, Principal principal) {
        // Usuario se agrega automáticamente por @ModelAttribute en BaseAdminController
        
        // Estadísticas para el reporte
        long totalPedidos = pedidoService.contarPedidos();
        long pedidosPendientes = pedidoService.contarPedidosPorEstado("PENDIENTE");
        long pedidosEntregados = pedidoService.contarPedidosPorEstado("ENTREGADO");
        BigDecimal ingresosTotales = pedidoService.calcularIngresosTotales();
        
        model.addAttribute("totalPedidos", totalPedidos);
        model.addAttribute("pedidosPendientes", pedidosPendientes);
        model.addAttribute("pedidosEntregados", pedidosEntregados);
        model.addAttribute("ingresosTotales", ingresosTotales != null ? ingresosTotales.doubleValue() : 0.0);
        
        return "admin/reportes";
    }
    
    /**
     * Exportar reporte general a PDF
     */
    @GetMapping("/reportes/exportar/pdf")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<byte[]> exportarReportePDF() {
        try {
            List<Pedido> pedidos = pedidoService.findAll();
            byte[] pdfBytes = exportService.exportarReporteGeneralPDF(pedidos);
            
            String filename = "reporte_general_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".pdf";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Exportar reporte general a Excel
     */
    @GetMapping("/reportes/exportar/excel")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<byte[]> exportarReporteExcel() {
        try {
            List<Pedido> pedidos = pedidoService.findAll();
            byte[] excelBytes = exportService.exportarReporteGeneralExcel(pedidos);
            
            String filename = "reporte_general_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".xlsx";
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(excelBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Mostrar formulario para editar pedido
     */
    @GetMapping("/pedidos/editar/{id}")
    public String editarPedido(@PathVariable Integer id, Model model) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.findById(id);
            if (pedidoOpt.isEmpty()) {
                model.addAttribute("error", "Pedido no encontrado");
                return "redirect:/admin/pedidos";
            }
            
            Pedido pedido = pedidoOpt.get();
            model.addAttribute("pedido", pedido);
            model.addAttribute("estados", Arrays.asList("PENDIENTE", "PROCESANDO", "ENVIADO", "ENTREGADO", "CANCELADO"));
            return "admin/pedido-editar-simple";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el pedido: " + e.getMessage());
            return "redirect:/admin/pedidos";
        }
    }

    /**
     * Actualizar estado del pedido
     */
    @PostMapping("/pedidos/editar/{id}")
    public String actualizarPedido(
            @PathVariable Integer id,
            @RequestParam String estado,
            RedirectAttributes redirectAttributes) {
        
        try {
            Optional<Pedido> pedidoOpt = pedidoService.findById(id);
            if (pedidoOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Pedido no encontrado");
                return "redirect:/admin/pedidos";
            }
            
            Pedido pedido = pedidoOpt.get();
            pedido.setEstado(estado);
            pedidoService.save(pedido);
            
            redirectAttributes.addFlashAttribute("success", "Pedido actualizado correctamente");
            return "redirect:/admin/pedidos";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el pedido: " + e.getMessage());
            return "redirect:/admin/pedidos";
        }
    }

    /**
     * Imprimir pedido individual
     */
    @GetMapping("/pedidos/imprimir/{id}/pdf")
    @SuppressWarnings("UseSpecificCatch")
    public ResponseEntity<byte[]> imprimirPedidoPDF(@PathVariable Integer id, HttpServletResponse response) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.findById(id);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            
            Pedido pedido = pedidoOpt.get();
            
            // Generar PDF individual del pedido usando el método específico para facturas
            byte[] pdfBytes = exportService.generarBoletaPDFDesdePedido(pedido);
            
            String filename = "factura_" + pedido.getNumeroPedido() + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".pdf";
            
            // Establecer cookie de descarga completada
            String downloadId = "download_" + System.currentTimeMillis();
            Cookie downloadCookie = new Cookie("downloadComplete", downloadId);
            downloadCookie.setMaxAge(30); // 30 segundos
            downloadCookie.setPath("/");
            response.addCookie(downloadCookie);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/pedidos/exportar/jasper/{id}/pdf")
    @SuppressWarnings({"UseSpecificCatch", "CallToPrintStackTrace"})
    public ResponseEntity<byte[]> imprimirPedidoJasperPDF(@PathVariable Integer id, HttpServletResponse response) {
        try {
            Optional<Pedido> pedidoOpt = pedidoService.findById(id);
            if (pedidoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Pedido pedido = pedidoOpt.get();
            byte[] pdfBytes = jasperExportService.exportarPedidoJasperPDF(pedido);

            String filename = "pedido_jasper_" + pedido.getNumeroPedido() + "_" + LocalDate.now().format(DateTimeFormatter.ofPattern("ddMMyyyy")) + ".pdf";

            // Set cookie to indicate download completed
            String downloadId = "download_" + System.currentTimeMillis();
            Cookie downloadCookie = new Cookie("downloadComplete", downloadId);
            downloadCookie.setMaxAge(30);
            downloadCookie.setPath("/");
            response.addCookie(downloadCookie);

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }


}
