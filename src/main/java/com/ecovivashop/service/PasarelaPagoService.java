package com.ecovivashop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecovivashop.entity.TransaccionPago;
import com.ecovivashop.entity.TransaccionPago.EstadoTransaccion;
import com.ecovivashop.entity.TransaccionPago.MetodoPago;
import com.ecovivashop.entity.TransaccionPago.PasarelaPago;
import com.ecovivashop.entity.Usuario;
import com.ecovivashop.repository.TransaccionPagoRepository;

@Service
public class PasarelaPagoService {
    
    @Autowired
    private TransaccionPagoRepository transaccionRepository;
    
    /**
     * Procesa un pago según el método seleccionado
     */
    public RespuestaPago procesarPago(SolicitudPago solicitud) {
        try {
            // Crear transacción
            TransaccionPago transaccion = crearTransaccion(solicitud);
            transaccion = transaccionRepository.save(transaccion);
            
            // Procesar según el método de pago
            RespuestaPago respuesta = switch (solicitud.getMetodoPago()) {
                case TARJETA_CREDITO, TARJETA_DEBITO -> procesarPagoTarjeta(transaccion, solicitud);
                case YAPE -> procesarPagoYape(transaccion, solicitud);
                case PLIN -> procesarPagoPlin(transaccion, solicitud);
                case EFECTIVO -> procesarPagoEfectivo(transaccion, solicitud);
                default -> RespuestaPago.error("Método de pago no soportado");
            };
            
            return respuesta;
            
        } catch (Exception e) {
            return RespuestaPago.error("Error procesando pago: " + e.getMessage());
        }
    }
    
    /**
     * Confirma un pago usando código de confirmación
     */
    public RespuestaPago confirmarPago(String transactionId, String codigoConfirmacion) {
        try {
            Optional<TransaccionPago> optionalTransaccion = transaccionRepository.findByTransactionId(transactionId);
            if (optionalTransaccion.isEmpty()) {
                return RespuestaPago.error("Transacción no encontrada");
            }
            
            TransaccionPago transaccion = optionalTransaccion.get();
            
            if (validarCodigoConfirmacion(transaccion, codigoConfirmacion)) {
                transaccion.setEstadoTransaccion(EstadoTransaccion.COMPLETADA);
                transaccion.setFechaTransaccion(LocalDateTime.now());
                transaccionRepository.save(transaccion);
                
                return RespuestaPago.exitoso("Pago confirmado exitosamente", transactionId);
            } else {
                return RespuestaPago.error("Código de confirmación inválido");
            }
            
        } catch (Exception e) {
            return RespuestaPago.error("Error confirmando pago: " + e.getMessage());
        }
    }
    
    /**
     * Procesa pago con tarjeta de crédito/débito
     */
    @SuppressWarnings("UseSpecificCatch")
    private RespuestaPago procesarPagoTarjeta(TransaccionPago transaccion, SolicitudPago solicitud) {
        try {
            // Validar datos de tarjeta
            if (!validarTarjeta(solicitud.getNumeroTarjeta(), solicitud.getCvv(), 
                               solicitud.getMesVencimiento(), solicitud.getAnoVencimiento())) {
                return RespuestaPago.error("Datos de tarjeta inválidos");
            }
            
            // Simular procesamiento de tarjeta
            Thread.sleep(3000); // Simular tiempo de procesamiento
            
            // En una implementación real, aquí se haría la llamada a Culqi o Visa
            boolean pagoExitoso = Math.random() > 0.1; // 90% de éxito
            
            if (pagoExitoso) {
                transaccion.setEstadoTransaccion(EstadoTransaccion.COMPLETADA);
                transaccion.setAuthorizationCode(generarCodigoConfirmacion());
                transaccion.setFechaTransaccion(LocalDateTime.now());
                transaccionRepository.save(transaccion);
                
                return RespuestaPago.exitoso("Pago con tarjeta procesado exitosamente", 
                                           transaccion.getAuthorizationCode());
            } else {
                transaccion.setEstadoTransaccion(EstadoTransaccion.RECHAZADA);
                transaccionRepository.save(transaccion);
                return RespuestaPago.error("Pago rechazado por la entidad bancaria");
            }
            
        } catch (Exception e) {
            transaccion.setEstadoTransaccion(EstadoTransaccion.ERROR);
            transaccionRepository.save(transaccion);
            return RespuestaPago.error("Error procesando pago con tarjeta: " + e.getMessage());
        }
    }
    
