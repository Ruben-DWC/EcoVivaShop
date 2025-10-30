package com.ecovivashop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.repository.InventarioRepository;
import com.ecovivashop.repository.ProductoRepository;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Service
@Transactional
public class ProductoService {
    private static final Logger logger = LoggerFactory.getLogger(ProductoService.class);
    
    private final ProductoRepository productoRepository;
    private final InventarioRepository inventarioRepository;
    
    // Cache usando Google Guava para productos activos
    private final Cache<String, List<Producto>> productosActivosCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    // Cache para almacenar productos por ID
    private final Cache<Integer, Producto> productoCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .maximumSize(100)
            .build();

    // Constructor manual
    public ProductoService(ProductoRepository productoRepository, InventarioRepository inventarioRepository) {
        this.productoRepository = productoRepository;
        this.inventarioRepository = inventarioRepository;
    }

    // Métodos CRUD básicos
    public List<Producto> findAll() {
        return this.productoRepository.findAll();
    }
    
    public Optional<Producto> findById(Integer id) {
        // Intentar obtener el producto del caché
        Producto productoCacheado = productoCache.getIfPresent(id);
        if (productoCacheado != null) {
            return Optional.of(productoCacheado);
        }
        
        // Si no está en caché, buscar en la base de datos
        Optional<Producto> productoBD = this.productoRepository.findByIdWithInventario(id);
        
        // Si se encuentra en la base de datos, almacenar en caché antes de devolver
        productoBD.ifPresent(producto -> productoCache.put(id, producto));
        
        return productoBD;
    }
    
    public Producto save(Producto producto) {
        return this.productoRepository.save(producto);
    }
    
    public void deleteById(Integer id) {
        this.productoRepository.deleteById(id);
    }
    
    // Métodos de negocio
    /**
     * Obtiene la lista de productos activos utilizando caché de Google Guava.
     * Esta implementación demuestra el uso práctico de Google Guava para mejorar
     * el rendimiento mediante el almacenamiento en caché de consultas frecuentes.
     *
     * El método implementa un patrón de caché con las siguientes características:
     * - Cache hit: Retorna productos desde memoria sin consultar BD
     * - Cache miss: Consulta BD y almacena resultado en caché por 10 minutos
     * - Máximo 100 entradas en caché para evitar consumo excesivo de memoria
     *
     * @return Lista de productos activos (estado = true)
     */
    public List<Producto> obtenerProductosActivos() {
        // Intentar obtener del caché usando Google Guava
        List<Producto> productosCacheados = productosActivosCache.getIfPresent("activos");
        if (productosCacheados != null) {
            logger.info("Cache hit: Productos activos obtenidos del caché (Google Guava) - {} productos disponibles", productosCacheados.size());
            return productosCacheados;
        }

        // Si no está en caché, consultar la base de datos
        logger.info("Cache miss: Consultando productos activos desde la base de datos");
        List<Producto> productosBD = this.productoRepository.findByEstadoTrue();

        // Almacenar en caché antes de devolver
        productosActivosCache.put("activos", productosBD);
        logger.info("Productos activos almacenados en caché (Google Guava) - {} productos, expiración en 10 minutos", productosBD.size());
        return productosBD;
    }
    
    public List<Producto> obtenerPorCategoria(String categoria) {
        return this.productoRepository.findByCategoriaAndEstadoTrue(categoria);
    }
    
    public Page<Producto> obtenerPorCategoriaPaginado(String categoria, Pageable pageable) {
        return this.productoRepository.findByCategoriaAndEstadoTrue(categoria, pageable);
    }
    
    public List<Producto> obtenerPorMarca(String marca) {
        return this.productoRepository.findByMarca(marca);
    }
    
    public List<Producto> obtenerPorRangoPrecios(BigDecimal precioMin, BigDecimal precioMax) {
        return this.productoRepository.findByPrecioBetween(precioMin, precioMax);
    }
    
    public Page<Producto> obtenerPorRangoPreciosPaginado(BigDecimal precioMin, BigDecimal precioMax, Pageable pageable) {
        return this.productoRepository.findByPrecioBetweenAndEstadoTrue(precioMin, precioMax, pageable);
    }
    
    public List<Producto> buscarProductos(String busqueda) {
        return this.productoRepository.buscarProductos(busqueda);
    }
    
    public Page<Producto> buscarProductosPaginado(String busqueda, Pageable pageable) {
        return this.productoRepository.buscarProductosPaginado(busqueda, pageable);
    }
    
    public List<Producto> obtenerProductosEcoAmigables() {
        return this.productoRepository.findProductosEcoAmigables(BigDecimal.valueOf(7.0));
    }
    
    public List<Producto> obtenerProductosConStock() {
        return this.productoRepository.findProductosConStock();
    }
    
    public List<Producto> obtenerProductosAgotados() {
        return this.productoRepository.findProductosAgotados();
    }
    
    public List<Producto> obtenerProductosConStockBajo() {
        return this.productoRepository.findProductosConStockBajo();
    }
    
