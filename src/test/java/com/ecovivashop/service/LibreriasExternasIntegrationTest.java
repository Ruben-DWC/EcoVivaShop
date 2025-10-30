package com.ecovivashop.service;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Producto;

/**
 * Prueba de integración que demuestra el uso combinado de:
 * - Apache Commons Lang3 para validaciones en UsuarioService
 * - Google Guava para caché en ProductoService
 *
 * Esta prueba integra ambos servicios para mostrar cómo las librerías
 * externas mejoran la funcionalidad y rendimiento de la aplicación.
 *
 * NOTA: Esta prueba requiere una base de datos de prueba configurada.
 * Si no hay datos en la BD, algunas pruebas pueden fallar.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class LibreriasExternasIntegrationTest {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoService productoService;

    /**
     * Prueba de integración que combina validaciones de Apache Commons Lang3
     * con caché de Google Guava.
     *
     * Escenario: Un usuario intenta crear una cuenta con datos inválidos
     * y luego consulta productos, demostrando ambas librerías en acción.
     */
    @Test
    void testValidacionesYCacheIntegrados() {
        // Test: Validación con Apache Commons Lang3 (debe fallar por nombre vacío)
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("", "Apellido", "test@example.com", "password123", "CLIENTE");
        });

        // Verificar validación de Apache Commons Lang3
        assertEquals("El nombre no puede estar vacío (Apache Commons Lang3)", exception.getMessage());

        // Test: Caché con Google Guava - consultar productos activos
        // Esta llamada puede devolver una lista vacía si no hay productos en BD de test
        List<Producto> productos = productoService.obtenerProductosActivos();

        // Verificar que se retorna una lista (puede estar vacía)
        assertNotNull(productos);
    }

    /**
     * Prueba que demuestra las validaciones de Apache Commons Lang3
     * en diferentes escenarios de entrada inválida.
     */
    @Test
    void testValidacionesApacheCommonsLang3() {
        // Test: Nombre vacío
        IllegalArgumentException exception1 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("", "Apellido", "test@example.com", "password123", "CLIENTE");
        });
        assertEquals("El nombre no puede estar vacío (Apache Commons Lang3)", exception1.getMessage());

        // Test: Nombre solo espacios
        IllegalArgumentException exception2 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("   ", "Apellido", "test@example.com", "password123", "CLIENTE");
        });
        assertEquals("El nombre no puede estar vacío (Apache Commons Lang3)", exception2.getMessage());

        // Test: Email vacío
        IllegalArgumentException exception3 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("Usuario", "Apellido", "", "password123", "CLIENTE");
        });
        assertEquals("El email no puede estar vacío (Apache Commons Lang3)", exception3.getMessage());

        // Test: Email solo espacios
        IllegalArgumentException exception4 = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("Usuario", "Apellido", "   ", "password123", "CLIENTE");
        });
        assertEquals("El email no puede estar vacío (Apache Commons Lang3)", exception4.getMessage());
    }

    /**
     * Prueba que demuestra el funcionamiento del caché de Google Guava
     * en ProductoService - verifica que el método funciona correctamente.
     */
    @Test
    void testCacheGoogleGuava() {
        // Esta prueba verifica que el método obtenerProductosActivos funciona
        // y utiliza el caché de Google Guava configurado en ProductoService
        List<Producto> productos = productoService.obtenerProductosActivos();

        // Verificar que se retorna una lista (puede estar vacía en BD de test)
        assertNotNull(productos);

        // Si hay productos, verificar que todos están activos
        if (!productos.isEmpty()) {
            for (Producto producto : productos) {
                assertTrue(producto.getEstado(), "Todos los productos deben estar activos");
            }
        }
    }

    /**
     * Prueba de integración completa: Validación + Caché
     * Demuestra el flujo completo de una operación de negocio.
     */
    @Test
    void testFlujoCompletoIntegracion() {
        // Test: Validación con Apache Commons Lang3
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario("", "Apellido", "test@example.com", "password123", "CLIENTE");
        });
        assertEquals("El nombre no puede estar vacío (Apache Commons Lang3)", exception.getMessage());

        // Test: Caché con Google Guava
        List<Producto> productos = productoService.obtenerProductosActivos();
        assertNotNull(productos);

        // Verificar que todos los productos están activos (si existen)
        if (!productos.isEmpty()) {
            for (Producto producto : productos) {
                assertTrue(producto.getEstado(), "Todos los productos deben estar activos");
            }
        }
    }
}