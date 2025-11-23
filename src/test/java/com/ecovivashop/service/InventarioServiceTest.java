package com.ecovivashop.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.repository.InventarioRepository;
import com.ecovivashop.repository.ProductoRepository;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void testFindAll() {
        // Given
        List<Inventario> inventarios = Arrays.asList(
            crearInventario(1, 10),
            crearInventario(2, 20)
        );
        when(inventarioRepository.findAll()).thenReturn(inventarios);

        // When
        List<Inventario> result = inventarioService.findAll();

        // Then
        assertEquals(2, result.size());
        verify(inventarioRepository).findAll();
    }

    @Test
    void testFindById_InventarioExiste() {
        // Given
        Inventario inventario = crearInventario(1, 10);
        when(inventarioRepository.findById(1)).thenReturn(Optional.of(inventario));

        // When
        Optional<Inventario> result = inventarioService.findById(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(10, result.get().getStock());
        verify(inventarioRepository).findById(1);
    }

    @Test
    void testFindById_InventarioNoExiste() {
        // Given
        when(inventarioRepository.findById(999)).thenReturn(Optional.empty());

        // When
        Optional<Inventario> result = inventarioService.findById(999);

        // Then
        assertFalse(result.isPresent());
        verify(inventarioRepository).findById(999);
    }

    @Test
    void testFindByProductoId_InventarioExiste() {
        // Given
        Inventario inventario = crearInventario(1, 10);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));

        // When
        Optional<Inventario> result = inventarioService.findByProductoId(1);

        // Then
        assertTrue(result.isPresent());
        assertEquals(10, result.get().getStock());
        verify(inventarioRepository).findByProductoId(1);
    }

    @Test
    void testSave() {
        // Given
        Inventario inventario = crearInventario(1, 10);
        when(inventarioRepository.save(inventario)).thenReturn(inventario);

        // When
        Inventario result = inventarioService.save(inventario);

        // Then
        assertEquals(10, result.getStock());
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void testActualizarStock_InventarioExiste() {
        // Given
        Inventario inventario = crearInventario(1, 10);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // When
        inventarioService.actualizarStock(1, 20, "usuario");

        // Then
        assertEquals(20, inventario.getStock());
        assertEquals("usuario", inventario.getUsuarioActualizacion());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void testActualizarStock_InventarioNoExiste() {
        // Given
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            inventarioService.actualizarStock(1, 20, "usuario"));
        assertEquals("Inventario no encontrado para el producto ID: 1", exception.getMessage());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testReducirStock_InventarioExiste_StockSuficiente() {
        // Given
        Inventario inventario = crearInventario(1, 10);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // When
        inventarioService.reducirStock(1, 5, "usuario");

        // Then
        assertEquals(5, inventario.getStock());
        assertEquals("usuario", inventario.getUsuarioActualizacion());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void testReducirStock_InventarioNoExiste() {
        // Given
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            inventarioService.reducirStock(1, 5, "usuario"));
        assertEquals("Inventario no encontrado para el producto ID: 1", exception.getMessage());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testReducirStock_StockInsuficiente() {
        // Given
        Inventario inventario = crearInventario(1, 3);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            inventarioService.reducirStock(1, 5, "usuario"));
        assertEquals("Stock insuficiente. Disponible: 3, Solicitado: 5", exception.getMessage());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testAumentarStock_InventarioExiste() {
        // Given
        Inventario inventario = crearInventario(1, 10);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // When
        inventarioService.aumentarStock(1, 5, "usuario");

        // Then
        assertEquals(15, inventario.getStock());
        assertEquals("usuario", inventario.getUsuarioActualizacion());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void testAumentarStock_InventarioNoExiste() {
        // Given
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            inventarioService.aumentarStock(1, 5, "usuario"));
        assertEquals("Inventario no encontrado para el producto ID: 1", exception.getMessage());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testAjustarStock_Aumento() {
        // Given
        Inventario inventario = crearInventario(1, 10);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // When
        inventarioService.ajustarStock(1, 5, "motivo", "usuario");

        // Then
        assertEquals(15, inventario.getStock());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void testAjustarStock_Reduccion() {
        // Given
        Inventario inventario = crearInventario(1, 10);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventario);

        // When
        inventarioService.ajustarStock(1, -3, "motivo", "usuario");

        // Then
        assertEquals(7, inventario.getStock());
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository).save(inventario);
    }

    @Test
    void testAjustarStock_Cero() {
        // When
        inventarioService.ajustarStock(1, 0, "motivo", "usuario");

        // Then
        verify(inventarioRepository, never()).findByProductoId(anyInt());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testCrearInventario_ProductoExiste_InventarioNoExiste() {
        // Given
        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Producto Test");

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(Inventario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Inventario result = inventarioService.crearInventario(1, 10, 5, 100, "Ubicacion A", "usuario");

        // Then
        assertEquals(producto, result.getProducto());
        assertEquals(10, result.getStock());
        assertEquals(5, result.getStockMinimo());
        assertEquals(100, result.getStockMaximo());
        assertEquals("Ubicacion A", result.getUbicacion());
        assertTrue(result.getEstado());
        assertEquals("usuario", result.getUsuarioActualizacion());
        verify(productoRepository).findById(1);
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository).save(any(Inventario.class));
    }

    @Test
    void testCrearInventario_ProductoNoExiste() {
        // Given
        when(productoRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            inventarioService.crearInventario(1, 10, 5, 100, "Ubicacion A", "usuario"));
        assertEquals("Producto no encontrado con ID: 1", exception.getMessage());
        verify(productoRepository).findById(1);
        verify(inventarioRepository, never()).findByProductoId(anyInt());
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testCrearInventario_InventarioYaExiste() {
        // Given
        Producto producto = new Producto();
        producto.setIdProducto(1);
        Inventario inventarioExistente = crearInventario(1, 5);

        when(productoRepository.findById(1)).thenReturn(Optional.of(producto));
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventarioExistente));

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            inventarioService.crearInventario(1, 10, 5, 100, "Ubicacion A", "usuario"));
        assertEquals("Ya existe inventario para el producto ID: 1", exception.getMessage());
        verify(productoRepository).findById(1);
        verify(inventarioRepository).findByProductoId(1);
        verify(inventarioRepository, never()).save(any(Inventario.class));
    }

    @Test
    void testVerificarDisponibilidad_Disponible() {
        // Given
        when(inventarioRepository.verificarDisponibilidad(1, 5)).thenReturn(true);

        // When
        boolean result = inventarioService.verificarDisponibilidad(1, 5);

        // Then
        assertTrue(result);
        verify(inventarioRepository).verificarDisponibilidad(1, 5);
    }

    @Test
    void testVerificarDisponibilidad_NoDisponible() {
        // Given
        when(inventarioRepository.verificarDisponibilidad(1, 5)).thenReturn(false);

        // When
        boolean result = inventarioService.verificarDisponibilidad(1, 5);

        // Then
        assertFalse(result);
        verify(inventarioRepository).verificarDisponibilidad(1, 5);
    }

    @Test
    void testObtenerStockDisponible_InventarioExiste() {
        // Given
        Inventario inventario = crearInventario(1, 15);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));

        // When
        Integer result = inventarioService.obtenerStockDisponible(1);

        // Then
        assertEquals(15, result);
        verify(inventarioRepository).findByProductoId(1);
    }

    @Test
    void testObtenerStockDisponible_InventarioNoExiste() {
        // Given
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.empty());

        // When
        Integer result = inventarioService.obtenerStockDisponible(1);

        // Then
        assertEquals(0, result);
        verify(inventarioRepository).findByProductoId(1);
    }

    @Test
    void testEstaAgotado_InventarioExiste_Agotado() {
        // Given
        Inventario inventario = crearInventario(1, 0);
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.of(inventario));

        // When
        boolean result = inventarioService.estaAgotado(1);

        // Then
        assertTrue(result);
        verify(inventarioRepository).findByProductoId(1);
    }

    @Test
    void testEstaAgotado_InventarioNoExiste() {
        // Given
        when(inventarioRepository.findByProductoId(1)).thenReturn(Optional.empty());

        // When
        boolean result = inventarioService.estaAgotado(1);

        // Then
        assertTrue(result);
        verify(inventarioRepository).findByProductoId(1);
    }

    @Test
    void testContarConStock() {
        // Given
        when(inventarioRepository.contarConStock()).thenReturn(25L);

        // When
        Long result = inventarioService.contarConStock();

        // Then
        assertEquals(25L, result);
        verify(inventarioRepository).contarConStock();
    }

    @Test
    void testObtenerUbicaciones() {
        // Given
        List<String> ubicaciones = Arrays.asList("Almacen A", "Almacen B");
        when(inventarioRepository.findUbicaciones()).thenReturn(ubicaciones);

        // When
        List<String> result = inventarioService.obtenerUbicaciones();

        // Then
        assertEquals(2, result.size());
        assertEquals("Almacen A", result.get(0));
        verify(inventarioRepository).findUbicaciones();
    }

    // Método helper para crear inventarios de prueba
    private Inventario crearInventario(Integer idProducto, Integer stock) {
        Inventario inventario = new Inventario();
        Producto producto = new Producto();
        producto.setIdProducto(idProducto);
        producto.setNombre("Producto " + idProducto);
        inventario.setProducto(producto);
        inventario.setStock(stock);
        inventario.setStockMinimo(5);
        inventario.setStockMaximo(100);
        inventario.setEstado(true);
        inventario.setFechaActualizacion(LocalDateTime.now());
        return inventario;
    }
}