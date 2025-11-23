package com.ecovivashop.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.PedidoDetalle;
import com.ecovivashop.entity.Producto;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.opencsv.CSVWriter;

@Service
public class ExportService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATE_ONLY_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Exportar pedidos a PDF
     */
    public byte[] exportarPedidosPDF(List<Pedido> pedidos, String fechaInicio, String fechaFin, String estados) 
            throws DocumentException, IOException {
        
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Título
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK);
        Paragraph title = new Paragraph("Reporte de Pedidos - EcoVivaShop", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Información del reporte
        com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 10, BaseColor.GRAY);
        Paragraph info = new Paragraph();
        info.add(new Chunk("Generado el: " + java.time.LocalDateTime.now().format(DATE_FORMATTER) + "\n", infoFont));
        if (fechaInicio != null && fechaFin != null) {
            info.add(new Chunk("Periodo: " + fechaInicio + " - " + fechaFin + "\n", infoFont));
        }
        info.add(new Chunk("Estados: " + (estados.equals("todos") ? "Todos" : estados) + "\n", infoFont));
        info.add(new Chunk("Total de pedidos: " + pedidos.size() + "\n", infoFont));
        info.setSpacingAfter(20);
        document.add(info);
        
        // Tabla de pedidos
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15, 25, 15, 20, 15, 15, 15});
        
        // Cabeceras
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        String[] headers = {"N° Pedido", "Cliente", "Fecha", "Productos", "Estado", "Método Pago", "Total"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(76, 175, 80));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
        
        // Datos
        com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
        for (Pedido pedido : pedidos) {
            table.addCell(new PdfPCell(new Phrase("#" + pedido.getNumeroPedido(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(pedido.getFechaPedido().format(DATE_ONLY_FORMATTER), dataFont)));
            
            // Productos
            StringBuilder productos = new StringBuilder();
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                productos.append(detalle.getProducto().getNombre()).append(" (").append(detalle.getCantidad()).append("), ");
            }
            String productosStr = productos.toString();
            if (productosStr.length() > 2) {
                productosStr = productosStr.substring(0, productosStr.length() - 2);
            }
            table.addCell(new PdfPCell(new Phrase(productosStr, dataFont)));
            
            table.addCell(new PdfPCell(new Phrase(pedido.getEstado(), dataFont)));
            table.addCell(new PdfPCell(new Phrase(pedido.getMetodoPago(), dataFont)));
            table.addCell(new PdfPCell(new Phrase("S/ " + pedido.getTotal().toString(), dataFont)));
        }
        
        document.add(table);
        document.close();
        
        return baos.toByteArray();
    }

    /**
     * Exportar pedidos a Excel
     */
    public byte[] exportarPedidosExcel(List<Pedido> pedidos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            Sheet sheet = workbook.createSheet("Pedidos");
        
        // Estilo para cabeceras
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        
        // Estilo para datos
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        dataStyle.setAlignment(HorizontalAlignment.LEFT);
        dataStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        
        // Crear cabeceras
        Row headerRow = sheet.createRow(0);
        String[] headers = {"N° Pedido", "Cliente", "Email", "Fecha", "Estado", "Método Pago", "Total", "Productos"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Llenar datos
        int rowNum = 1;
        for (Pedido pedido : pedidos) {
            Row row = sheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue("#" + pedido.getNumeroPedido());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(pedido.getUsuario().getEmail());
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(pedido.getFechaPedido().format(DATE_FORMATTER));
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(pedido.getEstado());
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(pedido.getMetodoPago());
            cell5.setCellStyle(dataStyle);
            
            Cell cell6 = row.createCell(6);
            cell6.setCellValue("S/ " + pedido.getTotal().toString());
            cell6.setCellStyle(dataStyle);
            
            // Productos
            StringBuilder productos = new StringBuilder();
            for (PedidoDetalle detalle : pedido.getDetalles()) {
                productos.append(detalle.getProducto().getNombre())
                         .append(" (Cant: ").append(detalle.getCantidad())
                         .append(", Precio: S/ ").append(detalle.getPrecioUnitario())
                         .append("), ");
            }
            String productosStr = productos.toString();
            if (productosStr.length() > 2) {
                productosStr = productosStr.substring(0, productosStr.length() - 2);
            }
            
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(productosStr);
            cell7.setCellStyle(dataStyle);
        }
        
        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
            if (sheet.getColumnWidth(i) > 15000) {
                sheet.setColumnWidth(i, 15000);
            }
        }
        
        workbook.write(baos);
        return baos.toByteArray();
        } // Cierre del try-with-resources
    }

    /**
     * Exportar pedidos a CSV
     */
    public byte[] exportarPedidosCSV(List<Pedido> pedidos) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Cabeceras
            String[] headers = {"N° Pedido", "Cliente", "Email", "Fecha", "Estado", "Método Pago", "Total", "Productos"};
            writer.writeNext(headers);
            
            // Datos
            for (Pedido pedido : pedidos) {
                StringBuilder productos = new StringBuilder();
                for (PedidoDetalle detalle : pedido.getDetalles()) {
                    productos.append(detalle.getProducto().getNombre())
                             .append(" (").append(detalle.getCantidad()).append(") ");
                }
                
                String[] data = {
                    "#" + pedido.getNumeroPedido(),
                    pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(),
                    pedido.getUsuario().getEmail(),
                    pedido.getFechaPedido().format(DATE_FORMATTER),
                    pedido.getEstado(),
                    pedido.getMetodoPago(),
                    "S/ " + pedido.getTotal().toString(),
                    productos.toString().trim()
                };
                writer.writeNext(data);
            }
            
            writer.flush();
            return baos.toByteArray();
        }
    }
    
    /**
     * Exportar reporte general a PDF
     */
    public byte[] exportarReporteGeneralPDF(List<Pedido> pedidos) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Título del reporte
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph title = new Paragraph("Reporte General de Pedidos - EcoVivaShop", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Información del reporte
        com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph info = new Paragraph();
        info.add(new Chunk("Generado el: " + java.time.LocalDateTime.now().format(DATE_FORMATTER) + "\n", infoFont));
        info.add(new Chunk("Total de pedidos incluidos: " + pedidos.size() + "\n", infoFont));
        info.setSpacingAfter(20);
        document.add(info);
        
        // Estadísticas generales
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Paragraph statsTitle = new Paragraph("Estadísticas Generales", sectionFont);
        statsTitle.setSpacingAfter(10);
        document.add(statsTitle);
        
        // Calcular estadísticas
        long totalPedidos = pedidos.size();
        long pendientes = pedidos.stream().mapToLong(p -> "PENDIENTE".equals(p.getEstado()) ? 1 : 0).sum();
        long entregados = pedidos.stream().mapToLong(p -> "ENTREGADO".equals(p.getEstado()) ? 1 : 0).sum();
        double ingresosTotales = pedidos.stream()
            .filter(p -> "ENTREGADO".equals(p.getEstado()))
            .mapToDouble(p -> p.getTotal().doubleValue())
            .sum();
        
        com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        Paragraph stats = new Paragraph();
        stats.add(new Chunk("• Total de pedidos: " + totalPedidos + "\n", dataFont));
        stats.add(new Chunk("• Pedidos pendientes: " + pendientes + "\n", dataFont));
        stats.add(new Chunk("• Pedidos entregados: " + entregados + "\n", dataFont));
        stats.add(new Chunk("• Ingresos totales: S/ " + String.format("%.2f", ingresosTotales) + "\n", dataFont));
        stats.setSpacingAfter(20);
        document.add(stats);
        
        // Tabla de pedidos más recientes (últimos 20)
        Paragraph tableTitle = new Paragraph("Pedidos Recientes", sectionFont);
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        
        List<Pedido> pedidosRecientes = pedidos.stream()
            .sorted((p1, p2) -> p2.getFechaPedido().compareTo(p1.getFechaPedido()))
            .limit(20)
            .toList();
        
        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{20, 25, 15, 15, 15, 15});
        
        // Cabeceras
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        String[] headers = {"N° Pedido", "Cliente", "Fecha", "Estado", "Método Pago", "Total"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(76, 175, 80));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(8);
            table.addCell(cell);
        }
        
        // Datos
        com.itextpdf.text.Font tableDataFont = FontFactory.getFont(FontFactory.HELVETICA, 9, BaseColor.BLACK);
        for (Pedido pedido : pedidosRecientes) {
            table.addCell(new PdfPCell(new Phrase("#" + pedido.getNumeroPedido(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(pedido.getFechaPedido().format(DATE_ONLY_FORMATTER), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(pedido.getEstado(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(pedido.getMetodoPago(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase("S/ " + pedido.getTotal().toString(), tableDataFont)));
        }
        
        document.add(table);
        document.close();
        
        return baos.toByteArray();
    }
    
    /**
     * Exportar reporte general a Excel
     */
    public byte[] exportarReporteGeneralExcel(List<Pedido> pedidos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Hoja de estadísticas
            Sheet statsSheet = workbook.createSheet("Estadísticas");
        
        // Estilo para títulos
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        
        // Estilo para datos
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        
        // Título de la hoja
        Row titleRow = statsSheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Reporte General de Pedidos - EcoVivaShop");
        titleCell.setCellStyle(titleStyle);
        
        // Estadísticas
        long totalPedidos = pedidos.size();
        long pendientes = pedidos.stream().mapToLong(p -> "PENDIENTE".equals(p.getEstado()) ? 1 : 0).sum();
        long entregados = pedidos.stream().mapToLong(p -> "ENTREGADO".equals(p.getEstado()) ? 1 : 0).sum();
        double ingresosTotales = pedidos.stream()
            .filter(p -> "ENTREGADO".equals(p.getEstado()))
            .mapToDouble(p -> p.getTotal().doubleValue())
            .sum();
        
        statsSheet.createRow(2).createCell(0).setCellValue("Total de pedidos:");
        statsSheet.getRow(2).createCell(1).setCellValue(totalPedidos);
        
        statsSheet.createRow(3).createCell(0).setCellValue("Pedidos pendientes:");
        statsSheet.getRow(3).createCell(1).setCellValue(pendientes);
        
        statsSheet.createRow(4).createCell(0).setCellValue("Pedidos entregados:");
        statsSheet.getRow(4).createCell(1).setCellValue(entregados);
        
        statsSheet.createRow(5).createCell(0).setCellValue("Ingresos totales:");
        statsSheet.getRow(5).createCell(1).setCellValue("S/ " + String.format("%.2f", ingresosTotales));
        
        // Hoja de pedidos
        Sheet pedidosSheet = workbook.createSheet("Todos los Pedidos");
        
        // Estilo para cabeceras
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        
        // Crear cabeceras
        Row headerRow = pedidosSheet.createRow(0);
        String[] headers = {"N° Pedido", "Cliente", "Email", "Fecha", "Estado", "Método Pago", "Total"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Llenar datos
        int rowNum = 1;
        for (Pedido pedido : pedidos) {
            Row row = pedidosSheet.createRow(rowNum++);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue("#" + pedido.getNumeroPedido());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(pedido.getUsuario().getEmail());
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(pedido.getFechaPedido().format(DATE_FORMATTER));
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(pedido.getEstado());
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(pedido.getMetodoPago());
            cell5.setCellStyle(dataStyle);
            
            Cell cell6 = row.createCell(6);
            cell6.setCellValue("S/ " + pedido.getTotal().toString());
            cell6.setCellStyle(dataStyle);
        }
        
        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            pedidosSheet.autoSizeColumn(i);
            if (pedidosSheet.getColumnWidth(i) > 15000) {
                pedidosSheet.setColumnWidth(i, 15000);
            }
        }
        
        // Ajustar columnas de estadísticas
        statsSheet.autoSizeColumn(0);
        statsSheet.autoSizeColumn(1);
        
        workbook.write(baos);
        return baos.toByteArray();
        } // Cierre del try-with-resources
    }
    
    /**
     * Exportar productos a PDF
     */
    public byte[] exportarProductosPDF(List<Producto> productos) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Título del reporte
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph title = new Paragraph("Reporte de Productos - EcoVivaShop", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Información del reporte
        com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph info = new Paragraph();
        info.add(new Chunk("Generado el: " + java.time.LocalDateTime.now().format(DATE_FORMATTER) + "\n", infoFont));
        info.add(new Chunk("Total de productos: " + productos.size() + "\n", infoFont));
        info.setSpacingAfter(20);
        document.add(info);
        
        // Estadísticas generales
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Paragraph statsTitle = new Paragraph("Estadísticas del Inventario", sectionFont);
        statsTitle.setSpacingAfter(10);
        document.add(statsTitle);
        
        // Calcular estadísticas
        long totalProductos = productos.size();
        long productosActivos = productos.stream().mapToLong(p -> Boolean.TRUE.equals(p.getEstado()) ? 1 : 0).sum();
        long productosInactivos = productos.stream().mapToLong(p -> Boolean.FALSE.equals(p.getEstado()) ? 1 : 0).sum();
        double stockTotal = productos.stream().mapToDouble(p -> p.getStockDisponible() != null ? p.getStockDisponible().doubleValue() : 0).sum();
        double valorInventario = productos.stream()
            .filter(p -> Boolean.TRUE.equals(p.getEstado()))
            .mapToDouble(p -> p.getPrecio().doubleValue() * (p.getStockDisponible() != null ? p.getStockDisponible().doubleValue() : 0))
            .sum();
        
        com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        Paragraph stats = new Paragraph();
        stats.add(new Chunk("• Total de productos: " + totalProductos + "\n", dataFont));
        stats.add(new Chunk("• Productos activos: " + productosActivos + "\n", dataFont));
        stats.add(new Chunk("• Productos inactivos: " + productosInactivos + "\n", dataFont));
        stats.add(new Chunk("• Stock total: " + String.format("%.0f", stockTotal) + " unidades\n", dataFont));
        stats.add(new Chunk("• Valor del inventario: S/ " + String.format("%.2f", valorInventario) + "\n", dataFont));
        stats.setSpacingAfter(20);
        document.add(stats);
        
        // Tabla de productos
        Paragraph tableTitle = new Paragraph("Lista de Productos", sectionFont);
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{15, 25, 20, 10, 10, 10, 10});
        
        // Cabeceras
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, BaseColor.WHITE);
        String[] headers = {"ID", "Nombre", "Categoría", "Precio", "Stock", "Estado", "Eco Score"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(76, 175, 80));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }
        
        // Datos
        com.itextpdf.text.Font tableDataFont = FontFactory.getFont(FontFactory.HELVETICA, 8, BaseColor.BLACK);
        for (Producto producto : productos) {
            table.addCell(new PdfPCell(new Phrase(producto.getIdProducto().toString(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(producto.getNombre(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(producto.getCategoria(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase("S/ " + producto.getPrecio().toString(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(producto.getStockDisponible() != null ? producto.getStockDisponible().toString() : "0", tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(Boolean.TRUE.equals(producto.getEstado()) ? "ACTIVO" : "INACTIVO", tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(producto.getPuntuacionEco() != null ? producto.getPuntuacionEco().toString() : "N/A", tableDataFont)));
        }
        
        document.add(table);
        document.close();
        
        return baos.toByteArray();
    }
    
    /**
     * Exportar productos a Excel
     */
    public byte[] exportarProductosExcel(List<Producto> productos) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Hoja de estadísticas
            Sheet statsSheet = workbook.createSheet("Estadísticas del Inventario");
        
            // Estilo para títulos
            CellStyle titleStyle = workbook.createCellStyle();
            Font titleFont = workbook.createFont();
            titleFont.setBold(true);
            titleFont.setFontHeightInPoints((short) 14);
            titleStyle.setFont(titleFont);
            
            // Estilo para datos
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            
            // Título de la hoja
            Row titleRow = statsSheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Reporte de Productos - EcoVivaShop");
            titleCell.setCellStyle(titleStyle);
            
            // Estadísticas
            long totalProductos = productos.size();
            long productosActivos = productos.stream().mapToLong(p -> Boolean.TRUE.equals(p.getEstado()) ? 1 : 0).sum();
            long productosInactivos = productos.stream().mapToLong(p -> Boolean.FALSE.equals(p.getEstado()) ? 1 : 0).sum();
            double stockTotal = productos.stream().mapToDouble(p -> p.getStockDisponible() != null ? p.getStockDisponible().doubleValue() : 0).sum();
            double valorInventario = productos.stream()
                .filter(p -> Boolean.TRUE.equals(p.getEstado()))
                .mapToDouble(p -> p.getPrecio().doubleValue() * (p.getStockDisponible() != null ? p.getStockDisponible().doubleValue() : 0))
                .sum();
            
            statsSheet.createRow(2).createCell(0).setCellValue("Total de productos:");
            statsSheet.getRow(2).createCell(1).setCellValue(totalProductos);
            
            statsSheet.createRow(3).createCell(0).setCellValue("Productos activos:");
            statsSheet.getRow(3).createCell(1).setCellValue(productosActivos);
            
            statsSheet.createRow(4).createCell(0).setCellValue("Productos inactivos:");
            statsSheet.getRow(4).createCell(1).setCellValue(productosInactivos);
            
            statsSheet.createRow(5).createCell(0).setCellValue("Stock total:");
            statsSheet.getRow(5).createCell(1).setCellValue(String.format("%.0f unidades", stockTotal));
            
            statsSheet.createRow(6).createCell(0).setCellValue("Valor del inventario:");
            statsSheet.getRow(6).createCell(1).setCellValue("S/ " + String.format("%.2f", valorInventario));
            
            // Hoja de productos
            Sheet productosSheet = workbook.createSheet("Lista de Productos");
            
            // Estilo para cabeceras
            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            
            // Crear cabeceras
            Row headerRow = productosSheet.createRow(0);
            String[] headers = {"ID", "Nombre", "Descripción", "Categoría", "Precio", "Stock Actual", "Stock Mínimo", "Stock Máximo", "Estado Stock", "Ubicación", "Estado", "Última Actualización", "Usuario Actualización"};
            
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Llenar datos
            int rowNum = 1;
            for (Producto producto : productos) {
                Row row = productosSheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(producto.getIdProducto().toString());
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(producto.getNombre());
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(producto.getDescripcion() != null ? producto.getDescripcion() : "");
                cell2.setCellStyle(dataStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(producto.getCategoria());
                cell3.setCellStyle(dataStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue("S/ " + producto.getPrecio().toString());
                cell4.setCellStyle(dataStyle);
                
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(producto.getStockDisponible() != null ? producto.getStockDisponible().toString() : "0");
                cell5.setCellStyle(dataStyle);
                
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(producto.getInventario() != null && producto.getInventario().getStockMinimo() != null ? 
                    producto.getInventario().getStockMinimo().toString() : "0");
                cell6.setCellStyle(dataStyle);
                
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(producto.getInventario() != null && producto.getInventario().getStockMaximo() != null ? 
                    producto.getInventario().getStockMaximo().toString() : "N/A");
                cell7.setCellStyle(dataStyle);
                
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(producto.getEstado());
                cell8.setCellStyle(dataStyle);
                
                Cell cell9 = row.createCell(9);
                cell9.setCellValue(producto.getInventario() != null && producto.getInventario().getUbicacion() != null ? 
                    producto.getInventario().getUbicacion() : "N/A");
                cell9.setCellStyle(dataStyle);
                
                Cell cell10 = row.createCell(10);
                cell10.setCellValue(Boolean.TRUE.equals(producto.getEstado()) ? "ACTIVO" : "INACTIVO");
                cell10.setCellStyle(dataStyle);
                
                Cell cell11 = row.createCell(11);
                cell11.setCellValue(producto.getFechaCreacion() != null ? 
                    producto.getFechaCreacion().format(DATE_ONLY_FORMATTER) : "");
                cell11.setCellStyle(dataStyle);
                
                Cell cell12 = row.createCell(12);
                cell12.setCellValue(producto.getInventario() != null && producto.getInventario().getUsuarioActualizacion() != null ? 
                    producto.getInventario().getUsuarioActualizacion() : "Sistema");
                cell12.setCellStyle(dataStyle);
            }
            
            // Ajustar ancho de columnas
            for (int i = 0; i < headers.length; i++) {
                productosSheet.autoSizeColumn(i);
                if (productosSheet.getColumnWidth(i) > 15000) {
                    productosSheet.setColumnWidth(i, 15000);
                }
            }
            
            // Ajustar columnas de estadísticas
            statsSheet.autoSizeColumn(0);
            statsSheet.autoSizeColumn(1);
            
            workbook.write(baos);
            return baos.toByteArray();
        } // Cierre del try-with-resources
    }

    /**
     * Generar boleta/factura en PDF para cliente
     */
    @SuppressWarnings("unchecked")
    public byte[] generarBoletaPDF(java.util.Map<String, Object> pedido) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Datos del pedido
        String numeroPedido = (String) pedido.get("numeroPedido");
        java.time.LocalDateTime fechaPedido = (java.time.LocalDateTime) pedido.get("fechaPedido");
        double subtotal = ((Number) pedido.get("subtotal")).doubleValue();
        double envio = ((Number) pedido.get("envio")).doubleValue();
        double descuento = ((Number) pedido.get("descuento")).doubleValue();
        double igv = ((Number) pedido.get("igv")).doubleValue();
        double total = ((Number) pedido.get("total")).doubleValue();
        String metodoPago = (String) pedido.get("metodoPago");
        
        java.util.Map<String, Object> datosCliente = 
            (java.util.Map<String, Object>) pedido.get("datosCliente");
        java.util.List<java.util.Map<String, Object>> productos = 
            (java.util.List<java.util.Map<String, Object>>) pedido.get("productos");
        
        // Título principal
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.GREEN);
        Paragraph title = new Paragraph("ECOVIVASHOP", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);
        
        // Subtítulo
        com.itextpdf.text.Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
        Paragraph subtitle = new Paragraph("Boleta de Venta Electrónica", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
        
        // Información de la empresa y pedido
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20);
        
        // Datos empresa
        PdfPCell empresaCell = new PdfPCell();
        empresaCell.setBorder(0);
        empresaCell.addElement(new Paragraph("EcoVivaShop E.I.R.L.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        empresaCell.addElement(new Paragraph("RUC: 20123456789", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        empresaCell.addElement(new Paragraph("Av. Ecológica 123, Lima", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        empresaCell.addElement(new Paragraph("Teléfono: (01) 234-5678", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        headerTable.addCell(empresaCell);
        
        // Datos del pedido
        PdfPCell pedidoCell = new PdfPCell();
        pedidoCell.setBorder(0);
        pedidoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pedidoCell.addElement(new Paragraph("BOLETA DE VENTA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        pedidoCell.addElement(new Paragraph("N° " + numeroPedido, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        pedidoCell.addElement(new Paragraph("Fecha: " + fechaPedido.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        headerTable.addCell(pedidoCell);
        
        document.add(headerTable);
        
        // Datos del cliente
        PdfPTable clienteTable = new PdfPTable(1);
        clienteTable.setWidthPercentage(100);
        clienteTable.setSpacingAfter(20);
        
        PdfPCell clienteCell = new PdfPCell();
        clienteCell.setBorder(1);
        clienteCell.setPadding(10);
        clienteCell.addElement(new Paragraph("DATOS DEL CLIENTE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        clienteCell.addElement(new Paragraph("Cliente: " + datosCliente.get("nombres") + " " + datosCliente.get("apellidos"), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        clienteCell.addElement(new Paragraph(datosCliente.get("tipoDocumento") + ": " + datosCliente.get("numeroDocumento"), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        clienteCell.addElement(new Paragraph("Dirección: " + datosCliente.get("direccion") + ", " + 
            datosCliente.get("distrito") + ", " + datosCliente.get("provincia") + ", " + datosCliente.get("departamento"), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        clienteCell.addElement(new Paragraph("Teléfono: " + datosCliente.get("telefono"), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        clienteTable.addCell(clienteCell);
        
        document.add(clienteTable);
        
        // Detalle de productos
        PdfPTable productosTable = new PdfPTable(5);
        productosTable.setWidthPercentage(100);
        productosTable.setWidths(new float[]{1, 4, 1, 2, 2});
        productosTable.setSpacingAfter(20);
        
        // Headers
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE);
        String[] headers = {"Cant.", "Descripción", "P.Unit", "Descuento", "Importe"};
        
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, headerFont));
            headerCell.setBackgroundColor(BaseColor.GREEN);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(8);
            productosTable.addCell(headerCell);
        }
        
        // Productos
        com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
        for (java.util.Map<String, Object> producto : productos) {
            int cantidad = (Integer) producto.get("cantidad");
            String nombre = (String) producto.get("nombre");
            double precio = ((Number) producto.get("precio")).doubleValue();
            
            productosTable.addCell(new PdfPCell(new Phrase(String.valueOf(cantidad), dataFont)));
            productosTable.addCell(new PdfPCell(new Phrase(nombre, dataFont)));
            productosTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", precio), dataFont)));
            productosTable.addCell(new PdfPCell(new Phrase("S/ 0.00", dataFont)));
            productosTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", precio * cantidad), dataFont)));
        }
        
        document.add(productosTable);
        
        // Resumen de totales
        PdfPTable totalesTable = new PdfPTable(2);
        totalesTable.setWidthPercentage(50);
        totalesTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalesTable.setWidths(new float[]{3, 2});
        
        com.itextpdf.text.Font totalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
        com.itextpdf.text.Font totalBoldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
        
        // Subtotal
        totalesTable.addCell(new PdfPCell(new Phrase("Subtotal:", totalFont)));
        totalesTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", subtotal), totalFont)));
        
        // Descuento
        totalesTable.addCell(new PdfPCell(new Phrase("Descuento Eco (5%):", totalFont)));
        totalesTable.addCell(new PdfPCell(new Phrase("-S/ " + String.format("%.2f", descuento), totalFont)));
        
        // Envío
        totalesTable.addCell(new PdfPCell(new Phrase("Envío:", totalFont)));
        totalesTable.addCell(new PdfPCell(new Phrase(envio > 0 ? "S/ " + String.format("%.2f", envio) : "GRATIS", totalFont)));
        
        // IGV
        totalesTable.addCell(new PdfPCell(new Phrase("IGV (18%):", totalFont)));
        totalesTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", igv), totalFont)));
        
        // Total
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL:", totalBoldFont));
        totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalesTable.addCell(totalLabelCell);
        
        PdfPCell totalValueCell = new PdfPCell(new Phrase("S/ " + String.format("%.2f", total), totalBoldFont));
        totalValueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalesTable.addCell(totalValueCell);
        
        document.add(totalesTable);
        
        // Información adicional
        document.add(new Paragraph("\n"));
        document.add(new Paragraph("Método de Pago: " + metodoPago, FontFactory.getFont(FontFactory.HELVETICA, 10)));
        document.add(new Paragraph("Fecha de Entrega Estimada: " + 
            fechaPedido.plusDays(3).format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        
        // Pie de página
        document.add(new Paragraph("\n"));
        Paragraph footer = new Paragraph("¡Gracias por elegir productos eco-amigables! 🌱", 
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GREEN));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        document.close();
        return baos.toByteArray();
    }

    /**
     * Generar boleta/factura en PDF desde entidad Pedido
     */
    public byte[] generarBoletaPDFDesdePedido(com.ecovivashop.entity.Pedido pedido) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Título principal
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.GREEN);
        Paragraph title = new Paragraph("ECOVIVASHOP", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);
        
        // Subtítulo
        com.itextpdf.text.Font subtitleFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.DARK_GRAY);
        Paragraph subtitle = new Paragraph("Factura de Venta Electrónica", subtitleFont);
        subtitle.setAlignment(Element.ALIGN_CENTER);
        subtitle.setSpacingAfter(20);
        document.add(subtitle);
        
        // Información de la empresa y pedido
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setSpacingAfter(20);
        
        // Datos empresa
        PdfPCell empresaCell = new PdfPCell();
        empresaCell.setBorder(0);
        empresaCell.addElement(new Paragraph("EcoVivaShop E.I.R.L.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        empresaCell.addElement(new Paragraph("RUC: 20123456789", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        empresaCell.addElement(new Paragraph("Av. Ecológica 123, Lima", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        empresaCell.addElement(new Paragraph("Teléfono: (01) 234-5678", FontFactory.getFont(FontFactory.HELVETICA, 10)));
        headerTable.addCell(empresaCell);
        
        // Datos del pedido
        PdfPCell pedidoCell = new PdfPCell();
        pedidoCell.setBorder(0);
        pedidoCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        pedidoCell.addElement(new Paragraph("FACTURA DE VENTA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        pedidoCell.addElement(new Paragraph("N° " + pedido.getNumeroPedido(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10)));
        pedidoCell.addElement(new Paragraph("Fecha: " + pedido.getFechaPedido().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        headerTable.addCell(pedidoCell);
        
        document.add(headerTable);
        
        // Datos del cliente
        PdfPTable clienteTable = new PdfPTable(1);
        clienteTable.setWidthPercentage(100);
        clienteTable.setSpacingAfter(20);
        
        PdfPCell clienteCell = new PdfPCell();
        clienteCell.setBorder(1);
        clienteCell.setPadding(10);
        clienteCell.addElement(new Paragraph("DATOS DEL CLIENTE", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        clienteCell.addElement(new Paragraph("Cliente: " + pedido.getUsuario().getNombre() + " " + pedido.getUsuario().getApellido(), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        clienteCell.addElement(new Paragraph("Email: " + pedido.getUsuario().getEmail(), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        if (pedido.getDireccionEnvio() != null) {
            clienteCell.addElement(new Paragraph("Dirección: " + pedido.getDireccionEnvio(), 
                FontFactory.getFont(FontFactory.HELVETICA, 10)));
        }
        clienteTable.addCell(clienteCell);
        document.add(clienteTable);
        
        // Tabla de productos
        PdfPTable productosTable = new PdfPTable(5);
        productosTable.setWidthPercentage(100);
        productosTable.setWidths(new float[]{3, 1, 2, 2, 2});
        productosTable.setSpacingAfter(20);
        
        // Headers de la tabla
        String[] headers = {"Producto", "Cant.", "P. Unit.", "Descuento", "Subtotal"};
        for (String header : headers) {
            PdfPCell headerCell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, BaseColor.WHITE)));
            headerCell.setBackgroundColor(BaseColor.GREEN);
            headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            headerCell.setPadding(5);
            productosTable.addCell(headerCell);
        }
        
        // Agregar productos
        if (pedido.getDetalles() != null) {
            for (com.ecovivashop.entity.PedidoDetalle detalle : pedido.getDetalles()) {
                productosTable.addCell(new PdfPCell(new Phrase(detalle.getProducto().getNombre(), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9))));
                productosTable.addCell(new PdfPCell(new Phrase(String.valueOf(detalle.getCantidad()), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9))));
                productosTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", detalle.getPrecioUnitario()), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9))));
                productosTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", detalle.getDescuentoUnitario() != null ? detalle.getDescuentoUnitario() : 0.00), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9))));
                productosTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", detalle.getSubtotal()), 
                    FontFactory.getFont(FontFactory.HELVETICA, 9))));
            }
        }
        
        document.add(productosTable);
        
        // Tabla de totales
        PdfPTable totalesTable = new PdfPTable(2);
        totalesTable.setWidthPercentage(50);
        totalesTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalesTable.setSpacingAfter(20);
        
        // Subtotal
        totalesTable.addCell(new PdfPCell(new Phrase("Subtotal:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10))));
        totalesTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", pedido.getSubtotal() != null ? pedido.getSubtotal() : 0.00), 
            FontFactory.getFont(FontFactory.HELVETICA, 10))));
        
        // Costo de envío
        totalesTable.addCell(new PdfPCell(new Phrase("Envío:", FontFactory.getFont(FontFactory.HELVETICA, 10))));
        totalesTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", pedido.getCostoEnvio() != null ? pedido.getCostoEnvio() : 0.00), 
            FontFactory.getFont(FontFactory.HELVETICA, 10))));
        
        // Descuento
        if (pedido.getDescuento() != null && pedido.getDescuento().compareTo(java.math.BigDecimal.ZERO) > 0) {
            totalesTable.addCell(new PdfPCell(new Phrase("Descuento:", FontFactory.getFont(FontFactory.HELVETICA, 10))));
            totalesTable.addCell(new PdfPCell(new Phrase("-S/ " + String.format("%.2f", pedido.getDescuento()), 
                FontFactory.getFont(FontFactory.HELVETICA, 10))));
        }
        
        // IGV
        if (pedido.getImpuestos() != null && pedido.getImpuestos().compareTo(java.math.BigDecimal.ZERO) > 0) {
            totalesTable.addCell(new PdfPCell(new Phrase("IGV (18%):", FontFactory.getFont(FontFactory.HELVETICA, 10))));
            totalesTable.addCell(new PdfPCell(new Phrase("S/ " + String.format("%.2f", pedido.getImpuestos()), 
                FontFactory.getFont(FontFactory.HELVETICA, 10))));
        }
        
        // Total
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("TOTAL:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        totalLabelCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalesTable.addCell(totalLabelCell);
        
        PdfPCell totalValueCell = new PdfPCell(new Phrase("S/ " + String.format("%.2f", pedido.getTotal()), 
            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
        totalValueCell.setBackgroundColor(BaseColor.LIGHT_GRAY);
        totalesTable.addCell(totalValueCell);
        
        document.add(totalesTable);
        
        // Información del pago
        document.add(new Paragraph("Método de Pago: " + (pedido.getMetodoPago() != null ? pedido.getMetodoPago() : "No especificado"), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        document.add(new Paragraph("Estado: " + pedido.getEstado(), 
            FontFactory.getFont(FontFactory.HELVETICA, 10)));
        
        // Pie de página
        document.add(new Paragraph("\n"));
        Paragraph footer = new Paragraph("¡Gracias por elegir productos eco-amigables! 🌱", 
            FontFactory.getFont(FontFactory.HELVETICA_OBLIQUE, 10, BaseColor.GREEN));
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
        
        document.close();
        return baos.toByteArray();
    }
    
    /**
     * Exportar inventario a PDF
     */
    public byte[] exportarInventarioPDF(List<Inventario> inventarios) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate()); // Horizontal para más columnas
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        
        document.open();
        
        // Título del reporte
        com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20, BaseColor.BLACK);
        Paragraph title = new Paragraph("Reporte de Inventario - EcoVivaShop", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);
        
        // Información del reporte
        com.itextpdf.text.Font infoFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY);
        Paragraph info = new Paragraph();
        info.add(new Chunk("Generado el: " + java.time.LocalDateTime.now().format(DATE_FORMATTER) + "\n", infoFont));
        info.add(new Chunk("Total de productos en inventario: " + inventarios.size() + "\n", infoFont));
        info.setSpacingAfter(20);
        document.add(info);
        
        // Estadísticas generales
        com.itextpdf.text.Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Paragraph statsTitle = new Paragraph("Estadísticas del Inventario", sectionFont);
        statsTitle.setSpacingAfter(10);
        document.add(statsTitle);
        
        // Calcular estadísticas
        long totalProductos = inventarios.size();
        long productosConStock = inventarios.stream().mapToLong(i -> i.getStock() > 0 ? 1 : 0).sum();
        long productosAgotados = inventarios.stream().mapToLong(i -> i.agotado() ? 1 : 0).sum();
        long productosStockBajo = inventarios.stream().mapToLong(i -> i.necesitaReposicion() && !i.agotado() ? 1 : 0).sum();
        long productosCriticos = inventarios.stream().mapToLong(i -> i.stockCritico() ? 1 : 0).sum();
        long stockTotal = inventarios.stream().mapToLong(i -> i.getStock() != null ? i.getStock().longValue() : 0L).sum();
        
        com.itextpdf.text.Font dataFont = FontFactory.getFont(FontFactory.HELVETICA, 11, BaseColor.BLACK);
        Paragraph stats = new Paragraph();
        stats.add(new Chunk("• Total de productos: " + totalProductos + "\n", dataFont));
        stats.add(new Chunk("• Productos con stock: " + productosConStock + "\n", dataFont));
        stats.add(new Chunk("• Productos agotados: " + productosAgotados + "\n", dataFont));
        stats.add(new Chunk("• Productos con stock bajo: " + productosStockBajo + "\n", dataFont));
        stats.add(new Chunk("• Productos en estado crítico: " + productosCriticos + "\n", dataFont));
        stats.add(new Chunk("• Stock total acumulado: " + stockTotal + " unidades\n", dataFont));
        stats.setSpacingAfter(20);
        document.add(stats);
        
        // Tabla de inventario
        Paragraph tableTitle = new Paragraph("Detalle del Inventario", sectionFont);
        tableTitle.setSpacingAfter(10);
        document.add(tableTitle);
        
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{8, 20, 12, 8, 8, 8, 10, 12, 14});
        
        // Cabeceras
        com.itextpdf.text.Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, BaseColor.WHITE);
        String[] headers = {"ID", "Producto", "Categoría", "Stock", "Mínimo", "Estado", "Ubicación", "Última Actualización", "Usuario"};
        
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new BaseColor(76, 175, 80));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(5);
            table.addCell(cell);
        }
        
        // Datos
        com.itextpdf.text.Font tableDataFont = FontFactory.getFont(FontFactory.HELVETICA, 7, BaseColor.BLACK);
        for (Inventario inventario : inventarios) {
            Producto producto = inventario.getProducto();
            
            table.addCell(new PdfPCell(new Phrase(producto.getIdProducto().toString(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(producto.getNombre(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(producto.getCategoria(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(inventario.getStock().toString(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(inventario.getStockMinimo().toString(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(inventario.getEstadoStock(), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(inventario.getUbicacion() != null ? inventario.getUbicacion() : "N/A", tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(inventario.getFechaActualizacion().format(DATE_FORMATTER), tableDataFont)));
            table.addCell(new PdfPCell(new Phrase(inventario.getUsuarioActualizacion() != null ? inventario.getUsuarioActualizacion() : "Sistema", tableDataFont)));
        }
        
        document.add(table);
        document.close();
        
        return baos.toByteArray();
    }
    
    /**
     * Exportar inventario a Excel
     */
    public byte[] exportarInventarioExcel(List<Inventario> inventarios) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            
            // Hoja de estadísticas
            Sheet statsSheet = workbook.createSheet("Estadísticas del Inventario");
        
        // Estilo para títulos
        CellStyle titleStyle = workbook.createCellStyle();
        Font titleFont = workbook.createFont();
        titleFont.setBold(true);
        titleFont.setFontHeightInPoints((short) 14);
        titleStyle.setFont(titleFont);
        
        // Estilo para datos
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        
        // Título de la hoja
        Row titleRow = statsSheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Reporte de Inventario - EcoVivaShop");
        titleCell.setCellStyle(titleStyle);
        
        // Estadísticas
        long totalProductos = inventarios.size();
        long productosConStock = inventarios.stream().mapToLong(i -> i.getStock() > 0 ? 1 : 0).sum();
        long productosAgotados = inventarios.stream().mapToLong(i -> i.agotado() ? 1 : 0).sum();
        long productosStockBajo = inventarios.stream().mapToLong(i -> i.necesitaReposicion() && !i.agotado() ? 1 : 0).sum();
        long productosCriticos = inventarios.stream().mapToLong(i -> i.stockCritico() ? 1 : 0).sum();
        long stockTotal = inventarios.stream().mapToLong(i -> i.getStock() != null ? i.getStock().longValue() : 0L).sum();
        
        statsSheet.createRow(2).createCell(0).setCellValue("Total de productos:");
        statsSheet.getRow(2).createCell(1).setCellValue(totalProductos);
        
        statsSheet.createRow(3).createCell(0).setCellValue("Productos con stock:");
        statsSheet.getRow(3).createCell(1).setCellValue(productosConStock);
        
        statsSheet.createRow(4).createCell(0).setCellValue("Productos agotados:");
        statsSheet.getRow(4).createCell(1).setCellValue(productosAgotados);
        
        statsSheet.createRow(5).createCell(0).setCellValue("Productos con stock bajo:");
        statsSheet.getRow(5).createCell(1).setCellValue(productosStockBajo);
        
        statsSheet.createRow(6).createCell(0).setCellValue("Productos en estado crítico:");
        statsSheet.getRow(6).createCell(1).setCellValue(productosCriticos);
        
        statsSheet.createRow(7).createCell(0).setCellValue("Stock total acumulado:");
        statsSheet.getRow(7).createCell(1).setCellValue(stockTotal + " unidades");
        
        // Hoja de inventario
        Sheet inventarioSheet = workbook.createSheet("Detalle del Inventario");
        
        // Estilo para cabeceras
        CellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setColor(IndexedColors.WHITE.getIndex());
        headerStyle.setFont(headerFont);
        
        // Crear cabeceras
        Row headerRow = inventarioSheet.createRow(0);
        String[] headers = {"ID Producto", "Nombre", "Descripción", "Categoría", "Precio", "Stock Actual", "Stock Mínimo", "Stock Máximo", "Estado Stock", "Ubicación", "Estado", "Última Actualización", "Usuario Actualización"};
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Llenar datos
        int rowNum = 1;
        for (Inventario inventario : inventarios) {
            Row row = inventarioSheet.createRow(rowNum++);
            Producto producto = inventario.getProducto();
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(producto.getIdProducto().toString());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(producto.getNombre());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(producto.getDescripcion() != null ? producto.getDescripcion() : "");
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(producto.getCategoria());
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue("S/ " + producto.getPrecio().toString());
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(inventario.getStock().toString());
            cell5.setCellStyle(dataStyle);
            
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(inventario.getStockMinimo().toString());
            cell6.setCellStyle(dataStyle);
            
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(inventario.getStockMaximo() != null ? inventario.getStockMaximo().toString() : "N/A");
            cell7.setCellStyle(dataStyle);
            
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(inventario.getEstadoStock());
            cell8.setCellStyle(dataStyle);
            
            Cell cell9 = row.createCell(9);
            cell9.setCellValue(inventario.getUbicacion() != null ? inventario.getUbicacion() : "N/A");
            cell9.setCellStyle(dataStyle);
            
            Cell cell10 = row.createCell(10);
            cell10.setCellValue(Boolean.TRUE.equals(inventario.getEstado()) ? "ACTIVO" : "INACTIVO");
            cell10.setCellStyle(dataStyle);
            
            Cell cell11 = row.createCell(11);
            cell11.setCellValue(inventario.getFechaActualizacion().format(DATE_FORMATTER));
            cell11.setCellStyle(dataStyle);
            
            Cell cell12 = row.createCell(12);
            cell12.setCellValue(inventario.getUsuarioActualizacion() != null ? inventario.getUsuarioActualizacion() : "Sistema");
            cell12.setCellStyle(dataStyle);
        }
        
        // Ajustar ancho de columnas
        for (int i = 0; i < headers.length; i++) {
            inventarioSheet.autoSizeColumn(i);
            if (inventarioSheet.getColumnWidth(i) > 15000) {
                inventarioSheet.setColumnWidth(i, 15000);
            }
        }
        
        // Ajustar columnas de estadísticas
        statsSheet.autoSizeColumn(0);
        statsSheet.autoSizeColumn(1);
        
        workbook.write(baos);
        return baos.toByteArray();
        } // Cierre del try-with-resources
    }
    
    /**
     * Exportar inventario a CSV
     */
    public byte[] exportarInventarioCSV(List<Inventario> inventarios) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             OutputStreamWriter osw = new OutputStreamWriter(baos, "UTF-8");
             CSVWriter writer = new CSVWriter(osw)) {
            
            // Cabeceras
            String[] headers = {"ID Producto", "Nombre", "Descripción", "Categoría", "Precio", "Stock Actual", "Stock Mínimo", "Stock Máximo", "Estado Stock", "Ubicación", "Estado", "Última Actualización", "Usuario Actualización"};
            writer.writeNext(headers);
            
            // Datos
            for (Inventario inventario : inventarios) {
                Producto producto = inventario.getProducto();
                
                String[] data = {
                    producto.getIdProducto().toString(),
                    producto.getNombre(),
                    producto.getDescripcion() != null ? producto.getDescripcion() : "",
                    producto.getCategoria(),
                    "S/ " + producto.getPrecio().toString(),
                    inventario.getStock().toString(),
                    inventario.getStockMinimo().toString(),
                    inventario.getStockMaximo() != null ? inventario.getStockMaximo().toString() : "N/A",
                    inventario.getEstadoStock(),
                    inventario.getUbicacion() != null ? inventario.getUbicacion() : "N/A",
                    Boolean.TRUE.equals(inventario.getEstado()) ? "ACTIVO" : "INACTIVO",
                    inventario.getFechaActualizacion().format(DATE_FORMATTER),
                    inventario.getUsuarioActualizacion() != null ? inventario.getUsuarioActualizacion() : "Sistema"
                };
                writer.writeNext(data);
            }
            
            writer.flush();
            return baos.toByteArray();
        }
    }
}