    public List<Producto> obtenerProductosMasVendidos(int cantidad) {
        return this.productoRepository.findProductosMasVendidos(Pageable.ofSize(cantidad));
    }
    
    public List<Producto> obtenerProductosRecomendados(BigDecimal precioMaximo) {
        return this.productoRepository.findProductosRecomendados(precioMaximo);
    }
    
    // Métodos de creación y actualización
    public Producto crearProducto(String nombre, String descripcion, BigDecimal precio, String categoria,
                                 String marca, String modelo, String color, BigDecimal peso, 
                                 String dimensiones, String material, Integer garantiaMeses,
                                 String eficienciaEnergetica, String impactoAmbiental, 
                                 BigDecimal puntuacionEco, String imagenUrl, Integer stockInicial) {
        
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setCategoria(categoria);
        producto.setMarca(marca);
        producto.setModelo(modelo);
        producto.setColor(color);
        producto.setPeso(peso);
        producto.setDimensiones(dimensiones);
        producto.setMaterial(material);
        producto.setGarantiaMeses(garantiaMeses);
        producto.setEficienciaEnergetica(eficienciaEnergetica);
        producto.setImpactoAmbiental(impactoAmbiental);
        producto.setPuntuacionEco(puntuacionEco);
        producto.setImagenUrl(imagenUrl);
        producto.setEstado(true);
        producto.setFechaCreacion(LocalDateTime.now());
        
        Producto productoGuardado = this.productoRepository.save(producto);
        
        // Crear inventario inicial
        if (stockInicial != null && stockInicial > 0) {
            this.crearInventarioInicial(productoGuardado, stockInicial);
        }
        
        return productoGuardado;
    }
    
    private void crearInventarioInicial(Producto producto, Integer stockInicial) {
        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setStock(stockInicial);
        inventario.setStockMinimo(5); // Valor por defecto
        inventario.setEstado(true);
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventario.setUsuarioActualizacion("SISTEMA");
        
        this.inventarioRepository.save(inventario);
    }
    
    public Producto actualizarProducto(Integer id, String nombre, String descripcion, BigDecimal precio,
                                      String categoria, String marca, String modelo, String color,
                                      BigDecimal peso, String dimensiones, String material,
                                      Integer garantiaMeses, String eficienciaEnergetica,
                                      String impactoAmbiental, BigDecimal puntuacionEco, String imagenUrl) {
        
        Optional<Producto> productoExistente = this.productoRepository.findById(id);
        if (productoExistente.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        
        Producto producto = productoExistente.get();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);
        producto.setPrecio(precio);
        producto.setCategoria(categoria);
        producto.setMarca(marca);
        producto.setModelo(modelo);
        producto.setColor(color);
        producto.setPeso(peso);
        producto.setDimensiones(dimensiones);
        producto.setMaterial(material);
        producto.setGarantiaMeses(garantiaMeses);
        producto.setEficienciaEnergetica(eficienciaEnergetica);
        producto.setImpactoAmbiental(impactoAmbiental);
        producto.setPuntuacionEco(puntuacionEco);
        producto.setImagenUrl(imagenUrl);
        producto.setFechaActualizacion(LocalDateTime.now());
        
        return this.productoRepository.save(producto);
    }
    
