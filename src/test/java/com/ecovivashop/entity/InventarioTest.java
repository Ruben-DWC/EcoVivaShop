package com.ecovivashop.entity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class InventarioTest {

    @Test
    void testDefaultConstructor() {
        Inventario inv = new Inventario();
        assertNotNull(inv);
        assertNull(inv.getIdInventario());
        assertNull(inv.getProducto());
        assertEquals(0, inv.getStock()); // Default value
        assertEquals(5, inv.getStockMinimo()); // Default value
        assertNull(inv.getStockMaximo());
        assertNull(inv.getUbicacion());
        assertTrue(inv.getEstado()); // Default value
        assertNotNull(inv.getFechaActualizacion()); // Default value (now)
        assertNull(inv.getUsuarioActualizacion());
    }

    @Test
    void testCustomConstructor() {
        Producto prod = new Producto();
        Integer stock = 100;
        Integer stockMinimo = 10;
        Integer stockMaximo = 500;

        Inventario inv = new Inventario(prod, stock, stockMinimo, stockMaximo);

        assertEquals(prod, inv.getProducto());
        assertEquals(stock, inv.getStock());
        assertEquals(stockMinimo, inv.getStockMinimo());
        assertEquals(stockMaximo, inv.getStockMaximo());
        assertTrue(inv.getEstado());
        assertNotNull(inv.getFechaActualizacion());
    }

    @Test
    void testSettersAndGetters() {
        Inventario inventario = new Inventario();
        Producto productoTest = new Producto();
        LocalDateTime fecha = LocalDateTime.now();

        // Set values
        inventario.setIdInventario(1);
        inventario.setProducto(productoTest);
        inventario.setStock(50);
        inventario.setStockMinimo(10);
        inventario.setStockMaximo(200);
        inventario.setUbicacion("Almacén A");
        inventario.setEstado(true);
        inventario.setFechaActualizacion(fecha);
        inventario.setUsuarioActualizacion("admin");

        // Verify getters
        assertEquals(1, inventario.getIdInventario());
        assertEquals(productoTest, inventario.getProducto());
        assertEquals(50, inventario.getStock());
        assertEquals(10, inventario.getStockMinimo());
        assertEquals(200, inventario.getStockMaximo());
        assertEquals("Almacén A", inventario.getUbicacion());
        assertTrue(inventario.getEstado());
        assertEquals(fecha, inventario.getFechaActualizacion());
        assertEquals("admin", inventario.getUsuarioActualizacion());
    }

    @Test
    void testNecesitaReposicion() {
        Inventario inventario = new Inventario();

        // Stock equal to minimum
        inventario.setStock(5);
        inventario.setStockMinimo(5);
        assertTrue(inventario.necesitaReposicion());

        // Stock below minimum
        inventario.setStock(3);
        inventario.setStockMinimo(5);
        assertTrue(inventario.necesitaReposicion());

        // Stock above minimum
        inventario.setStock(10);
        inventario.setStockMinimo(5);
        assertFalse(inventario.necesitaReposicion());
    }

    @Test
    void testAgotado() {
        Inventario inventario = new Inventario();

        // Stock is zero
        inventario.setStock(0);
        assertTrue(inventario.agotado());

        // Stock is negative
        inventario.setStock(-1);
        assertTrue(inventario.agotado());

        // Stock is positive
        inventario.setStock(5);
        assertFalse(inventario.agotado());
    }

    @Test
    void testStockCritico() {
        Inventario inventario = new Inventario();
        inventario.setStockMinimo(10);

        // Stock is zero (not critical, it's agotado)
        inventario.setStock(0);
        assertFalse(inventario.stockCritico());

        // Stock at half minimum (5) - critical
        inventario.setStock(5);
        assertTrue(inventario.stockCritico());

        // Stock below half minimum (4) - critical
        inventario.setStock(4);
        assertTrue(inventario.stockCritico());

        // Stock above half minimum (6) - not critical
        inventario.setStock(6);
        assertFalse(inventario.stockCritico());
    }

    @Test
    void testReducirStock() {
        Inventario inventario = new Inventario();
        inventario.setStock(20);

        // Valid reduction
        inventario.reducirStock(5);
        assertEquals(15, inventario.getStock());

        // Invalid reduction (negative quantity)
        inventario.reducirStock(-3);
        assertEquals(15, inventario.getStock()); // No change

        // Invalid reduction (insufficient stock)
        inventario.reducirStock(20);
        assertEquals(15, inventario.getStock()); // No change
    }

    @Test
    void testAumentarStock() {
        Inventario inventario = new Inventario();
        inventario.setStock(10);

        // Valid increase
        inventario.aumentarStock(15);
        assertEquals(25, inventario.getStock());

        // Invalid increase (negative quantity)
        inventario.aumentarStock(-5);
        assertEquals(25, inventario.getStock()); // No change
    }

    @Test
    void testGetEstadoStock() {
        Inventario inventario = new Inventario();
        inventario.setStockMinimo(10);

        // Agotado
        inventario.setStock(0);
        assertEquals("AGOTADO", inventario.getEstadoStock());

        // Crítico
        inventario.setStock(4);
        assertEquals("CRITICO", inventario.getEstadoStock());

        // Bajo
        inventario.setStock(8);
        assertEquals("BAJO", inventario.getEstadoStock());

        // Normal
        inventario.setStock(20);
        assertEquals("NORMAL", inventario.getEstadoStock());
    }

    @Test
    void testPrePersist() {
        Inventario inv = new Inventario();
        // All default values should be null initially (except those set by @Column defaults)

        inv.prePersist();

        // These should be set by prePersist
        assertNotNull(inv.getFechaActualizacion());
        assertTrue(inv.getEstado());
        assertEquals(0, inv.getStock());
        assertEquals(5, inv.getStockMinimo());
    }

    @Test
    void testPreUpdate() {
        Inventario inventario = new Inventario();
        LocalDateTime oldFecha = LocalDateTime.now().minusDays(1);
        inventario.setFechaActualizacion(oldFecha);

        inventario.preUpdate();

        // fechaActualizacion should be updated to now
        assertTrue(inventario.getFechaActualizacion().isAfter(oldFecha));
    }

    @Test
    void testEqualsAndHashCode() {
        Inventario inv1 = new Inventario();
        inv1.setIdInventario(1);

        Inventario inv2 = new Inventario();
        inv2.setIdInventario(1);

        Inventario inv3 = new Inventario();
        inv3.setIdInventario(2);

        // Same idInventario
        assertEquals(inv1, inv2);
        assertEquals(inv1.hashCode(), inv2.hashCode());

        // Different idInventario
        assertNotEquals(inv1, inv3);
        assertNotEquals(inv1.hashCode(), inv3.hashCode());

        // Null idInventario
        Inventario inv4 = new Inventario();
        Inventario inv5 = new Inventario();
        assertEquals(inv4, inv5);
        assertEquals(inv4.hashCode(), inv5.hashCode());
    }

    @Test
    void testToString() {
        Inventario inventario = new Inventario();
        inventario.setIdInventario(1);
        inventario.setStock(100);

        String toString = inventario.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Inventario"));
    }
}