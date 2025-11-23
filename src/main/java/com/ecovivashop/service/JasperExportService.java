package com.ecovivashop.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ecovivashop.entity.Producto;
import com.ecovivashop.entity.Pedido;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class JasperExportService {

    public byte[] exportarProductosJasperPDF(List<Producto> productos) throws JRException, IOException {
        if (productos == null) {
            productos = List.of();
        }

        try (InputStream jrxmlStream = getClass().getResourceAsStream("/reports/productos.jrxml")) {

            if (jrxmlStream == null) {
                throw new IllegalStateException("No se encontró el template JRXML: /reports/productos.jrxml");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(productos);
            Map<String, Object> params = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }

    public byte[] exportarPedidoJasperPDF(Pedido pedido) throws JRException, IOException {
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido no puede ser nulo");
        }

        try (InputStream jrxmlStream = getClass().getResourceAsStream("/reports/pedido.jrxml")) {

            if (jrxmlStream == null) {
                throw new IllegalStateException("No se encontró el template JRXML: /reports/pedido.jrxml");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(jrxmlStream);
            // Use a collection with a single element so fields are read from the Pedido bean
            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(List.of(pedido));
            Map<String, Object> params = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, dataSource);
            return JasperExportManager.exportReportToPdf(jasperPrint);
        }
    }
}