    /**
     * Procesa pago con Yape
     */
    private RespuestaPago procesarPagoYape(TransaccionPago transaccion, SolicitudPago solicitud) {
        try {
            // Validar datos específicos de Yape
            if (solicitud.getTelefono() == null || solicitud.getTelefono().trim().isEmpty()) {
                return RespuestaPago.error("Número de teléfono requerido para Yape");
            }
            
            // Validar formato de teléfono peruano
            if (!solicitud.getTelefono().matches("^9\\d{8}$")) {
                return RespuestaPago.error("Número de teléfono inválido para Yape (debe empezar con 9 y tener 9 dígitos)");
            }
            
            // Generar código QR o token para Yape
            String tokenYape = "YAPE_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            
            // Actualizar la transacción con datos de la solicitud
            transaccion.setClienteTelefono(solicitud.getTelefono());
            transaccion.setTransactionId(tokenYape);
            
            // En un entorno real, aquí se generaría el QR o se integraría con la API de Yape
            Map<String, Object> datosExtra = new HashMap<>();
            datosExtra.put("tokenYape", tokenYape);
            datosExtra.put("qrCode", "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
            datosExtra.put("tiempoExpiracion", LocalDateTime.now().plusMinutes(10));
            datosExtra.put("monto", solicitud.getMonto());
            datosExtra.put("telefono", solicitud.getTelefono());
            
            String transactionId = "YAPE_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // Guardar la transacción actualizada
            transaccionRepository.save(transaccion);
            
            return new RespuestaPago(true, "Código Yape generado. Escanea el QR para completar el pago", 
                                    transactionId, tokenYape, datosExtra);
        } catch (Exception e) {
            return RespuestaPago.error("Error procesando pago con Yape: " + e.getMessage());
        }
    }
    
    /**
     * Procesa pago con Plin
     */
    private RespuestaPago procesarPagoPlin(TransaccionPago transaccion, SolicitudPago solicitud) {
        try {
            // Validar datos específicos de Plin
            if (solicitud.getTelefono() == null || solicitud.getTelefono().trim().isEmpty()) {
                return RespuestaPago.error("Número de teléfono requerido para Plin");
            }
            
            // Validar formato de teléfono peruano
            if (!solicitud.getTelefono().matches("^9\\d{8}$")) {
                return RespuestaPago.error("Número de teléfono inválido para Plin (debe empezar con 9 y tener 9 dígitos)");
            }
            
            // Similar a Yape, generar token para Plin
            String tokenPlin = "PLIN_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            
            // Actualizar la transacción con datos de la solicitud
            transaccion.setClienteTelefono(solicitud.getTelefono());
            transaccion.setTransactionId(tokenPlin);
            
            Map<String, Object> datosExtra = new HashMap<>();
            datosExtra.put("tokenPlin", tokenPlin);
            datosExtra.put("numeroOperacion", tokenPlin);
            datosExtra.put("instrucciones", "Envía el monto exacto al número 999-888-777 con el código: " + tokenPlin);
            datosExtra.put("monto", solicitud.getMonto());
            datosExtra.put("telefono", solicitud.getTelefono());
            
            String transactionId = "PLIN_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // Guardar la transacción actualizada
            transaccionRepository.save(transaccion);
            
            return new RespuestaPago(true, "Código Plin generado. Sigue las instrucciones para completar el pago", 
                                    transactionId, tokenPlin, datosExtra);
        } catch (Exception e) {
            return RespuestaPago.error("Error procesando pago con Plin: " + e.getMessage());
        }
    }
    
    /**
     * Procesa pago en efectivo (contra entrega)
     */
    private RespuestaPago procesarPagoEfectivo(TransaccionPago transaccion, SolicitudPago solicitud) {
        try {
            // Validar datos de entrega para pago en efectivo
            if (solicitud.getDireccionEntrega() == null || solicitud.getDireccionEntrega().trim().isEmpty()) {
                return RespuestaPago.error("Dirección de entrega requerida para pago en efectivo");
            }
            
            if (solicitud.getTelefono() == null || solicitud.getTelefono().trim().isEmpty()) {
                return RespuestaPago.error("Teléfono de contacto requerido para pago en efectivo");
            }
            
            String transactionId = "EFECTIVO_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            // Actualizar la transacción con datos de la solicitud
            transaccion.setClienteTelefono(solicitud.getTelefono());
            transaccion.setDireccionEnvio(solicitud.getDireccionEntrega());
            transaccion.setTransactionId(transactionId);
            // Note: MetodoPago ya está establecido en crearTransaccion
            
            Map<String, Object> datosExtra = new HashMap<>();
            datosExtra.put("instrucciones", "Pago contra entrega. Tenga listo el monto exacto.");
            datosExtra.put("montoExacto", solicitud.getMonto().toString());
            datosExtra.put("direccionEntrega", solicitud.getDireccionEntrega());
            datosExtra.put("telefono", solicitud.getTelefono());
            datosExtra.put("tiempoEstimadoEntrega", "24-48 horas");
            
            // Guardar la transacción actualizada
            transaccionRepository.save(transaccion);
            
            return new RespuestaPago(true, "Pedido confirmado. Pago contra entrega programado", 
                                    transactionId, transactionId, datosExtra);
        } catch (Exception e) {
            return RespuestaPago.error("Error procesando pago en efectivo: " + e.getMessage());
        }
    }
    
    /**
     * Valida los datos de una tarjeta
     */
    private boolean validarTarjeta(String numero, String cvv, String mes, String ano) {
        if (numero == null || numero.replace(" ", "").length() < 13) return false;
        if (cvv == null || cvv.length() < 3) return false;
        if (mes == null) return false;
        if (ano == null) return false;
        
        try {
            int mesInt = Integer.parseInt(mes);
            int anoInt = Integer.parseInt(ano);
            
            if (mesInt < 1 || mesInt > 12) return false;
            // Actualizado para año 2025
            
            return anoInt >= 2025;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Crea una nueva transacción
     */
    private TransaccionPago crearTransaccion(SolicitudPago solicitud) {
        TransaccionPago transaccion = new TransaccionPago();
        
        // Datos básicos
        transaccion.setNumeroPedido(solicitud.getNumeroPedido());
        transaccion.setMonto(solicitud.getMonto());
        transaccion.setMetodoPago(solicitud.getMetodoPago());
        transaccion.setPasarelaPago(determinarPasarela(solicitud.getMetodoPago()));
        transaccion.setUsuario(solicitud.getUsuario());
        
        // Datos del cliente
        transaccion.setClienteNombres(solicitud.getNombres());
        transaccion.setClienteApellidos(solicitud.getApellidos());
        transaccion.setClienteEmail(solicitud.getEmail());
        transaccion.setClienteTelefono(solicitud.getTelefono());
        transaccion.setClienteDocumentoTipo(solicitud.getTipoDocumento());
        transaccion.setClienteDocumentoNumero(solicitud.getNumeroDocumento());
        
        // Dirección
        transaccion.setDireccionEnvio(solicitud.getDireccion());
        transaccion.setDepartamento(solicitud.getDepartamento());
        transaccion.setProvincia(solicitud.getProvincia());
        transaccion.setDistrito(solicitud.getDistrito());
        
        // Datos de tarjeta (si aplica)
        if (solicitud.getMetodoPago() == MetodoPago.TARJETA_CREDITO || 
            solicitud.getMetodoPago() == MetodoPago.TARJETA_DEBITO) {
            transaccion.setTarjetaNumeroEncriptado(encriptarNumeroTarjeta(solicitud.getNumeroTarjeta()));
            transaccion.setTarjetaNombre(solicitud.getNombreTarjeta());
        }
        
        // Estados iniciales
        transaccion.setEstadoTransaccion(EstadoTransaccion.PENDIENTE);
        transaccion.setFechaCreacion(LocalDateTime.now());
        
        return transaccion;
    }
    
    /**
     * Determina la pasarela de pago según el método
     */
    private PasarelaPago determinarPasarela(MetodoPago metodoPago) {
        return switch (metodoPago) {
            case TARJETA_CREDITO, TARJETA_DEBITO -> PasarelaPago.CULQI;
            case YAPE -> PasarelaPago.INTERNO; // Yape no tiene pasarela directa
            case PLIN -> PasarelaPago.INTERNO; // Plin no tiene pasarela directa
            default -> PasarelaPago.INTERNO;
        };
    }
    
    /**
     * Encripta el número de tarjeta para almacenamiento seguro
     */
    private String encriptarNumeroTarjeta(String numero) {
        if (numero == null || numero.length() < 4) return "****";
        return "**** **** **** " + numero.substring(numero.length() - 4);
    }
    
    /**
     * Genera un código de confirmación único
     */
    private String generarCodigoConfirmacion() {
        return "CONF_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    /**
     * Valida un código de confirmación
     */
    private boolean validarCodigoConfirmacion(TransaccionPago transaccion, String codigo) {
        // Validaciones básicas
        if (codigo == null || codigo.length() < 6) {
            return false;
        }
        
        // Validar que la transacción existe y está en estado pendiente
        if (transaccion == null || !EstadoTransaccion.PENDIENTE.equals(transaccion.getEstadoTransaccion())) {
            return false;
        }
        
        // Validar que el código coincide con el código de confirmación de la transacción
        if (transaccion.getAuthorizationCode() != null) {
            return transaccion.getAuthorizationCode().equals(codigo);
        }
        
        // En un entorno real, aquí se validaría con la API correspondiente
        // Por ejemplo, verificar con Visa, MasterCard, Yape, Plin, etc.
        return true;
    }
    
    // Clases internas para solicitud y respuesta de pago
    public static class SolicitudPago {
        private String numeroPedido;
        private BigDecimal monto;
        private MetodoPago metodoPago;
        private Usuario usuario;
        
        // Datos del cliente
        private String nombres;
        private String apellidos;
        private String email;
        private String telefono;
        private String tipoDocumento;
        private String numeroDocumento;
        
        // Dirección
        private String direccion;
        private String departamento;
        private String provincia;
        private String distrito;
        private String direccionEntrega;
        
        // Datos de tarjeta
        private String numeroTarjeta;
        private String nombreTarjeta;
        private String cvv;
        private String mesVencimiento;
        private String anoVencimiento;
        
        // Constructores
        public SolicitudPago() {}
        
        public SolicitudPago(String numeroPedido, BigDecimal monto, MetodoPago metodoPago, Usuario usuario) {
            this.numeroPedido = numeroPedido;
            this.monto = monto;
            this.metodoPago = metodoPago;
            this.usuario = usuario;
        }
        
        // Métodos de validación
        public boolean esValidoParaTarjeta() {
            return numeroTarjeta != null && nombreTarjeta != null && 
                   cvv != null && mesVencimiento != null && anoVencimiento != null;
        }
        
        public boolean esValidoParaMovil() {
            return telefono != null && telefono.matches("^9\\d{8}$");
        }
        
        public boolean esValidoParaEfectivo() {
            return direccionEntrega != null && telefono != null;
        }
        
        // Getters y Setters
        public String getNumeroPedido() { return numeroPedido; }
        public void setNumeroPedido(String numeroPedido) { this.numeroPedido = numeroPedido; }
        
        public BigDecimal getMonto() { return monto; }
        public void setMonto(BigDecimal monto) { this.monto = monto; }
        
        public MetodoPago getMetodoPago() { return metodoPago; }
        public void setMetodoPago(MetodoPago metodoPago) { this.metodoPago = metodoPago; }
        
        public Usuario getUsuario() { return usuario; }
        public void setUsuario(Usuario usuario) { this.usuario = usuario; }
        
        public String getNombres() { return nombres; }
        public void setNombres(String nombres) { this.nombres = nombres; }
        
        public String getApellidos() { return apellidos; }
        public void setApellidos(String apellidos) { this.apellidos = apellidos; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }
        
        public String getTipoDocumento() { return tipoDocumento; }
        public void setTipoDocumento(String tipoDocumento) { this.tipoDocumento = tipoDocumento; }
        
        public String getNumeroDocumento() { return numeroDocumento; }
        public void setNumeroDocumento(String numeroDocumento) { this.numeroDocumento = numeroDocumento; }
        
        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }
        
        public String getDepartamento() { return departamento; }
        public void setDepartamento(String departamento) { this.departamento = departamento; }
        
        public String getProvincia() { return provincia; }
        public void setProvincia(String provincia) { this.provincia = provincia; }
        
        public String getDistrito() { return distrito; }
        public void setDistrito(String distrito) { this.distrito = distrito; }
        
        public String getDireccionEntrega() { return direccionEntrega; }
        public void setDireccionEntrega(String direccionEntrega) { this.direccionEntrega = direccionEntrega; }
        
        public String getNumeroTarjeta() { return numeroTarjeta; }
        public void setNumeroTarjeta(String numeroTarjeta) { this.numeroTarjeta = numeroTarjeta; }
        
        public String getNombreTarjeta() { return nombreTarjeta; }
        public void setNombreTarjeta(String nombreTarjeta) { this.nombreTarjeta = nombreTarjeta; }
        
        public String getCvv() { return cvv; }
        public void setCvv(String cvv) { this.cvv = cvv; }
        
        public String getMesVencimiento() { return mesVencimiento; }
        public void setMesVencimiento(String mesVencimiento) { this.mesVencimiento = mesVencimiento; }
        
        public String getAnoVencimiento() { return anoVencimiento; }
        public void setAnoVencimiento(String anoVencimiento) { this.anoVencimiento = anoVencimiento; }
    }
    
    public static class RespuestaPago {
        private boolean exitoso;
        private String mensaje;
        private String transactionId;
        private String codigoConfirmacion;
        private Map<String, Object> datosExtra;
        
        // Constructores
        public RespuestaPago() {}
        
        public RespuestaPago(boolean exitoso, String mensaje) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
        }
        
        public RespuestaPago(boolean exitoso, String mensaje, String transactionId) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.transactionId = transactionId;
        }
        
        public RespuestaPago(boolean exitoso, String mensaje, String transactionId, String codigoConfirmacion) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.transactionId = transactionId;
            this.codigoConfirmacion = codigoConfirmacion;
        }
        
        public RespuestaPago(boolean exitoso, String mensaje, String transactionId, String codigoConfirmacion, Map<String, Object> datosExtra) {
            this.exitoso = exitoso;
            this.mensaje = mensaje;
            this.transactionId = transactionId;
            this.codigoConfirmacion = codigoConfirmacion;
            this.datosExtra = datosExtra;
        }
        
        // Métodos factory
        public static RespuestaPago exitoso(String mensaje) {
            return new RespuestaPago(true, mensaje);
        }
        
        public static RespuestaPago exitoso(String mensaje, String transactionId) {
            return new RespuestaPago(true, mensaje, transactionId);
        }
        
        public static RespuestaPago error(String mensaje) {
            return new RespuestaPago(false, mensaje, (String) null);
        }
        
        // Getters y Setters
        public boolean isExitoso() { return exitoso; }
        public void setExitoso(boolean exitoso) { this.exitoso = exitoso; }
        
        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
        
        public String getTransactionId() { return transactionId; }
        public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
        
        public String getCodigoConfirmacion() { return codigoConfirmacion; }
        public void setCodigoConfirmacion(String codigoConfirmacion) { this.codigoConfirmacion = codigoConfirmacion; }
        
        public Map<String, Object> getDatosExtra() { return datosExtra; }
        public void setDatosExtra(Map<String, Object> datosExtra) { this.datosExtra = datosExtra; }
    }
}
