package com.ecovivashop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ProductoTest {

    @Test
    void testDefaultConstructor() {
        Producto nuevoProducto = new Producto();

        assertNull(nuevoProducto.getIdProducto());
        assertNull(nuevoProducto.getNombre());
        assertNull(nuevoProducto.getDescripcion());
        assertNull(nuevoProducto.getPrecio());
        assertNull(nuevoProducto.getCategoria());
        assertTrue(nuevoProducto.getEstado());
        assertNotNull(nuevoProducto.getFechaCreacion());
    }

    @Test
    void testCustomConstructor() {
        Producto nuevoProducto = new Producto("Batería Solar", "Batería de litio", BigDecimal.valueOf(199.99), "Almacenamiento", "EcoStore");

        assertEquals("Batería Solar", nuevoProducto.getNombre());
        assertEquals("Batería de litio", nuevoProducto.getDescripcion());
        assertEquals(BigDecimal.valueOf(199.99), nuevoProducto.getPrecio());
        assertEquals("Almacenamiento", nuevoProducto.getCategoria());
        assertEquals("EcoStore", nuevoProducto.getMarca());
        assertTrue(nuevoProducto.getEstado());
        assertNotNull(nuevoProducto.getFechaCreacion());
    }

    @Test
    void testGettersAndSetters() {
        Producto producto = new Producto();
        LocalDateTime fechaCreacion = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime fechaActualizacion = LocalDateTime.of(2023, 12, 1, 15, 30);

        producto.setIdProducto(2);
        producto.setNombre("Inversor Solar");
        producto.setDescripcion("Inversor híbrido");
        producto.setPrecio(BigDecimal.valueOf(399.99));
        producto.setCategoria("Inversores");
        producto.setMarca("SolarTech");
        producto.setModelo("INV-5000");
        producto.setColor("Blanco");
        producto.setPeso(BigDecimal.valueOf(8.2));
        producto.setDimensiones("30x20x15 cm");
        producto.setMaterial("Aluminio");
        producto.setGarantiaMeses(60);
        producto.setEficienciaEnergetica("A+");
        producto.setImpactoAmbiental("Muy Bajo");
        producto.setPuntuacionEco(BigDecimal.valueOf(9.2));
        producto.setImagenUrl("https://example.com/inversor.jpg");
        producto.setEstado(false);
        producto.setFechaCreacion(fechaCreacion);
        producto.setFechaActualizacion(fechaActualizacion);

        assertEquals(2, producto.getIdProducto());
        assertEquals("Inversor Solar", producto.getNombre());
        assertEquals("Inversor híbrido", producto.getDescripcion());
        assertEquals(BigDecimal.valueOf(399.99), producto.getPrecio());
        assertEquals("Inversores", producto.getCategoria());
        assertEquals("SolarTech", producto.getMarca());
        assertEquals("INV-5000", producto.getModelo());
        assertEquals("Blanco", producto.getColor());
        assertEquals(BigDecimal.valueOf(8.2), producto.getPeso());
        assertEquals("30x20x15 cm", producto.getDimensiones());
        assertEquals("Aluminio", producto.getMaterial());
        assertEquals(60, producto.getGarantiaMeses());
        assertEquals("A+", producto.getEficienciaEnergetica());
        assertEquals("Muy Bajo", producto.getImpactoAmbiental());
        assertEquals(BigDecimal.valueOf(9.2), producto.getPuntuacionEco());
        assertEquals("https://example.com/inversor.jpg", producto.getImagenUrl());
        assertFalse(producto.getEstado());
        assertEquals(fechaCreacion, producto.getFechaCreacion());
        assertEquals(fechaActualizacion, producto.getFechaActualizacion());
    }

    @Test
    void testEqualsAndHashCode() {
        Producto producto1 = new Producto();
        producto1.setIdProducto(1);

        Producto producto2 = new Producto();
        producto2.setIdProducto(1);

        Producto producto3 = new Producto();
        producto3.setIdProducto(2);

        assertEquals(producto1, producto2);
        assertEquals(producto1.hashCode(), producto2.hashCode());
        assertNotEquals(producto1, producto3);
        assertNotEquals(producto1, null);
        assertEquals(producto1, producto1);
    }

    @Test
    void testPrePersist() {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setFechaCreacion(null);
        nuevoProducto.setEstado(null);

        nuevoProducto.prePersist();

        assertNotNull(nuevoProducto.getFechaCreacion());
        assertTrue(nuevoProducto.getEstado());
    }

    @Test
    void testPrePersistExistingValues() {
        LocalDateTime existingFecha = LocalDateTime.of(2023, 1, 1, 10, 0);

        Producto nuevoProducto = new Producto();
        nuevoProducto.setFechaCreacion(existingFecha);
        nuevoProducto.setEstado(false);

        nuevoProducto.prePersist();

        assertEquals(existingFecha, nuevoProducto.getFechaCreacion());
        assertFalse(nuevoProducto.getEstado());
    }

    @Test
    void testPreUpdate() {
        Producto producto = new Producto();
        producto.setFechaActualizacion(null);

        producto.preUpdate();

        assertNotNull(producto.getFechaActualizacion());
    }

    @Test
    void testTieneStock() {
        Producto producto = new Producto();
        // Test without inventory
        assertFalse(producto.tieneStock());

        // Test with inventory but no stock
        Inventario inventario = new Inventario();
        inventario.setStock(0);
        producto.setInventario(inventario);
        assertFalse(producto.tieneStock());

        // Test with inventory and stock
        inventario.setStock(10);
        assertTrue(producto.tieneStock());
    }

    @Test
    void testGetStockDisponible() {
        Producto producto = new Producto();
        // Test without inventory
        assertEquals(0, producto.getStockDisponible());

        // Test with inventory
        Inventario inventario = new Inventario();
        inventario.setStock(25);
        producto.setInventario(inventario);
        assertEquals(25, producto.getStockDisponible());

        // Test with null stock
        inventario.setStock(null);
        assertEquals(0, producto.getStockDisponible());
    }

    @Test
    void testEsEcoAmigable() {
        Producto producto = new Producto();
        // Test with null puntuacionEco
        producto.setPuntuacionEco(null);
        assertFalse(producto.esEcoAmigable());

        // Test with low score
        producto.setPuntuacionEco(BigDecimal.valueOf(6.5));
        assertFalse(producto.esEcoAmigable());

        // Test with high score
        producto.setPuntuacionEco(BigDecimal.valueOf(7.0));
        assertTrue(producto.esEcoAmigable());

        producto.setPuntuacionEco(BigDecimal.valueOf(8.5));
        assertTrue(producto.esEcoAmigable());
    }

    @Test
    void testRelationships() {
        Producto producto = new Producto();
        // Test inventario relationship
        Inventario inventario = new Inventario();
        producto.setInventario(inventario);
        assertEquals(inventario, producto.getInventario());

        // Test pedidoDetalles relationship
        producto.setPedidoDetalles(null);
        assertNull(producto.getPedidoDetalles());
    }
}