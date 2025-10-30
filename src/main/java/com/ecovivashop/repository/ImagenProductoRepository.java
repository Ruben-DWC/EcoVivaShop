package com.ecovivashop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.ImagenProducto;

@Repository
public interface ImagenProductoRepository extends JpaRepository<ImagenProducto, Long> {
    
    @Query("SELECT i FROM ImagenProducto i WHERE i.productoId = :productoId AND i.activo = true ORDER BY i.orden ASC")
    List<ImagenProducto> findByProductoIdAndActivoTrueOrderByOrden(@Param("productoId") Long productoId);
    
    @Query("SELECT i FROM ImagenProducto i WHERE i.productoId = :productoId AND i.esPrincipal = true AND i.activo = true")
    Optional<ImagenProducto> findPrincipalByProductoId(@Param("productoId") Long productoId);
    
    @Query("SELECT i FROM ImagenProducto i WHERE i.productoId = :productoId AND i.activo = true")
    List<ImagenProducto> findByProductoIdAndActivoTrue(@Param("productoId") Long productoId);
    
    @Query("SELECT i FROM ImagenProducto i WHERE i.activo = true")
    List<ImagenProducto> findAllActive();
    
    @Modifying
    @Transactional
    @Query("UPDATE ImagenProducto i SET i.activo = false WHERE i.productoId = :productoId")
    void deactivateByProductoId(@Param("productoId") Long productoId);
    
    @Modifying
    @Transactional
    @Query("UPDATE ImagenProducto i SET i.esPrincipal = false WHERE i.productoId = :productoId")
    void clearPrincipalByProductoId(@Param("productoId") Long productoId);
    
    @Query("SELECT COUNT(i) FROM ImagenProducto i WHERE i.productoId = :productoId AND i.activo = true")
    Long countByProductoIdAndActivoTrue(@Param("productoId") Long productoId);
}