    public void desactivarProducto(Integer id) {
        Optional<Producto> productoExistente = this.productoRepository.findById(id);
        if (productoExistente.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        
        Producto producto = productoExistente.get();
        producto.setEstado(false);
        producto.setFechaActualizacion(LocalDateTime.now());
        this.productoRepository.save(producto);
    }
    
    public void activarProducto(Integer id) {
        Optional<Producto> productoExistente = this.productoRepository.findById(id);
        if (productoExistente.isEmpty()) {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        
        Producto producto = productoExistente.get();
        producto.setEstado(true);
        producto.setFechaActualizacion(LocalDateTime.now());
        this.productoRepository.save(producto);
    }
    
    // Métodos de consulta
    public List<String> obtenerCategorias() {
        return this.productoRepository.findCategorias();
    }
    
    public List<String> obtenerMarcas() {
        return this.productoRepository.findMarcas();
    }
    
    // Estadísticas
    public Long contarProductosActivos() {
        return this.productoRepository.contarProductosActivos();
    }
    
    public Long contarPorCategoria(String categoria) {
        return this.productoRepository.contarPorCategoria(categoria);
    }
    
    public BigDecimal obtenerPrecioPromedio() {
        return this.productoRepository.obtenerPrecioPromedio();
    }
    
    public BigDecimal obtenerPuntuacionEcoPromedio() {
        return this.productoRepository.obtenerPuntuacionEcoPromedio();
    }
    
    // Validaciones
    public boolean tieneStock(Integer idProducto) {
        Optional<Producto> producto = this.productoRepository.findById(idProducto);
        return producto.isPresent() && producto.get().tieneStock();
    }
    
    public Integer obtenerStockDisponible(Integer idProducto) {
        Optional<Producto> producto = this.productoRepository.findById(idProducto);
        return producto.map(Producto::getStockDisponible).orElse(0);
    }
    
    public boolean puedeEliminarProducto(Integer id) {
        Optional<Producto> producto = this.productoRepository.findById(id);
        if (producto.isEmpty()) {
            return false;
        }
        
        // No se puede eliminar si tiene pedidos asociados
        return producto.get().getPedidoDetalles() == null || producto.get().getPedidoDetalles().isEmpty();
    }
    
    // Métodos adicionales para paginación y categorías
    public Page<Producto> obtenerProductosPaginados(Pageable pageable) {
        return this.productoRepository.findByEstadoTrueWithInventario(pageable);
    }
    
    public List<String> obtenerCategoriasDisponibles() {
        return this.productoRepository.findCategorias();
    }
    
    public boolean validarSKUUnico(String sku, Integer idProducto) {
        // Como no tenemos campo SKU, validamos por nombre
        List<Producto> productos = this.productoRepository.findByNombre(sku);
        
        if (productos.isEmpty()) {
            return true;
        }
        
        // Si existe un producto con el mismo nombre, verificar si es el mismo que estamos editando
        if (idProducto != null) {
            return productos.stream().allMatch(p -> p.getIdProducto().equals(idProducto));
        }
        
        return false;
    }
    
    public void crearInventarioInicial(Producto producto) {
        if (producto.getInventario() == null) {
            Inventario inventario = new Inventario();
            inventario.setProducto(producto);
            inventario.setStock(0);
            inventario.setStockMinimo(5);
            inventario.setStockMaximo(100);
            inventario.setFechaActualizacion(LocalDateTime.now());
            
            this.inventarioRepository.save(inventario);
        }
    }
    
    // ===== MÉTODOS PARA REPORTES Y ESTADÍSTICAS =====
    
    /**
     * Contar total de productos
     */
    public long contarProductos() {
        return productoRepository.count();
    }
    
    /**
     * Contar productos por categoría
     */
    public List<Object[]> contarProductosPorCategoria() {
        return productoRepository.contarProductosPorCategoria();
    }
    
    // ===== MÉTODOS PARA EL CLIENTE =====
    
    /**
     * Buscar productos con filtros múltiples para el catálogo del cliente
     */
    public Page<Producto> buscarProductosConFiltros(String busqueda, String categoria, 
                                                   String minPrecio, String maxPrecio, 
                                                   Pageable pageable) {
        
        BigDecimal precioMin = null;
        BigDecimal precioMax = null;
        
        try {
            if (minPrecio != null && !minPrecio.trim().isEmpty()) {
                precioMin = new BigDecimal(minPrecio);
            }
            if (maxPrecio != null && !maxPrecio.trim().isEmpty()) {
                precioMax = new BigDecimal(maxPrecio);
            }
        } catch (NumberFormatException e) {
            // Ignorar errores de formato y usar valores null
        }
        
        // Si no hay filtros específicos, devolver todos los productos activos CON INVENTARIO OPTIMIZADO
        if ((busqueda == null || busqueda.trim().isEmpty()) && 
            (categoria == null || categoria.trim().isEmpty()) &&
            precioMin == null && precioMax == null) {
            return productoRepository.findByEstadoTrueWithInventario(pageable);
        }
        
        // Aplicar filtros específicos
        if (busqueda != null && !busqueda.trim().isEmpty()) {
            if (categoria != null && !categoria.trim().isEmpty()) {
                // Busqueda + categoria
                if (precioMin != null && precioMax != null) {
                    return productoRepository.findByNombreContainingIgnoreCaseAndCategoriaAndPrecioBetweenAndEstadoTrue(
                        busqueda, categoria, precioMin, precioMax, pageable);
                } else {
                    return productoRepository.findByNombreContainingIgnoreCaseAndCategoriaAndEstadoTrue(
                        busqueda, categoria, pageable);
                }
            } else {
                // Solo busqueda
                if (precioMin != null && precioMax != null) {
                    return productoRepository.findByNombreContainingIgnoreCaseAndPrecioBetweenAndEstadoTrue(
                        busqueda, precioMin, precioMax, pageable);
                } else {
                    return productoRepository.buscarProductosPaginadoWithInventario(busqueda, pageable);
                }
            }
        } else if (categoria != null && !categoria.trim().isEmpty()) {
            // Solo categoria
            if (precioMin != null && precioMax != null) {
                return productoRepository.findByCategoriaAndPrecioBetweenAndEstadoTrue(
                    categoria, precioMin, precioMax, pageable);
            } else {
                return productoRepository.findByCategoriaAndEstadoTrueWithInventario(categoria, pageable);
            }
        } else if (precioMin != null && precioMax != null) {
            // Solo rango de precios - OPTIMIZADO
            return productoRepository.findByPrecioBetweenAndEstadoTrueWithInventario(precioMin, precioMax, pageable);
        }
        
        // Fallback - OPTIMIZADO
        return productoRepository.findByEstadoTrueWithInventario(pageable);
    }
    
    /**
     * Obtener productos por categoría (para productos relacionados)
     */
    public List<Producto> findByCategoria(String categoria) {
        return productoRepository.findByCategoriaAndEstadoTrue(categoria);
    }
}
