package com.ecovivashop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class PagoTest {

    @Test
    void testDefaultConstructor() {
        Pago p = new Pago();
        assertNotNull(p);
        assertNull(p.getIdPago());
        assertNull(p.getPedido());
        assertNull(p.getMetodoPago());
        assertNull(p.getMonto());
        assertNotNull(p.getFechaPago()); // Default value
        assertEquals("PENDIENTE", p.getEstado()); // Default value
        assertNull(p.getReferenciaPago());
        assertNull(p.getNumeroTransaccion());
        assertNull(p.getEntidadFinanciera());
        assertNull(p.getNumeroAprobacion());
        assertNull(p.getCodigoRespuesta());
        assertNull(p.getMensajeRespuesta());
        assertNull(p.getFechaAprobacion());
        assertNull(p.getIpCliente());
        assertNull(p.getUserAgent());
        assertNull(p.getDatosAdicionales());
    }

    @Test
    void testCustomConstructor() {
        Pedido pedido = new Pedido();
        String metodoPago = "TARJETA_CREDITO";
        BigDecimal monto = new BigDecimal("150.00");
        String estado = "APROBADO";

        Pago p = new Pago(pedido, metodoPago, monto, estado);

        assertEquals(pedido, p.getPedido());
        assertEquals(metodoPago, p.getMetodoPago());
        assertEquals(0, monto.compareTo(p.getMonto()));
        assertEquals(estado, p.getEstado());
        assertNotNull(p.getFechaPago());
    }

    @Test
    void testSettersAndGetters() {
        Pedido pedido = new Pedido();
        Pago pago = new Pago();
        LocalDateTime fechaPago = LocalDateTime.now();
        LocalDateTime fechaAprobacion = LocalDateTime.now().plusMinutes(5);

        // Set values
        pago.setIdPago(1);
        pago.setPedido(pedido);
        pago.setMetodoPago("PSE");
        pago.setMonto(new BigDecimal("200.50"));
        pago.setFechaPago(fechaPago);
        pago.setEstado("APROBADO");
        pago.setReferenciaPago("PAY123456789");
        pago.setNumeroTransaccion("TXN987654321");
        pago.setEntidadFinanciera("Bancolombia");
        pago.setNumeroAprobacion("APR001");
        pago.setCodigoRespuesta("00");
        pago.setMensajeRespuesta("Transacción exitosa");
        pago.setFechaAprobacion(fechaAprobacion);
        pago.setIpCliente("192.168.1.100");
        pago.setUserAgent("Mozilla/5.0");
        pago.setDatosAdicionales("{\"extra\":\"data\"}");

        // Verify getters
        assertEquals(1, pago.getIdPago());
        assertEquals(pedido, pago.getPedido());
        assertEquals("PSE", pago.getMetodoPago());
        assertEquals(0, new BigDecimal("200.50").compareTo(pago.getMonto()));
        assertEquals(fechaPago, pago.getFechaPago());
        assertEquals("APROBADO", pago.getEstado());
        assertEquals("PAY123456789", pago.getReferenciaPago());
        assertEquals("TXN987654321", pago.getNumeroTransaccion());
        assertEquals("Bancolombia", pago.getEntidadFinanciera());
        assertEquals("APR001", pago.getNumeroAprobacion());
        assertEquals("00", pago.getCodigoRespuesta());
        assertEquals("Transacción exitosa", pago.getMensajeRespuesta());
        assertEquals(fechaAprobacion, pago.getFechaAprobacion());
        assertEquals("192.168.1.100", pago.getIpCliente());
        assertEquals("Mozilla/5.0", pago.getUserAgent());
        assertEquals("{\"extra\":\"data\"}", pago.getDatosAdicionales());
    }

    @Test
    void testPrePersist() {
        Pedido pedido = new Pedido();
        Pago p = new Pago();
        p.setPedido(pedido);
        p.setMetodoPago("TARJETA_CREDITO");
        p.setMonto(new BigDecimal("100.00"));

        p.prePersist();

        // Should set default values
        assertNotNull(p.getFechaPago());
        assertEquals("PENDIENTE", p.getEstado());
        assertNotNull(p.getReferenciaPago());
        assertTrue(p.getReferenciaPago().startsWith("PAY"));
    }

    @Test
    void testPrePersistGeneratesReferenciaPago() {
        Pedido pedido = new Pedido();
        Pago p = new Pago();
        p.setPedido(pedido);
        p.setMetodoPago("TARJETA_CREDITO");
        p.setMonto(new BigDecimal("100.00"));
        p.setReferenciaPago(null); // Ensure it's null initially

        p.prePersist();

        // Should generate referenciaPago
        assertNotNull(p.getReferenciaPago());
        assertTrue(p.getReferenciaPago().startsWith("PAY"));
    }

    @Test
    void testAprobar() {
        Pago pago = new Pago();
        pago.setEstado("PENDIENTE");

        pago.aprobar("APR123", "TXN456");

        assertEquals("APROBADO", pago.getEstado());
        assertEquals("APR123", pago.getNumeroAprobacion());
        assertEquals("TXN456", pago.getNumeroTransaccion());
        assertNotNull(pago.getFechaAprobacion());
    }

    @Test
    void testRechazar() {
        Pago pago = new Pago();
        pago.setEstado("PENDIENTE");

        pago.rechazar("05", "Fondos insuficientes");

        assertEquals("RECHAZADO", pago.getEstado());
        assertEquals("05", pago.getCodigoRespuesta());
        assertEquals("Fondos insuficientes", pago.getMensajeRespuesta());
    }

    @Test
    void testCancelar() {
        Pago pago = new Pago();
        // Cancel from PENDIENTE
        pago.setEstado("PENDIENTE");
        pago.cancelar();
        assertEquals("CANCELADO", pago.getEstado());

        // Try to cancel from APROBADO (should not change)
        pago.setEstado("APROBADO");
        pago.cancelar();
        assertEquals("APROBADO", pago.getEstado());
    }

    @Test
    void testEstadoMethods() {
        Pago pago = new Pago();
        // Test estaAprobado
        pago.setEstado("APROBADO");
        assertTrue(pago.estaAprobado());
        assertFalse(pago.estaPendiente());
        assertFalse(pago.estaRechazado());
        assertFalse(pago.estaCancelado());

        // Test estaPendiente
        pago.setEstado("PENDIENTE");
        assertFalse(pago.estaAprobado());
        assertTrue(pago.estaPendiente());
        assertFalse(pago.estaRechazado());
        assertFalse(pago.estaCancelado());

        // Test estaRechazado
        pago.setEstado("RECHAZADO");
        assertFalse(pago.estaAprobado());
        assertFalse(pago.estaPendiente());
        assertTrue(pago.estaRechazado());
        assertFalse(pago.estaCancelado());

        // Test estaCancelado
        pago.setEstado("CANCELADO");
        assertFalse(pago.estaAprobado());
        assertFalse(pago.estaPendiente());
        assertFalse(pago.estaRechazado());
        assertTrue(pago.estaCancelado());
    }

    @Test
    void testEsExitoso() {
        Pago pago = new Pago();
        pago.setEstado("APROBADO");
        assertTrue(pago.esExitoso());

        pago.setEstado("PENDIENTE");
        assertFalse(pago.esExitoso());

        pago.setEstado("RECHAZADO");
        assertFalse(pago.esExitoso());
    }

    @Test
    void testGetDescripcionEstado() {
        Pago pago = new Pago();
        // APROBADO
        pago.setEstado("APROBADO");
        assertEquals("Pago aprobado exitosamente", pago.getDescripcionEstado());

        // RECHAZADO with message
        pago.setEstado("RECHAZADO");
        pago.setMensajeRespuesta("Fondos insuficientes");
        assertEquals("Pago rechazado: Fondos insuficientes", pago.getDescripcionEstado());

        // RECHAZADO without message
        pago.setMensajeRespuesta(null);
        assertEquals("Pago rechazado: Sin especificar", pago.getDescripcionEstado());

        // CANCELADO
        pago.setEstado("CANCELADO");
        assertEquals("Pago cancelado", pago.getDescripcionEstado());

        // PENDIENTE
        pago.setEstado("PENDIENTE");
        assertEquals("Pago en proceso", pago.getDescripcionEstado());
    }

    @Test
    void testMetodoPagoTypes() {
        Pago pago = new Pago();
        // TARJETA_CREDITO
        pago.setMetodoPago("TARJETA_CREDITO");
        assertTrue(pago.esTarjetaCredito());
        assertFalse(pago.esTarjetaDebito());
        assertFalse(pago.esPSE());
        assertFalse(pago.esEfectivo());

        // TARJETA_DEBITO
        pago.setMetodoPago("TARJETA_DEBITO");
        assertFalse(pago.esTarjetaCredito());
        assertTrue(pago.esTarjetaDebito());
        assertFalse(pago.esPSE());
        assertFalse(pago.esEfectivo());

        // PSE
        pago.setMetodoPago("PSE");
        assertFalse(pago.esTarjetaCredito());
        assertFalse(pago.esTarjetaDebito());
        assertTrue(pago.esPSE());
        assertFalse(pago.esEfectivo());

        // EFECTIVO
        pago.setMetodoPago("EFECTIVO");
        assertFalse(pago.esTarjetaCredito());
        assertFalse(pago.esTarjetaDebito());
        assertFalse(pago.esPSE());
        assertTrue(pago.esEfectivo());
    }

    @Test
    void testToString() {
        Pago pago = new Pago();
        pago.setIdPago(1);
        pago.setMonto(new BigDecimal("100.00"));

        String toString = pago.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Pago"));
    }
}