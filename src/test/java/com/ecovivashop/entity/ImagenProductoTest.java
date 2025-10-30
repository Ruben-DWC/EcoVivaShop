package com.ecovivashop.entity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

class ImagenProductoTest {

    @Test
    void testDefaultConstructor() {
        ImagenProducto img = new ImagenProducto();
        assertNotNull(img);
        assertNull(img.getId());
        assertNull(img.getProductoId());
        assertNull(img.getNombreArchivo());
        assertNull(img.getNombreOriginal());
        assertNull(img.getRutaArchivo());
        assertNull(img.getTipoMime());
        assertNull(img.getTamaño());
        assertEquals(0, img.getOrden()); // Default value
        assertFalse(img.getEsPrincipal()); // Default value
        assertNotNull(img.getFechaSubida()); // Default value (now)
        assertTrue(img.getActivo()); // Default value
        assertNull(img.getAltText());
    }

    @Test
    void testCustomConstructor() {
        Long productoId = 1L;
        String nombreArchivo = "product_123.jpg";
        String nombreOriginal = "imagen_principal.jpg";
        String rutaArchivo = "/uploads/products/";
        String tipoMime = "image/jpeg";
        Long tamaño = 1536000L;
        Integer orden = 1;
        Boolean esPrincipal = true;
        String altText = "Imagen principal del producto ecológico";

        ImagenProducto img = new ImagenProducto(productoId, nombreArchivo, nombreOriginal,
                                              rutaArchivo, tipoMime, tamaño, orden, esPrincipal, altText);

        assertEquals(productoId, img.getProductoId());
        assertEquals(nombreArchivo, img.getNombreArchivo());
        assertEquals(nombreOriginal, img.getNombreOriginal());
        assertEquals(rutaArchivo, img.getRutaArchivo());
        assertEquals(tipoMime, img.getTipoMime());
        assertEquals(tamaño, img.getTamaño());
        assertEquals(orden, img.getOrden());
        assertEquals(esPrincipal, img.getEsPrincipal());
        assertEquals(altText, img.getAltText());
        assertNotNull(img.getFechaSubida());
        assertTrue(img.getActivo());
    }

    @Test
    void testSettersAndGetters() {
        ImagenProducto imagenProducto = new ImagenProducto();
        LocalDateTime fechaSubida = LocalDateTime.now();

        // Set values
        imagenProducto.setId(1L);
        imagenProducto.setProductoId(100L);
        imagenProducto.setNombreArchivo("eco_product.png");
        imagenProducto.setNombreOriginal("producto_ecologico.png");
        imagenProducto.setRutaArchivo("/uploads/products/eco/");
        imagenProducto.setTipoMime("image/png");
        imagenProducto.setTamaño(2048000L);
        imagenProducto.setOrden(2);
        imagenProducto.setEsPrincipal(true);
        imagenProducto.setFechaSubida(fechaSubida);
        imagenProducto.setActivo(false);
        imagenProducto.setAltText("Producto ecológico certificado");

        // Verify getters
        assertEquals(1L, imagenProducto.getId());
        assertEquals(100L, imagenProducto.getProductoId());
        assertEquals("eco_product.png", imagenProducto.getNombreArchivo());
        assertEquals("producto_ecologico.png", imagenProducto.getNombreOriginal());
        assertEquals("/uploads/products/eco/", imagenProducto.getRutaArchivo());
        assertEquals("image/png", imagenProducto.getTipoMime());
        assertEquals(2048000L, imagenProducto.getTamaño());
        assertEquals(2, imagenProducto.getOrden());
        assertTrue(imagenProducto.getEsPrincipal());
        assertEquals(fechaSubida, imagenProducto.getFechaSubida());
        assertFalse(imagenProducto.getActivo());
        assertEquals("Producto ecológico certificado", imagenProducto.getAltText());
    }

    @Test
    void testToString() {
        ImagenProducto imagenProducto = new ImagenProducto();
        imagenProducto.setId(1L);
        imagenProducto.setNombreArchivo("test.jpg");

        String toString = imagenProducto.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("ImagenProducto"));
    }
}