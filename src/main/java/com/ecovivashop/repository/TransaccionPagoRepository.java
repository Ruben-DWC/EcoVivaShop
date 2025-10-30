package com.ecovivashop.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.TransaccionPago;
import com.ecovivashop.entity.TransaccionPago.EstadoTransaccion;
import com.ecovivashop.entity.TransaccionPago.MetodoPago;
import com.ecovivashop.entity.Usuario;

@Repository
public interface TransaccionPagoRepository extends JpaRepository<TransaccionPago, Long> {
    
    /**
     * Busca una transacción por número de pedido
     */
    TransaccionPago findByNumeroPedido(String numeroPedido);
    
    /**
     * Busca transacciones por usuario
     */
    List<TransaccionPago> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);
    
    /**
     * Busca transacciones por usuario con paginación
     */
    Page<TransaccionPago> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario, Pageable pageable);
    
    /**
     * Busca transacciones por estado
     */
    List<TransaccionPago> findByEstadoTransaccionOrderByFechaCreacionDesc(EstadoTransaccion estado);
    
    /**
     * Busca transacciones por método de pago
     */
    List<TransaccionPago> findByMetodoPagoOrderByFechaCreacionDesc(MetodoPago metodoPago);
    
    /**
     * Busca transacciones por rango de fechas
     */
    @Query("SELECT t FROM TransaccionPago t WHERE t.fechaCreacion BETWEEN :fechaInicio AND :fechaFin ORDER BY t.fechaCreacion DESC")
    List<TransaccionPago> findByFechaCreacionBetween(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                                    @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Busca transacciones exitosas por usuario
     */
    @Query("SELECT t FROM TransaccionPago t WHERE t.usuario = :usuario AND t.estadoTransaccion IN ('COMPLETADA', 'AUTORIZADA') ORDER BY t.fechaCreacion DESC")
    List<TransaccionPago> findTransaccionesExitosasByUsuario(@Param("usuario") Usuario usuario);
    
    /**
     * Busca transacciones por transaction ID
     */
    Optional<TransaccionPago> findByTransactionId(String transactionId);
    
    /**
     * Busca transacciones por código de autorización
     */
    Optional<TransaccionPago> findByAuthorizationCode(String authorizationCode);
    
    /**
     * Cuenta transacciones por estado
     */
    @Query("SELECT COUNT(t) FROM TransaccionPago t WHERE t.estadoTransaccion = :estado")
    long countByEstadoTransaccion(@Param("estado") EstadoTransaccion estado);
    
    /**
     * Suma total de montos por estado
     */
    @Query("SELECT COALESCE(SUM(t.monto), 0) FROM TransaccionPago t WHERE t.estadoTransaccion = :estado")
    BigDecimal sumMontoByEstadoTransaccion(@Param("estado") EstadoTransaccion estado);
    
    /**
     * Busca transacciones de suscripciones
     */
    @Query("SELECT t FROM TransaccionPago t WHERE t.esSuscripcion = true ORDER BY t.fechaCreacion DESC")
    List<TransaccionPago> findTransaccionesSuscripciones();
    
    /**
     * Busca transacciones por ID de suscripción
     */
    List<TransaccionPago> findByIdSuscripcionOrderByFechaCreacionDesc(Long idSuscripcion);
    
    /**
     * Busca transacciones pendientes más antiguas que X tiempo
     */
    @Query("SELECT t FROM TransaccionPago t WHERE t.estadoTransaccion = 'PENDIENTE' AND t.fechaCreacion < :fechaLimite")
    List<TransaccionPago> findTransaccionesPendientesAntiguas(@Param("fechaLimite") LocalDateTime fechaLimite);
    
    /**
     * Estadísticas de transacciones por método de pago
     */
    @Query("SELECT t.metodoPago, COUNT(t), COALESCE(SUM(t.monto), 0) FROM TransaccionPago t " +
           "WHERE t.estadoTransaccion = 'COMPLETADA' GROUP BY t.metodoPago")
    List<Object[]> getEstadisticasPorMetodoPago();
    
    /**
     * Transacciones del día
     */
    @Query("SELECT t FROM TransaccionPago t WHERE CAST(t.fechaCreacion AS DATE) = CAST(CURRENT_TIMESTAMP AS DATE) ORDER BY t.fechaCreacion DESC")
    List<TransaccionPago> findTransaccionesDelDia();
    
    /**
     * Transacciones de la semana
     */
    @Query("SELECT t FROM TransaccionPago t WHERE t.fechaCreacion >= :fechaInicio ORDER BY t.fechaCreacion DESC")
    List<TransaccionPago> findTransaccionesDesdeFecha(@Param("fechaInicio") LocalDateTime fechaInicio);
    
    /**
     * Busca por email del cliente
     */
    List<TransaccionPago> findByClienteEmailOrderByFechaCreacionDesc(String email);
    
    /**
     * Busca por número de documento del cliente
     */
    List<TransaccionPago> findByClienteDocumentoNumeroOrderByFechaCreacionDesc(String numeroDocumento);
    
    /**
     * Busca transacciones fallidas con más de X intentos
     */
    @Query("SELECT t FROM TransaccionPago t WHERE t.estadoTransaccion IN ('RECHAZADA', 'ERROR') AND t.intentosPago > :minIntentos")
    List<TransaccionPago> findTransaccionesFallidasConMultiplesIntentos(@Param("minIntentos") Integer minIntentos);
    
    /**
     * Verifica si existe una transacción exitosa para un pedido
     */
    @Query("SELECT CASE WHEN COUNT(t) > 0 THEN true ELSE false END FROM TransaccionPago t " +
           "WHERE t.numeroPedido = :numeroPedido AND t.estadoTransaccion IN ('COMPLETADA', 'AUTORIZADA')")
    boolean existeTransaccionExitosaParaPedido(@Param("numeroPedido") String numeroPedido);
    
    /**
     * Busca la última transacción exitosa de un usuario
     */
    @Query("SELECT t FROM TransaccionPago t WHERE t.usuario = :usuario AND t.estadoTransaccion IN ('COMPLETADA', 'AUTORIZADA') " +
           "ORDER BY t.fechaCreacion DESC LIMIT 1")
    Optional<TransaccionPago> findUltimaTransaccionExitosa(@Param("usuario") Usuario usuario);
    
    /**
     * Reportes de ventas por rango de fechas
     */
    @Query("SELECT DATE(t.fechaCreacion) as fecha, COUNT(t) as cantidad, COALESCE(SUM(t.monto), 0) as total " +
           "FROM TransaccionPago t WHERE t.estadoTransaccion = 'COMPLETADA' " +
           "AND t.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY DATE(t.fechaCreacion) ORDER BY fecha DESC")
    List<Object[]> getReporteVentasPorFecha(@Param("fechaInicio") LocalDateTime fechaInicio, 
                                           @Param("fechaFin") LocalDateTime fechaFin);
    
    /**
     * Top métodos de pago más usados
     */
    @Query("SELECT t.metodoPago, COUNT(t) as uso FROM TransaccionPago t " +
           "WHERE t.estadoTransaccion = 'COMPLETADA' " +
           "GROUP BY t.metodoPago ORDER BY uso DESC")
    List<Object[]> getTopMetodosPago();
    
    /**
     * Busca una transacción por reference number
     */
    TransaccionPago findByReferenceNumber(String referenceNumber);
    
    /**
     * Busca una transacción por código de respuesta 
     */
    TransaccionPago findByCodigoRespuesta(String codigoRespuesta);
}
