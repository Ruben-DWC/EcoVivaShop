package com.ecovivashop.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class SuscripcionTest {

    @Test
    void testDefaultConstructor() {
        Suscripcion s = new Suscripcion();
        assertNotNull(s);
        assertNull(s.getIdSuscripcion());
        assertNull(s.getUsuario());
        assertNull(s.getTipoSuscripcion());
        assertNull(s.getPrecioMensual());
        assertNull(s.getDescuentoPorcentaje());
        assertNull(s.getBeneficios());
        assertNull(s.getFechaInicio());
        assertNull(s.getFechaFin());
        assertTrue(s.getEstado()); // Default value
        assertFalse(s.getAutoRenovacion()); // Default value
        assertNotNull(s.getFechaCreacion()); // Default value
        assertNull(s.getFechaCancelacion());
        assertNull(s.getMotivoCancelacion());
    }

    @Test
    void testCustomConstructor() {
        Usuario usuario = new Usuario();
        String tipoSuscripcion = "PREMIUM";
        BigDecimal precioMensual = new BigDecimal("29.99");

        Suscripcion s = new Suscripcion(usuario, tipoSuscripcion, precioMensual);

        assertEquals(usuario, s.getUsuario());
        assertEquals(tipoSuscripcion, s.getTipoSuscripcion());
        assertEquals(0, precioMensual.compareTo(s.getPrecioMensual()));
        assertTrue(s.getEstado());
        assertTrue(s.getAutoRenovacion());
        assertNotNull(s.getFechaInicio());
        assertNotNull(s.getFechaCreacion());
    }

    @Test
    void testSettersAndGetters() {
        Suscripcion suscripcion = new Suscripcion();
        Usuario usuario = new Usuario();
        LocalDateTime fechaInicio = LocalDateTime.now();
        LocalDateTime fechaFin = LocalDateTime.now().plusMonths(1);
        LocalDateTime fechaCreacion = LocalDateTime.now().minusDays(1);
        LocalDateTime fechaCancelacion = LocalDateTime.now();

        // Set values
        suscripcion.setIdSuscripcion(1);
        suscripcion.setUsuario(usuario);
        suscripcion.setTipoSuscripcion("BASIC");
        suscripcion.setPrecioMensual(new BigDecimal("19.99"));
        suscripcion.setDescuentoPorcentaje(new BigDecimal("10.00"));
        suscripcion.setBeneficios("Acceso básico a productos");
        suscripcion.setFechaInicio(fechaInicio);
        suscripcion.setFechaFin(fechaFin);
        suscripcion.setEstado(true);
        suscripcion.setAutoRenovacion(true);
        suscripcion.setFechaCreacion(fechaCreacion);
        suscripcion.setFechaCancelacion(fechaCancelacion);
        suscripcion.setMotivoCancelacion("Cambio de plan");

        // Verify getters
        assertEquals(1, suscripcion.getIdSuscripcion());
        assertEquals(usuario, suscripcion.getUsuario());
        assertEquals("BASIC", suscripcion.getTipoSuscripcion());
        assertEquals(0, new BigDecimal("19.99").compareTo(suscripcion.getPrecioMensual()));
        assertEquals(0, new BigDecimal("10.00").compareTo(suscripcion.getDescuentoPorcentaje()));
        assertEquals("Acceso básico a productos", suscripcion.getBeneficios());
        assertEquals(fechaInicio, suscripcion.getFechaInicio());
        assertEquals(fechaFin, suscripcion.getFechaFin());
        assertTrue(suscripcion.getEstado());
        assertTrue(suscripcion.getAutoRenovacion());
        assertEquals(fechaCreacion, suscripcion.getFechaCreacion());
        assertEquals(fechaCancelacion, suscripcion.getFechaCancelacion());
        assertEquals("Cambio de plan", suscripcion.getMotivoCancelacion());
    }

    @Test
    void testPrePersist() {
        Usuario usuario = new Usuario();
        Suscripcion s = new Suscripcion();
        s.setUsuario(usuario);
        s.setTipoSuscripcion("PREMIUM");
        s.setPrecioMensual(new BigDecimal("29.99"));

        s.prePersist();

        // Should set default values
        assertNotNull(s.getFechaCreacion());
        assertNotNull(s.getFechaInicio());
        assertTrue(s.getEstado());
        assertFalse(s.getAutoRenovacion());
    }

    @Test
    void testEstaActiva() {
        Suscripcion s = new Suscripcion();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime past = now.minusDays(1);
        LocalDateTime future = now.plusDays(30);

        // Active subscription with no end date
        s.setEstado(true);
        s.setFechaInicio(past);
        s.setFechaFin(null);
        assertTrue(s.estaActiva());

        // Active subscription with future end date
        s.setFechaFin(future);
        assertTrue(s.estaActiva());

        // Inactive subscription
        s.setEstado(false);
        assertFalse(s.estaActiva());

        // Past start date
        s.setEstado(true);
        s.setFechaInicio(future);
        assertFalse(s.estaActiva());

        // Expired subscription
        s.setFechaInicio(past);
        s.setFechaFin(past);
        assertFalse(s.estaActiva());
    }

    @Test
    void testProximaAVencer() {
        Suscripcion s = new Suscripcion();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime in3Days = now.plusDays(3);
        LocalDateTime in10Days = now.plusDays(10);
        LocalDateTime past = now.minusDays(1);

        // Expires in 3 days (should be true)
        s.setFechaFin(in3Days);
        assertTrue(s.proximaAVencer());

        // Expires in 10 days (should be false)
        s.setFechaFin(in10Days);
        assertFalse(s.proximaAVencer());

        // No end date (should be false)
        s.setFechaFin(null);
        assertFalse(s.proximaAVencer());

        // Already expired (should be false)
        s.setFechaFin(past);
        assertFalse(s.proximaAVencer());
    }

    @Test
    void testVencida() {
        Suscripcion s = new Suscripcion();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(30);
        LocalDateTime past = now.minusDays(1);

        // Not expired
        s.setFechaFin(future);
        assertFalse(s.vencida());

        // No end date (never expires)
        s.setFechaFin(null);
        assertFalse(s.vencida());

        // Expired
        s.setFechaFin(past);
        assertTrue(s.vencida());
    }

    @Test
    void testCancelar() {
        Suscripcion s = new Suscripcion();
        s.setEstado(true);
        s.setFechaCancelacion(null);

        s.cancelar("No longer needed");

        assertFalse(s.getEstado());
        assertNotNull(s.getFechaCancelacion());
        assertEquals("No longer needed", s.getMotivoCancelacion());
    }

    @Test
    void testRenovar() {
        Suscripcion s = new Suscripcion();
        LocalDateTime now = LocalDateTime.now();

        // Renew with no previous end date
        s.setFechaFin(null);
        s.renovar(3);
        assertNotNull(s.getFechaFin());
        assertTrue(s.getFechaFin().isAfter(now.plusMonths(2)));
        assertTrue(s.getFechaFin().isBefore(now.plusMonths(4)));

        // Renew with existing end date
        LocalDateTime originalEnd = s.getFechaFin();
        s.renovar(2);
        assertTrue(s.getFechaFin().isAfter(originalEnd));
        assertTrue(s.getFechaFin().isBefore(originalEnd.plusMonths(3)));
    }

    @Test
    void testGetEstadoSuscripcion() {
        Suscripcion s = new Suscripcion();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime future = now.plusDays(30);
        LocalDateTime in3Days = now.plusDays(3);
        LocalDateTime past = now.minusDays(1);

        // CANCELADA
        s.setEstado(false);
        assertEquals("CANCELADA", s.getEstadoSuscripcion());

        // VENCIDA
        s.setEstado(true);
        s.setFechaFin(past);
        assertEquals("VENCIDA", s.getEstadoSuscripcion());

        // POR_VENCER
        s.setFechaFin(in3Days);
        assertEquals("POR_VENCER", s.getEstadoSuscripcion());

        // ACTIVA
        s.setFechaFin(future);
        assertEquals("ACTIVA", s.getEstadoSuscripcion());
    }

    @Test
    void testToString() {
        Suscripcion s = new Suscripcion();
        s.setIdSuscripcion(1);
        s.setTipoSuscripcion("PREMIUM");

        String toString = s.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("Suscripcion"));
    }
}