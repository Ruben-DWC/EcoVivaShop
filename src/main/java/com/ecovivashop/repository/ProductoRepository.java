package com.ecovivashop.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    
    // Buscar productos activos
    List<Producto> findByEstadoTrue();
    
    // Buscar productos activos con paginación
    Page<Producto> findByEstadoTrue(Pageable pageable);
    
    // Buscar por categoría
    List<Producto> findByCategoria(String categoria);
    
    // Buscar productos activos por categoría
    List<Producto> findByCategoriaAndEstadoTrue(String categoria);
    
    // Buscar por nombre
    List<Producto> findByNombre(String nombre);
    
    // Buscar por marca
    List<Producto> findByMarca(String marca);
    
    // Buscar por rango de precios
    List<Producto> findByPrecioBetween(BigDecimal precioMin, BigDecimal precioMax);
    
    // Buscar productos eco-amigables
    @Query("SELECT p FROM Producto p WHERE p.puntuacionEco >= :puntuacionMinima AND p.estado = true")
    List<Producto> findProductosEcoAmigables(@Param("puntuacionMinima") BigDecimal puntuacionMinima);
    
    // Búsqueda general de productos
    @Query("SELECT p FROM Producto p WHERE " +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.categoria) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :busqueda, '%'))")
    List<Producto> buscarProductos(@Param("busqueda") String busqueda);
    
    // Búsqueda general de productos con paginación
    @Query("SELECT p FROM Producto p WHERE p.estado = true AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.categoria) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Producto> buscarProductosPaginado(@Param("busqueda") String busqueda, Pageable pageable);
    
    // Productos con stock disponible
    @Query("SELECT p FROM Producto p JOIN p.inventario i WHERE i.stock > 0 AND p.estado = true")
    List<Producto> findProductosConStock();
    
    // Productos con stock bajo
    @Query("SELECT p FROM Producto p JOIN p.inventario i WHERE i.stock <= i.stockMinimo AND p.estado = true")
    List<Producto> findProductosConStockBajo();
    
    // Productos agotados
    @Query("SELECT p FROM Producto p JOIN p.inventario i WHERE i.stock = 0 AND p.estado = true")
    List<Producto> findProductosAgotados();
    
    // Productos más vendidos
    @Query("SELECT p FROM Producto p JOIN p.pedidoDetalles pd " +
           "GROUP BY p ORDER BY SUM(pd.cantidad) DESC")
    List<Producto> findProductosMasVendidos(Pageable pageable);
    
    // Productos por categoría con paginación
    Page<Producto> findByCategoriaAndEstadoTrue(String categoria, Pageable pageable);
    
    // Productos por rango de precios con paginación
    Page<Producto> findByPrecioBetweenAndEstadoTrue(BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);
    
    // Contar productos por categoría
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.categoria = :categoria AND p.estado = true")
    Long contarPorCategoria(@Param("categoria") String categoria);
    
    // Obtener categorías únicas
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.estado = true ORDER BY p.categoria")
    List<String> findCategorias();
    
    // Obtener marcas únicas
    @Query("SELECT DISTINCT p.marca FROM Producto p WHERE p.estado = true ORDER BY p.marca")
    List<String> findMarcas();
    
    // Estadísticas de productos
    @Query("SELECT COUNT(p) FROM Producto p WHERE p.estado = true")
    Long contarProductosActivos();
    
    @Query("SELECT AVG(p.precio) FROM Producto p WHERE p.estado = true")
    BigDecimal obtenerPrecioPromedio();
    
    @Query("SELECT AVG(p.puntuacionEco) FROM Producto p WHERE p.puntuacionEco IS NOT NULL AND p.estado = true")
    BigDecimal obtenerPuntuacionEcoPromedio();
    
    // Productos recomendados (alta puntuación eco y precio razonable)
    @Query("SELECT p FROM Producto p WHERE p.puntuacionEco >= 7.0 AND p.precio <= :precioMaximo AND p.estado = true")
    List<Producto> findProductosRecomendados(@Param("precioMaximo") BigDecimal precioMaximo);
    
    // ===== MÉTODOS PARA REPORTES =====
    
    // Contar productos activos por estado
    Long countByEstadoTrue();
    
    // Buscar por categoría (case insensitive)
    List<Producto> findByCategoriaIgnoreCase(String categoria);
    
    // Contar productos por categoría para reportes
    @Query("SELECT p.categoria, COUNT(p) FROM Producto p WHERE p.estado = true GROUP BY p.categoria ORDER BY p.categoria")
    List<Object[]> contarProductosPorCategoria();
    
    // Obtener todas las categorías
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL ORDER BY p.categoria")
    List<String> findAllCategorias();
    
    // ===== MÉTODOS PARA EL CATÁLOGO DEL CLIENTE =====
    
    // Buscar por nombre y categoría
    Page<Producto> findByNombreContainingIgnoreCaseAndCategoriaAndEstadoTrue(
        String nombre, String categoria, Pageable pageable);
    
    // Buscar por nombre, categoría y rango de precios
    Page<Producto> findByNombreContainingIgnoreCaseAndCategoriaAndPrecioBetweenAndEstadoTrue(
        String nombre, String categoria, BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);
    
    // Buscar por nombre y rango de precios
    Page<Producto> findByNombreContainingIgnoreCaseAndPrecioBetweenAndEstadoTrue(
        String nombre, BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);
    
    // Buscar por categoría y rango de precios
    Page<Producto> findByCategoriaAndPrecioBetweenAndEstadoTrue(
        String categoria, BigDecimal precioMin, BigDecimal precioMax, Pageable pageable);
    
    // Alias para obtener categorías distintas
    @Query("SELECT DISTINCT p.categoria FROM Producto p WHERE p.categoria IS NOT NULL AND p.estado = true ORDER BY p.categoria")
    List<String> findDistinctCategorias();
    
    // ===== MÉTODOS OPTIMIZADOS PARA CATÁLOGO (SIN N+1 QUERIES) =====
    
    // Cargar productos activos con inventario optimizado
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.inventario WHERE p.estado = true")
    List<Producto> findByEstadoTrueWithInventario();
    
    // Cargar productos activos con inventario optimizado - CON PAGINACIÓN
    @Query(value = "SELECT p FROM Producto p LEFT JOIN FETCH p.inventario WHERE p.estado = true",
           countQuery = "SELECT COUNT(p) FROM Producto p WHERE p.estado = true")
    Page<Producto> findByEstadoTrueWithInventario(Pageable pageable);
    
    // Cargar productos por categoría con inventario optimizado - CON PAGINACIÓN
    @Query(value = "SELECT p FROM Producto p LEFT JOIN FETCH p.inventario WHERE p.categoria = :categoria AND p.estado = true",
           countQuery = "SELECT COUNT(p) FROM Producto p WHERE p.categoria = :categoria AND p.estado = true")
    Page<Producto> findByCategoriaAndEstadoTrueWithInventario(@Param("categoria") String categoria, Pageable pageable);
    
    // Cargar productos por rango de precio con inventario optimizado - CON PAGINACIÓN
    @Query(value = "SELECT p FROM Producto p LEFT JOIN FETCH p.inventario WHERE p.precio BETWEEN :precioMin AND :precioMax AND p.estado = true",
           countQuery = "SELECT COUNT(p) FROM Producto p WHERE p.precio BETWEEN :precioMin AND :precioMax AND p.estado = true")
    Page<Producto> findByPrecioBetweenAndEstadoTrueWithInventario(@Param("precioMin") BigDecimal precioMin, @Param("precioMax") BigDecimal precioMax, Pageable pageable);
    
    // Búsqueda optimizada con inventario - CON PAGINACIÓN
    @Query(value = "SELECT p FROM Producto p LEFT JOIN FETCH p.inventario WHERE p.estado = true AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.categoria) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :busqueda, '%')))",
           countQuery = "SELECT COUNT(p) FROM Producto p WHERE p.estado = true AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.categoria) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(p.marca) LIKE LOWER(CONCAT('%', :busqueda, '%')))")
    Page<Producto> buscarProductosPaginadoWithInventario(@Param("busqueda") String busqueda, Pageable pageable);
    
    // Buscar producto por ID con inventario cargado
    @Query("SELECT p FROM Producto p LEFT JOIN FETCH p.inventario WHERE p.idProducto = :id")
    Optional<Producto> findByIdWithInventario(@Param("id") Integer id);
}
