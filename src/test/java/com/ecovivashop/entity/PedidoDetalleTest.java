package com.ecovivashop.entity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PedidoDetalleTest {

    @Test
    void testDefaultConstructor() {
        PedidoDetalle detalle = new PedidoDetalle();
        assertNotNull(detalle);
        assertNull(detalle.getIdDetalle());
        assertNull(detalle.getPedido());
        assertNull(detalle.getProducto());
        assertNull(detalle.getCantidad());
        assertNull(detalle.getPrecioUnitario());
        assertEquals(BigDecimal.ZERO, detalle.getDescuentoUnitario()); // Default value
        assertNull(detalle.getSubtotal());
        assertNull(detalle.getNotas());
    }

    @Test
    void testCustomConstructor() {
        Pedido pedido = new Pedido();
        Producto producto = new Producto();
        Integer cantidad = 3;
        BigDecimal precioUnitario = BigDecimal.valueOf(150.00);

        PedidoDetalle detalle = new PedidoDetalle(pedido, producto, cantidad, precioUnitario);

        assertEquals(pedido, detalle.getPedido());
        assertEquals(producto, detalle.getProducto());
        assertEquals(cantidad, detalle.getCantidad());
        assertEquals(precioUnitario, detalle.getPrecioUnitario());
        assertEquals(BigDecimal.ZERO, detalle.getDescuentoUnitario());
        assertEquals(BigDecimal.valueOf(450.00), detalle.getSubtotal()); // 3 * 150.00
    }

    @Test
    void testSettersAndGetters() {
        Pedido pedido = new Pedido();
        Producto producto = new Producto();
        PedidoDetalle pedidoDetalle = new PedidoDetalle();

        // Set values
        pedidoDetalle.setIdDetalle(1);
        pedidoDetalle.setPedido(pedido);
        pedidoDetalle.setProducto(producto);
        pedidoDetalle.setCantidad(5);
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(200.00));
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(10.00));
        pedidoDetalle.setSubtotal(BigDecimal.valueOf(900.00));
        pedidoDetalle.setNotas("Producto en promoción");

        // Verify getters
        assertEquals(1, pedidoDetalle.getIdDetalle());
        assertEquals(pedido, pedidoDetalle.getPedido());
        assertEquals(producto, pedidoDetalle.getProducto());
        assertEquals(5, pedidoDetalle.getCantidad());
        assertEquals(BigDecimal.valueOf(200.00), pedidoDetalle.getPrecioUnitario());
        assertEquals(BigDecimal.valueOf(10.00), pedidoDetalle.getDescuentoUnitario());
        assertEquals(BigDecimal.valueOf(900.00), pedidoDetalle.getSubtotal());
        assertEquals("Producto en promoción", pedidoDetalle.getNotas());
    }

    @Test
    void testCalcularSubtotal() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setCantidad(4);
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(250.00));
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(25.00));

        pedidoDetalle.calcularSubtotal();

        // subtotal = (250.00 - 25.00) * 4 = 225.00 * 4 = 900.00
        assertEquals(0, BigDecimal.valueOf(900.00).compareTo(pedidoDetalle.getSubtotal()));
    }

    @Test
    void testCalcularSubtotalWithoutDiscount() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setCantidad(2);
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(300.00));
        pedidoDetalle.setDescuentoUnitario(BigDecimal.ZERO);

        pedidoDetalle.calcularSubtotal();

        // subtotal = (300.00 - 0.00) * 2 = 300.00 * 2 = 600.00
        assertEquals(0, BigDecimal.valueOf(600.00).compareTo(pedidoDetalle.getSubtotal()));
    }

    @Test
    void testCalcularSubtotalWithNullValues() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setCantidad(null);
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(100.00));

        pedidoDetalle.calcularSubtotal();

        assertEquals(0, BigDecimal.ZERO.compareTo(pedidoDetalle.getSubtotal()));
    }

    @Test
    void testGetTotalDescuento() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setCantidad(3);
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(15.00));

        // total descuento = 15.00 * 3 = 45.00
        assertEquals(0, BigDecimal.valueOf(45.00).compareTo(pedidoDetalle.getTotalDescuento()));
    }

    @Test
    void testGetPrecioFinalUnitario() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(200.00));
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(20.00));

        // precio final unitario = 200.00 - 20.00 = 180.00
        assertEquals(0, BigDecimal.valueOf(180.00).compareTo(pedidoDetalle.getPrecioFinalUnitario()));
    }

    @Test
    void testGetAhorro() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setCantidad(2);
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(50.00));

        // ahorro = 50.00 * 2 = 100.00
        assertEquals(0, BigDecimal.valueOf(100.00).compareTo(pedidoDetalle.getAhorro()));
    }

    @Test
    void testTieneDescuento() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();

        // Without discount
        pedidoDetalle.setDescuentoUnitario(BigDecimal.ZERO);
        assertFalse(pedidoDetalle.tieneDescuento());

        // With discount
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(10.00));
        assertTrue(pedidoDetalle.tieneDescuento());

        // With null discount
        pedidoDetalle.setDescuentoUnitario(null);
        assertFalse(pedidoDetalle.tieneDescuento());
    }

    @Test
    void testGetPorcentajeDescuento() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(200.00));
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(40.00));

        // porcentaje = (40.00 / 200.00) * 100 = 20.00%
        assertEquals(0, BigDecimal.valueOf(20.00).compareTo(pedidoDetalle.getPorcentajeDescuento()));
    }

    @Test
    void testGetPorcentajeDescuentoWithZeroPrice() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setPrecioUnitario(BigDecimal.ZERO);
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(10.00));

        assertEquals(0, BigDecimal.ZERO.compareTo(pedidoDetalle.getPorcentajeDescuento()));
    }

    @Test
    void testPrePersist() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setCantidad(2);
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(100.00));
        // descuentoUnitario is null

        pedidoDetalle.prePersist();

        // descuentoUnitario should be set to ZERO
        assertEquals(0, BigDecimal.ZERO.compareTo(pedidoDetalle.getDescuentoUnitario()));
        // subtotal should be calculated: 2 * 100.00 = 200.00
        assertEquals(0, BigDecimal.valueOf(200.00).compareTo(pedidoDetalle.getSubtotal()));
    }

    @Test
    void testPreUpdate() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setCantidad(3);
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(150.00));
        pedidoDetalle.setDescuentoUnitario(BigDecimal.valueOf(10.00));

        pedidoDetalle.preUpdate();

        // subtotal should be calculated: (150.00 - 10.00) * 3 = 140.00 * 3 = 420.00
        assertEquals(0, BigDecimal.valueOf(420.00).compareTo(pedidoDetalle.getSubtotal()));
    }

    @Test
    void testEqualsAndHashCode() {
        PedidoDetalle detalle1 = new PedidoDetalle();
        detalle1.setIdDetalle(1);

        PedidoDetalle detalle2 = new PedidoDetalle();
        detalle2.setIdDetalle(1);

        PedidoDetalle detalle3 = new PedidoDetalle();
        detalle3.setIdDetalle(2);

        // Same idDetalle
        assertEquals(detalle1, detalle2);
        assertEquals(detalle1.hashCode(), detalle2.hashCode());

        // Different idDetalle
        assertNotEquals(detalle1, detalle3);
        assertNotEquals(detalle1.hashCode(), detalle3.hashCode());

        // Null idDetalle
        PedidoDetalle detalle4 = new PedidoDetalle();
        PedidoDetalle detalle5 = new PedidoDetalle();
        assertEquals(detalle4, detalle5);
        assertEquals(detalle4.hashCode(), detalle5.hashCode());
    }

    @Test
    void testToString() {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setIdDetalle(1);
        pedidoDetalle.setCantidad(2);
        pedidoDetalle.setPrecioUnitario(BigDecimal.valueOf(100.00));

        String toString = pedidoDetalle.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("PedidoDetalle"));
    }
}