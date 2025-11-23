package com.ecovivashop.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.Usuario;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {
    
    // Buscar por número de pedido
    Optional<Pedido> findByNumeroPedido(String numeroPedido);
    
    // Buscar por número de pedido con detalles cargados
    @EntityGraph(attributePaths = {"detalles", "detalles.producto"})
    @Query("SELECT p FROM Pedido p WHERE p.numeroPedido = :numeroPedido")
    Optional<Pedido> findByNumeroPedidoWithDetalles(@Param("numeroPedido") String numeroPedido);
    
    // Buscar pedidos por usuario
    List<Pedido> findByUsuario(Usuario usuario);
    
    // Buscar pedidos por usuario con paginación
    Page<Pedido> findByUsuario(Usuario usuario, Pageable pageable);
    
    // Buscar por estado
    List<Pedido> findByEstado(String estado);
    
    // Buscar pedidos por estado con paginación
    Page<Pedido> findByEstado(String estado, Pageable pageable);
    
    // Pedidos en un rango de fechas
    @Query("SELECT p FROM Pedido p WHERE p.fechaPedido BETWEEN :inicio AND :fin")
    List<Pedido> findByFechaPedidoBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Pedidos pendientes
    List<Pedido> findByEstadoOrderByFechaPedidoDesc(String estado);
    
    // Pedidos recientes
    @Query("SELECT p FROM Pedido p ORDER BY p.fechaPedido DESC")
    List<Pedido> findPedidosRecientes(Pageable pageable);
    
    // Pedidos por usuario ordenados por fecha
    @Query("SELECT p FROM Pedido p WHERE p.usuario = :usuario ORDER BY p.fechaPedido DESC")
    List<Pedido> findByUsuarioOrderByFechaPedidoDesc(@Param("usuario") Usuario usuario);
    
    // Buscar por rango de total
    @Query("SELECT p FROM Pedido p WHERE p.total BETWEEN :totalMin AND :totalMax")
    List<Pedido> findByTotalBetween(@Param("totalMin") BigDecimal totalMin, @Param("totalMax") BigDecimal totalMax);
    
    // Pedidos entregados en un período
    @Query("SELECT p FROM Pedido p WHERE p.estado = 'ENTREGADO' AND p.fechaEntrega BETWEEN :inicio AND :fin")
    List<Pedido> findEntregadosEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Pedidos por método de pago
    List<Pedido> findByMetodoPago(String metodoPago);
    
    // Buscar por transportadora
    List<Pedido> findByTransportadora(String transportadora);
    
    // Buscar por número de seguimiento
    Optional<Pedido> findByNumeroSeguimiento(String numeroSeguimiento);
    
    // Estadísticas de pedidos
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.estado = :estado")
    Long contarPorEstado(@Param("estado") String estado);
    
    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.fechaPedido >= :fecha")
    Long contarPedidosDespueDe(@Param("fecha") LocalDateTime fecha);
    
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado = 'ENTREGADO' AND p.fechaEntrega BETWEEN :inicio AND :fin")
    BigDecimal calcularVentasEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT AVG(p.total) FROM Pedido p WHERE p.estado = 'ENTREGADO'")
    BigDecimal obtenerTicketPromedio();
    
    // Total de ingresos de todos los pedidos entregados
    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.estado = 'ENTREGADO'")
    BigDecimal calcularIngresosTotales();
    
    // Estados de pedidos únicos
    @Query("SELECT DISTINCT p.estado FROM Pedido p ORDER BY p.estado")
    List<String> findEstados();
    
    // Métodos de pago únicos
    @Query("SELECT DISTINCT p.metodoPago FROM Pedido p ORDER BY p.metodoPago")
    List<String> findMetodosPago();
    
    // Pedidos que necesitan atención (pendientes por más de X días)
    @Query("SELECT p FROM Pedido p WHERE p.estado = 'PENDIENTE' AND p.fechaPedido < :fechaLimite")
    List<Pedido> findPedidosQueNecesitanAtencion(@Param("fechaLimite") LocalDateTime fechaLimite);
    
    // Top clientes por volumen de pedidos
    @Query("SELECT p.usuario, COUNT(p) FROM Pedido p GROUP BY p.usuario ORDER BY COUNT(p) DESC")
    List<Object[]> findTopClientesPorVolumen(Pageable pageable);
    
    // Top clientes por valor de compras
    @Query("SELECT p.usuario, SUM(p.total) FROM Pedido p WHERE p.estado = 'ENTREGADO' " +
           "GROUP BY p.usuario ORDER BY SUM(p.total) DESC")
    List<Object[]> findTopClientesPorValor(Pageable pageable);
    
    // Búsqueda general de pedidos
    @Query("SELECT p FROM Pedido p WHERE " +
           "p.numeroPedido LIKE %:busqueda% OR " +
           "LOWER(p.usuario.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.usuario.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "p.numeroSeguimiento LIKE %:busqueda%")
    List<Pedido> buscarPedidos(@Param("busqueda") String busqueda);
    
    // Pedidos con retraso en entrega
    @Query("SELECT p FROM Pedido p WHERE p.estado IN ('CONFIRMADO', 'EN_PREPARACION', 'ENVIADO') " +
           "AND p.fechaEstimadaEntrega < :ahora")
    List<Pedido> findPedidosConRetraso(@Param("ahora") LocalDateTime ahora);
    
    // Pedidos completados hoy
    @Query("SELECT p FROM Pedido p WHERE p.estado = 'ENTREGADO' " +
           "AND DATE(p.fechaEntrega) = DATE(:fecha)")
    List<Pedido> findCompletadosEnFecha(@Param("fecha") LocalDateTime fecha);
    
    // Ventas por mes
    @Query("SELECT EXTRACT(YEAR FROM p.fechaPedido), EXTRACT(MONTH FROM p.fechaPedido), SUM(p.total) " +
           "FROM Pedido p WHERE p.estado = 'ENTREGADO' " +
           "GROUP BY EXTRACT(YEAR FROM p.fechaPedido), EXTRACT(MONTH FROM p.fechaPedido) " +
           "ORDER BY EXTRACT(YEAR FROM p.fechaPedido) DESC, EXTRACT(MONTH FROM p.fechaPedido) DESC")
    List<Object[]> obtenerVentasPorMes();
    
    // ===== MÉTODOS PARA ADMINISTRACIÓN =====
    
    // Búsqueda paginada de pedidos
    @Query("SELECT p FROM Pedido p WHERE " +
           "p.numeroPedido LIKE %:busqueda% OR " +
           "LOWER(p.usuario.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.usuario.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "p.numeroSeguimiento LIKE %:busqueda%")
    Page<Pedido> buscarPedidos(@Param("busqueda") String busqueda, Pageable pageable);
    
    // Contar pedidos por estado
    long countByEstado(String estado);
    
    // Contar pedidos por usuario
    long countByUsuario(Usuario usuario);
}
