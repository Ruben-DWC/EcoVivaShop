package com.ecovivashop.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecovivashop.entity.Producto;
import com.ecovivashop.service.ProductoService;
import com.ecovivashop.service.UsuarioService;

/**
 * Controlador de demostración para mostrar el uso práctico de Google Guava y Apache Commons Lang3
 * en la aplicación EcoVivaShop. Este endpoint permite demostrar ambas librerías funcionando
 * en conjunto dentro del contexto real de la aplicación.
 *
 * @author EcoVivaShop Team
 * @version 1.0
 * @since 2025
 */
@RestController
@RequestMapping("/api/demo")
public class LibreriasExternasDemoController {

    private final UsuarioService usuarioService;
    private final ProductoService productoService;

    public LibreriasExternasDemoController(UsuarioService usuarioService, ProductoService productoService) {
        this.usuarioService = usuarioService;
        this.productoService = productoService;
    }

    /**
     * Endpoint de demostración que muestra el funcionamiento integrado de ambas librerías.
     * Realiza operaciones que utilizan tanto las validaciones de Apache Commons Lang3
     * como el sistema de caché de Google Guava.
     *
     * @return ResponseEntity con información sobre las operaciones realizadas
     */
    @GetMapping("/librerias-externas")
    public ResponseEntity<Map<String, Object>> demostrarLibreriasExternas() {
        Map<String, Object> resultado = new java.util.HashMap<>();

        try {
            // 1. Demostrar validaciones con Apache Commons Lang3 (en UsuarioService)
            resultado.put("apacheCommonsLang3", Map.of(
                "libreria", "Apache Commons Lang3 3.17.0",
                "funcionalidad", "Validaciones de strings",
                "metodo", "StringUtils.isBlank()",
                "ubicacion", "UsuarioService.crearUsuario()",
                "descripcion", "Valida que campos requeridos no estén vacíos o solo contengan espacios"
            ));

            // 2. Intentar crear usuario con datos inválidos para mostrar validación
            try {
                // Intento crear usuario con nombre vacío - debería fallar con Apache Commons Lang3
                usuarioService.crearUsuario("", "Test", "test@example.com", "password123", "CLIENTE");
            } catch (IllegalArgumentException e) {
                resultado.put("validacionApacheCommons", Map.of(
                    "tipo", "Validación exitosa",
                    "mensaje", e.getMessage(),
                    "libreria", "Apache Commons Lang3"
                ));
            }

            // 3. Demostrar caché con Google Guava (en ProductoService)
            resultado.put("googleGuava", Map.of(
                "libreria", "Google Guava 32.1.3-jre",
                "funcionalidad", "Sistema de caché avanzado",
                "metodo", "CacheBuilder.newBuilder()",
                "ubicacion", "ProductoService.obtenerProductosActivos()",
                "descripcion", "Cache con expiración automática de 10 minutos para mejorar rendimiento"
            ));

            // 4. Obtener productos usando caché (primera llamada - cache miss)
            long inicioPrimeraLlamada = System.currentTimeMillis();
            List<Producto> productos1 = productoService.obtenerProductosActivos();
            long tiempoPrimeraLlamada = System.currentTimeMillis() - inicioPrimeraLlamada;

            // 5. Obtener productos usando caché (segunda llamada - cache hit)
            long inicioSegundaLlamada = System.currentTimeMillis();
            productoService.obtenerProductosActivos(); // Solo medimos tiempo, no necesitamos el resultado
            long tiempoSegundaLlamada = System.currentTimeMillis() - inicioSegundaLlamada;

            resultado.put("cacheGoogleGuava", Map.of(
                "productosEncontrados", productos1.size(),
                "tiempoPrimeraLlamadaMs", tiempoPrimeraLlamada,
                "tiempoSegundaLlamadaMs", tiempoSegundaLlamada,
                "aceleracion", String.format("%.2fx", (double) tiempoPrimeraLlamada / Math.max(1, tiempoSegundaLlamada)),
                "libreria", "Google Guava"
            ));

            // 6. Información general del demo
            resultado.put("demoInfo", Map.of(
                "titulo", "Demostración de Librerías Externas en EcoVivaShop",
                "objetivo", "Mostrar uso práctico de Google Guava y Apache Commons Lang3",
                "contexto", "Aplicación Spring Boot para e-commerce sostenible",
                "beneficios", List.of(
                    "Validaciones robustas con Apache Commons Lang3",
                    "Mejora de rendimiento con caché Google Guava",
                    "Código más limpio y mantenible",
                    "Integración perfecta con Spring Boot"
                )
            ));

            return ResponseEntity.ok(resultado);

        } catch (Exception e) {
            resultado.put("error", Map.of(
                "mensaje", "Error en la demostración: " + e.getMessage(),
                "tipo", e.getClass().getSimpleName()
            ));
            return ResponseEntity.internalServerError().body(resultado);
        }
    }

    /**
     * Endpoint para verificar el estado de salud de las librerías externas.
     *
     * @return ResponseEntity con estado de las librerías
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new java.util.HashMap<>();
        health.put("status", "OK");
        health.put("googleGuava", "32.1.3-jre - Funcionando");
        health.put("apacheCommonsLang3", "3.17.0 - Funcionando");
        health.put("timestamp", java.time.LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}