package com.ecovivashop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class TransaccionPagoTest {

    @Test
    void testDefaultConstructor() {
        TransaccionPago t = new TransaccionPago();
        assertNotNull(t);
        assertNull(t.getIdTransaccion());
        assertNull(t.getUsuario());
        assertNull(t.getNumeroPedido());
        assertNull(t.getMonto());
        assertNull(t.getMetodoPago());
        assertEquals(TransaccionPago.EstadoTransaccion.PENDIENTE, t.getEstadoTransaccion()); // Default value
        assertNull(t.getPasarelaPago());
        assertNull(t.getTransactionId());
        assertNull(t.getAuthorizationCode());
        assertNull(t.getReferenceNumber());
        assertEquals("PEN", t.getCurrencyCode()); // Default value
        assertNull(t.getPaymentToken());
        assertNull(t.getClienteNombres());
        assertNull(t.getClienteApellidos());
        assertNull(t.getClienteEmail());
        assertNull(t.getClienteTelefono());
        assertNull(t.getClienteDocumentoTipo());
        assertNull(t.getClienteDocumentoNumero());
        assertNull(t.getDireccionEnvio());
        assertNull(t.getDepartamento());
        assertNull(t.getProvincia());
        assertNull(t.getDistrito());
        assertNull(t.getTarjetaNumeroEncriptado());
        assertNull(t.getTarjetaNombre());
        assertNull(t.getTarjetaMesVencimiento());
        assertNull(t.getTarjetaAnoVencimiento());
        assertNull(t.getFechaTransaccion());
        assertNull(t.getFechaAutorizacion());
        assertNotNull(t.getFechaCreacion()); // Default value
        assertNotNull(t.getFechaActualizacion()); // Default value
        assertNull(t.getIpCliente());
        assertNull(t.getUserAgent());
        assertNull(t.getMensajeRespuesta());
        assertNull(t.getCodigoRespuesta());
        assertEquals(1, t.getIntentosPago()); // Default value
        assertFalse(t.getEsSuscripcion()); // Default value
        assertNull(t.getIdSuscripcion());
    }

    @Test
    void testSettersAndGetters() {
        Usuario usuario = new Usuario();
        TransaccionPago transaccion = new TransaccionPago();
        LocalDateTime fechaTransaccion = LocalDateTime.now();
        LocalDateTime fechaAutorizacion = LocalDateTime.now().plusMinutes(5);
        LocalDateTime fechaCreacion = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaActualizacion = LocalDateTime.now();

        // Set values
        transaccion.setIdTransaccion(1L);
        transaccion.setUsuario(usuario);
        transaccion.setNumeroPedido("PED001");
        transaccion.setMonto(new BigDecimal("150.00"));
        transaccion.setMetodoPago(TransaccionPago.MetodoPago.TARJETA_CREDITO);
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.COMPLETADA);
        transaccion.setPasarelaPago(TransaccionPago.PasarelaPago.CULQI);
        transaccion.setTransactionId("TXN123456");
        transaccion.setAuthorizationCode("AUTH789");
        transaccion.setReferenceNumber("REF001");
        transaccion.setCurrencyCode("USD");
        transaccion.setPaymentToken("TOKEN123");
        transaccion.setClienteNombres("Juan");
        transaccion.setClienteApellidos("Pérez");
        transaccion.setClienteEmail("juan@email.com");
        transaccion.setClienteTelefono("999888777");
        transaccion.setClienteDocumentoTipo("DNI");
        transaccion.setClienteDocumentoNumero("12345678");
        transaccion.setDireccionEnvio("Av. Principal 123");
        transaccion.setDepartamento("Lima");
        transaccion.setProvincia("Lima");
        transaccion.setDistrito("Miraflores");
        transaccion.setTarjetaNumeroEncriptado("4111111111111111");
        transaccion.setTarjetaNombre("Juan Pérez");
        transaccion.setTarjetaMesVencimiento("12");
        transaccion.setTarjetaAnoVencimiento("25");
        transaccion.setFechaTransaccion(fechaTransaccion);
        transaccion.setFechaAutorizacion(fechaAutorizacion);
        transaccion.setFechaCreacion(fechaCreacion);
        transaccion.setFechaActualizacion(fechaActualizacion);
        transaccion.setIpCliente("192.168.1.100");
        transaccion.setUserAgent("Mozilla/5.0");
        transaccion.setMensajeRespuesta("Transacción exitosa");
        transaccion.setCodigoRespuesta("00");
        transaccion.setIntentosPago(2);
        transaccion.setEsSuscripcion(true);
        transaccion.setIdSuscripcion(100L);

        // Verify getters
        assertEquals(1L, transaccion.getIdTransaccion());
        assertEquals(usuario, transaccion.getUsuario());
        assertEquals("PED001", transaccion.getNumeroPedido());
        assertEquals(0, new BigDecimal("150.00").compareTo(transaccion.getMonto()));
        assertEquals(TransaccionPago.MetodoPago.TARJETA_CREDITO, transaccion.getMetodoPago());
        assertEquals(TransaccionPago.EstadoTransaccion.COMPLETADA, transaccion.getEstadoTransaccion());
        assertEquals(TransaccionPago.PasarelaPago.CULQI, transaccion.getPasarelaPago());
        assertEquals("TXN123456", transaccion.getTransactionId());
        assertEquals("AUTH789", transaccion.getAuthorizationCode());
        assertEquals("REF001", transaccion.getReferenceNumber());
        assertEquals("USD", transaccion.getCurrencyCode());
        assertEquals("TOKEN123", transaccion.getPaymentToken());
        assertEquals("Juan", transaccion.getClienteNombres());
        assertEquals("Pérez", transaccion.getClienteApellidos());
        assertEquals("juan@email.com", transaccion.getClienteEmail());
        assertEquals("999888777", transaccion.getClienteTelefono());
        assertEquals("DNI", transaccion.getClienteDocumentoTipo());
        assertEquals("12345678", transaccion.getClienteDocumentoNumero());
        assertEquals("Av. Principal 123", transaccion.getDireccionEnvio());
        assertEquals("Lima", transaccion.getDepartamento());
        assertEquals("Lima", transaccion.getProvincia());
        assertEquals("Miraflores", transaccion.getDistrito());
        assertEquals("4111111111111111", transaccion.getTarjetaNumeroEncriptado());
        assertEquals("Juan Pérez", transaccion.getTarjetaNombre());
        assertEquals("12", transaccion.getTarjetaMesVencimiento());
        assertEquals("25", transaccion.getTarjetaAnoVencimiento());
        assertEquals(fechaTransaccion, transaccion.getFechaTransaccion());
        assertEquals(fechaAutorizacion, transaccion.getFechaAutorizacion());
        assertEquals(fechaCreacion, transaccion.getFechaCreacion());
        assertEquals(fechaActualizacion, transaccion.getFechaActualizacion());
        assertEquals("192.168.1.100", transaccion.getIpCliente());
        assertEquals("Mozilla/5.0", transaccion.getUserAgent());
        assertEquals("Transacción exitosa", transaccion.getMensajeRespuesta());
        assertEquals("00", transaccion.getCodigoRespuesta());
        assertEquals(2, transaccion.getIntentosPago());
        assertTrue(transaccion.getEsSuscripcion());
        assertEquals(100L, transaccion.getIdSuscripcion());
    }

    @Test
    void testSetEstadoTransaccionUpdatesFechaActualizacion() {
        TransaccionPago transaccion = new TransaccionPago();
        LocalDateTime oldFecha = transaccion.getFechaActualizacion();

        // Wait a bit to ensure different timestamp
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.COMPLETADA);

        assertEquals(TransaccionPago.EstadoTransaccion.COMPLETADA, transaccion.getEstadoTransaccion());
        assertTrue(transaccion.getFechaActualizacion().isAfter(oldFecha));
    }

    @Test
    void testIsExitosa() {
        TransaccionPago transaccion = new TransaccionPago();
        // COMPLETADA
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.COMPLETADA);
        assertTrue(transaccion.isExitosa());

        // AUTORIZADA
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.AUTORIZADA);
        assertTrue(transaccion.isExitosa());

        // Other states
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.PENDIENTE);
        assertFalse(transaccion.isExitosa());

        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.RECHAZADA);
        assertFalse(transaccion.isExitosa());
    }

    @Test
    void testIsPendiente() {
        TransaccionPago transaccion = new TransaccionPago();
        // PENDIENTE
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.PENDIENTE);
        assertTrue(transaccion.isPendiente());

        // PROCESANDO
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.PROCESANDO);
        assertTrue(transaccion.isPendiente());

        // Other states
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.COMPLETADA);
        assertFalse(transaccion.isPendiente());

        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.RECHAZADA);
        assertFalse(transaccion.isPendiente());
    }

    @Test
    void testIsFallida() {
        TransaccionPago transaccion = new TransaccionPago();
        // RECHAZADA
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.RECHAZADA);
        assertTrue(transaccion.isFallida());

        // ERROR
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.ERROR);
        assertTrue(transaccion.isFallida());

        // CANCELADA
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.CANCELADA);
        assertTrue(transaccion.isFallida());

        // Other states
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.COMPLETADA);
        assertFalse(transaccion.isFallida());

        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.PENDIENTE);
        assertFalse(transaccion.isFallida());
    }

    @Test
    void testGetNumeroTarjetaEnmascarado() {
        TransaccionPago transaccion = new TransaccionPago();
        // Valid card number
        transaccion.setTarjetaNumeroEncriptado("4111111111111111");
        assertEquals("**** **** **** 1111", transaccion.getNumeroTarjetaEnmascarado());

        // Short card number
        transaccion.setTarjetaNumeroEncriptado("1111");
        assertEquals("**** **** **** 1111", transaccion.getNumeroTarjetaEnmascarado());

        // Very short card number
        transaccion.setTarjetaNumeroEncriptado("11");
        assertEquals("****", transaccion.getNumeroTarjetaEnmascarado());

        // Null card number
        transaccion.setTarjetaNumeroEncriptado(null);
        assertEquals("****", transaccion.getNumeroTarjetaEnmascarado());
    }

    @Test
    void testEnums() {
        // MetodoPago
        assertEquals("Tarjeta de Crédito", TransaccionPago.MetodoPago.TARJETA_CREDITO.getDescripcion());
        assertEquals("Yape", TransaccionPago.MetodoPago.YAPE.getDescripcion());

        // EstadoTransaccion
        assertEquals("Completada", TransaccionPago.EstadoTransaccion.COMPLETADA.getDescripcion());
        assertEquals("Rechazada", TransaccionPago.EstadoTransaccion.RECHAZADA.getDescripcion());

        // PasarelaPago
        assertEquals("Culqi", TransaccionPago.PasarelaPago.CULQI.getDescripcion());
        assertEquals("PayPal", TransaccionPago.PasarelaPago.PAYPAL.getDescripcion());
    }

    @Test
    void testToString() {
        TransaccionPago transaccion = new TransaccionPago();
        transaccion.setIdTransaccion(1L);
        transaccion.setNumeroPedido("PED001");
        transaccion.setMonto(new BigDecimal("100.00"));
        transaccion.setMetodoPago(TransaccionPago.MetodoPago.TARJETA_CREDITO);
        transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.COMPLETADA);
        transaccion.setPasarelaPago(TransaccionPago.PasarelaPago.CULQI);

        String toString = transaccion.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("TransaccionPago"));
        assertTrue(toString.contains("idTransaccion=1"));
        assertTrue(toString.contains("numeroPedido='PED001'"));
        assertTrue(toString.contains("estadoTransaccion=COMPLETADA"));
    }
}