package com.ecovivashop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.PedidoDetalle;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.repository.InventarioRepository;
import com.ecovivashop.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class ProductoServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @Mock
    private InventarioRepository inventarioRepository;

    @InjectMocks
    private ProductoService productoService;

    private Producto createTestProducto() {
        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Producto Test");
        producto.setDescripcion("Descripción del producto test");
        producto.setPrecio(BigDecimal.valueOf(100.00));
        producto.setCategoria("Electrónicos");
        producto.setMarca("Marca Test");
        producto.setModelo("Modelo Test");
        producto.setColor("Negro");
        producto.setPeso(BigDecimal.valueOf(1.5));
        producto.setDimensiones("10x20x5");
        producto.setMaterial("Plástico");
        producto.setGarantiaMeses(12);
        producto.setEficienciaEnergetica("A+");
        producto.setImpactoAmbiental("Bajo");
        producto.setPuntuacionEco(BigDecimal.valueOf(8.5));
        producto.setImagenUrl("http://example.com/image.jpg");
        producto.setEstado(true);
        producto.setFechaCreacion(LocalDateTime.now());
        // Asignar inventario al producto
        Inventario inventario = createTestInventario(producto);
        producto.setInventario(inventario);
        return producto;
    }

    private Inventario createTestInventario(Producto producto) {
        Inventario inventario = new Inventario();
        inventario.setIdInventario(1);
        inventario.setProducto(producto);
        inventario.setStock(10);
        inventario.setStockMinimo(5);
        inventario.setStockMaximo(100);
        inventario.setUbicacion("Almacén A");
        inventario.setEstado(true);
        inventario.setFechaActualizacion(LocalDateTime.now());
        inventario.setUsuarioActualizacion("admin");
        return inventario;
    }

    private List<Producto> createTestProductos() {
        Producto producto1 = createTestProducto();
        Producto producto2 = new Producto();
        producto2.setIdProducto(2);
        producto2.setNombre("Producto Test 2");
        producto2.setDescripcion("Descripción del producto test 2");
        producto2.setPrecio(BigDecimal.valueOf(200.00));
        producto2.setCategoria("Electrónicos");
        producto2.setMarca("Marca Test 2");
        producto2.setModelo("Modelo Test 2");
        producto2.setColor("Blanco");
        producto2.setPeso(BigDecimal.valueOf(2.0));
        producto2.setDimensiones("15x25x8");
        producto2.setMaterial("Metal");
        producto2.setGarantiaMeses(24);
        producto2.setEficienciaEnergetica("A++");
        producto2.setImpactoAmbiental("Muy Bajo");
        producto2.setPuntuacionEco(BigDecimal.valueOf(9.0));
        producto2.setImagenUrl("http://example.com/image2.jpg");
        producto2.setEstado(true);
        producto2.setFechaCreacion(LocalDateTime.now());
        // Asignar inventario al segundo producto
        Inventario inventario2 = new Inventario();
        inventario2.setIdInventario(2);
        inventario2.setProducto(producto2);
        inventario2.setStock(20);
        inventario2.setStockMinimo(10);
        inventario2.setStockMaximo(200);
        inventario2.setUbicacion("Almacén B");
        inventario2.setEstado(true);
        inventario2.setFechaActualizacion(LocalDateTime.now());
        inventario2.setUsuarioActualizacion("admin");
        producto2.setInventario(inventario2);

        return Arrays.asList(producto1, producto2);
    }

    @Test
    void testFindAll() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findAll()).thenReturn(productos);

        List<Producto> result = productoService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Producto Test", result.get(0).getNombre());
        verify(productoRepository, times(1)).findAll();
    }

    @Test
    void testFindById_CacheMiss() {
        Producto producto = createTestProducto();
        Inventario inventario = createTestInventario(producto);
        producto.setInventario(inventario);
        when(productoRepository.findByIdWithInventario(1)).thenReturn(Optional.of(producto));

        Optional<Producto> result = productoService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Producto Test", result.get().getNombre());
        verify(productoRepository, times(1)).findByIdWithInventario(1);
    }

    @Test
    void testFindById_CacheHit() {
        // Primero cargar en caché
        Producto producto = createTestProducto();
        Inventario inventario = createTestInventario(producto);
        producto.setInventario(inventario);
        when(productoRepository.findByIdWithInventario(1)).thenReturn(Optional.of(producto));
        productoService.findById(1);

        // Segundo llamado debería usar caché
        Optional<Producto> result = productoService.findById(1);

        assertTrue(result.isPresent());
        assertEquals("Producto Test", result.get().getNombre());
        // Solo una llamada al repository (la primera)
        verify(productoRepository, times(1)).findByIdWithInventario(1);
    }

    @Test
    void testFindById_NotFound() {
        when(productoRepository.findByIdWithInventario(999)).thenReturn(Optional.empty());

        Optional<Producto> result = productoService.findById(999);

        assertFalse(result.isPresent());
        verify(productoRepository, times(1)).findByIdWithInventario(999);
    }

    @Test
    void testSave() {
        Producto producto = createTestProducto();
        when(productoRepository.save(producto)).thenReturn(producto);

        Producto result = productoService.save(producto);

        assertNotNull(result);
        assertEquals("Producto Test", result.getNombre());
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testDeleteById() {
        doNothing().when(productoRepository).deleteById(1);

        assertDoesNotThrow(() -> productoService.deleteById(1));

        verify(productoRepository, times(1)).deleteById(1);
    }

    @Test
    void testObtenerProductosActivos_CacheMiss() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findByEstadoTrue()).thenReturn(productos);

        List<Producto> result = productoService.obtenerProductosActivos();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Producto Test", result.get(0).getNombre());
        verify(productoRepository, times(1)).findByEstadoTrue();
    }

    @Test
    void testObtenerProductosActivos_CacheHit() {
        // Primero cargar en caché
        List<Producto> productos = createTestProductos();
        when(productoRepository.findByEstadoTrue()).thenReturn(productos);
        productoService.obtenerProductosActivos();

        // Segundo llamado debería usar caché
        List<Producto> result = productoService.obtenerProductosActivos();

        assertNotNull(result);
        assertEquals(2, result.size());
        // Solo una llamada al repository (la primera)
        verify(productoRepository, times(1)).findByEstadoTrue();
    }

    @Test
    void testObtenerPorCategoria() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findByCategoriaAndEstadoTrue("Electrónicos")).thenReturn(productos);

        List<Producto> result = productoService.obtenerPorCategoria("Electrónicos");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electrónicos", result.get(0).getCategoria());
        verify(productoRepository, times(1)).findByCategoriaAndEstadoTrue("Electrónicos");
    }

    @Test
    void testObtenerPorCategoriaPaginado() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, 1);
        when(productoRepository.findByCategoriaAndEstadoTrue("Electrónicos", pageable)).thenReturn(page);

        Page<Producto> result = productoService.obtenerPorCategoriaPaginado("Electrónicos", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).findByCategoriaAndEstadoTrue("Electrónicos", pageable);
    }

    @Test
    void testObtenerPorMarca() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findByMarca("Marca Test")).thenReturn(productos);

        List<Producto> result = productoService.obtenerPorMarca("Marca Test");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Marca Test", result.get(0).getMarca());
        verify(productoRepository, times(1)).findByMarca("Marca Test");
    }

    @Test
    void testObtenerPorRangoPrecios() {
        List<Producto> productos = createTestProductos();
        BigDecimal min = BigDecimal.valueOf(50.00);
        BigDecimal max = BigDecimal.valueOf(150.00);
        when(productoRepository.findByPrecioBetween(min, max)).thenReturn(productos);

        List<Producto> result = productoService.obtenerPorRangoPrecios(min, max);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).findByPrecioBetween(min, max);
    }

    @Test
    void testObtenerPorRangoPreciosPaginado() {
        List<Producto> productos = createTestProductos();
        BigDecimal min = BigDecimal.valueOf(50.00);
        BigDecimal max = BigDecimal.valueOf(150.00);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, 1);
        when(productoRepository.findByPrecioBetweenAndEstadoTrue(min, max, pageable)).thenReturn(page);

        Page<Producto> result = productoService.obtenerPorRangoPreciosPaginado(min, max, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).findByPrecioBetweenAndEstadoTrue(min, max, pageable);
    }

    @Test
    void testBuscarProductos() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.buscarProductos("test")).thenReturn(productos);

        List<Producto> result = productoService.buscarProductos("test");

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).buscarProductos("test");
    }

    @Test
    void testBuscarProductosPaginado() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, 1);
        when(productoRepository.buscarProductosPaginado("test", pageable)).thenReturn(page);

        Page<Producto> result = productoService.buscarProductosPaginado("test", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).buscarProductosPaginado("test", pageable);
    }

    @Test
    void testObtenerProductosEcoAmigables() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findProductosEcoAmigables(BigDecimal.valueOf(7.0))).thenReturn(productos);

        List<Producto> result = productoService.obtenerProductosEcoAmigables();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).findProductosEcoAmigables(BigDecimal.valueOf(7.0));
    }

    @Test
    void testObtenerProductosConStock() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findProductosConStock()).thenReturn(productos);

        List<Producto> result = productoService.obtenerProductosConStock();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).findProductosConStock();
    }

    @Test
    void testObtenerProductosAgotados() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findProductosAgotados()).thenReturn(productos);

        List<Producto> result = productoService.obtenerProductosAgotados();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).findProductosAgotados();
    }

    @Test
    void testObtenerProductosConStockBajo() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findProductosConStockBajo()).thenReturn(productos);

        List<Producto> result = productoService.obtenerProductosConStockBajo();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).findProductosConStockBajo();
    }

    @Test
    void testObtenerProductosMasVendidos() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 5);
        when(productoRepository.findProductosMasVendidos(pageable)).thenReturn(productos);

        List<Producto> result = productoService.obtenerProductosMasVendidos(5);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).findProductosMasVendidos(pageable);
    }

    @Test
    void testObtenerProductosRecomendados() {
        List<Producto> productos = createTestProductos();
        BigDecimal precioMax = BigDecimal.valueOf(200.00);
        when(productoRepository.findProductosRecomendados(precioMax)).thenReturn(productos);

        List<Producto> result = productoService.obtenerProductosRecomendados(precioMax);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).findProductosRecomendados(precioMax);
    }

    @Test
    void testCrearProducto() {
        Producto producto = createTestProducto();
        Inventario inventario = createTestInventario(producto);
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        Producto result = productoService.crearProducto(
            "Producto Test", "Descripción", BigDecimal.valueOf(100.00), "Electrónicos",
            "Marca Test", "Modelo Test", "Negro", BigDecimal.valueOf(1.5),
            "10x20x5", "Plástico", 12, "A+", "Bajo", BigDecimal.valueOf(8.5),
            "http://example.com/image.jpg", 10
        );

        assertNotNull(result);
        assertEquals("Producto Test", result.getNombre());
        assertEquals("Electrónicos", result.getCategoria());
        assertTrue(result.getEstado());
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
    }

    @Test
    void testCrearProducto_SinStockInicial() {
        Producto producto = createTestProducto();
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto result = productoService.crearProducto(
            "Producto Test", "Descripción", BigDecimal.valueOf(100.00), "Electrónicos",
            "Marca Test", "Modelo Test", "Negro", BigDecimal.valueOf(1.5),
            "10x20x5", "Plástico", 12, "A+", "Bajo", BigDecimal.valueOf(8.5),
            "http://example.com/image.jpg", null
        );

        assertNotNull(result);
        assertEquals("Producto Test", result.getNombre());
        verify(productoRepository, times(1)).save(any(Producto.class));
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testActualizarProducto() {
        Producto producto = createTestProducto();
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        Producto result = productoService.actualizarProducto(
            1, "Producto Actualizado", "Descripción actualizada", BigDecimal.valueOf(150.00),
            "Electrónicos", "Marca Test", "Modelo Test", "Blanco", BigDecimal.valueOf(2.0),
            "15x25x8", "Metal", 24, "A++", "Muy Bajo", BigDecimal.valueOf(9.0),
            "http://example.com/new-image.jpg"
        );

        assertNotNull(result);
        assertEquals("Producto Actualizado", result.getNombre());
        assertEquals(BigDecimal.valueOf(150.00), result.getPrecio());
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testActualizarProducto_NotFound() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            productoService.actualizarProducto(999, "Nombre", "Desc", BigDecimal.ONE,
                "Cat", "Marca", "Modelo", "Color", BigDecimal.ONE, "Dim", "Mat",
                1, "A", "Bajo", BigDecimal.ONE, "url")
        );

        assertEquals("Producto no encontrado con ID: 999", exception.getMessage());
        verify(productoRepository, times(1)).findById(999);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testDesactivarProducto() {
        Producto producto = createTestProducto();
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        assertDoesNotThrow(() -> productoService.desactivarProducto(1));

        assertFalse(producto.getEstado());
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testDesactivarProducto_NotFound() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            productoService.desactivarProducto(999)
        );

        assertEquals("Producto no encontrado con ID: 999", exception.getMessage());
        verify(productoRepository, times(1)).findById(999);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testActivarProducto() {
        Producto producto = createTestProducto();
        producto.setEstado(false);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(productoRepository.save(any(Producto.class))).thenReturn(producto);

        assertDoesNotThrow(() -> productoService.activarProducto(1));

        assertTrue(producto.getEstado());
        verify(productoRepository, times(1)).findById(1);
        verify(productoRepository, times(1)).save(producto);
    }

    @Test
    void testActivarProducto_NotFound() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            productoService.activarProducto(999)
        );

        assertEquals("Producto no encontrado con ID: 999", exception.getMessage());
        verify(productoRepository, times(1)).findById(999);
        verify(productoRepository, never()).save(any(Producto.class));
    }

    @Test
    void testObtenerCategorias() {
        List<String> categorias = Arrays.asList("Electrónicos", "Ropa", "Hogar");
        when(productoRepository.findCategorias()).thenReturn(categorias);

        List<String> result = productoService.obtenerCategorias();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Electrónicos"));
        verify(productoRepository, times(1)).findCategorias();
    }

    @Test
    void testObtenerMarcas() {
        List<String> marcas = Arrays.asList("Marca A", "Marca B", "Marca C");
        when(productoRepository.findMarcas()).thenReturn(marcas);

        List<String> result = productoService.obtenerMarcas();

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("Marca A"));
        verify(productoRepository, times(1)).findMarcas();
    }

    @Test
    void testContarProductosActivos() {
        when(productoRepository.contarProductosActivos()).thenReturn(25L);

        Long result = productoService.contarProductosActivos();

        assertEquals(25L, result);
        verify(productoRepository, times(1)).contarProductosActivos();
    }

    @Test
    void testContarPorCategoria() {
        when(productoRepository.contarPorCategoria("Electrónicos")).thenReturn(10L);

        Long result = productoService.contarPorCategoria("Electrónicos");

        assertEquals(10L, result);
        verify(productoRepository, times(1)).contarPorCategoria("Electrónicos");
    }

    @Test
    void testObtenerPrecioPromedio() {
        BigDecimal precioPromedio = BigDecimal.valueOf(125.50);
        when(productoRepository.obtenerPrecioPromedio()).thenReturn(precioPromedio);

        BigDecimal result = productoService.obtenerPrecioPromedio();

        assertEquals(precioPromedio, result);
        verify(productoRepository, times(1)).obtenerPrecioPromedio();
    }

    @Test
    void testObtenerPuntuacionEcoPromedio() {
        BigDecimal puntuacionPromedio = BigDecimal.valueOf(7.8);
        when(productoRepository.obtenerPuntuacionEcoPromedio()).thenReturn(puntuacionPromedio);

        BigDecimal result = productoService.obtenerPuntuacionEcoPromedio();

        assertEquals(puntuacionPromedio, result);
        verify(productoRepository, times(1)).obtenerPuntuacionEcoPromedio();
    }

    @Test
    void testTieneStock_True() {
        Producto producto = createTestProducto();
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        boolean result = productoService.tieneStock(1);

        assertTrue(result);
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void testTieneStock_False() {
        Producto producto = createTestProducto();
        Inventario inventario = createTestInventario(producto);
        inventario.setStock(0);
        producto.setInventario(inventario);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        boolean result = productoService.tieneStock(1);

        assertFalse(result);
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void testTieneStock_NotFound() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = productoService.tieneStock(999);

        assertFalse(result);
        verify(productoRepository, times(1)).findById(999);
    }

    @Test
    void testObtenerStockDisponible() {
        Producto producto = createTestProducto();
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        Integer result = productoService.obtenerStockDisponible(1);

        assertEquals(10, result);
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void testObtenerStockDisponible_NotFound() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        Integer result = productoService.obtenerStockDisponible(999);

        assertEquals(0, result);
        verify(productoRepository, times(1)).findById(999);
    }

    @Test
    void testPuedeEliminarProducto_True() {
        Producto producto = createTestProducto();
        producto.setPedidoDetalles(null);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        boolean result = productoService.puedeEliminarProducto(1);

        assertTrue(result);
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void testPuedeEliminarProducto_False() {
        // Simular que tiene pedidos asociados
        Producto producto = createTestProducto();
        Set<PedidoDetalle> pedidoDetalles = new HashSet<>();
        PedidoDetalle detalle = new PedidoDetalle();
        pedidoDetalles.add(detalle);
        producto.setPedidoDetalles(pedidoDetalles);
        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));

        boolean result = productoService.puedeEliminarProducto(1);

        assertFalse(result);
        verify(productoRepository, times(1)).findById(1);
    }

    @Test
    void testPuedeEliminarProducto_NotFound() {
        when(productoRepository.findById(999)).thenReturn(Optional.empty());

        boolean result = productoService.puedeEliminarProducto(999);

        assertFalse(result);
        verify(productoRepository, times(1)).findById(999);
    }

    @Test
    void testObtenerProductosPaginados() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, productos.size());
        when(productoRepository.findByEstadoTrueWithInventario(pageable)).thenReturn(page);

        Page<Producto> result = productoService.obtenerProductosPaginados(pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).findByEstadoTrueWithInventario(pageable);
    }

    @Test
    void testObtenerCategoriasDisponibles() {
        List<String> categorias = Arrays.asList("Electrónicos", "Ropa");
        when(productoRepository.findCategorias()).thenReturn(categorias);

        List<String> result = productoService.obtenerCategoriasDisponibles();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).findCategorias();
    }

    @Test
    void testValidarSKUUnico_True() {
        when(productoRepository.findByNombre("Producto Único")).thenReturn(new ArrayList<>());

        boolean result = productoService.validarSKUUnico("Producto Único", null);

        assertTrue(result);
        verify(productoRepository, times(1)).findByNombre("Producto Único");
    }

    @Test
    void testValidarSKUUnico_False() {
        Producto productoExistente = createTestProducto();
        List<Producto> productosExistentes = Arrays.asList(productoExistente);
        when(productoRepository.findByNombre("Producto Test")).thenReturn(productosExistentes);

        boolean result = productoService.validarSKUUnico("Producto Test", null);

        assertFalse(result);
        verify(productoRepository, times(1)).findByNombre("Producto Test");
    }

    @Test
    void testValidarSKUUnico_EditingExisting_True() {
        Producto productoExistente = createTestProducto();
        List<Producto> productosExistentes = Arrays.asList(productoExistente);
        when(productoRepository.findByNombre("Producto Test")).thenReturn(productosExistentes);

        boolean result = productoService.validarSKUUnico("Producto Test", 1);

        assertTrue(result);
        verify(productoRepository, times(1)).findByNombre("Producto Test");
    }

    @Test
    void testContarProductos() {
        when(productoRepository.count()).thenReturn(50L);

        long result = productoService.contarProductos();

        assertEquals(50L, result);
        verify(productoRepository, times(1)).count();
    }

    @Test
    void testContarProductosPorCategoria() {
        List<Object[]> estadisticas = Arrays.asList(
            new Object[]{"Electrónicos", 15L},
            new Object[]{"Ropa", 10L}
        );
        when(productoRepository.contarProductosPorCategoria()).thenReturn(estadisticas);

        List<Object[]> result = productoService.contarProductosPorCategoria();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productoRepository, times(1)).contarProductosPorCategoria();
    }

    @Test
    void testBuscarProductosConFiltros_SinFiltros() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, productos.size());
        when(productoRepository.findByEstadoTrueWithInventario(pageable)).thenReturn(page);

        Page<Producto> result = productoService.buscarProductosConFiltros(null, null, null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).findByEstadoTrueWithInventario(pageable);
    }

    @Test
    void testBuscarProductosConFiltros_SoloBusqueda() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, productos.size());
        when(productoRepository.buscarProductosPaginadoWithInventario("test", pageable)).thenReturn(page);

        Page<Producto> result = productoService.buscarProductosConFiltros("test", null, null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).buscarProductosPaginadoWithInventario("test", pageable);
    }

    @Test
    void testBuscarProductosConFiltros_BusquedaYCategoria() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, productos.size());
        when(productoRepository.findByNombreContainingIgnoreCaseAndCategoriaAndEstadoTrue(
            "test", "Electrónicos", pageable)).thenReturn(page);

        Page<Producto> result = productoService.buscarProductosConFiltros("test", "Electrónicos", null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).findByNombreContainingIgnoreCaseAndCategoriaAndEstadoTrue(
            "test", "Electrónicos", pageable);
    }

    @Test
    void testBuscarProductosConFiltros_SoloCategoria() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, productos.size());
        when(productoRepository.findByCategoriaAndEstadoTrueWithInventario("Electrónicos", pageable)).thenReturn(page);

        Page<Producto> result = productoService.buscarProductosConFiltros(null, "Electrónicos", null, null, pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).findByCategoriaAndEstadoTrueWithInventario("Electrónicos", pageable);
    }

    @Test
    void testBuscarProductosConFiltros_SoloRangoPrecios() {
        List<Producto> productos = createTestProductos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Producto> page = new PageImpl<>(productos, pageable, productos.size());
        when(productoRepository.findByPrecioBetweenAndEstadoTrueWithInventario(
            new BigDecimal("50.00"), new BigDecimal("150.00"), pageable)).thenReturn(page);

        Page<Producto> result = productoService.buscarProductosConFiltros(null, null, "50.00", "150.00", pageable);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(productoRepository, times(1)).findByPrecioBetweenAndEstadoTrueWithInventario(
            new BigDecimal("50.00"), new BigDecimal("150.00"), pageable);
    }

    @Test
    void testFindByCategoria() {
        List<Producto> productos = createTestProductos();
        when(productoRepository.findByCategoriaAndEstadoTrue("Electrónicos")).thenReturn(productos);

        List<Producto> result = productoService.findByCategoria("Electrónicos");

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Electrónicos", result.get(0).getCategoria());
        verify(productoRepository, times(1)).findByCategoriaAndEstadoTrue("Electrónicos");
    }
}