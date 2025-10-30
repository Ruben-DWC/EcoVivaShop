package com.ecovivashop.entity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ImagenPerfilTest {

    @Test
    void testDefaultConstructor() {
        ImagenPerfil img = new ImagenPerfil();
        assertNotNull(img);
        assertNull(img.getId());
        assertNull(img.getUsuarioId());
        assertNull(img.getTipoUsuario());
        assertNull(img.getNombreArchivo());
        assertNull(img.getNombreOriginal());
        assertNull(img.getRutaArchivo());
        assertNull(img.getTipoMime());
        assertNull(img.getTamaño());
        assertNotNull(img.getFechaSubida()); // Default value (now)
        assertTrue(img.getActivo()); // Default value
    }

    @Test
    void testCustomConstructor() {
        Long usuarioId = 1L;
        ImagenPerfil.TipoUsuario tipoUsuario = ImagenPerfil.TipoUsuario.CLIENTE;
        String nombreArchivo = "profile_123.jpg";
        String nombreOriginal = "mi_foto.jpg";
        String rutaArchivo = "/uploads/profiles/";
        String tipoMime = "image/jpeg";
        Long tamaño = 2048000L;

        ImagenPerfil img = new ImagenPerfil(usuarioId, tipoUsuario, nombreArchivo,
                                          nombreOriginal, rutaArchivo, tipoMime, tamaño);

        assertEquals(usuarioId, img.getUsuarioId());
        assertEquals(tipoUsuario, img.getTipoUsuario());
        assertEquals(nombreArchivo, img.getNombreArchivo());
        assertEquals(nombreOriginal, img.getNombreOriginal());
        assertEquals(rutaArchivo, img.getRutaArchivo());
        assertEquals(tipoMime, img.getTipoMime());
        assertEquals(tamaño, img.getTamaño());
        assertNotNull(img.getFechaSubida());
        assertTrue(img.getActivo());
    }

    @Test
    void testSettersAndGetters() {
        ImagenPerfil img = new ImagenPerfil();
        LocalDateTime fechaSubida = LocalDateTime.now();

        // Set values
        img.setId(1L);
        img.setUsuarioId(100L);
        img.setTipoUsuario(ImagenPerfil.TipoUsuario.ADMIN);
        img.setNombreArchivo("admin_profile.png");
        img.setNombreOriginal("foto_admin.png");
        img.setRutaArchivo("/uploads/admin/");
        img.setTipoMime("image/png");
        img.setTamaño(1024000L);
        img.setFechaSubida(fechaSubida);
        img.setActivo(false);

        // Verify getters
        assertEquals(1L, img.getId());
        assertEquals(100L, img.getUsuarioId());
        assertEquals(ImagenPerfil.TipoUsuario.ADMIN, img.getTipoUsuario());
        assertEquals("admin_profile.png", img.getNombreArchivo());
        assertEquals("foto_admin.png", img.getNombreOriginal());
        assertEquals("/uploads/admin/", img.getRutaArchivo());
        assertEquals("image/png", img.getTipoMime());
        assertEquals(1024000L, img.getTamaño());
        assertEquals(fechaSubida, img.getFechaSubida());
        assertFalse(img.getActivo());
    }

    @Test
    void testTipoUsuarioEnum() {
        assertEquals(ImagenPerfil.TipoUsuario.ADMIN, ImagenPerfil.TipoUsuario.valueOf("ADMIN"));
        assertEquals(ImagenPerfil.TipoUsuario.CLIENTE, ImagenPerfil.TipoUsuario.valueOf("CLIENTE"));

        // Test all values
        ImagenPerfil.TipoUsuario[] tipos = ImagenPerfil.TipoUsuario.values();
        assertEquals(2, tipos.length);
        assertTrue(java.util.Arrays.asList(tipos).contains(ImagenPerfil.TipoUsuario.ADMIN));
        assertTrue(java.util.Arrays.asList(tipos).contains(ImagenPerfil.TipoUsuario.CLIENTE));
    }

    @Test
    void testToString() {
        ImagenPerfil img = new ImagenPerfil();
        img.setId(1L);
        img.setNombreArchivo("test.jpg");

        String toString = img.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ImagenPerfil"));
    }
}

















