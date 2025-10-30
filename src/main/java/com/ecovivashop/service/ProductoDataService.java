package com.ecovivashop.service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.ImagenProducto;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.repository.ImagenProductoRepository;
import com.ecovivashop.repository.ProductoRepository;

@Service
public class ProductoDataService {
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private ImagenProductoRepository imagenProductoRepository;    
    /**
     * Crea productos de ejemplo con imágenes reales
     */
    @Transactional
    public void crearProductosDeEjemplo() {
        crearProductosConImagenes();
    }
    
    private void crearProductosConImagenes() {
        // Productos de Electrónicos
        crearProducto("Smartphone Samsung Galaxy S23", 
                     "Smartphone de alta gama con cámara de 50MP y pantalla AMOLED",
                     new BigDecimal("899.99"), "Electrónicos", "Samsung", "Galaxy S23",
                     "https://images.unsplash.com/photo-1592750475338-74b7b21085ab?w=400&h=400&fit=crop");
        
        crearProducto("Laptop Dell XPS 13", 
                     "Laptop ultradelgada con procesador Intel i7 y 16GB RAM",
                     new BigDecimal("1299.99"), "Electrónicos", "Dell", "XPS 13",
                     "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&h=400&fit=crop");
        
        crearProducto("Auriculares Sony WH-1000XM4", 
                     "Auriculares inalámbricos con cancelación de ruido activa",
                     new BigDecimal("349.99"), "Electrónicos", "Sony", "WH-1000XM4",
                     "https://images.unsplash.com/photo-1505740420928-5e560c06d30e?w=400&h=400&fit=crop");
        
        crearProducto("Tablet iPad Air", 
                     "Tablet de 10.9 pulgadas con chip M1 y soporte para Apple Pencil",
                     new BigDecimal("599.99"), "Electrónicos", "Apple", "iPad Air",
                     "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&h=400&fit=crop");
        
        crearProducto("Smartwatch Apple Watch Series 8", 
                     "Reloj inteligente con GPS y monitoreo de salud avanzado",
                     new BigDecimal("399.99"), "Electrónicos", "Apple", "Watch Series 8",
                     "https://images.unsplash.com/photo-1434493789847-2f02dc6ca35d?w=400&h=400&fit=crop");
        
        // Productos de Ropa y Moda
        crearProducto("Camiseta Básica Algodón", 
                     "Camiseta de algodón 100% en varios colores disponibles",
                     new BigDecimal("19.99"), "Ropa y Moda", "BasicWear", "Cotton Tee",
                     "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400&h=400&fit=crop");
        
        crearProducto("Jeans Slim Fit", 
                     "Jeans de mezclilla con corte slim fit y lavado stone",
                     new BigDecimal("79.99"), "Ropa y Moda", "Denim Co", "Slim Fit",
                     "https://images.unsplash.com/photo-1541099649105-f69ad21f3246?w=400&h=400&fit=crop");
        
        crearProducto("Zapatillas Running Nike", 
                     "Zapatillas deportivas con tecnología Air Max para running",
                     new BigDecimal("129.99"), "Ropa y Moda", "Nike", "Air Max",
                     "https://images.unsplash.com/photo-1549298916-b41d501d3772?w=400&h=400&fit=crop");
        
        crearProducto("Chaqueta Impermeable", 
                     "Chaqueta resistente al agua ideal para actividades outdoor",
                     new BigDecimal("89.99"), "Ropa y Moda", "OutdoorPro", "Waterproof",
                     "https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=400&h=400&fit=crop");
        
        crearProducto("Reloj Analógico Clásico", 
                     "Reloj de pulsera con correa de cuero y diseño clásico",
                     new BigDecimal("199.99"), "Ropa y Moda", "TimeClassic", "Leather Watch",
                     "https://images.unsplash.com/photo-1524592094714-0f0654e20314?w=400&h=400&fit=crop");
        
        // Productos de Hogar y Jardín
        crearProducto("Lámpara de Pie LED", 
                     "Lámpara de pie con luz LED regulable y diseño minimalista",
                     new BigDecimal("129.99"), "Hogar y Jardín", "LightDesign", "LED Floor",
                     "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=400&h=400&fit=crop");
        
        crearProducto("Aspiradora Robot", 
                     "Aspiradora robótica con navegación inteligente y control WiFi",
                     new BigDecimal("299.99"), "Hogar y Jardín", "CleanBot", "Smart Vacuum",
                     "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop");
        
        crearProducto("Set de Cuchillos Japoneses", 
                     "Set de cuchillos de acero japonés con bloque de madera",
                     new BigDecimal("159.99"), "Hogar y Jardín", "BladeArt", "Japanese Set",
                     "https://images.unsplash.com/photo-1581126564616-2a3b5f9a5c1f?w=400&h=400&fit=crop");
        
        crearProducto("Cafetera Espresso", 
                     "Cafetera para espresso con sistema de presión de 15 bares",
                     new BigDecimal("249.99"), "Hogar y Jardín", "BrewMaster", "Espresso Pro",
                     "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?w=400&h=400&fit=crop");
        
        crearProducto("Plantas Suculentas Set", 
                     "Set de 6 plantas suculentas con macetas decorativas",
                     new BigDecimal("39.99"), "Hogar y Jardín", "GreenLife", "Succulent Set",
                     "https://images.unsplash.com/photo-1416879595882-3373a0480b5b?w=400&h=400&fit=crop");
        
        // Productos de Deportes
        crearProducto("Bicicleta Montaña", 
                     "Bicicleta de montaña con 21 velocidades y frenos de disco",
                     new BigDecimal("599.99"), "Deportes", "MountainRider", "Trail Pro",
                     "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=400&h=400&fit=crop");
        
        crearProducto("Pelota Fútbol Profesional", 
                     "Pelota de fútbol oficial con certificación FIFA",
                     new BigDecimal("29.99"), "Deportes", "SportsPro", "FIFA Ball",
                     "https://images.unsplash.com/photo-1574629810360-7efbbe195018?w=400&h=400&fit=crop");
        
        crearProducto("Pesas Ajustables", 
                     "Set de pesas ajustables de 2kg a 20kg por mancuerna",
                     new BigDecimal("199.99"), "Deportes", "FitnessPro", "Adjustable Weights",
                     "https://images.unsplash.com/photo-1571019613540-996a0a0c4c3a?w=400&h=400&fit=crop");
        
        crearProducto("Esterilla Yoga Premium", 
                     "Esterilla de yoga antideslizante con grosor extra",
                     new BigDecimal("49.99"), "Deportes", "YogaFlow", "Premium Mat",
                     "https://images.unsplash.com/photo-1592432678016-e910b452f906?w=400&h=400&fit=crop");
        
        crearProducto("Casco Ciclismo", 
                     "Casco para ciclismo con ventilación y ajuste cómodo",
                     new BigDecimal("79.99"), "Deportes", "SafeRide", "Aero Helmet",
                     "https://images.unsplash.com/photo-1544966503-7cc5ac882d5f?w=400&h=400&fit=crop");
    }
    
