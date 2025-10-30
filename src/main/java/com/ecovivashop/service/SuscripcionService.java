package com.ecovivashop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecovivashop.entity.Suscripcion;
import com.ecovivashop.entity.TransaccionPago;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.SuscripcionRepository;
import com.ecovivashop.repository.TransaccionPagoRepository;

@Service
@Transactional
public class SuscripcionService {
    
    @Autowired
    private SuscripcionRepository suscripcionRepository;
    
    @Autowired
    private TransaccionPagoRepository transaccionPagoRepository;
    
    @Autowired
    private PasarelaPagoService pasarelaPagoService;
    
    // Tipos de suscripción disponibles
    public enum TipoSuscripcion {
        BASIC("BASIC", new BigDecimal("29.90"), "Acceso básico a productos y descuentos del 5%"),
        PREMIUM("PREMIUM", new BigDecimal("59.90"), "Acceso premium con descuentos del 15% y envío gratis"),
        VIP("VIP", new BigDecimal("99.90"), "Acceso VIP con descuentos del 25%, envío gratis y soporte prioritario");
        
        private final String nombre;
        private final BigDecimal precio;
        private final String descripcion;
        
        TipoSuscripcion(String nombre, BigDecimal precio, String descripcion) {
            this.nombre = nombre;
            this.precio = precio;
            this.descripcion = descripcion;
        }
        
        public String getNombre() { return nombre; }
        public BigDecimal getPrecio() { return precio; }
        public String getDescripcion() { return descripcion; }
    }
    
