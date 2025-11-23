package com.ecovivashop.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.service.PedidoService;

@Controller
@RequestMapping("/admin/reportes")
@PreAuthorize("hasRole('ADMIN')")
public class ReporteController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping("/pedidos/csv")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<String> exportarPedidosCSV() {
        try {
            List<Pedido> pedidos = pedidoService.findAll();
            
            StringBuilder csv = new StringBuilder();
            
            // Headers
            csv.append("ID,Cliente,Email,Fecha,Estado,Total,Productos\n");
            
            // Data
            for (Pedido pedido : pedidos) {
                csv.append(pedido.getIdPedido()).append(",");
                csv.append("\"").append(pedido.getUsuario().getNombre()).append(" ")
                   .append(pedido.getUsuario().getApellido()).append("\"").append(",");
                csv.append("\"").append(pedido.getUsuario().getEmail()).append("\"").append(",");
                csv.append(pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append(",");
                csv.append("\"").append(pedido.getEstado()).append("\"").append(",");
                csv.append(pedido.getTotal()).append(",");
                
                // Productos del pedido
                StringBuilder productos = new StringBuilder();
                if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                    pedido.getDetalles().forEach(detalle -> {
                        if (productos.length() > 0) productos.append("; ");
                        productos.append(detalle.getProducto().getNombre())
                                .append(" (x").append(detalle.getCantidad()).append(")");
                    });
                }
                csv.append("\"").append(productos.toString()).append("\"");
                
                csv.append("\n");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("pedidos_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv").build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csv.toString());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar CSV: " + e.getMessage());
        }
    }

    @GetMapping("/pedidos/html")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<String> exportarPedidosHTML() {
        try {
            List<Pedido> pedidos = pedidoService.findAll();
            
            StringBuilder html = new StringBuilder();
            html.append("<!DOCTYPE html>");
            html.append("<html><head>");
            html.append("<meta charset='UTF-8'>");
            html.append("<title>Reporte de Pedidos - EcoVivaShop</title>");
            html.append("<style>");
            html.append("body { font-family: Arial, sans-serif; margin: 20px; }");
            html.append("table { border-collapse: collapse; width: 100%; margin-top: 20px; }");
            html.append("th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }");
            html.append("th { background-color: #28a745; color: white; }");
            html.append("tr:nth-child(even) { background-color: #f9f9f9; }");
            html.append(".header { background-color: #28a745; color: white; padding: 20px; text-align: center; margin-bottom: 20px; }");
            html.append("</style>");
            html.append("</head><body>");
            
            html.append("<div class='header'>");
            html.append("<h1>EcoVivaShop - Reporte de Pedidos</h1>");
            html.append("<p>Generado el: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("</p>");
            html.append("<p>Total de pedidos: ").append(pedidos.size()).append("</p>");
            html.append("</div>");
            
            html.append("<table>");
            html.append("<thead>");
            html.append("<tr>");
            html.append("<th>ID</th>");
            html.append("<th>Cliente</th>");
            html.append("<th>Email</th>");
            html.append("<th>Fecha</th>");
            html.append("<th>Estado</th>");
            html.append("<th>Total</th>");
            html.append("<th>Productos</th>");
            html.append("</tr>");
            html.append("</thead>");
            html.append("<tbody>");
            
            for (Pedido pedido : pedidos) {
                html.append("<tr>");
                html.append("<td>").append(pedido.getIdPedido()).append("</td>");
                html.append("<td>").append(pedido.getUsuario().getNombre()).append(" ")
                    .append(pedido.getUsuario().getApellido()).append("</td>");
                html.append("<td>").append(pedido.getUsuario().getEmail()).append("</td>");
                html.append("<td>").append(pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("</td>");
                html.append("<td>").append(pedido.getEstado()).append("</td>");
                html.append("<td>S/ ").append(String.format("%.2f", pedido.getTotal())).append("</td>");
                
                html.append("<td>");
                StringBuilder productos = new StringBuilder();
                if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                    pedido.getDetalles().forEach(detalle -> {
                        if (productos.length() > 0) productos.append("<br>");
                        productos.append(detalle.getProducto().getNombre())
                                .append(" (x").append(detalle.getCantidad()).append(")");
                    });
                }
                html.append(productos.toString());
                html.append("</td>");
                
                html.append("</tr>");
            }
            
            html.append("</tbody>");
            html.append("</table>");
            html.append("</body></html>");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_HTML);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("reporte_pedidos_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".html").build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(html.toString());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar HTML: " + e.getMessage());
        }
    }

    @GetMapping("/pedidos/txt")
    @SuppressWarnings("CallToPrintStackTrace")
    public ResponseEntity<String> exportarPedidosTXT() {
        try {
            List<Pedido> pedidos = pedidoService.findAll();
            
            StringBuilder txt = new StringBuilder();
            txt.append("===========================================\n");
            txt.append("    ECOVIVASHOP - REPORTE DE PEDIDOS\n");
            txt.append("===========================================\n");
            txt.append("Generado el: ").append(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
            txt.append("Total de pedidos: ").append(pedidos.size()).append("\n");
            txt.append("===========================================\n\n");
            
            for (Pedido pedido : pedidos) {
                txt.append("PEDIDO #").append(pedido.getIdPedido()).append("\n");
                txt.append("Cliente: ").append(pedido.getUsuario().getNombre()).append(" ")
                   .append(pedido.getUsuario().getApellido()).append("\n");
                txt.append("Email: ").append(pedido.getUsuario().getEmail()).append("\n");
                txt.append("Fecha: ").append(pedido.getFechaPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))).append("\n");
                txt.append("Estado: ").append(pedido.getEstado()).append("\n");
                txt.append("Total: S/ ").append(String.format("%.2f", pedido.getTotal())).append("\n");
                txt.append("Productos:\n");
                
                if (pedido.getDetalles() != null && !pedido.getDetalles().isEmpty()) {
                    pedido.getDetalles().forEach(detalle -> {
                        txt.append("  - ").append(detalle.getProducto().getNombre())
                           .append(" (Cantidad: ").append(detalle.getCantidad())
                           .append(", Precio: S/ ").append(String.format("%.2f", detalle.getPrecioUnitario()))
                           .append(")\n");
                    });
                } else {
                    txt.append("  - Sin productos\n");
                }
                
                txt.append("-".repeat(50)).append("\n\n");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("pedidos_" + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".txt").build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(txt.toString());
                    
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar TXT: " + e.getMessage());
        }
    }

    @GetMapping("/test-simple")
    public ResponseEntity<String> testSimple() {
        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Controlador de reportes funcionando correctamente - " + 
                          LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/prueba")
    public String mostrarPaginaPrueba() {
        return "admin/reportes-prueba";
    }

    @GetMapping("/simple-csv")
    public ResponseEntity<String> exportSimpleCSV() {
        try {
            List<Pedido> pedidos = pedidoService.findAll();
            
            StringBuilder csv = new StringBuilder();
            csv.append("ID,Cliente,Email,Estado,Total\n");
            
            for (Pedido pedido : pedidos) {
                csv.append(pedido.getIdPedido()).append(",");
                csv.append(pedido.getUsuario().getNombre()).append(",");
                csv.append(pedido.getUsuario().getEmail()).append(",");
                csv.append(pedido.getEstado()).append(",");
                csv.append(pedido.getTotal()).append("\n");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.TEXT_PLAIN);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("pedidos_simple.csv").build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csv.toString());
                    
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }
}