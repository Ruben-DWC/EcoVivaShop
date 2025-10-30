package com.ecovivashop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
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

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.PedidoDetalle;
import com.ecovivashop.entity.Producto;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.PedidoDetalleRepository;
import com.ecovivashop.repository.PedidoRepository;
import com.ecovivashop.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private PedidoRepository pedidoRepository;

    @Mock
    private PedidoDetalleRepository pedidoDetalleRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private InventarioService inventarioService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PedidoService pedidoService;

    // ===== MÉTODOS HELPER PARA CREAR ENTIDADES DE PRUEBA =====

    private Usuario createTestUsuario() {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setEmail("test@example.com");
        usuario.setNombre("Test User");
        return usuario;
    }

    private Producto createTestProducto() {
        Producto producto = new Producto();
        producto.setIdProducto(1);
        producto.setNombre("Producto Test");
        producto.setPrecio(new BigDecimal("100.00"));
        producto.setCategoria("Electrónica");
        producto.setEstado(true);
        return producto;
    }

    private Pedido createTestPedido() {
        Usuario usuario = createTestUsuario();
        Pedido pedido = new Pedido();
        pedido.setIdPedido(1);
        pedido.setUsuario(usuario);
        pedido.setNumeroPedido("PED-001");
        pedido.setFechaPedido(LocalDateTime.now());
        pedido.setEstado("PENDIENTE");
        pedido.setSubtotal(new BigDecimal("100.00"));
        pedido.setDescuento(new BigDecimal("0.00"));
        pedido.setCostoEnvio(new BigDecimal("10.00"));
        pedido.setImpuestos(new BigDecimal("18.00"));
        pedido.setTotal(new BigDecimal("128.00"));
        pedido.setDireccionEnvio("Dirección de prueba");
        pedido.setTelefonoContacto("123456789");
        pedido.setMetodoPago("TARJETA");
        return pedido;
    }

    private PedidoDetalle createTestPedidoDetalle(Pedido pedido, Producto producto) {
        PedidoDetalle pedidoDetalle = new PedidoDetalle();
        pedidoDetalle.setIdDetalle(1);
        pedidoDetalle.setPedido(pedido);
        pedidoDetalle.setProducto(producto);
        pedidoDetalle.setCantidad(1);
        pedidoDetalle.setPrecioUnitario(new BigDecimal("100.00"));
        pedidoDetalle.setDescuentoUnitario(new BigDecimal("0.00"));
        pedidoDetalle.setSubtotal(new BigDecimal("100.00"));
        return pedidoDetalle;
    }

    private List<Pedido> createTestPedidos() {
        return Arrays.asList(createTestPedido());
    }

    private List<PedidoDetalle> createTestPedidoDetalles() {
        Pedido pedido = createTestPedido();
        Producto producto = createTestProducto();
        return Arrays.asList(createTestPedidoDetalle(pedido, producto));
    }

    // ===== TESTS CRUD BÁSICOS =====

    @Test
    void testFindAll() {
        List<Pedido> pedidos = createTestPedidos();
        when(pedidoRepository.findAll()).thenReturn(pedidos);

        List<Pedido> result = pedidoService.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(pedidos.get(0).getIdPedido(), result.get(0).getIdPedido());
        verify(pedidoRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        Pedido pedido = createTestPedido();
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

        Optional<Pedido> result = pedidoService.findById(1);

        assertTrue(result.isPresent());
        assertEquals(pedido.getIdPedido(), result.get().getIdPedido());
        verify(pedidoRepository, times(1)).findById(1);
    }

    @Test
    void testFindById_NotFound() {
        when(pedidoRepository.findById(999)).thenReturn(Optional.empty());

        Optional<Pedido> result = pedidoService.findById(999);

        assertFalse(result.isPresent());
        verify(pedidoRepository, times(1)).findById(999);
    }

    @Test
    void testFindByNumeroPedido_Success() {
        Pedido pedido = createTestPedido();
        when(pedidoRepository.findByNumeroPedido("PED-001")).thenReturn(Optional.of(pedido));

        Optional<Pedido> result = pedidoService.findByNumeroPedido("PED-001");

        assertTrue(result.isPresent());
        assertEquals("PED-001", result.get().getNumeroPedido());
        verify(pedidoRepository, times(1)).findByNumeroPedido("PED-001");
    }

    @Test
    void testFindByNumeroPedidoWithDetalles_Success() {
        Pedido pedido = createTestPedido();
        when(pedidoRepository.findByNumeroPedidoWithDetalles("PED-001")).thenReturn(Optional.of(pedido));

        Optional<Pedido> result = pedidoService.findByNumeroPedidoWithDetalles("PED-001");

        assertTrue(result.isPresent());
        assertEquals("PED-001", result.get().getNumeroPedido());
        verify(pedidoRepository, times(1)).findByNumeroPedidoWithDetalles("PED-001");
    }

    @Test
    void testSave() {
        Pedido pedido = createTestPedido();
        when(pedidoRepository.save(pedido)).thenReturn(pedido);

        Pedido result = pedidoService.save(pedido);

        assertNotNull(result);
        assertEquals(pedido.getIdPedido(), result.getIdPedido());
        verify(pedidoRepository, times(1)).save(pedido);
    }

    // ===== TESTS DE NEGOCIO =====

    @Test
    void testObtenerPedidosPorUsuario_Success() {
        Usuario usuario = createTestUsuario();
        List<Pedido> pedidos = createTestPedidos();
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findByUsuario(usuario)).thenReturn(pedidos);

        List<Pedido> result = pedidoService.obtenerPedidosPorUsuario(1);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(usuarioRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).findByUsuario(usuario);
    }

    @Test
    void testObtenerPedidosPorUsuario_UsuarioNotFound() {
        when(usuarioRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            pedidoService.obtenerPedidosPorUsuario(999));

        assertEquals("Usuario no encontrado con ID: 999", exception.getMessage());
        verify(usuarioRepository, times(1)).findById(999);
        verify(pedidoRepository, never()).findByUsuario(any());
    }

    @Test
    void testObtenerPedidosPorUsuarioPaginado_Success() {
        Usuario usuario = createTestUsuario();
        List<Pedido> pedidos = createTestPedidos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> page = new PageImpl<>(pedidos, pageable, 1);

        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuario));
        when(pedidoRepository.findByUsuario(usuario, pageable)).thenReturn(page);

        Page<Pedido> result = pedidoService.obtenerPedidosPorUsuarioPaginado(1, pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(usuarioRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).findByUsuario(usuario, pageable);
    }

    @Test
    void testObtenerPorEstado() {
        List<Pedido> pedidos = createTestPedidos();
        when(pedidoRepository.findByEstado("PENDIENTE")).thenReturn(pedidos);

        List<Pedido> result = pedidoService.obtenerPorEstado("PENDIENTE");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).findByEstado("PENDIENTE");
    }

    @Test
    void testObtenerPorEstadoPaginado() {
        List<Pedido> pedidos = createTestPedidos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> page = new PageImpl<>(pedidos, pageable, 1);

        when(pedidoRepository.findByEstado("PENDIENTE", pageable)).thenReturn(page);

        Page<Pedido> result = pedidoService.obtenerPorEstadoPaginado("PENDIENTE", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(pedidoRepository, times(1)).findByEstado("PENDIENTE", pageable);
    }

    @Test
    void testObtenerPedidosRecientes() {
        List<Pedido> pedidos = createTestPedidos();
        when(pedidoRepository.findPedidosRecientes(any(Pageable.class))).thenReturn(pedidos);

        List<Pedido> result = pedidoService.obtenerPedidosRecientes(5);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).findPedidosRecientes(any(Pageable.class));
    }

    @Test
    void testObtenerPedidosPendientes() {
        List<Pedido> pedidos = createTestPedidos();
        when(pedidoRepository.findByEstadoOrderByFechaPedidoDesc("PENDIENTE")).thenReturn(pedidos);

        List<Pedido> result = pedidoService.obtenerPedidosPendientes();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).findByEstadoOrderByFechaPedidoDesc("PENDIENTE");
    }

    @Test
    void testBuscarPedidos() {
        List<Pedido> pedidos = createTestPedidos();
        when(pedidoRepository.buscarPedidos("test")).thenReturn(pedidos);

        List<Pedido> result = pedidoService.buscarPedidos("test");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).buscarPedidos("test");
    }

    // ===== TESTS DE GESTIÓN DE ESTADOS =====

    @Test
    void testConfirmarPedido_Success() {
        Pedido pedido = createTestPedido();
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Configurar email service para no hacer nada
        doNothing().when(emailService).enviarCorreoConfirmacionPedido(anyString(), anyString(), anyString(), anyString());

        assertDoesNotThrow(() -> pedidoService.confirmarPedido(1));

        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
        verify(emailService, times(1)).enviarCorreoConfirmacionPedido(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void testConfirmarPedido_NotFound() {
        when(pedidoRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            pedidoService.confirmarPedido(999));

        assertEquals("Pedido no encontrado con ID: 999", exception.getMessage());
        verify(pedidoRepository, times(1)).findById(999);
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    void testEnviarPedido_Success() {
        Pedido pedido = createTestPedido();
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        assertDoesNotThrow(() -> pedidoService.enviarPedido(1, "TRACK123", "DHL"));

        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testEntregarPedido_Success() {
        Pedido pedido = createTestPedido();
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        assertDoesNotThrow(() -> pedidoService.entregarPedido(1));

        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testCancelarPedido_Success() {
        Pedido pedido = createTestPedido();
        List<PedidoDetalle> pedidoDetalles = createTestPedidoDetalles();
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));
        when(pedidoDetalleRepository.findByPedido(pedido)).thenReturn(pedidoDetalles);
        when(pedidoRepository.save(any(Pedido.class))).thenReturn(pedido);

        // Configurar inventario service
        doNothing().when(inventarioService).aumentarStock(anyInt(), anyInt(), anyString());

        assertDoesNotThrow(() -> pedidoService.cancelarPedido(1));

        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoDetalleRepository, times(1)).findByPedido(pedido);
        verify(inventarioService, times(1)).aumentarStock(anyInt(), anyInt(), anyString());
        verify(pedidoRepository, times(1)).save(any(Pedido.class));
    }

    @Test
    void testCancelarPedido_NotFound() {
        when(pedidoRepository.findById(999)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
            pedidoService.cancelarPedido(999));

        assertEquals("Pedido no encontrado con ID: 999", exception.getMessage());
        verify(pedidoRepository, times(1)).findById(999);
    }

    // ===== TESTS ESTADÍSTICOS =====

    @Test
    void testContarPorEstado() {
        when(pedidoRepository.contarPorEstado("PENDIENTE")).thenReturn(5L);

        Long result = pedidoService.contarPorEstado("PENDIENTE");

        assertEquals(5L, result);
        verify(pedidoRepository, times(1)).contarPorEstado("PENDIENTE");
    }

    @Test
    void testCalcularVentasEnPeriodo() {
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now();
        BigDecimal expected = new BigDecimal("1000.00");

        when(pedidoRepository.calcularVentasEnPeriodo(inicio, fin)).thenReturn(expected);

        BigDecimal result = pedidoService.calcularVentasEnPeriodo(inicio, fin);

        assertEquals(expected, result);
        verify(pedidoRepository, times(1)).calcularVentasEnPeriodo(inicio, fin);
    }

    @Test
    void testObtenerTicketPromedio() {
        BigDecimal expected = new BigDecimal("150.00");

        when(pedidoRepository.obtenerTicketPromedio()).thenReturn(expected);

        BigDecimal result = pedidoService.obtenerTicketPromedio();

        assertEquals(expected, result);
        verify(pedidoRepository, times(1)).obtenerTicketPromedio();
    }

    @Test
    void testCalcularIngresosTotales() {
        BigDecimal expected = new BigDecimal("5000.00");

        when(pedidoRepository.calcularIngresosTotales()).thenReturn(expected);

        BigDecimal result = pedidoService.calcularIngresosTotales();

        assertEquals(expected, result);
        verify(pedidoRepository, times(1)).calcularIngresosTotales();
    }

    @Test
    void testObtenerEstados() {
        List<String> expected = Arrays.asList("PENDIENTE", "CONFIRMADO", "ENVIADO", "ENTREGADO");

        when(pedidoRepository.findEstados()).thenReturn(expected);

        List<String> result = pedidoService.obtenerEstados();

        assertEquals(expected, result);
        verify(pedidoRepository, times(1)).findEstados();
    }

    @Test
    void testObtenerMetodosPago() {
        List<String> expected = Arrays.asList("TARJETA", "EFECTIVO", "TRANSFERENCIA");

        when(pedidoRepository.findMetodosPago()).thenReturn(expected);

        List<String> result = pedidoService.obtenerMetodosPago();

        assertEquals(expected, result);
        verify(pedidoRepository, times(1)).findMetodosPago();
    }

    // ===== TESTS ADMINISTRACIÓN =====

    @Test
    void testObtenerPedidosPaginados() {
        List<Pedido> pedidos = createTestPedidos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> page = new PageImpl<>(pedidos, pageable, 1);

        when(pedidoRepository.findAll(pageable)).thenReturn(page);

        Page<Pedido> result = pedidoService.obtenerPedidosPaginados(pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(pedidoRepository, times(1)).findAll(pageable);
    }

    @Test
    void testBuscarPedidosPaginado() {
        List<Pedido> pedidos = createTestPedidos();
        Pageable pageable = PageRequest.of(0, 10);
        Page<Pedido> page = new PageImpl<>(pedidos, pageable, 1);

        when(pedidoRepository.buscarPedidos("test", pageable)).thenReturn(page);

        Page<Pedido> result = pedidoService.buscarPedidosPaginado("test", pageable);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(pedidoRepository, times(1)).buscarPedidos("test", pageable);
    }

    @Test
    void testContarPedidos() {
        when(pedidoRepository.count()).thenReturn(100L);

        long result = pedidoService.contarPedidos();

        assertEquals(100L, result);
        verify(pedidoRepository, times(1)).count();
    }

    @Test
    void testContarPedidosPorEstado() {
        when(pedidoRepository.countByEstado("PENDIENTE")).thenReturn(25L);

        long result = pedidoService.contarPedidosPorEstado("PENDIENTE");

        assertEquals(25L, result);
        verify(pedidoRepository, times(1)).countByEstado("PENDIENTE");
    }

    @Test
    void testObtenerPedidosEntreFechas() {
        List<Pedido> pedidos = createTestPedidos();
        LocalDateTime inicio = LocalDateTime.now().minusDays(30);
        LocalDateTime fin = LocalDateTime.now();

        when(pedidoRepository.findByFechaPedidoBetween(inicio, fin)).thenReturn(pedidos);

        List<Pedido> result = pedidoService.obtenerPedidosEntreFechas(inicio, fin);

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).findByFechaPedidoBetween(inicio, fin);
    }

    @Test
    void testObtenerPedidosPorEstado() {
        List<Pedido> pedidos = createTestPedidos();
        when(pedidoRepository.findByEstado("PENDIENTE")).thenReturn(pedidos);

        List<Pedido> result = pedidoService.obtenerPedidosPorEstado("PENDIENTE");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(pedidoRepository, times(1)).findByEstado("PENDIENTE");
    }

    @Test
    void testEliminarPedido_Success() {
        Pedido pedido = createTestPedido();
        pedido.setEstado("CANCELADO");
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

        assertDoesNotThrow(() -> pedidoService.eliminarPedido(1));

        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, times(1)).delete(pedido);
    }

    @Test
    void testEliminarPedido_NotFound() {
        when(pedidoRepository.findById(999)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
            pedidoService.eliminarPedido(999));

        assertEquals("Pedido no encontrado con ID: 999", exception.getMessage());
        verify(pedidoRepository, times(1)).findById(999);
        verify(pedidoRepository, never()).delete(any());
    }

    @Test
    void testEliminarPedido_EstadoInvalido() {
        Pedido pedido = createTestPedido();
        pedido.setEstado("PENDIENTE");
        when(pedidoRepository.findById(1)).thenReturn(Optional.of(pedido));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
            pedidoService.eliminarPedido(1));

        assertEquals("Solo se pueden eliminar pedidos cancelados", exception.getMessage());
        verify(pedidoRepository, times(1)).findById(1);
        verify(pedidoRepository, never()).delete(any());
    }
}