package com.ecovivashop.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ecovivashop.service.ProductoDataService;

@Controller
@RequestMapping("/admin/data")
public class DataController extends BaseAdminController {
    
    @Autowired
    private ProductoDataService productoDataService;
    
    /**
     * Página para gestionar datos de ejemplo
     */
    @GetMapping("/productos")
    public String gestionarDatos(Model model) {
        boolean hayProductos = productoDataService.hayProductos();
        model.addAttribute("hayProductos", hayProductos);
        return "admin/data-productos";
    }
    
    /**
     * Crea productos de ejemplo con imágenes
     */
    @PostMapping("/productos/crear")
    @ResponseBody
    public ResponseEntity<String> crearProductosEjemplo() {
        try {
            if (productoDataService.hayProductos()) {
                return ResponseEntity.badRequest().body("Ya existen productos en la base de datos");
            }
            
            productoDataService.crearProductosDeEjemplo();
            return ResponseEntity.ok("Productos de ejemplo creados exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al crear productos: " + e.getMessage());
        }
    }
    
    /**
     * Elimina todos los productos de ejemplo
     */
    @DeleteMapping("/productos/eliminar")
    @ResponseBody
    public ResponseEntity<String> eliminarProductosEjemplo() {
        try {
            productoDataService.eliminarProductosDeEjemplo();
            return ResponseEntity.ok("Productos de ejemplo eliminados exitosamente");
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar productos: " + e.getMessage());
        }
    }
    
    /**
     * Verifica el estado de los productos
     */
    @GetMapping("/productos/estado")
    @ResponseBody
    public ResponseEntity<String> verificarEstadoProductos() {
        try {
            boolean hayProductos = productoDataService.hayProductos();
            long totalProductos = productoDataService.hayProductos() ? 1 : 0; // Simplified check
            
            return ResponseEntity.ok(String.format("Hay productos: %s. Total aproximado: %d", 
                                                 hayProductos ? "Sí" : "No", totalProductos));
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al verificar estado: " + e.getMessage());
        }
    }
}
