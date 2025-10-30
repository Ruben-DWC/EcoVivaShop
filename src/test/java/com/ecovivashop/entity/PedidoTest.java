package com.ecovivashop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PedidoTest {

    @Test
    void testDefaultConstructor() {
        Pedido nuevoPedido = new Pedido();

        assertNull(nuevoPedido.getIdPedido());
        assertNull(nuevoPedido.getUsuario());
        assertNull(nuevoPedido.getNumeroPedido());
        assertNotNull(nuevoPedido.getFechaPedido());
        assertEquals("PENDIENTE", nuevoPedido.getEstado());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getSubtotal());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getDescuento());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getImpuestos());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getCostoEnvio());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getTotal());
    }

    @Test
    void testCustomConstructor() {
        Usuario testUser = new Usuario();
        testUser.setIdUsuario(2);

        Pedido nuevoPedido = new Pedido(testUser, "EM987654321", "CONFIRMADO");

        assertEquals(testUser, nuevoPedido.getUsuario());
        assertEquals("EM987654321", nuevoPedido.getNumeroPedido());
        assertEquals("CONFIRMADO", nuevoPedido.getEstado());
        assertNotNull(nuevoPedido.getFechaPedido());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getSubtotal());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getDescuento());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getImpuestos());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getCostoEnvio());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getTotal());
    }

    @Test
    void testGettersAndSetters() {
        Pedido pedido = new Pedido();
        LocalDateTime fechaPedido = LocalDateTime.of(2023, 1, 1, 10, 0);
        LocalDateTime fechaEntrega = LocalDateTime.of(2023, 1, 5, 14, 30);
        LocalDateTime fechaEstimada = LocalDateTime.of(2023, 1, 4, 12, 0);

        pedido.setIdPedido(2);
        pedido.setNumeroPedido("EM999999999");
        pedido.setEstado("ENVIADO");
        pedido.setFechaPedido(fechaPedido);
        pedido.setFechaEntrega(fechaEntrega);
        pedido.setFechaEstimadaEntrega(fechaEstimada);
        pedido.setDireccionEnvio("Nueva dirección");
        pedido.setTelefonoContacto("987654321");
        pedido.setMetodoPago("PAYPAL");
        pedido.setNotas("Nuevas notas");
        pedido.setNumeroSeguimiento("TRACK999");
        pedido.setTransportadora("FedEx");

        assertEquals(2, pedido.getIdPedido());
        assertEquals("EM999999999", pedido.getNumeroPedido());
        assertEquals("ENVIADO", pedido.getEstado());
        assertEquals(fechaPedido, pedido.getFechaPedido());
        assertEquals(fechaEntrega, pedido.getFechaEntrega());
        assertEquals(fechaEstimada, pedido.getFechaEstimadaEntrega());
        assertEquals("Nueva dirección", pedido.getDireccionEnvio());
        assertEquals("987654321", pedido.getTelefonoContacto());
        assertEquals("PAYPAL", pedido.getMetodoPago());
        assertEquals("Nuevas notas", pedido.getNotas());
        assertEquals("TRACK999", pedido.getNumeroSeguimiento());
        assertEquals("FedEx", pedido.getTransportadora());
    }

    @Test
    void testEqualsAndHashCode() {
        Pedido pedido1 = new Pedido();
        pedido1.setIdPedido(1);

        Pedido pedido2 = new Pedido();
        pedido2.setIdPedido(1);

        Pedido pedido3 = new Pedido();
        pedido3.setIdPedido(2);

        assertEquals(pedido1, pedido2);
        assertEquals(pedido1.hashCode(), pedido2.hashCode());
        assertNotEquals(pedido1, pedido3);
        assertNotEquals(pedido1, null);
        assertEquals(pedido1, pedido1);
    }

    @Test
    void testPrePersist() {
        Pedido nuevoPedido = new Pedido();
        nuevoPedido.setFechaPedido(null);
        nuevoPedido.setEstado(null);
        nuevoPedido.setNumeroPedido(null);
        nuevoPedido.setSubtotal(null);
        nuevoPedido.setDescuento(null);
        nuevoPedido.setImpuestos(null);
        nuevoPedido.setCostoEnvio(null);
        nuevoPedido.setTotal(null);

        nuevoPedido.prePersist();

        assertNotNull(nuevoPedido.getFechaPedido());
        assertEquals("PENDIENTE", nuevoPedido.getEstado());
        assertNotNull(nuevoPedido.getNumeroPedido());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getSubtotal());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getDescuento());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getImpuestos());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getCostoEnvio());
        assertEquals(BigDecimal.ZERO, nuevoPedido.getTotal());
    }

    @Test
    void testCalcularTotales() {
        Pedido pedido = new Pedido();
        // Create mock pedido detalles
        Set<PedidoDetalle> detalles = new HashSet<>();

        PedidoDetalle detalle1 = new PedidoDetalle();
        detalle1.setIdDetalle(1); // Set unique ID
        detalle1.setCantidad(2);
        detalle1.setPrecioUnitario(BigDecimal.valueOf(25.00)); // 2 * 25.00 = 50.00
        detalle1.setDescuentoUnitario(BigDecimal.ZERO);
        detalle1.calcularSubtotal(); // This should set subtotal to 50.00

        PedidoDetalle detalle2 = new PedidoDetalle();
        detalle2.setIdDetalle(2); // Set unique ID
        detalle2.setCantidad(1);
        detalle2.setPrecioUnitario(BigDecimal.valueOf(30.00)); // 1 * 30.00 = 30.00
        detalle2.setDescuentoUnitario(BigDecimal.ZERO);
        detalle2.calcularSubtotal(); // This should set subtotal to 30.00

        detalles.add(detalle1);
        detalles.add(detalle2);

        pedido.setDetalles(detalles);
        pedido.setCostoEnvio(BigDecimal.valueOf(10.00));
        pedido.setDescuento(BigDecimal.valueOf(5.00));

        pedido.calcularTotales();

        // subtotal = 50.00 + 30.00 = 80.00
        // impuestos = 80.00 * 0.19 = 15.200
        // total = 80.00 + 15.200 + 10.00 - 5.00 = 100.200
        assertEquals(0, BigDecimal.valueOf(80.00).compareTo(pedido.getSubtotal()));
        assertEquals(0, BigDecimal.valueOf(15.200).compareTo(pedido.getImpuestos()));
        assertEquals(0, BigDecimal.valueOf(100.200).compareTo(pedido.getTotal()));
    }

    @Test
    void testPuedeSerCancelado() {
        Pedido pedido = new Pedido();
        pedido.setEstado("PENDIENTE");
        assertTrue(pedido.puedeSerCancelado());

        pedido.setEstado("CONFIRMADO");
        assertTrue(pedido.puedeSerCancelado());

        pedido.setEstado("EN_PREPARACION");
        assertFalse(pedido.puedeSerCancelado());

        pedido.setEstado("ENVIADO");
        assertFalse(pedido.puedeSerCancelado());

        pedido.setEstado("ENTREGADO");
        assertFalse(pedido.puedeSerCancelado());

        pedido.setEstado("CANCELADO");
        assertFalse(pedido.puedeSerCancelado());
    }

    @Test
    void testEstaCompleto() {
        Pedido pedido = new Pedido();
        pedido.setEstado("ENTREGADO");
        assertTrue(pedido.estaCompleto());

        pedido.setEstado("PENDIENTE");
        assertFalse(pedido.estaCompleto());

        pedido.setEstado("ENVIADO");
        assertFalse(pedido.estaCompleto());
    }

    @Test
    void testEstaEnProceso() {
        Pedido pedido = new Pedido();
        pedido.setEstado("CONFIRMADO");
        assertTrue(pedido.estaEnProceso());

        pedido.setEstado("EN_PREPARACION");
        assertTrue(pedido.estaEnProceso());

        pedido.setEstado("ENVIADO");
        assertTrue(pedido.estaEnProceso());

        pedido.setEstado("PENDIENTE");
        assertFalse(pedido.estaEnProceso());

        pedido.setEstado("ENTREGADO");
        assertFalse(pedido.estaEnProceso());

        pedido.setEstado("CANCELADO");
        assertFalse(pedido.estaEnProceso());
    }

    @Test
    void testConfirmar() {
        Pedido pedido = new Pedido();
        pedido.setEstado("PENDIENTE");
        pedido.confirmar();
        assertEquals("CONFIRMADO", pedido.getEstado());

        pedido.setEstado("ENVIADO");
        pedido.confirmar();
        assertEquals("ENVIADO", pedido.getEstado()); // No debería cambiar
    }

    @Test
    void testEnviar() {
        Pedido pedido = new Pedido();
        pedido.setEstado("CONFIRMADO");
        pedido.enviar("TRACK456", "UPS");

        assertEquals("ENVIADO", pedido.getEstado());
        assertEquals("TRACK456", pedido.getNumeroSeguimiento());
        assertEquals("UPS", pedido.getTransportadora());

        pedido.setEstado("EN_PREPARACION");
        pedido.enviar("TRACK789", "DHL");

        assertEquals("ENVIADO", pedido.getEstado());
        assertEquals("TRACK789", pedido.getNumeroSeguimiento());
        assertEquals("DHL", pedido.getTransportadora());

        pedido.setEstado("PENDIENTE");
        pedido.enviar("TRACK999", "FedEx");
        assertEquals("PENDIENTE", pedido.getEstado()); // No debería cambiar
    }

    @Test
    void testEntregar() {
        Pedido pedido = new Pedido();
        pedido.setEstado("ENVIADO");
        pedido.setFechaEntrega(null);

        pedido.entregar();

        assertEquals("ENTREGADO", pedido.getEstado());
        assertNotNull(pedido.getFechaEntrega());

        pedido.setEstado("PENDIENTE");
        pedido.entregar();
        assertEquals("PENDIENTE", pedido.getEstado()); // No debería cambiar
    }

    @Test
    void testCancelar() {
        Pedido pedido = new Pedido();
        pedido.setEstado("PENDIENTE");
        pedido.cancelar();
        assertEquals("CANCELADO", pedido.getEstado());

        pedido.setEstado("CONFIRMADO");
        pedido.cancelar();
        assertEquals("CANCELADO", pedido.getEstado());

        pedido.setEstado("ENVIADO");
        pedido.cancelar();
        assertEquals("ENVIADO", pedido.getEstado()); // No debería cambiar
    }

    @Test
    void testGetCantidadItems() {
        Pedido pedido = new Pedido();
        // Test without detalles
        pedido.setDetalles(null);
        assertEquals(0, pedido.getCantidadItems());

        // Test with empty detalles
        pedido.setDetalles(new HashSet<>());
        assertEquals(0, pedido.getCantidadItems());

        // Test with detalles
        Set<PedidoDetalle> detalles = new HashSet<>();

        PedidoDetalle detalle1 = new PedidoDetalle();
        detalle1.setIdDetalle(1); // Set ID to ensure proper Set behavior
        detalle1.setCantidad(2);

        PedidoDetalle detalle2 = new PedidoDetalle();
        detalle2.setIdDetalle(2); // Set ID to ensure proper Set behavior
        detalle2.setCantidad(3);

        detalles.add(detalle1);
        detalles.add(detalle2);

        pedido.setDetalles(detalles);
        assertEquals(5, pedido.getCantidadItems());
    }

    @Test
    void testRelationships() {
        Pedido pedido = new Pedido();
        // Test usuario relationship
        Usuario newUser = new Usuario();
        newUser.setIdUsuario(3);
        pedido.setUsuario(newUser);
        assertEquals(newUser, pedido.getUsuario());

        // Test detalles relationship
        pedido.setDetalles(new HashSet<>());
        assertNotNull(pedido.getDetalles());

        // Test pagos relationship
        pedido.setPagos(new HashSet<>());
        assertNotNull(pedido.getPagos());
    }
}