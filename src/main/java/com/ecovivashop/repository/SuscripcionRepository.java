package com.ecovivashop.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Suscripcion;
import com.ecovivashop.entity.Usuario;

@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Integer> {
    
    // Buscar suscripciones por usuario
    List<Suscripcion> findByUsuario(Usuario usuario);
    
    // Buscar suscripción activa del usuario
    @Query("SELECT s FROM Suscripcion s WHERE s.usuario = :usuario AND s.estado = true " +
           "AND s.fechaInicio <= :ahora AND (s.fechaFin IS NULL OR s.fechaFin > :ahora)")
    Optional<Suscripcion> findSuscripcionActivaByUsuario(@Param("usuario") Usuario usuario, @Param("ahora") LocalDateTime ahora);
    
    // Buscar por tipo de suscripción
    List<Suscripcion> findByTipoSuscripcion(String tipoSuscripcion);
    
    // Suscripciones activas
    @Query("SELECT s FROM Suscripcion s WHERE s.estado = true " +
           "AND s.fechaInicio <= :ahora AND (s.fechaFin IS NULL OR s.fechaFin > :ahora)")
    List<Suscripcion> findSuscripcionesActivas(@Param("ahora") LocalDateTime ahora);
    
    // Suscripciones próximas a vencer
    @Query("SELECT s FROM Suscripcion s WHERE s.estado = true " +
           "AND s.fechaFin BETWEEN :ahora AND :fechaLimite")
    List<Suscripcion> findProximasAVencer(@Param("ahora") LocalDateTime ahora, @Param("fechaLimite") LocalDateTime fechaLimite);
    
    // Suscripciones vencidas
    @Query("SELECT s FROM Suscripcion s WHERE s.estado = true AND s.fechaFin < :ahora")
    List<Suscripcion> findVencidas(@Param("ahora") LocalDateTime ahora);
    
    // Suscripciones canceladas
    List<Suscripcion> findByEstadoFalse();
    
    // Suscripciones con auto-renovación
    List<Suscripcion> findByAutoRenovacionTrue();
    
    // Suscripciones creadas en un período
    @Query("SELECT s FROM Suscripcion s WHERE s.fechaCreacion BETWEEN :inicio AND :fin")
    List<Suscripcion> findByFechaCreacionBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Suscripciones canceladas en un período
    @Query("SELECT s FROM Suscripcion s WHERE s.fechaCancelacion BETWEEN :inicio AND :fin")
    List<Suscripcion> findCanceladasEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Estadísticas de suscripciones
    @Query("SELECT COUNT(s) FROM Suscripcion s WHERE s.estado = true " +
           "AND s.fechaInicio <= :ahora AND (s.fechaFin IS NULL OR s.fechaFin > :ahora)")
    Long contarSuscripcionesActivas(@Param("ahora") LocalDateTime ahora);
    
    @Query("SELECT COUNT(s) FROM Suscripcion s WHERE s.tipoSuscripcion = :tipo AND s.estado = true")
    Long contarPorTipo(@Param("tipo") String tipo);
    
    @Query("SELECT SUM(s.precioMensual) FROM Suscripcion s WHERE s.estado = true " +
           "AND s.fechaInicio <= :ahora AND (s.fechaFin IS NULL OR s.fechaFin > :ahora)")
    Double calcularIngresosMensuales(@Param("ahora") LocalDateTime ahora);
    
    // Obtener tipos de suscripción únicos
    @Query("SELECT DISTINCT s.tipoSuscripcion FROM Suscripcion s ORDER BY s.tipoSuscripcion")
    List<String> findTiposSuscripcion();
    
    // Suscripciones más populares
    @Query("SELECT s.tipoSuscripcion, COUNT(s) FROM Suscripcion s " +
           "WHERE s.estado = true GROUP BY s.tipoSuscripcion ORDER BY COUNT(s) DESC")
    List<Object[]> obtenerTiposMasPopulares();
    
    // Verificar si usuario tiene suscripción activa
    @Query("SELECT CASE WHEN COUNT(s) > 0 THEN true ELSE false END FROM Suscripcion s " +
           "WHERE s.usuario = :usuario AND s.estado = true " +
           "AND s.fechaInicio <= :ahora AND (s.fechaFin IS NULL OR s.fechaFin > :ahora)")
    Boolean usuarioTieneSuscripcionActiva(@Param("usuario") Usuario usuario, @Param("ahora") LocalDateTime ahora);
    
    // Buscar por motivo de cancelación
    @Query("SELECT s FROM Suscripcion s WHERE s.motivoCancelacion LIKE %:motivo%")
    List<Suscripcion> findByMotivoCancelacionContaining(@Param("motivo") String motivo);
    
    // Suscripciones que necesitan renovación automática
    @Query("SELECT s FROM Suscripcion s WHERE s.autoRenovacion = true AND s.estado = true " +
           "AND s.fechaFin BETWEEN :ahora AND :fechaLimite")
    List<Suscripcion> findParaRenovacionAutomatica(@Param("ahora") LocalDateTime ahora, @Param("fechaLimite") LocalDateTime fechaLimite);
}
