package com.ecovivashop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Rol;
import com.ecovivashop.entity.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    // Buscar por email
    Optional<Usuario> findByEmail(String email);
    
    // Buscar por email ignorando mayúsculas
    Optional<Usuario> findByEmailIgnoreCase(String email);
    
    // Buscar usuario con rol por email
    @Query("SELECT u FROM Usuario u JOIN FETCH u.rol WHERE LOWER(u.email) = LOWER(:email)")
    Optional<Usuario> findByEmailIgnoreCaseWithRole(@Param("email") String email);
    
    // Verificar si existe email
    boolean existsByEmail(String email);
    
    // Buscar usuarios activos
    List<Usuario> findByEstadoTrue();
    
    // Buscar usuarios por rol
    List<Usuario> findByRol(Rol rol);
    
    // Buscar usuarios por nombre de rol
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = :rolNombre")
    List<Usuario> findByRolNombre(@Param("rolNombre") String rolNombre);
    
    // Buscar administradores
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre IN ('ROLE_ADMIN', 'ROLE_SUPER_ADMIN')")
    List<Usuario> findAdministradores();
    
    // Buscar clientes
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'ROLE_CLIENTE'")
    List<Usuario> findClientes();
    
    // Buscar por nombre o apellido
    @Query("SELECT u FROM Usuario u WHERE CONCAT(u.nombre, ' ', u.apellido) LIKE %:nombreCompleto%")
    List<Usuario> findByNombreCompletoContaining(@Param("nombreCompleto") String nombreCompleto);
    
    // Búsqueda general
    @Query("SELECT u FROM Usuario u WHERE " +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "u.telefono LIKE %:busqueda%")
    List<Usuario> buscarUsuarios(@Param("busqueda") String busqueda);
    
    // Usuarios registrados en un período
    @Query("SELECT u FROM Usuario u WHERE u.fechaRegistro BETWEEN :inicio AND :fin")
    List<Usuario> findByFechaRegistroBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Usuarios con paginación
    Page<Usuario> findByEstadoTrue(Pageable pageable);
    
    // Contar usuarios por rol
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = :rolNombre")
    Long contarPorRol(@Param("rolNombre") String rolNombre);
    
    // Estadísticas de usuarios
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.estado = true")
    Long contarUsuariosActivos();
    
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.fechaRegistro >= :fecha")
    Long contarUsuariosRegistradosDespueDe(@Param("fecha") LocalDateTime fecha);
    
    // Últimos usuarios registrados
    @Query("SELECT u FROM Usuario u ORDER BY u.fechaRegistro DESC")
    List<Usuario> findUltimosRegistrados(Pageable pageable);
    
    // ========== MÉTODOS PARA ADMINISTRACIÓN DE CLIENTES ==========
    
    // Buscar usuarios por rol con paginación
    Page<Usuario> findByRol(Rol rol, Pageable pageable);
    
    // Buscar usuarios por rol y estado
    Page<Usuario> findByRolAndEstado(Rol rol, boolean estado, Pageable pageable);
    
    // Buscar usuarios por rol y texto (nombre o email)
    Page<Usuario> findByRolAndNombreContainingIgnoreCaseOrRolAndEmailContainingIgnoreCase(
        Rol rol1, String nombre, Rol rol2, String email, Pageable pageable);
    
    // Búsqueda completa por rol y múltiples campos (ID, nombre, email, teléfono, DNI)
    @Query("SELECT u FROM Usuario u WHERE u.rol = :rol AND (" +
           "LOWER(u.nombre) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.apellido) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :busqueda, '%')) OR " +
           "u.telefono LIKE CONCAT('%', :busqueda, '%') OR " +
           "u.dni LIKE CONCAT('%', :busqueda, '%')" +
           ")")
    Page<Usuario> buscarClientesCompleto(@Param("rol") Rol rol, @Param("busqueda") String busqueda, Pageable pageable);
    
    // Contar usuarios por rol
    long countByRol(Rol rol);
    
    // Contar usuarios por rol y estado
    long countByRolAndEstado(Rol rol, boolean estado);
    
    // ===== MÉTODOS PARA REPORTES =====
    
    // Contar clientes activos
    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.rol.nombre = 'ROLE_CLIENTE' AND u.estado = true")
    Long contarClientesActivos();
    
    // Buscar clientes por rango de fechas
    @Query("SELECT u FROM Usuario u WHERE u.rol.nombre = 'ROLE_CLIENTE' AND u.fechaRegistro BETWEEN :fechaInicio AND :fechaFin ORDER BY u.fechaRegistro DESC")
    List<Usuario> findClientesPorFecha(@Param("fechaInicio") LocalDateTime fechaInicio, @Param("fechaFin") LocalDateTime fechaFin);
}
