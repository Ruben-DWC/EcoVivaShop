package com.ecovivashop.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Pedido;
import com.ecovivashop.entity.PedidoDetalle;
import com.ecovivashop.entity.Producto;

@Repository
public interface PedidoDetalleRepository extends JpaRepository<PedidoDetalle, Integer> {
    
    // Buscar detalles por pedido
    List<PedidoDetalle> findByPedido(Pedido pedido);
    
    // Buscar detalles por producto
    List<PedidoDetalle> findByProducto(Producto producto);
    
    // Productos más vendidos
    @Query("SELECT pd.producto, SUM(pd.cantidad) FROM PedidoDetalle pd " +
           "JOIN pd.pedido p WHERE p.estado = 'ENTREGADO' " +
           "GROUP BY pd.producto ORDER BY SUM(pd.cantidad) DESC")
    List<Object[]> findProductosMasVendidos(Pageable pageable);
    
    // Productos que generan más ingresos
    @Query("SELECT pd.producto, SUM(pd.subtotal) FROM PedidoDetalle pd " +
           "JOIN pd.pedido p WHERE p.estado = 'ENTREGADO' " +
           "GROUP BY pd.producto ORDER BY SUM(pd.subtotal) DESC")
    List<Object[]> findProductosQueGeneranMasIngresos(Pageable pageable);
    
    // Cantidad total vendida de un producto
    @Query("SELECT COALESCE(SUM(pd.cantidad), 0) FROM PedidoDetalle pd " +
           "JOIN pd.pedido p WHERE pd.producto = :producto AND p.estado = 'ENTREGADO'")
    Long obtenerCantidadVendidaDelProducto(@Param("producto") Producto producto);
    
    // Ingresos totales de un producto
    @Query("SELECT COALESCE(SUM(pd.subtotal), 0) FROM PedidoDetalle pd " +
           "JOIN pd.pedido p WHERE pd.producto = :producto AND p.estado = 'ENTREGADO'")
    BigDecimal obtenerIngresosDelProducto(@Param("producto") Producto producto);
    
    // Detalles con descuento
    @Query("SELECT pd FROM PedidoDetalle pd WHERE pd.descuentoUnitario > 0")
    List<PedidoDetalle> findConDescuento();
    
    // Total de descuentos aplicados
    @Query("SELECT COALESCE(SUM(pd.descuentoUnitario * pd.cantidad), 0) FROM PedidoDetalle pd " +
           "JOIN pd.pedido p WHERE p.estado = 'ENTREGADO'")
    BigDecimal calcularTotalDescuentos();
    
    // Promedio de cantidad por producto
    @Query("SELECT AVG(pd.cantidad) FROM PedidoDetalle pd JOIN pd.pedido p WHERE pd.producto = :producto AND p.estado = 'ENTREGADO'")
    Double obtenerPromedioCantidadPorProducto(@Param("producto") Producto producto);
    
    // Análisis de ventas por categoría
    @Query("SELECT pd.producto.categoria, SUM(pd.cantidad), SUM(pd.subtotal) " +
           "FROM PedidoDetalle pd JOIN pd.pedido p WHERE p.estado = 'ENTREGADO' " +
           "GROUP BY pd.producto.categoria ORDER BY SUM(pd.subtotal) DESC")
    List<Object[]> obtenerVentasPorCategoria();
    
    // Análisis de ventas por marca
    @Query("SELECT pd.producto.marca, SUM(pd.cantidad), SUM(pd.subtotal) " +
           "FROM PedidoDetalle pd JOIN pd.pedido p WHERE p.estado = 'ENTREGADO' " +
           "GROUP BY pd.producto.marca ORDER BY SUM(pd.subtotal) DESC")
    List<Object[]> obtenerVentasPorMarca();
    
    // Productos nunca vendidos
    @Query("SELECT p FROM Producto p WHERE p.idProducto NOT IN " +
           "(SELECT DISTINCT pd.producto.idProducto FROM PedidoDetalle pd JOIN pd.pedido ped WHERE ped.estado = 'ENTREGADO')")
    List<Producto> findProductosNuncaVendidos();
    
    // Detalles de pedidos en un rango de fechas
    @Query("SELECT pd FROM PedidoDetalle pd JOIN pd.pedido p " +
           "WHERE p.fechaPedido BETWEEN :inicio AND :fin")
    List<PedidoDetalle> findDetallesEnPeriodo(@Param("inicio") java.time.LocalDateTime inicio, 
                                             @Param("fin") java.time.LocalDateTime fin);
    
    // Productos con mayor margen de ganancia
    @Query("SELECT pd.producto, AVG(pd.precioUnitario - pd.producto.precio) " +
           "FROM PedidoDetalle pd JOIN pd.pedido p WHERE p.estado = 'ENTREGADO' " +
           "GROUP BY pd.producto ORDER BY AVG(pd.precioUnitario - pd.producto.precio) DESC")
    List<Object[]> findProductosConMayorMargen(Pageable pageable);
    
    // Estadísticas generales de detalles
    @Query("SELECT COUNT(pd), SUM(pd.cantidad), SUM(pd.subtotal) " +
           "FROM PedidoDetalle pd JOIN pd.pedido p WHERE p.estado = 'ENTREGADO'")
    Object[] obtenerEstadisticasGenerales();
    
    // Productos que se venden juntos frecuentemente
    @Query("SELECT pd1.producto, pd2.producto, COUNT(*) " +
           "FROM PedidoDetalle pd1 JOIN PedidoDetalle pd2 ON pd1.pedido = pd2.pedido " +
           "WHERE pd1.producto.idProducto < pd2.producto.idProducto " +
           "AND pd1.pedido.estado = 'ENTREGADO' " +
           "GROUP BY pd1.producto, pd2.producto " +
           "ORDER BY COUNT(*) DESC")
    List<Object[]> findProductosQueSeVendenJuntos(Pageable pageable);
    
    // Cantidad total de items vendidos
    @Query("SELECT COALESCE(SUM(pd.cantidad), 0) FROM PedidoDetalle pd " +
           "JOIN pd.pedido p WHERE p.estado = 'ENTREGADO'")
    Long obtenerTotalItemsVendidos();
    
    // Valor promedio por item
    @Query("SELECT AVG(pd.precioUnitario) FROM PedidoDetalle pd " +
           "JOIN pd.pedido p WHERE p.estado = 'ENTREGADO'")
    BigDecimal obtenerValorPromedioPorItem();
    
    // Productos con stock insuficiente para pedidos pendientes
    @Query("SELECT pd.producto, SUM(pd.cantidad) " +
           "FROM PedidoDetalle pd JOIN pd.pedido p " +
           "WHERE p.estado IN ('PENDIENTE', 'CONFIRMADO') " +
           "GROUP BY pd.producto " +
           "HAVING SUM(pd.cantidad) > " +
           "(SELECT COALESCE(i.stock, 0) FROM Inventario i WHERE i.producto = pd.producto)")
    List<Object[]> findProductosConStockInsuficiente();
}
