package com.ecovivashop.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.ContentDisposition;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.service.PedidoService;

@Controller
@RequestMapping("/admin/test")
public class TestExportController {
    
    private final PedidoService pedidoService;
    
    public TestExportController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }
    
    @GetMapping("/export-simple")
    public ResponseEntity<String> testExportSimple(Principal principal,
                                                   @RequestParam(required = false) String fechaInicio,
                                                   @RequestParam(required = false) String fechaFin,
                                                   @RequestParam(defaultValue = "todos") String estados) {
        try {
            // Obtener pedidos
            List<Pedido> pedidos = pedidoService.findAll();
            
            // Crear CSV simple
            StringBuilder csv = new StringBuilder();
            csv.append("ID,Cliente,Fecha,Estado,Total\n");
            
            for (Pedido pedido : pedidos) {
                csv.append(pedido.getIdPedido()).append(",");
                csv.append(pedido.getUsuario() != null ? 
                    pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido() : "N/A").append(",");
                csv.append(pedido.getFechaPedido()).append(",");
                csv.append(pedido.getEstado()).append(",");
                csv.append(pedido.getTotal()).append("\n");
            }
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDisposition(ContentDisposition.builder("attachment").filename("test-pedidos.csv").build());
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csv.toString());
                    
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error: " + e.getMessage());
        }
    }
}