    private void crearProducto(String nombre, String descripcion, BigDecimal precio, 
                             String categoria, String marca, String modelo, String urlImagen) {
        try {
            // Crear producto
            Producto producto = new Producto();
            producto.setNombre(nombre);
            producto.setDescripcion(descripcion);
            producto.setPrecio(precio);
            producto.setCategoria(categoria);
            producto.setMarca(marca);
            producto.setModelo(modelo);
            // No hay campo stock en la entidad Producto
            producto.setEstado(true); // Activo
            
            // Guardar producto
            producto = productoRepository.save(producto);
            
            // Descargar y guardar imagen
            descargarYGuardarImagen(producto, urlImagen);
            
        } catch (RuntimeException e) {
            System.err.println("Error al crear producto " + nombre + ": " + e.getMessage());
        }
    }
    
    private void descargarYGuardarImagen(Producto producto, String urlImagen) {
        try {
            // Crear directorio si no existe
            Path directorio = Paths.get("uploads", "products", producto.getIdProducto().toString());
            Files.createDirectories(directorio);
            
            // Generar nombre de archivo
            String extension = ".jpg";
            String nombreArchivo = "producto_" + producto.getIdProducto() + "_principal" + extension;
            
            // Descargar imagen
            URL url = new URL(urlImagen);
            Path rutaArchivo = directorio.resolve(nombreArchivo);
            
            try (InputStream in = url.openStream()) {
                Files.copy(in, rutaArchivo, StandardCopyOption.REPLACE_EXISTING);
            }
            
            // Crear registro en base de datos
            ImagenProducto imagenProducto = new ImagenProducto();
            imagenProducto.setProductoId(producto.getIdProducto().longValue());
            imagenProducto.setNombreArchivo(nombreArchivo);
            imagenProducto.setNombreOriginal(nombreArchivo);
            imagenProducto.setRutaArchivo(rutaArchivo.toString());
            imagenProducto.setTamaño(Files.size(rutaArchivo));
            imagenProducto.setTipoMime("image/jpeg");
            imagenProducto.setOrden(0);
            imagenProducto.setEsPrincipal(true);
            imagenProducto.setAltText(producto.getNombre());
            
            imagenProductoRepository.save(imagenProducto);
            
        } catch (IOException e) {
            System.err.println("Error al descargar imagen para producto " + producto.getNombre() + ": " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Error del sistema al guardar imagen para producto " + producto.getNombre() + ": " + e.getMessage());
        }
    }
    
    /**
     * Verifica si hay productos en la base de datos
     */
    public boolean hayProductos() {
        return productoRepository.count() > 0;
    }
    
    /**
     * Elimina todos los productos de ejemplo
     */
    @Transactional
    public void eliminarProductosDeEjemplo() {
        imagenProductoRepository.deleteAll();
        productoRepository.deleteAll();
    }
}
