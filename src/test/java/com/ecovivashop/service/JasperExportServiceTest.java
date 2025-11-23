package com.ecovivashop.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ecovivashop.entity.Producto;
import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.Usuario;

class JasperExportServiceTest {

    @Test
    void testExportarProductosJasperPDF() throws Exception {
        JasperExportService jasperExportService = new JasperExportService();

        Producto p1 = new Producto();
        p1.setIdProducto(1);
        p1.setNombre("Producto A");
        p1.setCategoria("Categoria1");
        p1.setPrecio(new BigDecimal("10.50"));

        Producto p2 = new Producto();
        p2.setIdProducto(2);
        p2.setNombre("Producto B");
        p2.setCategoria("Categoria2");
        p2.setPrecio(new BigDecimal("25.75"));

        List<Producto> productos = Arrays.asList(p1, p2);

        byte[] pdfBytes = jasperExportService.exportarProductosJasperPDF(productos);
        assertTrue(pdfBytes != null && pdfBytes.length > 0, "El PDF generado no debe ser vacío");
    }

    @Test
    void testExportarPedidoJasperPDF() throws Exception {
        JasperExportService jasperExportService = new JasperExportService();

        Pedido pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setNumeroPedido("ORD-12345");
        pedido.setTotal(new BigDecimal("150.00"));
        Usuario user = new Usuario();
        user.setNombre("John");
        user.setApellido("Doe");
        pedido.setUsuario(user);

        byte[] pdfBytes = jasperExportService.exportarPedidoJasperPDF(pedido);
        assertTrue(pdfBytes != null && pdfBytes.length > 0, "El PDF de pedido generado no debe ser vacío");
    }
}
