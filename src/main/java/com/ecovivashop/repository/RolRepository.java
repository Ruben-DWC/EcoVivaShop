package com.ecovivashop.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Rol;

@Repository
public interface RolRepository extends JpaRepository<Rol, Integer> {
    
    // Buscar rol por nombre
    Optional<Rol> findByNombre(String nombre);
    
    // Buscar rol por nombre ignorando mayúsculas/minúsculas
    Optional<Rol> findByNombreIgnoreCase(String nombre);
    
    // Verificar si existe un rol con ese nombre
    boolean existsByNombre(String nombre);
    
    // Buscar roles activos
    List<Rol> findByEstadoTrue();
    
    // Buscar roles inactivos
    List<Rol> findByEstadoFalse();
    
    // Buscar roles por estado
    List<Rol> findByEstado(Boolean estado);
    
    // Buscar roles que contengan un texto en el nombre
    @Query("SELECT r FROM Rol r WHERE r.nombre LIKE %:texto% OR r.descripcion LIKE %:texto%")
    List<Rol> buscarPorTexto(@Param("texto") String texto);
    
    // Contar roles activos
    @Query("SELECT COUNT(r) FROM Rol r WHERE r.estado = true")
    Long contarRolesActivos();
    
    // Obtener roles más utilizados
    @Query("SELECT r FROM Rol r LEFT JOIN r.usuarios u GROUP BY r ORDER BY COUNT(u) DESC")
    List<Rol> obtenerRolesMasUtilizados();
}
