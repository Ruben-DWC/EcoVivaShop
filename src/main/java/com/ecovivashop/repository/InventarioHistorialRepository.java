package com.ecovivashop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Inventario;
import com.ecovivashop.entity.InventarioHistorial;

@Repository
public interface InventarioHistorialRepository extends JpaRepository<InventarioHistorial, Integer> {

    // Obtener historial de un inventario específico, ordenado por fecha descendente
    List<InventarioHistorial> findByInventarioOrderByFechaCambioDesc(Inventario inventario);

    // Obtener los últimos N cambios de un inventario
    @Query("SELECT h FROM InventarioHistorial h WHERE h.inventario = :inventario ORDER BY h.fechaCambio DESC")
    List<InventarioHistorial> findTop10ByInventarioOrderByFechaCambioDesc(@Param("inventario") Inventario inventario);

    // Obtener historial por tipo de cambio
    List<InventarioHistorial> findByInventarioAndTipoCambioOrderByFechaCambioDesc(Inventario inventario, String tipoCambio);

    // Obtener historial por usuario
    List<InventarioHistorial> findByUsuarioOrderByFechaCambioDesc(String usuario);

    // Contar cambios en un período específico
    @Query("SELECT COUNT(h) FROM InventarioHistorial h WHERE h.inventario = :inventario AND h.fechaCambio >= :fechaInicio AND h.fechaCambio <= :fechaFin")
    Long countCambiosByInventarioAndPeriodo(@Param("inventario") Inventario inventario,
                                           @Param("fechaInicio") java.time.LocalDateTime fechaInicio,
                                           @Param("fechaFin") java.time.LocalDateTime fechaFin);

    // Obtener cambios recientes de todos los inventarios (para dashboard)
    @Query("SELECT h FROM InventarioHistorial h ORDER BY h.fechaCambio DESC")
    List<InventarioHistorial> findTop20OrderByFechaCambioDesc();
}