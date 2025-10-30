package com.ecovivashop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.Producto;

@Repository
public interface InventarioRepository extends JpaRepository<Inventario, Integer> {
    
    // Buscar inventario por producto
    Optional<Inventario> findByProducto(Producto producto);
    
    // Buscar inventario por ID de producto
    @Query("SELECT i FROM Inventario i WHERE i.producto.idProducto = :idProducto")
    Optional<Inventario> findByProductoId(@Param("idProducto") Integer idProducto);
    
    // Inventarios activos
    List<Inventario> findByEstadoTrue();
    
    // Productos con stock disponible
    @Query("SELECT i FROM Inventario i WHERE i.stock > 0 AND i.estado = true")
    List<Inventario> findConStock();
    
    // Productos agotados
    @Query("SELECT i FROM Inventario i WHERE i.stock = 0 AND i.estado = true")
    List<Inventario> findAgotados();
    
    // Productos con stock bajo (necesitan reposición)
    @Query("SELECT i FROM Inventario i WHERE i.stock <= i.stockMinimo AND i.stock > 0 AND i.estado = true")
    List<Inventario> findConStockBajo();
    
    // Productos en estado crítico
    @Query("SELECT i FROM Inventario i WHERE i.stock <= (i.stockMinimo / 2) AND i.stock > 0 AND i.estado = true")
    List<Inventario> findEnEstadoCritico();
    
    // Buscar por ubicación
    List<Inventario> findByUbicacion(String ubicacion);
    
    // Buscar por rango de stock
    @Query("SELECT i FROM Inventario i WHERE i.stock BETWEEN :stockMin AND :stockMax AND i.estado = true")
    List<Inventario> findByStockBetween(@Param("stockMin") Integer stockMin, @Param("stockMax") Integer stockMax);
    
    // Inventarios actualizados recientemente
    @Query("SELECT i FROM Inventario i WHERE i.fechaActualizacion >= :fecha AND i.estado = true")
    List<Inventario> findActualizadosDespueDe(@Param("fecha") LocalDateTime fecha);
    
    // Inventarios por usuario que los actualizó
    List<Inventario> findByUsuarioActualizacion(String usuarioActualizacion);
    
    // Estadísticas de inventario
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.stock > 0 AND i.estado = true")
    Long contarConStock();
    
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.stock = 0 AND i.estado = true")
    Long contarAgotados();
    
    @Query("SELECT COUNT(i) FROM Inventario i WHERE i.stock <= i.stockMinimo AND i.estado = true")
    Long contarConStockBajo();
    
    @Query("SELECT SUM(i.stock) FROM Inventario i WHERE i.estado = true")
    Long obtenerStockTotal();
    
    @Query("SELECT AVG(i.stock) FROM Inventario i WHERE i.estado = true")
    Double obtenerStockPromedio();
      // Alertas de inventario
    @Query("SELECT i FROM Inventario i JOIN i.producto p WHERE " +
           "i.stock <= i.stockMinimo AND i.estado = true AND p.estado = true " +
           "ORDER BY CAST(i.stock AS double) / CAST(i.stockMinimo AS double) ASC")
    List<Inventario> obtenerAlertasInventario();
    
    // Ubicaciones disponibles
    @Query("SELECT DISTINCT i.ubicacion FROM Inventario i WHERE i.ubicacion IS NOT NULL AND i.estado = true")
    List<String> findUbicaciones();
    
    // Productos por ubicación
    @Query("SELECT i FROM Inventario i WHERE i.ubicacion = :ubicacion AND i.estado = true")
    List<Inventario> findByUbicacionActivos(@Param("ubicacion") String ubicacion);
    
    // Verificar disponibilidad de stock
    @Query("SELECT CASE WHEN i.stock >= :cantidad THEN true ELSE false END " +
           "FROM Inventario i WHERE i.producto.idProducto = :idProducto")
    Boolean verificarDisponibilidad(@Param("idProducto") Integer idProducto, @Param("cantidad") Integer cantidad);
}
