package com.ecovivashop.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecovivashop.entity.Pago;
import com.ecovivashop.entity.Pedido;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Integer> {
    
    // Buscar pagos por pedido
    List<Pago> findByPedido(Pedido pedido);
    
    // Buscar por referencia de pago
    Optional<Pago> findByReferenciaPago(String referenciaPago);
    
    // Buscar por número de transacción
    Optional<Pago> findByNumeroTransaccion(String numeroTransaccion);
    
    // Buscar por número de aprobación
    Optional<Pago> findByNumeroAprobacion(String numeroAprobacion);
    
    // Buscar pagos por estado
    List<Pago> findByEstado(String estado);
    
    // Pagos aprobados
    List<Pago> findByEstadoOrderByFechaPagoDesc(String estado);
    
    // Pagos por método de pago
    List<Pago> findByMetodoPago(String metodoPago);
    
    // Pagos por entidad financiera
    List<Pago> findByEntidadFinanciera(String entidadFinanciera);
    
    // Pagos en un rango de fechas
    @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :inicio AND :fin")
    List<Pago> findByFechaPagoBetween(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Pagos aprobados en un período
    @Query("SELECT p FROM Pago p WHERE p.estado = 'APROBADO' AND p.fechaAprobacion BETWEEN :inicio AND :fin")
    List<Pago> findAprobadosEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Pagos pendientes por más de X tiempo
    @Query("SELECT p FROM Pago p WHERE p.estado = 'PENDIENTE' AND p.fechaPago < :fechaLimite")
    List<Pago> findPagosPendientesVencidos(@Param("fechaLimite") LocalDateTime fechaLimite);
    
    // Pagos rechazados
    List<Pago> findByEstadoAndCodigoRespuestaIsNotNull(String estado);
    
    // Estadísticas de pagos
    @Query("SELECT COUNT(p) FROM Pago p WHERE p.estado = :estado")
    Long contarPorEstado(@Param("estado") String estado);
    
    @Query("SELECT COUNT(p) FROM Pago p WHERE p.metodoPago = :metodo")
    Long contarPorMetodoPago(@Param("metodo") String metodo);
    
    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.estado = 'APROBADO' AND p.fechaAprobacion BETWEEN :inicio AND :fin")
    BigDecimal calcularMontoAprobadoEnPeriodo(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    @Query("SELECT AVG(p.monto) FROM Pago p WHERE p.estado = 'APROBADO'")
    BigDecimal obtenerMontoPromedio();
    
    // Métodos de pago más utilizados
    @Query("SELECT p.metodoPago, COUNT(p) FROM Pago p WHERE p.estado = 'APROBADO' " +
           "GROUP BY p.metodoPago ORDER BY COUNT(p) DESC")
    List<Object[]> obtenerMetodosPagoMasUtilizados();
    
    // Entidades financieras más utilizadas
    @Query("SELECT p.entidadFinanciera, COUNT(p) FROM Pago p WHERE p.estado = 'APROBADO' " +
           "AND p.entidadFinanciera IS NOT NULL " +
           "GROUP BY p.entidadFinanciera ORDER BY COUNT(p) DESC")
    List<Object[]> obtenerEntidadesFinancierasMasUtilizadas();
    
    // Tasa de aprobación por método de pago
    @Query("SELECT p.metodoPago, " +
           "COUNT(CASE WHEN p.estado = 'APROBADO' THEN 1 END) as aprobados, " +
           "COUNT(p) as total " +
           "FROM Pago p GROUP BY p.metodoPago")
    List<Object[]> obtenerTasaAprobacionPorMetodo();
    
    // Pagos por rango de monto
    @Query("SELECT p FROM Pago p WHERE p.monto BETWEEN :montoMin AND :montoMax")
    List<Pago> findByMontoBetween(@Param("montoMin") BigDecimal montoMin, @Param("montoMax") BigDecimal montoMax);
    
    // Pagos con mayor monto
    @Query("SELECT p FROM Pago p WHERE p.estado = 'APROBADO' ORDER BY p.monto DESC")
    List<Pago> findPagosConMayorMonto(org.springframework.data.domain.Pageable pageable);
    
    // Estados de pago únicos
    @Query("SELECT DISTINCT p.estado FROM Pago p ORDER BY p.estado")
    List<String> findEstadosPago();
    
    // Códigos de respuesta más frecuentes (para análisis de errores)
    @Query("SELECT p.codigoRespuesta, p.mensajeRespuesta, COUNT(p) " +
           "FROM Pago p WHERE p.estado = 'RECHAZADO' AND p.codigoRespuesta IS NOT NULL " +
           "GROUP BY p.codigoRespuesta, p.mensajeRespuesta ORDER BY COUNT(p) DESC")
    List<Object[]> obtenerCodigosRespuestaMasFrecuentes();    // Tiempo promedio de procesamiento de pagos (conteo alternativo)
    @Query("SELECT COUNT(p) FROM Pago p WHERE p.estado = 'APROBADO' AND p.fechaAprobacion IS NOT NULL")
    Long contarPagosAprobados();
    
    // Pagos por IP (para análisis de seguridad)
    @Query("SELECT p.ipCliente, COUNT(p) FROM Pago p WHERE p.ipCliente IS NOT NULL " +
           "GROUP BY p.ipCliente ORDER BY COUNT(p) DESC")
    List<Object[]> obtenerPagosPorIP(org.springframework.data.domain.Pageable pageable);
    
    // Buscar pagos sospechosos (múltiples intentos desde la misma IP)
    @Query("SELECT p FROM Pago p WHERE p.ipCliente IN " +
           "(SELECT p2.ipCliente FROM Pago p2 WHERE p2.estado = 'RECHAZADO' " +
           "GROUP BY p2.ipCliente HAVING COUNT(p2) >= :intentosMinimos)")
    List<Pago> findPagosSospechosos(@Param("intentosMinimos") Long intentosMinimos);
    
    // Ingresos diarios
    @Query("SELECT DATE(p.fechaAprobacion), SUM(p.monto) " +
           "FROM Pago p WHERE p.estado = 'APROBADO' " +
           "AND p.fechaAprobacion BETWEEN :inicio AND :fin " +
           "GROUP BY DATE(p.fechaAprobacion) " +
           "ORDER BY DATE(p.fechaAprobacion)")
    List<Object[]> obtenerIngresosDiarios(@Param("inicio") LocalDateTime inicio, @Param("fin") LocalDateTime fin);
    
    // Verificar si un pedido está completamente pagado
    @Query("SELECT CASE WHEN SUM(p.monto) >= ped.total THEN true ELSE false END " +
           "FROM Pago p JOIN p.pedido ped WHERE ped = :pedido AND p.estado = 'APROBADO'")
    Boolean verificarPedidoCompletamentePagado(@Param("pedido") Pedido pedido);
}