    /**
     * Crear nueva suscripción con pago
     */
    public ResultadoSuscripcion crearSuscripcion(Usuario usuario, String tipoSuscripcion, 
                                               int mesesDuracion, PasarelaPagoService.SolicitudPago solicitudPago) {
        try {
            // Validar que no tenga suscripción activa
            if (tieneSuscripcionActiva(usuario)) {
                return new ResultadoSuscripcion(false, "Usuario ya tiene una suscripción activa", null, null);
            }
            
            // Obtener información del tipo de suscripción
            TipoSuscripcion tipo = TipoSuscripcion.valueOf(tipoSuscripcion);
            BigDecimal montoTotal = tipo.getPrecio().multiply(new BigDecimal(mesesDuracion));
            
            // Crear la suscripción
            Suscripcion suscripcion = new Suscripcion();
            suscripcion.setUsuario(usuario);
            suscripcion.setTipoSuscripcion(tipo.getNombre());
            suscripcion.setPrecioMensual(tipo.getPrecio());
            suscripcion.setBeneficios(tipo.getDescripcion());
            suscripcion.setFechaInicio(LocalDateTime.now());
            suscripcion.setFechaFin(LocalDateTime.now().plusMonths(mesesDuracion));
            suscripcion.setAutoRenovacion(true);
            suscripcion.setEstado(false); // Inactiva hasta completar pago
            
            // Guardar suscripción temporal
            suscripcion = suscripcionRepository.save(suscripcion);
            
            // Crear transacción de pago para la suscripción
            TransaccionPago transaccion = new TransaccionPago();
            transaccion.setUsuario(usuario);
            transaccion.setMonto(montoTotal);
            transaccion.setCurrencyCode("PEN");
            transaccion.setNumeroPedido("SUBS-" + System.currentTimeMillis());
            transaccion.setEsSuscripcion(true);
            transaccion.setIdSuscripcion(suscripcion.getIdSuscripcion().longValue());
            
            // Procesar pago
            PasarelaPagoService.RespuestaPago respuestaPago = pasarelaPagoService.procesarPago(solicitudPago);
            
            if (respuestaPago.isExitoso()) {
                // Activar suscripción si el pago fue exitoso
                suscripcion.setEstado(true);
                suscripcionRepository.save(suscripcion);
                
                return new ResultadoSuscripcion(true, "Suscripción creada exitosamente", 
                                              suscripcion, null);
            } else {
                // Eliminar suscripción si el pago falló
                suscripcionRepository.delete(suscripcion);
                return new ResultadoSuscripcion(false, "Error en el pago: " + respuestaPago.getMensaje(), 
                                              null, null);
            }
            
        } catch (Exception e) {
            return new ResultadoSuscripcion(false, "Error al crear suscripción: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Renovar suscripción existente
     */
    public ResultadoSuscripcion renovarSuscripcion(Integer idSuscripcion, int mesesExtension, 
                                                 PasarelaPagoService.SolicitudPago solicitudPago) {
        try {
            Optional<Suscripcion> optSuscripcion = suscripcionRepository.findById(idSuscripcion);
            if (!optSuscripcion.isPresent()) {
                return new ResultadoSuscripcion(false, "Suscripción no encontrada", null, null);
            }
            
            Suscripcion suscripcion = optSuscripcion.get();
            if (!suscripcion.getEstado()) {
                return new ResultadoSuscripcion(false, "La suscripción está cancelada", null, null);
            }
            
            // Calcular monto de renovación
            BigDecimal montoRenovacion = suscripcion.getPrecioMensual().multiply(new BigDecimal(mesesExtension));
            
            // Crear transacción de renovación
            TransaccionPago transaccion = new TransaccionPago();
            transaccion.setUsuario(suscripcion.getUsuario());
            transaccion.setMonto(montoRenovacion);
            transaccion.setCurrencyCode("PEN");
            transaccion.setNumeroPedido("RENOV-" + suscripcion.getIdSuscripcion() + "-" + System.currentTimeMillis());
            transaccion.setEsSuscripcion(true);
            transaccion.setIdSuscripcion(suscripcion.getIdSuscripcion().longValue());
            
            // Procesar pago de renovación
            PasarelaPagoService.RespuestaPago respuestaPago = pasarelaPagoService.procesarPago(solicitudPago);
            
            if (respuestaPago.isExitoso()) {
                // Extender fecha de fin
                if (suscripcion.getFechaFin() == null || suscripcion.getFechaFin().isBefore(LocalDateTime.now())) {
                    suscripcion.setFechaFin(LocalDateTime.now().plusMonths(mesesExtension));
                } else {
                    suscripcion.setFechaFin(suscripcion.getFechaFin().plusMonths(mesesExtension));
                }
                
                suscripcionRepository.save(suscripcion);
                
                return new ResultadoSuscripcion(true, "Suscripción renovada exitosamente", 
                                              suscripcion, null);
            } else {
                return new ResultadoSuscripcion(false, "Error en el pago de renovación: " + respuestaPago.getMensaje(), 
                                              suscripcion, null);
            }
            
        } catch (Exception e) {
            return new ResultadoSuscripcion(false, "Error al renovar suscripción: " + e.getMessage(), null, null);
        }
    }
    
    /**
     * Cancelar suscripción
     */
    public boolean cancelarSuscripcion(Integer idSuscripcion, String motivo) {
        try {
            Optional<Suscripcion> optSuscripcion = suscripcionRepository.findById(idSuscripcion);
            if (!optSuscripcion.isPresent()) {
                return false;
            }
            
            Suscripcion suscripcion = optSuscripcion.get();
            suscripcion.cancelar(motivo);
            suscripcionRepository.save(suscripcion);
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Verificar si usuario tiene suscripción activa
     */
    @Transactional(readOnly = true)
    public boolean tieneSuscripcionActiva(Usuario usuario) {
        Optional<Suscripcion> suscripcion = suscripcionRepository.findSuscripcionActivaByUsuario(
            usuario, LocalDateTime.now());
        return suscripcion.isPresent();
    }
    
    /**
     * Obtener suscripción activa del usuario
     */
    @Transactional(readOnly = true)
    public Optional<Suscripcion> getSuscripcionActiva(Usuario usuario) {
        return suscripcionRepository.findSuscripcionActivaByUsuario(usuario, LocalDateTime.now());
    }
    
    /**
     * Obtener todas las suscripciones del usuario
     */
    @Transactional(readOnly = true)
    public List<Suscripcion> getSuscripcionesUsuario(Usuario usuario) {
        return suscripcionRepository.findByUsuario(usuario);
    }
    
    /**
     * Procesar auto-renovaciones
     */
    @Transactional
    public void procesarAutoRenovaciones() {
        // Buscar suscripciones que expiran en los próximos 3 días
        LocalDateTime ahora = LocalDateTime.now();
        
        List<Suscripcion> suscripcionesPorVencer = suscripcionRepository.findSuscripcionesActivas(ahora);
        
        for (Suscripcion suscripcion : suscripcionesPorVencer) {
            if (suscripcion.getAutoRenovacion() && suscripcion.proximaAVencer()) {
                try {
                    // Intentar auto-renovación por 1 mes
                    // Aquí necesitarías un método de pago guardado del usuario
                    // Por simplicidad, renovamos automáticamente
                    suscripcion.renovar(1);
                    suscripcionRepository.save(suscripcion);
                    
                    // Crear transacción de auto-renovación
                    TransaccionPago transaccion = new TransaccionPago();
                    transaccion.setUsuario(suscripcion.getUsuario());
                    transaccion.setMonto(suscripcion.getPrecioMensual());
                    transaccion.setCurrencyCode("PEN");
                    transaccion.setNumeroPedido("AUTO-RENOV-" + suscripcion.getIdSuscripcion() + "-" + System.currentTimeMillis());
                    transaccion.setEstadoTransaccion(TransaccionPago.EstadoTransaccion.COMPLETADA);
                    transaccion.setEsSuscripcion(true);
                    transaccion.setIdSuscripcion(suscripcion.getIdSuscripcion().longValue());
                    
                    transaccionPagoRepository.save(transaccion);
                    
                } catch (Exception e) {
                    // Log error pero continúa con otras suscripciones
                    System.err.println("Error en auto-renovación para suscripción " + 
                                     suscripcion.getIdSuscripcion() + ": " + e.getMessage());
                }
            }
        }
    }
    
    /**
     * Obtener descuento por suscripción
     */
    @Transactional(readOnly = true)
    public BigDecimal obtenerDescuentoPorSuscripcion(Usuario usuario) {
        Optional<Suscripcion> suscripcionOpt = getSuscripcionActiva(usuario);
        if (suscripcionOpt.isPresent()) {
            Suscripcion suscripcion = suscripcionOpt.get();
            
            // Aplicar descuentos según el tipo
            return switch (suscripcion.getTipoSuscripcion()) {
                case "BASIC" -> new BigDecimal("5.00"); // 5%
                case "PREMIUM" -> new BigDecimal("15.00"); // 15%
                case "VIP" -> new BigDecimal("25.00"); // 25%
                default -> BigDecimal.ZERO;
            };
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * Verificar si tiene envío gratis por suscripción
     */
    @Transactional(readOnly = true)
    public boolean tieneEnvioGratis(Usuario usuario) {
        Optional<Suscripcion> suscripcionOpt = getSuscripcionActiva(usuario);
        if (suscripcionOpt.isPresent()) {
            String tipo = suscripcionOpt.get().getTipoSuscripcion();
            return "PREMIUM".equals(tipo) || "VIP".equals(tipo);
        }
        return false;
    }
    
    /**
     * Obtener estadísticas de suscripciones
     */
    @Transactional(readOnly = true)
    public EstadisticasSuscripcion getEstadisticas() {
        LocalDateTime ahora = LocalDateTime.now();
        
        long totalActivas = suscripcionRepository.findSuscripcionesActivas(ahora)
                                                .stream()
                                                .filter(s -> s.estaActiva())
                                                .count();
        
        long totalBasic = suscripcionRepository.findByTipoSuscripcion("BASIC")
                                             .stream()
                                             .filter(s -> s.estaActiva())
                                             .count();
        
        long totalPremium = suscripcionRepository.findByTipoSuscripcion("PREMIUM")
                                               .stream()
                                               .filter(s -> s.estaActiva())
                                               .count();
        
        long totalVip = suscripcionRepository.findByTipoSuscripcion("VIP")
                                           .stream()
                                           .filter(s -> s.estaActiva())
                                           .count();
        
        return new EstadisticasSuscripcion(totalActivas, totalBasic, totalPremium, totalVip);
    }
    
    // Clases auxiliares
    public static class ResultadoSuscripcion {
        private final boolean exitoso;
        private final String mensaje;
        private final Suscripcion suscripcion;
        private final TransaccionPago transaccion;
        
        public ResultadoSuscripcion(boolean exitoso, String mensaje, Suscripcion suscripcion, TransaccionPago transaccion) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.suscripcion = suscripcion;
            this.transaccion = transaccion;
        }
        
        // Getters
        public boolean isExitoso() { return exitoso; }
        public String getMensaje() { return mensaje; }
        public Suscripcion getSuscripcion() { return suscripcion; }
        public TransaccionPago getTransaccion() { return transaccion; }
    }
    
    public static class EstadisticasSuscripcion {
        private final long totalActivas;
        private final long totalBasic;
        private final long totalPremium;
        private final long totalVip;
        
        public EstadisticasSuscripcion(long totalActivas, long totalBasic, long totalPremium, long totalVip) {
            this.totalActivas = totalActivas;
            this.totalBasic = totalBasic;
            this.totalPremium = totalPremium;
            this.totalVip = totalVip;
        }
        
        // Getters
        public long getTotalActivas() { return totalActivas; }
        public long getTotalBasic() { return totalBasic; }
        public long getTotalPremium() { return totalPremium; }
        public long getTotalVip() { return totalVip; }
    }
}
